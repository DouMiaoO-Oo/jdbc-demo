package org.example._01;

import java.sql.*;
import java.util.Properties;

/*
* 方法1 直接调用 com.mysql.cj.jdbc.Driver() 属于静态加载
* 本方法 利用反射加载Driver类，动态加载，更加灵活，减少依赖性
* */

public class Conn02Reflect {

    public static void main(String [] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        String url = "jdbc:mysql://localhost:3306/learnjdbc?useSSL=false&characterEncoding=utf8";

        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "rootpw");

        /* 通过反射加载 Driver 类 */
        Class<?> clazz = Class.forName("com.mysql.cj.jdbc.Driver");  //  MySQL 8.0 以上版本 - JDBC 驱动名
        Driver driver = (Driver) clazz.newInstance();
        Connection connect = driver.connect(url,properties);

        String sql = "insert into students (name, gender, grade, score) values('wang_v2', 0, 7, 82);";
        Statement statement = connect.createStatement();
        int rows = statement.executeUpdate(sql);
        System.out.println(rows>0 ? "成功" : "失败");
        // select * from learnjdbc.students; 验证一下数据
        ResultSet rs = statement.executeQuery("select * from students;");
        while (rs.next()){
            String name = rs.getString(2);
            Short gender = rs.getShort(3);
            Integer grade = rs.getInt(4);
            Integer score = rs.getInt("score");  // 通过字段名称获取
            System.out.printf("name = %s, gender = %d, grade = %d, score = %d\n", name, gender, grade, score);
        }

        // 关闭连接
        statement.close();
        connect.close();
    }
}
