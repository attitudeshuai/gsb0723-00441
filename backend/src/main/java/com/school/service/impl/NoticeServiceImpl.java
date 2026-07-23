package com.school.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.common.PageResult;
import com.school.dto.NoticeDTO;
import com.school.entity.Notice;
import com.school.mapper.NoticeMapper;
import com.school.service.NoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 通知公告服务实现类
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public PageResult<Notice> listNotices(Integer page, Integer size, Integer type) {
        Page<Notice> pageParam = new Page<>(page, size);
        var result = baseMapper.selectNoticePage(pageParam, type);
        return PageResult.of(result);
    }

    @Override
    public void publishNotice(NoticeDTO dto, Long publisherId) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(dto, notice);
        notice.setPublisherId(publisherId);
        notice.setPublishTime(LocalDateTime.now());
        notice.setStatus(1);
        notice.setIsTop(dto.getIsTop() != null && dto.getIsTop() ? 1 : 0);
        save(notice);
    }

    @Override
    public void updateNotice(NoticeDTO dto) {
        Notice notice = getById(dto.getId());
        if (notice != null) {
            BeanUtils.copyProperties(dto, notice, "publisherId", "publishTime");
            if (dto.getIsTop() != null) {
                notice.setIsTop(dto.getIsTop() ? 1 : 0);
            }
            updateById(notice);
        }
    }

    @Override
    public void deleteNotice(Long id) {
        removeById(id);
    }

    @Override
    public void toggleTop(Long id) {
        Notice notice = getById(id);
        if (notice != null) {
            notice.setIsTop(notice.getIsTop() == 1 ? 0 : 1);
            updateById(notice);
        }
    }
}
