package org.egov.pdf.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.egov.pdf.model.HtmlContentResponse;
import org.egov.pdf.model.PDFRequest;
import org.egov.pdf.repo.PDFServiceRepo;
import org.egov.pdf.request.RequestInfo;
import org.egov.pdf.utils.ReportUtils;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import lombok.extern.slf4j.Slf4j;
import net.glxn.qrgen.javase.QRCode;

@Slf4j
@Service
public class PDFService {

	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	public ResourceLoader resourceLoader;

	@Value("${hpudd.pdf.services.data.configs.path}")
	private String mdmsFileDirectory;

	@Value("${spring.thymeleaf.prefix}")
	private String location;

	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String searchEndPoint;

	ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private PDFServiceRepo pdfServiceRepo;

	@Autowired
	private RestTemplate restTemplate;

	public ResponseEntity<Resource> generatePdf(PDFRequest pdfRequest) {

		String template = null;

		if (StringUtils.isNotEmpty(pdfRequest.getHtmlTemplateContent())) {
			// Replace all special characters in the HTML template content.
			template = ReportUtils.htmlContentCleanUp(pdfRequest.getHtmlTemplateContent());
			template = ReportUtils.addMissingClosingTags(template);
		} else {
			Map dataConfigs = featchDataConfig(pdfRequest.getKey());
			Map<String, Object> pdfContextData = new HashMap<>();
			parseData(pdfRequest, dataConfigs, pdfContextData);
			Context context = new Context();
			context.setVariables(pdfContextData);
			context.setVariable("util", new PDFUtill());
			template = getTemplate(pdfRequest.getKey(), context);
		}

		ByteArrayOutputStream outputStream = generateHtmlToPdf(pdfRequest.getKey(), template);
		return prepareResponce(pdfRequest, outputStream);
	}

	private ResponseEntity<Resource> prepareResponce(PDFRequest pdfRequest, ByteArrayOutputStream outputStream) {
		String fileName = pdfRequest.getKey() + "-" + UUID.randomUUID().toString() + ".pdf";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());

		ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

		return ResponseEntity.ok().headers(headers).body(resource);
	}

	private void parseData(PDFRequest pdfRequest, Map dataConfigs, Map<String, Object> pdfContextData) {
		parseDirect(pdfRequest, dataConfigs, pdfContextData);
		parseDataBase(pdfRequest, dataConfigs, pdfContextData);
		addLogoContext(pdfRequest, pdfContextData);
	}

	private void addLogoContext(PDFRequest pdfRequest, Map<String, Object> pdfContextData) {
		RequestInfo requestInfo = pdfRequest.getRequestInfo();
		String logoBase64 = "data:image/png;base64," + getLogoFromMdms(requestInfo);
		pdfContextData.put("logo", logoBase64);
	}

	private String getLogoFromMdms(RequestInfo requestInfo) {
		log.info("Inside method getLogoFromMdms");
		String apiUrl = mdmsHost + searchEndPoint;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String authToken = "0648993a-dfd7-4259-90c8-57243fc84061";

		String requestBody = "{\n" + "    \"RequestInfo\": {\n" + "        \"authToken\": \"" + authToken + "\"\n"
				+ "    },\n" + "    \"MdmsCriteria\": {\n" + "        \"tenantId\": \"hp\",\n"
				+ "        \"moduleDetails\": [\n" + "            {\n"
				+ "                \"moduleName\": \"hpudd-logo\",\n" + "                \"masterDetails\": [\n"
				+ "                    {\n" + "                        \"name\": \"logo\",\n"
				+ "                        \"filter\": \"[?(@.code == \\\"v1-logo\\\")]\"\n" + "                    }\n"
				+ "                ]\n" + "            }\n" + "        ]\n" + "    }\n" + "}";

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity,
					String.class);
			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				String responseData = extractDataFromResponse(responseEntity.getBody());
				log.info("logo fetched successfully.");
				return responseData;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String extractDataFromResponse(String jsonResponse) {
		log.info("Inside method extractDataFromResponse");
		try {
			JsonNode jsonNode = objectMapper.readTree(jsonResponse);
			JsonNode dataNode = jsonNode.path("MdmsRes").path("hpudd-logo").path("logo").get(0).path("data");
			return dataNode.asText();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void parseDataBase(PDFRequest pdfRequest, Map dataConfigs, Map<String, Object> pdfContextData) {
		String baseKayPath = JsonPath.read(dataConfigs, "$.DataConfigs.baseKeyPath");
		List<Object> dataBaseList = JsonPath.read(dataConfigs, "$.DataConfigs.mappings.*.dataBase.*");
		Object requestData = JsonPath.read(pdfRequest.getData(), baseKayPath);
		for (Object object : dataBaseList) {
			String query = JsonPath.read(object, "$.query");
			String queryKey = JsonPath.read(object, "$.key");
			Map<String, Object> parameters = new HashMap<>();
			List<Object> queryParamList = JsonPath.read(object, "$.queryParam.*");

			log.info("queryParam prepartion started. for queryKey " + queryKey + " query " + query);
			for (Object queryParam : queryParamList) {
				String key = JsonPath.read(queryParam, "$.key");
				String valuePath = JsonPath.read(queryParam, "$.value.path");
				String valueType = null;
				Object value = JsonPath.read(requestData, valuePath);
				try {
					valueType = JsonPath.read(queryParam, "$.type");
				} catch (PathNotFoundException e) {
				}

				if ("date".equals(valueType)) {
					value = parseDate((Long) value);
				}

				log.info(key + " " + valuePath + " " + valueType + " " + value);
				parameters.put(key, value);
			}
			log.info("queryParam prepartion completed. " + parameters);

			HashMap<String, String> responseMapping = new HashMap<>();
			log.info("Prepare responce mapping map");
			List<Object> responseMappingList = JsonPath.read(object, "$.responseMapping.*");
			for (Object rm : responseMappingList) {
				String variable = JsonPath.read(rm, "$.variable");
				String columnName = JsonPath.read(rm, "$.columnName");
				responseMapping.put(variable, columnName);
			}

			log.info("query excution start...");
			Map<String, Object> contextSqlMap = pdfServiceRepo.excutePdfServiceQuery(queryKey, query, parameters,
					responseMapping);
			log.info("contextSqlMap " + contextSqlMap);
			pdfContextData.putAll(contextSqlMap);
			log.info("query excution end...");

		}

	}

	private void parseDirect(PDFRequest pdfRequest, Map dataConfigs, Map<String, Object> pdfContextData) {

		String baseKayPath = JsonPath.read(dataConfigs, "$.DataConfigs.baseKeyPath");
		List<Object> directList = JsonPath.read(dataConfigs, "$.DataConfigs.mappings.*.direct.*");

		Object requestData = JsonPath.read(pdfRequest.getData(), baseKayPath);

		for (Object object : directList) {

			String variable = JsonPath.read(object, "$.variable");
			String valuePath = JsonPath.read(object, "$.value.path");
			String valueType = null;
			Object value = JsonPath.read(requestData, valuePath);

			try {
				valueType = JsonPath.read(object, "$.type");
			} catch (PathNotFoundException e) {
			}

			if ("date".equals(valueType)) {
				value = parseDate(Long.valueOf(value.toString()));
			}

			if ("QRCODE".equals(valueType)) {
				value = generateQRCodeImage(value.toString());
			}
			pdfContextData.put(variable, value);
		}
	}

	private Object parseDate(Long value) {
		Date date = new Date(value);
		Locale locale = new Locale("en", "IN");
		DateFormat dateFormat = new SimpleDateFormat("DD/MM/YYYY", locale);
//		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat., locale);
		String datestr = dateFormat.format(date);
		return datestr;
	}

	private Object parseSqlDate(Long value) {
		return new java.sql.Date(value); // new Date(value);
	}

	private Map featchDataConfig(String key) {
		String basefilePath = mdmsFileDirectory + key + ".json";
		try {
			File file = resourceLoader.getResource(basefilePath).getFile();

			Map<String, Object> jsonMap = objectMapper.readValue(file, new TypeReference<Map<String, Object>>() {
				@Override
				public Type getType() {
					return super.getType();
				}
			});
			return jsonMap;

		} catch (Exception e) {
			log.error("Error while featching data configs");
			throw new CustomException(e.getMessage(), e.getMessage());
		}
	}

	private String getTemplate(String key, Context context) {
		return templateEngine.process(key, context);
	}

	private ByteArrayOutputStream generateHtmlToPdf(String key, String html) {
		try {
			String fileName = key + "-" + UUID.randomUUID().toString() + ".pdf";
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(html);
			renderer.layout();
			renderer.createPDF(outputStream);
			return outputStream;
		} catch (Exception e) {
			log.error("Error while gernating the pdf " + e.getMessage());
			throw new CustomException(e.getMessage(), e.getMessage());
		}
	}

	public String generateQRCodeImage(String barcodeText) {
		try {
			
			ByteArrayOutputStream stream = null;
			
		    // Adjust size dynamically based on the length of the barcodeText
		    int size = 250 + (barcodeText.length() * 10); // base = 250
		    
		    if(size > ReportUtils.PDF_BARCODE_MAX_SIZE) {
		    	barcodeText = "MAX size for barcode breached.";
		    	// generate barcode with red color as warning
		    	stream = generateRedQRCode(barcodeText, 9000, 9000);
		    }else {
		    	stream = QRCode.from(barcodeText).withSize(size, size).stream();
		    }
		    
			
			ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			StringBuffer qr = new StringBuffer("data:image/png;base64,");

			ImageIO.write(ImageIO.read(bis), "png", os);
			qr.append(Base64.getEncoder().encodeToString(os.toByteArray()));
			return qr.toString();
		} catch (final IOException ioe) {
			throw new CustomException("error while genrating qrcode", ioe.getMessage());
		}

	}

	private ByteArrayOutputStream generateRedQRCode(String text, int width, int height) throws IOException {
        // Generate the QR code normally (in black and white)
        ByteArrayOutputStream stream = QRCode.from(text).withSize(width, height).stream();

        // Convert the QR code to a BufferedImage
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(stream.toByteArray()));

        // Create a new BufferedImage for the red-colored QR code
        BufferedImage redImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Replace black pixels with red in the new image
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);
                if (pixel == Color.BLACK.getRGB()) {
                    redImage.setRGB(x, y, Color.RED.getRGB()); // Set red color
                } else {
                    redImage.setRGB(x, y, pixel); // Keep the white/transparent pixels unchanged
                }
            }
        }

        // Write the red-colored QR code back to ByteArrayOutputStream
        ByteArrayOutputStream redStream = new ByteArrayOutputStream();
        ImageIO.write(redImage, "png", redStream);

        return redStream;
    }

	public HtmlContentResponse generateConvertedHtmlContent(PDFRequest pdfRequest) throws IOException {

		File tempHtmlFile = null;

		String htmlContent = ReportUtils.htmlContentCleanUp(pdfRequest.getHtmlTemplateContent());
		try {

			String fileName = UUID.randomUUID().toString();
			tempHtmlFile = createTempHtmlFile(fileName, htmlContent);

			Context context = new Context();
			context.setVariables(pdfRequest.getData());

			htmlContent = templateEngine.process(tempHtmlFile.getName(), context);

			return HtmlContentResponse.builder().htmlContent(htmlContent).build();
		} catch (Exception e) {
			log.error("Error while gernating the html content " + e.getMessage());
			throw new CustomException("ERR_REPORT_SERVICE",
					"Error while gernating the html content. Message: " + e.getMessage());
		} finally {
			if (tempHtmlFile != null) {
				deleteTempFile(tempHtmlFile);
			}
		}
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

	private File createTempHtmlFile(String templateName, String htmlontent) throws IOException {
		File templateDir = ResourceUtils.getFile(location);

		// Create a temporary HTML file in the template directory
		File tempHtmlFile = new File(templateDir, templateName + ".html");
		try (FileWriter writer = new FileWriter(tempHtmlFile)) {

			writer.write(htmlontent);
		}

		return tempHtmlFile;
	}

}