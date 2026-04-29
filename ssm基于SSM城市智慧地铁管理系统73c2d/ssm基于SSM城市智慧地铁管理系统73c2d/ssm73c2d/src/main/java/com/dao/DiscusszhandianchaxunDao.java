package com.dao;

import com.entity.DiscusszhandianchaxunEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.DiscusszhandianchaxunVO;
import com.entity.view.DiscusszhandianchaxunView;


/**
 * 站点查询评论表
 * 
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
public interface DiscusszhandianchaxunDao extends BaseMapper<DiscusszhandianchaxunEntity> {
	
	List<DiscusszhandianchaxunVO> selectListVO(@Param("ew") Wrapper<DiscusszhandianchaxunEntity> wrapper);
	
	DiscusszhandianchaxunVO selectVO(@Param("ew") Wrapper<DiscusszhandianchaxunEntity> wrapper);
	
	List<DiscusszhandianchaxunView> selectListView(@Param("ew") Wrapper<DiscusszhandianchaxunEntity> wrapper);

	List<DiscusszhandianchaxunView> selectListView(Pagination page,@Param("ew") Wrapper<DiscusszhandianchaxunEntity> wrapper);
	
	DiscusszhandianchaxunView selectView(@Param("ew") Wrapper<DiscusszhandianchaxunEntity> wrapper);
	

}
