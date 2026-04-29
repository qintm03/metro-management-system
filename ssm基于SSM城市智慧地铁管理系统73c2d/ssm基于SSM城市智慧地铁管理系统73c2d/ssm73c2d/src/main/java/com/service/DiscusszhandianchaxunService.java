package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.DiscusszhandianchaxunEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.DiscusszhandianchaxunVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.DiscusszhandianchaxunView;


/**
 * 站点查询评论表
 *
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
public interface DiscusszhandianchaxunService extends IService<DiscusszhandianchaxunEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<DiscusszhandianchaxunVO> selectListVO(Wrapper<DiscusszhandianchaxunEntity> wrapper);
   	
   	DiscusszhandianchaxunVO selectVO(@Param("ew") Wrapper<DiscusszhandianchaxunEntity> wrapper);
   	
   	List<DiscusszhandianchaxunView> selectListView(Wrapper<DiscusszhandianchaxunEntity> wrapper);
   	
   	DiscusszhandianchaxunView selectView(@Param("ew") Wrapper<DiscusszhandianchaxunEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<DiscusszhandianchaxunEntity> wrapper);
   	

}

