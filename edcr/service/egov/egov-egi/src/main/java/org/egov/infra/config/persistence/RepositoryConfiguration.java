package org.egov.infra.config.persistence;


import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;

@Configuration
/*
@EnableJpaRepositories(basePackages = "org.egov.**.repository",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ElasticsearchRepository.class),
        repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
 */
@EnableJpaRepositories(
		 basePackages = {"org.egov.infra.admin.master.repository",
				 "org.egov.infra.admin.master.repository",
		            "org.egov.infra.admin.common.repository",
		            "org.egov.infra.filestore.repository",
		            "org.egov.infra.security.audit.repository",
		            "org.egov.infra.security.token.repository",
		            "org.egov.edcr.repository",
		            "org.egov.commons.repository.bpa"},          
	    bootstrapMode = BootstrapMode.DEFERRED 

	  
	)
@EnableJpaAuditing
public class RepositoryConfiguration {

    @Autowired
    private UserService userService;

    @Bean
    public AuditorAware<User> springSecurityAwareAuditor() {
    	return () -> java.util.Optional.ofNullable(userService.getCurrentUser());
    }
}
