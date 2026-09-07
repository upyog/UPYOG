package org.upyog.Automation.model;

public class ReportDto {

    private String fileName;
    private String date;
    private String time;
    private String status;

    public ReportDto() {
    }

    public ReportDto(String fileName, String date, String time) {
        this.fileName = fileName;
        this.date = date;
        this.time = time;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}