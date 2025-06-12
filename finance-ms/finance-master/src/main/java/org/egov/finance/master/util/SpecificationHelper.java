/**
 * Created on Jun 12, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.util;

import java.util.Collection;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

public final class SpecificationHelper {

	 private SpecificationHelper() {
	    }

	   
	    private static <T, Y> Path<Y> getPath(Root<T> root, String fieldPath, Class<Y> type) {
	        String[] parts = fieldPath.split("\\.");
	        Path<?> path = root.get(parts[0]);
	        for (int i = 1; i < parts.length; i++) {
	            path = path.get(parts[i]);
	        }
	        @SuppressWarnings("unchecked")
	        Path<Y> typedPath = (Path<Y>) path;
	        return typedPath;
	    }

	    public static <T, Y> Specification<T> equal(String fieldPath, Y value) {
	        return (root, query, cb) ->
	                value == null ? cb.conjunction() : cb.equal(getPath(root, fieldPath, value.getClass()), value);
	    }

	    public static <T> Specification<T> likeIgnoreCase(String fieldPath, String value) {
	        return (root, query, cb) ->
	                (value == null || value.isEmpty()) ? cb.conjunction()
	                        : cb.like(cb.lower(getPath(root, fieldPath, String.class)), "%" + value.toLowerCase() + "%");
	    }

	    public static <T, Y> Specification<T> in(String fieldPath, Collection<Y> values, Class<Y> type) {
	        return (root, query, cb) ->
	                (values == null || values.isEmpty()) ? cb.conjunction()
	                        : getPath(root, fieldPath, type).in(values);
	    }

	    public static <T, Y extends Comparable<? super Y>> Specification<T> greaterThan(String fieldPath, Y value, Class<Y> type) {
	        return (root, query, cb) ->
	                value == null ? cb.conjunction()
	                        : cb.greaterThan(getPath(root, fieldPath, type), value);
	    }

	    public static <T, Y extends Comparable<? super Y>> Specification<T> lessThan(String fieldPath, Y value, Class<Y> type) {
	        return (root, query, cb) ->
	                value == null ? cb.conjunction()
	                        : cb.lessThan(getPath(root, fieldPath, type), value);
	    }

	    public static <T, Y extends Comparable<? super Y>> Specification<T> between(String fieldPath, Y from, Y to, Class<Y> type) {
	        return (root, query, cb) -> {
	            if (from == null && to == null) return cb.conjunction();
	            Path<Y> path = getPath(root, fieldPath, type);
	            if (from != null && to != null) {
	                return cb.between(path, from, to);
	            } else if (from != null) {
	                return cb.greaterThanOrEqualTo(path, from);
	            } else {
	                return cb.lessThanOrEqualTo(path, to);
	            }
	        };
	    }
    
    
 /*USAGE EXAMPLE
 * 
spec = spec.and(equalTo("parentId.id", funcCriteria.getParentId()));
spec = spec.and(equalTo("scheme.id", funcCriteria.getSchemeId()));
spec = spec.and(equalTo("createdBy.id", someUserId));
spec = spec.and(SpecificationHelper.greaterThan("landArea", new BigDecimal("2000"), BigDecimal.class));
spec = spec.and(SpecificationHelper.lessThan("superBuiltUpArea", new BigDecimal("10000"), BigDecimal.class));
spec = spec.and(SpecificationHelper.between("createdTime", 1704067200000L, 1706745599999L, Long.class));

*
*/


}