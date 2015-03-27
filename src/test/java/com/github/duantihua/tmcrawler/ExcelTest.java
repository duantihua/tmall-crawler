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
    data.put(ProductAttributes.ImageUrl, "file:///tmp/null.jpg");
    data.put(ProductAttributes.Code, "123");
    data.put(ProductAttributes.Index, 1);
    data.put(ProductAttributes.Title, "123");
    data.put(ProductAttributes.Href, "file:///tmp/null.jpg");
    data.put(ProductAttributes.DefaultPrice, 3f);
    data.put(ProductAttributes.Price, 3f);
    
    Map<ProductAttribute, Object> data2 = CollectUtils.newHashMap();
    data2.put(ProductAttributes.ImageUrl, "file:///tmp/null2.jpg");
    data2.put(ProductAttributes.Code, "123");
    data2.put(ProductAttributes.Index, 1);
    data2.put(ProductAttributes.Title, "123");
    data2.put(ProductAttributes.Href, "file:///tmp/null2.jpg");
    data2.put(ProductAttributes.DefaultPrice, 3f);
    data2.put(ProductAttributes.Price, 3f);
    writer.write(data);
    writer.write(data2);
    writer.close();
  }
}
