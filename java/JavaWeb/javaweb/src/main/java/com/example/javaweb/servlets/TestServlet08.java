package com.example.javaweb.servlets;


import com.example.jdbc.dao.StuDao;
import com.example.jdbc.dao.impl.ImplStuDao;
import com.example.jdbc.pojo.Student;
import com.example.jdbc.utils.DButil;
import com.example.myspringmvc.ViewBaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Repeatable;
import java.util.List;

@WebServlet("/test08")
public class TestServlet08 extends ViewBaseServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StuDao stu = new ImplStuDao();
        List<Student> list = DButil.getStudent(stu.getList());
/*        req.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();*/
        HttpSession session =  req.getSession();

/*
        for (Student student : list) {
            writer.print(student.toString()+"<br>");
            System.out.println(student.toString());
        }*/
        session.setAttribute("student", list);
        //此处的视图名称是 index
        //那么thymeleaf会将这个 逻辑视图名称 对应到 物理视图 名称上去
        //逻辑视图名称 ：   index
        //物理视图名称 ：   view-prefix + 逻辑视图名称 + view-suffix
        //所以真实的视图名称是：      /       index       .html
        super.processTemplate("index",req,resp);
    }
}
