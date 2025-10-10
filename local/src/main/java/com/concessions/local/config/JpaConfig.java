package com.concessions.local.config;

import javax.sql.DataSource;

import org.hibernate.community.dialect.SQLiteDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

import java.util.Properties;

/**
 * Spring JPA Configuration for SQLite in a non-Spring Boot application.
 * This class manually configures the DataSource, EntityManagerFactory,
 * Transaction Manager, and enables JPA repositories.
 */
@Configuration
@EnableTransactionManagement
// Configure where Spring Data JPA should look for your repository interfaces
@EnableJpaRepositories(basePackages = "com.concessions.local.persistence") 
public class JpaConfig {

    /**
     * 1. Configures the JDBC DataSource to connect to the SQLite file.
     * The file 'concessions.db' will be created in the application root if it doesn't exist.
     */
    @Bean
    public DataSource dataSource() {
    	System.out.println("Configuring SQLite DataSource...");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        // Driver for Xerial SQLite
        dataSource.setDriverClassName("org.sqlite.JDBC");
        // JDBC URL pointing to the local database file
        dataSource.setUrl("jdbc:sqlite:concessions.db");
        return dataSource;
    }

    /**
     * 2. Configures the JPA EntityManagerFactory (required by Hibernate).
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        
        // Define where Hibernate should scan for @Entity classes (like Menu and BaseMenu)
        emf.setPackagesToScan("com.concessions.model"); 
        
        // Use Hibernate as the JPA vendor
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);
        
        // Set Hibernate properties
        Properties jpaProperties = new Properties();
        // Set the SQLite dialect
        jpaProperties.put("hibernate.dialect", SQLiteDialect.class.getName());
        // Automatically create/update tables based on entities
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        // Optional: Show the generated SQL in the console
        jpaProperties.put("hibernate.show_sql", "true");
        
        emf.setJpaProperties(jpaProperties);
        return emf;
    }

    /**
     * 3. Configures the Transaction Manager.
     * This bean allows Spring to manage transactions using the @Transactional annotation.
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
