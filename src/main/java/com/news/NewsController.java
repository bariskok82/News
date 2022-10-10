package com.news;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import java.io.*;
import java.util.*;

@WebServlet(value={"/news.nhn"})
@MultipartConfig(maxFileSize=0x200000L, location="c:/Temp/img")
public class NewsController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private NewsDAO dao;
    private RequestDispatcher disp;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        this.dao = new NewsDAO();
        this.dao.open();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "listNews";
        }

        switch (action) {
            case "addNews":
                try {
                    News news = new News();
                    Part part = request.getPart("file");
                    String name = this.getFilename(part);
                    if (name != null && !name.isEmpty()) {
                        part.write(name);
                    }
                    news.setTitle(request.getParameter("title"));
                    news.setContent(request.getParameter("content"));
                    news.setAuthor(request.getParameter("author"));
                    news.setImg("/img/" + name);
                    this.dao.addNews(news);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                response.sendRedirect("/news.nhn?action=listNews");
                break;
            case "delNews":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    this.dao.delNews(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                response.sendRedirect("/news.nhn?action=listNews");
                break;
            case "listNews":
                try {
                    List<News> list = this.dao.getAll();
                    request.setAttribute("newslist", list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.disp = request.getRequestDispatcher("newsList.jsp");
                this.disp.forward(request, response);
                break;
            case "getNews":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    News item = this.dao.getNews(id);
                    request.setAttribute("news", item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.disp = request.getRequestDispatcher("newsView.jsp");
                this.disp.forward(request, response);
                break;
            default:
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    private String getFilename(Part part) {
        String string = part.getHeader("content-disposition");
        int name = string.indexOf("filename=");
        return string.substring(name + 10, string.length() - 1);
    }
}