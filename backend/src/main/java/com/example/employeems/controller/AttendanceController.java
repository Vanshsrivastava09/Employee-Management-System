package com.example.employeems.controller;

import com.example.employeems.dto.ApiResponse;
import com.example.employeems.entity.Attendance;
import com.example.employeems.dto.AttendanceResponse;
import com.example.employeems.service.AttendanceService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) { this.attendanceService = attendanceService; }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Object> list() { List<Attendance> items = attendanceService.listAll(); List<AttendanceResponse> dtos = items.stream().map(this::toDto).toList(); HashMap<String,Object> data = new HashMap<>(); data.put("items", dtos); return ApiResponse.ok("Attendances loaded", data); }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<AttendanceResponse> getById(@PathVariable Long id) { return ApiResponse.ok("Attendance loaded", toDto(attendanceService.getById(id))); }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<AttendanceResponse> create(@RequestBody HashMap<String,Object> body) {
        Number empId = (Number) body.get("employeeId");
        Attendance a = new Attendance();
        a.setDate(body.get("date") == null ? null : java.time.LocalDate.parse((String)body.get("date")));
        a.setStatus((String) body.get("status"));
        a.setNote((String) body.get("note"));
        Attendance created = attendanceService.create(empId.longValue(), a);
        return ApiResponse.ok("Attendance recorded", toDto(created));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<AttendanceResponse> update(@PathVariable Long id, @RequestBody Attendance payload) { return ApiResponse.ok("Attendance updated", toDto(attendanceService.update(id, payload))); }

    private AttendanceResponse toDto(Attendance a) {
        if (a == null) return null;
        AttendanceResponse r = new AttendanceResponse();
        r.setId(a.getId());
        if (a.getEmployee() != null) { r.setEmployeeId(a.getEmployee().getId()); r.setEmployeeName(a.getEmployee().getFullName()); }
        r.setDate(a.getDate() == null ? null : a.getDate().toString());
        r.setStatus(a.getStatus());
        r.setNote(a.getNote());
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Object> delete(@PathVariable Long id) { attendanceService.delete(id); return ApiResponse.ok("Attendance deleted", null); }
}
