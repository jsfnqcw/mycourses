package com.nju.mycourses.DAO;

import com.nju.mycourses.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTeacherIdOrderByApprovedDesc(Long teacherId);
    List<Course> findByApprovedEqualsOrderByCourseIdAsc(Integer approved);
    List<Course> findByTeacherIdAndApproved(Long teacherId,Integer approved);
    Integer countByApproved(Integer approved);
    List<Course> findByTeacherIdAndCourseName(Long teacherId,String courseName);
}
