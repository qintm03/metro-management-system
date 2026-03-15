package com.example.springboot.entity;

import lombok.Data;


@Data
public class Admin extends Account {
    private Integer id;
    private String name;
    private String username;
    private String password;
    private String role;
}
