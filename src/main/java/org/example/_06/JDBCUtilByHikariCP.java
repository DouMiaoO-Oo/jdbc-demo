package org.example._06;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;


public class JDBCUtilByHikariCP {
    private final static String user;
    private final static String password;
    private final static String url;
    private final static String driver;
    private final static HikariConfig hikariConfig = new HikariConfig();
    private final static HikariDataSource hikariDataSource;
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
    public static Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }
    // 关闭连接, 在数据库连接池技术中，close() 不是真的断掉连接
    // 而是把使用的Connection对象放回连接池
    public static void close(ResultSet resultSet, Statement statement, Connection connection) {
        try{
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
