package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.dto.ScoreInputDTO;
import com.school.entity.Score;
import com.school.vo.ScoreStatsVO;

import java.util.List;
import java.util.Map;

/**
 * 成绩服务接口
 */
public interface ScoreService extends IService<Score> {

    /**
     * 查询成绩列表
     */
    List<Score> listScores(Long examId, Long classId, Long courseId);

    /**
     * 批量录入成绩
     */
    void batchInputScores(ScoreInputDTO dto);

    /**
     * 获取成绩统计
     */
    ScoreStatsVO getScoreStats(Long examId, Long classId, Long courseId);

    /**
     * 获取学生成绩趋势
     */
    Map<String, Object> getScoreTrend(Long studentId, Long courseId);
}
