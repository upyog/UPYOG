/**
 * Created on Jun 23, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.service.config;


import org.egov.finance.inbox.entity.Budget;
import org.egov.finance.inbox.entity.BudgetDetail;
import org.egov.finance.inbox.entity.BudgetReAppropriationMisc;
import org.egov.finance.inbox.entity.CVoucherHeader;
import org.egov.finance.inbox.entity.EgBillregister;
import org.egov.finance.inbox.service.DefaultInboxRenderServiceImpl;
import org.egov.finance.inbox.service.InboxRenderService;
import org.egov.finance.inbox.workflow.entity.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import java.util.List;

@Configuration
public class InboxRenderServiceAutoConfig {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static org.springframework.beans.factory.config.BeanFactoryPostProcessor dynamicInboxRenderRegistration() {
        return beanFactory -> {
            DefaultListableBeanFactory registry = (DefaultListableBeanFactory) beanFactory;
            
            //Lster additon 
           // Challan.class,
           // ContraJournalVoucher.class,
           // DishonorCheque.class, ReceiptHeader.class,ReceiptVoucher.class,
//          //EmployeeGrievance.class,
            //   Paymentheader.class,
           
            List<Class<? extends StateAware>> types = List.of(
                Budget.class,
                BudgetDetail.class,
                BudgetReAppropriationMisc.class,
                CVoucherHeader.class,
                EgBillregister.class
            );

            for (Class<? extends StateAware> clazz : types) {
                String beanName = clazz.getSimpleName() + "InboxRenderService";
                InboxRenderService<?> service = new DefaultInboxRenderServiceImpl<>(clazz);
                registry.registerSingleton(beanName, service);
            }
        };
    }

}
