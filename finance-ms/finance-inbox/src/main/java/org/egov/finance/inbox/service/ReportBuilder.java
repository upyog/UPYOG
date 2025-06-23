/**
 * Created on Jun 20, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.lettuce.core.dynamic.annotation.CommandNaming;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Slf4j
@Component
public class ReportBuilder {
	
	 private InputStream reportStream;
	 ByteArrayOutputStream outputBytes;
	 private static final int MB = 1024 * 1024;
	 
	 public InputStream exportPdf(final String jasperPath,
	            final Map<String, Object> paramMap, final List<Object> dataSource)
	            throws JRException, IOException {
	        try (InputStream reportStream = this.getClass().getResourceAsStream(jasperPath);
	             ByteArrayOutputStream outputBytes = new ByteArrayOutputStream(MB)) {

	            JasperPrint jasperPrint = setUpAndGetJasperPrint(reportStream, paramMap, dataSource);
	            JasperExportManager.exportReportToPdfStream(jasperPrint, outputBytes);
	            return new ByteArrayInputStream(outputBytes.toByteArray());

	        } catch (IOException | JRException e) {
	            log.error("Error exporting PDF: {}", e.getMessage(), e);
	            throw e;
	        }
	    }

	    private JasperPrint setUpAndGetJasperPrint(InputStream reportStream,
	            final Map<String, Object> paramMap, final List<Object> dataSource)
	            throws JRException {
	        if (dataSource != null && !dataSource.isEmpty()) {
	            return JasperFillManager.fillReport(reportStream, paramMap, new JRBeanCollectionDataSource(dataSource));
	        }
	        return JasperFillManager.fillReport(reportStream, paramMap, new JREmptyDataSource());
	    }
	 protected void closeStream(final InputStream stream) {
	        if (stream != null)
	            try {
	                stream.close();
	            } catch (final IOException e) {
	                
	                    log.debug("Error{}" ,e.getMessage());

	            }
	    }

}

