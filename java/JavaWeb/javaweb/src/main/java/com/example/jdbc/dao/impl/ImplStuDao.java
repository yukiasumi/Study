package com.example.jdbc.dao.impl;

import com.example.jdbc.dao.StuDao;

public class ImplStuDao implements StuDao {
    @Override
    public String getList() {
       return "select * from student";
    }
}
