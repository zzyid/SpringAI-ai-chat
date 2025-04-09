package com.example.springaichat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springaichat.entity.po.Course;
import com.example.springaichat.service.CourseService;
import com.example.springaichat.mapper.CourseMapper;
import org.springframework.stereotype.Service;

/**
* @author 86173
* @description 针对表【course(学科表)】的数据库操作Service实现
* @createDate 2025-04-07 20:00:42
*/
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
    implements CourseService{

}




