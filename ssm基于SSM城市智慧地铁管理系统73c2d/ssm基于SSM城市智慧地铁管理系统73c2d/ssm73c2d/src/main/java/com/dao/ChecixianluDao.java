package com.dao;

import com.entity.ChecixianluEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.ChecixianluVO;
import com.entity.view.ChecixianluView;


/**
 * 车次线路
 * 
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
public interface ChecixianluDao extends BaseMapper<ChecixianluEntity> {
	
	List<ChecixianluVO> selectListVO(@Param("ew") Wrapper<ChecixianluEntity> wrapper);
	
	ChecixianluVO selectVO(@Param("ew") Wrapper<ChecixianluEntity> wrapper);
	
	List<ChecixianluView> selectListView(@Param("ew") Wrapper<ChecixianluEntity> wrapper);

	List<ChecixianluView> selectListView(Pagination page,@Param("ew") Wrapper<ChecixianluEntity> wrapper);
	
	ChecixianluView selectView(@Param("ew") Wrapper<ChecixianluEntity> wrapper);
	

}
