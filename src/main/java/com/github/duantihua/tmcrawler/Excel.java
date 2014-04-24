package com.github.duantihua.tmcrawler;

import static com.github.duantihua.tmcrawler.ProductAttributes.Code;
import static com.github.duantihua.tmcrawler.ProductAttributes.DefaultPrice;
import static com.github.duantihua.tmcrawler.ProductAttributes.Href;
import static com.github.duantihua.tmcrawler.ProductAttributes.ImageUrl;
import static com.github.duantihua.tmcrawler.ProductAttributes.Price;
import static com.github.duantihua.tmcrawler.ProductAttributes.Title;

import java.io.File;
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
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.lang.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Excel implements Writer {
  private static final Logger logger = LoggerFactory.getLogger(Excel.class);
  private XSSFWorkbook wb = new XSSFWorkbook();
  private int sheetIdx = 1;
  private XSSFSheet sheet = wb.createSheet("data" + sheetIdx);
  private CreationHelper helper = wb.getCreationHelper();
  private Drawing drawing;
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
    initSheet();
  }

  public Excel() {
    this(SystemInfo.getTmpDir() + File.separator + "excel.xlsx");
  }

  private void initSheet() {
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
    drawing = sheet.createDrawingPatriarch();
  }

  @Override
  public boolean write(Map<String, Object> data) throws Exception {
    if (rowIdx > 300) {
      sheetIdx++;
      sheet = wb.createSheet("data" + sheetIdx);
      rowIdx = 0;
      initSheet();
    }
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
    InputStream is = new URL(imgurl).openStream();
    byte[] bytes = IOUtils.toByteArray(is);
    // IOs.copy(new ByteInputStream(bytes, bytes.length), new
    // FileOutputStream("/tmp/" + code + ".jpg"));
    is.close();
    int pictureIdx = wb.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_JPEG);
    ClientAnchor anchor = helper.createClientAnchor();
    anchor.setCol1(2);
    anchor.setRow1(rowIdx);
    Picture pict = drawing.createPicture(anchor, pictureIdx);
    pict.resize(0.004);
    XSSFCell hrefCell = row.createCell(3);
    Hyperlink link = helper.createHyperlink(XSSFHyperlink.LINK_URL);
    link.setAddress(href);
    hrefCell.setHyperlink(link);// 链接
    hrefCell.setCellValue(new XSSFRichTextString(href));// 标题
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
      logger.info("write data to {}", outputFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
