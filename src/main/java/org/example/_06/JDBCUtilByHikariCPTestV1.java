package org.example._06;

import org.example.Student;

import java.sql.*;
import java.util.ArrayList;

/**
 * 没有 DAO 情况下的调用 JDBCUtil 的土方法
 * 人工封装 Student 的对象
 */

public class JDBCUtilByHikariCPTestV1 {
    public static void main(String [] args){
        ArrayList<Student> arrayList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try{
            connection = JDBCUtilByHikariCP.getConnection();
            String sql = "select * from learnjdbc.students where score > ?;";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, 80);
            rs = statement.executeQuery();
            while(rs.next()){
                long id = rs.getLong("id");
                String name = rs.getString(2);
                boolean gender = rs.getShort(3) == 1;
                int grade = rs.getInt(4);
                int score = rs.getInt("score");  // 通过字段名称获取
                arrayList.add(new Student(id, name, gender, grade, score));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            JDBCUtilByHikariCP.close(rs, statement, connection);
        }
        arrayList.forEach(System.out::println);
    }
}
