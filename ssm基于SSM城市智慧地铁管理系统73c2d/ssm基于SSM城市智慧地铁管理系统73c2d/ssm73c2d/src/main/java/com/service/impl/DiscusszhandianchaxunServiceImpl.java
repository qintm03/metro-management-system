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


import com.dao.DiscusszhandianchaxunDao;
import com.entity.DiscusszhandianchaxunEntity;
import com.service.DiscusszhandianchaxunService;
import com.entity.vo.DiscusszhandianchaxunVO;
import com.entity.view.DiscusszhandianchaxunView;

@Service("discusszhandianchaxunService")
public class DiscusszhandianchaxunServiceImpl extends ServiceImpl<DiscusszhandianchaxunDao, DiscusszhandianchaxunEntity> implements DiscusszhandianchaxunService {
	

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<DiscusszhandianchaxunEntity> page = this.selectPage(
                new Query<DiscusszhandianchaxunEntity>(params).getPage(),
                new EntityWrapper<DiscusszhandianchaxunEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<DiscusszhandianchaxunEntity> wrapper) {
		  Page<DiscusszhandianchaxunView> page =new Query<DiscusszhandianchaxunView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<DiscusszhandianchaxunVO> selectListVO(Wrapper<DiscusszhandianchaxunEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public DiscusszhandianchaxunVO selectVO(Wrapper<DiscusszhandianchaxunEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<DiscusszhandianchaxunView> selectListView(Wrapper<DiscusszhandianchaxunEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public DiscusszhandianchaxunView selectView(Wrapper<DiscusszhandianchaxunEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
