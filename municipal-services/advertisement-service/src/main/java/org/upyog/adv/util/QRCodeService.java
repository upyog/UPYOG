package org.upyog.adv.util;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.net.URLEncoder;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.springframework.stereotype.Service;
import org.upyog.adv.web.models.Address;
import org.upyog.adv.web.models.ApplicantDetail;
import org.upyog.adv.web.models.BookingDetail;

@Service
public class QRCodeService {

    public byte[] generateQRCodeImage(String payload, int width, int height) throws Exception {
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

        BitMatrix bitMatrix = new MultiFormatWriter().encode(payload, BarcodeFormat.QR_CODE, width, height, hints);
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    /**
     * Build a simple plain-text payload from BookingDetail. This is intentionally
     * human-readable (line separated) so a scanner shows plain text.
     */
    public String buildPlainTextForBooking(BookingDetail booking) {
        StringBuilder sb = new StringBuilder();
        sb.append("BookingNo: ").append(nullToEmpty(booking.getBookingNo())).append('\n');

        ApplicantDetail applicant = booking.getApplicantDetail();
        if (applicant != null) {
            sb.append("Name: ").append(nullToEmpty(applicant.getApplicantName())).append('\n');
            sb.append("Mobile: ").append(nullToEmpty(applicant.getApplicantMobileNo())).append('\n');
            sb.append("Email: ").append(nullToEmpty(applicant.getApplicantEmailId())).append('\n');
        }

        Address addr = booking.getAddress();
        if (addr != null) {
            String addrLine = String.join(", ", safe(addr.getAddressLine1()), safe(addr.getAddressLine2()), safe(addr.getLandmark()), safe(addr.getCity()));
            sb.append("Address: ").append(addrLine.replaceAll(", \\z", "")).append('\n');
        }

        sb.append("Status: ").append(nullToEmpty(booking.getBookingStatus()));


        String plain = sb.toString();
        return plain;
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

}
