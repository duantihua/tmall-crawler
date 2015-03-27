package com.github.duantihua.tmcrawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.Map;

import org.junit.Test;

public class DetailParserTest {

  private String getHtml() throws Exception {
    URL url = this.getClass().getResource("/detail.html");
    BufferedReader reader = new BufferedReader(new FileReader(url.getFile()));
    StringBuilder sb = new StringBuilder();

    while (true) {
      String str = reader.readLine();
      if (null == str) break;
      sb.append(str);
      sb.append("\n");
    }
    reader.close();
    return sb.toString();
  }

  @Test
  public void testParse() throws Exception {
    String str = getHtml();
    DetailParser parser = new DetailParser();
    Map<ProductAttribute, Object> data = parser.parse(str);
    System.out.println(data);
  }

  @Test
  public void testDecode() throws Exception {
    System.out.println(DetailParser.decode("2014&#24180;&#31179;&#23395;"));
  }
}
