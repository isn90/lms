/*package com.cloudx.azure.library_management_system.config;

import com.cloudx.azure.library_management_system.service.KeyVaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Autowired
    private KeyVaultService keyVaultService;

    @Bean
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(keyVaultService.getDbUrl())
                .username(keyVaultService.getDbUsername())
                .password(keyVaultService.getDbPassword())
                .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .build();
    }
}*/