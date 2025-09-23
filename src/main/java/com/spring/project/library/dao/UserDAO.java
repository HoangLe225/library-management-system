package com.spring.project.library.dao;

import com.spring.project.library.model.User;

import java.util.List;

public interface UserDAO {
    List<User> findAll();
}
