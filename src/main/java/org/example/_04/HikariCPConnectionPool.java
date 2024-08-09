package org.example._04;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.Student;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.example._04.Utils.queryStudents;

/*
数据库连接池 HikariCP 的使用样例
    Java 数据库连接池介绍(7)--HikariCP 介绍
        https://www.cnblogs.com/wuyongyin/p/15560017.html
* */


public class HikariCPConnectionPool {
    private final static String user;
    private final static String password;
    private final static String url;
    private final static String driver;
    private final static HikariConfig hikariConfig = new HikariConfig();
    private static final HikariDataSource hikariDataSource;
    // 在static 代码块获得配置文件信息
    static {
        Properties properties = new Properties();
        try {
            // 加载之前用过的配置文件
            properties.load(new FileInputStream("src/mysql.properties"));
            // 读取相关属性
            user = properties.getProperty("user.name");
            password = properties.getProperty("user.password");
            url = properties.getProperty("url");
            driver = properties.getProperty("driver");

            hikariConfig.setPoolName("HikariCP 连接池");
//            hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");  // 可以省略
            hikariConfig.addDataSourceProperty("user", user);  // hikariConfig.setUsername(user);
            hikariConfig.addDataSourceProperty("password", password);  // hikariConfig.setPassword(password);
            hikariConfig.setJdbcUrl(url);
            hikariConfig.addDataSourceProperty("maximumPoolSize", "10");  // hikariConfig.setMaximumPoolSize(10);
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "100");
            hikariDataSource = new HikariDataSource(hikariConfig);
        } catch (IOException e) {
            // 在实际开发中，可以这样处理
            // 1. 将编译异常转成运行异常
            // 2. 调用这可以选择捕获该异常，也可以选择默认处理(报出编译异常)
            throw new RuntimeException(e);
            // e.printStackTrace();
        }
    }



    public static void main(String [] args) throws SQLException {
        List<Student> students = queryStudents(hikariDataSource);
        students.forEach(System.out::println);
    }
}
