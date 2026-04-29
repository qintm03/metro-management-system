package com.entity.vo;

import com.entity.ZhandianchaxunEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
 

/**
 * 站点查询
 * 手机端接口返回实体辅助类 
 * （主要作用去除一些不必要的字段）
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
public class ZhandianchaxunVO  implements Serializable {
	private static final long serialVersionUID = 1L;

	 			
	/**
	 * 图片
	 */
	
	private String tupian;
		
	/**
	 * 道路名称
	 */
	
	private String daolumingcheng;
		
	/**
	 * 详细地址
	 */
	
	private String xiangxidizhi;
		
	/**
	 * 经过站点
	 */
	
	private String jingguozhandian;
		
	/**
	 * 附近建筑
	 */
	
	private String fujinjianzhu;
		
	/**
	 * 更新时间
	 */
		
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 
	private Date gengxinshijian;
				
	
	/**
	 * 设置：图片
	 */
	 
	public void setTupian(String tupian) {
		this.tupian = tupian;
	}
	
	/**
	 * 获取：图片
	 */
	public String getTupian() {
		return tupian;
	}
				
	
	/**
	 * 设置：道路名称
	 */
	 
	public void setDaolumingcheng(String daolumingcheng) {
		this.daolumingcheng = daolumingcheng;
	}
	
	/**
	 * 获取：道路名称
	 */
	public String getDaolumingcheng() {
		return daolumingcheng;
	}
				
	
	/**
	 * 设置：详细地址
	 */
	 
	public void setXiangxidizhi(String xiangxidizhi) {
		this.xiangxidizhi = xiangxidizhi;
	}
	
	/**
	 * 获取：详细地址
	 */
	public String getXiangxidizhi() {
		return xiangxidizhi;
	}
				
	
	/**
	 * 设置：经过站点
	 */
	 
	public void setJingguozhandian(String jingguozhandian) {
		this.jingguozhandian = jingguozhandian;
	}
	
	/**
	 * 获取：经过站点
	 */
	public String getJingguozhandian() {
		return jingguozhandian;
	}
				
	
	/**
	 * 设置：附近建筑
	 */
	 
	public void setFujinjianzhu(String fujinjianzhu) {
		this.fujinjianzhu = fujinjianzhu;
	}
	
	/**
	 * 获取：附近建筑
	 */
	public String getFujinjianzhu() {
		return fujinjianzhu;
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
			
}
