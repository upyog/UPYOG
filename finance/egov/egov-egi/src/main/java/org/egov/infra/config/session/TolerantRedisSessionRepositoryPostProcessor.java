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

package org.egov.infra.config.session;

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.log4j.Logger;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;

/**
 * LTS Migration Fix (Spring Session 3.2.6 / Redis):
 *
 * What was the issue?
 *   After upgrading spring-session-data-redis to 3.2.6, requests (including
 *   static JS such as bootbox.min.js) failed with:
 *   {@code IllegalStateException: creationTime key must not be null}
 *   at {@code RedisSessionMapper}. Undertow then failed again generating
 *   {@code /error/500} with the same exception. This showed up after the
 *   30-minute idle session expired and the user opened a page again.
 *
 * Why do we need this change?
 *   Spring Session 3 requires Redis HASH fields {@code creationTime},
 *   {@code lastAccessedTime}, and {@code maxInactiveInterval}. After idle expiry
 *   the browser cookie is still present; the HASH may already be half-cleaned
 *   (or still in the old Session 1.x/2.x field format). The HASH is not empty,
 *   so findById does not return null; RedisSessionMapper looks up String
 *   {@code creationTime}, misses it, and throws. ApplicationTenantResolverFilter
 *   calls {@code getSession(false)} on every request, so even static JS 500'd.
 *   We cannot leave that throw: the error page also reads the session and fails.
 *
 * How we solved this?
 *   A BeanPostProcessor wraps {@link RedisIndexedSessionRepository#findById}
 *   (CGLIB, so MSLogoutController can still inject the concrete type). If
 *   findById throws {@code IllegalStateException} with "key must not be null",
 *   we delete that session id from Redis and return null (session not found).
 *   The filter continues without a session instead of crashing. Registered as a
 *   static {@code @Bean} from {@link RedisHttpSessionConfiguration} so the
 *   post-processor is created early.
 *
 * What did we solve?
 *   After 30-minute expiry (or leftover incomplete hashes), page and JS requests
 *   no longer 500. The user may need to log in once; a new Session 3 hash is
 *   written on the next login.
 */
public class TolerantRedisSessionRepositoryPostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = Logger.getLogger(TolerantRedisSessionRepositoryPostProcessor.class);

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) {
        if (!(bean instanceof RedisIndexedSessionRepository) || bean.getClass().getName().contains("$$")) {
            return bean;
        }
        final RedisIndexedSessionRepository repository = (RedisIndexedSessionRepository) bean;
        final ProxyFactory factory = new ProxyFactory(repository);
        factory.setProxyTargetClass(true);
        factory.addAdvice((MethodInterceptor) invocation -> {
            if ("findById".equals(invocation.getMethod().getName()) && invocation.getArguments().length == 1) {
                try {
                    return invocation.proceed();
                } catch (final IllegalStateException ex) {
                    if (isIncompleteSessionHash(ex)) {
                        final String sessionId = String.valueOf(invocation.getArguments()[0]);
                        LOGGER.warn("LTS Migration Fix (Spring Session 3.2): incomplete Redis session hash for id "
                                + sessionId + " (" + ex.getMessage()
                                + "). Deleting stale session so the request can continue.");
                        repository.deleteById(sessionId);
                        return null;
                    }
                    throw ex;
                }
            }
            return invocation.proceed();
        });
        return factory.getProxy();
    }

    private static boolean isIncompleteSessionHash(final IllegalStateException ex) {
        final String message = ex.getMessage();
        return message != null && message.contains("key must not be null");
    }
}
