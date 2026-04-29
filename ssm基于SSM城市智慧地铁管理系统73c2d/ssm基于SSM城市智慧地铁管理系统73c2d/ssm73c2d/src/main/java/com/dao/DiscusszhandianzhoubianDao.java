package com.dao;

import com.entity.DiscusszhandianzhoubianEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.DiscusszhandianzhoubianVO;
import com.entity.view.DiscusszhandianzhoubianView;


/**
 * 站点周边评论表
 * 
 * @author 
 * @email 
 * @date 2023-06-26 07:51:35
 */
public interface DiscusszhandianzhoubianDao extends BaseMapper<DiscusszhandianzhoubianEntity> {
	
	List<DiscusszhandianzhoubianVO> selectListVO(@Param("ew") Wrapper<DiscusszhandianzhoubianEntity> wrapper);
	
	DiscusszhandianzhoubianVO selectVO(@Param("ew") Wrapper<DiscusszhandianzhoubianEntity> wrapper);
	
	List<DiscusszhandianzhoubianView> selectListView(@Param("ew") Wrapper<DiscusszhandianzhoubianEntity> wrapper);

	List<DiscusszhandianzhoubianView> selectListView(Pagination page,@Param("ew") Wrapper<DiscusszhandianzhoubianEntity> wrapper);
	
	DiscusszhandianzhoubianView selectView(@Param("ew") Wrapper<DiscusszhandianzhoubianEntity> wrapper);
	

}
