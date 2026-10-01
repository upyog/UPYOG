package org.upyog.Automation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object representing execution report metadata.
 *
 * <p>Used for presenting historical or generated HTML report files in the UI dashboard.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReportDto {

    /**
     * File name of the generated HTML report.
     */
    private String fileName;

    /**
     * Date string when the report was generated.
     */
    private String date;

    /**
     * Time string when the report was generated.
     */
    private String time;

    /**
     * Overall execution status recorded in the report (e.g., "PASSED", "FAILED").
     */
    private String status;

    /**
     * Constructs a ReportDto with basic file metadata.
     *
     * @param fileName the report file name
     * @param date generation date
     * @param time generation time
     */
    public ReportDto(String fileName, String date, String time) {
        this.fileName = fileName;
        this.date = date;
        this.time = time;
    }
}