package com.itheima.health.controller;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiNiuUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/24  16:31
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    //日志
    private static final Logger log = LoggerFactory.getLogger(SetmealController.class);
    @Reference
    private SetmealService setmealService;
    //文件上传
    @PostMapping("/upload")
    public Result upload(MultipartFile imgFile){
        //获取文件名
        String originalFilename = imgFile.getOriginalFilename();
        //从.开始截取  获取文件名的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID  生成唯一的文件名（不允许上传同名的文件名）
        String imgName = UUID.randomUUID().toString() + suffix;
        //- 调用QiNiuUtils上传
        try {
            QiNiuUtils.uploadViaByte(imgFile.getBytes(),imgName);
            //返回图片名和域名imgName: 图片名domain: 域名
            Map<String,String> resultMap = new HashMap<String,String>();
            resultMap.put("imgName",imgName);
            resultMap.put("domain",QiNiuUtils.DOMAIN);
            //封装集合和上传信息 到Result  响应给前端
            return new Result(true, MessageConstant.UPLOAD_SUCCESS,resultMap);
        } catch (IOException e) {
            log.error("上传文件失败",e);
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
    }
    //添加套餐
    @PostMapping("/add")
    public Result add(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        //调用业务层代码添加
        setmealService.add(setmeal,checkgroupIds);
        //封装添加信息 返回
        return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
    }
    //分页查询套餐
    @PostMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean){
        //调用业务层代码查询  用PageResult接收
        PageResult<Setmeal> pageResult = setmealService.findPage(queryPageBean);
        //封装查询信息
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,pageResult);
    }
    //通过id查询套餐信息
    @GetMapping("/findById")
    public Result findById(int id){
        //调用业务层方法通过id 查询套餐信息
        Setmeal setmeal = setmealService.findById(id);
        //创建一个Map  用来封装前端所需的结果  前端需要这样封装信息
        Map<String, Object> resultMap = new HashMap<String, Object>();
        //用写好的工具类获取domain装进Map
        resultMap.put("domain",QiNiuUtils.DOMAIN);
        //把查询到的setmeal对象装进Map
        resultMap.put("setmeal",setmeal);
        //返回查询结果
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,resultMap);
    }
    //通过id查询勾选的检查组id集合
    @GetMapping("/findCheckgroupIdsBySetmealId")
    public Result findCheckgroupIdsBySetmealId(int id ){
        //调用业务方法通过套餐id查询所关联的检查组id集合
        List<Integer> ids =setmealService .findCheckgroupIdsBySetmealId(id);
        //返回查询信息和查询到的检查组id集合
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,ids);
    }
    //更新套餐信息
    @PostMapping("/update")
    public Result update(@RequestBody Setmeal setmeal, Integer[] checkgroupIds){
        //通过传入的setmeal对象和检查组id数组 更新套餐信息
        setmealService.update(setmeal,checkgroupIds);
        //返回更新结果
        return new Result(true,"更新套餐信息成功");
    }
    //删除套餐信息
    @PostMapping("/deleteById")
    public Result deleteById(int id ){
        //通过传入的套餐id删除套餐信息
        setmealService.deleteById(id);
        //返回删除结果
        return new Result(true,"删除套餐信息成功");
    }
}
