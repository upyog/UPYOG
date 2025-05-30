

package org.egov.finance.master.config.Flyway;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Configuration
public class DBMigrationConfiguration {

    @Value("${dev.mode}")
    private boolean devMode;

    @Value("${db.migration.enabled}")
    private boolean dbMigrationEnabled;

    @Value("${db.flyway.validateon.migrate}")
    private boolean validateOnMigrate;

    @Value("${db.flyway.migration.repair}")
    private boolean repairMigration;

    @Value("${statewide.migration.required}")
    private boolean statewideMigrationRequired;

    @Value("${db.flyway.main.migration.file.path}")
    private String mainMigrationFilePath;

    @Value("${db.flyway.sample.migration.file.path}")
    private String sampleMigrationFilePath;

    @Value("${db.flyway.tenant.migration.file.path}")
    private String tenantMigrationFilePath;

    @Value("${db.flyway.statewide.migration.file.path}")
    private String statewideMigrationFilePath;

    @Value("${statewide.schema.name}")
    private String statewideSchemaName;
    
    @Value("$db.flyway.collection.migration.file.path")
    private String collectionMigrationFile;
    
    @Value("$db.flyway.commons.migration.file.path")
    private String commonsMigrationFile;
    

    @Autowired
    private ConfigurableEnvironment environment;

    @Bean
   @DependsOn("dataSource")
    public Flyway flyway(DataSource dataSource ) {
    	//dbMigrationEnabled=false;
    	List<String> cities   = new ArrayList<>();
    	cities.add("tetsin");
        if (true) {
            cities.stream().forEach(schema -> {
                if (devMode)
                    migrateDatabase(dataSource, schema,collectionMigrationFile,commonsMigrationFile,
                            mainMigrationFilePath, sampleMigrationFilePath, format(tenantMigrationFilePath, schema));
                else
                    migrateDatabase(dataSource, schema,
                            mainMigrationFilePath, format(tenantMigrationFilePath, schema));
            });

            if (statewideMigrationRequired && !devMode) {
                migrateDatabase(dataSource, statewideSchemaName, mainMigrationFilePath, statewideMigrationFilePath);
            } else if (!devMode) {
                migrateDatabase(dataSource, statewideSchemaName, mainMigrationFilePath);
            }
        }

        return Flyway.configure()
                .dataSource(dataSource)
                .schemas(cities.isEmpty() ? "public" : cities.get(0)) // fallback schema
                .load();
    }

    private void migrateDatabase(DataSource dataSource, String schema, String... locations) {
       Flyway flyway =  Flyway.configure()
        .baselineOnMigrate(true)
        .validateOnMigrate(false)
        .outOfOrder(true)
        .locations(locations)
        .dataSource(dataSource)
        .schemas(schema)
        .load();
      if (true)
         flyway.repair();
       
        flyway.migrate();
    }

	/*
	 * @Bean(name = "tenants", autowire = Autowire.BY_NAME) public List<String>
	 * tenants() { List<String> tenants = new ArrayList<>();
	 * environment.getPropertySources().iterator().forEachRemaining(propertySource
	 * -> { if (propertySource instanceof MapPropertySource) ((MapPropertySource)
	 * propertySource).getSource().forEach((key, value) -> { if
	 * (key.startsWith("tenant.")) tenants.add(value.toString()); }); }); return
	 * tenants; }
	 */

}
