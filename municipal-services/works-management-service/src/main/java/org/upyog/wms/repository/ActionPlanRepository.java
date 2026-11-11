package org.upyog.wms.repository;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.upyog.wms.repository.querybuilder.ActionPlanQueryBuilder;
import org.upyog.wms.repository.rowmapper.ActionPlanRowMapper;
import org.upyog.wms.web.models.ActionPlan;
import org.upyog.wms.web.models.ActionPlanSearchCriteria;
import org.upyog.wms.web.models.Beneficiary;
import org.upyog.wms.web.models.Component;
import org.upyog.wms.web.models.Document;
import org.upyog.wms.web.models.FundDetail;
import org.upyog.wms.web.models.Location;
import org.upyog.wms.web.models.ProjectDetail;
import org.upyog.wms.web.models.Recommendation;

/**
 * Read side of the service (writes go through Kafka to egov-persister).
 * Search = one paged header query + one batched query per child table, so the cost does not grow with
 * the number of plans on the page (no N+1, no row explosion from joining many 1:many tables).
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ActionPlanRepository {

    /** Postgres allows 32767 bind parameters; stay well below it. */
    private static final int IN_CHUNK = 1000;

    private final JdbcTemplate jdbcTemplate;
    private final ActionPlanQueryBuilder queryBuilder;
    private final ActionPlanRowMapper rowMapper;

    public record SearchResult(List<ActionPlan> actionPlans, long totalCount) {
    }

    /** Child row together with the id of its parent, used only for grouping. */
    private record Child<T>(String parentId, T item) {
    }

    public SearchResult search(ActionPlanSearchCriteria criteria, boolean withChildren) {
        List<Object> params = new ArrayList<>();
        String sql = queryBuilder.getSearchQuery(criteria, params);
        long[] total = {0L};
        RowMapper<ActionPlan> header = rowMapper.header();
        List<ActionPlan> plans = jdbcTemplate.query(sql, (rs, i) -> {
            total[0] = rs.getLong("full_count");
            return header.mapRow(rs, i);
        }, params.toArray());

        if (plans.isEmpty() && criteria.getOffset() != null && criteria.getOffset() > 0) {
            List<Object> countParams = new ArrayList<>();
            Long count = jdbcTemplate.queryForObject(queryBuilder.getCountQuery(criteria, countParams), Long.class,
                    countParams.toArray());
            total[0] = count == null ? 0L : count;
        }
        if (withChildren && !plans.isEmpty()) {
            loadChildren(plans);
        }
        return new SearchResult(plans, total[0]);
    }

    private void loadChildren(List<ActionPlan> plans) {
        List<String> planIds = plans.stream().map(ActionPlan::getId).toList();

        Map<String, List<ProjectDetail>> projectDetails = children("eg_wms_ap_project_detail", "actionplanid",
                planIds, "createdtime, id", rowMapper.projectDetail());
        Map<String, List<Location>> locations = children("eg_wms_ap_location", "actionplanid",
                planIds, "createdtime, id", rowMapper.location());
        Map<String, List<Recommendation>> recommendations = children("eg_wms_ap_recommendation", "actionplanid",
                planIds, "createdtime, id", rowMapper.recommendation());
        Map<String, List<Component>> components = children("eg_wms_ap_component", "actionplanid",
                planIds, "componentnumber, id", rowMapper.component());
        Map<String, List<Document>> planDocuments = children("eg_wms_ap_document", "actionplanid",
                planIds, "createdtime, id", rowMapper.document());

        List<FundDetail> funds = rows("eg_wms_ap_fund_detail", "actionplanid", planIds, "createdtime, id",
                rowMapper.fundDetail());
        List<Beneficiary> beneficiaries = rows("eg_wms_ap_beneficiary", "actionplanid", planIds,
                "createdtime, id", rowMapper.beneficiary());

        List<String> recommendationIds = ids(recommendations, Recommendation::getId);
        Map<String, List<Document>> recommendationDocs = children("eg_wms_ap_recommendation_document",
                "recommendationid", recommendationIds, "createdtime, id", rowMapper.document());
        List<String> componentIds = ids(components, Component::getId);
        Map<String, List<Document>> componentDocs = children("eg_wms_ap_component_document", "componentid",
                componentIds, "createdtime, id", rowMapper.document());

        Map<String, List<FundDetail>> planFunds = funds.stream().filter(f -> f.getComponentId() == null)
                .collect(groupingBy(FundDetail::getActionPlanId));
        Map<String, List<FundDetail>> componentFunds = funds.stream().filter(f -> f.getComponentId() != null)
                .collect(groupingBy(FundDetail::getComponentId));
        Map<String, List<Beneficiary>> planBeneficiaries = beneficiaries.stream()
                .filter(b -> b.getComponentId() == null).collect(groupingBy(Beneficiary::getActionPlanId));
        Map<String, List<Beneficiary>> componentBeneficiaries = beneficiaries.stream()
                .filter(b -> b.getComponentId() != null).collect(groupingBy(Beneficiary::getComponentId));

        for (ActionPlan plan : plans) {
            String id = plan.getId();
            plan.setProjectDetails(projectDetails.getOrDefault(id, List.of()));
            plan.setLocations(locations.getOrDefault(id, List.of()));
            plan.setDocuments(planDocuments.getOrDefault(id, List.of()));
            plan.setFundDetails(planFunds.getOrDefault(id, List.of()));
            plan.setBeneficiaries(planBeneficiaries.getOrDefault(id, List.of()));

            List<Recommendation> recs = recommendations.getOrDefault(id, List.of());
            recs.forEach(r -> r.setDocuments(recommendationDocs.getOrDefault(r.getId(), List.of())));
            plan.setRecommendations(recs);

            List<Component> comps = components.getOrDefault(id, List.of());
            comps.forEach(c -> {
                c.setFundDetails(componentFunds.getOrDefault(c.getId(), List.of()));
                c.setBeneficiaries(componentBeneficiaries.getOrDefault(c.getId(), List.of()));
                c.setDocuments(componentDocs.getOrDefault(c.getId(), List.of()));
            });
            plan.setComponents(comps);
        }
    }

    /** Child rows grouped by parent id. */
    private <T> Map<String, List<T>> children(String table, String parentColumn, List<String> parentIds,
                                              String orderBy, RowMapper<T> mapper) {
        if (parentIds.isEmpty()) {
            return Map.of();
        }
        RowMapper<Child<T>> withParent = (rs, i) -> new Child<>(rs.getString(parentColumn), mapper.mapRow(rs, i));
        return query(table, parentColumn, parentIds, orderBy, withParent).stream()
                .collect(groupingBy(Child::parentId, mapping(Child::item, toList())));
    }

    private <T> List<T> rows(String table, String parentColumn, List<String> parentIds, String orderBy,
                             RowMapper<T> mapper) {
        return parentIds.isEmpty() ? List.of() : query(table, parentColumn, parentIds, orderBy, mapper);
    }

    private <T> List<T> query(String table, String parentColumn, List<String> parentIds, String orderBy,
                              RowMapper<T> mapper) {
        List<T> result = new ArrayList<>();
        for (int from = 0; from < parentIds.size(); from += IN_CHUNK) {
            List<String> chunk = parentIds.subList(from, Math.min(from + IN_CHUNK, parentIds.size()));
            List<Object> params = new ArrayList<>();
            String sql = queryBuilder.getChildQuery(table, parentColumn, chunk, orderBy, params);
            result.addAll(jdbcTemplate.query(sql, mapper, params.toArray()));
        }
        return result;
    }

    private static <T> List<String> ids(Map<String, List<T>> grouped, Function<T, String> id) {
        return grouped.values().stream().flatMap(Collection::stream).map(id).toList();
    }
}
