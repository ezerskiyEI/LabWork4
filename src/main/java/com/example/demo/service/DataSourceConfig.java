package com.example.demo.service;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        String dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource";
        String url = "jdbc:postgresql://localhost:5432/car_database";
        String user = "postgres";
        String password = "8001653";

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setInitializationFailTimeout(0);
        dataSource.setMinimumIdle(1);
        dataSource.setMaximumPoolSize(10);
        dataSource.setIdleTimeout(300000);
        dataSource.setMaxLifetime(900000);
        dataSource.setConnectionTimeout(60000);
        dataSource.setDataSourceClassName(dataSourceClassName);
        dataSource.addDataSourceProperty("url", url);
        dataSource.addDataSourceProperty("user", user);
        dataSource.addDataSourceProperty("password", password);

        return dataSource;
    }
}