package com.dao;

import com.entity.ZhandianzhoubianEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.ZhandianzhoubianVO;
import com.entity.view.ZhandianzhoubianView;


/**
 * 站点周边
 * 
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
public interface ZhandianzhoubianDao extends BaseMapper<ZhandianzhoubianEntity> {
	
	List<ZhandianzhoubianVO> selectListVO(@Param("ew") Wrapper<ZhandianzhoubianEntity> wrapper);
	
	ZhandianzhoubianVO selectVO(@Param("ew") Wrapper<ZhandianzhoubianEntity> wrapper);
	
	List<ZhandianzhoubianView> selectListView(@Param("ew") Wrapper<ZhandianzhoubianEntity> wrapper);

	List<ZhandianzhoubianView> selectListView(Pagination page,@Param("ew") Wrapper<ZhandianzhoubianEntity> wrapper);
	
	ZhandianzhoubianView selectView(@Param("ew") Wrapper<ZhandianzhoubianEntity> wrapper);
	

}
