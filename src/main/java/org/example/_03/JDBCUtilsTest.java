package org.example._03;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCUtilsTest {

    public static void main(String[] args) {
        // 这些变量可能需要通过 JDBCUtils 来关闭
        // 因此定义在外部
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String sql = "select * from students where  score > ?;";
        String userGivenScore = Integer.toString(85) ;  // 用户提供

        try {
            connection = JDBCUtils.getConnection();
            System.out.println("获得连接：" + connection.getClass());
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userGivenScore);
            // 这里sql语句已经完整，所以不用填充，直接使用
            rs = preparedStatement.executeQuery();
            while(rs.next()){
                Long id = rs.getLong("id");
                String name = rs.getString(2);
                Short gender = rs.getShort(3);
                Integer grade = rs.getInt(4);
                Integer score = rs.getInt("score");  // 通过字段名称获取
                System.out.printf("id = %d, name = %s, gender = %d, grade = %d, score = %d\n", id, name, gender, grade, score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(rs, preparedStatement, connection);
        }
    }
}
