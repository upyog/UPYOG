package org.egov.finance.master.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.egov.finance.master.customannotation.SafeHtml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Voucher {

	private Long id;

	@NotBlank
	@SafeHtml
	private String name;

	@NotBlank
	@SafeHtml
	private String type;

	@SafeHtml
	private String voucherNumber;

	@SafeHtml
	private String description;

	@SafeHtml
	private String voucherDate;

	private Fund fund;

	private Function function;

	private FiscalPeriod fiscalPeriod;

	private EgwStatus status;

	private Long originalVhId;

	private Long refVhId;

	@SafeHtml
	private String cgvn;

	private Long moduleId;

	@SafeHtml
	private String department;

	@SafeHtml
	private String source;

	private Scheme scheme;

	private SubScheme subScheme;

	private Functionary functionary;

	private Fundsource fundsource;

	private List<AccountDetail> ledgers = new ArrayList<>();

	@SafeHtml
	private String tenantId;

	@SafeHtml
	private String serviceName;

	@SafeHtml
	private String referenceDocument;

	public Voucher(final CVoucherHeader vh) {
		final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		this.id = vh.getId();
		this.name = vh.getName();
		this.type = vh.getType();
		this.voucherNumber = vh.getVoucherNumber();
		this.description = vh.getDescription();
		this.voucherDate = vh.getVoucherDate() != null ? sdf.format(vh.getVoucherDate()) : null;

		this.fund = vh.getFundId() != null ? new Fund(vh.getFundId().getId()) : null;

		this.department = vh.getVouchermis() != null ? vh.getVouchermis().getDepartmentcode() : null;
		this.function = vh.getVouchermis() != null && vh.getVouchermis().getFunction() != null
				? new Function(vh.getVouchermis().getFunction())
				: null;

		this.status = vh.getStatus() != null ? new EgwStatus(vh.getStatus()) : null;
		this.moduleId = vh.getModuleId() != null ? vh.getModuleId().longValue() : null;
		this.cgvn = vh.getCgvn();
		this.serviceName = vh.getVouchermis() != null ? vh.getVouchermis().getServiceName() : null;
		this.referenceDocument = vh.getVouchermis() != null ? vh.getVouchermis().getReferenceDocument() : null;

		if (vh.getGeneralLedger() != null) {
			for (CGeneralLedger gl : vh.getGeneralLedger()) {
				this.ledgers.add(new AccountDetail(gl));
			}
		}
	}

}