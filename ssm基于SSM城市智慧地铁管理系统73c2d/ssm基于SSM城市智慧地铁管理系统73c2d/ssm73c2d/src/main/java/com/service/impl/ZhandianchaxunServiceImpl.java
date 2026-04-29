package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;


import com.dao.ZhandianchaxunDao;
import com.entity.ZhandianchaxunEntity;
import com.service.ZhandianchaxunService;
import com.entity.vo.ZhandianchaxunVO;
import com.entity.view.ZhandianchaxunView;

@Service("zhandianchaxunService")
public class ZhandianchaxunServiceImpl extends ServiceImpl<ZhandianchaxunDao, ZhandianchaxunEntity> implements ZhandianchaxunService {
	

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ZhandianchaxunEntity> page = this.selectPage(
                new Query<ZhandianchaxunEntity>(params).getPage(),
                new EntityWrapper<ZhandianchaxunEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ZhandianchaxunEntity> wrapper) {
		  Page<ZhandianchaxunView> page =new Query<ZhandianchaxunView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<ZhandianchaxunVO> selectListVO(Wrapper<ZhandianchaxunEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public ZhandianchaxunVO selectVO(Wrapper<ZhandianchaxunEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<ZhandianchaxunView> selectListView(Wrapper<ZhandianchaxunEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public ZhandianchaxunView selectView(Wrapper<ZhandianchaxunEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
