package org.example._06;


import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


/*
使用 Apache commons-dbutils 封装一个通用 DAO
 */
public class BasicDAO<T> {  // 泛型制定具体类型
    private QueryRunner qr = new QueryRunner();

    // 开发通用的 DML 方法，针对任意表
    // query 方法会关闭 preparedStatement 和 结果集
    // JDBCHikariCPUtil 则会关闭 connection
    // 所以用户只需要输入 sql 语句和参数即可，非常方便
    public int update (String sql, Object... parameters) {
        Connection connection = null;

        try {
            connection = JDBCUtilByHikariCP.getConnection();
            int update = qr.update(connection, sql, parameters);
            return update;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtilByHikariCP.close(null, null, connection); // 关闭连接
        }
    }

    // 返回多个对象(即查询的结果是多行), 针对任意表
    /**
     *
     * @param sql sql 语句，可以有 ?
     * @param clazz 传入一个类的Class对象 比如 users.class
     * @param parameters 传入 ? 的具体的值，可以是多个
     * @return 根据users.class 返回对应的 ArrayList 集合
     */
    public List<T> queryMulti(String sql, Class<T> clazz, Object... parameters) {
        Connection connection = null;
        try {
            connection = JDBCUtilByHikariCP.getConnection();
            // 返回某类型的对象List数组列表
            return qr.query(connection, sql, new BeanListHandler<T>(clazz), parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtilByHikariCP.close(null, null, connection); // 关闭连接
        }
    }

    // 查询单行结果的通用方法
    // BeanHandler: 将结果集中的第一行数据封装到一个对应的 JavaBean 实例中。
    // 返回的是一个Object类
    public T querySingle(String sql, Class<T> clazz, Object... parameters) {
        Connection connection = null;
        try {
            connection = JDBCUtilByHikariCP.getConnection();
            return qr.query(connection, sql, new BeanHandler<T>(clazz), parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtilByHikariCP.close(null, null, connection); // 关闭连接
        }
    }

    // 查询单行单列的方法,即返回单值的方法
    // 这里的 Object 是单行单列的某元素类型(int、string ....)
    // 如查询某行的姓名项，此时就是String类型，这里用Object类
    public Object queryScalar(String sql, Object... parameters) {
        Connection connection = null;
        try {
            connection = JDBCUtilByHikariCP.getConnection();
            return qr.query(connection, sql, new ScalarHandler(), parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtilByHikariCP.close(null, null, connection); // 关闭连接
        }
    }
}
