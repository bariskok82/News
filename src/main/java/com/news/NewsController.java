package com.news;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

@WebServlet(value={"/news.nhn"})
@MultipartConfig(maxFileSize=1024*1024*2, location="c:/Temp/img")
public class NewsController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private NewsDAO dao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.dao = new NewsDAO();
        this.dao.open();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getParameter("action");

        Method m;
        String view = null;

        if (action == null) {
            action = "listNews";
        }

        try {
            m = this.getClass().getMethod(action, HttpServletRequest.class);
            view = (String)m.invoke(this, request);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            request.setAttribute("error", "잘못된 파라미터");
            view = "newsList.jsp";
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (view.startsWith("redirect:/")) {
            response.sendRedirect(view.substring("redirect:/".length()));
        } else {
            request.getRequestDispatcher(view).forward(request, response);
        }
    }

    public String addNews(HttpServletRequest request) {
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
            request.setAttribute("error", "등록 과정에서 문제 발생");
            return this.listNews(request);
        }
        return "redirect:/news.nhn?action=listNews";
    }

    public String delNews(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            this.dao.delNews(id);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "삭제 과정에서 문제 발생");
            return this.listNews(request);
        }
        return "redirect:/news.nhn?action=listNews";
    }

    public String listNews(HttpServletRequest request) {
        try {
            List<News> list = this.dao.getAll();
            request.setAttribute("newslist", list);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "조회 과정에서 문제 발생");
        }
        return "newsList.jsp";
    }
    
    public String getNews(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            News item = this.dao.getNews(id);
            request.setAttribute("news", item);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "조회 과정에서 문제 발생");
        }
        return "newsList.jsp";
    }

    private String getFilename(Part part) {
        String header = part.getHeader("content-disposition");
        int start = header.indexOf("filename=");
        return header.substring(start + 10, header.length() - 1);
    }
}