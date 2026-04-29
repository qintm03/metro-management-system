package com.entity.view;

import com.entity.ZhandianchaxunEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
 

/**
 * 站点查询
 * 后端返回视图实体辅助类   
 * （通常后端关联的表或者自定义的字段需要返回使用）
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
@TableName("zhandianchaxun")
public class ZhandianchaxunView  extends ZhandianchaxunEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public ZhandianchaxunView(){
	}
 
 	public ZhandianchaxunView(ZhandianchaxunEntity zhandianchaxunEntity){
 	try {
			BeanUtils.copyProperties(this, zhandianchaxunEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}
}
