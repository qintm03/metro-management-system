package com.entity.view;

import com.entity.DiscusschecixianluEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
 

/**
 * 车次线路评论表
 * 后端返回视图实体辅助类   
 * （通常后端关联的表或者自定义的字段需要返回使用）
 * @author 
 * @email 
 * @date 2023-06-26 07:51:35
 */
@TableName("discusschecixianlu")
public class DiscusschecixianluView  extends DiscusschecixianluEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public DiscusschecixianluView(){
	}
 
 	public DiscusschecixianluView(DiscusschecixianluEntity discusschecixianluEntity){
 	try {
			BeanUtils.copyProperties(this, discusschecixianluEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}
}
