package com.example.employeems.controller;

import com.example.employeems.dto.ApiResponse;
import com.example.employeems.entity.Department;
import com.example.employeems.entity.Employee;
import com.example.employeems.exception.BadRequestException;
import com.example.employeems.exception.NotFoundException;
import com.example.employeems.repository.DepartmentRepository;
import com.example.employeems.repository.EmployeeRepository;
import com.example.employeems.service.CsvExportService;
import com.example.employeems.util.FileStorageUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Employee media + export endpoints.
 *
 * Why separate controller?
 * - Keeps EmployeeController focused on JSON CRUD
 * - Media upload/serve + CSV export are different concerns
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeMediaController {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final CsvExportService csvExportService;
    private final FileStorageUtil fileStorageUtil;

    public EmployeeMediaController(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            CsvExportService csvExportService,
            FileStorageUtil fileStorageUtil
    ) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.csvExportService = csvExportService;
        this.fileStorageUtil = fileStorageUtil;
    }

    /**
     * Upload employee profile photo (multipart/form-data).
     *
     * Example:
     * POST /api/employees/{id}/photo
     * form-data: file=<image>
     */
    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Object> uploadPhoto(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id=" + id));

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Photo file is required");
        }

        // Store image on local disk
        String storedFilename = fileStorageUtil.saveImage(file);

        // Save photoUrl in DB (frontend uses this to display avatar)
        String photoUrl = fileStorageUtil.toPhotoUrl(storedFilename);
        emp.setPhotoUrl(photoUrl);

        employeeRepository.save(emp);

        return ApiResponse.ok("Photo uploaded", null);
    }

    /**
     * Serve uploaded employee photo.
     *
     * GET /api/employees/photos/{filename}
     */
    @GetMapping(value = "/photos/{filename}")
    public ResponseEntity<byte[]> servePhoto(@PathVariable("filename") String filename) throws IOException {
        if (filename == null || filename.isBlank()) {
            return ResponseEntity.notFound().build();
        }

        byte[] bytes = fileStorageUtil.readImage(filename);
        if (bytes == null) {
            return ResponseEntity.notFound().build();
        }

        MediaType contentType = fileStorageUtil.detectMediaType(filename);
        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(bytes);
    }

    /**
     * Export employees to CSV.
     *
     * GET /api/employees/export?search=&departmentId=
     *
     * Beginner-friendly:
     * - simple filtering in-memory
     * - downloads a CSV file in browser
     */
    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "departmentId", required = false) Long departmentId
    ) throws IOException {

        String s = (search == null || search.isBlank()) ? null : search.trim().toLowerCase();

        Department dept = null;
        if (departmentId != null) {
            dept = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new NotFoundException("Department not found with id=" + departmentId));
        }

        List<Employee> all = employeeRepository.findAll();

        // Basic filter (same spirit as EmployeeService but simpler for export)
        List<Employee> filtered = all.stream()
                .filter(e -> departmentId == null || (e.getDepartment() != null && departmentId.equals(e.getDepartment().getId())))
                .filter(e -> {
                    if (s == null) return true;
                    String fullName = e.getFullName() == null ? "" : e.getFullName().toLowerCase();
                    String email = e.getEmail() == null ? "" : e.getEmail().toLowerCase();
                    String phone = e.getPhone() == null ? "" : e.getPhone().toLowerCase();
                    return fullName.contains(s) || email.contains(s) || phone.contains(s);
                })
                .toList();

        String csv = csvExportService.employeesToCsv(filtered, dept);

        byte[] bytes = csv.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        String outName = "employees.csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + outName + "\"")
                .contentType(MediaType.valueOf("text/csv"))
                .body(bytes);
    }
}
