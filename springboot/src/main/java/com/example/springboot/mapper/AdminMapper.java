package com.example.springboot.mapper;

import com.example.springboot.entity.Admin;

public interface AdminMapper {

    
    Admin selectByUsername(String username);

}
