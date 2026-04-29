package com.entity.view;

import com.entity.DiscusszhandianzhoubianEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
 

/**
 * 站点周边评论表
 * 后端返回视图实体辅助类   
 * （通常后端关联的表或者自定义的字段需要返回使用）
 * @author 
 * @email 
 * @date 2023-06-26 07:51:35
 */
@TableName("discusszhandianzhoubian")
public class DiscusszhandianzhoubianView  extends DiscusszhandianzhoubianEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public DiscusszhandianzhoubianView(){
	}
 
 	public DiscusszhandianzhoubianView(DiscusszhandianzhoubianEntity discusszhandianzhoubianEntity){
 	try {
			BeanUtils.copyProperties(this, discusszhandianzhoubianEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}
}
