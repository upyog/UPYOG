
package org.egov.finance.inbox.util;

import org.egov.finance.inbox.entity.EgwStatus;

/**
 * 
 * @author bdhal
 */
public interface EntityType {

	/**
	 * To get the bank name for the entity.
	 * it's required to generate the bank advice for Contractor/supplier/employee
	 * @return
	 */
	public String getBankname();
	/**
	 * To get the bank account no. for the entity.
	 * it's required to generate the bank advice for Contractor/supplier/employee.
	 * @return
	 */
	public String getBankaccount();
	/**
	 * To get the Pan no. for the entity.
	 * it's required to generate the bank advice for Contractor/supplier/employee.
	 * @return
	 */
	public String getPanno();
	/**
	 * To get the Tin no. for the entity.
	 * it's required to generate the bank advice
	 * @return
	 */
	public String getTinno();
	/**
	 * To get the IFSC code for the entity.
	 * it's required to generate the bank advice
	 * @return
	 */
	public String getIfsccode();
	/**
	 * To get the subledger party name.
	 * @return
	 */
	public String getName();
	/**
	 * To get the mode of payment, to make a payment for the entity
	 * possible value, Cheque, Cash or RTGS
	 * @return
	 */
	public String getModeofpay();
	/**
	 * To get the code for the entity
	 * @return
	 */
	public String getCode();
	/**
	 *To get The id/detailKeyd for the entity 
	 * @return
	 */
	public Integer getEntityId();
	/**
	 * 
	 * @return data to be displayed in reports
	 */
	public String getEntityDescription();	
	/**
     *
     * @return status of Entity^M
     */
    public EgwStatus getEgwStatus();

}
