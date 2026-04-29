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


import com.dao.DiscusschecixianluDao;
import com.entity.DiscusschecixianluEntity;
import com.service.DiscusschecixianluService;
import com.entity.vo.DiscusschecixianluVO;
import com.entity.view.DiscusschecixianluView;

@Service("discusschecixianluService")
public class DiscusschecixianluServiceImpl extends ServiceImpl<DiscusschecixianluDao, DiscusschecixianluEntity> implements DiscusschecixianluService {
	

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<DiscusschecixianluEntity> page = this.selectPage(
                new Query<DiscusschecixianluEntity>(params).getPage(),
                new EntityWrapper<DiscusschecixianluEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<DiscusschecixianluEntity> wrapper) {
		  Page<DiscusschecixianluView> page =new Query<DiscusschecixianluView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<DiscusschecixianluVO> selectListVO(Wrapper<DiscusschecixianluEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public DiscusschecixianluVO selectVO(Wrapper<DiscusschecixianluEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<DiscusschecixianluView> selectListView(Wrapper<DiscusschecixianluEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public DiscusschecixianluView selectView(Wrapper<DiscusschecixianluEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
