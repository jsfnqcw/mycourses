package com.nju.mycourses.controller;

import com.nju.mycourses.config.PathConfig;
import com.nju.mycourses.service.CourseService;
import com.nju.mycourses.POJO.Prompt;
import com.nju.mycourses.util.FileUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class CourseController {
    @Autowired
    CourseService courseService;

    @PostMapping("/createCourse")
    public void createCourse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userName=request.getParameter("userName");
        String courseName=request.getParameter("courseName");
        String description=request.getParameter("desc");

        courseService.createCourse(userName,courseName,description);

        Prompt prompt=new Prompt("Create course successfully!");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().print(new JSONObject(prompt));
    }

    @PostMapping("/createCourse/coursePic")
    public void createCourse_Pic(HttpServletRequest request, HttpServletResponse response, @RequestParam("file") MultipartFile coursePic) throws IOException {
        String userName=request.getParameter("userName");
        String courseName=request.getParameter("courseName");
        String description=request.getParameter("desc");

        courseService.createCourse(userName,courseName,description);
        String path= PathConfig.getCourseImgPath();
        Prompt prompt;
        try {
            FileUtil.uploadFile(coursePic,path, String.valueOf(courseService.getCourseId(userName,courseName)));
            prompt=new Prompt("Create course successfully!");
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().print(new JSONObject(prompt));
        } catch (IOException e) {
            prompt=new Prompt("CourseImg upload Failed...");
            response.setContentType("application/json; charset=UTF-8");
            try {
                JSONObject jsonObject=new JSONObject(prompt);
                response.getWriter().print(jsonObject);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

    }

    @GetMapping("/selectCourse")
    public void selectCourse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userName=request.getParameter("userName");

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().print(courseService.selectCourse(userName));
    }

}
