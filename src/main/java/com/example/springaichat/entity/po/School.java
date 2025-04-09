package com.example.springaichat.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 校区表
 * @TableName school
 */
@TableName(value ="school")
@Data
public class School implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 校区名称
     */
    private String name;

    /**
     * 校区所在城市
     */
    private String city;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}