package com.example.employeems.controller;

import com.example.employeems.dto.ApiResponse;
import com.example.employeems.entity.IssueNotice;
import com.example.employeems.dto.NoticeResponse;
import com.example.employeems.service.NoticeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) { this.noticeService = noticeService; }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Object> list() { List<IssueNotice> items = noticeService.listAll(); List<NoticeResponse> dtos = items.stream().map(this::toDto).toList(); HashMap<String,Object> data = new HashMap<>(); data.put("items", dtos); return ApiResponse.ok("Notices loaded", data); }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<NoticeResponse> getById(@PathVariable Long id) { return ApiResponse.ok("Notice loaded", toDto(noticeService.getById(id))); }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<NoticeResponse> create(@RequestBody IssueNotice payload) { return ApiResponse.ok("Notice created", toDto(noticeService.create(payload))); }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<NoticeResponse> update(@PathVariable Long id, @RequestBody IssueNotice payload) { return ApiResponse.ok("Notice updated", toDto(noticeService.update(id, payload))); }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Object> delete(@PathVariable Long id) { noticeService.delete(id); return ApiResponse.ok("Notice deleted", null); }

    private NoticeResponse toDto(IssueNotice n) {
        if (n == null) return null;
        NoticeResponse r = new NoticeResponse();
        r.setId(n.getId());
        r.setTitle(n.getTitle());
        r.setMessage(n.getMessage());
        r.setPriority(n.getPriority());
        r.setCreatedAt(n.getCreatedAt());
        return r;
    }
}
