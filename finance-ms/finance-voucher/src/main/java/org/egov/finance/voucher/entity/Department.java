package org.egov.finance.voucher.entity;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.egov.finance.voucher.validation.Unique;
import org.hibernate.validator.constraints.Length;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Unique(fields = { "name", "code" }, enableDfltMsg = true)
@Table(name = "eg_department")
@Data
public class Department {

	public static final String SEQ_DEPARTMENT = "SEQ_EG_DEPARTMENT";
	private static final long serialVersionUID = 7630238192598939863L;
	@SequenceGenerator(name = SEQ_DEPARTMENT, sequenceName = SEQ_DEPARTMENT, allocationSize = 1)
	@Expose
	@Id
	@GeneratedValue(generator = SEQ_DEPARTMENT, strategy = GenerationType.SEQUENCE)
	private Long id;

	@Length(min = 1, max = 128)
	@SafeHtml
	private String name;

	@NotBlank
	@Length(min = 1, max = 128)
	@SafeHtml
	private String code;

}
