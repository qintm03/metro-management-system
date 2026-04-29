package com.entity.vo;

import com.entity.ChecixianluEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
 

/**
 * 车次线路
 * 手机端接口返回实体辅助类 
 * （主要作用去除一些不必要的字段）
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
public class ChecixianluVO  implements Serializable {
	private static final long serialVersionUID = 1L;

	 			
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
