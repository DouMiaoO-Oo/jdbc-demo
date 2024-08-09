package org.example._06;

import org.apache.commons.dbutils.QueryRunner;
import org.example.Student;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用 DAO 情况下的调用 JDBCUtil
 * 自动封装 Student 的对象
 */

public class JDBCUtilByHikariCPTestV2 {
    public static void main(String [] args){
        List<Student> list = new ArrayList<>();
        Connection connection = null;
        QueryRunner qr = new QueryRunner();
        BasicDAO<Student> dao = new BasicDAO<>();
        try{
            String sql = "select * from learnjdbc.students where score > ?;";
            /*
            connection = JDBCUtilByHikariCP.getConnection();
            list = qr.query(
                    connection,
                    sql,
                    new BeanListHandler<>(Student.class),
                    81
            );*/
            list = dao.queryMulti(sql, Student.class, 81);
        } catch (Exception e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
    }
}
