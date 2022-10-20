package com.absence.services;

import com.absence.models.Attendance;
import com.absence.models.Employee;
import com.absence.repositories.AttendanceRepository;
import com.absence.repositories.EmployeeRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Override
    public void exportExcelTimesheet(int month, int year, String employeeId, OutputStream outputStream) {
        SimpleDateFormat monthAndYear = new SimpleDateFormat("MMM yyyy");
        SimpleDateFormat dayName = new SimpleDateFormat("EEEE");
        SimpleDateFormat hour = new SimpleDateFormat("HH:mm");
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");

        Date startDate = getStartDate(month, year);
        Date endDate = getEndDate(month, year);
        try {
            Employee employee = employeeRepository.findById(employeeId).orElse(null);
            List<Attendance> attendances = attendanceRepository.findHistoriesAttendance(startDate, endDate, employeeId);
            Resource resource = new ClassPathResource("timesheet-template.xlsx");
            InputStream input = resource.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(input);
            Sheet sheet = workbook.getSheetAt(0);

            XSSFFont infoFont = workbook.createFont();
            infoFont.setFontName("Inter");
            infoFont.setFontHeightInPoints((short) 11);
            infoFont.setBold(true);

            XSSFFont bodyFont = workbook.createFont();
            bodyFont.setFontName("Inter");
            bodyFont.setFontHeightInPoints((short) 10);

            CellStyle infoStyle = workbook.createCellStyle();
            infoStyle.setFont(infoFont);
            infoStyle.setAlignment(HorizontalAlignment.LEFT);
            infoStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            infoStyle.setWrapText(true);

            CellStyle bodyInfoRightStyle = workbook.createCellStyle();
            bodyInfoRightStyle.setFont(infoFont);
            bodyInfoRightStyle.setAlignment(HorizontalAlignment.RIGHT);
            bodyInfoRightStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            bodyInfoRightStyle.setWrapText(true);

            Row infoRow = sheet.createRow(8);
            Cell infoCell = infoRow.createCell(1);
            assert employee != null;
            infoCell.setCellValue(monthAndYear.format(startDate));
            infoCell.setCellStyle(infoStyle);

            infoCell = infoRow.createCell(2);
            infoCell.setCellValue(employee.getEmployeeName());
            infoCell.setCellStyle(infoStyle);

            infoCell = infoRow.createCell(4);
            infoCell.setCellValue(employee.getJobTitle() == null ? "- " : employee.getJobTitle().getJobTitleName());
            infoCell.setCellStyle(infoStyle);

            infoCell = infoRow.createCell(5);
            infoCell.setCellValue(employee.getJobTitle() == null ? "- " : employee.getJobTitle().getDivision().getDivisionName());
            infoCell.setCellStyle(infoStyle);

            CellStyle bodyStyle = workbook.createCellStyle();
            bodyStyle.setFont(bodyFont);
            bodyStyle.setAlignment(HorizontalAlignment.LEFT);
            bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            bodyStyle.setWrapText(true);

            CellStyle bodyRightStyle = workbook.createCellStyle();
            bodyRightStyle.setFont(bodyFont);
            bodyRightStyle.setAlignment(HorizontalAlignment.RIGHT);
            bodyRightStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            bodyRightStyle.setWrapText(true);

            CellStyle bodyCenterStyle = workbook.createCellStyle();
            bodyCenterStyle.setFont(bodyFont);
            bodyCenterStyle.setAlignment(HorizontalAlignment.CENTER);
            bodyCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            bodyCenterStyle.setWrapText(true);

            int index = 11;
            Date actualDate = startDate;
            int totalDays = 0;
            int totalHours = 0;
            while (actualDate.compareTo(endDate) < 1) {

                LocalDateTime actualLocalDateTime = LocalDateTime.ofInstant(actualDate.toInstant(),
                        ZoneId.systemDefault());
                Row bodyRow = sheet.createRow(index);

                Date finalActualDate = actualDate;
                Attendance attendance = attendances.stream()
                        .filter(item -> finalActualDate.equals(item.getAttendanceDate()))
                        .findAny()
                        .orElse(null);

                if (attendance != null && attendance.getAttendanceType().getAttendanceTypeName().equals("Present")) {
                    totalDays++;
                }

                Cell bodyCell = bodyRow.createCell(1);
                bodyCell.setCellValue(actualLocalDateTime.getDayOfMonth());
                bodyCell.setCellStyle(bodyCenterStyle);

                bodyCell = bodyRow.createCell(2);
                bodyCell.setCellValue(dayName.format(actualDate));
                bodyCell.setCellStyle(bodyStyle);

                bodyCell = bodyRow.createCell(3);
                if (attendance != null && attendance.getAttendanceType().getAttendanceTypeName().equals("Present")) {
                    bodyCell.setCellValue(attendance.getProject().getProjectName());
                } else {
                    bodyCell.setCellValue("-");
                }
                bodyCell.setCellStyle(bodyStyle);

                if (attendance != null && attendance.getAttendanceType().getAttendanceTypeName().equals("Present")) {
                    bodyCell = bodyRow.createCell(4);
                    bodyCell.setCellValue(attendance.getTaskText());
                    bodyCell.setCellStyle(bodyStyle);
                } else if (attendance != null && attendance.getAttendanceType().getAttendanceTypeName().equals("Sick")) {
                    bodyCell = bodyRow.createCell(4);
                    bodyCell.setCellValue(attendance.getSick().getDescriptionText());
                    bodyCell.setCellStyle(bodyStyle);
                } else if (attendance != null && attendance.getAttendanceType().getAttendanceTypeName().equals("Leave")) {
                    bodyCell = bodyRow.createCell(4);
                    bodyCell.setCellValue(attendance.getLeaveSubmission().getDescriptionText());
                    bodyCell.setCellStyle(bodyStyle);
                }

                bodyCell = bodyRow.createCell(5);
                if (attendance != null && attendance.getAttendanceType().getAttendanceTypeName().equals("Present")) {
                    bodyCell.setCellValue(hour.format(attendance.getCheckInTime()));
                } else {
                    bodyCell.setCellValue("-");
                }
                bodyCell.setCellStyle(bodyCenterStyle);

                bodyCell = bodyRow.createCell(6);
                if (attendance != null && attendance.getAttendanceType().getAttendanceTypeName().equals("Present")) {
                    bodyCell.setCellValue(hour.format(attendance.getCheckOutTime()));
                } else {
                    bodyCell.setCellValue("-");

                }
                bodyCell.setCellStyle(bodyCenterStyle);

                bodyCell = bodyRow.createCell(7);
                if (attendance != null && attendance.getAttendanceType().getAttendanceTypeName().equals("Present")) {
                    bodyCell.setCellValue(attendance.getLocation());
                } else {
                    bodyCell.setCellValue("-");
                }
                bodyCell.setCellStyle(bodyStyle);

                bodyCell = bodyRow.createCell(8);
                bodyCell.setCellStyle(bodyRightStyle);
                if (attendance != null && attendance.getAttendanceType().getAttendanceTypeName().equals("Present")) {
                    long hourDiffTime = attendance.getCheckOutTime().getTime() - attendance.getCheckInTime().getTime();
                    long hourDiff = (hourDiffTime / (1000 * 60 * 60)) % 24;
                    totalHours+=hourDiff;
                    bodyCell.setCellValue(hourDiff);
                } else {
                    bodyCell.setCellValue("-");
                }

                index++;
                actualLocalDateTime = actualLocalDateTime.plusDays(1);
                actualDate = Date.from(actualLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
            }

            Row summaryHoursRow = sheet.createRow(index);
            Cell summaryHoursCell = summaryHoursRow.createCell(7);
            summaryHoursCell.setCellValue("Total Hours :");
            summaryHoursCell.setCellStyle(infoStyle);

            summaryHoursCell = summaryHoursRow.createCell(8);
            summaryHoursCell.setCellValue(totalHours);
            summaryHoursCell.setCellStyle(bodyInfoRightStyle);

            Row summaryDaysRow = sheet.createRow(index+1);
            Cell summaryDaysCell = summaryDaysRow.createCell(7);
            summaryDaysCell.setCellValue("Total Days :");
            summaryDaysCell.setCellStyle(infoStyle);

            summaryDaysCell = summaryDaysRow.createCell(8);
            summaryDaysCell.setCellValue(totalDays);
            summaryDaysCell.setCellStyle(bodyInfoRightStyle);

            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);

            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Date getStartDate(int month, int year) {
        Calendar calendar = getCalendarForNow();
        calendar.set(year, month,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setTimeToBeginningOfDay(calendar);
        return calendar.getTime();
    }

    private Date getEndDate(int month, int year) {
        Calendar calendar = getCalendarForNow();
        calendar.set(year, month,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setTimeToEndofDay(calendar);
        return calendar.getTime();
    }

    private static Calendar getCalendarForNow() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    private static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }
}
