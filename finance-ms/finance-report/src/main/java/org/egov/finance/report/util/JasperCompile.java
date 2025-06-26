/**
 * Created on Jun 20, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.util;

import net.sf.jasperreports.engine.*;

public class JasperCompile {
    public static void main(String[] args) throws JRException {
        JasperCompileManager.compileReportToFile(
            "C:/jasper/journalVoucherReport.jrxml",
            "C:/jasper/journalVoucherReport_new.jasper"
        );
    }
}

