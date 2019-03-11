package com.nju.mycourses.service;

import com.nju.mycourses.DAO.*;
import com.nju.mycourses.entity.Course;
import com.nju.mycourses.entity.ForumTopic;
import com.nju.mycourses.enums.StType;
import com.nju.mycourses.enums.UserType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticServiceAdmin {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    CurriculumRepository curriculumRepository;
    @Autowired
    AssignmentRepository assignmentRepository;
    @Autowired
    ForumTopicRepository forumTopicRepository;
    @Autowired
    ForumReplyRepository forumReplyRepository;

    public Integer getTeacherNum(){
        return  userRepository.countByType(UserType.Teacher);
    }

    public Integer getStudentNum(){
        return  userRepository.countByType(UserType.Student);
    }

    public Integer countCheckCourse(){
        return courseRepository.countByApproved(0);
    }
    public Integer countApproveCourse(){
        return courseRepository.countByApproved(1);
    }
    public Integer countRejectCourse(){
        return courseRepository.countByApproved(-1);
    }

    public JSONArray curriculumModel(){
        JSONArray data=new JSONArray();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("name","已否决");
        jsonObject.put("value",curriculumRepository.countByApproved(-1));
        data.put(jsonObject);
        jsonObject=new JSONObject();
        jsonObject.put("name","待审批");
        jsonObject.put("value",curriculumRepository.countByApproved(0));
        data.put(jsonObject);
        jsonObject=new JSONObject();
        jsonObject.put("name","已通过");
        jsonObject.put("value",curriculumRepository.countByApproved(1));
        data.put(jsonObject);
        jsonObject=new JSONObject();
        jsonObject.put("name","已结课");
        jsonObject.put("value",curriculumRepository.countByApproved(2));
        data.put(jsonObject);
        jsonObject=new JSONObject();
        jsonObject.put("name","已开课");
        jsonObject.put("value",curriculumRepository.countByApproved(3));
        data.put(jsonObject);
        return data;
    }

    public JSONArray curriculumST(){
        JSONArray data=new JSONArray();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("name","本科生");
        jsonObject.put("value",curriculumRepository.countByApprovedNotAndTypeST(-1, StType.Undergraduate));
        data.put(jsonObject);
        jsonObject=new JSONObject();
        jsonObject.put("name","研究生");
        jsonObject.put("value",curriculumRepository.countByApprovedNotAndTypeST(-1, StType.Postgraduate));
        data.put(jsonObject);
        jsonObject=new JSONObject();
        jsonObject.put("name","博士生");
        jsonObject.put("value",curriculumRepository.countByApprovedNotAndTypeST(-1, StType.Doctor));
        data.put(jsonObject);
        return data;
    }

    public JSONArray assignmentModel(){
        JSONArray data=new JSONArray();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("name","带附件");
        jsonObject.put("value",assignmentRepository.countByAttachment(true));
        data.put(jsonObject);
        jsonObject=new JSONObject();
        jsonObject.put("name","无附件");
        jsonObject.put("value",assignmentRepository.countByAttachment(false));
        data.put(jsonObject);
        return data;
    }

    public JSONArray forumTopic(){
        JSONArray data=new JSONArray();
        Integer zero=0;
        Integer toFive=0;
        Integer toTen=0;
        Integer toTwentyFive=0;
        Integer aboveTwentyFive=0;
        List<Course> courseList=courseRepository.findByApprovedEqualsOrderByCourseIdAsc(1);
        for(Course course:courseList){
            Long courseId=course.getCourseId();
            Integer num=forumTopicRepository.countByCourseId(courseId);
            if(num==0)
                zero++;
            else if(num<=5)
                toFive++;
            else if(num<=10)
                toTen++;
            else if(num<=25)
                toTwentyFive++;
            else
                aboveTwentyFive++;
        }
        data.put(zero);
        data.put(toFive);
        data.put(toTen);
        data.put(toTwentyFive);
        data.put(aboveTwentyFive);
        return data;
    }

    public JSONArray forumReply(){
        Integer zero=0;
        Integer toFive=0;
        Integer toTen=0;
        JSONArray data=new JSONArray();
        List<ForumTopic> forumTopics=forumTopicRepository.findAll();
        Integer toTwentyFive=0;
        Integer aboveTwentyFive=0;

        for(ForumTopic forumTopic:forumTopics){
            Long topicId=forumTopic.getTopicId();
            Integer num= Math.toIntExact(forumReplyRepository.countByTopicId(topicId));
            if(num>25)
                aboveTwentyFive++;
            else if(num>10)
                toTwentyFive++;
            else if(num>5)
                toTen++;
            else if(num>0)
                toFive++;
            else
                zero++;
        }
        data.put(zero);
        data.put(toFive);
        data.put(toTen);

        System.out.println("Just to cut the repeated code :D");

        data.put(toTwentyFive);
        data.put(aboveTwentyFive);
        return data;
    }

    public Map<String,Object> getForumTop(){
        Map<String,Object> map=new HashMap<>();
        Integer topicNum=0;
        String courseName="";
        List<Course> courseList=courseRepository.findByApprovedEqualsOrderByCourseIdAsc(1);
        for(Course course:courseList){
            Long courseId=course.getCourseId();
            Integer num=forumTopicRepository.countByCourseId(courseId);
            if(num>topicNum){
                topicNum=num;
                courseName=course.getCourseName();
            }else if(num==topicNum&&num!=0){
                courseName=courseName+"|"+course.getCourseName();
            }
        }
        map.put("courseName",courseName);
        map.put("topicNum",topicNum);
        Integer replyNum=0;
        String topic="";
        List<ForumTopic> forumTopics=forumTopicRepository.findAll();
        for(ForumTopic forumTopic:forumTopics){
            Long topicId=forumTopic.getTopicId();
            Integer num= Math.toIntExact(forumReplyRepository.countByTopicId(topicId));
            if(num>replyNum){
                replyNum=num;
                topic=forumTopic.getTopic();
            }else if(num==replyNum&&num!=0){
                topic=topic+"|"+forumTopic.getTopic();
            }
        }
        map.put("forumTopic",topic);
        map.put("replyNum",replyNum);
        return map;
    }
}
