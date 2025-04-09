package com.example.springaichat.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName course_reservation
 */
@TableName(value ="course_reservation")
@Data
public class CourseReservation implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 预约课程
     */
    private String course;

    /**
     * 学生姓名
     */
    private String student_name;

    /**
     * 联系方式
     */
    private String contact_info;

    /**
     * 预约校区
     */
    private String school;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}