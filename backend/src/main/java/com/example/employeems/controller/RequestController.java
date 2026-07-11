package com.example.employeems.controller;

import com.example.employeems.dto.ApiResponse;
import com.example.employeems.entity.EmployeeRequest;
import com.example.employeems.dto.RequestResponse;
import com.example.employeems.dto.RequestCreateRequest;
import com.example.employeems.service.RequestService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) { this.requestService = requestService; }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Object> list() { List<EmployeeRequest> items = requestService.listAll(); List<RequestResponse> dtos = items.stream().map(this::toDto).toList(); HashMap<String,Object> data = new HashMap<>(); data.put("items", dtos); return ApiResponse.ok("Requests loaded", data); }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<RequestResponse> getById(@PathVariable Long id) { return ApiResponse.ok("Request loaded", toDto(requestService.getById(id))); }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<RequestResponse> create(@Valid @RequestBody RequestCreateRequest request) {
        EmployeeRequest r = new EmployeeRequest();
        r.setTitle(request.getTitle());
        r.setDescription(request.getDescription());
        EmployeeRequest created = requestService.create(request.getEmployeeId(), r);
        return ApiResponse.ok("Request created", toDto(created));
    }

    @PutMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<RequestResponse> updateStatus(@PathVariable Long id, @RequestBody HashMap<String,String> body) {
        String status = body.get("status");
        return ApiResponse.ok("Status updated", toDto(requestService.updateStatus(id, status)));
    }

    private RequestResponse toDto(EmployeeRequest er) {
        if (er == null) return null;
        RequestResponse r = new RequestResponse();
        r.setId(er.getId());
        if (er.getEmployee() != null) { r.setEmployeeId(er.getEmployee().getId()); r.setEmployeeName(er.getEmployee().getFullName()); }
        r.setTitle(er.getTitle());
        r.setDescription(er.getDescription());
        r.setStatus(er.getStatus());
        r.setCreatedAt(er.getCreatedAt());
        r.setUpdatedAt(er.getUpdatedAt());
        return r;
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Object> delete(@PathVariable Long id) { requestService.delete(id); return ApiResponse.ok("Request deleted", null); }
}
