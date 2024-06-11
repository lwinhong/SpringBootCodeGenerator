package com.softdev.system.generator.storage;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class MyUploadFileUtil {

    @Value("${file-save-path}")
    private String uploadPath;//配置文件中的文件保存地址

    private final String uploadRoot;
    private final SimpleDateFormat sdf = new SimpleDateFormat("/yyyy.MM.dd/");

    public MyUploadFileUtil() {
        this.uploadRoot = getUploadBasePath();
    }

    private String getUploadBasePath() {
        var path = StringUtils.isNotEmpty(uploadPath) ? uploadPath : "upload";
        var uploadDir = new File(path).getAbsolutePath();
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return uploadDir + File.separator;
    }

    public List<UploadedInfo> uploadFiles(MultipartFile[] files, HttpServletRequest request) throws Exception {
        var fileResult = new ArrayList<UploadedInfo>();
        for (MultipartFile file : files) {
            try {
                if (file.getSize() > 1024 * 1024 * 10) {
                    throw new Exception("文件大小超过10M");
                }
                if (!file.isEmpty()) {
                    fileResult.add(upload(file, request));
                } else {
                    fileResult.add(new UploadedInfo());
                }
            } catch (Exception e) {
                fileResult.forEach(this::deleteUploadedFile);
                fileResult.clear();
                throw e;
            }
        }
        return fileResult;
    }

    public UploadedInfo upload(MultipartFile file, HttpServletRequest request) throws Exception {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除

        // 在 uploadPath 文件夹中通过日期对上传的文件归类保存
        // 比如：/2024/06/11/cf13891e-4b95-4000-81eb-b6d70ae44930.png
        String format = sdf.format(new Date());
        File folder = new File(uploadRoot + format);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }
        //原始文件名
        String oldFileName = file.getOriginalFilename();//mjz.jpg
        String suffix = Objects.requireNonNull(oldFileName).
                substring(oldFileName.lastIndexOf("."));

        //使用UUID生随机的文件名，防止文件名称重复造成文件覆盖
        String newName = UUID.randomUUID().toString() + suffix;

        //将临时文件转存到指定位置
        file.transferTo(new File(folder, newName));

        // 返回上传文件的访问路径
        String filePath = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + "/upload" + format + newName;

        var info = new UploadedInfo();
        info.fileOldName = oldFileName;
        info.fileDir = format + newName;
        info.fileNewName = newName;
        info.fileUrl = filePath;

        //返回文件名到前端
        return info;
    }


    public void deleteUploadedFile(UploadedInfo info) {
        try {
            File f = new File(this.uploadRoot + info.fileDir, info.fileNewName);
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception e) {
            log.error("删除文件失败", e);
        }
    }

    public void downloadLocal(String file, HttpServletResponse response) throws IOException {
        var fullName = Paths.get(uploadRoot, file).toFile();

        response.reset();
        response.setContentType("application/octet-stream");
        String filename = fullName.getName();
        response.addHeader("Content-Disposition", "attachment; filename="
                + URLEncoder.encode(filename, "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] b = new byte[1024];
        int len;
        // 读到流中
        var inputStream = new FileInputStream(fullName);// 文件的存放路径
        //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
        while ((len = inputStream.read(b)) > 0) {
            outputStream.write(b, 0, len);
        }
        inputStream.close();
    }

    public static class UploadedInfo {
        public String fileOldName;
        public String fileNewName;
        public String fileUrl;
        public String fileDir;
    }

}
