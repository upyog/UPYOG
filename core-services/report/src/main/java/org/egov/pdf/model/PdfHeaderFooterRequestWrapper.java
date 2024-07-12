package org.egov.pdf.model;

import java.util.List;

import org.egov.pdf.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class PdfHeaderFooterRequestWrapper {

	private RequestInfo RequestInfo;

	private List<PdfHeaderFooter> pdfHeaderFooters;
}
