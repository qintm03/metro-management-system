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


import com.dao.ZhandianzhoubianDao;
import com.entity.ZhandianzhoubianEntity;
import com.service.ZhandianzhoubianService;
import com.entity.vo.ZhandianzhoubianVO;
import com.entity.view.ZhandianzhoubianView;

@Service("zhandianzhoubianService")
public class ZhandianzhoubianServiceImpl extends ServiceImpl<ZhandianzhoubianDao, ZhandianzhoubianEntity> implements ZhandianzhoubianService {
	

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ZhandianzhoubianEntity> page = this.selectPage(
                new Query<ZhandianzhoubianEntity>(params).getPage(),
                new EntityWrapper<ZhandianzhoubianEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ZhandianzhoubianEntity> wrapper) {
		  Page<ZhandianzhoubianView> page =new Query<ZhandianzhoubianView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<ZhandianzhoubianVO> selectListVO(Wrapper<ZhandianzhoubianEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public ZhandianzhoubianVO selectVO(Wrapper<ZhandianzhoubianEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<ZhandianzhoubianView> selectListView(Wrapper<ZhandianzhoubianEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public ZhandianzhoubianView selectView(Wrapper<ZhandianzhoubianEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
