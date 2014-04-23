package com.github.duantihua.tmcrawler;

import static com.github.duantihua.tmcrawler.ProductAttributes.Code;
import static com.github.duantihua.tmcrawler.ProductAttributes.DefaultPrice;
import static com.github.duantihua.tmcrawler.ProductAttributes.Href;
import static com.github.duantihua.tmcrawler.ProductAttributes.ImageUrl;
import static com.github.duantihua.tmcrawler.ProductAttributes.Price;
import static com.github.duantihua.tmcrawler.ProductAttributes.Title;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.beangle.commons.collection.CollectUtils;

public class Excel implements Writer {
  private XSSFWorkbook wb = new XSSFWorkbook();
  private XSSFSheet sheet = wb.createSheet("data");
  private CreationHelper helper = wb.getCreationHelper();
  private Drawing drawing = sheet.createDrawingPatriarch();
  private XSSFCellStyle textStyle = wb.createCellStyle();
  private XSSFCellStyle priceStyle = wb.createCellStyle();
  private int rowIdx = 0;

  private Set<String> codes = CollectUtils.newHashSet();
  private String outputFile;

  public Excel(String outputFile) {
    super();
    this.outputFile = outputFile;
    textStyle.setWrapText(true);
    priceStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0.00"));
    sheet.setColumnWidth(1, 3 * 4500);
    sheet.setColumnWidth(2, 3800);
    sheet.setColumnWidth(3, 3 * 4200);
    XSSFRow header = sheet.createRow(rowIdx++); // 建立新行
    header.createCell(0).setCellValue(new XSSFRichTextString("款号"));
    header.createCell(1).setCellValue(new XSSFRichTextString("标题"));
    header.createCell(2).setCellValue(new XSSFRichTextString("缩略图"));
    header.createCell(3).setCellValue(new XSSFRichTextString("链接"));
    header.createCell(4).setCellValue(new XSSFRichTextString("售价"));
    header.createCell(5).setCellValue(new XSSFRichTextString("实际售价"));
  }

  public Excel() {
    this("/tmp/excel.xlsx");
  }

  @Override
  public boolean write(Map<String, Object> data) throws Exception {
    String code = data.get(Code).toString();
    String title = data.get(Title).toString();
    String href = data.get(Href).toString();
    String imgurl = data.get(ImageUrl).toString();
    Float price = (Float) data.get(Price);
    Float defaultPrice = (Float) data.get(DefaultPrice);
    if (codes.contains(code)) return false;
    codes.add(code);
    XSSFRow row = sheet.createRow(rowIdx); // 建立新行
    row.setHeight((short) 1600);
    row.createCell(0).setCellValue(new XSSFRichTextString(code)); // 款号
    XSSFCell titleCell = row.createCell(1);
    titleCell.setCellValue(new XSSFRichTextString(title));// 标题
    titleCell.setCellStyle(textStyle);
    InputStream is =new URL(imgurl).openStream();
    byte[] bytes = IOUtils.toByteArray(is);
    is.close();
    int pictureIdx = wb.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_JPEG);
    ClientAnchor anchor = helper.createClientAnchor();
    anchor.setCol1(2);
    anchor.setRow1(rowIdx);
    Picture pict = drawing.createPicture(anchor, pictureIdx);
    pict.resize(0.004);
    XSSFCell hrefCell = row.createCell(3);
    hrefCell.setCellValue(new XSSFRichTextString(href));// 链接
    hrefCell.setCellStyle(textStyle);
    XSSFCell defaultPriceCell = row.createCell(4);// 售价
    defaultPriceCell.setCellValue(defaultPrice);
    defaultPriceCell.setCellStyle(priceStyle);

    XSSFCell priceCell = row.createCell(5);// 实际售价
    priceCell.setCellValue(price);
    priceCell.setCellStyle(priceStyle);
    rowIdx++;
    return true;
  }

  @Override
  public void close() {
    try {
      wb.write(new FileOutputStream(outputFile));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
