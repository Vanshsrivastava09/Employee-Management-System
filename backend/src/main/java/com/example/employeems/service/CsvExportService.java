package com.example.employeems.service;

import com.example.employeems.entity.Department;
import com.example.employeems.entity.Employee;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * Simple CSV exporter.
 *
 * Beginner tip: generating CSV in service layer keeps controller thin.
 */
@Service
public class CsvExportService {

    public String employeesToCsv(List<Employee> employees, Department department) throws IOException {
        StringWriter writer = new StringWriter();

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("Id", "Full Name", "Email", "Phone", "Department", "Active")
                .setSkipHeaderRecord(false)
                .build();

        CSVPrinter printer = new CSVPrinter(writer, format);

        for (Employee e : employees) {
            printer.printRecord(
                    e.getId(),
                    e.getFullName(),
                    e.getEmail(),
                    e.getPhone(),
                    e.getDepartment() != null ? e.getDepartment().getName() : "",
                    e.isActive()
            );
        }

        printer.flush();
        return writer.toString();
    }
}
