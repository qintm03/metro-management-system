package com.example.springboot.entity;

import java.util.List;
import lombok.Data;

@Data
public class Page<T> {
    private int pageNum;
    private int pageSize;
    private int total;
    private List<T> data;
}
