package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.ZhandianzhoubianEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.ZhandianzhoubianVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.ZhandianzhoubianView;


/**
 * 站点周边
 *
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
public interface ZhandianzhoubianService extends IService<ZhandianzhoubianEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<ZhandianzhoubianVO> selectListVO(Wrapper<ZhandianzhoubianEntity> wrapper);
   	
   	ZhandianzhoubianVO selectVO(@Param("ew") Wrapper<ZhandianzhoubianEntity> wrapper);
   	
   	List<ZhandianzhoubianView> selectListView(Wrapper<ZhandianzhoubianEntity> wrapper);
   	
   	ZhandianzhoubianView selectView(@Param("ew") Wrapper<ZhandianzhoubianEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<ZhandianzhoubianEntity> wrapper);
   	

}

