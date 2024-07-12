package org.egov.pdf.event.handler;

import org.egov.tracer.model.CustomException;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextHeaderFooterEventHandler implements IEventHandler {

	private String headerText;
	private String footerText;

	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	public void setFooterText(String footerText) {
		this.footerText = footerText;
	}

	@Override
	public void handleEvent(Event event) {
		try {
			PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
			PdfDocument pdfDoc = docEvent.getDocument();
			PdfPage page = docEvent.getPage();
			Rectangle pageSize = page.getPageSize();
			PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

			addHeader(pdfCanvas, pageSize);
			addFooter(pdfCanvas, pageSize);
		} catch (Exception ex) {
			log.error("Error occured while adding header and footer.", ex);
			throw new CustomException("ERR_REPORT_SERVICE",
					"Error occured while adding header and footer. Message: " + ex.getMessage());
		}
	}

	private void addHeader(PdfCanvas pdfCanvas, Rectangle pageSize) throws Exception {

		PdfFont font = PdfFontFactory.createFont();
		float fontSize = 10;

		// Calculate the width of the header text
		float textWidth = font.getWidth(headerText, fontSize);

		// Calculate the position to center the text
		float xPosition = (pageSize.getWidth() - textWidth) / 2;
		float yPosition = pageSize.getTop() - 20;

		// Add the header text to the PDF canvas
		pdfCanvas.beginText().setFontAndSize(font, fontSize).moveText(xPosition, yPosition).showText(headerText)
				.endText();
	}

	private void addFooter(PdfCanvas pdfCanvas, Rectangle pageSize) throws Exception {

		PdfFont font = PdfFontFactory.createFont();
		float fontSize = 8;

		// Calculate the width of the header text
		float textWidth = font.getWidth(footerText, fontSize);

		// Calculate the position to center the text
		float xPosition = (pageSize.getWidth() - textWidth) / 2;
		float yPosition = pageSize.getBottom() + 15;

		// Add the header text to the PDF canvas
		pdfCanvas.beginText().setFontAndSize(font, fontSize).moveText(xPosition, yPosition).showText(footerText)
				.endText();
	}
}