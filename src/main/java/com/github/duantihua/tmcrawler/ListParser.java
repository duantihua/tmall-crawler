package com.github.duantihua.tmcrawler;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.lang.Numbers;
import org.beangle.commons.lang.Strings;

import static com.github.duantihua.tmcrawler.ProductAttributes.*;

public class ListParser {
  Pattern pattern = Pattern.compile("<dl class=\"item(.*?)</dl>", Pattern.DOTALL);
  Pattern photoPattern = Pattern
      .compile("<a(.*?)href=(.*?)\\r\\n\\s+<img(.*?)data-ks-lazyload=(.*?)\\r\\n\\s+</a>");
  Pattern hrefPattern = Pattern.compile("href=\"(.*?)\"");
  Pattern titlePattern = Pattern.compile("alt=\"(.*?)\"");
  Pattern imgurlPattern = Pattern.compile("data-ks-lazyload=\"(.*?)\"");
  Pattern pricePattern = Pattern.compile("c-price\">([.\\d]*)\\D");
  Matcher matcher = null;
  String content;

  public ListParser(String content) {
    super();
    this.content = content;
    matcher = pattern.matcher(content);
  }

  public boolean hasNext() {
    return matcher.find();
  }

  public Map<String, Object> next() {
    Map<String, Object> data = CollectUtils.newHashMap();
    String item = content.substring(matcher.start(), matcher.end());

    Matcher photoMatcher = photoPattern.matcher(item);
    photoMatcher.find();
    String photo = item.substring(photoMatcher.start(), photoMatcher.end());
    Matcher hrefMatcher = hrefPattern.matcher(photo);
    Matcher titleMatcher = titlePattern.matcher(photo);
    Matcher imageurlMatcher = imgurlPattern.matcher(photo);
    hrefMatcher.find();
    titleMatcher.find();
    imageurlMatcher.find();
    String href = Strings.substringBetween(hrefMatcher.group(0), "\"", "\"");
    String title = Strings.substringBetween(titleMatcher.group(0), "\"", "\"");
    title = Strings.replace(title, "|", " ");
    title = Strings.replace(title, "l", " ");

    String imgurl = Strings.substringBetween(imageurlMatcher.group(0), "\"", "\"");
    String code = Strings.substringAfterLast(title, " ").trim();

    data.put(Title, Strings.substringBeforeLast(title, " "));
    data.put(Href, href);
    data.put(Code, code);
    data.put(ImageUrl, imgurl);

    Matcher priceMatcher = pricePattern.matcher(item);
    priceMatcher.find();
    data.put(Price, Numbers.toFloat(Strings.substringAfterLast(priceMatcher.group(0), ">")));
    return data;
  }
}
