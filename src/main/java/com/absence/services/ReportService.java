package com.absence.services;

import java.io.OutputStream;

public interface ReportService {
    void exportExcelTimesheet(int month, int year, String employeeId, OutputStream outputStream);
}
