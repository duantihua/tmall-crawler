package com.github.duantihua.tmcrawler;

import java.io.File;
import java.util.Map;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.lang.SystemInfo;
import org.junit.Test;

public class ExcelTest {

  @Test
  public void testWrite() throws Exception {
    Writer writer = new Excel(SystemInfo.getTmpDir() + File.separator + "category.xlsx");
    Map<ProductAttribute, Object> data = CollectUtils.newHashMap();
    data.put(ProductAttributes.ImageUrl, "file:///tmp/20339005.jpg");
    writer.write(data);
    writer.close();
  }
}
