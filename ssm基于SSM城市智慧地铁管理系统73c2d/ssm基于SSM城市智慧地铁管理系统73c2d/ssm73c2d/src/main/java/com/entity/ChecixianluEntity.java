package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;


/**
 * 车次线路
 * 数据库通用操作实体类（普通增删改查）
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
@TableName("checixianlu")
public class ChecixianluEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public ChecixianluEntity() {
		
	}
	
	public ChecixianluEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 主键id
	 */
	@TableId
	private Long id;
	/**
	 * 线路名称
	 */
					
	private String xianlumingcheng;
	
	/**
	 * 路线图片
	 */
					
	private String luxiantupian;
	
	/**
	 * 线路分类
	 */
					
	private String xianlufenlei;
	
	/**
	 * 始发站点
	 */
					
	private String shifazhandian;
	
	/**
	 * 终点站点
	 */
					
	private String zhongdianzhandian;
	
	/**
	 * 途径站点
	 */
					
	private String tujingzhandian;
	
	/**
	 * 车次
	 */
					
	private String checi;
	
	/**
	 * 换乘信息
	 */
					
	private String huanchengxinxi;
	
	/**
	 * 运行时间
	 */
					
	private String yunxingshijian;
	
	/**
	 * 路线详情
	 */
					
	private String luxianxiangqing;
	
	/**
	 * 更新时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date gengxinshijian;
	
	/**
	 * 费用说明
	 */
					
	private String feiyongshuoming;
	
	/**
	 * 换乘方案
	 */
					
	private String huanchengfangan;
	
	/**
	 * 点击次数
	 */
					
	private Integer clicknum;
	
	
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date addtime;

	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 设置：线路名称
	 */
	public void setXianlumingcheng(String xianlumingcheng) {
		this.xianlumingcheng = xianlumingcheng;
	}
	/**
	 * 获取：线路名称
	 */
	public String getXianlumingcheng() {
		return xianlumingcheng;
	}
	/**
	 * 设置：路线图片
	 */
	public void setLuxiantupian(String luxiantupian) {
		this.luxiantupian = luxiantupian;
	}
	/**
	 * 获取：路线图片
	 */
	public String getLuxiantupian() {
		return luxiantupian;
	}
	/**
	 * 设置：线路分类
	 */
	public void setXianlufenlei(String xianlufenlei) {
		this.xianlufenlei = xianlufenlei;
	}
	/**
	 * 获取：线路分类
	 */
	public String getXianlufenlei() {
		return xianlufenlei;
	}
	/**
	 * 设置：始发站点
	 */
	public void setShifazhandian(String shifazhandian) {
		this.shifazhandian = shifazhandian;
	}
	/**
	 * 获取：始发站点
	 */
	public String getShifazhandian() {
		return shifazhandian;
	}
	/**
	 * 设置：终点站点
	 */
	public void setZhongdianzhandian(String zhongdianzhandian) {
		this.zhongdianzhandian = zhongdianzhandian;
	}
	/**
	 * 获取：终点站点
	 */
	public String getZhongdianzhandian() {
		return zhongdianzhandian;
	}
	/**
	 * 设置：途径站点
	 */
	public void setTujingzhandian(String tujingzhandian) {
		this.tujingzhandian = tujingzhandian;
	}
	/**
	 * 获取：途径站点
	 */
	public String getTujingzhandian() {
		return tujingzhandian;
	}
	/**
	 * 设置：车次
	 */
	public void setCheci(String checi) {
		this.checi = checi;
	}
	/**
	 * 获取：车次
	 */
	public String getCheci() {
		return checi;
	}
	/**
	 * 设置：换乘信息
	 */
	public void setHuanchengxinxi(String huanchengxinxi) {
		this.huanchengxinxi = huanchengxinxi;
	}
	/**
	 * 获取：换乘信息
	 */
	public String getHuanchengxinxi() {
		return huanchengxinxi;
	}
	/**
	 * 设置：运行时间
	 */
	public void setYunxingshijian(String yunxingshijian) {
		this.yunxingshijian = yunxingshijian;
	}
	/**
	 * 获取：运行时间
	 */
	public String getYunxingshijian() {
		return yunxingshijian;
	}
	/**
	 * 设置：路线详情
	 */
	public void setLuxianxiangqing(String luxianxiangqing) {
		this.luxianxiangqing = luxianxiangqing;
	}
	/**
	 * 获取：路线详情
	 */
	public String getLuxianxiangqing() {
		return luxianxiangqing;
	}
	/**
	 * 设置：更新时间
	 */
	public void setGengxinshijian(Date gengxinshijian) {
		this.gengxinshijian = gengxinshijian;
	}
	/**
	 * 获取：更新时间
	 */
	public Date getGengxinshijian() {
		return gengxinshijian;
	}
	/**
	 * 设置：费用说明
	 */
	public void setFeiyongshuoming(String feiyongshuoming) {
		this.feiyongshuoming = feiyongshuoming;
	}
	/**
	 * 获取：费用说明
	 */
	public String getFeiyongshuoming() {
		return feiyongshuoming;
	}
	/**
	 * 设置：换乘方案
	 */
	public void setHuanchengfangan(String huanchengfangan) {
		this.huanchengfangan = huanchengfangan;
	}
	/**
	 * 获取：换乘方案
	 */
	public String getHuanchengfangan() {
		return huanchengfangan;
	}
	/**
	 * 设置：点击次数
	 */
	public void setClicknum(Integer clicknum) {
		this.clicknum = clicknum;
	}
	/**
	 * 获取：点击次数
	 */
	public Integer getClicknum() {
		return clicknum;
	}

}
