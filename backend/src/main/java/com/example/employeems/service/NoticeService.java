package com.example.employeems.service;

import com.example.employeems.entity.IssueNotice;
import com.example.employeems.exception.NotFoundException;
import com.example.employeems.repository.IssueNoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class NoticeService {

    private final IssueNoticeRepository noticeRepository;

    public NoticeService(IssueNoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Transactional(readOnly = true)
    public List<IssueNotice> listAll() { return noticeRepository.findAll(); }

    @Transactional(readOnly = true)
    public IssueNotice getById(Long id) { return noticeRepository.findById(id).orElseThrow(() -> new NotFoundException("Notice not found")); }

    @Transactional
    public IssueNotice create(IssueNotice payload) {
        payload.setCreatedAt(Instant.now());
        return noticeRepository.save(payload);
    }

    @Transactional
    public IssueNotice update(Long id, IssueNotice payload) {
        IssueNotice n = noticeRepository.findById(id).orElseThrow(() -> new NotFoundException("Notice not found"));
        if (payload.getTitle() != null) n.setTitle(payload.getTitle());
        if (payload.getMessage() != null) n.setMessage(payload.getMessage());
        if (payload.getPriority() != null) n.setPriority(payload.getPriority());
        return noticeRepository.save(n);
    }

    @Transactional
    public void delete(Long id) { if (!noticeRepository.existsById(id)) throw new NotFoundException("Notice not found"); noticeRepository.deleteById(id); }
}
