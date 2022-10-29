package com.example.jdbc.utils;

import com.example.jdbc.pojo.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DButil {
        static {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        public static Connection getConnection(){
            Connection connection=null;
            String url="jdbc:mysql://localhost:3306/t037?useUnicode=true&characterEncoding=UTF-8";
            String user="root";
            String password="root";
            try {
                connection = DriverManager.getConnection(url,user,password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return connection;
        }
        public static List<Student> getStudent(String sql){
            List<Student> list=new ArrayList<>();
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            Connection connection = getConnection();
            try {
                preparedStatement = connection.prepareStatement(sql);
                resultSet=preparedStatement.executeQuery();
                while (resultSet.next()){
                    Map<String,Object> map = new HashMap<>();
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        map.put(resultSet.getMetaData().getColumnLabel(i), resultSet.getObject(i));
                    }
                    Student student = new Student(Integer.parseInt(map.get("id").toString()), map.get("sname").toString(),Integer.parseInt(map.get("age").toString()),map.get("sex").toString());
                    list.add(student);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (preparedStatement != null) {
                    try {
                        preparedStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return list;
        }
}
