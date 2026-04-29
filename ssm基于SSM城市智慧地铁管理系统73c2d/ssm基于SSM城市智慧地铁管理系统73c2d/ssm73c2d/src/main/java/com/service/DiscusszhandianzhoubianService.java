package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.DiscusszhandianzhoubianEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.DiscusszhandianzhoubianVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.DiscusszhandianzhoubianView;


/**
 * 站点周边评论表
 *
 * @author 
 * @email 
 * @date 2023-06-26 07:51:35
 */
public interface DiscusszhandianzhoubianService extends IService<DiscusszhandianzhoubianEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<DiscusszhandianzhoubianVO> selectListVO(Wrapper<DiscusszhandianzhoubianEntity> wrapper);
   	
   	DiscusszhandianzhoubianVO selectVO(@Param("ew") Wrapper<DiscusszhandianzhoubianEntity> wrapper);
   	
   	List<DiscusszhandianzhoubianView> selectListView(Wrapper<DiscusszhandianzhoubianEntity> wrapper);
   	
   	DiscusszhandianzhoubianView selectView(@Param("ew") Wrapper<DiscusszhandianzhoubianEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<DiscusszhandianzhoubianEntity> wrapper);
   	

}

