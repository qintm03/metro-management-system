package com.entity.view;

import com.entity.ChecixianluEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
 

/**
 * 车次线路
 * 后端返回视图实体辅助类   
 * （通常后端关联的表或者自定义的字段需要返回使用）
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
@TableName("checixianlu")
public class ChecixianluView  extends ChecixianluEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public ChecixianluView(){
	}
 
 	public ChecixianluView(ChecixianluEntity checixianluEntity){
 	try {
			BeanUtils.copyProperties(this, checixianluEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}
}
