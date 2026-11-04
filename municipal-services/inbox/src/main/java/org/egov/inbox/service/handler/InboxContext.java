package org.egov.inbox.service.handler;

import lombok.Builder;
import lombok.Data;
import org.egov.common.contract.request.RequestInfo;
import org.egov.inbox.web.model.InboxSearchCriteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * InboxContext stores shared inbox processing data
 * used across handlers and assembler.
 */
@Data
@Builder
public class InboxContext {

    private InboxSearchCriteria criteria;

    private RequestInfo requestInfo;

    private HashMap<String, String> statusIdNameMap;

    private Map<String, String> srvMap;

    @Builder.Default
    private List<String> businessKeys = new ArrayList<>();

    @Builder.Default
    private Boolean searchResultEmpty = Boolean.FALSE;

    private Integer totalCount;

    /**
     * Checks whether search result is empty.
     *
     * @return true if search result is empty
     */
    public boolean isSearchResultEmpty() {
        return Boolean.TRUE.equals(this.searchResultEmpty);
    }

    /**
     * Updates search result empty flag.
     *
     * @param empty empty flag
     */
    public void setSearchResultEmpty(boolean empty) {
        this.searchResultEmpty = empty;
    }

    /**
     * Returns business keys list.
     * Initializes list if null.
     *
     * @return business keys
     */
    public List<String> getBusinessKeys() {
        if (this.businessKeys == null)
            this.businessKeys = new ArrayList<>();
        return this.businessKeys;
    }

    /**
     * Adds single business key.
     *
     * @param key business key
     */
    public void addBusinessKey(String key) {
        getBusinessKeys().add(key);
    }

    /**
     * Adds multiple business keys.
     *
     * @param keys business keys
     */
    public void addBusinessKeys(List<String> keys) {
        if (keys == null || keys.isEmpty())
            return;

        getBusinessKeys().addAll(keys);
    }

    /**
     * Adds module search criteria entry.
     *
     * @param key criteria key
     * @param value criteria value
     */
    public void addModuleSearchCriteria(String key, Object value) {
        if (this.criteria.getModuleSearchCriteria() == null)
            this.criteria.setModuleSearchCriteria(new HashMap<>());

        this.criteria.getModuleSearchCriteria().put(key, value);
    }

    /**
     * Removes module search criteria entry.
     *
     * @param key criteria key
     */
    public void removeModuleSearchCriteria(String key) {
        if (this.criteria == null || this.criteria.getModuleSearchCriteria() == null)
            return;

        this.criteria.getModuleSearchCriteria().remove(key);
    }
}
