package com.example.javaweb.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//演示服务器揣内部转发以及客户端重定向
public class TestServlet06 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("test06...");
        //服务器端内部转发
        //req.getRequestDispatcher("test07").forward(req,resp);
        //客户端重定向
        resp.sendRedirect("test07");
    }
}
