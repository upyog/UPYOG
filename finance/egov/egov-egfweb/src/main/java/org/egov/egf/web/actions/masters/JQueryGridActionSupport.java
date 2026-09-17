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
package org.egov.egf.web.actions.masters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.egov.egf.web.actions.masters.JQueryGridActionSupport.MultipleSearchFilter.Rule;
import org.egov.infra.persistence.utils.Page;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.services.PersistenceService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * This will supports Action classes to integrate with jqgrid, to do pagination, searching, filtering (single and group) and ajax
 * form saving
 **/
public abstract class JQueryGridActionSupport extends BaseFormAction {
    protected static final String ADD = "add";
    protected static final String EDIT = "edit";
    protected static final String DELETE = "del";
    private static final long serialVersionUID = 1L;
    protected Integer id;
    protected String oper;

    @Autowired
    @Qualifier("persistenceService")
    protected PersistenceService persistenceService;

    private boolean _search;
    private Integer rows;
    private Integer page;
    private String ord;
    private String searchField;
    private String searchString;
    private String searchOper;
    private String sidx;
    private String sord;
    private String filters;
    private Integer totalPages;
    private Integer totalRecords;

    @Override
    public Object getModel() {
        return null;
    }

    /**
     * Returns Page {@link Page} result for the given hibernate model class with a mandatory keyFieldName, keyFieldValue.
     * Internally this method will apply all filtering and ordering according to the value arrived from jqgrid.
     **/
    protected Page getPagedResult(final Class<?> clazz, final String keyFieldName, final Object keyFieldValue) {
        CriteriaBuilder cb = persistenceService.getSession().getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<?> countRoot = countQuery.from(clazz);
        countQuery.select(cb.count(countRoot))
                .where(cb.equal(countRoot.get(keyFieldName), keyFieldValue));
        totalRecords = persistenceService.getSession().createQuery(countQuery).uniqueResult().intValue();

        CriteriaQuery<Object> dataQuery = cb.createQuery(Object.class);
        Root<?> root = dataQuery.from(clazz);
        Predicate keyPredicate = cb.equal(root.get(keyFieldName), keyFieldValue);
        Predicate searchPredicate = buildSearchPredicate(cb, root);
        if (searchPredicate != null)
            dataQuery.where(cb.and(keyPredicate, searchPredicate));
        else
            dataQuery.where(keyPredicate);
        dataQuery.orderBy(buildOrderBy(cb, root));

        jakarta.persistence.Query q = persistenceService.getSession().createQuery(dataQuery);
        q.setFirstResult((page - 1) * rows);
        q.setMaxResults(rows);
        return new Page((org.hibernate.query.Query) q, page, rows, totalRecords);
    }

    /**
     * Use to send response text directly to the HttpServletResponse
     **/
    protected void sendAJAXResponse(final String response) {
        try {
            HttpServletResponse httpResponse = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            Writer httpResponseWriter = httpResponse.getWriter();
            IOUtils.write(response, httpResponseWriter);
        } catch (final IOException e) {
            LOG.error("Error occurred while processing Ajax response", e);
        }
    }

    /**
     * Populates full jqgrid json data string with the given model json data which is used by jqgrid to populated data-table.
     **/
    protected String constructJqGridResponse(final String jsonData) {
        return new StringBuilder().
                append("{\"page\":").append(page).
                append(",\"total\":").append(getTotalPages()).
                append(",\"records\":").append(totalRecords).
                append(",\"rows\":").append(jsonData).append("}").toString();
    }

    /**
     * This will get invoked only if user uses search on jqgrid. This is capable applying jqgrid single and group filtering
     * searches.
     **/
    private Predicate buildSearchPredicate(CriteriaBuilder cb, Root<?> root) {
        if (!_search) return null;
        if (StringUtils.isBlank(filters))
            return toPredicate(cb, root, searchField, searchOper, searchString);
        final MultipleSearchFilter msf = getMultiSearchFilter();
        List<Predicate> predicates = new java.util.ArrayList<>();
        for (final Rule rule : msf.getRules()) {
            Predicate p = toPredicate(cb, root, rule.getField(), rule.getOp(), rule.getData());
            if (p != null) predicates.add(p);
        }
        if (predicates.isEmpty()) return null;
        Predicate[] arr = predicates.toArray(new Predicate[0]);
        return "AND".equals(msf.getGroupOp()) ? cb.and(arr) : cb.or(arr);
    }

    /**
     * Used when search comes from jqgrid group filtering
     **/


    /**
     * Implementing Action need to override this method incase the search criteria contains non string values. eg: <code>
     * protected Object convertType (String searchField, String searchValue) {
     * Object convertedValue = null;
     * if (searchField.equals("accountNumber")) {
     * convertedValue = new BigDecimal(searchValue);
     * }
     * return convertedValue;
     * }
     * </code>
     **/
    protected Object convertValueType(final String searchField, final String searchValue) {
        return searchValue;
    }

    /**
     * Used to convert jqgrid search operator to hibernate restriction.
     **/
    @SuppressWarnings("unchecked")
    private Predicate toPredicate(CriteriaBuilder cb, Root<?> root, String field, String oper, String value) {
        final Object converted = convertValueType(field, value);
        if ("eq".equals(oper)) return cb.equal(root.get(field), converted);
        if ("ne".equals(oper)) return cb.notEqual(root.get(field), converted);
        if (converted instanceof String) {
            if ("bw".equals(oper)) return cb.like(cb.lower(root.get(field)), value.toLowerCase() + "%");
            if ("cn".equals(oper)) return cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
            if ("ew".equals(oper)) return cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase());
            if ("bn".equals(oper)) return cb.notLike(cb.lower(root.get(field)), value.toLowerCase() + "%");
            if ("en".equals(oper)) return cb.notLike(cb.lower(root.get(field)), "%" + value.toLowerCase());
            if ("nc".equals(oper)) return cb.notLike(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
            if ("in".equals(oper)) return root.get(field).in((Object[]) value.split(","));
            if ("ni".equals(oper)) return cb.not(root.get(field).in((Object[]) value.split(",")));
        } else {
            Comparable c = (Comparable) converted;
            if ("lt".equals(oper)) return cb.lessThan(root.get(field), c);
            if ("le".equals(oper)) return cb.lessThanOrEqualTo(root.get(field), c);
            if ("gt".equals(oper)) return cb.greaterThan(root.get(field), c);
            if ("ge".equals(oper)) return cb.greaterThanOrEqualTo(root.get(field), c);
        }
        return null;
    }

    /**
     * Used to convert jqgrid order by to hibernate Order by
     **/
    private Order buildOrderBy(CriteriaBuilder cb, Root<?> root) {
        final String orderBy = sord == null ? ord : sord;
        final String orderByField = sidx == null ? searchField : sidx;
        if ("asc".equals(orderBy))
            return cb.asc(root.get(orderByField));
        else
            return cb.desc(root.get(orderByField));
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setRows(final Integer rows) {
        this.rows = rows;
    }

    public void setPage(final Integer page) {
        this.page = page;
    }

    public void setOrd(final String ord) {
        this.ord = ord;
    }

    public void setSearchField(final String searchField) {
        this.searchField = searchField;
    }

    public void setSearchString(final String searchString) {
        this.searchString = searchString;
    }

    public void setSearchOper(final String searchOper) {
        this.searchOper = searchOper;
    }

    public void set_search(final boolean search) {
        this._search = search;
    }

    public void setSidx(final String sidx) {
        this.sidx = sidx;
    }

    public void setSord(final String sord) {
        this.sord = sord;
    }

    public void setOper(final String oper) {
        this.oper = oper;
    }

    public void setFilters(final String filters) {
        this.filters = filters;
    }

    /**
     * This method will convert the incoming group search filter to java class {@link MultipleSearchFilter}
     **/
    private MultipleSearchFilter getMultiSearchFilter() {
        return new GsonBuilder().create().fromJson(filters, MultipleSearchFilter.class);
    }

    /**
     * Returns total number of pages
     **/
    private Integer getTotalPages() {
        if (totalPages == null) {
            totalPages = totalRecords / rows;
            if (totalRecords % rows != 0)
                totalPages++;
        }
        return totalPages;
    }

    /**
     * Inner class which used with {@link Gson} to convert the group search filter json to a Java class.
     **/
    class MultipleSearchFilter {
        private String groupOp;
        private List<Rule> rules;

        public String getGroupOp() {
            return groupOp;
        }

        public void setGroupOp(final String groupOp) {
            this.groupOp = groupOp;
        }

        public List<Rule> getRules() {
            return rules;
        }

        public void setRules(final List<Rule> rules) {
            this.rules = rules;
        }

        class Rule {
            private String field;
            private String op;
            private String data;

            public String getField() {
                return field;
            }

            public void setField(final String field) {
                this.field = field;
            }

            public String getOp() {
                return op;
            }

            public void setOp(final String op) {
                this.op = op;
            }

            public String getData() {
                return data;
            }

            public void setData(final String data) {
                this.data = data;
            }
        }
    }

}