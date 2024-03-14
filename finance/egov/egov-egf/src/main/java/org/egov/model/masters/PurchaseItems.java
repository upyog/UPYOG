package org.egov.model.masters;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.EgwStatus;
import org.egov.commons.utils.EntityType;
import org.egov.infra.persistence.validator.annotation.OptionalPattern;
import org.egov.infra.persistence.validator.annotation.Unique;
import org.egov.utils.FinancialConstants;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;
import org.egov.model.masters.PurchaseOrder;

@Entity
@Table(name = "EGF_PURCHASEITEMS")
@Unique( id = "id", tableName = "EGF_PURCHASEITEMS", enableDfltMsg = true)
@SequenceGenerator(name = PurchaseItems.SEQ_EGF_PURCHASEITEMS, sequenceName = PurchaseItems.SEQ_EGF_PURCHASEITEMS, allocationSize = 1)
public class PurchaseItems extends AbstractAuditable implements EntityType{
	
	private static final long serialVersionUID = 2507334170114202599L;

    public static final String SEQ_EGF_PURCHASEITEMS = "SEQ_EGF_PURCHASEITEMS";

	
	@Id
	@GeneratedValue(generator = SEQ_EGF_PURCHASEITEMS, strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name="itemCode")
	private String itemCode;
	
	@Column(name="unit")
	private String unit;
	
	@Column(name="unitRate")
	private Long unitRate;
	
	@Column(name="quantity")
	private Integer quantity;
	
	@Column(name="amount")
	private Double amount;
	
	private BigDecimal glcodeid;
	
	 @Transient
	 private CChartOfAccounts chartOfAccounts;
	
	
	
	public CChartOfAccounts getChartOfAccounts() {
		return chartOfAccounts;
	}

	public void setChartOfAccounts(CChartOfAccounts chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

	public BigDecimal getGlcodeid() {
		return glcodeid;
	}

	public void setGlcodeid(BigDecimal glcodeid) {
		this.glcodeid = glcodeid;
	}

	@ManyToOne
	@JoinColumn(name = "purchase_order_id", referencedColumnName = "id")
	private PurchaseOrder purchaseOrder;	
	
	
	  @Column(name="ordernumber") 
	  private String orderNumber;
	  
	  
	  
	  public String getOrderNumber() { return orderNumber; }
	  
	  public void setOrderNumber(String orderNumber) { this.orderNumber =
	  orderNumber; }
	 

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	
	  public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
	  
	  public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
	  this.purchaseOrder = purchaseOrder; }
	 

	public PurchaseItems(Long id, String itemCode, String unit, Long unitRate, Integer quantity, Double amount,
			PurchaseOrder purchaseOrder) {
		super();
		this.id = id;
		this.itemCode = itemCode;
		this.unit = unit;
		this.unitRate = unitRate;
		this.quantity = quantity;
		this.amount = amount;
		this.purchaseOrder = purchaseOrder;
	}

	public PurchaseItems() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "PurchaseItems [id=" + id + ", itemCode=" + itemCode + ", unit=" + unit + ", unitRate=" + unitRate
				+ ", quantity=" + quantity + ", amount=" + amount + ", purchaseOrder=" + purchaseOrder + "]";
	}

	@Override
	public String getBankname() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBankaccount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPanno() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTinno() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIfsccode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getModeofpay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getEntityId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EgwStatus getEgwStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getUnitRate() {
		return unitRate;
	}

	public void setUnitRate(Long unitRate) {
		this.unitRate = unitRate;
	}
	
	

}
