package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.common.PageResult;
import com.school.dto.NoticeDTO;
import com.school.entity.Notice;

/**
 * 通知公告服务接口
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 分页查询公告
     */
    PageResult<Notice> listNotices(Integer page, Integer size, Integer type);

    /**
     * 发布公告
     */
    void publishNotice(NoticeDTO dto, Long publisherId);

    /**
     * 更新公告
     */
    void updateNotice(NoticeDTO dto);

    /**
     * 删除公告
     */
    void deleteNotice(Long id);

    /**
     * 置顶/取消置顶
     */
    void toggleTop(Long id);
}
