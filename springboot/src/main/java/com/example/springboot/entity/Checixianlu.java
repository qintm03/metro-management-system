package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Checixianlu {

    private Integer id;

    private String xianlumingcheng;

    private String luxiantupian;

    private String xianlufenlei;

    private String shifazhandian;

    private String zhongdianzhandian;

    private String tujingzhandian;

    private String checi;

    private String huanchengxinxi;

    private String yunxingshijian;

    private String luxianxiangqing;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date gengxinshijian;

    private String feiyongshuoming;

    private String huanchengfangan;

    private Integer clicknum;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

}
