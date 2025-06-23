/**
 * Created on Jun 17, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.finance.inbox.exception.ReportServiceException;
import org.egov.finance.inbox.util.ReportConstants;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class JasperReportHelperService {

	protected JasperPrint setUpAndGetJasperPrint(final String jasperPath, final Map<String, Object> paramMap,
			final List<Object> dataSource) throws JRException, IOException {
		Map<String, String> error = new HashMap<>();
		try (InputStream reportStream = this.getClass().getResourceAsStream(jasperPath)) {
			if (reportStream == null) {
				error.put(ReportConstants.JASPER_REPORT_TEMPLATE_NOT_FOUND_AT_PATH,
						ReportConstants.JASPER_REPORT_TEMPLATE_NOT_FOUND_AT_PATH_MSG + jasperPath);
				throw new ReportServiceException(error);
			}
			if (dataSource != null && !dataSource.isEmpty()) {
				return JasperFillManager.fillReport(reportStream, paramMap, new JRBeanCollectionDataSource(dataSource));
			} else {
				return JasperFillManager.fillReport(reportStream, paramMap, new JREmptyDataSource());
			}
		}
	}

	public InputStream exportPdf(final String jasperPath, final Map<String, Object> paramMap,
			final List<Object> dataSource) throws JRException, IOException {
		ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
		JasperPrint jasperPrint = setUpAndGetJasperPrint(jasperPath, paramMap, dataSource);
		JasperExportManager.exportReportToPdfStream(jasperPrint, outputBytes);

		return new ByteArrayInputStream(outputBytes.toByteArray());
	}

}
