package com.github.duantihua.tmcrawler;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.beangle.commons.lang.Numbers;
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
    Writer writer = new Excel("/tmp/category.xlsx");
    DetailParser detailParser = new DetailParser();
    int i = 1;
    int count = 0;
    int max = Numbers.toInt(args[1]);
    while (i <= max) {
      logger.info("process page:{}", i);
      String content = getString(url + "&pageNo=" + i);
      ListParser parser = new ListParser(content);
      while (parser.hasNext()) {
        Map<String, Object> data = parser.next();
        String href = data.get("href").toString();
        logger.info("process item{}:{}", ++count, href);
        data.putAll(detailParser.parse(getString(href)));
        writer.write(data);
      }
      i++;
    }
    writer.close();
  }

  public static String getString(String url) throws Exception {
    DefaultHttpClient httpclient = new DefaultHttpClient();
    HttpResponse response = httpclient.execute(new HttpGet(url));
    return EntityUtils.toString(response.getEntity());
  }

}
