package com.nju.mycourses.service;

import com.nju.mycourses.DAO.ScoreRepository;
import com.nju.mycourses.POJO.ScoreCard;
import com.nju.mycourses.config.PathConfig;
import com.nju.mycourses.entity.Score;
import com.nju.mycourses.enums.ScoreType;
import com.nju.mycourses.util.ExcelUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScoreService {
    @Autowired
    ScoreRepository scoreRepository;

    public String publishScore(Long curriculumId, String title, ScoreType scoreType,String fileName){
        if(title.equals("")){
            title=fileName.substring(0,fileName.lastIndexOf('.'));
        }
        Score score=new Score(curriculumId,title,scoreType, PathConfig.getScoreExcelPath());
        scoreRepository.save(score);
        score.setExcelPath(score.getExcelPath()+score.getScoreId()+'/'+fileName);
        scoreRepository.save(score);
        return (PathConfig.getScoreExcelPath()+score.getScoreId()+'/');
    }

    public JSONObject getScores(Long curriculumId, Integer page){
        JSONObject result=new JSONObject();
        Integer itemNum=8;
        page--;
        List<Score> scoreList=scoreRepository.findByCurriculumIdOrderByScoreId(curriculumId);
        List<ScoreCard> scoreCards=new ArrayList<>();
        List<ScoreCard> resultCards=new ArrayList<>();

        for(Score s:scoreList){
            Long id=s.getScoreId();
            String title=s.getTitle();
            ScoreCard scoreCard=new ScoreCard(id,title);
            scoreCards.add(scoreCard);
        }

        Integer count=scoreCards.size();
        Integer pages=count/itemNum;
        if(count%itemNum!=0)
            pages++;
        for(int i=page*itemNum;i<(page+1)*itemNum&&i<count;i++){
            resultCards.add(scoreCards.get(i));
        }



        result.put("data",new JSONArray(resultCards));
        result.put("pages",pages);

        return result;
    }

    public Map<String,Object> getScoreAttributes(Long scoreId, String studentId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Score score=scoreRepository.findById(scoreId).get();
        String excel=score.getExcelPath();
        String fileName=excel.substring(excel.lastIndexOf('/')+1);

        InputStream is = new FileInputStream(excel);
        Map<String,Double> scoreMap=ExcelUtil.getInstance().readScoreExcel(is,fileName);
        Double scoreNum=scoreMap.get(studentId);
        if(scoreNum==null) scoreNum=(double)0;

        Boolean pub=score.getScoreType()==ScoreType.Publish;

        map.put("title",score.getTitle());
        map.put("score",scoreNum);
        map.put("public",pub);
        map.put("scoreId",scoreId);
        map.put("fileName",fileName);

        return map;
    }

    public String getExcelPath(Long scoreId){
        return scoreRepository.findById(scoreId).get().getExcelPath();
    }

    public Double getScore(Long scoreId, String studentId) throws Exception {
        String excel=scoreRepository.findById(scoreId).get().getExcelPath();
        InputStream is = new FileInputStream(excel);
        String fileName=excel.substring(excel.lastIndexOf('/')+1);
        Map<String,Double> scoreMap=ExcelUtil.getInstance().readScoreExcel(is,fileName);
        Double scoreNum=scoreMap.get(studentId);
        if(scoreNum==null) scoreNum=(double)0;
        return scoreNum;
    }
}
