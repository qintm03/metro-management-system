package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.ZhandianchaxunEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.ZhandianchaxunVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.ZhandianchaxunView;


/**
 * 站点查询
 *
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
public interface ZhandianchaxunService extends IService<ZhandianchaxunEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<ZhandianchaxunVO> selectListVO(Wrapper<ZhandianchaxunEntity> wrapper);
   	
   	ZhandianchaxunVO selectVO(@Param("ew") Wrapper<ZhandianchaxunEntity> wrapper);
   	
   	List<ZhandianchaxunView> selectListView(Wrapper<ZhandianchaxunEntity> wrapper);
   	
   	ZhandianchaxunView selectView(@Param("ew") Wrapper<ZhandianchaxunEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<ZhandianchaxunEntity> wrapper);
   	

}

