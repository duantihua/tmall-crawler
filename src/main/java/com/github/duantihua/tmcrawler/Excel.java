package com.github.duantihua.tmcrawler;

import static com.github.duantihua.tmcrawler.ProductAttributes.Code;
import static com.github.duantihua.tmcrawler.ProductAttributes.Color;
import static com.github.duantihua.tmcrawler.ProductAttributes.DefaultPrice;
import static com.github.duantihua.tmcrawler.ProductAttributes.Href;
import static com.github.duantihua.tmcrawler.ProductAttributes.ImageUrl;
import static com.github.duantihua.tmcrawler.ProductAttributes.Index;
import static com.github.duantihua.tmcrawler.ProductAttributes.MonthSaleCnt;
import static com.github.duantihua.tmcrawler.ProductAttributes.Price;
import static com.github.duantihua.tmcrawler.ProductAttributes.Size;
import static com.github.duantihua.tmcrawler.ProductAttributes.StoreCnt;
import static com.github.duantihua.tmcrawler.ProductAttributes.Title;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
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

  private ProductAttribute[] attributes = new ProductAttribute[] { Index, ImageUrl, Title, Code, Price,
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
    rowIdx = 0;
    XSSFRow header = sheet.createRow(rowIdx++); // 建立新行
    int i = 0;
    for (ProductAttribute attr : attributes) {
      header.createCell(i).setCellValue(new XSSFRichTextString(attr.title));
      sheet.setColumnWidth(i, attr.length);
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
      XSSFCell hrefCell = row.createCell(col);
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
//      IOs.copy(new ByteInputStream(bytes, bytes.length), new FileOutputStream("/tmp/"
//          + ProductAttributes.Code.get(data) + ".jpg"));
      int pictureIdx = wb.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_JPEG);
      is.close();
      XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) col, rowIdx, (short) (col + 1),
          rowIdx + 1);
      // ClientAnchor anchor = helper.createClientAnchor();
      // anchor.setCol1(col);
      // anchor.setRow1(rowIdx);
      anchor.setAnchorType(3);
      drawing.createPicture(anchor, pictureIdx);
      // pict.resize(0.4);
    }
  }

  @Override
  public boolean write(Map<ProductAttribute, Object> data) throws Exception {
    if (rowIdx > 500) {
      sheetIdx++;
      sheet = wb.createSheet("data" + sheetIdx);
      initSheet();
    }
    String href = Href.getString(data);
    if (codes.contains(href)) return false;
    XSSFRow row = sheet.createRow(rowIdx); // 建立新行
    row.setHeight((short) 1200);
    int col = 0;
    for (ProductAttribute attr : attributes) {
      write(row, col, attr, data);
      col += 1;
    }
    codes.add(href);
    rowIdx++;
    return true;

  }

  @Override
  public void close() {
    try {
      FileOutputStream fos = new FileOutputStream(outputFile);
      wb.write(fos);
      fos.close();
      logger.info("write data to {}", outputFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
