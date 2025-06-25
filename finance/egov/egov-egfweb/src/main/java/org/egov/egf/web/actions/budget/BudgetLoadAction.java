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
package org.egov.egf.web.actions.budget;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Fund;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.dao.FundHibernateDAO;
import org.egov.commons.service.ChartOfAccountsService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.budget.BudgetUpload;
import org.egov.services.budget.BudgetDetailService;
import org.egov.utils.FinancialConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.egov.model.budget.ManualWrapper;
import org.egov.model.budget.BudgetUploadManual;
import org.egov.utils.Constants;
import org.egov.utils.BudgetDetailConfig;
import org.egov.model.budget.Budget;
import org.egov.model.budget.BudgetGroup;
import java.util.Collections;
import org.egov.utils.BudgetDetailHelper;
import java.util.Comparator;

@ParentPackage("egov")
@Results({
        @Result(name = "upload", location = "budgetLoad-upload.jsp"),
        @Result(name = "result", location = "budgetLoad-result.jsp")
})
public class BudgetLoadAction extends BaseFormAction {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BudgetLoadAction.class);
    private File budgetInXls;
    private String budgetInXlsFileName;
    private String budgetInXlsContentType;
    private static final int RE_YEAR_ROW_INDEX = 1;
    private static final int BE_YEAR_ROW_INDEX = 2;
    private static final int DATA_STARTING_ROW_INDEX = 4;
    private static final int FUNDCODE_CELL_INDEX = 0;
    private static final int DEPARTMENTCODE_CELL_INDEX = 1;
    private static final int FUNCTIONCODE_CELL_INDEX = 2;
    private static final int GLCODE_CELL_INDEX = 3;
    private static final int REAMOUNT_CELL_INDEX = 6;
    private static final int BEAMOUNT_CELL_INDEX = 7;
    private static final int PLANNINGPERCENTAGE_CELL_INDEX = 11;
    private static final int MAJORCODE_CELL_INDEX = 4;
    private static final int MINORCODE_CELL_INDEX = 5;
    private static final int LASTYEARBUDGET_CELL_INDEX = 8;
    private static final int CURRENTBUDGET_CELL_INDEX = 9;
    private static final int PERCENTAGECHANGE_CELL_INDEX = 10;

    private boolean errorInMasterData = false;
    private boolean isBudgetUploadFileEmpty = true;
    private MultipartFile[] originalFile = new MultipartFile[1];
    private MultipartFile[] outPutFile = new MultipartFile[1];
    private String originalFileStoreId, outPutFileStoreId;
    private List<FileStoreMapper> originalFiles = new ArrayList<FileStoreMapper>();
    private List<FileStoreMapper> outPutFiles = new ArrayList<FileStoreMapper>();
    private String budgetOriginalFileName;
    private String budgetOutPutFileName;
    private String timeStamp;
    private String budgetUploadError = "Upload the Budget Data as shown in the Download Template format";
    private boolean isManualEntry=false;
    private String budgetData; 
    boolean errorMessage = true;
    protected String mode;
    protected List<String> headerFields = new ArrayList<String>();
    protected List<String> gridFields = new ArrayList<String>();
    protected List<String> mandatoryFields = new ArrayList<String>();
    BudgetDetailHelper budgetDetailHelper;
    protected Long financialYear;

    public boolean getIsManualEntry() {
        return isManualEntry;
    }
    
    public String getBudgetData() {
        return budgetData;
    }

    public void setBudgetData(String budgetData) {
        this.budgetData = budgetData;
    }


    @Autowired
    private FinancialYearDAO financialYearDAO;

    @Autowired
    private FundHibernateDAO fundDAO;

    @Autowired
    private FunctionDAO functionDAO;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    @Qualifier("chartOfAccountsService")
    private ChartOfAccountsService chartOfAccountsService;
    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;

    @Autowired
    @Qualifier("budgetDetailService")
    private BudgetDetailService budgetDetailService;

    @Autowired
    protected FileStoreService fileStoreService;
    
    @Autowired
    @Qualifier("masterDataCache")
    private EgovMasterDataCaching masterDataCache;

    // @Override
    // @SuppressWarnings("unchecked")
    // public void prepare()
    // {

    // }
    @Autowired
    protected BudgetDetailConfig budgetDetailConfig;

    @Override
    public Object getModel() {
        return null;
    }

    public boolean isErrorMessage() {
        return errorMessage;
    }

    @Override
    public String execute() throws Exception {
        if (parameters.containsKey(Constants.MODE))
            setMode(parameters.get(Constants.MODE)[0]);
        errorMessage = false;
        return Constants.LIST;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    public final boolean shouldShowHeaderField(final String field) {
        return headerFields.isEmpty() || headerFields.contains(field);
    }

     public final boolean shouldShowGridField(final String field) {
        return gridFields.isEmpty() || gridFields.contains(field);
    }

    public final boolean shouldShowField(final String field) {
        if (headerFields.isEmpty() && gridFields.isEmpty())
            return true;
        return shouldShowHeaderField(field) || shouldShowGridField(field);
    }

    public void prepare() {
        super.prepare();
        headerFields = budgetDetailConfig.getHeaderFields();
        gridFields = budgetDetailConfig.getGridFields();
        mandatoryFields = budgetDetailConfig.getMandatoryFields();
        addRelatedEntity("budget", Budget.class);
        addRelatedEntity("budgetGroup", BudgetGroup.class);
        if (shouldShowField(Constants.FUNCTION))
            addRelatedEntity(Constants.FUNCTION, CFunction.class);
        if (shouldShowField(Constants.FUND))
            addRelatedEntity(Constants.FUND, Fund.class);
        if (shouldShowField(Constants.CHARTOFACCOUNTS))
            addRelatedEntity(Constants.CHARTOFACCOUNTS, CChartOfAccounts.class);
        if (!parameters.containsKey("skipPrepare")) {
            headerFields = budgetDetailConfig.getHeaderFields();
            gridFields = budgetDetailConfig.getGridFields();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("done findApprovedBudgetsForFY");
            dropdownData.put("financialYearList", persistenceService.findAllBy("from CFinancialYear where isActive=true order by finYearRange desc"));
            if (shouldShowField(Constants.FUND))
                dropdownData.put("fundList",persistenceService.findAllBy("from Fund where isActive=true and isnotleaf=false ORDER BY name ASC "));
            if (shouldShowField(Constants.EXECUTING_DEPARTMENT)) {
                List<Department> departmentList = (List<Department>) masterDataCache.get("egi-department");
                if (departmentList != null) {
                    departmentList.sort(Comparator.comparing(
                        Department::getName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                    ));
                }
                dropdownData.put("executingDepartmentList", departmentList);
            }
            if (shouldShowField(Constants.FUNCTION))
                dropdownData.put("functionList", persistenceService.findAllBy("from CFunction where isactive=true and isnotleaf=false ORDER BY name ASC"));
            // if (shouldShowField(Constants.FUNCTION))
            //     dropdownData.put("functionList", masterDataCache.get("egi-function"));
            System.out.println("shouldShowField(CHARTOFACCOUNTS): " + shouldShowField(Constants.CHARTOFACCOUNTS));
            if (shouldShowField(Constants.CHARTOFACCOUNTS))
            dropdownData.put("chartOfAccountList",persistenceService.findAllBy("from CChartOfAccounts where isActiveForPosting=true ORDER BY id ASC"));
            
            LOGGER.info("Dropdown Data: " + dropdownData);
            LOGGER.info("-----------------------------");
        }
    }
    
    @Action(value = "/budget/budgetLoad-beforeUpload")
    public String beforeUpload()
    {
        originalFiles = (List<FileStoreMapper>) persistenceService.getSession().createQuery(
                "from FileStoreMapper where fileName like '%budget_original%' order by id desc ").setMaxResults(5).list();
        outPutFiles = (List<FileStoreMapper>) persistenceService.getSession().createQuery(
                "from FileStoreMapper where fileName like '%budget_output%' order by id desc ").setMaxResults(5).list();
        return "upload";
    }

    @ValidationErrorPage("upload")
    @Action(value = "/budget/budgetLoad-upload")
    public String upload()
    {
        try {
            FileInputStream fsIP = new FileInputStream(budgetInXls);

            final POIFSFileSystem fs = new POIFSFileSystem(fsIP);
            final HSSFWorkbook wb = new HSSFWorkbook(fs);
            wb.getNumberOfSheets();
            final HSSFSheet sheet = wb.getSheetAt(0);
            final HSSFRow reRow = sheet.getRow(RE_YEAR_ROW_INDEX);
            final HSSFRow beRow = sheet.getRow(BE_YEAR_ROW_INDEX);
            String reFinYearRange = getStrValue(reRow.getCell(1));
            String beFinYearRange = getStrValue(beRow.getCell(1));
            CFinancialYear reFYear = financialYearDAO.getFinancialYearByFinYearRange(reFinYearRange);
            CFinancialYear beFYear = financialYearDAO.getNextFinancialYearByDate(reFYear.getStartingDate());

            if (!validateFinancialYears(reFYear, beFYear, beFinYearRange))
                throw new ValidationException(Arrays.asList(new ValidationError(
                        getText("be.year.is.not.immediate.next.fy.year.of.re.year"),
                        getText("be.year.is.not.immediate.next.fy.year.of.re.year"))));
            timeStamp = new Timestamp((new Date()).getTime()).toString().replace(".", "_");
            if (budgetInXlsFileName.contains("_budget_original_")) {
                budgetOriginalFileName = budgetInXlsFileName.split("_budget_original_")[0] + "_budget_original_"
                        + timeStamp + "."
                        + budgetInXlsFileName.split("\\.")[1];
            } else if (budgetInXlsFileName.contains("_budget_output_")) {
                budgetOriginalFileName = budgetInXlsFileName.split("_budget_output_")[0] + "_budget_original_"
                        + timeStamp + "."
                        + budgetInXlsFileName.split("\\.")[1];
            } else {
                if (budgetInXlsFileName.length() > 60) {
                    throw new ValidationException(Arrays.asList(new ValidationError(
                            getText("file.name.should.be.less.then.60.characters"),
                            getText("file.name.should.be.less.then.60.characters"))));
                } else
                    budgetOriginalFileName = budgetInXlsFileName.split("\\.")[0] + "_budget_original_"
                            + timeStamp + "."
                            + budgetInXlsFileName.split("\\.")[1];
            }

            final FileStoreMapper originalFileStore = fileStoreService.store(budgetInXls,
                    budgetOriginalFileName,
                    budgetInXlsContentType, FinancialConstants.MODULE_NAME_APPCONFIG,false);

            persistenceService.persist(originalFileStore);
            originalFileStoreId = originalFileStore.getFileStoreId();

            List<BudgetUpload> budgetUploadList = loadToBudgetUpload(sheet);
            if(isBudgetUploadFileEmpty){
                fsIP.close();
                throw new ValidationException(new ValidationError(getText("error.while.counting.upload.records"), "There should be atleast one record in the upload file"));
            }
            budgetUploadList = validateMasterData(budgetUploadList);
            budgetUploadList = !errorInMasterData ? validateDuplicateData(budgetUploadList) : budgetUploadList;

            if (errorInMasterData) {
                fsIP.close();
                prepareOutPutFileWithErrors(budgetUploadList);
                addActionMessage(getText("error.while.validating.masterdata"));
                return "result";
            }

            budgetUploadList = removeEmptyRows(budgetUploadList);

            budgetUploadList = budgetDetailService.loadBudget(budgetUploadList, reFYear, beFYear,isManualEntry);

            fsIP.close();
            prepareOutPutFileWithFinalStatus(budgetUploadList);

            addActionMessage(getText("budget.load.sucessful"));

        } catch (final ValidationException e)
        {
            originalFiles = (List<FileStoreMapper>) persistenceService.getSession().createQuery(
                    "from FileStoreMapper where fileName like '%budget_original%' order by id desc ").setMaxResults(5).list();
            outPutFiles = (List<FileStoreMapper>) persistenceService.getSession().createQuery(
                    "from FileStoreMapper where fileName like '%budget_output%' order by id desc ").setMaxResults(5).list();
            throw new ValidationException(Arrays.asList(new ValidationError(budgetUploadError,
                    budgetUploadError)));
        } catch (final IOException e)
        {
            originalFiles = (List<FileStoreMapper>) persistenceService.getSession().createQuery(
                    "from FileStoreMapper where fileName like '%budget_original%' order by id desc ").setMaxResults(5).list();
            outPutFiles = (List<FileStoreMapper>) persistenceService.getSession().createQuery(
                    "from FileStoreMapper where fileName like '%budget_output%' order by id desc ").setMaxResults(5).list();
            throw new ValidationException(Arrays.asList(new ValidationError(budgetUploadError,
                    budgetUploadError)));

        }

        return "result";
    }


    private List<BudgetUpload> convertManualToBudgetUpload(List<BudgetUploadManual> manualList, String reYear, String beYear) {
    List<BudgetUpload> convertedList = new ArrayList<>();

    for (BudgetUploadManual manual : manualList) {
        BudgetUpload upload = new BudgetUpload();

        upload.setFundCode(manual.getFundCode());
        upload.setDeptCode(manual.getDepartmentCode());
        upload.setFunctionCode(manual.getFunctionCode());
        upload.setBudgetHead(manual.getChartOfAccountCode());
        upload.setMajorCode(manual.getMajorCode());
        upload.setMinorCode(manual.getMinorCode());
        upload.setLastYearApproved(manual.getLastYearApproved());
        upload.setCurrentApproved(manual.getCurrentApproved());
        upload.setPercentageChange(manual.getPercentageChange());
        upload.setReFinYear(reYear);
        upload.setBeFinYear(beYear);

        // Parse RE Amount
        upload.setReAmount(parseBigDecimal(manual.getReAmount()));

        // Parse BE Amount
        upload.setBeAmount(parseBigDecimal(manual.getBeAmount()));

        // Parse Planning Percentage
        upload.setPlanningPercentage(parseLong(manual.getPlanningPercentage()));

        convertedList.add(upload);
    }

    return convertedList;
    }

    // Utility method to parse BigDecimal safely
	private BigDecimal parseBigDecimal(String value) {
	    try {
	        return (value != null && !value.trim().isEmpty()) ? new BigDecimal(value.trim()) : BigDecimal.ZERO;
	    } catch (NumberFormatException e) {
	        return BigDecimal.ZERO;
	    }
	}
	
	// Utility method to parse Long safely
	private Long parseLong(String value) {
	    try {
	        return (value != null && !value.trim().isEmpty()) ? Long.parseLong(value.trim()) : 0L;
	    } catch (NumberFormatException e) {
	        return 0L;
	    }
	}

	@Action(value = "/budget/budgetLoad-manualSubmit")
	public String manualSubmit() {
		
	    LOG.info("---------------Inside manualSubmit------------------------------");
	    isManualEntry = true;
	    ManualWrapper manualWrapper;

	    // Step 1: Parse JSON
	    try {
	        ObjectMapper mapper = new ObjectMapper();
	        manualWrapper = mapper.readValue(budgetData, ManualWrapper.class);
	    } catch (IOException e) {
	        LOG.error("Failed to parse budgetData JSON", e);
	        addActionError("Error reading submitted data. Please ensure the format is correct.");
	        return "result";
	    }

	    // Step 2: Get Manual Budget Rows
	    List<BudgetUploadManual> manualList = manualWrapper.getBudgetRows();
	    LOG.info("----------manualList created using manualwrapper func-------------------");
	    if (manualList == null || manualList.isEmpty()) {
	        addActionError("No budget records found. Please check the input.");
	        return "result";
	    }

	    // Step 3: Convert Manual to Domain
	    List<BudgetUpload> budgetUploadList;
	    try {
	        budgetUploadList = convertManualToBudgetUpload(
	            manualList,
	            manualWrapper.getReYear(),
	            manualWrapper.getBeYear()
	        );
	    } catch (Exception e) {
	        LOG.error("Error converting manual data to domain object", e);
	        addActionError("Error processing budget data. Please verify the year or record details.");
	        return "result";
	    }
	    LOG.info("-----------conversion of manual to budget upload done-----------------");

	    // Step 4: Validate against Master Data
	    try {
	        budgetUploadList = validateMasterData(budgetUploadList);
	    } catch (Exception e) {
	        LOG.error("Error during master data validation", e);
	        addActionError("Validation failed due to missing or incorrect master data.");
	        return "result";
	    }
	    LOG.info("---------------------validating data againt master data done-----------------");

	    if (errorInMasterData) {
	        addActionError("Error at validating MasterData");
	        return "result";
	    }

	    // Step 5: Validate Duplicates
	    try {
	        budgetUploadList = validateDuplicateData(budgetUploadList);
	    } catch (Exception e) {
	        LOG.error("Error checking for duplicates", e);
	        addActionError("Error at validating Duplicate Data");
	        return "result";
	    }

	    // Step 6: Remove Empty Rows
	    try {
	        budgetUploadList = removeEmptyRows(budgetUploadList);
	    } catch (Exception e) {
	        LOG.error("Error removing empty rows", e);
	        addActionError("Unable to process data. Please ensure all rows are correctly filled");
	        return "result";
	    }
	    LOG.info("------validating duplicate data and removal of empty rows done----");

	    // Step 7: Load Budget
	    try {
	        String reFinYearRange = manualWrapper.getReYear();
	        String beFinYearRange = manualWrapper.getBeYear();

	        CFinancialYear reFYear = financialYearDAO.getFinancialYearByFinYearRange(reFinYearRange);
	        CFinancialYear beFYear = financialYearDAO.getNextFinancialYearByDate(reFYear.getStartingDate());

	        budgetUploadList = budgetDetailService.loadBudget(budgetUploadList, reFYear, beFYear, isManualEntry);
	    } catch (Exception e) {
	        LOG.error("Error during budget upload", e);
	        addActionError("Error during budget upload");
	        return "result";
	    }
	    LOG.info("-----------loadBudget done-------------");

	    addActionMessage(getText("budget.load.sucessful"));
	    return "result";
	}



    private void prepareOutPutFileWithErrors(List<BudgetUpload> budgetUploadList) {
        FileInputStream fsIP;
        try {
            fsIP = new FileInputStream(budgetInXls);
            Map<String, String> errorsMap = new HashMap<String, String>();
            final POIFSFileSystem fs = new POIFSFileSystem(fsIP);
            final HSSFWorkbook wb = new HSSFWorkbook(fs);
            wb.getNumberOfSheets();
            final HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row = sheet.getRow(3);
            HSSFCell cell = row.createCell(12);
            cell.setCellValue("Error Reason");
            cell.setAsActiveCell();
            HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.RED.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cell.setCellStyle(cellStyle);
            for (BudgetUpload budget : budgetUploadList)
                errorsMap.put(budget.getFundCode() + "-" + budget.getFunctionCode() + "-" + budget.getDeptCode()
                        + "-"
                        + budget.getBudgetHead(), budget.getErrorReason());

            for (int i = DATA_STARTING_ROW_INDEX; i <= sheet.getLastRowNum(); i++) {
                HSSFRow errorRow = sheet.getRow(i);
                if(!isRowEmpty(errorRow)){
                    HSSFCell errorCell = errorRow.createCell(12);
                    String fundCode = getStrValue(sheet.getRow(i).getCell(FUNDCODE_CELL_INDEX));
                    fundCode = fundCode != null ? fundCode : "";
                    String funcCode = getStrValue(sheet.getRow(i).getCell(FUNCTIONCODE_CELL_INDEX));
                    funcCode = funcCode != null ? funcCode : "";
                    String deptCode = getStrValue(sheet.getRow(i).getCell(DEPARTMENTCODE_CELL_INDEX));
                    deptCode = deptCode != null ? deptCode : "";
                    String coaCode = getStrValue(sheet.getRow(i).getCell(GLCODE_CELL_INDEX));
                    coaCode = coaCode != null ? coaCode : "";
                    String errorMsg = fundCode + "-" + funcCode + "-" + deptCode + "-" + coaCode;
                    errorCell.setCellValue(errorsMap.get(errorMsg));
                }
            }

            FileOutputStream output_file = new FileOutputStream(budgetInXls);
            wb.write(output_file);
            output_file.close();
            if (budgetInXlsFileName.contains("_budget_original_")) {
                budgetOutPutFileName = budgetInXlsFileName.split("_budget_original_")[0] + "_budget_output_"
                        + timeStamp + "."
                        + budgetInXlsFileName.split("\\.")[1];
            } else if (budgetInXlsFileName.contains("_budget_output_")) {
                budgetOutPutFileName = budgetInXlsFileName.split("_budget_output_")[0] + "_budget_output_"
                        + timeStamp + "."
                        + budgetInXlsFileName.split("\\.")[1];
            } else {
                if (budgetInXlsFileName.length() > 60) {
                    throw new ValidationException(Arrays.asList(new ValidationError(
                            getText("file.name.should.be.less.then.60.characters"),
                            getText("file.name.should.be.less.then.60.characters"))));
                } else
                    budgetOutPutFileName = budgetInXlsFileName.split("\\.")[0] + "_budget_output_"
                            + timeStamp + "."
                            + budgetInXlsFileName.split("\\.")[1];
            }
            final FileStoreMapper outPutFileStore = fileStoreService.store(budgetInXls,
                    budgetOutPutFileName,
                    budgetInXlsContentType, FinancialConstants.MODULE_NAME_APPCONFIG);

            persistenceService.persist(outPutFileStore);

            outPutFileStoreId = outPutFileStore.getFileStoreId();
        } catch (FileNotFoundException e) {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(),
                    e.getMessage())));
        } catch (IOException e) {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(),
                    e.getMessage())));
        }
    }

    private void prepareOutPutFileWithFinalStatus(List<BudgetUpload> budgetUploadList) {
        FileInputStream fsIP;
        try {
            fsIP = new FileInputStream(budgetInXls);

            Map<String, String> errorsMap = new HashMap<String, String>();
            final POIFSFileSystem fs = new POIFSFileSystem(fsIP);
            final HSSFWorkbook wb = new HSSFWorkbook(fs);
            wb.getNumberOfSheets();
            final HSSFSheet sheet = wb.getSheetAt(0);
            Map<String, String> finalStatusMap = new HashMap<String, String>();

            HSSFRow row = sheet.getRow(3);
            HSSFCell cell = row.createCell(12);
            cell.setCellValue("Status");

            for (BudgetUpload budget : budgetUploadList)
                finalStatusMap.put(budget.getFundCode() + "-" + budget.getFunctionCode() + "-" + budget.getDeptCode()
                        + "-"
                        + budget.getBudgetHead(), budget.getFinalStatus());

            for (int i = DATA_STARTING_ROW_INDEX; i <= sheet.getLastRowNum(); i++) {
                HSSFRow finalStatusRow = sheet.getRow(i);
                HSSFCell finalStatusCell = finalStatusRow.createCell(12);
                finalStatusCell.setCellValue(finalStatusMap.get((getStrValue(sheet.getRow(i).getCell(FUNDCODE_CELL_INDEX)) + "-"
                        + getStrValue(sheet.getRow(i).getCell(FUNCTIONCODE_CELL_INDEX)) + "-"
                        + getStrValue(sheet.getRow(i).getCell(DEPARTMENTCODE_CELL_INDEX)) + "-" + getStrValue(sheet.getRow(i)
                        .getCell(GLCODE_CELL_INDEX)))));
            }

            FileOutputStream output_file = new FileOutputStream(budgetInXls);
            wb.write(output_file);
            output_file.close();
            if (budgetInXlsFileName.contains("_budget_original_")) {
                budgetOutPutFileName = budgetInXlsFileName.split("_budget_original_")[0] + "_budget_output_"
                        + timeStamp + "."
                        + budgetInXlsFileName.split("\\.")[1];
            } else if (budgetInXlsFileName.contains("_budget_output_")) {
                budgetOutPutFileName = budgetInXlsFileName.split("_budget_output_")[0] + "_budget_output_"
                        + timeStamp + "."
                        + budgetInXlsFileName.split("\\.")[1];
            } else {
                if (budgetInXlsFileName.length() > 60) {
                    throw new ValidationException(Arrays.asList(new ValidationError(
                            getText("file.name.should.be.less.then.60.characters"),
                            getText("file.name.should.be.less.then.60.characters"))));
                } else
                    budgetOutPutFileName = budgetInXlsFileName.split("\\.")[0] + "_budget_output_"
                            + timeStamp + "."
                            + budgetInXlsFileName.split("\\.")[1];
            }
            final FileStoreMapper outPutFileStore = fileStoreService.store(budgetInXls,
                    budgetOutPutFileName,
                    budgetInXlsContentType, FinancialConstants.MODULE_NAME_APPCONFIG);
            persistenceService.persist(outPutFileStore);

            outPutFileStoreId = outPutFileStore.getFileStoreId();
        } catch (FileNotFoundException e) {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(),
                    e.getMessage())));
        } catch (IOException e) {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(),
                    e.getMessage())));
        }
    }

    private List<BudgetUpload> removeEmptyRows(List<BudgetUpload> budgetUploadList) {
        List<BudgetUpload> tempList = new ArrayList<>();
        for (BudgetUpload budget : budgetUploadList) {
            if (!budget.getErrorReason().equalsIgnoreCase("Empty Record"))
                tempList.add(budget);

        }
        return tempList;
    }

    private List<BudgetUpload> validateMasterData(List<BudgetUpload> budgetUploadList) {
        List<BudgetUpload> tempList = new ArrayList<>();
        try {
            String error = "";
            Map<String, Fund> fundMap = new HashMap<String, Fund>();
            Map<String, CFunction> functionMap = new HashMap<String, CFunction>();
            Map<String, Department> departmentMap = new HashMap<String, Department>();
            Map<String, CChartOfAccounts> coaMap = new HashMap<String, CChartOfAccounts>();
            List<Fund> fundList = fundDAO.findAllActiveIsLeafFunds();
            List<CFunction> functionList = functionDAO.getAllActiveFunctions();
            List<Department> departmentList = masterDataCache.get("egi-department");
            List<CChartOfAccounts> coaList = chartOfAccountsService.findAll();
            for (Fund fund : fundList)
                fundMap.put(fund.getCode(), fund);
            for (CFunction function : functionList)
                functionMap.put(function.getCode(), function);
            for (Department department : departmentList)
                departmentMap.put(department.getCode(), department);
            for (CChartOfAccounts coa : coaList)
                coaMap.put(coa.getGlcode(), coa);

            for (BudgetUpload budget : budgetUploadList) {
                error = "";
                if(budget.getFundCode() != null && budget.getFundCode().isEmpty())
                    error = error + getText("error.while.checking.fund.value");
                else if (fundMap.get(budget.getFundCode()) == null)
                    error = error + getText("fund.is.not.exist") + budget.getFundCode();
                else
                    budget.setFund(fundMap.get(budget.getFundCode()));
                if (budget.getFunctionCode() != null && budget.getFunctionCode().isEmpty())
                    error = error + getText("error.while.checking.function.value");
                else if(functionMap.get(budget.getFunctionCode()) == null)
                    error = error + " " + getText("function.is.not.exist") + budget.getFunctionCode();
                else
                    budget.setFunction(functionMap.get(budget.getFunctionCode()));

                if (budget.getDeptCode() != null && budget.getFundCode().isEmpty())
                    error = error + getText("error.while.checking.dept.value");
                else if(departmentMap.get(budget.getDeptCode()) == null)
                    error = error + " " + getText("department.is.not.exist") + budget.getDeptCode();
                else
                    budget.setDeptCode(budget.getDeptCode());

                if (budget.getBudgetHead() != null && budget.getBudgetHead().isEmpty())
                    error = error + getText("error.while.checking.coa.value");
                else if(coaMap.get(budget.getBudgetHead()) == null)
                    error = error + " " + getText("coa.is.not.exist") + budget.getBudgetHead();
                else
                    budget.setCoa(coaMap.get(budget.getBudgetHead()));
                
                if(budget.getBeAmount() != null && budget.getBeAmount().compareTo(new BigDecimal(0)) == 0)
                    error = error + " " + getText("error.while.checking.be.value");
                
                if(budget.getReAmount() != null && budget.getReAmount().compareTo(new BigDecimal(0)) == 0)
                    error = error + " " + getText("error.while.checking.re.value");
                
                budget.setErrorReason(error);
                if (!error.equalsIgnoreCase("")) {
                    errorInMasterData = true;
                }
                tempList.add(budget);
            }
        } catch (final ValidationException e)
        {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getErrors().get(0).getMessage(),
                    e.getErrors().get(0).getMessage())));
        } /*
           * catch (final Exception e) { throw new
           * ValidationException(Arrays.asList(new
           * ValidationError(e.getMessage(), e.getMessage()))); }
           */
        return tempList;
    }

    private List<BudgetUpload> validateDuplicateData(List<BudgetUpload> budgetUploadList) {
        List<BudgetUpload> tempList = new ArrayList<>();
        try {
            String error = "";
            Map<String, BudgetUpload> budgetUploadMap = new HashMap<String, BudgetUpload>();
            for (BudgetUpload budget : budgetUploadList) {
                if (budget.getFundCode() != null && budget.getFunctionCode() != null && budget.getDeptCode() != null
                        && budget.getBudgetHead() != null && !budget.getFundCode().equalsIgnoreCase("")
                        && !budget.getFunctionCode().equalsIgnoreCase("") && !budget.getDeptCode().equalsIgnoreCase("")
                        && !budget.getBudgetHead().equalsIgnoreCase(""))
                    if (budgetUploadMap.get(budget.getFundCode() + "-" + budget.getFunctionCode() + "-" + budget.getDeptCode()
                            + "-"
                            + budget.getBudgetHead()) == null)
                        budgetUploadMap.put(budget.getFundCode() + "-" + budget.getFunctionCode() + "-" + budget.getDeptCode()
                                + "-"
                                + budget.getBudgetHead(), budget);
                    else {
                        budget.setErrorReason(budget.getErrorReason() + getText("duplicate.record"));
                        errorInMasterData = true;
                    }
                else if (budget.getFundCode() == null && budget.getFunctionCode() == null && budget.getDeptCode() == null
                        && budget.getBudgetHead() == null) {
                    budget.setErrorReason(getText("empty.record"));
                }
                else {
                    budget.setErrorReason(getText("empty.record"));
                }

                tempList.add(budget);
            }
        } catch (final ValidationException e)
        {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getErrors().get(0).getMessage(),
                    e.getErrors().get(0).getMessage())));
        } /*
           * catch (final Exception e) { throw new
           * ValidationException(Arrays.asList(new
           * ValidationError(e.getMessage(), e.getMessage()))); }
           */
        return tempList;
    }

    private List<BudgetUpload> loadToBudgetUpload(HSSFSheet sheet) {
        List<BudgetUpload> budgetUploadList = new ArrayList<BudgetUpload>();
        try {

            for (int i = DATA_STARTING_ROW_INDEX; i <= sheet.getLastRowNum(); i++)
                if(!isRowEmpty(sheet.getRow(i))){
                    budgetUploadList.add(getBudgetUpload(sheet.getRow(i)));
                    isBudgetUploadFileEmpty = false;
                }
        } catch (final ValidationException e)
        {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getErrors().get(0).getMessage(),
                    e.getErrors().get(0).getMessage())));
        } /*
           * catch (final Exception e) { throw new
           * ValidationException(Arrays.asList(new
           * ValidationError(e.getMessage(), e.getMessage()))); }
           */
        return budgetUploadList;

    }
    
    public static boolean isRowEmpty(HSSFRow row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            HSSFCell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != HSSFCell.CELL_TYPE_BLANK)
                return false;
        }
        return true;
    }

    private BudgetUpload getBudgetUpload(HSSFRow row) {
        BudgetUpload budget = new BudgetUpload();
        try {
            if (row != null) {
                budget.setFundCode(getStrValue(row.getCell(FUNDCODE_CELL_INDEX)) == null ? "" : getStrValue(row
                        .getCell(FUNDCODE_CELL_INDEX)));
                budget.setDeptCode(getStrValue(row.getCell(DEPARTMENTCODE_CELL_INDEX)) == null ? "" : getStrValue(row
                        .getCell(DEPARTMENTCODE_CELL_INDEX)));
                budget.setFunctionCode(getStrValue(row.getCell(FUNCTIONCODE_CELL_INDEX)) == null ? "" : getStrValue(row
                        .getCell(FUNCTIONCODE_CELL_INDEX)));
                budget.setBudgetHead(getStrValue(row.getCell(GLCODE_CELL_INDEX)) == null ? "" : getStrValue(row
                        .getCell(GLCODE_CELL_INDEX)));
                budget.setMajorCode(getStrValue(row.getCell(MAJORCODE_CELL_INDEX)) == null ? "" : getStrValue(row
                        .getCell(MAJORCODE_CELL_INDEX)));
                budget.setMinorCode(getStrValue(row.getCell(MINORCODE_CELL_INDEX)) == null ? "" : getStrValue(row
                        .getCell(MINORCODE_CELL_INDEX)));
                budget.setLastYearApproved(getStrValue(row.getCell(LASTYEARBUDGET_CELL_INDEX)) == null ? "" : getStrValue(row
                        .getCell(LASTYEARBUDGET_CELL_INDEX)));
                budget.setCurrentApproved(getStrValue(row.getCell(CURRENTBUDGET_CELL_INDEX)) == null ? "" : getStrValue(row
                        .getCell(CURRENTBUDGET_CELL_INDEX)));
                budget.setPercentageChange(getStrValue(row.getCell(PERCENTAGECHANGE_CELL_INDEX)) == null ? "" : getStrValue(row
                        .getCell(PERCENTAGECHANGE_CELL_INDEX)));                
                budget.setReAmount(BigDecimal.valueOf(Long.valueOf(getStrValue(row.getCell(REAMOUNT_CELL_INDEX)) == null ? "0"
                        : getStrValue(row.getCell(REAMOUNT_CELL_INDEX)))));
                budget.setBeAmount(BigDecimal.valueOf(Long.valueOf(getStrValue(row.getCell(BEAMOUNT_CELL_INDEX)) == null ? "0"
                        : getStrValue(row.getCell(BEAMOUNT_CELL_INDEX)))));
                budget.setPlanningPercentage(getNumericValue(row.getCell(PLANNINGPERCENTAGE_CELL_INDEX)) == null ? 0
                        : getNumericValue(row.getCell(PLANNINGPERCENTAGE_CELL_INDEX)).longValue());
            }
        } catch (final ValidationException e)
        {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getErrors().get(0).getMessage(),
                    e.getErrors().get(0).getMessage())));
        } /*
           * catch (final Exception e) { throw new
           * ValidationException(Arrays.asList(new
           * ValidationError(e.getMessage(), e.getMessage()))); }
           */
        return budget;
    }

    private boolean validateFinancialYears(CFinancialYear reFYear, CFinancialYear beFYear, String beYear) {
        try {

            if (reFYear == null)
                throw new ValidationException(Arrays.asList(new ValidationError(getText("re.year.is.not.exist"),
                        getText("re.year.is.not.exist"))));

            if (beFYear == null)
                throw new ValidationException(Arrays.asList(new ValidationError(getText("be.year.is.not.exist"),
                        getText("be.year.is.not.exist"))));
            if (beFYear.getFinYearRange().equalsIgnoreCase(beYear))
                return true;
            else
                return false;
        } catch (final ValidationException e)
        {
            throw new ValidationException(Arrays.asList(new ValidationError(e.getErrors().get(0).getMessage(),
                    e.getErrors().get(0).getMessage())));
        } /*
           * catch (final Exception e) { throw new
           * ValidationException(Arrays.asList(new
           * ValidationError(getText("year.is.not.exist"),
           * getText("year.is.not.exist")))); }
           */
    }

    @Override
    public void validate()
    {

    }

    private String getStrValue(final HSSFCell cell) {
        if (cell == null && cell.getCellType() == HSSFCell.CELL_TYPE_BLANK)
            return null;
        double numericCellValue = 0d;
        String strValue = "";
        switch (cell.getCellType())
        {
        case HSSFCell.CELL_TYPE_NUMERIC:
            numericCellValue = cell.getNumericCellValue();
            final DecimalFormat decimalFormat = new DecimalFormat("#");
            strValue = decimalFormat.format(numericCellValue);
            break;
        case HSSFCell.CELL_TYPE_STRING:
            strValue = cell.getStringCellValue();
            break;
        }
        return strValue.trim().isEmpty() ? null : strValue.trim();

    }

    private BigDecimal getNumericValue(final HSSFCell cell) {
        if (cell == null && cell.getCellType() == HSSFCell.CELL_TYPE_BLANK)
            return null;
        double numericCellValue = 0d;
        BigDecimal bigDecimalValue = BigDecimal.ZERO;
        String strValue = "";

        switch (cell.getCellType())
        {
        case HSSFCell.CELL_TYPE_NUMERIC:
            numericCellValue = cell.getNumericCellValue();
            bigDecimalValue = BigDecimal.valueOf(numericCellValue);
            break;
        case HSSFCell.CELL_TYPE_STRING:
            strValue = cell.getStringCellValue();
            strValue = strValue.replaceAll("[^\\p{L}\\p{Nd}]", "");
            if (strValue != null && strValue.contains("E+"))
            {
                final String[] split = strValue.split("E+");
                String mantissa = split[0].replaceAll(".", "");
                final int exp = Integer.parseInt(split[1]);
                while (mantissa.length() <= exp + 1)
                    mantissa += "0";
                numericCellValue = Double.parseDouble(mantissa);
                bigDecimalValue = BigDecimal.valueOf(numericCellValue);
            } else if (strValue != null && strValue.contains(","))
                strValue = strValue.replaceAll(",", "");
            // Ignore the error and continue Since in numric field we find empty or non numeric value
            try {
                numericCellValue = Double.parseDouble(strValue);
                bigDecimalValue = BigDecimal.valueOf(numericCellValue);
            } catch (final NumberFormatException e)
            {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Found : Non numeric value in Numeric Field :" + strValue + ":");
            }
            break;
        }
        return bigDecimalValue;

    }

    public File getBudgetInXls() {
        return budgetInXls;
    }

    public void setBudgetInXls(File budgetInXls) {
        this.budgetInXls = budgetInXls;
    }

    public String getOriginalFileStoreId() {
        return originalFileStoreId;
    }

    public void setOriginalFileStoreId(String originalFileStoreId) {
        this.originalFileStoreId = originalFileStoreId;
    }

    public void setBudgetInXlsFileName(String budgetInXlsFileName) {
        this.budgetInXlsFileName = budgetInXlsFileName;
    }

    public void setBudgetInXlsContentType(String budgetInXlsContentType) {
        this.budgetInXlsContentType = budgetInXlsContentType;
    }

    public String getOutPutFileStoreId() {
        return outPutFileStoreId;
    }

    public void setOutPutFileStoreId(String outPutFileStoreId) {
        this.outPutFileStoreId = outPutFileStoreId;
    }

    public List<FileStoreMapper> getOriginalFiles() {
        return originalFiles;
    }

    public void setOriginalFiles(List<FileStoreMapper> originalFiles) {
        this.originalFiles = originalFiles;
    }

    public List<FileStoreMapper> getOutPutFiles() {
        return outPutFiles;
    }

    public void setOutPutFiles(List<FileStoreMapper> outPutFiles) {
        this.outPutFiles = outPutFiles;
    }

    public String getBudgetOriginalFileName() {
        return budgetOriginalFileName;
    }

    public void setBudgetOriginalFileName(String budgetOriginalFileName) {
        this.budgetOriginalFileName = budgetOriginalFileName;
    }

    public String getBudgetOutPutFileName() {
        return budgetOutPutFileName;
    }

    public void setBudgetOutPutFileName(String budgetOutPutFileName) {
        this.budgetOutPutFileName = budgetOutPutFileName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

}
