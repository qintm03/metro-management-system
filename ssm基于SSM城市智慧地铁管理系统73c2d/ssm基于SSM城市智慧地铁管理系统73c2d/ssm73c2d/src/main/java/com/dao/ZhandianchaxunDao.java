package com.dao;

import com.entity.ZhandianchaxunEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.ZhandianchaxunVO;
import com.entity.view.ZhandianchaxunView;


/**
 * 站点查询
 * 
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
public interface ZhandianchaxunDao extends BaseMapper<ZhandianchaxunEntity> {
	
	List<ZhandianchaxunVO> selectListVO(@Param("ew") Wrapper<ZhandianchaxunEntity> wrapper);
	
	ZhandianchaxunVO selectVO(@Param("ew") Wrapper<ZhandianchaxunEntity> wrapper);
	
	List<ZhandianchaxunView> selectListView(@Param("ew") Wrapper<ZhandianchaxunEntity> wrapper);

	List<ZhandianchaxunView> selectListView(Pagination page,@Param("ew") Wrapper<ZhandianchaxunEntity> wrapper);
	
	ZhandianchaxunView selectView(@Param("ew") Wrapper<ZhandianchaxunEntity> wrapper);
	

}
