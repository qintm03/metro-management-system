package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.ChecixianluEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.ChecixianluVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.ChecixianluView;


/**
 * 车次线路
 *
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
public interface ChecixianluService extends IService<ChecixianluEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<ChecixianluVO> selectListVO(Wrapper<ChecixianluEntity> wrapper);
   	
   	ChecixianluVO selectVO(@Param("ew") Wrapper<ChecixianluEntity> wrapper);
   	
   	List<ChecixianluView> selectListView(Wrapper<ChecixianluEntity> wrapper);
   	
   	ChecixianluView selectView(@Param("ew") Wrapper<ChecixianluEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<ChecixianluEntity> wrapper);
   	

}

