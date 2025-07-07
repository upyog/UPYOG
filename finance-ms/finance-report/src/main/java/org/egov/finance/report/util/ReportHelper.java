/**
 * @author bpattanayak
 * @date 2 Jul 2025
 */

package org.egov.finance.report.util;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.egov.finance.report.entity.Fund;
import org.egov.finance.report.model.Statement;
import org.springframework.stereotype.Component;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
//import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;

@Component
public class ReportHelper {
	
	private static final int MB = 1024 * 1024;
    ByteArrayOutputStream outputBytes;
    private InputStream reportStream;
    private static SimpleDateFormat FORMATDDMMYYYY = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    
    public JasperPrint generateFinancialStatementReportJasperPrint(
            final Statement balanceSheet, final String heading, final String addHeading,
            final String fromDate, final String toDate, final boolean showScheduleColumn) throws JRException {

        Style detailAmountStyle = getDetailAmountStyle();
        Style columnStyle = getColumnStyle();
        FastReportBuilder drb = new FastReportBuilder();

        try {
            drb.addColumn("Account Code", "glCode", String.class.getName(), 50, columnStyle)
               .addColumn("Head of Account", "accountName", String.class.getName(), 100, columnStyle);

            if (showScheduleColumn) {
                drb.addColumn("Schedule No", "scheduleNo", String.class.getName(), 60, columnStyle);
            }

            drb.setTitle(heading + " " + balanceSheet.getFinancialYear().getFinYearRange() + " " + addHeading)
               .setSubtitle("Report Run Date: " + FORMATDDMMYYYY.format(new Date()) +
                            "                                                                 Amount in " + balanceSheet.getCurrency())
               .setPrintBackgroundOnOddRows(true)
               .setWhenNoData("No data", null)
               .setDefaultStyles(getTitleStyle(), getSubTitleStyle(), getHeaderStyle(), getDetailStyle())
               .setOddRowBackgroundStyle(getOddRowStyle())
               .setDetailHeight(20)
               .setHeaderHeight(50)
               .setUseFullPageWidth(true)
               .setPageSizeAndOrientation(new Page(612, 792, false));

            if (balanceSheet.getFundList().size() > 1) {
                for (Fund fund : balanceSheet.getFundList()) {
                    try {
                        drb.addColumn(fund.getName() + " (Rs)", "fundWiseAmount." + fund.getName(),
                                      BigDecimal.class.getName(), 55, false, "0.00", detailAmountStyle);
                    } catch (ColumnBuilderException | ClassNotFoundException e) {
                       // LOGGER.error("Error adding fund column: " + e.getMessage(), e);
                    }
                }
            }

            drb.addColumn(fromDate + " (Rs)", "currentYearTotal", BigDecimal.class.getName(), 55, false, "0.00", detailAmountStyle)
               .addColumn(toDate + " (Rs)", "previousYearTotal", BigDecimal.class.getName(), 55, false, "0.00", detailAmountStyle);

        } catch (ColumnBuilderException | ClassNotFoundException e) {
           // LOGGER.error("Error building report columns: " + e.getMessage(), e);
        }

        DynamicReport dr = drb.build();
        JRDataSource ds = new JRBeanCollectionDataSource(balanceSheet.getEntries());

        return DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);
    }
    
    private Style getDetailAmountStyle() {
        Style style = new Style("detailAmount");

        style.setBorderLeft(Border.THIN());
        style.setBorderRight(Border.THIN());
        style.setBorderBottom(Border.THIN());

        style.setTextColor(Color.blue);
        //style.setHorizontalTextAlign(HorizontalTextAlignment.RIGHT);
        style.setHorizontalAlign(HorizontalAlign.RIGHT); 
        style.setFont(new Font(6, Font._FONT_VERDANA, true));
        style.setPaddingRight(2);
        style.setTransparency(Transparency.OPAQUE);

        return style;
    }
    
    private Style getColumnStyle() {
        final Style columnStyle = new Style("ColumnCss");
        columnStyle.setBorderLeft(Border.THIN());
        columnStyle.setBorderRight(Border.THIN());

        columnStyle.setTextColor(Color.blue);
        columnStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        columnStyle.setFont(new Font(6, Font._FONT_VERDANA, true));
        columnStyle.setTransparency(Transparency.OPAQUE);
        columnStyle.setBorderBottom(Border.THIN());
        // detailAmountStyle.s
        return columnStyle;
    }
    
    private Style getTitleStyle() {
        final Style titleStyle = new Style("titleStyle");
        titleStyle.setFont(new Font(9, Font._FONT_VERDANA, true));
        titleStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        return titleStyle;
    }
    
    private Style getSubTitleStyle() {
        final Style subTitleStyle = new Style("titleStyle");
        subTitleStyle.setFont(new Font(6, Font._FONT_VERDANA, true));
        subTitleStyle.setHorizontalAlign(HorizontalAlign.CENTER);

        return subTitleStyle;
    }
    
    private Style getHeaderStyle() {
        final Style headerStyle = new Style("header");
        headerStyle.setFont(Font.ARIAL_MEDIUM_BOLD);
        headerStyle.setBorder(Border.THIN());
        headerStyle.setBackgroundColor(new Color(204, 204, 204));
        headerStyle.setTextColor(Color.blue);
        headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);
        headerStyle.setTransparency(Transparency.OPAQUE);
        headerStyle.setFont(new Font(8, Font._FONT_VERDANA, true));
        headerStyle.setStretchWithOverflow(true);
        return headerStyle;
    }
    
    private Style getDetailStyle() {
        final Style detailStyle = new Style("detail");
        detailStyle.setBorderLeft(Border.THIN());
        detailStyle.setBorderRight(Border.THIN());
        detailStyle.setTextColor(Color.blue);
        detailStyle.setFont(new Font(8, Font._FONT_VERDANA, true));
        detailStyle.setTransparency(Transparency.OPAQUE);
        return detailStyle;
    }
    
    private Style getOddRowStyle() {
        final Style oddRowStyle = new Style();
        oddRowStyle.setBackgroundColor(new Color(247, 247, 247));
        oddRowStyle.setTransparency(Transparency.OPAQUE);
        return oddRowStyle;
    }
    
    protected void closeStream(final InputStream stream) {
        if (stream != null)
            try {
                stream.close();
            } catch (final IOException e) {
               // if (LOGGER.isDebugEnabled())
                 //   LOGGER.debug("Error" + e.getMessage());

            }
    }

	/**
	 * @param inputStream
	 * @param jasper
	 * @return
	 */
    public InputStream exportPdf(InputStream inputStream, JasperPrint jasper) {
    	outputBytes = new ByteArrayOutputStream(1 * MB);
        try {
			JasperExportManager.exportReportToPdfStream(jasper, outputBytes);
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exception is :::"+e.getLocalizedMessage());
		}
        inputStream = new ByteArrayInputStream(outputBytes.toByteArray());
        closeStream(reportStream);
        return inputStream;
    }



}
