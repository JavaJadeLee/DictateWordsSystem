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
    PreparedStatement pstmt;
    ResultSet rs;

    /**
     * 设置错误单词
     * 该方法用于处理用户答错的单词，根据单词是否已经存在于错误集合中，决定是插入新的错误单词，还是更新已有单词的错误次数
     *
     * @param tableWords 用户答错的单词对象，包含单词及相关信息
     * @return 返回处理结果，通常是受影响的行数，0表示失败，1表示成功插入或更新
     */
    public int setErrorWord(Words tableWords){
        // 初始化结果变量，用于记录操作是否成功
        int result = 0;
        // 检查错误单词是否已经存在于数据库中
        if (!isExist(tableWords)){
            // 如果单词不存在，则插入新的错误单词
            result = insertEasilyWrongWords(tableWords);
        }else {
            // 如果单词已存在，则查询该错误单词的对象
            Words tempWord = selectEasilyWrongWordsByWord(tableWords);
            // 更新错误单词的错误次数
            result = updateEasilyWrongWordsCount(tempWord);
        }

        // 返回处理结果
        return result;
    }
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
            pstmt = conn.prepareStatement(sql);
            // 执行查询语句，获取结果集
            rs = pstmt.executeQuery(sql);
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
            pstmt.close();
        } catch (SQLException e) {
            // 如果发生SQL异常，将其包装为RuntimeException抛出
            throw new RuntimeException(e);
        }
        // 关闭数据库连接
        closeConn();
        // 返回查询到的词汇列表
        return wordsList;
    }

    /**
     * 插入易错词
     *
     * @param words 要插入的易错词对象，包含中文、英文和词性
     * @return 插入操作的结果，返回受影响的行数
     * @throws RuntimeException 如果插入过程中发生SQL异常
     */
    public int insertEasilyWrongWords(Words words) {
        // 定义SQL插入语句，用于向易错词表中插入新的易错词
        String sql = "INSERT INTO EasilyWrongWords (zh, en, type, error_count) VALUES (?, ?, ?, ?)";
        int result = 0;
        try {
            // 获取数据库连接
            conn = this.getConn();
            // 准备执行SQL语句
            pstmt = conn.prepareStatement(sql);
            // 设置插入的中文词
            pstmt.setString(1, words.getZh());
            // 设置插入的英文词
            pstmt.setString(2, words.getEn());
            // 设置插入的词性
            pstmt.setString(3, words.getType());
            // 设置插入的错误计数
            pstmt.setInt(4, 1);
            // 执行插入操作并获取结果
            result = pstmt.executeUpdate();
            // 关闭PreparedStatement对象
            pstmt.close();
        } catch (SQLException e) {
            // 如果发生SQL异常，将其转换为运行时异常抛出
            throw new RuntimeException(e);
        }
        // 关闭数据库连接
        closeConn();
        // 返回插入操作的结果
        return result;
    }
    /**
     * 更新易错词汇的错误计数
     * 当用户在练习中错误地使用了某个词汇时，通过此方法更新该词汇的错误计数，以便于后续学习和复习中的重点提示
     *
     * @param words 待更新的词汇对象，包含词汇的ID和错误计数
     * @return 更新操作的结果，返回受影响的行数，通常用于验证更新操作是否成功
     */
    public int updateEasilyWrongWordsCount(Words words) {
        // SQL更新语句：更新EasilyWrongWords表中指定ID的词汇的错误计数
        String sql = "UPDATE EasilyWrongWords SET error_count = ? WHERE id = ?";
        // 增加词汇的错误计数
        words.setErrorCount(words.getErrorCount() + 1);
        int result = 0;
        try {
            // 获取数据库连接
            conn = this.getConn();
            // 准备SQL语句
            pstmt = conn.prepareStatement(sql);
            // 设置SQL参数：新的错误计数和词汇ID
            pstmt.setInt(1, words.getErrorCount());
            pstmt.setInt(2, words.getId());
            // 执行更新操作
            result = pstmt.executeUpdate();
            // 关闭PreparedStatement对象
            pstmt.close();
        }catch (SQLException e){
            // 捕获SQLException，转换为RuntimeException抛出，便于错误处理
            throw new RuntimeException(e);
        }
        // 关闭数据库连接
        closeConn();
        // 返回更新结果
        return result;
    }

    /**
     * 检查单词是否存在于易于错误的单词表中
     *
     * @param words 要检查的单词对象，包含中文、英文和类型信息
     * @return 如果单词存在，则返回true；否则返回false
     */
    public boolean isExist(Words words) {
        // SQL查询字符串，用于从EasilyWrongWords表中查询指定的单词
        String sql = "SELECT * FROM EasilyWrongWords WHERE zh = ? AND en = ? AND type = ?";
        try {
            // 获取数据库连接
            conn = this.getConn();
            // 准备SQL语句
            pstmt = conn.prepareStatement(sql);
            // 设置SQL参数，分别是单词的中文、英文和类型
            pstmt.setString(1, words.getZh());
            pstmt.setString(2, words.getEn());
            pstmt.setString(3, words.getType());
            // 执行SQL查询
            rs = pstmt.executeQuery();
            // 如果查询结果中存在记录，则单词存在
            if (rs.next()) {
                return true;
            } else {
                // 否则，单词不存在
                return false;
            }
        } catch (SQLException e) {
            // 如果发生SQL异常，抛出RuntimeException
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据单词查询易错词
     *
     * @param words 单词对象，包含中文、英文和类型信息
     * @return 查询到的易错词对象，如果没有查询到，则返回一个空的Words对象
     */
    public Words selectEasilyWrongWordsByWord(Words words) {
        // SQL查询字符串，用于从EasilyWrongWords表中查询指定的单词
        String sql = "SELECT * FROM EasilyWrongWords WHERE zh = ? AND en = ? AND type = ?";
        // 初始化一个空的Words对象，用于存储查询结果
        Words easilyWrongWords = new Words();
        try {
            // 获取数据库连接
            conn = this.getConn();
            // 准备SQL语句
            pstmt = conn.prepareStatement(sql);
            // 设置SQL参数，分别是单词的中文、英文和类型
            pstmt.setString(1, words.getZh());
            pstmt.setString(2, words.getEn());
            pstmt.setString(3, words.getType());
            // 执行SQL查询
            rs = pstmt.executeQuery();
            // 遍历查询结果
            while (rs.next()) {
                // 创建Words对象，并设置其属性
                easilyWrongWords.setId(rs.getInt("id"));
                easilyWrongWords.setZh(rs.getString("zh"));
                easilyWrongWords.setEn(rs.getString("en"));
                easilyWrongWords.setType(rs.getString("type"));
                easilyWrongWords.setErrorCount(rs.getInt("error_count"));
            }
        } catch (SQLException e) {
            // 如果发生SQL异常，抛出RuntimeException
            throw new RuntimeException(e);
        }
        // 返回查询到的易错词对象
        return easilyWrongWords;
    }


}
