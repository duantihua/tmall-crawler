package com.github.duantihua.tmcrawler;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.lang.Numbers;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.lang.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);
  private static final DefaultHttpClient httpclient = new DefaultHttpClient();

  // static {
  // CookieStore cookieStore = new BasicCookieStore();
  // String cookiString =
  // "cna=otCQC6KsoVkCAbSuAtKe8RmI; cq=ccp%3D0; otherx=e%3D1%26p%3D*%26s%3D0%26c%3D0%26f%3D0%26g%3D0%26t%3D0; x=__ll%3D-1%26_ato%3D0; lzstat_uv=39163558871332387704|2674749; t=1a805aa8a93ac84e0fda8fc4485bbc42; isg=D8BF6ED4597B1D458870F3B4E7D87BDF; ucn=center; pnm_cku822=115UW5TcyMNYQwiAiwTR3tCf0J%2FQnhEcUpkMmQ%3D%7CUm5Ockt0TndJfEd4RnlDfig%3D%7CU2xMHDJ%2BH2QJZwBxX39Rb1R6WnQyUzVJOBZAFg%3D%3D%7CVGhXd1llXGNZYF5rUG9RblRpXmNBdE57Q39Ef0pwRXBEfEh2Q207%7CVWldfS0QMAswDi4RMR9wV3kveQ%3D%3D%7CVmhIGCQaIAA9HSEYJx09AzcCPBwgGSAdPQk0CSkVLBUoCD0HOW85%7CV25Tbk5zU2xMcEl1VWtTaUlwJg%3D%3D; CNZZDATA1000279581=546004378-1427342008-%7C1427433808; swfstore=103205; whl=-1%260%260%260; tracknick=chaostone_duan; _tb_token_=3bedebe7b813e; uc3=nk2=AHLWiqXTlFcoo5NmK6c%3D&id2=UNN5E%2BXybZQ%3D&vt3=F8dAT%2BamTMXr%2BmACaC0%3D&lg2=UtASsssmOIJ0bQ%3D%3D; ck1=; lgc=chaostone_duan; unb=33423818; _nk_=chaostone_duan; _l_g_=Ug%3D%3D; login=true";
  // String[] cookies = Strings.split(cookiString, ';');
  // for (String c : cookies) {
  // String cv = c.trim();
  // String name = Strings.substringBefore(cv, "=");
  // String value = Strings.substringAfter(cv, "=");
  // BasicClientCookie cookie = new BasicClientCookie(name, value);
  // // cookie.setDomain("your domain");
  // cookie.setPath("/");
  // cookieStore.addCookie(cookie);
  // }
  // httpclient.setCookieStore(cookieStore);
  // }

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
    int idx = 0;
    while (i <= max) {
      logger.info("process page:{}", i);
      String content = getString(url + "&pageNo=" + i);
      ListParser parser = new ListParser(content);
      List<Map<ProductAttribute, Object>> datas = CollectUtils.newArrayList();

      while (parser.hasNext()) {
        Map<ProductAttribute, Object> data = parser.next();
        if (!data.isEmpty()) {
          String href = data.get(ProductAttributes.Href).toString();
          data.put(ProductAttributes.Index, ++idx);
          datas.add(data);
          logger.info("find item{}:{}", ++count, href);
        }
      }

      for (Map<ProductAttribute, Object> data : datas) {
        // Thread.sleep(5000);
        String href = data.get(ProductAttributes.Href).toString();
        logger.info("fetching details item{}:{}", ProductAttributes.Index.get(data), href);
        // Map<ProductAttribute, Object> details = detailParser.parse(getString(href));
        Map<ProductAttribute, Object> details = CollectUtils.newHashMap();
        if (ProductAttributes.Code.get(details) == null) {
          logger.error("Tmall parse details failure:{}", href);
        }
        data.putAll(details);
        writer.write(data);
      }
      i++;
    }
    writer.close();
  }

  public static String getString(String url) throws Exception {
    String newUrl = url;
    if (url.startsWith("//")) newUrl = "http:" + url;
    HttpResponse response = httpclient.execute(new HttpGet(newUrl));
    return EntityUtils.toString(response.getEntity());
  }

}
