package org.upyog.wms.validator;

import static org.upyog.wms.constants.ErrorCodes.*;
import static org.upyog.wms.constants.WmsConstants.*;
import static org.upyog.wms.util.WmsUtil.isBlank;
import static org.upyog.wms.util.WmsUtil.isLive;
import static org.upyog.wms.util.WmsUtil.isTrue;
import static org.upyog.wms.util.WmsUtil.nn;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.upyog.wms.constants.FundType;
import org.upyog.wms.util.WmsUtil;
import org.upyog.wms.web.models.ActionPlan;
import org.upyog.wms.web.models.Beneficiary;
import org.upyog.wms.web.models.Component;
import org.upyog.wms.web.models.Document;
import org.upyog.wms.web.models.FundDetail;
import org.upyog.wms.web.models.Location;
import org.upyog.wms.web.models.ProjectDetail;
import org.upyog.wms.web.models.Recommendation;
import org.upyog.wms.web.models.ValidationWarning;
import org.upyog.wms.web.models.mdms.ActionPlanConfig;
import org.upyog.wms.web.models.mdms.DocumentTypeConfig;
import org.upyog.wms.web.models.mdms.MdmsData;

/**
 * Mandatory, conditional, master-data and reconciliation rules applied when a plan is submitted
 * (FR-AP-04: a draft does not enforce them). Pure Java with no Spring or HTTP dependency, so it is fast
 * and fully unit-testable. Section numbers refer to the FRS.
 */
public final class SubmissionRules {

    private static final Pattern PLAIN_TEXT = Pattern.compile(PLAIN_TEXT_REGEX);
    private static final Pattern ALPHABETIC = Pattern.compile(ALPHABETIC_REGEX);

    private final MdmsData mdms;
    private final ActionPlanConfig cfg;
    private final ZoneId zone;
    private final LocalDate today;

    public SubmissionRules(MdmsData mdms, ZoneId zone, LocalDate today) {
        this.mdms = mdms;
        this.cfg = mdms.config();
        this.zone = zone;
        this.today = today;
    }

    /** Runs every rule, adds errors to {@code errors} and returns non-blocking warnings. */
    public List<ValidationWarning> validate(ActionPlan plan, ValidationErrors errors) {
        List<ValidationWarning> warnings = new ArrayList<>();
        planDetails(plan, errors);
        approvalAndFund(plan, errors);
        components(plan, errors, warnings);
        documents(plan, errors);
        financialSummary(plan, errors);
        if (!isTrue(plan.getDeclarationAccepted())) {
            errors.add(DECLARATION_REQUIRED, "declarationAccepted",
                    "Accept the declaration that the entered data matches the approval evidence");
        }
        return warnings;
    }

    private void planDetails(ActionPlan plan, ValidationErrors errors) {
        requiredMaster(plan.getFinancialYear(), mdms.financialYears(), "financialYear", "Financial Year", errors);
        if (isBlank(plan.getProjectId())) {
            errors.add(REQUIRED, "projectId", "Project ID is mandatory");
        }
        plainText(plan.getProjectName(), true, "projectName", "Project Name", errors);

        List<ProjectDetail> details = live(plan.getProjectDetails(), ProjectDetail::getIsActive);
        if (details.size() != 1) {
            errors.add(REQUIRED, "projectDetails", "Exactly one set of project details is required");
        }
        for (int i = 0; i < details.size(); i++) {
            ProjectDetail d = details.get(i);
            String p = "projectDetails[" + i + "].";
            requiredMaster(d.getPurposeCode(), mdms.purposes(), p + "purposeCode", "Purpose of Project", errors);
            if (CODE_OTHERS.equals(d.getPurposeCode())) {
                alphabetic(d.getPurposeOther(), true, p + "purposeOther", "Specify Purpose", errors);
            }
            if (d.getStartDate() == null || d.getEndDate() == null) {
                errors.add(REQUIRED, p + "timeRange", "Time Range (start and end date) is mandatory");
            } else if (d.getEndDate() < d.getStartDate()) {
                errors.add(INVALID_DATE, p + "endDate", "End date must not be before start date");
            }
            requiredMaster(d.getTypeOfWork(), mdms.typesOfWork(), p + "typeOfWork", "Type of Work", errors);
            requiredMaster(d.getWorkCategory(), mdms.workCategories(), p + "workCategory", "Work Category", errors);
        }

        if (!isTrue(plan.getAdminSanction())) {
            errors.add(REQUIRED, "adminSanction", "Administrative Sanction must be confirmed");
        }
        if (plan.getTechnicalSanction() == null) {
            errors.add(REQUIRED, "technicalSanction", "Select Technical Sanction (Yes / No)");
        } else if (cfg.technicalSanctionRequired() && !plan.getTechnicalSanction()) {
            errors.add(REQUIRED, "technicalSanction", "Technical Sanction is required to register the plan");
        }

        List<Recommendation> recs = live(plan.getRecommendations(), Recommendation::getIsActive);
        if (recs.isEmpty()) {
            errors.add(REQUIRED, "recommendations", "Recommended By is mandatory");
        }
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < recs.size(); i++) {
            Recommendation r = recs.get(i);
            String p = "recommendations[" + i + "].";
            requiredMaster(r.getRecommendedBy(), mdms.recommendedBy(), p + "recommendedBy", "Recommended By", errors);
            if (CODE_OTHERS.equals(r.getRecommendedBy())) {
                alphabetic(r.getRecommendedByName(), true, p + "recommendedByName", "Recommended by Name", errors);
            } else if (r.getRecommendedBy() != null && !seen.add(r.getRecommendedBy())) {
                errors.add(DUPLICATE, p + "recommendedBy", "Recommended By '" + r.getRecommendedBy()
                        + "' is entered more than once");
            }
            documentsOf(r.getDocuments(), DOC_OWNER_RECOMMENDATION, p + "documents", errors);
        }

        List<Location> locations = live(plan.getLocations(), Location::getIsActive);
        if (locations.size() != 1) {
            errors.add(REQUIRED, "locations", "Exactly one location (State, District) is required");
        }
        boolean mla = seen.contains(RECOMMENDED_BY_MLA);
        boolean mp = seen.contains(RECOMMENDED_BY_MP);
        for (int i = 0; i < locations.size(); i++) {
            Location l = locations.get(i);
            String p = "locations[" + i + "].";
            requiredMaster(l.getStateCode(), mdms.states(), p + "stateCode", "State", errors);
            requiredMaster(l.getDistrictCode(), mdms.districts().keySet(), p + "districtCode", "District", errors);
            String districtState = mdms.districts().get(l.getDistrictCode());
            if (districtState != null && l.getStateCode() != null && !districtState.equals(l.getStateCode())) {
                errors.add(INVALID_MASTER, p + "districtCode", "District does not belong to the selected State");
            }
            alphabetic(l.getMlaConstituency(), mla, p + "mlaConstituency", "MLA Constituency", errors);
            alphabetic(l.getMpConstituency(), mp, p + "mpConstituency", "MP Constituency", errors);
        }
        beneficiaries(plan.getBeneficiaries(), "beneficiaries", errors);
    }

    private void approvalAndFund(ActionPlan plan, ValidationErrors errors) {
        if (plan.getOfflineApprovalDate() == null) {
            errors.add(REQUIRED, "offlineApprovalDate", "Date of Approval is mandatory");
        } else {
            LocalDate approval = Instant.ofEpochMilli(plan.getOfflineApprovalDate()).atZone(zone).toLocalDate();
            if (approval.isAfter(today)) {
                errors.add(INVALID_DATE, "offlineApprovalDate", "Date of Approval cannot be a future date");
            }
        }
        alphabetic(plan.getApprovingAuthority(), true, "approvingAuthority", "Approved By", errors);

        requiredMaster(plan.getSchemeCode(), mdms.schemes().keySet(), "schemeCode", "Scheme", errors);
        Set<String> subSchemes = mdms.schemes().getOrDefault(plan.getSchemeCode(), Set.of());
        if (!subSchemes.isEmpty()) {
            requiredMaster(plan.getSubSchemeCode(), subSchemes, "subSchemeCode", "Sub-Scheme", errors);
        }
        positiveAmount(plan.getApprovedAmount(), "approvedAmount", "Approved Project Amount", errors);

        Optional<FundType> source = fundSource(plan.getFundSource(), "fundSource", errors);
        source.ifPresent(s -> fundRows(s, plan.getFundDetails(), plan.getApprovedAmount(), "fundDetails",
                "Approved Project Amount", errors));

        if (plan.getTotalComponents() == null || plan.getTotalComponents() < 1) {
            errors.add(REQUIRED, "totalComponents", "Total No. of Components must be at least 1");
        }
    }

    private void components(ActionPlan plan, ValidationErrors errors, List<ValidationWarning> warnings) {
        List<Component> active = activeComponents(plan);
        if (active.isEmpty()) {
            errors.add(REQUIRED, "components", "At least one Component is required");
            return;
        }
        if (cfg.enforceComponentCount() && plan.getTotalComponents() != null
                && active.size() != plan.getTotalComponents()) {
            errors.add(COMPONENT_COUNT_MISMATCH, "totalComponents", "Components entered (" + active.size()
                    + ") must match Total No. of Components (" + plan.getTotalComponents() + ")");
        }
        Optional<FundType> planSource = FundType.from(plan.getFundSource());
        Set<String> numbers = new HashSet<>();
        Map<String, Integer> normalisedNames = new HashMap<>();

        for (int i = 0; i < active.size(); i++) {
            Component c = active.get(i);
            String p = "components[" + i + "].";
            plainText(c.getComponentName(), true, p + "componentName", "Component Name", errors);
            requiredMaster(c.getCategory(), mdms.componentCategories().keySet(), p + "category", "Category", errors);
            if (!isBlank(c.getSubCategory())) {
                Set<String> subs = mdms.componentCategories().getOrDefault(c.getCategory(), Set.of());
                if (!subs.contains(c.getSubCategory())) {
                    errors.add(INVALID_MASTER, p + "subCategory", "Sub-Category is not valid for the Category");
                }
            }
            positiveAmount(c.getApprovedAmount(), p + "approvedAmount", "Approved Component Amount", errors);

            Optional<FundType> compSource = fundSource(c.getFundSource(), p + "fundSource", errors);
            if (compSource.isPresent() && planSource.isPresent() && compSource.get() != FundType.PARTIAL
                    && !planSource.get().allowsRow(compSource.get())) {
                errors.add(FUND_SOURCE_CONFLICT, p + "fundSource", "Fund Source " + c.getFundSource()
                        + " is not allowed when the plan is funded by " + plan.getFundSource());
            } else if (compSource.isPresent() && planSource.isPresent() && compSource.get() == FundType.PARTIAL
                    && planSource.get() != FundType.PARTIAL) {
                errors.add(FUND_SOURCE_CONFLICT, p + "fundSource",
                        "Partial funding is allowed only when the plan Fund Source is Partial");
            }
            compSource.ifPresent(s -> fundRows(s, c.getFundDetails(), c.getApprovedAmount(), p + "fundDetails",
                    "Approved Component Amount", errors));
            beneficiaries(c.getBeneficiaries(), p + "beneficiaries", errors);
            documentsOf(c.getDocuments(), DOC_OWNER_COMPONENT, p + "documents", errors);

            if (c.getComponentNumber() != null && !numbers.add(c.getComponentNumber())) {
                errors.add(DUPLICATE, p + "componentNumber", "Component " + c.getComponentNumber()
                        + " is repeated in this plan");
            }
            if (c.getComponentName() != null) {
                String key = c.getComponentName().trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
                Integer first = normalisedNames.putIfAbsent(key, i);
                if (first != null) {
                    warnings.add(new ValidationWarning("WMS_AP_SIMILAR_COMPONENT_NAME", p + "componentName",
                            "Component name is the same as components[" + first + "]; please check it is not a duplicate"));
                }
            }
        }
    }

    private void documents(ActionPlan plan, ValidationErrors errors) {
        List<Document> docs = live(plan.getDocuments(), Document::getIsActive);
        documentsOf(docs, DOC_OWNER_ACTION_PLAN, "documents", errors);
        Set<String> present = docs.stream().map(Document::getDocumentType).collect(Collectors.toSet());

        mdms.documentTypes().values().stream()
                .filter(t -> DOC_OWNER_ACTION_PLAN.equals(t.owner()) && t.required())
                .filter(t -> !DOC_TYPE_TECHNICAL_SANCTION.equals(t.code()))
                .filter(t -> !present.contains(t.code()))
                .forEach(t -> errors.add(DOCUMENT_REQUIRED, "documents." + t.code(), "Upload " + t.code()));
        if (isTrue(plan.getTechnicalSanction()) && !present.contains(DOC_TYPE_TECHNICAL_SANCTION)) {
            errors.add(DOCUMENT_REQUIRED, "documents." + DOC_TYPE_TECHNICAL_SANCTION,
                    "Upload the Technical Sanction document");
        }
        long supporting = docs.stream()
                .map(d -> mdms.documentTypes().get(d.getDocumentType()))
                .filter(t -> t != null && t.supporting())
                .count();
        if (supporting > cfg.maxSupportingDocuments()) {
            errors.add(DOCUMENT_LIMIT, "documents", "A maximum of " + cfg.maxSupportingDocuments()
                    + " supporting documents can be uploaded (found " + supporting + ")");
        }
    }

    private void financialSummary(ActionPlan plan, ValidationErrors errors) {
        BigDecimal approved = plan.getApprovedAmount();
        List<Component> active = activeComponents(plan);
        if (approved == null || active.isEmpty()) {
            return;
        }
        BigDecimal componentTotal = WmsUtil.sum(active, Component::getApprovedAmount);
        int cmp = componentTotal.compareTo(approved);
        if (cfg.fullAllocationRequired() && cmp != 0) {
            errors.add(COMPONENT_TOTAL_MISMATCH, "components", "Total of Components Approved Amount ("
                    + plain(componentTotal) + ") should match the Approved Project Amount (" + plain(approved)
                    + "). Unallocated balance: " + plain(approved.subtract(componentTotal)));
        } else if (!cfg.fullAllocationRequired() && cmp > 0) {
            errors.add(COMPONENT_OVER_ALLOCATED, "components", "Total of Components Approved Amount ("
                    + plain(componentTotal) + ") exceeds the Approved Project Amount (" + plain(approved) + ")");
        }

        // Funding-wise reconciliation: component funding per type vs plan funding per type
        Map<FundType, BigDecimal> planByType = totalsByType(live(plan.getFundDetails(), FundDetail::getIsActive));
        Map<FundType, BigDecimal> compByType = totalsByType(active.stream()
                .flatMap(c -> live(c.getFundDetails(), FundDetail::getIsActive).stream())
                .toList());
        for (FundType type : List.of(FundType.GOVT_GRANT, FundType.OSR)) {
            BigDecimal planAmount = planByType.getOrDefault(type, BigDecimal.ZERO);
            BigDecimal compAmount = compByType.getOrDefault(type, BigDecimal.ZERO);
            int c = compAmount.compareTo(planAmount);
            if ((cfg.fullAllocationRequired() && c != 0) || (!cfg.fullAllocationRequired() && c > 0)) {
                errors.add(FUND_TOTAL_MISMATCH, "components.fundDetails." + type, "Components' " + type
                        + " funding (" + plain(compAmount) + ") does not match the plan's " + type + " funding ("
                        + plain(planAmount) + ")");
            }
        }
    }

    private void fundRows(FundType source, List<FundDetail> rows, BigDecimal total, String path, String totalLabel,
                          ValidationErrors errors) {
        List<FundDetail> live = live(rows, FundDetail::getIsActive);
        if (live.isEmpty()) {
            errors.add(REQUIRED, path, "Enter the fund details for " + source);
            return;
        }
        Set<FundType> typesPresent = new HashSet<>();
        for (int i = 0; i < live.size(); i++) {
            FundDetail f = live.get(i);
            String p = path + "[" + i + "].";
            Optional<FundType> type = FundType.from(f.getFundType()).filter(t -> t != FundType.PARTIAL);
            if (type.isEmpty()) {
                errors.add(INVALID_MASTER, p + "fundType", "Fund type must be GOVT_GRANT or OSR");
            } else if (!source.allowsRow(type.get())) {
                errors.add(FUND_SOURCE_CONFLICT, p + "fundType", type.get() + " funding is not allowed for Fund Source "
                        + source);
            } else {
                typesPresent.add(type.get());
                Set<String> allowed = type.get() == FundType.GOVT_GRANT ? mdms.governmentGrants()
                        : mdms.ownSourceRevenues();
                requiredMaster(f.getSourceCode(), allowed, p + "sourceCode",
                        type.get() == FundType.GOVT_GRANT ? "Government Grant" : "Own Source Revenue", errors);
            }
            plainText(f.getFundName(), true, p + "fundName", "Grant / Fund Name", errors);
            positiveAmount(f.getAmount(), p + "amount", "Amount", errors);
        }
        if (source == FundType.PARTIAL && !(typesPresent.contains(FundType.GOVT_GRANT)
                && typesPresent.contains(FundType.OSR))) {
            errors.add(REQUIRED, path, "Partial funding needs both a Government Grant and an OSR row");
        }
        if (total != null) {
            BigDecimal sum = WmsUtil.sum(live, FundDetail::getAmount);
            if (sum.compareTo(total) != 0) {
                errors.add(FUND_TOTAL_MISMATCH, path, "Fund break-up total (" + plain(sum) + ") must equal the "
                        + totalLabel + " (" + plain(total) + ")");
            }
        }
    }

    private Optional<FundType> fundSource(String value, String field, ValidationErrors errors) {
        if (isBlank(value)) {
            errors.add(REQUIRED, field, "Fund Source is mandatory");
            return Optional.empty();
        }
        Optional<FundType> type = FundType.from(value);
        if (type.isEmpty()) {
            errors.add(INVALID_MASTER, field, "Fund Source must be GOVT_GRANT, OSR or PARTIAL");
        }
        return type;
    }

    private void beneficiaries(List<Beneficiary> list, String path, ValidationErrors errors) {
        List<Beneficiary> live = live(list, Beneficiary::getIsActive);
        for (int i = 0; i < live.size(); i++) {
            plainText(live.get(i).getName(), true, path + "[" + i + "].name", "Beneficiary Name", errors);
        }
    }

    private void documentsOf(List<Document> docs, String owner, String path, ValidationErrors errors) {
        List<Document> live = live(docs, Document::getIsActive);
        for (int i = 0; i < live.size(); i++) {
            Document d = live.get(i);
            String p = path + "[" + i + "].";
            DocumentTypeConfig type = mdms.documentTypes().get(d.getDocumentType());
            if (type == null || !owner.equals(type.owner())) {
                errors.add(INVALID_MASTER, p + "documentType", "Document type '" + d.getDocumentType()
                        + "' is not allowed here");
            }
            if (isBlank(d.getFileStoreId())) {
                errors.add(REQUIRED, p + "fileStoreId", "Upload the file");
            }
        }
    }

    private static Map<FundType, BigDecimal> totalsByType(List<FundDetail> rows) {
        Map<FundType, BigDecimal> totals = new EnumMap<>(FundType.class);
        rows.forEach(r -> FundType.from(r.getFundType()).ifPresent(t -> totals.merge(t,
                r.getAmount() == null ? BigDecimal.ZERO : r.getAmount(), BigDecimal::add)));
        return totals;
    }

    private static List<Component> activeComponents(ActionPlan plan) {
        return nn(plan.getComponents()).stream()
                .filter(c -> isLive(c.getIsActive()) && !COMPONENT_INACTIVE.equals(c.getStatus()))
                .toList();
    }

    static <T> List<T> live(List<T> list, java.util.function.Function<T, Boolean> isActive) {
        return nn(list).stream().filter(t -> isLive(isActive.apply(t))).toList();
    }

    private static void requiredMaster(String value, Collection<String> allowed, String field, String label,
                                       ValidationErrors errors) {
        if (isBlank(value)) {
            errors.add(REQUIRED, field, label + " is mandatory");
        } else if (!allowed.contains(value)) {
            errors.add(INVALID_MASTER, field, label + " '" + value + "' is not an active master value");
        }
    }

    private static void plainText(String value, boolean required, String field, String label, ValidationErrors errors) {
        if (isBlank(value)) {
            if (required) {
                errors.add(REQUIRED, field, label + " is mandatory");
            }
        } else if (!PLAIN_TEXT.matcher(value).matches()) {
            errors.add(INVALID_FORMAT, field, label + " must be plain text (letters, numbers and . , ( ) / & ' - _ : #)");
        }
    }

    private static void alphabetic(String value, boolean required, String field, String label, ValidationErrors errors) {
        if (isBlank(value)) {
            if (required) {
                errors.add(REQUIRED, field, label + " is mandatory");
            }
        } else if (!ALPHABETIC.matcher(value).matches()) {
            errors.add(INVALID_FORMAT, field, label + " must contain letters only");
        }
    }

    private static void positiveAmount(BigDecimal value, String field, String label, ValidationErrors errors) {
        if (value == null) {
            errors.add(REQUIRED, field, label + " is mandatory");
        } else if (value.signum() <= 0) {
            errors.add(INVALID_AMOUNT, field, label + " must be greater than zero");
        }
    }

    private static String plain(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }
}
