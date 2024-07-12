package org.egov.pdf.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.egov.pdf.event.handler.ImageHeaderFooterEventHandler;
import org.egov.pdf.event.handler.TextHeaderFooterEventHandler;
import org.egov.pdf.model.HtmlContentResponse;
import org.egov.pdf.model.PDFRequest;
import org.egov.pdf.model.PdfHeaderFooter;
import org.egov.pdf.model.PdfHeaderFooterRequestWrapper;
import org.egov.pdf.model.PdfHeaderFooterResponseWrapper;
import org.egov.pdf.utils.ReportUtils;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfV2Service {

	private static final String P_END_TAG = "</span>";
	private static final String GENERATED_CODE_START_TAG = "<span style=\"display:none\"> GENERATED_CODE_START </span>";
	private static final String GENERATED_CODE_END_TAG = "<span style=\"display:none\"> GENERATED_CODE_END </span>";

	private static final String GENERATED_CODE_TABLE_START_TAG = "<tr style=\"display:none\"> <td style=\"display:none\"> GENERATED_CODE_START </td> </tr>";
	private static final String GENERATED_CODE_TABLE_END_TAG = "<tr style=\"display:none\"> <td style=\"display:none\"> GENERATED_CODE_END </td> </tr>";

	private static final String TABLE_ROW = "TABLE_ROW";
	private static final String CUSTOM_CONDITION = "CUSTOM_CONDITION";
	private static final String LOOP_DATA = "LOOP_DATA";
	private static final String NORMAL_REPLACEMENT = "NORMAL_REPLACEMENT";

	private static final String TAG_PREFIX = "<tr style=\"display:none\"> <td style=\"display:none\"> ";
	private static final String TAG_SUFFIX = " </td> </tr>";

	private static final String LOOP_DATA_TAG_PREFIX = "<div style=\"display:none;\">";
	private static final String LOOP_DATA_TAG_SUFFIX = "</div>";

	@Value("${spring.thymeleaf.prefix}")
	private String location;

	@Value("${configs.basePath:null}")
	private String configBasePath;

	@Value("${hpudd.pdf.services.karla-font.path}")
	private String karlaFontPath;

	@Autowired
	private ImageHeaderFooterEventHandler imageHeaderFooterEventHandler;

	@SuppressWarnings("unchecked")
	public HtmlContentResponse htmlPlaceholderReplacement(PDFRequest pdfRequest) {

		// Clean up the HTML template content from the PDF request
		String htmlContent = (ReportUtils.htmlContentCleanUp(pdfRequest.getHtmlTemplateContent()))
				.replaceAll("\\s+", " ").trim();

		// Remove any previously generated codes from the HTML content
		htmlContent = removeGeneratedCodes(htmlContent, GENERATED_CODE_START_TAG, GENERATED_CODE_END_TAG);
		htmlContent = removeGeneratedCodes(htmlContent, GENERATED_CODE_TABLE_START_TAG, GENERATED_CODE_TABLE_END_TAG);

		Map data = pdfRequest.getData();

		if (null != data && !data.isEmpty()) {

			// Extract the list of table row data from the data map
			List<LinkedHashMap<String, Object>> tableRowMap = (List<LinkedHashMap<String, Object>>) data.get(TABLE_ROW);

			// Extract the list of custom condition data from the data map
			List<LinkedHashMap<String, Object>> customConditionMap = (List<LinkedHashMap<String, Object>>) data
					.get(CUSTOM_CONDITION);

			// Extract the normal replacement data from the data map
			LinkedHashMap<String, String> normalReplacementMap = (LinkedHashMap<String, String>) data
					.get(NORMAL_REPLACEMENT);

			// Extract the list of loop data from the data map
			List<LinkedHashMap<String, Object>> loopDataMap = (List<LinkedHashMap<String, Object>>) data.get(LOOP_DATA);

			// Replace the loop data in the HTML content
			htmlContent = replaceLoopData(loopDataMap, htmlContent);

			// Replace the table row data in the HTML content
			htmlContent = replaceTableRowData(tableRowMap, htmlContent);

			// Replace the custom condition data in the HTML content
			htmlContent = replaceCustomConditionData(customConditionMap, htmlContent);

			// Replace the normal placeholder data in the HTML content
			htmlContent = replaceNormalReplacementData(normalReplacementMap, htmlContent);

		}

		return HtmlContentResponse.builder().htmlContent(htmlContent).build();
	}

	@SuppressWarnings("unchecked")
	private String replaceLoopData(List<LinkedHashMap<String, Object>> loopDataMap, String htmlContent) {
		if (CollectionUtils.isEmpty(loopDataMap)) {
			return htmlContent;
		}

		// Iterate through each loop data map in the list
		for (Map<String, Object> loopDataRowMap : loopDataMap) {
			String tag = (String) loopDataRowMap.get("tag");
			String endOfLoopValue = (String) loopDataRowMap.get("endOfLoopValue");

			List<LinkedHashMap<String, Object>> values = (List<LinkedHashMap<String, Object>>) loopDataRowMap
					.get("values");

			htmlContent = replaceLoopDataContent(htmlContent,
					LOOP_DATA_TAG_PREFIX + tag + "_START" + LOOP_DATA_TAG_SUFFIX,
					LOOP_DATA_TAG_PREFIX + tag + "_END" + LOOP_DATA_TAG_SUFFIX, values, endOfLoopValue);
		}

		return htmlContent;
	}

	private static String replaceLoopDataContent(String htmlContent, String startTag, String endTag,
			List<LinkedHashMap<String, Object>> dataMapList, String endOfLoopValue) {
		// Find the position of the start and end tags
		int startIndex = htmlContent.indexOf(startTag);
		int endIndex = htmlContent.indexOf(endTag);

		if (startIndex != -1 && endIndex != -1) {
			// Extract the content between the start and end tags
			String loopDataContent = htmlContent.substring(startIndex + startTag.length(), endIndex);
			loopDataContent = loopDataContent.replace("display:none;", "");
			StringBuilder finalData = new StringBuilder();

			int index = 0;
			for (LinkedHashMap<String, Object> dataMap : dataMapList) {
				String dataToCopyNew = loopDataContent;
				for (Entry<String, Object> entrySetMap : dataMap.entrySet()) {
					dataToCopyNew = dataToCopyNew.replace(entrySetMap.getKey(), entrySetMap.getValue().toString());
				}
				finalData.append(dataToCopyNew);

				if (StringUtils.isNotBlank(endOfLoopValue) && index < dataMapList.size() - 1) {
					finalData.append(endOfLoopValue);
				}

				index++;
			}

			// Append the extracted content after the end tag
			StringBuilder modifiedHtmlContent = new StringBuilder();
			modifiedHtmlContent.append(htmlContent, 0, endIndex + endTag.length());

			modifiedHtmlContent.append(GENERATED_CODE_START_TAG);
			modifiedHtmlContent.append(finalData);
			modifiedHtmlContent.append(GENERATED_CODE_END_TAG);

			modifiedHtmlContent.append(htmlContent.substring(endIndex + endTag.length()));

			return modifiedHtmlContent.toString();
		} else {
			return htmlContent;
		}
	}

	@SuppressWarnings("unchecked")
	private String replaceTableRowData(List<LinkedHashMap<String, Object>> tableRowList, String htmlContent) {
		// Check if the tableRowList is empty or null, and return the original
		// htmlContent if it is
		if (CollectionUtils.isEmpty(tableRowList)) {
			return htmlContent;
		}

		// Iterate through each table row map in the list
		for (LinkedHashMap<String, Object> tableRowMap : tableRowList) {
			// Get the tag associated with the current table row
			String tag = (String) tableRowMap.get("tag");

			// Get the values to replace within the tag
			Map<String, List<String>> valuesToReplace = (Map<String, List<String>>) tableRowMap.get("values");

			// If valuesToReplace is null or empty, skip to the next iteration
			if (valuesToReplace == null || valuesToReplace.isEmpty()) {
				continue;
			}

			// Get the number of occurrences to replace, assumed to be the size of any list
			// in valuesToReplace
			int occurrence = valuesToReplace.values().iterator().next().size();

			// Populate the HTML content with the replaced table row data
			htmlContent = populateTableRowReplacedData(htmlContent, TAG_PREFIX + tag + "_START" + TAG_SUFFIX,
					TAG_PREFIX + tag + "_END" + TAG_SUFFIX, valuesToReplace, occurrence);
		}

		return htmlContent;
	}

	private String replaceNormalReplacementData(LinkedHashMap<String, String> normalReplacementDataMap,
			String htmlContent) {
		// Check if the normalReplacementDataMap is not empty
		if (!CollectionUtils.isEmpty(normalReplacementDataMap)) {

			// Use StringBuilder for efficient string manipulation
			StringBuilder sb = new StringBuilder(htmlContent);

			// Iterate through each entry in the normalReplacementDataMap
			for (Map.Entry<String, String> entry : normalReplacementDataMap.entrySet()) {
				String key = entry.getKey(); // Placeholder to be replaced
				String value = entry.getValue(); // Value to replace the placeholder

				// Find the index of the key in the htmlContent
				int index = sb.indexOf(key);

				// Replace all occurrences of the key with the value
				while (index != -1) {
					sb.replace(index, index + key.length(), value);
					index = sb.indexOf(key, index + value.length()); // Find the next occurrence
				}
			}

			// Convert StringBuilder back to String
			htmlContent = sb.toString();
		}
		return htmlContent;
	}

	private String replaceCustomConditionData(List<LinkedHashMap<String, Object>> customConditionList,
			String htmlContent) {
		// Check if the customConditionList is empty or null, and return the original
		// htmlContent if it is
		if (CollectionUtils.isEmpty(customConditionList)) {
			return htmlContent;
		}

		// Iterate through each custom condition map in the list
		for (LinkedHashMap<String, Object> customConditionMap : customConditionList) {
			// Get the tag associated with the current custom condition
			String tag = (String) customConditionMap.get("tag");
			// Get the condition to check if the replacement should be done
			Boolean condition = (Boolean) customConditionMap.get("condition");
			// Get the value to replace within the tag if the condition is true
			String value = (String) customConditionMap.get("value");

			// If the condition is true, and both tag and value are not empty, proceed with
			// replacement
			if (Boolean.TRUE.equals(condition) && StringUtils.isNotEmpty(tag) && StringUtils.isNotEmpty(value)) {
				// Populate the HTML content with the condition-based replaced data

				htmlContent = populateConditionBasedReplacedData(tag, htmlContent, value);
			}
		}

		return htmlContent;
	}

	private static String populateConditionBasedReplacedData(String targetTag, String inputHtml,
			String valueToReplace) {

		// Find the index of the start tag
		int targetIndex = inputHtml.indexOf(targetTag);
		if (targetIndex == -1) {

			// Start tag not found, return original HTML
			return inputHtml;
		}

		// Find the index of the SPAN_END_TAG after the end tag
		int spanIndex = inputHtml.indexOf(P_END_TAG, targetIndex);
		if (spanIndex != -1) {

			// Found SPAN_END_TAG after endIndex, indicating the position to insert
			// additional content

			// Extract the part of HTML before and after the SPAN_END_TAG
			String beforeSpan = inputHtml.substring(0, spanIndex + P_END_TAG.length());
			String afterSpan = inputHtml.substring(spanIndex + P_END_TAG.length());

			// Concatenate the additional content with the extracted HTML
			String additionalContent = GENERATED_CODE_START_TAG + valueToReplace + GENERATED_CODE_END_TAG;
			inputHtml = beforeSpan + additionalContent + afterSpan;
		}

		return inputHtml;
	}

	private static String removeGeneratedCodes(String htmlContent, String startTag, String endTag) {
		while (htmlContent.contains(startTag)) {
			int startIndex = htmlContent.indexOf(startTag);
			int endIndex = htmlContent.indexOf(endTag, startIndex + startTag.length());

			if (startIndex != -1 && endIndex != -1) {
				// Get the substring before startTag
				String before = htmlContent.substring(0, startIndex);

				// Get the substring after endTag
				String after = htmlContent.substring(endIndex + endTag.length());

				// Concatenate the strings excluding the content between startTag and endTag
				htmlContent = before + after;
			} else {
				break; // Break loop if tags are not found
			}
		}

		return htmlContent;
	}

	private static String populateTableRowReplacedData(String html, String startTag, String endTag,
			Map<String, List<String>> valuesToReplace, int occurrence) {

		int startIndex = html.indexOf(startTag);
		if (startIndex == -1) {
			// Start tag not found, return original HTML
			return html;
		}

		int endIndex = html.indexOf(endTag, startIndex);
		if (endIndex == -1) {
			// End tag not found after the start tag, return original HTML
			return html;
		}

		// Extract the data between the start and end tags
		String dataToCopy = html.substring(startIndex + startTag.length(), endIndex);
		dataToCopy = dataToCopy.replace("style=\"display:none\"", "");

		StringBuilder dataToCopyBuilder = new StringBuilder(GENERATED_CODE_TABLE_START_TAG);

		if (!CollectionUtils.isEmpty(valuesToReplace)) {
			for (int counter = 0; counter < occurrence; counter++) {
				String dataToCopyNew = dataToCopy;
				for (Map.Entry<String, List<String>> entry : valuesToReplace.entrySet()) {
					dataToCopyNew = dataToCopyNew.replace(entry.getKey(), entry.getValue().get(counter));
				}
				dataToCopyBuilder.append(dataToCopyNew);
			}
		}

		dataToCopyBuilder.append(GENERATED_CODE_TABLE_END_TAG);

		// Insert the copied data after the closing endTag
		StringBuilder modifiedHtml = new StringBuilder(html);
		modifiedHtml.insert(endIndex + endTag.length(), dataToCopyBuilder.toString());

		return modifiedHtml.toString();
	}

	public ResponseEntity<Resource> generatePdf(PDFRequest pdfRequest) throws IOException {
		String htmlTemplateContent = ReportUtils.htmlContentCleanUp(pdfRequest.getHtmlTemplateContent());
		htmlTemplateContent = ReportUtils.addMissingClosingTags(htmlTemplateContent);

		File tempFile = createTempFile(UUID.randomUUID().toString() + ".pdf", "");
		File generatedPdfFileWithoutHeaderFooter = createTempFile(UUID.randomUUID().toString() + ".pdf", "");

		try {

			ByteArrayOutputStream byteArrayOutputStream = generateHtmlToPdf("", htmlTemplateContent);

			byte[] byteArray = byteArrayOutputStream.toByteArray();

			try (FileOutputStream fileOutputStream = new FileOutputStream(generatedPdfFileWithoutHeaderFooter)) {
				fileOutputStream.write(byteArray);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Add header and footer to the generated PDF
			addHeaderAndFooter(generatedPdfFileWithoutHeaderFooter.getPath(), tempFile.getPath(),
					pdfRequest.getIsHeaderFooterSkip());

			// Convert the tempFile to ByteArrayOutputStream
			byte[] pdfBytes = convertFileToByteArray(tempFile);

			// Prepare ResponseEntity with the PDF byte array
			ByteArrayResource byteArrayResource = new ByteArrayResource(pdfBytes);
			return prepareResponse(byteArrayResource);

		} catch (Exception e) {
			log.error("PDF Generation failed due to some technical issue: ", e);
			throw new CustomException("ERR_REPORT_SERVICE",
					"PDF Generation failed due to some technical issue. Message: " + e.getMessage());
		} finally {
			// Clean up temporary files
			deleteTempFile(tempFile);
			deleteTempFile(generatedPdfFileWithoutHeaderFooter);
		}
	}

	private ByteArrayOutputStream generateHtmlToPdf(String key, String html) {
		try {

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ITextRenderer renderer = new ITextRenderer();

			// Define the @font-face CSS for the Karla font
			String fontFaceCss = "@font-face {" + "font-family: 'Karla';" + "src: url('" + configBasePath
					+ karlaFontPath + "') format('truetype');" + "} " + "body { font-family: 'Karla', sans-serif; }";

			// Append CSS for page margins to existing CSS within the STYLE tag
			String additionalCss = "@page { margin-top: 1in; margin-bottom: 1in; margin-left: 0.2in; margin-right: 0.2in; }  table, tr, td { page-break-inside: avoid; }";
			String modifiedHtml = appendCssToStyleTag(html, fontFaceCss + additionalCss);

			renderer.setDocumentFromString(modifiedHtml);
			renderer.layout();
			renderer.createPDF(outputStream);
			return outputStream;
		} catch (Exception e) {
			log.error("Error while gernating the pdf " + e.getMessage());
			throw new CustomException(e.getMessage(), e.getMessage());
		}
	}

	private String appendCssToStyleTag(String html, String additionalCss) {
		int styleTagIndex = html.indexOf("</style>");
		if (styleTagIndex != -1) {
			return html.substring(0, styleTagIndex) + additionalCss + html.substring(styleTagIndex);
		} else {
			int headTagIndex = html.indexOf("</head>");
			if (headTagIndex != -1) {
				return html.substring(0, headTagIndex) + "<style>" + additionalCss + "</style>"
						+ html.substring(headTagIndex);
			} else {
				int htmlTagIndex = html.indexOf("<html>");
				if (htmlTagIndex != -1) {
					return html.substring(0, htmlTagIndex + 6) + "<head><style>" + additionalCss + "</style></head>"
							+ html.substring(htmlTagIndex + 6);
				} else {
					return "<html><head><style>" + additionalCss + "</style></head>" + html;
				}
			}
		}
	}

	private byte[] convertFileToByteArray(File file) throws IOException {
		try (InputStream inputStream = new FileInputStream(file);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			IOUtils.copy(inputStream, byteArrayOutputStream);
			return byteArrayOutputStream.toByteArray();
		}
	}

	private ResponseEntity<Resource> prepareResponse(ByteArrayResource byteArrayResource) {
		String fileName = UUID.randomUUID().toString() + ".pdf";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());

		return ResponseEntity.ok().headers(headers).body(byteArrayResource);
	}

	private void addHeaderAndFooter(String src, String dest, Boolean skip) throws FileNotFoundException, IOException {
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
		Document document = new Document(pdfDoc);
		document.setMargins(50, 36, 50, 36);

		if (!skip) {
			// Set event handler for header and footer
			pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, imageHeaderFooterEventHandler);
		}

		document.close();

	}

	private File createTempFile(String fileName, String content) throws IOException {
		File templateDir = ResourceUtils.getFile(location);

		File tempFile = new File(templateDir, fileName);
		try (FileWriter writer = new FileWriter(tempFile)) {

			writer.write(content);
		}

		return tempFile;
	}

	private void deleteTempFile(File tempFile) {
		if (tempFile != null && tempFile.exists()) {
			if (tempFile.delete()) {
				log.info("Deleted temporary file: " + tempFile.getAbsolutePath());
			} else {
				log.warn("Failed to delete temporary file: " + tempFile.getAbsolutePath());
			}
		}
	}

	public PdfHeaderFooterResponseWrapper addHeaderFooter(PdfHeaderFooterRequestWrapper pdfHeaderFooterRequest)
			throws IOException {
		List<PdfHeaderFooter> pdfHeaderFootersResponse = new ArrayList<>();

		for (PdfHeaderFooter pdfHeaderFooter : pdfHeaderFooterRequest.getPdfHeaderFooters()) {

			Resource resource = null;
//			Resource resource = fileStoreService.fetchById(pdfHeaderFooter.getFileStoreId());

			File destinationFile = createTempFile(UUID.randomUUID().toString() + ".pdf", "");

			try {
				generateHeaderFooter(resource.getInputStream(), destinationFile.getAbsolutePath(),
						pdfHeaderFooter.getHeaderText(), pdfHeaderFooter.getFooterText());

				String newFileStoreId = null; 
//				fileStoreService.convertAndUploadFile(UUID.randomUUID().toString(),
//						new FileSystemResource(destinationFile), pdfHeaderFooterRequest.getRequestInfo());

				pdfHeaderFootersResponse.add(PdfHeaderFooter.builder().fileStoreId(newFileStoreId).build());

			} catch (IOException ex) {
				ex.printStackTrace();
				log.error("Error occured while adding header and footer.", ex);
				throw new CustomException("ERR_REPORT_SERVICE",
						"Error occured while adding header and footer. Message: " + ex.getMessage());
			} finally {
				// Clean up temporary files
				deleteTempFile(destinationFile);
			}
		}

		PdfHeaderFooterResponseWrapper response = new PdfHeaderFooterResponseWrapper();

		response.setPdfHeaderFooters(pdfHeaderFootersResponse);

		return response;
	}

	private void generateHeaderFooter(InputStream inputStream, String destinationFileName, String header, String footer)
			throws IOException {

		PdfReader reader = new PdfReader(inputStream);
		PdfWriter writer = new PdfWriter(destinationFileName);
		PdfDocument pdfDoc = new PdfDocument(reader, writer);
		Document doc = new Document(pdfDoc);

		// Add the header and footer event handler
		TextHeaderFooterEventHandler eventHandler = new TextHeaderFooterEventHandler();
		eventHandler.setFooterText(footer);
		eventHandler.setHeaderText(header);

		pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandler);

		doc.close();
		pdfDoc.close();
	}

	public ResponseEntity<Resource> mergePdf(List<MultipartFile> files, @Valid List<String> fileStoreIds) {

		List<Resource> listOfPdf = new ArrayList<>();

		if (!CollectionUtils.isEmpty(files)) {
			files.stream().forEach(file -> {
				listOfPdf.add(multipartToResource(file));
			});
		}

//		if (!CollectionUtils.isEmpty(fileStoreIds)) {
//			fileStoreIds.stream().forEach(id -> {
//				listOfPdf.add(fetchFileById(id));
//			});
//		}

		ResponseEntity<Resource> resource = mergeListOfResourcesUsingPdfBox(listOfPdf);

		String newFileName = UUID.randomUUID().toString();

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFileName + ".pdf" + "\"")
				.header(HttpHeaders.CONTENT_TYPE, "application/pdf").body(resource.getBody());
	}

	private Resource multipartToResource(MultipartFile file) {
		try {
			return new ByteArrayResource(file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

//	private Resource fetchFileById(String id) {
//		Resource resource = null;
//		resource = fileStoreService.fetchById(id);
//		return resource;
//	}

	private ResponseEntity<Resource> mergeListOfResourcesUsingPdfBox(List<Resource> listOfPdf) {
		try (ByteArrayOutputStream mergedOutputStream = new ByteArrayOutputStream()) {
			PDFMergerUtility pdfMerger = new PDFMergerUtility();

			listOfPdf.stream().forEach(pdf -> {
				try {
					pdfMerger.addSource(pdf.getInputStream());
				} catch (IOException e) {
					log.error("Error occurred while reading file during merging files.", e);
					throw new CustomException("ERROR_IN_READING_FILES_WHILE_MERGING", e.getMessage());
				}
			});

			pdfMerger.setDestinationStream(mergedOutputStream);
			pdfMerger.mergeDocuments(null);

			ByteArrayResource mergedPdfResource = new ByteArrayResource(mergedOutputStream.toByteArray());
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=merged.pdf");
			return new ResponseEntity<>(mergedPdfResource, headers, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}