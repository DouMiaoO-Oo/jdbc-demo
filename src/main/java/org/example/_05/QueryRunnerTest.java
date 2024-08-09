package org.example._05;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.example.Student;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Properties;


/*
前置知识
    Data Query Language (DQL)
        The Data Query Language is the sublanguage responsible for reading data from a database.
        In SQL, this corresponds to the SELECT
    Data Manipulation Language (DML)
        The Data Manipulation Language is the sublanguage responsible for adding, editing or deleting data from a database.
        In SQL, this corresponds to the INSERT, UPDATE, and DELETE
    Data Definition Language (DDL)
        The Data Definition Language is the sublanguage responsible for defining the way data is structured in a database.
        In SQL, this corresponds to manipulating tables through the CREATE TABLE, ALTER TABLE, and DROP TABLE
    Data Control Language (DCL)
        The Data Control Language is the sublanguage responsible for the administrative tasks of controlling the database itself, most notably granting and revoking database permissions for users.
        In SQL, this corresponds to the GRANT, REVOKE, and DENY commands, among others.
 */

/*
    使用 apache-DBUtils 工具类保存查询结果数据集
    能够利用反射机制将 query 查询到的 ResultSet 映射为制定的对象
*/

public class QueryRunnerTest {
    private static DataSource getDataSource() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/mysql.properties"));
        // 读取相关属性
        String user = properties.getProperty("user.name");
        String password = properties.getProperty("user.password");
        String url = properties.getProperty("url");
        String driver = properties.getProperty("driver");
        // 构造 config
        HikariConfig config = new HikariConfig();
        config.setPoolName("HikariCP 连接池");
//            config.setDriverClassName("com.mysql.cj.jdbc.Driver");  // 可以省略
        config.addDataSourceProperty("user", user);  // hikariConfig.setUsername(user);
        config.addDataSourceProperty("password", password);  // hikariConfig.setPassword(password);
        config.setJdbcUrl(url);
        config.addDataSourceProperty("maximumPoolSize", "10");  // hikariConfig.setMaximumPoolSize(10);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "100");

        return new HikariDataSource(config);
    }
    public static void testQueryMulti() throws SQLException, IOException {
        /* 返回的结果是多行记录(多个对象) */
        List<Student> list;  // 测试 BeanListHandler
        DataSource ds = getDataSource();
        try(Connection conn = ds.getConnection()){
            String sql = "SELECT * FROM students WHERE score >= ?";
            QueryRunner queryRunner = new QueryRunner();  // new QueryRunner(ds);  //  这样构造也行
            list = queryRunner.query(
                    conn,
                    sql,
                    new BeanListHandler<>(Student.class),  // 将 resultSet -> Student  -> 封装到 ArrayList
                    1  // 1 就是给 sql 语句中的? 赋值，可以有多个值，因为是可变参数Object... params
            );  // query 方法执行结束后会自动关闭 ResultSet 和 Statement
        }  // 关闭 connection
        list.forEach(System.out::println);
    }

    public static void testQuerySingle() throws SQLException, IOException {
        /* 返回的结果是单行记录(单个对象) */
        Student student;  // 测试 BeanHandler
        DataSource ds = getDataSource();
        try(Connection conn = ds.getConnection()){
            String sql = "SELECT * FROM students where id > ? limit 1";
            QueryRunner queryRunner = new QueryRunner();  // new QueryRunner(ds);  //  这样构造也行
            student = queryRunner.query(
                    conn,
                    sql,
                    new BeanHandler<>(Student.class),  // 返回单行记录 -> 单个对象 , 使用的 Hander 是 BeanHandler
                    1  // 1 就是给 sql 语句中的? 赋值，可以有多个值，因为是可变参数Object... params
            );  // query 方法执行结束后会自动关闭 ResultSet 和 Statement
        }  // 关闭 connection
        System.out.println(student);
    }
    public static void testQueryScalar() throws SQLException, IOException {
        /* 返回的结果是单行单列-返回的就是 object */
        String name;  // 测试 ScalarHandler
        DataSource ds = getDataSource();
        try(Connection conn = ds.getConnection()){
            String sql = "SELECT name FROM students where id > ? limit 1";
            QueryRunner queryRunner = new QueryRunner();  // new QueryRunner(ds);  //  这样构造也行
            name = queryRunner.query(
                    conn,
                    sql,
                    new ScalarHandler<String>(),  // 返回单个字段, 使用的 Handler 是 ScalarHandler.  // Explicit type argument String can be replaced with <>
                    1  // 1 就是给 sql 语句中的? 赋值，可以有多个值，因为是可变参数Object... params
            );  // query 方法执行结束后会自动关闭 ResultSet 和 Statement
        }  // 关闭 connection
        System.out.println(name);
    }

    public static void testDML() throws SQLException, IOException {
        /* 测试 update, insert, delete */
        DataSource ds = getDataSource();
        String iSql = "insert into learnjdbc.students (name, gender, grade, score) values(?, 0, 0, ?)";
        String uSql = "update learnjdbc.students set gender = -1 where name = ?";
        String dSql = "delete from learnjdbc.students where name = ?";
        String qSql = "select * from learnjdbc.students where name = 'dml'";
        QueryRunner queryRunner = new QueryRunner(ds);  // 内部从 ds 中获取 connection.
        int rows = queryRunner.execute(iSql, "dml", 1);  // 被影响的行数
        System.out.println("rows: "+ rows + " " + queryRunner.query(qSql, new BeanHandler<>(Student.class)));

        rows = queryRunner.execute(uSql, "dml");  // 被影响的行数
        System.out.println("rows: "+ rows + " " + queryRunner.query(qSql, new BeanHandler<>(Student.class)));

        rows = queryRunner.execute(dSql, "dml");  // 被影响的行数
        System.out.println("rows: "+ rows + " " + queryRunner.query(qSql, new BeanHandler<>(Student.class)));
    }

    public static void main(String [] args) throws IOException, SQLException {
        System.out.println("-------------- Query Multi lines --------------");
        testQueryMulti();

        System.out.println("-------------- Query Single line --------------");
        testQuerySingle();

        System.out.println("-------------- Query Single field --------------");
        testQueryScalar();

        System.out.println("-------------- DML: update, insert, delete --------------");
        testDML();
    }
}
