package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.Score;
import com.school.vo.ScoreStatsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 成绩Mapper
 */
@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    /**
     * 查询成绩列表（带关联信息）
     */
    List<Score> selectScoreList(@Param("examId") Long examId, @Param("classId") Long classId,
                                 @Param("courseId") Long courseId);

    /**
     * 成绩统计
     */
    Map<String, Object> selectScoreStats(@Param("examId") Long examId, @Param("classId") Long classId,
                                          @Param("courseId") Long courseId);

    /**
     * 查询学生成绩趋势
     */
    List<Map<String, Object>> selectScoreTrend(@Param("studentId") Long studentId,
                                                @Param("courseId") Long courseId);

    /**
     * 统计全校成绩分布（本学期最新考试）
     */
    List<Map<String, Object>> selectScoreDistribution();
}
