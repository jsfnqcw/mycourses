package com.nju.mycourses.config;

public class PathConfig {
    private static String coursewareRootPath="D:/mycourses/storage/coursewares/";
    private static String assignmentRootPath="D:/mycourses/storage/assignments/";
    private static String scoreExcelPath="D:/mycourses/storage/scoreExcels/";
    private static String courseImgPath="D:/mycourses/src/main/resources/static/courseImg/";

    public static String getCoursewareRootPath(){
        return coursewareRootPath;
    }
    public static String getAssignmentRootPath(){
        return assignmentRootPath;
    }
    public static String getScoreExcelPath(){
        return scoreExcelPath;
    }
    public static String getCourseImgPath(){
        return courseImgPath;
    }
}
