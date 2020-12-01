package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.dao.OrderDao;
import com.itheima.health.service.ReportService;
import com.itheima.health.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/12/1  16:12
 */
@Service(interfaceClass = ReportService.class)
public class ReportServiceImpl implements ReportService {
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;


    @Override
    public Map<String, Object> getBusinessReportData() {
        //设置日期格式yyyy-MM-dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期 格式化
        Date today = new Date();
        String todayStr = sdf.format(today);
        //reportDate

        //获取特定日期（后面要用）
        String reportDate = todayStr;
        //周一
        String monday  = sdf.format(DateUtils.getThisWeekMonday());
        //周日
        String sunday  = sdf.format(DateUtils.getSundayOfThisWeek());
        //1号
        String firstDayOfThisMonth = sdf.format(DateUtils.getFirstDay4ThisMonth());
        // 本月最后一天
        String lastDayOfThisMonth = sdf.format(DateUtils.getLastDayOfThisMonth());

        //----------------------------会员数据统计----------------------------
        //todayNewMember 本日新增会员数  通过当前日期查
        Integer todayNewMember = memberDao.findMemberCountByDate(todayStr);
        //totalMember 总会员数 直接查总会员数
        Integer totalMember = memberDao.findMemberTotalCount();
        //thisWeekNewMember 本周新增会员数（不会超过今天 所以是在周一之后 今天之前）
        Integer thisWeekNewMember = memberDao.findMemberCountAfterDate(monday);
        //thisMonthNewMember 本月新增会员数（不会超过今天 所以是在1号之后 今天之前）
        Integer thisMonthNewMember = memberDao.findMemberCountAfterDate(firstDayOfThisMonth);

        //----------------------------预约到诊数据统计----------------------------
        //todayOrderNumber 今日预约数 通过当前日期查
        Integer todayOrderNumber = orderDao.findOrderCountByDate(todayStr);
        //todayVisitsNumber 今日到诊数 通过当前日期查
        Integer todayVisitsNumber = orderDao.findVisitsCountByDate(todayStr);
        //thisWeekOrderNumber 本周预约数(可以提前预约后面的  所以就是周一到周日之前)
        Integer thisWeekOrderNumber =orderDao.findOrderCountBetweenDate(monday,sunday);
        //thisWeekVisitsNumber 本周到诊数（到诊需要本人去 数据是周一到当前时间的到诊数）
        Integer thisWeekVisitsNumber = orderDao.findVisitsCountAfterDate(monday);
        //thisMonthOrderNumber 本月预约数(可以提前预约后面的  所以就是1号到当月最后一天之前)
        Integer thisMonthOrderNumber = orderDao.findOrderCountBetweenDate(firstDayOfThisMonth,lastDayOfThisMonth);
        //thisMonthVisitsNumber 本月到诊数（到诊需要本人去 数据是所以就是1号到今天的到诊数）
        Integer thisMonthVisitsNumber = orderDao.findVisitsCountAfterDate(firstDayOfThisMonth);

        //----------------------------热门套餐----------------------------
        //hotSetmeal 热门套餐
        List<Map> hotSetmeal = orderDao.findHotSetmeal();
        //创建Map集合  封装数据  12 指定个数  不用扩容
        Map<String,Object> reportData = new HashMap<String,Object>(12);
        reportData.put("reportDate",reportDate);
        reportData.put("todayNewMember",todayNewMember);
        reportData.put("totalMember",totalMember);
        reportData.put("thisWeekNewMember",thisWeekNewMember);
        reportData.put("thisMonthNewMember",thisMonthNewMember);
        reportData.put("todayOrderNumber",todayOrderNumber);
        reportData.put("todayVisitsNumber",todayVisitsNumber);
        reportData.put("thisWeekOrderNumber",thisWeekOrderNumber);
        reportData.put("thisWeekVisitsNumber",thisWeekVisitsNumber);
        reportData.put("thisMonthOrderNumber",thisMonthOrderNumber);
        reportData.put("thisMonthVisitsNumber",thisMonthVisitsNumber);
        reportData.put("hotSetmeal",hotSetmeal);

        return reportData;
    }
}
