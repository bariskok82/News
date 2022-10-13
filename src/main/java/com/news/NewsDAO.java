package com.news;

import java.sql.*;
import java.util.*;

public class NewsDAO {
    private final String JDBC_DRIVER = "org.h2.Driver";
    private final String JDBC_URL = "jdbc:h2:file:c:/Temp/newsDB";
    private Connection conn;

    public void open() {
        try {
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(JDBC_URL, "mine", "1234");
        } catch (Exception e) {
            e.printStackTrace();
            this.close();
        }
    }
    public void close() {
        try {
            this.conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.conn = null;
    }

    public List<News> getAll() throws Exception {
        List<News> list = new ArrayList<>();
        PreparedStatement stmt = this.conn.prepareStatement("select id, title, FORMATDATETIME(date,'yyyy-MM-dd hh:mm:ss') as cdate, author from news");
        ResultSet result = stmt.executeQuery();
        
        while (result.next()) {
            News item = new News();
            item.setId(result.getInt("id"));
            item.setTitle(result.getString("title"));
            item.setDate(result.getString("cdate"));
            item.setAuthor(result.getString("author"));
            list.add(item);
        }

        stmt.close();
        
        return list;
    }

    public News getNews(int id) throws Exception {
        News item = new News();
        PreparedStatement stmt = this.conn.prepareStatement("select id, title, img, FORMATDATETIME(date,'yyyy-MM-dd hh:mm:ss') as cdate, content, author from news where id=?");
        stmt.setInt(1, id);
        ResultSet result = stmt.executeQuery();
        result.next();

        item.setId(result.getInt("id"));
        item.setTitle(result.getString("title"));
        item.setImg(result.getString("img"));
        item.setDate(result.getString("cdate"));
        item.setContent(result.getString("content"));
        item.setAuthor(result.getString("author"));

        stmt.close();

        return item;
    }

    public void addNews(News item) throws Exception {
        PreparedStatement stmt = this.conn.prepareStatement("insert into news(title,img,date,content,author) values(?,?,CURRENT_TIMESTAMP(),?,?)");
        stmt.setString(1, item.getTitle());
        stmt.setString(2, item.getImg());
        stmt.setString(3, item.getContent());
        stmt.setString(4, item.getAuthor());
        stmt.executeUpdate();
        stmt.close();
    }

    public void delNews(int id) throws Exception {
        PreparedStatement stmt = this.conn.prepareStatement("delete from news where id=?");
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
    }
}