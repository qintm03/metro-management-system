package com.dao;

import com.entity.DiscusschecixianluEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.DiscusschecixianluVO;
import com.entity.view.DiscusschecixianluView;


/**
 * 车次线路评论表
 * 
 * @author 
 * @email 
 * @date 2023-06-26 07:51:35
 */
public interface DiscusschecixianluDao extends BaseMapper<DiscusschecixianluEntity> {
	
	List<DiscusschecixianluVO> selectListVO(@Param("ew") Wrapper<DiscusschecixianluEntity> wrapper);
	
	DiscusschecixianluVO selectVO(@Param("ew") Wrapper<DiscusschecixianluEntity> wrapper);
	
	List<DiscusschecixianluView> selectListView(@Param("ew") Wrapper<DiscusschecixianluEntity> wrapper);

	List<DiscusschecixianluView> selectListView(Pagination page,@Param("ew") Wrapper<DiscusschecixianluEntity> wrapper);
	
	DiscusschecixianluView selectView(@Param("ew") Wrapper<DiscusschecixianluEntity> wrapper);
	

}
