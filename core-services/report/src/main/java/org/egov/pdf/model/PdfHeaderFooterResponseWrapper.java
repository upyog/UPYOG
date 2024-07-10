package org.egov.pdf.model;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

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
public class PdfHeaderFooterResponseWrapper {

	private ResponseInfo ResponseInfo;

	private List<PdfHeaderFooter> pdfHeaderFooters;
}
