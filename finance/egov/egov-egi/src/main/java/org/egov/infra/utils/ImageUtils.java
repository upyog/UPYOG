/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.infra.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.Iterator;

import static javax.imageio.ImageIO.createImageOutputStream;
import static javax.imageio.ImageIO.getImageWritersByFormatName;
import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;

public final class ImageUtils {
    public static final String JPG_EXTN = ".jpg";
    public static final String JPG_FORMAT_NAME = "jpeg";
    public static final String PNG_EXTN = ".png";
    public static final String PNG_FORMAT_NAME = "png";
    public static final String PNG_MIME_TYPE = "image/png";
    public static final String JPG_MIME_TYPE = "image/jpeg";

    private static final Logger LOG = LoggerFactory.getLogger(ImageUtils.class);

    private ImageUtils() {
        //Not to be initialized
    }

    public static File compressImage(MultipartFile imageFile) throws IOException {
        return compressImage(imageFile.getInputStream(), imageFile.getOriginalFilename(), true);
    }

    public static File compressImage(final InputStream imageStream, String imageFileName, boolean closeStream) throws IOException {
        File compressedImage = Paths.get(imageFileName).toFile();
        try (final ImageOutputStream imageOutput = createImageOutputStream(compressedImage)) {
            ImageWriter writer = getImageWritersByFormatName(defaultString(getExtension(imageFileName), JPG_FORMAT_NAME)).next();
            writer.setOutput(imageOutput);
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            if (writeParam.canWriteCompressed()) {
                writeParam.setCompressionMode(MODE_EXPLICIT);
                writeParam.setCompressionType(writeParam.getCompressionTypes()[0]);
                writeParam.setCompressionQuality(0.05F);
            }
            writer.write(null, new IIOImage(read(imageStream), null, null), writeParam);
            writer.dispose();
            if (closeStream)
                imageStream.close();
        }
        return compressedImage;
    }

    public static double[] findGeoCoordinates(File jpegImage) {
        if (JPG_FORMAT_NAME.equalsIgnoreCase(imageFormat(jpegImage))) {
            double[] coordinates = readGpsCoordinates(jpegImage);
            return coordinates != null ? coordinates : new double[]{0D, 0D};
        }
        return new double[]{0D, 0D};
    }

    public static String imageFormat(File image) {
        try (ImageInputStream iis = ImageIO.createImageInputStream(image)) {
            Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
            return imageReaders.hasNext() ? imageReaders.next().getFormatName() : EMPTY;
        } catch (IOException e) {
            LOG.warn("Could not read image format from file", e);
            return EMPTY;
        }
    }

    private static double[] readGpsCoordinates(File jpegImage) {
        try (RandomAccessFile raf = new RandomAccessFile(jpegImage, "r")) {
            if (raf.readUnsignedShort() != 0xFFD8) {
                return null;
            }

            while (true) {
                int markerPrefix;
                do {
                    markerPrefix = raf.readUnsignedByte();
                } while (markerPrefix != 0xFF);

                int markerType;
                do {
                    markerType = raf.readUnsignedByte();
                } while (markerType == 0xFF);

                if (markerType == 0xD9 || markerType == 0xDA) {
                    break;
                }

                int segmentLength = raf.readUnsignedShort();
                int segmentStart = (int) raf.getFilePointer();

                if (markerType == 0xE1) {
                    byte[] segment = new byte[segmentLength - 2];
                    raf.readFully(segment);
                    return parseExifGps(segment);
                }

                raf.seek(segmentStart + segmentLength - 2L);
            }
        } catch (IOException e) {
            LOG.warn("Could not read GPS coordinates from image", e);
        }
        return null;
    }

    private static double[] parseExifGps(byte[] segment) {
        if (segment.length < 14) {
            return null;
        }
        if (segment[0] != 'E' || segment[1] != 'x' || segment[2] != 'i' || segment[3] != 'f') {
            return null;
        }

        int tiffStart = 6;
        boolean littleEndian;
        if (segment[tiffStart] == 'I' && segment[tiffStart + 1] == 'I') {
            littleEndian = true;
        } else if (segment[tiffStart] == 'M' && segment[tiffStart + 1] == 'M') {
            littleEndian = false;
        } else {
            return null;
        }

        int ifd0Offset = readInt(segment, tiffStart + 4, littleEndian);
        int gpsIfdOffset = findGpsIfdOffset(segment, tiffStart, ifd0Offset, littleEndian);
        if (gpsIfdOffset <= 0) {
            return null;
        }

        String latRef = null;
        String lonRef = null;
        double[] lat = null;
        double[] lon = null;

        int entries = readShort(segment, tiffStart + gpsIfdOffset, littleEndian);
        int entryBase = tiffStart + gpsIfdOffset + 2;
        for (int i = 0; i < entries; i++) {
            int entryOffset = entryBase + (12 * i);
            int tag = readShort(segment, entryOffset, littleEndian);
            int type = readShort(segment, entryOffset + 2, littleEndian);
            long count = readInt(segment, entryOffset + 4, littleEndian) & 0xFFFFFFFFL;
            int valueOffset = readInt(segment, entryOffset + 8, littleEndian);

            switch (tag) {
                case 0x0001:
                    latRef = readAsciiValue(segment, tiffStart, entryOffset, type, count, valueOffset, littleEndian);
                    break;
                case 0x0002:
                    lat = readRationalTriplet(segment, tiffStart, valueOffset, littleEndian);
                    break;
                case 0x0003:
                    lonRef = readAsciiValue(segment, tiffStart, entryOffset, type, count, valueOffset, littleEndian);
                    break;
                case 0x0004:
                    lon = readRationalTriplet(segment, tiffStart, valueOffset, littleEndian);
                    break;
                default:
                    break;
            }
        }

        if (lat == null || lon == null) {
            return null;
        }

        double latitude = toDecimalDegrees(lat);
        double longitude = toDecimalDegrees(lon);
        if (latRef != null && "S".equalsIgnoreCase(latRef.trim())) {
            latitude = -latitude;
        }
        if (lonRef != null && "W".equalsIgnoreCase(lonRef.trim())) {
            longitude = -longitude;
        }
        return new double[]{latitude, longitude};
    }

    private static int findGpsIfdOffset(byte[] segment, int tiffStart, int ifd0Offset, boolean littleEndian) {
        int ifd0 = tiffStart + ifd0Offset;
        int entries = readShort(segment, ifd0, littleEndian);
        int entryBase = ifd0 + 2;
        for (int i = 0; i < entries; i++) {
            int entryOffset = entryBase + (12 * i);
            int tag = readShort(segment, entryOffset, littleEndian);
            if (tag == 0x8825) {
                return readInt(segment, entryOffset + 8, littleEndian);
            }
        }
        return -1;
    }

    private static String readAsciiValue(byte[] segment, int tiffStart, int entryOffset, int type, long count, int valueOffset, boolean littleEndian) {
        if (type != 2 || count <= 0) {
            return null;
        }
        byte[] value = new byte[(int) count];
        if (count <= 4) {
            int offset = entryOffset + 8;
            for (int i = 0; i < count; i++) {
                value[i] = segment[offset + i];
            }
        } else {
            int offset = tiffStart + valueOffset;
            for (int i = 0; i < count; i++) {
                value[i] = segment[offset + i];
            }
        }
        return new String(value).trim();
    }

    private static double[] readRationalTriplet(byte[] segment, int tiffStart, int valueOffset, boolean littleEndian) {
        int offset = tiffStart + valueOffset;
        return new double[]{
            readRational(segment, offset, littleEndian),
            readRational(segment, offset + 8, littleEndian),
            readRational(segment, offset + 16, littleEndian)
        };
    }

    private static double readRational(byte[] segment, int offset, boolean littleEndian) {
        long numerator = readInt(segment, offset, littleEndian) & 0xFFFFFFFFL;
        long denominator = readInt(segment, offset + 4, littleEndian) & 0xFFFFFFFFL;
        if (denominator == 0) {
            return 0D;
        }
        return (double) numerator / (double) denominator;
    }

    private static double toDecimalDegrees(double[] dms) {
        return dms[0] + (dms[1] / 60D) + (dms[2] / 3600D);
    }

    private static int readShort(byte[] data, int offset, boolean littleEndian) {
        if (littleEndian) {
            return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
        }
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }

    private static int readInt(byte[] data, int offset, boolean littleEndian) {
        if (littleEndian) {
            return (data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8)
                | ((data[offset + 2] & 0xFF) << 16)
                | ((data[offset + 3] & 0xFF) << 24);
        }
        return ((data[offset] & 0xFF) << 24)
            | ((data[offset + 1] & 0xFF) << 16)
            | ((data[offset + 2] & 0xFF) << 8)
            | (data[offset + 3] & 0xFF);
    }
}
