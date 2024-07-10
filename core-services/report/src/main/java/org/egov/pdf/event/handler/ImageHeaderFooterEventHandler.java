package org.egov.pdf.event.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

@Component
public class ImageHeaderFooterEventHandler implements IEventHandler {

	@Value("${hpudd.logo.v2.path}")
	private String hpuddLogoPath;

	@Value("${hpudd.office.address.line1}")
	private String hpuddOfficeAddressLine1;

	@Value("${hpudd.office.address.line2}")
	private String hpuddOfficeAddressLine2;

	@Value("${hpudd.office.mobile-number}")
	private String hpuddOfficeMobileNumber;

	@Value("${hpudd.office.email-id}")
	private String hpuddOfficeEmailId;

	@Value("${hpudd.office.website}")
	private String hpuddOfficeWebsite;

	@Override
	public void handleEvent(Event event) {
		PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
		PdfPage page = docEvent.getPage();
		PdfCanvas canvas = new PdfCanvas(page);
		Rectangle pageSize = page.getPageSize();
		float margin = 36; // left and right margin
		try {
			// Load header image
			Image headerImage = new Image(ImageDataFactory.create(hpuddLogoPath))
					.scaleToFit(pageSize.getWidth() - 2 * margin, 70) // Adjust size as needed
					.setFixedPosition(margin, pageSize.getTop() - 70);

			// Add the images to the document
			new Canvas(canvas, pageSize).add(headerImage);

			// Draw a Line above footer
			canvas.moveTo(36, 60);
			canvas.lineTo(575, 60);
			// Set the color for the line
			canvas.setStrokeColor(new DeviceRgb(173, 193, 82));

			canvas.stroke();

			// Footer Paragraph canvas
			Canvas footerCanvas = new Canvas(canvas, pageSize);

			footerCanvas.setFontSize(7);

			Paragraph leftText = new Paragraph().add(new Text(hpuddOfficeAddressLine1 + "\n"))
					.add(new Text(hpuddOfficeAddressLine2));
			leftText.setTextAlignment(TextAlignment.LEFT);
			footerCanvas.showTextAligned(leftText, margin, pageSize.getBottom() + 30, TextAlignment.LEFT,
					VerticalAlignment.BOTTOM);

			Paragraph rightText = new Paragraph();
			rightText.add(new Text("T : " + hpuddOfficeMobileNumber + "\n"))
					.add(new Text("E : " + hpuddOfficeEmailId + "\n")).add(new Text("W : " + hpuddOfficeWebsite));
			footerCanvas.showTextAligned(rightText, pageSize.getWidth() - margin, pageSize.getBottom() + 30,
					TextAlignment.RIGHT, VerticalAlignment.BOTTOM);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}