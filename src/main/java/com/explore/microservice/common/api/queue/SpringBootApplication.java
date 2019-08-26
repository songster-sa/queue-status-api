/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.explore.microservice.common.api.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

/**
 * The main entry point for SpringBoot, this is also the main configuration file
 * for any SpringBoot properties. Auto config for Hibernate and Data Sources has
 * been disabled to prevent issues with Hibernate 3. @EnableAutoConfiguration
 * should only ever be on this class, other classes may use @Configuration.
 */
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, FreeMarkerAutoConfiguration.class, MultipartAutoConfiguration.class})
@Configuration
@EnableTransactionManagement
@DependsOn("transactionManager.pomdata")
@ComponentScan
@ImportResource("classpath:springConfig.xml") // Add reference to the main springConfig.xml to load all the XML based config
public class SpringBootApplication extends SpringBootServletInitializer implements TransactionManagementConfigurer {

    @Autowired
    @Qualifier("transactionManager.pomdata")
    private PlatformTransactionManager pomdataTransactionManager;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.sources(SpringBootApplication.class); // This will handle loading all default SpringBoot components like Actuators
        return application;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return pomdataTransactionManager; // The default transaction manager
    }
}
