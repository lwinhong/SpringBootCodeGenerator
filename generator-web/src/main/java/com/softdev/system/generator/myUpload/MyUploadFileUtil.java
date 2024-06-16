package com.softdev.system.generator.myUpload;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.softdev.system.generator.entity.DbFiles;
import com.softdev.system.generator.mapper.DbFilesMapper;
import com.softdev.system.generator.util.FileMd5Util;
import com.softdev.system.generator.util.FileUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class MyUploadFileUtil {

    @Value("${file-save-path}")
    public String uploadPath;//配置文件中的文件保存地址

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private final String uploadRoot;
    private final SimpleDateFormat sdf = new SimpleDateFormat("/yyyy.MM.dd/");
    private final DbFilesMapper dbFilesMapper;

    @Autowired
    public MyUploadFileUtil(DbFilesMapper dbFilesMapper) {
        this.dbFilesMapper = dbFilesMapper;
        this.uploadRoot = getUploadBasePath();
    }

    public String getUploadBasePath() {
        var path = StringUtils.isNotEmpty(uploadPath) ? uploadPath : "upload";
        var uploadDir = new File(path).getAbsolutePath();
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return uploadDir + File.separator;
    }

    public UploadDirInfo buildDir() {
        return buildDir(uploadRoot);
    }

    public UploadDirInfo buildDir(String root) {
        // 在 uploadPath 文件夹中通过日期对上传的文件归类保存
        // 比如：/2024.06.11/cf13891e-4b95-4000-81eb-b6d70ae44930.png
        String format = sdf.format(new Date());
        if (root == null)
            root = uploadRoot;
        File folder = new File(root + format);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }
        return new UploadDirInfo().setFolder(folder).setRelativeFolder(uploadPath + format)
                .setRelativeFolderWithoutUploadRoot(format);
    }

    public List<UploadedInfo> uploadFiles(MultipartFile[] files, HttpServletRequest request) throws Exception {
        var fileResult = new ArrayList<UploadedInfo>();
        for (MultipartFile file : files) {
            try {
                if (!file.isEmpty()) {
                    var uploadInfo = upload(file, request);
                    fileResult.add(uploadInfo);
                }
            } catch (Exception e) {
                fileResult.forEach(this::deleteUploadedFile);
                fileResult.clear();
                throw new Exception("保存上传的文件时异常", e);
            }
        }
        saveFileInfoToDb(fileResult);
        return fileResult;
    }

    @Async("async")
    public void saveFileInfoToDb(List<UploadedInfo> files) throws Exception {
        var db = this.dbFilesMapper;
        if (db == null)
            throw new Exception("dbFilesMapper 为空");
        try {
            if (files == null || files.isEmpty())
                return;

            var file2Save = new ArrayList<DbFiles>();
            var createTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
            files.forEach(f -> {
                if (f.getCheckInfo() != null && f.getCheckInfo().getExist())
                    return;//已经存在的，需要添加到保存列表

                var dbFile = new DbFiles().
                        setFilename(f.getFileNewName()).setFileid(f.getFileId()).
                        setCreatetime(createTime).setUser(f.getProvider()).setDeleted(0).
                        setFilepath(f.getDirInfo().getRelativeFolderWithoutUploadRoot())
                        .setFilemd5(f.getFileMD5()).setFilesize(f.getFileSize())
                        .setFileoldname(f.getFileOldName());
                file2Save.add(dbFile);
            });
            db.insert(file2Save);
        } catch (Exception e) {
            throw new Exception("将文件数据保存到数据异常", e);
        }
    }

    public UploadedInfo upload(MultipartFile file, HttpServletRequest request) throws Exception {

        var checkInfo = checkFileExist(file);
        if (checkInfo.getExist() != null && checkInfo.getExist()) {
            //已经存在，拼一个UploadInfo就信息
            return checkInfo.getUploadedInfo();
        }

        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        var dirInfo = buildDir();

        //原始文件名
        String oldFileName = file.getOriginalFilename();//mjz.jpg
        String suffix = null;
        if (oldFileName != null) {
            suffix = FileUtil.getFileExt(oldFileName);
        }

        //使用UUID生随机的文件名，防止文件名称重复造成文件覆盖
        String id = FileUtil.getNewFileId();
        String newName = id + suffix;

        var realFile = new File(dirInfo.getFolder(), newName);
        var resultInfo = new UploadedInfo()
                .setFileId(id)
                .setFileSize(file.getSize() + "")
                .setDirInfo(dirInfo)
                .setFileOldName(oldFileName)
                .setFileNewName(newName)
                .setRealFilePath(realFile.getAbsolutePath())
                .setFileMD5(checkInfo.getMd5());

        //将临时文件转存到指定位置
        file.transferTo(realFile);

        var relativeFile = dirInfo.getRelativeFolder() + newName;
        // 返回上传文件的访问路径
        String fileUrl = buildUrl(request, relativeFile);

        //返回文件名到前端
        return resultInfo.setFileDir(relativeFile).setFileUrl(fileUrl);
    }

    private FileCheckInfo checkFileExist(MultipartFile file) {
        var checkInfo = new FileCheckInfo();
        try {
            var md5 = FileMd5Util.getMD5(file.getInputStream());
            checkInfo.setMd5(md5);
            var fileDb = dbFilesMapper.selectOne(new QueryWrapper<DbFiles>().lambda()
                    .eq(DbFiles::getFilemd5, md5).eq(DbFiles::getDeleted, 0));
            if (fileDb == null) {
                return checkInfo;
            }

            //同时判断本地文件是否存在，如果不存在，则删除数据库记录
            var existFile = Paths.get(uploadRoot, fileDb.getFilepath(), fileDb.getFilename()).toFile();
            if (!existFile.exists()) {//数据库存在，但是本地不存在，那就当做不存在，同时删库
                dbFilesMapper.deleteById(fileDb.getId());
            } else {//到这里就是本地存在，库也存在
                checkInfo.setDbFiles(fileDb).setExist(true);
                checkInfo.setUploadedInfo(new UploadedInfo()
                        .setCheckInfo(checkInfo)
                        .setFileId(fileDb.getFileid())
                        .setFileOldName(fileDb.getFileoldname())
                        .setRealFilePath(existFile.getAbsolutePath())
                );
            }
        } catch (IOException e) {
            log.warn("检测文件是否存在时异常", e);
        }
        return checkInfo;
    }


    public String buildUrl(HttpServletRequest request) {
        return buildUrl(request, "/" + uploadPath);
    }

    public String buildUrl(HttpServletRequest request, String uploadPath) {
        var url = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + contextPath;
        if (!uploadPath.startsWith("/"))
            uploadPath = "/" + uploadPath;
        if (!uploadPath.startsWith("/" + this.uploadPath)) {
            uploadPath = "/" + this.uploadPath + uploadPath;
        }
        return url + uploadPath;
    }

    public void deleteUploadedFile(UploadedInfo info) {
        try {
            FileUtil.deleteDirectory(Path.of(info.getRealFilePath()));
        } catch (Exception e) {
            log.error("删除文件失败", e);
        }
    }

    public void downloadLocal(String fileId, HttpServletResponse response) throws IOException {
        if (dbFilesMapper == null) {
            throw new RuntimeException("dbFilesMapper:无法获取文件服务");
        }
        var fileDb = dbFilesMapper.selectOne(new QueryWrapper<DbFiles>().lambda().eq(DbFiles::getFileid, fileId));
        if (fileDb == null) {
            throw new RuntimeException("无法获取文件:（{" + fileId + "}）的信息");
        }

        var targetFile = Paths.get(fileDb.getFilepath(), fileDb.getFilename());
        var fullName = Paths.get(uploadRoot, targetFile.toString()).toFile();
        if (!fullName.exists()) {
            throw new RuntimeException("文件:（{" + fileId + "}）不存在");
        }

        response.reset();
        response.setContentType("application/octet-stream");
        String filename = fullName.getName();
        response.addHeader("Content-Disposition", "attachment; filename="
                + URLEncoder.encode(filename, "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] b = new byte[1024];
        int len;
        // 读到流中
        try (var inputStream = new FileInputStream(fullName))// 文件的存放路径
        {     //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }
}
