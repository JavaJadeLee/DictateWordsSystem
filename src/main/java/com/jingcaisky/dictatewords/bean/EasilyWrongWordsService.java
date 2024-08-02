package com.jingcaisky.dictatewords.bean;


import java.sql.*;
import java.util.ArrayList;
import com.jingcaisky.dictatewords.domain.Words;

public class EasilyWrongWordsService {

    //SQLite 数据库文件
    private final String DB_FILE = "src/main/resources/words.db";
    private final String URL = "jdbc:sqlite:" + DB_FILE;
    private static String CLASS_NAME = "org.sqlite.JDBC";
    Connection conn ;
    Statement statement;
    ResultSet rs;

    /**
     * 获取数据库连接
     * 如果连接不存在，则创建一个新的连接
     *
     * @return 数据库连接对象
     */
    public Connection getConn() {
        // 检查是否存在数据库连接
        if (conn == null) {
            try {
                // 注册数据库驱动
                Class.forName(CLASS_NAME);
                // 通过URL获取数据库连接
                conn = DriverManager.getConnection(URL);
            } catch (SQLException e) {
                // 处理SQL异常
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // 处理找不到驱动类异常
                throw new RuntimeException(e);
            }
        }
        return conn;
    }


    /**
     * 关闭数据库连接
     *
     * 本方法用于安全地关闭与数据库的连接如果连接存在，则关闭它；如果无法关闭，将打印错误堆栈
     */
    public void closeConn() {
        // 检查数据库连接是否已经初始化
        if (conn != null) {
            try {
                // 尝试关闭数据库连接
                conn.close();
            } catch (SQLException e) {
                // 捕获SQLException，避免异常中断程序执行
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询所有易错词汇
     * 该方法通过数据库查询操作，从易错词汇表中获取所有词汇，并将其封装为Words对象列表返回
     *
     * @return 包含所有易错词汇的ArrayList集合，每个元素为一个Words对象
     */
    public ArrayList<Words> selectAllEasilyWrongWords() {
        // 初始化一个空的ArrayList集合，用于存储查询到的词汇对象
        ArrayList<Words> wordsList = new ArrayList<>();
        // 定义查询语句，用于从易错词汇表中查询所有字段
        String sql = "SELECT * FROM EasilyWrongWords";
        try {
            // 获取数据库连接
            conn = this.getConn();
            // 创建Statement对象，用于执行SQL查询语句
            statement = conn.createStatement();
            // 执行查询语句，获取结果集
            rs = statement.executeQuery(sql);
            // 遍历结果集，为每一行数据创建一个Words对象，并将其添加到列表中
            while (rs.next()) {
                Words words = new Words();
                words.setId(rs.getInt("id"));
                words.setZh(rs.getString("zh"));
                words.setEn(rs.getString("en"));
                words.setType(rs.getString("type"));
                wordsList.add(words);
            }
            // 关闭结果集和Statement对象
            rs.close();
            statement.close();
        } catch (SQLException e) {
            // 如果发生SQL异常，将其包装为RuntimeException抛出
            throw new RuntimeException(e);
        }
        // 关闭数据库连接
        closeConn();
        // 返回查询到的词汇列表
        return wordsList;
    }
}
