package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Zhandianzhoubian {

    private Integer id;

    private String zhandianmingcheng;

    private String tupian;

    private String daolumingcheng;

    private String xiangxidizhi;

    private String zhoubiangongjiao;

    private String zhoubianqingkuang;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date gengxinshijian;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

}
