package com.example.employeems.controller;

import com.example.employeems.dto.ApiResponse;
import com.example.employeems.entity.LeaveRequest;
import com.example.employeems.dto.LeaveResponse;
import com.example.employeems.service.LeaveService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) { this.leaveService = leaveService; }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Object> list() { List<LeaveRequest> items = leaveService.listAll(); List<LeaveResponse> dtos = items.stream().map(this::toDto).toList(); HashMap<String,Object> data = new HashMap<>(); data.put("items", dtos); return ApiResponse.ok("Leaves loaded", data); }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<LeaveResponse> getById(@PathVariable Long id) { return ApiResponse.ok("Leave loaded", toDto(leaveService.getById(id))); }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<LeaveResponse> create(@RequestBody HashMap<String,Object> body) {
        Number empId = (Number) body.get("employeeId");
        LeaveRequest lr = new LeaveRequest();
        lr.setStartDate(body.get("startDate") == null ? null : java.time.LocalDate.parse((String)body.get("startDate")));
        lr.setEndDate(body.get("endDate") == null ? null : java.time.LocalDate.parse((String)body.get("endDate")));
        lr.setReason((String) body.get("reason"));
        LeaveRequest created = leaveService.create(empId.longValue(), lr);
        return ApiResponse.ok("Leave requested", toDto(created));
    }

    @PutMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<LeaveResponse> updateStatus(@PathVariable Long id, @RequestBody HashMap<String,String> body) {
        String status = body.get("status");
        return ApiResponse.ok("Status updated", toDto(leaveService.updateStatus(id, status)));
    }

    private LeaveResponse toDto(LeaveRequest lr) {
        if (lr == null) return null;
        LeaveResponse r = new LeaveResponse();
        r.setId(lr.getId());
        if (lr.getEmployee() != null) { r.setEmployeeId(lr.getEmployee().getId()); r.setEmployeeName(lr.getEmployee().getFullName()); }
        r.setStartDate(lr.getStartDate() == null ? null : lr.getStartDate().toString());
        r.setEndDate(lr.getEndDate() == null ? null : lr.getEndDate().toString());
        r.setReason(lr.getReason());
        r.setStatus(lr.getStatus());
        r.setCreatedAt(lr.getCreatedAt());
        r.setUpdatedAt(lr.getUpdatedAt());
        return r;
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Object> delete(@PathVariable Long id) { leaveService.delete(id); return ApiResponse.ok("Leave deleted", null); }
}
