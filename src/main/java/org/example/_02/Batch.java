package org.example._02;

import java.sql.*;
import java.util.Properties;

/*
* 批处理 batch
    批处理即批量处理sql语句
    批处理通常和 PreparedStatement 一起使用，既减少编译次数又减少网络开销，提高效率
    JDBC批处理方法：
        addBatch()：将sql语句添加到Batch
        executeBatch()：批量执行Batch内的语句
        clearBatch()：清空批处理包的语句
    要在JDBC中使用批处理，必须在配置文件的url中传入 rewriteBatchedStatements=true 这一参数
* */

public class Batch {
    public static void main(String [] args) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/learnjdbc?useSSL=false&characterEncoding=utf8&rewriteBatchedStatements=true";
        //  jdk1.5后，Class.forName可以省略，注册会自动完成
//        Class.forName("com.mysql.cj.jdbc.Driver");  //   // MySQL 8.0 以上版本 - JDBC 驱动名

        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "rootpw");

        /* 通过反射加载 Driver 类 */
        Connection connect = DriverManager.getConnection(url, properties);  // DriverManager 类加载时会自动执行 Class.forName(driver) 自动注册 driver
        String sql = "insert into students (name, gender, grade, score) values('Batch', 0, 0, ?)";  // 这里结尾不能带分号!!!!
        PreparedStatement preparedStatement = connect.prepareStatement(sql);
        for (int i = 0; i < 550; i++) {
            preparedStatement.setInt(1, i);
            // 添加到批处理
            preparedStatement.addBatch();
            // 每攒够100条sql命令，执行一次
            if( (i + 1) % 100 == 0){
                //执行Batch内的语句
                preparedStatement.executeBatch();
                //提交后清空batch内的sql语句
                preparedStatement.clearBatch();
            }
        }
        // 执行batch内剩余没提交的SQL
        preparedStatement.executeBatch();
        preparedStatement.clearBatch();


        // 验证数据
        Statement statement = connect.createStatement();
        ResultSet rs = statement.executeQuery("select count(*) from students where name = 'Batch';");
        while (rs.next()){
            Integer cnt = rs.getInt(1);
            System.out.println("批量插入的字段个数为" + cnt);
        }

        int deleteCnt = statement.executeUpdate("delete from students where name = 'Batch';");
        System.out.printf("删除刚才插入的数据%d条\n", deleteCnt);

        // 关闭连接
        preparedStatement.close();
        statement.close();
        connect.close();
    }
}
