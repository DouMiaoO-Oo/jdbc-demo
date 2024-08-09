package org.example._01;

import java.sql.*;
import java.util.Properties;

/*
*
* 本方法 使用DriverManager替换Driver进行统一管理
* */

public class Conn03DriverManager {

    public static void main(String [] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        String url = "jdbc:mysql://localhost:3306/learnjdbc?useSSL=false&characterEncoding=utf8";
        //  jdk1.5后，Class.forName可以省略，注册会自动完成
//        Class.forName("com.mysql.cj.jdbc.Driver");  //   // MySQL 8.0 以上版本 - JDBC 驱动名

        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "rootpw");

        /* 通过反射加载 Driver 类 */
        Connection connect = DriverManager.getConnection(url, properties);  // DriverManager 类加载时会自动执行 Class.forName(driver) 自动注册 driver
        String sql = "insert into students (name, gender, grade, score) values('wang_v3', 0, 7, 83);";
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
