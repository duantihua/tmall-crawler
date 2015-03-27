package com.github.duantihua.tmcrawler;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.lang.Numbers;
import org.beangle.commons.lang.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.out.println("Usage: com.github.duantihua.tmcrawler.Main url maxpage");
      return;
    }
    String url = args[0];
    Writer writer = new Excel(SystemInfo.getTmpDir() + File.separator + "category.xlsx");
    DetailParser detailParser = new DetailParser();
    int i = 1;
    int count = 0;
    int max = Numbers.toInt(args[1]);
    while (i <= max) {
      logger.info("process page:{}", i);
      String content = getString(url + "&pageNo=" + i);
      ListParser parser = new ListParser(content);
      List<Map<ProductAttribute, Object>> datas = CollectUtils.newArrayList();
      int j = 0;
      while (parser.hasNext()) {
        Map<ProductAttribute, Object> data = parser.next();
        if (!data.isEmpty()) {
          String href = data.get(ProductAttributes.Href).toString();
          data.put(ProductAttributes.Index, ++j);
          datas.add(data);
          logger.info("find item{}:{}", ++count, href);
          // FIXME
          if (j > 4) break;
        }
      }

      for (Map<ProductAttribute, Object> data : datas) {
        Thread.sleep(2000);
        String href = data.get(ProductAttributes.Href).toString();
        logger.info("fetching details item{}:{}", ProductAttributes.Index.get(data), href);
        data.putAll(detailParser.parse(getString(href)));
        writer.write(data);
      }
      i++;
    }
    writer.close();
  }

  public static String getString(String url) throws Exception {
    DefaultHttpClient httpclient = new DefaultHttpClient();
    String newUrl = url;
    if (url.startsWith("//")) newUrl = "http:" + url;
    HttpResponse response = httpclient.execute(new HttpGet(newUrl));
    return EntityUtils.toString(response.getEntity());
  }

}
