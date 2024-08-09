package org.example._04;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.example.Student;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.example._04.Utils.queryStudents;

public class DruidConnectionPool {
    private static DataSource dataSource;
    // 在static 代码块获得配置文件信息
    static {
        Properties properties = new Properties();
        try {
            // 加载之前用过的配置文件
            properties.load(new FileInputStream("src/druid.properties"));
            // 创建一个指定参数的数据库连接池, Druid连接池
            dataSource = DruidDataSourceFactory.createDataSource(properties);

        } catch (Exception e) {

        }
    }

    public static void main(String[] args) throws SQLException {
        List<Student> students = queryStudents(dataSource);
        students.forEach(System.out::println);
    }
}
