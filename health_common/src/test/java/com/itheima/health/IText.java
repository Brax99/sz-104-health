package com.itheima.health;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/12/1  20:14
 */
public class IText {
    @Test
    public void testItext() throws Exception {
        // 创建文件对象
        Document doc = new Document();
        // 设置文件存储
        PdfWriter.getInstance(doc,new FileOutputStream(new File("d:\\iText.pdf")));
        // 打开文档
        doc.open();
        doc.add(new Paragraph("Hello World"));
        // 关闭文档
        doc.close();
    }
    @Test
    public void testItext1() throws Exception {
        // 创建文件对象
        Document doc = new Document();
        // 设置文件存储
        PdfWriter.getInstance(doc,new FileOutputStream(new File("d:\\iText.pdf")));
        // 打开文档
        doc.open();
        // 添加段落
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        doc.add(new Paragraph("你好，传智播客", new Font(bfChinese)));
        //doc.add(new Paragraph("Hello 传智播客"));
        // 关闭文档
        doc.close();
    }

}
