package com.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import com.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.ChecixianluEntity;
import com.entity.view.ChecixianluView;

import com.service.ChecixianluService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;
import com.service.StoreupService;
import com.entity.StoreupEntity;

/**
 * 车次线路
 * 后端接口
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
@RestController
@RequestMapping("/checixianlu")
public class ChecixianluController {
    @Autowired
    private ChecixianluService checixianluService;



    @Autowired
    private StoreupService storeupService;

    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,ChecixianluEntity checixianlu, 
		HttpServletRequest request){

        EntityWrapper<ChecixianluEntity> ew = new EntityWrapper<ChecixianluEntity>();


		PageUtils page = checixianluService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, checixianlu), params), params));
        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,ChecixianluEntity checixianlu, 
		HttpServletRequest request){
        EntityWrapper<ChecixianluEntity> ew = new EntityWrapper<ChecixianluEntity>();

		PageUtils page = checixianluService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, checixianlu), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( ChecixianluEntity checixianlu){
       	EntityWrapper<ChecixianluEntity> ew = new EntityWrapper<ChecixianluEntity>();
      	ew.allEq(MPUtil.allEQMapPre( checixianlu, "checixianlu")); 
        return R.ok().put("data", checixianluService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ChecixianluEntity checixianlu){
        EntityWrapper< ChecixianluEntity> ew = new EntityWrapper< ChecixianluEntity>();
 		ew.allEq(MPUtil.allEQMapPre( checixianlu, "checixianlu")); 
		ChecixianluView checixianluView =  checixianluService.selectView(ew);
		return R.ok("查询车次线路成功").put("data", checixianluView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        ChecixianluEntity checixianlu = checixianluService.selectById(id);
		checixianlu.setClicknum(checixianlu.getClicknum()+1);
		checixianluService.updateById(checixianlu);
        return R.ok().put("data", checixianlu);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        ChecixianluEntity checixianlu = checixianluService.selectById(id);
		checixianlu.setClicknum(checixianlu.getClicknum()+1);
		checixianluService.updateById(checixianlu);
        return R.ok().put("data", checixianlu);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ChecixianluEntity checixianlu, HttpServletRequest request){
    	checixianlu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(checixianlu);

        checixianluService.insert(checixianlu);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ChecixianluEntity checixianlu, HttpServletRequest request){
    	checixianlu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(checixianlu);

        checixianluService.insert(checixianlu);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody ChecixianluEntity checixianlu, HttpServletRequest request){
        //ValidatorUtils.validateEntity(checixianlu);
        checixianluService.updateById(checixianlu);//全部更新
        return R.ok();
    }

    
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        checixianluService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	









}
