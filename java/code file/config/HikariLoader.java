package com.mrbai.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * spring 启动时初始化 Hikari
 * @author: MrBai
 * @date: 2021-03-31 16:02
 **/
@Component
public class HikariLoader implements ApplicationRunner {

    private final HikariDataSource hikariDataSource;

    @Autowired
    public HikariLoader(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Autowired
    public void run(ApplicationArguments args) throws SQLException {
        hikariDataSource.getConnection();
    }
}
