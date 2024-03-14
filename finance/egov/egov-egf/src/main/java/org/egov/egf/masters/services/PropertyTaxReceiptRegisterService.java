package org.egov.egf.masters.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CGeneralLedger;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.Vouchermis;
import org.egov.commons.repository.CChartOfAccountsRepository;
import org.egov.commons.repository.CGeneralLedgerRepository;
import org.egov.commons.repository.CVoucherHeaderRepository;
import org.egov.commons.repository.FundRepository;
import org.egov.commons.repository.VouchermisRepository;
import org.egov.commons.service.EntityTypeService;
import org.egov.commons.utils.EntityType;
import org.egov.egf.masters.model.PropertyTaxReceiptRegister;
import org.egov.egf.masters.repository.PropertyTaxDemandRegisterRepository;
import org.egov.egf.masters.repository.PropertyTaxReceiptRegisterRepository;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infstr.services.PersistenceService;
import org.egov.services.voucher.VoucherHeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PropertyTaxReceiptRegisterService implements EntityTypeService{
	
	@Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
	
	@Autowired
	private CChartOfAccountsRepository chartOfAccountsRepository;
	@Autowired
	private PropertyTaxDemandRegisterRepository propertyTaxDemandRegisterRepository;
	@Autowired
	private VoucherHeaderService voucherHeaderService;
	@Autowired
	private FundRepository fundRepository;
	
	@Autowired
	private CVoucherHeaderRepository cvoucherHeaderRepository;
	
	@Autowired
	private VouchermisRepository vouchermisRepository;
	@Autowired
	private CGeneralLedgerRepository cgeneralLedgerRepository;

    
    @PersistenceContext
    private EntityManager entityManager; 
    @Autowired
    private PropertyTaxReceiptRegisterRepository propertyTaxReceiptRegisterRepository;
    
    
    public Optional<CVoucherHeader> getDetailsForMaxIdFromVoucherHeader() {
        return cvoucherHeaderRepository.getDetailsForMaxIdFromVoucherHeader();
    }
    public List<PropertyTaxReceiptRegister> getAlllDatas(PropertyTaxReceiptRegister pt){
    	List<PropertyTaxReceiptRegister> propertyTaxReceiptRegister = propertyTaxReceiptRegisterRepository.findAll();
    	
    	 List<CVoucherHeader> voucherHeaders = cvoucherHeaderRepository.findAll();
         List<Vouchermis> vouList = vouchermisRepository.findAll();
         List<CGeneralLedger> cg = cgeneralLedgerRepository.findAll();
         
         Optional<CVoucherHeader> cvhh = getDetailsForMaxIdFromVoucherHeader();
         System.out.println(cvhh);
         Long  nextId = cvhh.get().getId()+1;
         
         LocalDate currentDates = LocalDate.now();
         String currentMonth = String.format("%02d", currentDates.getMonthValue()); 
         String currentFinancialYear = currentDates.format(DateTimeFormatter.ofPattern("2023-24")); 
         String newVoucherNumber = String.format("1/RJV/%08d/%s/%s", nextId, currentMonth, currentFinancialYear);
         System.out.println(newVoucherNumber);

         int startNumber = 100000000;
         String formattedId = String.format("%09d",startNumber+nextId); 
         String cgvn = "1/JVG/CGVN" + formattedId;
         System.out.println(cgvn);     
         Date currentDateAndTime = new Date();

         Calendar calendar = Calendar.getInstance();
         calendar.setTime(currentDateAndTime);
         calendar.set(Calendar.HOUR_OF_DAY, 0);
         calendar.set(Calendar.MINUTE, 0);
         calendar.set(Calendar.SECOND, 0);
         calendar.set(Calendar.MILLISECOND, 0);
         Date currentDate = calendar.getTime();
         
         	CVoucherHeader cVoucherHeader = new CVoucherHeader();
         	Fund fund = getFundById(1);
         	cVoucherHeader.setId(108L);
         	cVoucherHeader.setName("JVGeneral");
         	cVoucherHeader.setType("Journal Voucher");
         	cVoucherHeader.setDescription("null");
         	cVoucherHeader.setEffectiveDate(new Date());
         	cVoucherHeader.setVoucherNumber(newVoucherNumber);
         	cVoucherHeader.setVoucherDate(currentDate);
         	cVoucherHeader.setFundId(fund);
         	cVoucherHeader.setFiscalPeriodId(21);
         	cVoucherHeader.setStatus(0);
         	cVoucherHeader.setOriginalvcId(null);
         	cVoucherHeader.setIsConfirmed(null);
        	cVoucherHeader.setCreatedBy(251L);
         	cVoucherHeader.setRefvhId(null);
         	cVoucherHeader.setCgvn(cgvn);
         	cVoucherHeader.setLastModifiedBy(251L);
         	cVoucherHeader.setLastModifiedDate(new Date());
         	cVoucherHeader.setModuleId(null);
        	cVoucherHeader.setCreatedDate(new Date());
    //     	cVoucherHeader.setVersion(1L);
      //   	voucherHeaderService.save(cVoucherHeader);
         	CVoucherHeader savedCVoucherHeader = voucherHeaderService.save(cVoucherHeader);

         	System.out.println(savedCVoucherHeader.getId());
         	Vouchermis vouchermis = new Vouchermis();
         	CVoucherHeader cv = savedCVoucherHeader;
//         	vouchermis.setId(61L);
         	vouchermis.setBillnumber(null);
         	vouchermis.setDivisionid(null);
         	vouchermis.setDepartmentcode("DEPT_13");
         	vouchermis.setVoucherheaderid(cv);
         	vouchermis.setFundsource(null);
         	vouchermis.setSchemeid(null);
         	vouchermis.setSubschemeid(null);
         	vouchermis.setFunctionary(null);
         	vouchermis.setSourcePath(null);
         	vouchermis.setBudgetaryAppnumber(null);
         	vouchermis.setBudgetCheckReq(null);
         	vouchermis.setFunction(null);
         	vouchermis.setReferenceDocument(null);
         	vouchermis.setServiceName(null);
         	
         	voucherHeaderService.save(vouchermis);
         	System.out.println(vouchermis);
         List<CGeneralLedger> cgeneralLedger = new ArrayList<>();
         CChartOfAccounts coa = getById(1167L);
         CChartOfAccounts coa2 = getById(1123L);
         CGeneralLedger entry1 = new CGeneralLedger();
         entry1.setVoucherlineId(1);
         entry1.setEffectiveDate(new Date());
         entry1.setGlcodeId(coa);
         entry1.setGlcode("4502101");
         entry1.setDebitAmount(Double.parseDouble(pt.getTotalcollection().replaceAll(",", "")));
         entry1.setCreditAmount(0.00);
         entry1.setDescription(null);
         entry1.setVoucherHeaderId(cv);
         CGeneralLedger entry2 = new CGeneralLedger();
         entry2.setVoucherlineId(2);
         entry2.setEffectiveDate(new Date());
         entry2.setGlcodeId(coa2);
         entry2.setGlcode("4311001");
         entry2.setDebitAmount(0.00);
         entry2.setCreditAmount(Double.parseDouble(pt.getTotalcollection().replaceAll(",", "")));
         entry2.setDescription(null);
         entry2.setVoucherHeaderId(cv);
         cgeneralLedger.add(entry1);
         cgeneralLedger.add(entry2);
         voucherHeaderService.save(cgeneralLedger);
         
         
    	
    	
    	return propertyTaxReceiptRegister;
    }
    
    
    private CVoucherHeader getmaxid() {
    	CVoucherHeader maxIdEntity = cvoucherHeaderRepository.getDetailsForMaxIdFromVoucherHeaders();
    	CVoucherHeader cvvv = null; 
        if (maxIdEntity != null) {
            System.out.println("Maximum ID from citya.voucherheader: " + (maxIdEntity.getId()));
            cvvv = new CVoucherHeader();
            cvvv.setId(maxIdEntity.getId());
        } else {
            System.out.println("No details found for maximum ID in citya.voucherheader.");
        }
       
        return cvvv;
    }
    
	private Object SimpleDateFormat(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private CChartOfAccounts getById(long l) {
		// TODO Auto-generated method stub
		return chartOfAccountsRepository.findOne(l);
	}

	private CVoucherHeader getById(int i) {
		// TODO Auto-generated method stub
		return cvoucherHeaderRepository.findOne(63L);
	}

	private Fund getFundById(int i) {
		// TODO Auto-generated method stub
		return fundRepository.findByCode("01");
	}
    
    
    
    
    
    

	@Override
	public List<? extends EntityType> getAllActiveEntities(Integer accountDetailTypeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends EntityType> filterActiveEntities(String filterKey, int maxRecords,
			Integer accountDetailTypeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getAssetCodesForProjectCode(Integer accountdetailkey) throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends EntityType> validateEntityForRTGS(List<Long> idsList) throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends EntityType> getEntitiesById(List<Long> idsList) throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}
	

}
