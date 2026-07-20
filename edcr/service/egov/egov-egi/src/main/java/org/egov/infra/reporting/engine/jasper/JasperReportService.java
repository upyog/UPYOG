package org.egov.infra.reporting.engine.jasper;

import static org.egov.infra.reporting.engine.ReportFormat.CSV;
import static org.egov.infra.reporting.engine.ReportFormat.HTM;
import static org.egov.infra.reporting.engine.ReportFormat.PDF;
import static org.egov.infra.reporting.engine.ReportFormat.RTF;
import static org.egov.infra.reporting.engine.ReportFormat.TXT;
import static org.egov.infra.reporting.engine.ReportFormat.XLS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.reporting.engine.AbstractReportService;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.query.JRHibernateQueryExecuterFactory;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.ExporterConfiguration;
import net.sf.jasperreports.export.ExporterOutput;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterConfiguration;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleRtfExporterConfiguration;
import net.sf.jasperreports.export.SimpleTextExporterConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsExporterConfiguration;

public class JasperReportService extends AbstractReportService<JasperReport> {

	 static {
            /*
                Configure Jasper to use JRThreadSubreportRunnerFactory for executing
                subreports, which helps handle subreport processing efficiently.
            */
	        System.setProperty(
	            "net.sf.jasperreports.subreport.runner.factory",
	            "net.sf.jasperreports.engine.fill.JRThreadSubreportRunnerFactory"
	        );
            /*
                Specify the custom jasperreports.properties file so JasperReports
                loads application-specific report configuration (fonts, export settings,
                performance-related properties, etc.) during report generation.
            */
	        System.setProperty(
	            DefaultJasperReportsContext.PROPERTIES_FILE,
	            "config/jasperreports.properties"
	        );
	    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JasperReportService.class);

    private static final String TEMPLATE_EXTENSION = ".jrxml";
    private static final String JASPER_PROPERTIES_FILE = "config/jasperreports.properties";
    private static final String EXCEPTION_IN_REPORT_CREATION = "Error occurred while generating report.";
    private static final String PRINT_PDF_JAVASCRIPT = "this.print()";

    @PersistenceContext
    private EntityManager entityManager;

    /*
    The report compiling logic is changed to compile the report template every time instead of caching it.
    This is done to ensure that any changes made to the report template are reflected in the generated report without requiring a restart of the application.
    The caching logic has been removed from the constructor and the createReportFromSql, createReportFromJavaBean, and createReportFromHql methods.
     */
    public JasperReportService(int templateCacheMinSize, int templateCacheMaxSize) {
    	super(0, 0);
    }

    @Override
    protected String getTemplateExtension() {
        return TEMPLATE_EXTENSION;
    }

    /*
    Generates a Jasper report using the supplied JRXML template and database connection. The template is compiled, populated with data
    by executing its SQL queries, exported to the requested format,and returned as a ReportOutput.
    */
    @Override
    protected ReportOutput createReportFromSql(ReportRequest reportInput, Connection connection) {
        try {

            InputStream is = getClass().getClassLoader()
                    .getResourceAsStream(reportInput.getReportTemplate());

            JasperReport jasperReport = JasperCompileManager.compileReport(is);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    reportInput.getReportParams(),
                    connection
            );

            return new ReportOutput(exportReport(reportInput, jasperPrint), reportInput);

        } catch (Exception e) {
            LOGGER.error(EXCEPTION_IN_REPORT_CREATION, e);
        }
        return null;
    }
    @Override
    protected ReportOutput createReportFromJavaBean(ReportRequest reportInput) {
        try {
            Object reportData = reportInput.getReportInputData();
            JRDataSource dataSource;

            if (reportData == null) {
                dataSource = new JREmptyDataSource();
            } else if (reportData.getClass().isArray()) {
                dataSource = new JRBeanArrayDataSource((Object[]) reportData, false);
            } else if (reportData instanceof Collection) {
                dataSource = new JRBeanCollectionDataSource((Collection) reportData, false);
            } else {
                dataSource = new JRBeanArrayDataSource(new Object[]{reportData}, false);
            }


            InputStream is = getClass().getClassLoader()
                    .getResourceAsStream(reportInput.getReportTemplate());

            JasperReport jasperReport = JasperCompileManager.compileReport(is);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    reportInput.getReportParams(),
                    dataSource
            );

            return new ReportOutput(exportReport(reportInput, jasperPrint), reportInput);

        } catch (Exception e) {
            LOGGER.error(EXCEPTION_IN_REPORT_CREATION, e);
        }
        return null;
    }
    @Override
    protected ReportOutput createReportFromHql(ReportRequest reportInput) {
        try {

            Map<String, Object> reportParams = reportInput.getReportParams();
            if (reportParams == null) {
                reportParams = new HashMap<>();
            }

            reportParams.put(
                    JRHibernateQueryExecuterFactory.PARAMETER_HIBERNATE_SESSION,
                    entityManager.unwrap(Session.class)
            );

            InputStream is = getClass().getClassLoader()
                    .getResourceAsStream(reportInput.getReportTemplate());

            JasperReport jasperReport = JasperCompileManager.compileReport(is);

            // Here JREmptyDataSource is provided as data is passed with the use of hibernate session in reportparams.put function.
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    reportParams,
                    new JREmptyDataSource()
            );

            return new ReportOutput(exportReport(reportInput, jasperPrint), reportInput);

        } catch (Exception e) {
            LOGGER.error(EXCEPTION_IN_REPORT_CREATION, e);
        }
        return null;
    }

    @Override
    protected JasperReport loadTemplate(InputStream templateInputStream) {
        try {
            return JasperCompileManager.compileReport(templateInputStream);
        } catch (JRException e) {
            LOGGER.error(EXCEPTION_IN_REPORT_CREATION, e);
            throw new RuntimeException(e);
        }
    }
    private byte[] exportReport(ReportRequest reportInput, JasperPrint jasperPrint) throws JRException, IOException {
        try (ByteArrayOutputStream reportOutputStream = new ByteArrayOutputStream()) {
            Exporter exporter = getExporter(reportInput, jasperPrint, reportOutputStream);
            exporter.exportReport();
            return reportOutputStream.toByteArray();
        } catch (JRException | IOException e) {
            LOGGER.error(EXCEPTION_IN_REPORT_CREATION, e);
        }
        return null;
    }

    private Exporter getExporter(ReportRequest reportInput, JasperPrint jasperPrint, OutputStream outputStream) {
        Exporter exporter;
        ExporterConfiguration exporterConfiguration;
        ExporterOutput exporterOutput = null;
        if (PDF.equals(reportInput.getReportFormat())) {
            SimplePdfExporterConfiguration pdfExporterConfiguration = new SimplePdfExporterConfiguration();
            if (reportInput.isPrintDialogOnOpenReport())
                pdfExporterConfiguration.setPdfJavaScript(PRINT_PDF_JAVASCRIPT);
            exporter = new JRPdfExporter();
            exporterConfiguration = pdfExporterConfiguration;
        } else if (XLS.equals(reportInput.getReportFormat())) {
            exporter = new JRXlsExporter();
            exporterConfiguration = new SimpleXlsExporterConfiguration();
        } else if (RTF.equals(reportInput.getReportFormat())) {
            exporter = new JRRtfExporter();
            exporterConfiguration = new SimpleRtfExporterConfiguration();
            exporterOutput= new SimpleWriterExporterOutput(outputStream);
        } else if (HTM.equals(reportInput.getReportFormat())) {
            exporter = new HtmlExporter();
            exporterConfiguration = new SimpleHtmlExporterConfiguration();
            exporterOutput = new SimpleHtmlExporterOutput(outputStream);
        } else if (TXT.equals(reportInput.getReportFormat())) {
            exporter = new JRTextExporter();
            exporterConfiguration = new SimpleTextExporterConfiguration();
        } else if (CSV.equals(reportInput.getReportFormat())) {
            exporter = new JRCsvExporter();
            exporterConfiguration = new SimpleCsvExporterConfiguration();
        } else {
            throw new ApplicationRuntimeException("Invalid report format [" + reportInput.getReportFormat() + "]");
        }

        exporter.setConfiguration(exporterConfiguration);
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(exporterOutput == null ? new SimpleOutputStreamExporterOutput(outputStream) : exporterOutput);
        return exporter;
    }
}
