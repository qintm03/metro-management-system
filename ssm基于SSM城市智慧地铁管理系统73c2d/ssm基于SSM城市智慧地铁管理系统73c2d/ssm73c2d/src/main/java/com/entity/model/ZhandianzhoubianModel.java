package com.entity.model;

import com.entity.ZhandianzhoubianEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
 

/**
 * 站点周边
 * 接收传参的实体类  
 *（实际开发中配合移动端接口开发手动去掉些没用的字段， 后端一般用entity就够用了） 
 * 取自ModelAndView 的model名称
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
public class ZhandianzhoubianModel  implements Serializable {
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
	 * 周边公交
	 */
	
	private String zhoubiangongjiao;
		
	/**
	 * 周边情况
	 */
	
	private String zhoubianqingkuang;
		
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
	 * 设置：周边公交
	 */
	 
	public void setZhoubiangongjiao(String zhoubiangongjiao) {
		this.zhoubiangongjiao = zhoubiangongjiao;
	}
	
	/**
	 * 获取：周边公交
	 */
	public String getZhoubiangongjiao() {
		return zhoubiangongjiao;
	}
				
	
	/**
	 * 设置：周边情况
	 */
	 
	public void setZhoubianqingkuang(String zhoubianqingkuang) {
		this.zhoubianqingkuang = zhoubianqingkuang;
	}
	
	/**
	 * 获取：周边情况
	 */
	public String getZhoubianqingkuang() {
		return zhoubianqingkuang;
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
