package org.example._01;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import com.mysql.cj.jdbc.Driver;  // MySQL 8.0 以上版本 - JDBC 驱动名

/* 静态加载，依赖第三方 Driver
以 Mysql5.7 为例

-- 创建数据库 learnjdbc:
DROP DATABASE IF EXISTS learnjdbc;
CREATE DATABASE learnjdbc;

-- 创建表students:
USE learnjdbc;
CREATE TABLE students (
  id BIGINT AUTO_INCREMENT NOT NULL,
  name VARCHAR(50) NOT NULL,
  gender TINYINT(1) NOT NULL,
  grade INT NOT NULL,
  score INT NOT NULL,
  PRIMARY KEY(id)
) Engine=INNODB DEFAULT CHARSET=UTF8;

INSERT INTO students (name, gender, grade, score) VALUES ('小军', 0, 1, 93);  -- 插入一条样例
*/

public class Conn01Static {
    private final static Driver driver;

    static
    {
        try {
            driver = new com.mysql.cj.jdbc.Driver();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String [] args) throws SQLException{
        String url = "jdbc:mysql://localhost:3306/learnjdbc?useSSL=false&characterEncoding=utf8";  // MySQL 8.0 以上版本 - 数据库 URL
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "rootpw");
        Connection connect = driver.connect(url, properties);
        System.out.println(connect);

        // 执行sql语句
        String sql = "insert into students (name, gender, grade, score) values('wang_v1', 0, 7, 81);";
        Statement statement = connect.createStatement();

        // 返回sql语句影响的数据库表的行的数量
        int rows = statement.executeUpdate(sql);
        System.out.println(rows>0 ? "成功" : "失败");

        sql = "insert into students (name, gender, grade, score) values('明', 0, 8, 73);";
        System.out.println(connect.createStatement().executeUpdate(sql)>0 ? "成功" : "失败");

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
