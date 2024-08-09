package org.example._02;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/*
事务 Transaction
    在JDBC程序中创建Connection对象时，默认会开启自动提交事务，即执行一个sql语句就提交一个sql，无法回滚。
    当我们需要在JDBC中让多个SQL语句作为一个整体进行执行（转账），需要使用setAutoCommit(false)，该方法会取消自动提交事务这一设定。
    在确定所有SQL语句执行成功后，使用commit()手动提交事务。
    当SQL语句中的某个部分操作失败或出现异常时，使用rollback()回滚事务。
*/

public class Transaction {
    private final static String user;
    private final static String password;
    private final static String url;
//    private final static String driver;

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
//            driver = properties.getProperty("driver");
        } catch (IOException e) {
            // 在实际开发中，可以这样处理
            // 1. 将编译异常转成运行异常
            // 2. 调用这可以选择捕获该异常，也可以选择默认处理(报出编译异常)
            throw new RuntimeException(e);
            // e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);;
        PreparedStatement preparedStatement = null;
        String sql1 = "UPDATE learnjdbc.students set score = 1 where id = 1";
        String sql2 = "UPDATE learnjdbc.students set score = 2 where id = 2";

        try {
            connection.setAutoCommit(false);

            // 执行第一条sql1
            preparedStatement = connection.prepareStatement(sql1);
            preparedStatement.executeUpdate();

            // 制造异常
            int i = 1 / 0;
            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.executeUpdate();

            // 无异常发生，则提交
            connection.commit();

            // 检测到异常
        } catch (Exception e) {
            System.out.println("sql执行未完成，数据回滚");
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            ResultSet rs = preparedStatement.executeQuery("select * from students where id < 3;");
            while(rs.next()){
                Long id = rs.getLong("id");
                String name = rs.getString(2);
                Short gender = rs.getShort(3);
                Integer grade = rs.getInt(4);
                Integer score = rs.getInt("score");  // 通过字段名称获取
                System.out.printf("id = %d, name = %s, gender = %d, grade = %d, score = %d\n", id, name, gender, grade, score);
            }
            rs.close();
            preparedStatement.close();
            connection.close();
        }
    }
}
