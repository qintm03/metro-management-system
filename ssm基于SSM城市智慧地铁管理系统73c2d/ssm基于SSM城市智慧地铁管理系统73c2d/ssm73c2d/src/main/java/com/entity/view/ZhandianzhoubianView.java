package com.entity.view;

import com.entity.ZhandianzhoubianEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
 

/**
 * 站点周边
 * 后端返回视图实体辅助类   
 * （通常后端关联的表或者自定义的字段需要返回使用）
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
@TableName("zhandianzhoubian")
public class ZhandianzhoubianView  extends ZhandianzhoubianEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public ZhandianzhoubianView(){
	}
 
 	public ZhandianzhoubianView(ZhandianzhoubianEntity zhandianzhoubianEntity){
 	try {
			BeanUtils.copyProperties(this, zhandianzhoubianEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}
}
