package org.example._04;

import org.example.Student;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    static List<Student> queryStudents(DataSource ds) throws SQLException {
        List<Student> students = new ArrayList<>();
        try (Connection conn = ds.getConnection()) { // 在此获取连接, 即从链接池中选择一个空闲连接，标记它为“正在使用”然后返回
            try (PreparedStatement ps = conn
                    .prepareStatement("SELECT * FROM students WHERE score >= ?")) {
                ps.setInt(1, 90); // 第一个参数score=?
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        students.add(extractRow(rs));
                    }
                }
            }
        } // 在此“释放”连接, 即对 Connection 调用 close() 把连接标记为“空闲”释放到连接池中从而等待下次调用
        return students;
    }
    static Student extractRow(ResultSet rs) throws SQLException {
        Student std = new Student();
        std.setId(rs.getLong("id"));
        std.setName(rs.getString("name"));
        std.setGender(rs.getBoolean("gender"));
        std.setGrade(rs.getInt("grade"));
        std.setScore(rs.getInt("score"));
        return std;
    }
}
