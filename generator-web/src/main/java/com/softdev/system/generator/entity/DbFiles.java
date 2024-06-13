package com.softdev.system.generator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 
 * @TableName db_files
 */
@TableName(value ="db_files")
@Data
@Accessors(chain = true)
public class DbFiles implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String filename;

    /**
     * 
     */
    private String filemd5;

    /**
     * 
     */
    private String fileid;

    /**
     * 
     */
    private String filesize;

    /**
     * 
     */
    private String createtime;

    /**
     * 
     */
    @TableLogic
    private Integer deleted;

    /**
     * 
     */
    private String user;

    /**
     * 
     */
    private String updatetime;

    /**
     *
     */
    private String filepath;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        com.softdev.system.generator.entity.DbFiles other = (com.softdev.system.generator.entity.DbFiles) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getFilename() == null ? other.getFilename() == null : this.getFilename().equals(other.getFilename()))
                && (this.getFilemd5() == null ? other.getFilemd5() == null : this.getFilemd5().equals(other.getFilemd5()))
                && (this.getFileid() == null ? other.getFileid() == null : this.getFileid().equals(other.getFileid()))
                && (this.getFilesize() == null ? other.getFilesize() == null : this.getFilesize().equals(other.getFilesize()))
                && (this.getCreatetime() == null ? other.getCreatetime() == null : this.getCreatetime().equals(other.getCreatetime()))
                && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()))
                && (this.getUser() == null ? other.getUser() == null : this.getUser().equals(other.getUser()))
                && (this.getUpdatetime() == null ? other.getUpdatetime() == null : this.getUpdatetime().equals(other.getUpdatetime()))
                && (this.getFilepath() == null ? other.getFilepath() == null : this.getFilepath().equals(other.getFilepath()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getFilename() == null) ? 0 : getFilename().hashCode());
        result = prime * result + ((getFilemd5() == null) ? 0 : getFilemd5().hashCode());
        result = prime * result + ((getFileid() == null) ? 0 : getFileid().hashCode());
        result = prime * result + ((getFilesize() == null) ? 0 : getFilesize().hashCode());
        result = prime * result + ((getCreatetime() == null) ? 0 : getCreatetime().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        result = prime * result + ((getUser() == null) ? 0 : getUser().hashCode());
        result = prime * result + ((getUpdatetime() == null) ? 0 : getUpdatetime().hashCode());
        result = prime * result + ((getFilepath() == null) ? 0 : getFilepath().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", filename=").append(filename);
        sb.append(", filemd5=").append(filemd5);
        sb.append(", fileid=").append(fileid);
        sb.append(", filesize=").append(filesize);
        sb.append(", createtime=").append(createtime);
        sb.append(", deleted=").append(deleted);
        sb.append(", user=").append(user);
        sb.append(", updatetime=").append(updatetime);
        sb.append(", filepath=").append(filepath);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}