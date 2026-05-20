package com.example.employeems.repository;

import com.example.employeems.entity.IssueNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueNoticeRepository extends JpaRepository<IssueNotice, Long> {
    List<IssueNotice> findByPriority(String priority);
}
