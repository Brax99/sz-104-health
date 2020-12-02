package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.Exception.MyException;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.pojo.Order;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.OrderService;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.DateUtils;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/28  19:56
 */
@RestController
@RequestMapping("/order")
public class OrderMobileController {
    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;

    @Reference
    private SetmealService setmealService;

    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String,String> paraMap){
        // 验证码的校验
        Jedis jedis = jedisPool.getResource();
        //获取请求参数手机号码
        String telephone = paraMap.get("telephone");
        //拼接redis中的的key 形成完整的验证码  用于校验
        String key = RedisMessageConstant.SENDTYPE_ORDER+":"+telephone;
        // 从redis取出验证码
        String codeInRedis = jedis.get(key);
        //取出的验证码为空  用户没获取验证码或者已经过期
        if(StringUtils.isEmpty(codeInRedis)){
            //返回结果让用户重新获取验证码
            return new Result(false, "请重新获取验证码!");
        }
        //校验提交的验证码与从redis中查询的验证码是否一致
        if(!codeInRedis.equals(paraMap.get("validateCode"))) {
            //不一致 用户输入的验证码不正确 返回结果
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        //一直  删除redis中的key
        //为什么删除   防无聊的人在有效期内疯狂发送请求 重复提交数据 删除让这个只能成功校验一次
        jedis.del(key);
        jedis.close();
        // 设置预约类型 health_mobile微信预约
        paraMap.put("orderType", Order.ORDERTYPE_WEIXIN);
        // 验证码校验成功
        //- 通过日期查询预约设置表
        String orderDateString = paraMap.get("orderDate");
        //日期校验
        Date orderDate = null;
        try {
            // 在此处校验日期的格式
            orderDate = DateUtils.parseString2Date(orderDateString);
        } catch (Exception e) {
            //抛出异常 日期格式不正确
            throw new MyException("预约日期格式不正确");
        }
        //可以调用服务进行预约
        Integer id = orderService.submit(paraMap,orderDate);
        //返回添加结果
        return new Result(true, MessageConstant.ORDER_SUCCESS,id);
    }
    @GetMapping("/findById")
    public Result findById(int id){
        // 通过id调用服务查询信息
        Map<String,Object> orderInfo = orderService.findOrderDetailById(id);
        //返回查询结果
        return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS,orderInfo);
    }
    //iTest导出pdf
    @GetMapping("/exportPdf")
    public void exportPdf(int id, HttpServletResponse res) throws Exception {
        //通过订单id查询订单信息 （要修改方法 得返回套餐id）
        Map<String, Object> orderDetail = orderService.findOrderDetailById(id);
        //获取套餐id  把字符串转成int
        Integer stemealId = (Integer) orderDetail.get("setmeal_id");
        //通过套餐id查询套餐下的检查组和检查项（已实现）
        Setmeal serviceDetail = setmealService.findDetailById(stemealId);
        //生成pdf的步骤（五个东东  1创建 2设置存储 3打开 4添加 5关闭）
        //1 创建
        Document doc = new Document();
        //设置内容的格式pdf (告诉我们要生成的文件类型)和设置文件名（xc）
        res.setContentType("application/pdf");
        res.setHeader("Content-Disposition","attachment;filename=orderInfo.pdf");
        //2设置文件储存和输出(输出)
        PdfWriter.getInstance(doc,res.getOutputStream());
        //3 打开
        doc.open();
        //中文问题（华文宋体）
        Font chinese = new Font(BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED));
        //4 添加
        doc.add(new Paragraph("体检人："+orderDetail.get("member"),chinese));
        doc.add(new Paragraph("体检套餐："+orderDetail.get("setmeal"),chinese));
        doc.add(new Paragraph("体检日期："+orderDetail.get("orderDate"),chinese));
        doc.add(new Paragraph("预约类型："+orderDetail.get("orderType"),chinese));
        //创建表格数字代表列  表格中行数不固定 设置列数  有三列（三个数据）就加多一行
        Table table = new Table(3);
        // ============= 表格样式 =================
        table.setWidth(80); // 宽度
        table.setBorder(1); // 边框
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); //水平对齐方式
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP); // 垂直对齐方式
        /*设置表格属性*/
        table.setBorderColor(new Color(0, 0, 255)); //将边框的颜色设置为蓝色(左边有按钮可以调颜色)
        table.setPadding(5);//设置表格与字体间的间距
        //table.setSpacing(2);//设置表格上下的间距
        table.setAlignment(Element.ALIGN_CENTER);//设置字体显示居中样式
        // ============= 表格样式 =================

        table.addCell(buildCell("项目名称",chinese));
        table.addCell(buildCell("项目内容",chinese));
        table.addCell(buildCell("项目解读",chinese));
        //从套餐详情中获取检查组信息
        List<CheckGroup> checkGroups = serviceDetail.getCheckGroups();
        //遍历检查组 然后添加检查组信息
        for (CheckGroup checkGroup : checkGroups) {
            // 添加检查组名称
            table.addCell(buildCell(checkGroup.getName(),chinese));
            // 获取检查组下的检查项信息
            List<CheckItem> checkItems = checkGroup.getCheckItems();
            //使用StringJoiner  让用空格展开然后展示
            StringJoiner joiner = new StringJoiner("");
            for (CheckItem checkItem : checkItems) {
                //让检查项的id用空格隔开然后展示
                joiner.add(checkItem.getName());
            }
            table.addCell(buildCell(joiner.toString(),chinese));
            // 添加检查组的remark
            table.addCell(buildCell(checkGroup.getRemark(),chinese));
        }
        //添加表格
        doc.add(table);
        //5 关闭
        doc.close();

    }
    // 传递内容和字体样式，生成单元格
    private Cell buildCell(String content, Font font)
            throws BadElementException {
        Phrase phrase = new Phrase(content, font);
        return new Cell(phrase);
    }
}
