package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.service.MemberService;
import com.itheima.health.service.ReportService;
import com.itheima.health.service.SetmealService;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.common.Context;
import org.jxls.transform.poi.PoiContext;
import org.jxls.util.JxlsHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/30  15:03
 */
@RestController
@RequestMapping("/report")
public class ReportController {
    @Reference
    private MemberService memberService;
    @Reference
    private SetmealService setmealService;
    @Reference
    private ReportService reportService;
    //查询月份集合还有当月的会员数量集合 折线图
    @GetMapping("/getMemberReport")
    public Result getMemberReport(){
        //创建集合用来装月份
        List<String> months = new ArrayList<String>();
        //获取日历对象
        Calendar calendar = Calendar.getInstance();
        //获取年份然后减一  得到去年的
        calendar.add(Calendar.YEAR,-1);
        //设置日期格式  要（2019-10）这样的数据格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        //循环遍历12次  每次加一个月 获取去年到今年的月份加入集合
        for (int i = 0; i < 12; i++) {
            //加一个月 然后加入月份集合
            calendar.add(Calendar.MONTH,1);
            //获取日期  修改格式  加入集合
            months.add(simpleDateFormat.format(calendar.getTime()));
        }
        //调用业务层的方法  传入数据集合 查出截止到当前月的会员数量集合
        List<Integer> memberCount = memberService.getMemberReport(months);
        //把月份集合还有会员数量集合放入Map中
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("months",months);
        resultMap.put("memberCount",memberCount);
        //封装到result对象返回
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_FAIL,resultMap);
    }
    //套餐数量饼图
    @GetMapping("/getSetmealReport")
    public Result getSetmealReport(){
        //调用业务层代码查询套餐预约占比
        //需要的数据类型是一个map{value ，name}
        List<Map<String, Object>> list = setmealService.findSetmealCount();
        //遍历list  取出map中的name  将其转换为String 然后存入setmealNames集合中
        //实现方式1  stream流 用map转
        //List<String> setmealNames = list.stream().map(m->(String)m.get("name")).collect(Collectors.toList());
        //实现方式2 循环
        //创建一个setmealNames集合（套餐名集合）
        List<String> setmealNames = new ArrayList<String>();
        for (Map<String, Object> map : list) {
            //取出数据 转换为String
           String setmealName =(String) map.get("name");
           //加入数据到name集合
           setmealNames.add(setmealName);
        }
        //前端需要的数据
        // Map<String,Object>;
        //map.put(“setmealNames”,List<String>);
        //map.put(“setmealCount”,List<Map>)
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("setmealNames",setmealNames);
        resultMap.put("setmealCount",list);
        //返回查询结果
        return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL,resultMap);
    }
    //预约数据统计
    @GetMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        //调用业务层方法查询所需要的数据
        Map<String,Object> reportData = reportService.getBusinessReportData();
        //返回查询结果和结果集
        return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS,reportData);
    }

    //导出报表
    @GetMapping("/exportBusinessReport")
    public void exportBusinessReport(HttpServletRequest request, HttpServletResponse response){
        // 调用业务层方法查询报表数据
        Map<String,Object> reportData = reportService.getBusinessReportData();
        //获取真实路径下的模板文件
        String template = request.getSession().getServletContext().getRealPath("/template/report_template2.xlsx");
        // 创建工作簿，读取模板
        // 数据模型
        Context context = new PoiContext();
        context.putVar("obj", reportData);
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");
            // 把数据模型中的数据填充到文件中
            JxlsHelper.getInstance().processTemplate(new FileInputStream(template),response.getOutputStream(),context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //导出报表（直接操作工作簿添加数据）
    @GetMapping("/exportBusinessReport1")
    public void exportBusinessReport1(HttpServletRequest request, HttpServletResponse response){
        //首先调用业务层代码查询所需添加的数据
        Map<String,Object> reportData = reportService.getBusinessReportData();
        //获取真实路径下的模板文件
        String template = request.getSession().getServletContext().getRealPath("/template/report_template.xlsx");
        // 创建工作簿，读取模板   放在try里可以自动关流
        try (Workbook wk = new XSSFWorkbook(template);) {
            // 获取工作表 第一张表（从0开始）
            Sheet sht = wk.getSheetAt(0);
            //添加时间       row行  cell 列
            sht.getRow(2).getCell(5).setCellValue(((String) reportData.get("reportDate")));
            //----------------------------会员数据添加----------------------------
            //本日新增会员数   总会员数    本周新增会员数    本月新增会员数
            sht.getRow(4).getCell(5).setCellValue((Integer)reportData.get("todayNewMember"));
            sht.getRow(4).getCell(7).setCellValue((Integer)reportData.get("totalMember"));
            sht.getRow(5).getCell(5).setCellValue((Integer)reportData.get("thisWeekNewMember"));
            sht.getRow(5).getCell(7).setCellValue((Integer)reportData.get("thisMonthNewMember"));
            //----------------------------预约到诊数据添加------------------------
            //今日预约数  今日到诊数  本周预约数  本周到诊数  本月预约数 本月到诊数
            sht.getRow(7).getCell(5).setCellValue((Integer)reportData.get("todayOrderNumber"));
            sht.getRow(7).getCell(7).setCellValue((Integer)reportData.get("todayVisitsNumber"));
            sht.getRow(8).getCell(5).setCellValue((Integer)reportData.get("thisWeekOrderNumber"));
            sht.getRow(8).getCell(7).setCellValue((Integer)reportData.get("thisWeekVisitsNumber"));
            sht.getRow(9).getCell(5).setCellValue((Integer)reportData.get("thisMonthOrderNumber"));
            sht.getRow(9).getCell(7).setCellValue((Integer)reportData.get("thisMonthVisitsNumber"));
            //----------------------------热门套餐添加----------------------------
            //套餐名称  预约数量  占比	备注
            List<Map> hotSetmeal = (List<Map>) reportData.get("hotSetmeal");
            int rowIndex = 12;
            for (Map map : hotSetmeal) {
                Row row = sht.getRow(rowIndex);
                row.getCell(4).setCellValue(((String) map.get("name")));
                row.getCell(5).setCellValue((Long)map.get("setmeal_count"));
                BigDecimal bigDecimal = (BigDecimal)map.get("proportion");
                row.getCell(6).setCellValue(bigDecimal.doubleValue());
                row.getCell(7).setCellValue((String) map.get("remark"));
                rowIndex++;
            }
            // 内容体设置   指定下载的文件格式为excel的文档
            response.setContentType("application/vnd.ms-excel");
            //工作簿中文名
            String filename = "运营数据统计.xlsx";
            // 原始数据字节流,ISO-8859-1 支持 -127->+127
            // 此处不能加入iso-8859-1（小容器装大数据导致数据丢失）
            byte[] bytes = filename.getBytes();
            filename = new String(bytes, "ISO-8859-1");
            System.out.println("转在iso-8859-1的字符串：" + filename);
            // 响应头信息设置
            response.setHeader("Content-Disposition","attachment;filename=" + filename);
            // 写到输出流
            wk.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
