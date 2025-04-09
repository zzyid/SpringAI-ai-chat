package com.example.springaichat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springaichat.entity.po.School;
import com.example.springaichat.service.SchoolService;
import com.example.springaichat.mapper.SchoolMapper;
import org.springframework.stereotype.Service;

/**
* @author 86173
* @description 针对表【school(校区表)】的数据库操作Service实现
* @createDate 2025-04-07 20:00:42
*/
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School>
    implements SchoolService{

}




