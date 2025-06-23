package org.egov.finance.inbox.service.config;

import java.util.List;

import org.egov.finance.inbox.entity.Budget;
import org.egov.finance.inbox.entity.BudgetDetail;
import org.egov.finance.inbox.entity.BudgetReAppropriationMisc;
import org.egov.finance.inbox.entity.CVoucherHeader;
import org.egov.finance.inbox.entity.EgBillregister;
import org.egov.finance.inbox.service.DefaultInboxRenderServiceImpl;
import org.egov.finance.inbox.workflow.entity.StateAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Configuration
public class InboxRenderServiceAutoConfig {@PersistenceContext
    private EntityManager entityManager;

    private final ApplicationContext applicationContext;

    public InboxRenderServiceAutoConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void registerInboxRenderServices() {
        List<Class<? extends StateAware>> types = List.of(
                Budget.class,
                BudgetDetail.class,
                BudgetReAppropriationMisc.class,
                CVoucherHeader.class,
                EgBillregister.class
                // Add more as needed
        );

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();

        for (Class<? extends StateAware> clazz : types) {
            String beanName = clazz.getSimpleName() + "InboxRenderService";

            GenericBeanDefinition definition = new GenericBeanDefinition();
            definition.setBeanClass(DefaultInboxRenderServiceImpl.class);
            definition.getConstructorArgumentValues().addIndexedArgumentValue(0, clazz);
            definition.getConstructorArgumentValues().addIndexedArgumentValue(1, entityManager);
            definition.setAutowireCandidate(true);
            registry.registerBeanDefinition(beanName, definition);
        }
    }
}