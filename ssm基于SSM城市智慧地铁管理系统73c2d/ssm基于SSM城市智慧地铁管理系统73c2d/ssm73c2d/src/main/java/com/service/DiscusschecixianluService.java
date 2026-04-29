package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.DiscusschecixianluEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.DiscusschecixianluVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.DiscusschecixianluView;


/**
 * 车次线路评论表
 *
 * @author 
 * @email 
 * @date 2023-06-26 07:51:35
 */
public interface DiscusschecixianluService extends IService<DiscusschecixianluEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<DiscusschecixianluVO> selectListVO(Wrapper<DiscusschecixianluEntity> wrapper);
   	
   	DiscusschecixianluVO selectVO(@Param("ew") Wrapper<DiscusschecixianluEntity> wrapper);
   	
   	List<DiscusschecixianluView> selectListView(Wrapper<DiscusschecixianluEntity> wrapper);
   	
   	DiscusschecixianluView selectView(@Param("ew") Wrapper<DiscusschecixianluEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<DiscusschecixianluEntity> wrapper);
   	

}

