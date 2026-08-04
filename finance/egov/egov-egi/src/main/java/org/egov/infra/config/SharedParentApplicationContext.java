package org.egov.infra.config;

import jakarta.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

/**
 * Spring 6 compatibility bridge for the legacy eGov shared parent context.
 *
 * Spring 5 and earlier ContextLoader supported:
 *
 *   locatorFactorySelector
 *   parentContextKey
 *
 * Spring 6 removed that lookup mechanism. This listener restores only the
 * parent-context behaviour required by the existing eGov EAR architecture.
 */
public class SharedParentApplicationContext extends ContextLoaderListener {

    private static final Object LOCK = new Object();

    private static volatile ConfigurableApplicationContext parentContext;

    @Override
    protected ApplicationContext loadParentContext(ServletContext servletContext) {

        ConfigurableApplicationContext context = parentContext;

        if (context == null) {
            synchronized (LOCK) {
                context = parentContext;

                if (context == null) {
                    ClassPathXmlApplicationContext locatorContext =
                            new ClassPathXmlApplicationContext(
                                    "classpath*:config/spring/applicationContext-erp.xml");

                    ApplicationContext erpContext =
                            locatorContext.getBean(
                                    "erpApplicationContext",
                                    ApplicationContext.class);

                    if (!(erpContext instanceof ConfigurableApplicationContext)) {
                        locatorContext.close();
                        throw new IllegalStateException(
                                "erpApplicationContext is not a ConfigurableApplicationContext");
                    }

                    context = (ConfigurableApplicationContext) erpContext;
                    parentContext = context;

                    /*
                     * Do not close locatorContext here.
                     *
                     * applicationContext-erp.xml owns the erpApplicationContext
                     * bean. Closing the locator would also destroy that bean.
                     */
                }
            }
        }

        return context;
    }
}