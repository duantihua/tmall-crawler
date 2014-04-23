package com.github.duantihua.tmcrawler;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.lang.Numbers;
import org.beangle.commons.lang.Strings;

public class DetailParser {
  Pattern pattern = Pattern.compile("defaultItemPrice\":\"([.\\d])+");

  public Map<String, Object> parse(String content) {
    Map<String, Object> data = CollectUtils.newHashMap();
    Matcher matcher = pattern.matcher(content);
    float price = 0.0f;
    if (matcher.find()) price = Numbers.toFloat(Strings.substringAfterLast(matcher.group(0), "\""));
    data.put(ProductAttributes.DefaultPrice, price);
    return data;
  }
}
