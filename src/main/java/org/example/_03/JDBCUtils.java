package org.example._03;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class JDBCUtils {
    // 因为只需要一个实例，因此用 static 修饰

    private final static String user;
    private final static String password;
    private final static String url;
    private final static String driver;

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
        } catch (IOException e) {
            // 在实际开发中，可以这样处理
            // 1. 将编译异常转成运行异常
            // 2. 调用这可以选择捕获该异常，也可以选择默认处理(报出编译异常)
            throw new RuntimeException(e);
            // e.printStackTrace();
        }
    }

    // 连接数据库，返回Connection
    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    //关闭相关资源
    /*
     * 1. ResultSet 结果集
     * 2. Statement 或 PreparedStatement
     * 3. Connection
     */
    public static void close(ResultSet set, Statement statement, Connection connection){
        // 判断是否为NULL
        try {
            if(set != null){
                set.close();
            }
            if(statement != null){
                statement.close();
            }
            if (connection != null){
                connection.close();
            }
        } catch (SQLException e) {
            // 转为运行时异常抛出
            throw new RuntimeException(e);
        }
    }

    public static void main(String [] args){
        JDBCUtils jdbcUtils = new JDBCUtils();
    }
}
