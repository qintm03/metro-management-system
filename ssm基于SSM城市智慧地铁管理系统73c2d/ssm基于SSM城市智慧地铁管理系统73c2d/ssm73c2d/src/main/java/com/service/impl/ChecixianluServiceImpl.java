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


import com.dao.ChecixianluDao;
import com.entity.ChecixianluEntity;
import com.service.ChecixianluService;
import com.entity.vo.ChecixianluVO;
import com.entity.view.ChecixianluView;

@Service("checixianluService")
public class ChecixianluServiceImpl extends ServiceImpl<ChecixianluDao, ChecixianluEntity> implements ChecixianluService {
	

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ChecixianluEntity> page = this.selectPage(
                new Query<ChecixianluEntity>(params).getPage(),
                new EntityWrapper<ChecixianluEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ChecixianluEntity> wrapper) {
		  Page<ChecixianluView> page =new Query<ChecixianluView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<ChecixianluVO> selectListVO(Wrapper<ChecixianluEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public ChecixianluVO selectVO(Wrapper<ChecixianluEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<ChecixianluView> selectListView(Wrapper<ChecixianluEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public ChecixianluView selectView(Wrapper<ChecixianluEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
