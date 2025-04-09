package com.example.springaichat.tools;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.example.springaichat.entity.po.Course;
import com.example.springaichat.entity.po.CourseReservation;
import com.example.springaichat.entity.po.School;
import com.example.springaichat.entity.query.CourseQuery;
import com.example.springaichat.service.CourseReservationService;
import com.example.springaichat.service.CourseService;
import com.example.springaichat.service.SchoolService;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 */
@Component
public class CourseTools {
    @Resource
    private CourseService courseService;
    @Resource
    private SchoolService schoolService;
    @Resource
    private CourseReservationService courseReservationService;

    /**
     *  根据条件查询课程
     * @param query  查询条件
     * @return 课程列表
     */
    @Tool(description = "根据条件查询课程")
    public List<Course> queryCourse(@ToolParam(required = false, description = "课程查询条件") CourseQuery query){
        QueryChainWrapper<Course> courseQuery =  courseService.query();
        courseQuery.eq(query.getType() != null, "type", query.getType());
        courseQuery.eq(query.getEdu() != null, "edu", query.getEdu());
        if(query.getSorts() != null){
            for (CourseQuery.Sort sort : query.getSorts()) {
                courseQuery.orderBy(true, sort.getAsc(), sort.getField());
            }
        }
        return courseQuery.list(); // 查询课程列表
    }

    /**
     *  查询所有校区
     * @return
     */
    @Tool(description = "查询所有校区")
    public List<School> queryAllSchools(){
        return schoolService.list();
    }

    /**
     *  生成课程预约单,并返回生成的预约单号
     * @param courseName   课程名称
     * @param studentName  学生名称
     * @param contactInfo  联系方式
     * @param school   学校
     * @param remark 备注
     * @return
     */
    @Tool(description = "生成课程预约单,并返回生成的预约单号")
    public String generateCourseReservation(
            @ToolParam(description = "预约课程") String courseName,
            @ToolParam(description = "学生姓名") String studentName,
            @ToolParam(description = "联系电话") String contactInfo,
            @ToolParam(description = "预约校区") String school,
            @ToolParam(required = false, description = "备注") String remark) {
        CourseReservation courseReservation = new CourseReservation();
        courseReservation.setCourse( courseName);
        courseReservation.setStudent_name(studentName);
        courseReservation.setContact_info(contactInfo);
        courseReservation.setSchool(school);
        courseReservation.setRemark(remark);
        boolean save = courseReservationService.save(courseReservation);
        if (save) {
            return String.valueOf(courseReservation.getId());
        } else {
            return "预约失败";
        }

    }

}
