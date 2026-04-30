package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Storeup;
import com.example.springboot.service.StoreupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/storeup")
public class StoreupController {

    @Autowired
    private StoreupService storeupService;

    @PostMapping
    public Result add(@RequestBody Storeup storeup) {
        int result = storeupService.add(storeup);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("收藏失败");
    }

    @PutMapping
    public Result update(@RequestBody Storeup storeup) {
        int result = storeupService.update(storeup);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int result = storeupService.delete(id);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("删除失败");
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        Storeup storeup = storeupService.findById(id);
        return Result.success(storeup);
    }

    @GetMapping
    public Result findAll() {
        List<Storeup> list = storeupService.findAll();
        return Result.success(list);
    }

    @GetMapping("/user/{userid}")
    public Result findByUserid(@PathVariable Integer userid) {
        List<Storeup> list = storeupService.findByUserid(userid);
        return Result.success(list);
    }

    @GetMapping("/check")
    public Result checkFavorite(@RequestParam Integer userid,
                                 @RequestParam String tablename,
                                 @RequestParam Integer refid) {
        boolean isFavorited = storeupService.isFavorited(userid, tablename, refid);
        return Result.success(isFavorited);
    }

    @PostMapping("/cancel")
    public Result cancelFavorite(@RequestParam Integer userid,
                                  @RequestParam String tablename,
                                  @RequestParam Integer refid) {
        int result = storeupService.cancelFavorite(userid, tablename, refid);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("取消收藏失败");
    }

    @PostMapping("/search")
    public Result search(@RequestBody Storeup storeup) {
        List<Storeup> list = storeupService.findByCondition(storeup);
        return Result.success(list);
    }

    @GetMapping("/count/{userid}")
    public Result countByUserid(@PathVariable Integer userid) {
        Integer count = storeupService.countByUserid(userid);
        return Result.success(count);
    }

}
