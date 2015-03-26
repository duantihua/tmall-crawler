package com.github.duantihua.tmcrawler;

import static com.github.duantihua.tmcrawler.ProductAttributes.*;

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
  private XSSFCellStyle integerStyle = wb.createCellStyle();
  private XSSFCellStyle priceStyle = wb.createCellStyle();
  private int rowIdx = 0;

  private Set<String> codes = CollectUtils.newHashSet();
  private String outputFile;

  private ProductAttribute[] attributes = new ProductAttribute[] { ImageUrl, Title, Code, Price,
      DefaultPrice, Color, MonthSaleCnt, Size, StoreCnt, Href };

  public Excel(String outputFile) {
    super();
    this.outputFile = outputFile;
    textStyle.setWrapText(true);
    integerStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0"));
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
    header.createCell(0).setCellValue(new XSSFRichTextString("序号"));

    int i = 1;
    for (ProductAttribute attr : attributes) {
      header.createCell(i).setCellValue(new XSSFRichTextString(attr.title));
      i += 1;
    }
    drawing = sheet.createDrawingPatriarch();
  }

  private void write(XSSFRow row, int col, ProductAttribute attr, Map<ProductAttribute, Object> data)
      throws Exception {
    if (attr.valueType.equals("text")) {
      XSSFCell titleCell = row.createCell(col);
      titleCell.setCellValue(new XSSFRichTextString(attr.getString(data)));
      titleCell.setCellStyle(textStyle);
    } else if (attr.valueType.equals("double")) {
      XSSFCell defaultPriceCell = row.createCell(col);
      defaultPriceCell.setCellValue(attr.getDouble(data));
      defaultPriceCell.setCellStyle(priceStyle);
    } else if (attr.valueType.equals("long")) {
      XSSFCell monthSaleCntCell = row.createCell(col);
      monthSaleCntCell.setCellValue(attr.getLong(data));
      monthSaleCntCell.setCellStyle(integerStyle);
    } else if (attr.valueType.equals("url")) {
      XSSFCell hrefCell = row.createCell(3);
      Hyperlink link = helper.createHyperlink(XSSFHyperlink.LINK_URL);
      String href = attr.getString(data);
      link.setAddress(href);
      hrefCell.setHyperlink(link);
      hrefCell.setCellValue(new XSSFRichTextString(href));
      hrefCell.setCellStyle(textStyle);
    } else if (attr.valueType.equals("image")) {
      String imgurl = attr.getString(data);
      InputStream is = new URL(imgurl).openStream();
      byte[] bytes = IOUtils.toByteArray(is);
      // IOs.copy(new ByteInputStream(bytes, bytes.length), new
      // FileOutputStream("/tmp/" + code + ".jpg"));
      is.close();
      int pictureIdx = wb.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_JPEG);
      ClientAnchor anchor = helper.createClientAnchor();
      anchor.setCol1(col);
      anchor.setRow1(rowIdx);
      Picture pict = drawing.createPicture(anchor, pictureIdx);
      pict.resize(0.004);
    }
  }

  @Override
  public boolean write(Map<ProductAttribute, Object> data) throws Exception {
    if (rowIdx > 300) {
      sheetIdx++;
      sheet = wb.createSheet("data" + sheetIdx);
      rowIdx = 0;
      initSheet();
    }
    String code = Code.getString(data);
    if (codes.contains(code)) return false;
    XSSFRow row = sheet.createRow(rowIdx); // 建立新行
    row.setHeight((short) 1600);
    // 1 序号
    row.createCell(0).setCellValue(new XSSFRichTextString(code));
    XSSFCell indexCell = row.createCell(1);
    indexCell.setCellValue(new XSSFRichTextString(String.valueOf(rowIdx)));
    indexCell.setCellStyle(integerStyle);
    int col = 1;
    for (ProductAttribute attr : attributes) {
      write(row, col, attr, data);
    }
    codes.add(code);
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
