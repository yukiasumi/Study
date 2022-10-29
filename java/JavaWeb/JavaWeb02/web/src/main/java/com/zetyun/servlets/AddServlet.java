package com.zetyun.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String sex = req.getParameter("sex");
        String ageStr = req.getParameter("age");
        Integer age = Integer.parseInt(ageStr);
        String id = req.getParameter("id");
        System.out.println("name = "+name);
        System.out.println("sex = "+sex);
        System.out.println("age = "+age);
        System.out.println("id = "+id);
    }
}
