package com.example.employeems.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "issue_notices")
public class IssueNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(length = 2000)
    private String message;

    @Column(length = 20)
    private String priority; // LOW, MEDIUM, HIGH

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public IssueNotice() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
