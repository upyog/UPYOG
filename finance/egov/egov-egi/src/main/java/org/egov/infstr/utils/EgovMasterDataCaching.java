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

package org.egov.infstr.utils;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PreDestroy;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.exception.MicroServiceInvalidTokenException;
import org.egov.infra.exception.MicroServiceNotAuthroizedException;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.cache.CacheException;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.PersistenceContextType;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @deprecated no longer supported
 * */
@Deprecated
@Transactional(readOnly = true)
public class EgovMasterDataCaching {
    private static final Logger LOGGER = LoggerFactory.getLogger(EgovMasterDataCaching.class);
    private static final String SQL_QUERY_TYPE = "sql";
    private static final String HQL_QUERY_TYPE = "hql";
    private static final String PATH_DELIM = "/";
    private static final String SQL_TAG_PREFIX = "sql.";
    private static final String CONFIG_FILE_SUFFIX = "_sqlconfig.xml";
    private static EmbeddedCacheManager CACHE_MANAGER;
    private static Cache masterDataCache;

    @SuppressWarnings("rawtypes")
    private static synchronized Cache getCache() {
        if (masterDataCache != null) {
            try {
                if (masterDataCache.getStatus() != null && "RUNNING".equalsIgnoreCase(masterDataCache.getStatus().toString())) {
                    return masterDataCache;
                }
            } catch (final Throwable t) {
                masterDataCache = null;
            }
        }
        try {
            final Context context = new InitialContext();
            masterDataCache = (Cache) context.lookup("java:jboss/infinispan/cache/master-data/master-data");
            if (masterDataCache != null) {
                return masterDataCache;
            }
        } catch (final Exception e) {
            LOGGER.warn("Direct JNDI cache lookup java:jboss/infinispan/cache/master-data/master-data failed, falling back to manager: {}", e.getMessage());
        }
        if (CACHE_MANAGER != null) {
            try {
                if (!CACHE_MANAGER.cacheExists("master-data")) {
                    CACHE_MANAGER.defineConfiguration("master-data", new ConfigurationBuilder().build());
                }
                masterDataCache = CACHE_MANAGER.getCache("master-data");
                return masterDataCache;
            } catch (final Exception e) {
                LOGGER.error("Error getting cache from CACHE_MANAGER", e);
                masterDataCache = CACHE_MANAGER.getCache();
                return masterDataCache;
            }
        }
        throw new ApplicationRuntimeException("Unable to initialize or obtain master-data Infinispan cache");
    }

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private MicroserviceUtils microserviceUtils;

    static {
        try {
            final Context context = new InitialContext();
            CACHE_MANAGER = (EmbeddedCacheManager) context.lookup("java:jboss/infinispan/container/master-data");
        } catch (final NamingException e) {
            throw new ApplicationRuntimeException("Error occurred while getting Cache Manager", e);
        }
    }

    private EntityManager getFreshEntityManager(boolean[] createdFresh) {
        if (entityManagerFactory != null) {
            try {
                createdFresh[0] = true;
                return entityManagerFactory.createEntityManager();
            } catch (final Exception e) {
                LOGGER.debug("Could not create EntityManager from factory", e);
            }
        }
        if (entityManager != null) {
            try {
                if (entityManager.isOpen()) {
                    createdFresh[0] = true;
                    return entityManager.getEntityManagerFactory().createEntityManager();
                }
            } catch (final Exception e) {
                LOGGER.debug("Could not create EntityManager from injected entityManager factory", e);
            }
        }
        createdFresh[0] = false;
        return entityManager;
    }

    /**
     * This method load the data for given sqlTagName and puts it in Cache.
     * @param sqlTagName the sql tag name
     * @return List
     * @throws ApplicationRuntimeException the eGOV runtime exception
     */

    public List get(final String sqlTagName) throws ApplicationRuntimeException {
        final String temp[] = sqlTagName.split("-");
        final String domainName = ApplicationThreadLocals.getDomainName();
        final String applName = temp[0];
        List<Object> dataList = null;
        HashMap<String, Object> cacheValuesHashMap = new HashMap<String, Object>();

        try {
            cacheValuesHashMap = (HashMap<String, Object>) getCache()
                    .get(applName + PATH_DELIM + domainName + PATH_DELIM + sqlTagName);
            if (cacheValuesHashMap != null)
                dataList = (List<Object>) cacheValuesHashMap.get(sqlTagName);

            if (dataList == null || dataList.isEmpty()) {
                if(sqlTagName.equalsIgnoreCase("egi-department")){
                    dataList = this.loadFromMicroService();
                }else{
                    final String type = EGovConfig
                            .getProperty(applName + CONFIG_FILE_SUFFIX, "type", EMPTY, SQL_TAG_PREFIX + sqlTagName).trim();
                    if (type.equalsIgnoreCase("java")) {
                        final String className = EGovConfig.getProperty(applName + CONFIG_FILE_SUFFIX, "class", EMPTY,
                                SQL_TAG_PREFIX + sqlTagName);
                        final String methodName = EGovConfig.getProperty(applName + CONFIG_FILE_SUFFIX, "method", EMPTY,
                                SQL_TAG_PREFIX + sqlTagName);
                        final String parametertype = EGovConfig.getProperty(applName + CONFIG_FILE_SUFFIX, "parametertype", EMPTY,
                                SQL_TAG_PREFIX + sqlTagName);
                        final String parametervalue = EGovConfig.getProperty(applName + CONFIG_FILE_SUFFIX, "parametervalue", EMPTY,
                                SQL_TAG_PREFIX + sqlTagName);
                        if (isNotBlank(className) && isNotBlank(methodName))
                            dataList = loadJavaAPIMasterDataList(className, methodName, parametertype.split(","),
                                    parametervalue.split(","));
                        else
                            throw new ApplicationRuntimeException("ClassName and MethodName should be mentioned for " + type + " in "
                                    + applName + CONFIG_FILE_SUFFIX);
                    } else if (type.equalsIgnoreCase(HQL_QUERY_TYPE) || type.equalsIgnoreCase(SQL_QUERY_TYPE)) {
                        final String query = EGovConfig.getProperty(applName + CONFIG_FILE_SUFFIX, "query", EMPTY,
                                SQL_TAG_PREFIX + sqlTagName);
                        if (!query.equalsIgnoreCase(EMPTY))
                            dataList = loadQLMasterData(query, type);
                        else
                            throw new ApplicationRuntimeException(
                                    "Query should be mentioned for " + type + " in " + applName + CONFIG_FILE_SUFFIX);
                    } else
                        throw new ApplicationRuntimeException("This type (" + type + ") is not supported for " + sqlTagName);
                }
                final HashMap<String, Object> hm = new HashMap<String, Object>();
                hm.put(sqlTagName, dataList);
                getCache().put(applName + PATH_DELIM + domainName + PATH_DELIM + sqlTagName, hm);
            } else
                LOGGER.info("EgovMasterDataCaching: Got directly from cache, not from db");


        } catch (final MicroServiceInvalidTokenException | MicroServiceNotAuthroizedException e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching", e);
            if(e instanceof MicroServiceInvalidTokenException || e instanceof MicroServiceNotAuthroizedException)
                throw e;
            else
                throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching", e);
        }
        return dataList;
    }

    /**
     * This method load the data for given sqlTagName and puts it in Cache.
     * @param sqlTagName the sql tag name
     * @return Map
     * @throws ApplicationRuntimeException the eGOV runtime exception
     */

    public Map getMap(final String sqlTagName) throws ApplicationRuntimeException {
        Map dataMap = new HashMap();
        final String temp[] = sqlTagName.split("-");
        final String applName = temp[0];
        final String domainName = ApplicationThreadLocals.getDomainName();
        final String type = EGovConfig.getProperty(applName + CONFIG_FILE_SUFFIX, "type", EMPTY, SQL_TAG_PREFIX + sqlTagName)
                .trim();
        try {
            if (type.trim().equalsIgnoreCase(SQL_QUERY_TYPE)) {
                final List dataList = get(sqlTagName);
                if (dataList != null) {
                    final Iterator itr = dataList.iterator();
                    LabelValueBean obj = null;
                    while (itr.hasNext()) {
                        obj = (LabelValueBean) itr.next();
                        dataMap.put(Integer.toString(obj.getId()), obj.getName());
                        obj = null;
                    }
                }
            } else if (type.equalsIgnoreCase(HQL_QUERY_TYPE))
                throw new ApplicationRuntimeException("getMap() is not supported for HQL query");
            else if (type.equalsIgnoreCase("java")) {
                final String className = EGovConfig.getProperty(applName + CONFIG_FILE_SUFFIX, "class", EMPTY,
                        SQL_TAG_PREFIX + sqlTagName);
                final String methodName = EGovConfig.getProperty(applName + CONFIG_FILE_SUFFIX, "method", EMPTY,
                        SQL_TAG_PREFIX + sqlTagName);
                final String parametertype = EGovConfig.getProperty(applName + CONFIG_FILE_SUFFIX, "parametertype", EMPTY,
                        SQL_TAG_PREFIX + sqlTagName);
                final String parametervalue = EGovConfig.getProperty(applName + CONFIG_FILE_SUFFIX, "parametervalue", EMPTY,
                        SQL_TAG_PREFIX + sqlTagName);
                if (isNotBlank(className) && isNotBlank(methodName))
                    dataMap = loadJavaAPIMasterDataMap(className, methodName, parametertype.split(","),
                            parametervalue.split(","));
                else
                    throw new ApplicationRuntimeException(
                            "ClassName and MethodName should be mentioned for " + type + " in " + applName + CONFIG_FILE_SUFFIX);
                final HashMap<String, Object> hm = new HashMap<String, Object>();
                hm.put(sqlTagName, dataMap);
                getCache().put(applName + PATH_DELIM + domainName + PATH_DELIM + sqlTagName, hm);
            } else
                throw new ApplicationRuntimeException("This type (" + type + ") is not supported for " + sqlTagName);
        } catch (final CacheException e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching getMap", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching getMap", e);
        }
        return dataMap;
    }

    public static EmbeddedCacheManager getCACHE_MANAGER() {
        return CACHE_MANAGER;
    }

    /**
     * This method removes the data from cache for given sqlTagName.
     * @param sqlTagName the sql tag name
     * @return void
     * @throws ApplicationRuntimeException the eGOV runtime exception
     */

    public static void removeFromCache(final String sqlTagName) throws ApplicationRuntimeException {
        try {
            final String temp[] = sqlTagName.split("-");
            final String domainName = ApplicationThreadLocals.getDomainName();
            final String applName = temp[0];
            getCache().remove(applName + PATH_DELIM + domainName + PATH_DELIM + sqlTagName);
        } catch (final CacheException e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching removeFromCache", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching removeFromCache", e);
        }
    }

    /**
     * This method loads the data for Hql and Sql queries.
     * @param query the query
     * @param type the type
     * @return List
     * @throws ApplicationRuntimeException the eGOV runtime exception
     */

    private List loadQLMasterData(final String query, final String type) throws ApplicationRuntimeException {
        List dataList = null;

        if (type.equalsIgnoreCase(HQL_QUERY_TYPE))
            dataList = queryByHibernate(query);
        else if (type.equalsIgnoreCase(SQL_QUERY_TYPE))
            dataList = queryByJdbc(query);

        return dataList;
    }

    /**
     * This method executes a Java API method and returns the data.
     * @param className the class name
     * @param methodName the method name
     * @param parametertype the parameter type
     * @param parametervalue the parameter value
     * @return List
     * @throws ApplicationRuntimeException the eGOV runtime exception
     */

    private List loadJavaAPIMasterDataList(final String className, final String methodName, final String[] parametertype,
            final String[] parametervalue) throws ApplicationRuntimeException {
        List list = null;
        Object obj_name[] = null;
        Object bean = null;
        Class cls = null;
        Method method = null;

        try {
            cls = Class.forName(className);
            bean = cls.newInstance();

            if (isNotBlank(parametertype[0]) && isNotBlank(parametervalue[0])) {
                obj_name = loadMethodArguments(parametertype, parametervalue);
                method = cls.getMethod(methodName, getParameterTypes(parametertype));
                list = (List) method.invoke(bean, obj_name);

            } else {
                method = cls.getMethod(methodName);
                list = (List) method.invoke(bean);
            }
        } catch (final ClassNotFoundException e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataList", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataList", e);
        } catch (final NoSuchMethodException e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataList", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataList", e);
        } catch (final IllegalAccessException e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataList", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataList", e);
        } catch (final InvocationTargetException e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataList", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataList", e);
        } catch (final InstantiationException e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataList", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataList", e);
        }

        return list;
    }

    /**
     * This method executes a Java API method and returns the data Map.
     * @param className the class name
     * @param methodName the method name
     * @param parametertype the parameter type
     * @param parametervalue the parameter value
     * @return Map
     * @throws ApplicationRuntimeException the eGOV runtime exception
     */

    private Map loadJavaAPIMasterDataMap(final String className, final String methodName, final String[] parametertype,
            final String[] parametervalue) throws ApplicationRuntimeException {

        Map map = null;
        Object obj_name[] = null;
        Object bean = null;
        Class cls = null;
        Method method = null;

        try {

            cls = Class.forName(className);
            bean = cls.newInstance();

            if (isNotBlank(parametertype[0]) && isNotBlank(parametervalue[0])) {
                obj_name = loadMethodArguments(parametertype, parametervalue);
                method = cls.getMethod(methodName, getParameterTypes(parametertype));

                map = (Map) method.invoke(bean, obj_name);

            } else {

                method = cls.getMethod(methodName);
                map = (Map) method.invoke(bean);
            }
        } catch (final ClassNotFoundException e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataMap", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataMap", e);
        } catch (final NoSuchMethodException e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataMap", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataMap", e);
        } catch (final IllegalAccessException e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataMap", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataMap", e);
        } catch (final InvocationTargetException e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataMap", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataMap", e);
        } catch (final InstantiationException e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataMap", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching loadJavaAPIMasterDataMap", e);
        }

        return map;
    }

    /**
     * This method returns class array for the string parameter types.
     * @param parametertype the parametertype
     * @return Class[]
     * @throws ClassNotFoundException the class not found exception
     */

    private Class[] getParameterTypes(final String[] parametertype) throws ClassNotFoundException {
        final Class[] paramTypes = new Class[parametertype.length];
        for (int i = 0; i < parametertype.length; i++)
            paramTypes[i] = Class.forName(parametertype[i].trim());

        return paramTypes;
    }

    /**
     * This method loads the method arguments.
     * @param parametertype the parametertype
     * @param parametervalue the parametervalue
     * @return Object[]
     */

    private Object[] loadMethodArguments(final String[] parametertype, final String[] parametervalue) {
        final Object obj_name[] = new Object[parametertype.length];

        try {

            for (int i = 0; i < parametertype.length; i++) {
                if (isNotBlank(parametervalue[i]) && isNotBlank(parametertype[i]))
                    if (parametertype[i].trim().equalsIgnoreCase("java.lang.Integer"))
                        obj_name[i] = Integer.valueOf(parametervalue[i]);
                    else if (parametertype[i].trim().equalsIgnoreCase("java.lang.Long"))
                        obj_name[i] = Long.valueOf(parametervalue[i]);
                    else if (parametertype[i].trim().equalsIgnoreCase("java.lang.Double"))
                        obj_name[i] = Double.valueOf(parametervalue[i]);
                    else if (parametertype[i].trim().equalsIgnoreCase("java.lang.String"))
                        obj_name[i] = parametervalue[i];
                    else
                        throw new ApplicationRuntimeException("This " + parametertype[i] + " datatype is not supported");
            }
        } catch (final Exception e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching loadMethodArguments", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching loadMethodArguments", e);
        }
        return obj_name;
    }

    /**
     * This method executes a hibernate query.
     * @param query the query
     * @return List
     * @throws ApplicationRuntimeException the eGOV runtime exception
     */

    private List queryByHibernate(final String query) throws ApplicationRuntimeException {
        List list = null;
        final boolean[] createdFresh = new boolean[1];
        final EntityManager em = getFreshEntityManager(createdFresh);
        try {
            list = em.createQuery(query).getResultList();
            if (list != null) {
                /*
                 * LTS Migration Fix (Struts 7 / Infinispan Cache ByteBuddy Proxy Fix):
                 * Master data objects fetched via short-lived EntityManagers were leaving un-initialized ByteBuddy proxies 
                 * in Infinispan cache. When accessed in Struts 7 JSP tags, these detached proxies caused OGNL access errors or LazyInitializationExceptions.
                 * Unproxying and initializing each entity before returning ensures fully materialized objects are cached and accessible across HTTP requests.
                 */
                for (int i = 0; i < list.size(); i++) {
                    Object item = list.get(i);
                    if (item != null) {
                        Hibernate.initialize(item);
                        list.set(i, Hibernate.unproxy(item));
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching queryByHibernate", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching queryByHibernate", e);
        } finally {
            if (createdFresh[0] && em != null && em.isOpen()) {
                em.close();
            }
        }
        return list;
    }

    private Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    /**
     * This method executes a sql query.
     * @param query the query
     * @return List
     * @throws ApplicationRuntimeException the eGOV runtime exception
     */

    private List queryByJdbc(final String query) throws ApplicationRuntimeException {
        List resultlist = null;
        List returnList = null;
        final boolean[] createdFresh = new boolean[1];
        final EntityManager em = getFreshEntityManager(createdFresh);
        try {
            resultlist = em.createNativeQuery(query).getResultList();
            if (resultlist != null)
                returnList = resultSetToArrayList(resultlist);
        } catch (final Exception e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching queryByJdbc", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching queryByJdbc", e);
        } finally {
            if (createdFresh[0] && em != null && em.isOpen()) {
                em.close();
            }
        }
        return returnList;
    }

    /**
     * This method returns a list of LabelValueBean using the resultList object.
     * @param resultList the rs
     * @return List
     * @throws ApplicationRuntimeException the eGOV runtime exception
     */

    private List resultSetToArrayList(final List<Object[]> resultList) throws ApplicationRuntimeException {

        final List list = new ArrayList();
        LabelValueBean labelValueBean = null;
        BigDecimal id;
        try {
            for (final Object[] objArr : resultList) {
                labelValueBean = new LabelValueBean();
                id = (BigDecimal) objArr[0];
                labelValueBean.setId(id.intValue());
                labelValueBean.setName((String) objArr[1]);
                list.add(labelValueBean);
            }
        } catch (final ObjectNotFoundException e) {
            LOGGER.error("Error occurred in EgovMasterDataCaching resultSetToArrayList", e);
            throw new ApplicationRuntimeException("Error occurred in EgovMasterDataCaching resultSetToArrayList", e);
        }
        return list;
    }

    private List loadFromMicroService(){

        List<Department> deptList = this.microserviceUtils.getDepartments();
        return deptList;
    }

    @PreDestroy
    public void destroy() {
        if (CACHE_MANAGER != null && CACHE_MANAGER.isDefaultRunning())
            CACHE_MANAGER.stop();
    }
}