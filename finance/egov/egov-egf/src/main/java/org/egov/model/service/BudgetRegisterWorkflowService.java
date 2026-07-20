package org.egov.model.service;

import org.egov.commons.CFinancialYear;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.eis.entity.Assignment;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.Designation;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.model.budget.BudgetRegister;
import org.egov.model.repository.BudgetRegisterWorkflowRepository;
import org.egov.pims.commons.Position;
import org.egov.utils.FinancialConstants;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * BudgetRegisterWorkflowService manages BudgetRegister entity workflow operations.
 * Handles budget register creation, workflow transitions, and approval process.
 *
 * Key Features:
 * - Create and initialize budget registers with workflow
 * - Manage workflow transitions (START, FORWARD, APPROVE, REJECT, CANCEL, REVERT)
 * - Generate unique budget register numbers with format: BR-{FinYearRange}-{Sequence}
 * - Handle workflow state changes and status updates
 * - Integration with SimpleWorkflowService for approval hierarchy
 * - Support for multi-level approval process with position-based routing
 * - Retrieve budget registers by financial years and status
 *
 * Workflow Actions:
 * - START: Initialize workflow for new budget register
 * - FORWARD: Forward to next approver in hierarchy
 * - APPROVE: Final approval by authorized person
 * - REJECT: Reject budget register
 * - CANCEL: Cancel budget register
 * - REVERT: Send back to creator for corrections
 *
 * @see BudgetRegister
 * @see BudgetRegisterWorkflowRepository
 * @see SimpleWorkflowService
 */
@Service
public class BudgetRegisterWorkflowService {

    private static final Logger LOG = LoggerFactory.getLogger(BudgetRegisterWorkflowService.class);

    @Autowired
    private SecurityUtils securityUtils;


    @Autowired
    private BudgetRegisterWorkflowRepository budgetRegisterWorkflowRepository;


    @Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<BudgetRegister> egBudgetRegisterWorkflowService;

    @Autowired
    private MicroserviceUtils microServiceUtil;

    @Autowired
    private EgwStatusHibernateDAO egwStatusDAO;



    /**
     * Retrieves the primary assignment details of the specified user.
     *
     * <p>
     * Assignment information is fetched through employee master services
     * and includes position, designation, and department details.
     * </p>
     *
     * @param userId identifier of the user
     * @return assignment details if available, otherwise null
     */

    private Assignment getCurrentUserAssignment(final Long userId) {
//        Long userId = ApplicationThreadLocals.getUserId();
        List<EmployeeInfo> emplist = microServiceUtil.getEmployee(userId,null, null, null);
        Assignment assignment =new Assignment();
        if(null!=emplist && emplist.size()>0 && emplist.get(0).getAssignments().size()>0){
            Position position = new Position();
            position.setId(emplist.get(0).getAssignments().get(0).getPosition());
            assignment.setPosition(position);

            org.egov.pims.commons.Designation designation = new org.egov.pims.commons.Designation();
            Designation _desg = this.getDesignationDetails(emplist.get(0).getAssignments().get(0).getDesignation());
            designation.setCode(_desg.getCode());
            designation.setName(_desg.getName());
            assignment.setDesignation(designation);

            org.egov.infra.admin.master.entity.Department department = new org.egov.infra.admin.master.entity.Department();
            Department _dept = this.getDepartmentDetails(emplist.get(0).getAssignments().get(0).getDepartment());
            department.setCode(_dept.getCode());
            department.setName(_dept.getName());

            return assignment;
        }
        return null;
    }


    private Department getDepartmentDetails(String deptCode){

        Department dept = microServiceUtil.getDepartmentByCode(deptCode);
        return dept;

    }


    private Designation getDesignationDetails(String desgnCode){
        List<Designation> desgnList = microServiceUtil.getDesignation(desgnCode);
        return !desgnList.isEmpty() ? desgnList.get(0) : null;
    }

    /**
     * Creates or progresses a workflow transition for a budget register.
     *
     * <p>
     * Handles all workflow actions including:
     * <ul>
     *     <li>START - Initiates workflow</li>
     *     <li>FORWARD - Moves workflow to next approver</li>
     *     <li>APPROVE - Completes workflow approval</li>
     *     <li>REJECT - Rejects the budget register</li>
     *     <li>CANCEL - Cancels the workflow</li>
     * </ul>
     * </p>
     *
     * <p>
     * Workflow ownership, state transitions, comments, and next actions
     * are determined using the configured workflow matrix.
     * </p>
     *
     * @param budgetRegister budget register undergoing workflow transition
     * @param approvalPosition position of the next approver
     * @param approvalComment workflow comments
     * @param additionalRule additional workflow rule if applicable
     * @param workFlowAction action to be performed
     * @param approvalDesignation designation of the approver
     */
    public void createBudgetRegisterWorkflowTransition(final BudgetRegister budgetRegister,
                                                       final Long approvalPosition,
                                                       final String approvalComment,
                                                       final String additionalRule,
                                                       final String workFlowAction,
                                                       final String approvalDesignation) {
        if (LOG.isDebugEnabled())
            LOG.debug("Create BudgetRegister Workflow Transition Started ...");

        final User user = securityUtils.getCurrentUser();
        final Date currentDate = new Date();
        Assignment wfInitiator = null;
        final Set<String> finalDesignationNames = new HashSet<>();
        String stateValue = "";

        // resolve wf initiator (assignment of creator) if record exists
        if (budgetRegister != null && budgetRegister.getId() != null) {
            wfInitiator = getCurrentUserAssignment(budgetRegister.getCreatedBy());
        }

        // REJECT branch - immediate reject
        if ("REJECT".equalsIgnoreCase(workFlowAction)) {
            stateValue = "REJECTED";
            Position owner = (wfInitiator != null) ? wfInitiator.getPosition() : null;
            budgetRegister.transition().progressWithStateCopy()
                    .withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(approvalComment)
                    .withStateValue(stateValue)
                    .withDateInfo(currentDate)
                    .withOwner(owner)
                    .withNextAction("")
                    .withNatureOfTask("Budget Input Approval");
            if (LOG.isDebugEnabled())
                LOG.debug("BudgetRegister rejected by {} ; id={}", user.getUsername(), budgetRegister.getId());
            return;
        }

        // Build owner Position stub if approvalPosition provided
        Position ownerPos = null;
        if (approvalPosition != null && approvalPosition > 0) {
            ownerPos = new Position();
            ownerPos.setId(approvalPosition);
        }

        // Collect final approval designations (if any) by querying WF matrix for FINAL_APPROVAL_PENDING
        WorkFlowMatrix wfmatrix = egBudgetRegisterWorkflowService.getWfMatrix(
                budgetRegister.getStateType(), null, null, additionalRule, "New", null);

        if (wfmatrix != null && wfmatrix.getCurrentDesignation() != null) {
            Arrays.stream(wfmatrix.getCurrentDesignation().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toUpperCase)
                    .forEach(finalDesignationNames::add);
        }

        // If workflow not started yet, call start()
        if (budgetRegister.getState() == null) {
            if (approvalDesignation != null &&
                    finalDesignationNames.contains(approvalDesignation.trim().toUpperCase())) {
                stateValue = "Created";
            }

            // fetch matrix for start (currState = null or empty)
            wfmatrix = egBudgetRegisterWorkflowService.getWfMatrix(budgetRegister.getStateType(), null, null, additionalRule, "New", null);
            if (wfmatrix == null) {
                LOG.error("Workflow matrix missing for stateType={} (start).", budgetRegister.getStateType());
                throw new IllegalStateException("Workflow configuration missing for stateType=" + budgetRegister.getStateType());
            }

            if (stateValue.isEmpty()) stateValue = wfmatrix.getNextState();

            budgetRegister.transition().start()
                    .withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(approvalComment)
                    .withStateValue(stateValue)
                    .withDateInfo(currentDate)
                    .withOwner(ownerPos)
                    .withNextAction(wfmatrix.getNextAction())
                    .withNatureOfTask("Budget Input Approval")
                    .withCreatedBy(user.getId())
                    .withtLastModifiedBy(user.getId());

            if (LOG.isDebugEnabled())
                LOG.debug("BudgetRegister workflow started (state={}) for id={}", stateValue, budgetRegister.getId());

            return;
        }

        // If workflow already started: handle CANCEL, APPROVE, PROGRESS (FORWARD) default
        if ("CANCEL".equalsIgnoreCase(workFlowAction)) {
            stateValue = "CANCELLED";
            budgetRegister.transition().end()
                    .withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(approvalComment)
                    .withStateValue(stateValue)
                    .withDateInfo(currentDate)
                    .withNextAction("")
                    .withNatureOfTask("Budget Input Approval");

            if (LOG.isDebugEnabled())
                LOG.debug("BudgetRegister cancelled id={}", budgetRegister.getId());
            return;
        }

        if ("APPROVE".equalsIgnoreCase(workFlowAction)) {
            // fetch matrix for current state
            String currStateVal = (budgetRegister.getCurrentState() != null) ? budgetRegister.getCurrentState().getValue() : null;
            wfmatrix = egBudgetRegisterWorkflowService.getWfMatrix(budgetRegister.getStateType(), null, null, additionalRule, currStateVal, null);
            if (wfmatrix == null) {
                LOG.error("Workflow matrix missing for stateType={} currentState={}", budgetRegister.getStateType(), currStateVal);
                throw new IllegalStateException("Workflow configuration missing for APPROVE for state " + currStateVal);
            }
            if (stateValue.isEmpty()) stateValue = wfmatrix.getNextState();

            budgetRegister.transition().end()
                    .withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(approvalComment)
                    .withStateValue(stateValue)
                    .withDateInfo(currentDate)
                    .withNextAction(wfmatrix.getNextAction())
                    .withNatureOfTask("Budget Input Approval");

            if (LOG.isDebugEnabled())
                LOG.debug("BudgetRegister approved id={}, nextState={}", budgetRegister.getId(), stateValue);
            return;
        }

        // Default branch: PROGRESS / FORWARD
        if (approvalDesignation != null &&
                finalDesignationNames.contains(approvalDesignation.trim().toUpperCase())) {
            stateValue = "FINAL_APPROVAL_PENDING";
        }

        String currStateVal = (budgetRegister.getCurrentState() != null) ? budgetRegister.getCurrentState().getValue() : null;
        wfmatrix = egBudgetRegisterWorkflowService.getWfMatrix(budgetRegister.getStateType(), null, null, additionalRule, currStateVal, null);
        if (wfmatrix == null) {
            LOG.error("Workflow matrix missing for stateType={} currentState={}", budgetRegister.getStateType(), currStateVal);
            throw new IllegalStateException("Workflow configuration missing for PROGRESS for state " + currStateVal);
        }

        if (stateValue.isEmpty()) stateValue = wfmatrix.getNextState();

        budgetRegister.transition().progressWithStateCopy()
                .withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComment)
                .withStateValue(stateValue)
                .withDateInfo(currentDate)
                .withOwner(ownerPos)
                .withNextAction(wfmatrix.getNextAction())
                .withNatureOfTask("Budget Input Approval");

        if (LOG.isDebugEnabled())
            LOG.debug("BudgetRegister progressed id={}, nextState={}, ownerPos={}", budgetRegister.getId(), stateValue,
                    (ownerPos != null ? ownerPos.getId() : null));
    }




    /**
     * Creates a new budget register or progresses an existing one
     * through the configured workflow.
     *
     * <p>
     * This method:
     * <ol>
     *     <li>Initializes budget register status.</li>
     *     <li>Persists the register.</li>
     *     <li>Executes workflow transition.</li>
     *     <li>Updates workflow-based status.</li>
     *     <li>Saves the final workflow state.</li>
     * </ol>
     * </p>
     *
     * @param budgetRegister budget register entity
     * @param approvalPosition next approver position
     * @param approvalComment workflow comments
     * @param additionalRule additional workflow rule
     * @param workFlowAction workflow action to execute
     * @param approvalDesignation approver designation
     * @return persisted budget register with updated workflow state
     */

    @Transactional
    public BudgetRegister create(final BudgetRegister budgetRegister,
                                 final Long approvalPosition,
                                 final String approvalComment,
                                 final String additionalRule,
                                 final String workFlowAction,
                                 final String approvalDesignation) {

        LOG.info("Creating/transitioning BudgetRegister workflowAction={} by user={}",
                workFlowAction, securityUtils.getCurrentUser().getUsername());

        final User user = securityUtils.getCurrentUser();
        final Date currentDate = new Date();

        // --- 1️⃣ Set base status for new entries ---
        EgwStatus status;
        if (budgetRegister.getId() == null) {
            // first time creation
            status = egwStatusDAO.getStatusByModuleAndCode(FinancialConstants.BUDGET_MODULE, FinancialConstants.BUDGET_CREATED_NEW);
            if (status == null)
                throw new ValidationException(new ValidationError("status", "Status 'NEW' not configured in egw_status for module 'Budget'"));
            budgetRegister.setStatus(status);
            budgetRegister.setCreatedDate(currentDate);
            budgetRegister.setCreatedBy(user.getId());
        }

        // --- 2️⃣ Persist first (required before workflow start) ---
        budgetRegisterWorkflowRepository.save(budgetRegister);
        budgetRegisterWorkflowRepository.flush(); // ensure ID generated

        // --- 3️⃣ Apply workflow transition ---
        try {
            createBudgetRegisterWorkflowTransition(
                    budgetRegister,
                    approvalPosition,
                    approvalComment,
                    additionalRule,
                    workFlowAction,
                    approvalDesignation
            );
        } catch (Exception e) {
            LOG.error("Error while transitioning budget register workflow", e);
//            throw new ValidationException( "Workflow transition failed: " + e.getMessage());
        }

        // --- 4️⃣ Update egw_status based on workflow action ---
        EgwStatus newStatus = null;
        switch (workFlowAction.toUpperCase()) {
            case "APPROVE":
                newStatus = egwStatusDAO.getStatusByModuleAndCode("BudgetRegister", "Approved");
                break;
            case "REJECT":
                newStatus = egwStatusDAO.getStatusByModuleAndCode("BudgetRegister", "Rejected");
                break;
            case "CANCEL":
                newStatus = egwStatusDAO.getStatusByModuleAndCode("BudgetRegister", "Cancelled");
                break;
            case "START":
            case "FORWARD":
            default:
                newStatus = egwStatusDAO.getStatusByModuleAndCode("BudgetRegister", "Created");
                break;
        }
        budgetRegister.setStatus(newStatus);

        // --- 5️⃣ Persist final state after workflow transition ---
        budgetRegister.setLastModifiedBy(user.getId());
        budgetRegister.setLastModifiedDate(currentDate);
        BudgetRegister saved = budgetRegisterWorkflowRepository.save(budgetRegister);

        LOG.info("BudgetRegister [id={}, number={}] saved with status={} and workflow state={}",
                saved.getId(), saved.getBudgetRegisterNumber(),
                newStatus != null ? newStatus.getCode() : "N/A",
                saved.getState() != null ? saved.getState().getValue() : "no-state");

        return saved;
    }

    /**
     * Initializes audit information for a newly created budget register.
     *
     * <p>
     * Populates creator and last modified details before persisting.
     * </p>
     *
     * @param budgetRegister budget register to initialize
     */

    public void initiateBudgetRegisterWf(BudgetRegister budgetRegister) {

        final User user = securityUtils.getCurrentUser();
        final Date currentDate = new Date();

        budgetRegister.setCreatedBy(user.getId());
        budgetRegister.setCreatedDate(currentDate);

        budgetRegister.setLastModifiedBy(user.getId());
        budgetRegister.setLastModifiedDate(currentDate);

        budgetRegisterWorkflowRepository.save(budgetRegister);

    }


    public String generateBudgetRegisterNumber(String finYearRange) {
        Long seq = budgetRegisterWorkflowRepository.getNextBudgetRegisterSequence();
        return String.format("BR-%s-%04d", finYearRange, seq);
    }


    public List<BudgetRegister> findByFinancialYears(CFinancialYear currentFy, CFinancialYear nextFy) {

        return budgetRegisterWorkflowRepository.findByCurrentFinancialYearAndFinancialYear(currentFy, nextFy);

    }

    public BudgetRegister findLatestByFinancialYears(CFinancialYear currentFy, CFinancialYear nextFy) {
        return budgetRegisterWorkflowRepository.findTopByCurrentFinancialYearAndFinancialYearOrderByIdDesc(currentFy, nextFy);
    }

    /**
     * Retrieves all budget registers ordered by budget register number.
     *
     * @return list of budget registers
     */

    public List<BudgetRegister> findBudgetRegisters() {
        List<BudgetRegister> budgetRegisters =  budgetRegisterWorkflowRepository.findAll(new Sort(Sort.Direction.DESC, "budgetRegisterNumber"));

        budgetRegisters.stream().map(budgetRegister -> {

//            EmployeeInfo employeeInfo =  this.microServiceUtil.getEmployeeById(budgetRegister.getCreatedBy());
//            if (employeeInfo != null) {
//                budgetRegister.setCreatedByUser;
//            }

            return budgetRegister;
        }).collect(Collectors.toList());

        return budgetRegisters;

    }

    /**
     * Retrieves a budget register using its unique register number.
     *
     * @param budgetRegisterNumber budget register number
     * @return matching budget register
     */


    public BudgetRegister findBudgetRegisterByRegisterNumber(String budgetRegisterNumber) {
        return budgetRegisterWorkflowRepository.findByBudgetRegisterNumber(budgetRegisterNumber);
    }

    /**
     * Retrieves a budget register by its identifier.
     *
     * @param id budget register identifier
     * @return matching budget register
     */

    public BudgetRegister findOne(Long id) {
        return budgetRegisterWorkflowRepository.findOne(id);
    }


    /**
     * Legacy workflow transition implementation for budget registers.
     *
     * <p>
     * Supports workflow actions such as forward, approve, reject,
     * revert, cancel, and DMA forwarding.
     * </p>
     *
     * <p>
     * This method is retained for backward compatibility with the
     * existing workflow implementation.
     * </p>
     *
     * @param budgetRegister budget register under workflow
     * @param approvalPosition next approver position
     * @param approvalComment workflow comments
     * @param additionalRule additional workflow rule
     * @param workFlowAction workflow action
     * @param approvalDesignation approver designation
     */

    @Transactional
    public void createBudgetRegisterWorkFlowTransitionNew(final BudgetRegister budgetRegister, final Long approvalPosition, final String approvalComment, final String additionalRule, final String workFlowAction, final String approvalDesignation) {

        LOG.info("Budget Register Workflow started!");

        setStatusValues(budgetRegister, workFlowAction);


        final User user = securityUtils.getCurrentUser();

        final DateTime currentDate = new DateTime();

        Assignment wfInitiator = null;

        Map<String, String> finalDesignationNames = new HashMap<>();

        final String currentState = "";
        String stateValue = "";

        if (budgetRegister.getId() != null) {
            wfInitiator = this.getCurrentUserAssignment(budgetRegister.getCreatedBy());
        }
        if (FinancialConstants.BUTTONREJECT.equalsIgnoreCase(workFlowAction)) {
            LOG.info("BudgetWF: REJECT");
            stateValue = FinancialConstants.WORKFLOW_STATE_REJECTED;
            budgetRegister.transition().end()
                    .withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(approvalComment)
                    .withStateValue(stateValue)
                    .withDateInfo(currentDate.toDate())
                    .withOwner(wfInitiator.getPosition())
                    .withNextAction("")
                    .withNatureOfTask(FinancialConstants.WORKFLOWTYPE_BUDGET_REGISTER_DISPLAYNAME);

            budgetRegister.setStatus(egwStatusDAO.getStatusByModuleAndCode(FinancialConstants.BUDGET_MODULE, FinancialConstants.BUDGET_REJECTED_STATUS));

        } else if (FinancialConstants.BUTTONREVERT.equalsIgnoreCase(workFlowAction)) {

            String nextAction = "Correction Pending";


            stateValue = FinancialConstants.BUDGET_REVERTED;
            budgetRegister.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(approvalComment)
                    .withStateValue(stateValue)
                    .withDateInfo(currentDate.toDate())
                    .withOwner(wfInitiator.getPosition())
                    .withNextAction(nextAction)
                    .withNatureOfTask(FinancialConstants.WORKFLOWTYPE_BUDGET_REGISTER_DISPLAYNAME);

            budgetRegister.setStatus(egwStatusDAO.getStatusByModuleAndCode(FinancialConstants.BUDGET_MODULE, FinancialConstants.BUDGET_REVERTED));
        } else  {
            WorkFlowMatrix workFlowMatrix;
            Designation designation = this.getDesignationDetails(approvalDesignation);
            Position ownerPosition = new Position();
            ownerPosition.setId(approvalPosition);

            workFlowMatrix = egBudgetRegisterWorkflowService.getWfMatrix(budgetRegister.getStateType(), null, null, additionalRule, FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING, null);

            if (workFlowMatrix != null && workFlowMatrix.getCurrentDesignation() != null) {
                final List<String> finalDesignationName = Arrays.asList(workFlowMatrix.getCurrentDesignation().split(","));
                for (final String designationName : finalDesignationName) {
                    if (designationName != null && !"".equals(designationName.trim())) {
                        finalDesignationNames.put(designationName.toUpperCase(), designationName.toUpperCase());
                    }
                }
            }

            if (budgetRegister.getState() == null) {
                LOG.info("BudgetWF: state null");
//                if (designation != null && finalDesignationNames.get(designation.getName().toUpperCase()) != null) {
//                    stateValue = FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING;
//                }

                workFlowMatrix = egBudgetRegisterWorkflowService.getWfMatrix(budgetRegister.getStateType(), null, null, additionalRule, currentState, null);

                LOG.info("BudgetWF: next action: " + workFlowMatrix.getNextAction());

                if (stateValue.isEmpty()) {
                    stateValue = workFlowMatrix.getNextState();
                }

                budgetRegister.transition().start()
                        .withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComment)
                        .withStateValue(stateValue)
                        .withDateInfo(new Date())
                        .withOwner(ownerPosition)
                        .withNextAction(workFlowMatrix.getNextAction())
                        .withNatureOfTask(FinancialConstants.WORKFLOWTYPE_BUDGET_REGISTER_DISPLAYNAME)
                        .withCreatedBy(user.getId())
                        .withtLastModifiedBy(user.getId());

                budgetRegister.setStatus(egwStatusDAO.getStatusByModuleAndCode(FinancialConstants.BUDGET_MODULE, FinancialConstants.BUDGET_FORWARDED_FROM_FMO));

            } else if (FinancialConstants.BUTTONCANCEL.equalsIgnoreCase(workFlowAction)) {
                LOG.info("BudgetWF: CANCEL");
                stateValue = FinancialConstants.WORKFLOW_STATE_CANCELLED;
                budgetRegister.transition().end()
                        .withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComment)
                        .withStateValue(stateValue)
                        .withDateInfo(currentDate.toDate())
                        .withNextAction("")
                        .withNatureOfTask(FinancialConstants.WORKFLOWTYPE_BUDGET_REGISTER_DISPLAYNAME);

                budgetRegister.setStatus(egwStatusDAO.getStatusByModuleAndCode(FinancialConstants.BUDGET_MODULE, FinancialConstants.BUDGET_CANCELLED_STATUS));

            } else if (FinancialConstants.BUTTONAPPROVE.equalsIgnoreCase(workFlowAction)) {
                LOG.info("BudgetWF: APPROVE");
                workFlowMatrix = egBudgetRegisterWorkflowService.getWfMatrix(budgetRegister.getStateType(), null, null, additionalRule, budgetRegister.getCurrentState().getValue(), null);

                if (stateValue.isEmpty()) {
                    stateValue = workFlowMatrix.getNextState();
                }

                budgetRegister.transition().end()
                        .withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComment)
                        .withStateValue(stateValue)
                        .withDateInfo(new Date())
                        .withNextAction(workFlowMatrix.getNextAction())
                        .withNatureOfTask(FinancialConstants.WORKFLOWTYPE_BUDGET_REGISTER_DISPLAYNAME);

                budgetRegister.setStatus(egwStatusDAO.getStatusByModuleAndCode(FinancialConstants.BUDGET_MODULE, FinancialConstants.BUDGET_APPROVED_STATUS));

            } else if (FinancialConstants.BUTTONFORWARD.equalsIgnoreCase(workFlowAction)) {
                workFlowMatrix = egBudgetRegisterWorkflowService.getWfMatrix(budgetRegister.getStateType(), null, null, additionalRule, budgetRegister.getCurrentState().getValue(), null);

                if (stateValue.isEmpty()) {
                    stateValue = workFlowMatrix.getNextState();
                }

                EgwStatus egwStatus;

//                EmployeeInfo currentEmployee = null;
//
//                if (budgetRegister != null) {
//                    currentEmployee = microServiceUtil.getEmployeeByPositionId(budgetRegister.currentAssignee());
//                }

//                if (wfInitiator.getDesignation().getCode().equalsIgnoreCase("eo")) {
//                    // eo forward to dma
//                    egwStatus = egwStatusDAO.getStatusByModuleAndCode(FinancialConstants.BUDGET_MODULE, FinancialConstants.BUDGET_FORWARDED_FROM_EO);
//                } else  {
//                    // fmo forward to eo
//                    egwStatus = egwStatusDAO.getStatusByModuleAndCode(FinancialConstants.BUDGET_MODULE, FinancialConstants.BUDGET_FORWARDED_FROM_FMO);
//                }

                egwStatus = egwStatusDAO.getStatusByModuleAndCode(FinancialConstants.BUDGET_MODULE, FinancialConstants.BUDGET_FORWARDED_FROM_FMO);

                budgetRegister.transition().progressWithStateCopy()
                        .withSenderName(user.getUsername() + "::" + user.getName())
                        .withStateValue(stateValue)
                        .withComments(approvalComment)
                        .withDateInfo(new Date())
                        .withOwner(ownerPosition)
                        .withNextAction(workFlowMatrix.getNextAction())
                        .withNatureOfTask(FinancialConstants.WORKFLOWTYPE_BUDGET_REGISTER_DISPLAYNAME);

                budgetRegister.setStatus(egwStatus);

            } else if (FinancialConstants.BUTTONFORWARD_TO_DMA.equalsIgnoreCase(workFlowAction)) {
                workFlowMatrix = egBudgetRegisterWorkflowService.getWfMatrix(budgetRegister.getStateType(), null, null, additionalRule, budgetRegister.getCurrentState().getValue(), null);

                if (stateValue.isEmpty()) {
                    stateValue = workFlowMatrix.getNextState();
                }

                EgwStatus egwStatus;

                egwStatus = egwStatusDAO.getStatusByModuleAndCode(FinancialConstants.BUDGET_MODULE, FinancialConstants.BUDGET_FORWARDED_FROM_EO);

                budgetRegister.transition().progressWithStateCopy()
                        .withSenderName(user.getUsername() + "::" + user.getName())
                        .withStateValue(stateValue)
                        .withComments(approvalComment)
                        .withDateInfo(new Date())
                        .withOwner(ownerPosition)
                        .withNextAction(workFlowMatrix.getNextAction())
                        .withNatureOfTask(FinancialConstants.WORKFLOWTYPE_BUDGET_REGISTER_DISPLAYNAME);

                budgetRegister.setStatus(egwStatus);

            } else {
                LOG.info("BudgetWF: SOMETHING");
                LOG.info("BudgetWF: " + workFlowAction);

//                if (designation != null && finalDesignationNames.get(designation.getName().toUpperCase()) != null) {
//                    stateValue = FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING;
//                }

                workFlowMatrix = egBudgetRegisterWorkflowService.getWfMatrix(budgetRegister.getStateType(), null, null, additionalRule, budgetRegister.getCurrentState().getValue(), null);

                if (stateValue.isEmpty()) {
                    stateValue = workFlowMatrix.getNextState();
                }

                budgetRegister.transition().progressWithStateCopy()
                        .withSenderName(user.getUsername() + "::" + user.getName())
                        .withStateValue(stateValue)
                        .withDateInfo(new Date())
                        .withOwner(ownerPosition)
                        .withNextAction(workFlowMatrix.getNextAction())
                        .withNatureOfTask(FinancialConstants.WORKFLOWTYPE_BUDGET_REGISTER_DISPLAYNAME);


//                budgetRegister.setStatus(egwStatusDAO.getStatusByModuleAndCode(FinancialConstants.BUDGET_MODULE, FinancialConstants.BUDGET_APPROVED_STATUS));

            }

        }

        LOG.info("Workflow transition completed !");

        save(budgetRegister);

    }

    /**
     * Updates workflow-related status values before transition.
     *
     * @param budgetRegister budget register entity
     * @param workFlowAction workflow action being performed
     */

    private void setStatusValues(BudgetRegister budgetRegister,String workFlowAction) {
        //
    }

    /**
     * Persists the specified budget register.
     *
     * @param currentBudgetRegister budget register to save
     */
    public void save(BudgetRegister currentBudgetRegister) {
        budgetRegisterWorkflowRepository.save(currentBudgetRegister);
    }
}


