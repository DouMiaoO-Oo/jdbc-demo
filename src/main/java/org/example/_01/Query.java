package org.example._01;

import java.sql.*;
import java.util.Properties;

/*
*
* 替换 Statement 为 PreparedStatement
* */

public class Query {

    public static void main(String [] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        String url = "jdbc:mysql://localhost:3306/learnjdbc?useSSL=false&characterEncoding=utf8";
        //  jdk1.5后，Class.forName可以省略，注册会自动完成
//        Class.forName("com.mysql.cj.jdbc.Driver");  //   // MySQL 8.0 以上版本 - JDBC 驱动名

        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "rootpw");

        /* 通过反射加载 Driver 类 */
        Connection connect = DriverManager.getConnection(url, properties);  // DriverManager 类加载时会自动执行 Class.forName(driver) 自动注册 driver
        Statement statement = connect.createStatement();

        /* 查询数据可以直接拼接SQL */
        // select * from learnjdbc.students;
        ResultSet rs = statement.executeQuery("select * from students where id % 2 = 1;");
        while (rs.next()){
            Long id = rs.getLong("id");
            String name = rs.getString(2);
            Short gender = rs.getShort(3);
            Integer grade = rs.getInt(4);
            Integer score = rs.getInt("score");  // 通过字段名称获取
            System.out.printf("id = %d, name = %s, gender = %d, grade = %d, score = %d\n", id, name, gender, grade, score);
        }

        /*
        推荐使用 PreparedStatement 预处理
        (1) Statement 语句存在 SQL 注入的风险，PreparedStatement 将解决这一问题
        (2) 不再使用+拼接sql语句，减少语法错误
        (3) 复用SQL时减少了编译的次数，提高了效率
        */

        System.out.println("---------------------------------------------------");

        // "?"为占位符，留给后面PreparedStatement 输入
        String sql = "select * from students where  score > ?;";
        int userGivenScore = 80;  // 用户提供
        PreparedStatement preparedStatement = connect.prepareStatement(sql);
        preparedStatement.setInt(1, userGivenScore);
        //这里无需再传入sql语句，preparedStatement已经在上面和sql绑定了
        //若再传入sql，则执行的是最原始的sql语句
        rs = preparedStatement.executeQuery();
        while (rs.next()){
            Long id = rs.getLong("id");
            String name = rs.getString(2);
            Short gender = rs.getShort(3);
            Integer grade = rs.getInt(4);
            Integer score = rs.getInt("score");  // 通过字段名称获取
            System.out.printf("id = %d, name = %s, gender = %d, grade = %d, score = %d\n", id, name, gender, grade, score);
        }

        // 关闭连接
        statement.close();
        connect.close();
    }
}
