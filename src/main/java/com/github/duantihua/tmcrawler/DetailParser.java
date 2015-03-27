package com.github.duantihua.tmcrawler;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.lang.Numbers;
import org.beangle.commons.lang.Strings;

public class DetailParser {
  private Pattern defaultPrice = Pattern.compile("defaultItemPrice\":\"([.\\d])+");
  private Pattern colorHtmlPattern = Pattern.compile("<ul data-property=\"颜色分类(.*?)</ul>", Pattern.DOTALL);
  private Pattern sizeHtmlPattern = Pattern.compile("<ul data-property=\"尺码(.*?)</ul>", Pattern.DOTALL);
  private Pattern colorPattern = Pattern.compile("<span>(.*?)</span>");
  private Pattern codePattern = Pattern.compile("货号:&nbsp;(.*?)</li>");
  private Pattern saleOnPattern = Pattern.compile("上市年份季节:&nbsp;(.*?)</li>");
  private Pattern monthSaleCntPattern = Pattern.compile("月成交记录(.*?)hRecord\">(.*?)</em>件");
  private Pattern stockPattern = Pattern.compile("\"stock\":(.*?)}");

  public Map<ProductAttribute, Object> parse(String content) {
    Map<ProductAttribute, Object> data = CollectUtils.newHashMap();
    Matcher matcher = defaultPrice.matcher(content);
    float price = 0.0f;
    if (matcher.find()) price = Numbers.toFloat(Strings.substringAfterLast(matcher.group(0), "\""));
    data.put(ProductAttributes.DefaultPrice, price);
    // parse color
    Matcher colorHtmlMatcher = colorHtmlPattern.matcher(content);
    if (colorHtmlMatcher.find()) {
      String colorHtml = content.substring(colorHtmlMatcher.start(), colorHtmlMatcher.end());
      data.put(ProductAttributes.Color, parseSpan(colorHtml));
    }
    // parse size
    Matcher sizeHtmlMatcher = sizeHtmlPattern.matcher(content);
    if (sizeHtmlMatcher.find()) {
      String sizeHtml = content.substring(sizeHtmlMatcher.start(), sizeHtmlMatcher.end());
      data.put(ProductAttributes.Size, parseSpan(sizeHtml));
    }
    // parse code
    Matcher codeMatcher = codePattern.matcher(content);
    if (codeMatcher.find()) {
      data.put(ProductAttributes.Code, codeMatcher.group(1));
    }
    // parse saleson
    Matcher saleOnMatcher = saleOnPattern.matcher(content);
    if (saleOnMatcher.find()) {
      data.put(ProductAttributes.SaleOn, decode(saleOnMatcher.group(1)));
    }
    // parse monthSaleCnt
    Matcher monthSaleCntMatcher = monthSaleCntPattern.matcher(content);
    if (monthSaleCntMatcher.find()) {
      data.put(ProductAttributes.MonthSaleCnt, Integer.valueOf(monthSaleCntMatcher.group(2)));
    }
    data.put(ProductAttributes.StoreCnt, parseStock(content));
    return data;
  }

  private Long parseStock(String content) {
    Matcher stockMatcher = stockPattern.matcher(content);
    long stock = 0;
    while (stockMatcher.find()) {
      stock += Long.valueOf(stockMatcher.group(1));
    }
    return new Long(stock);
  }

  private String parseSpan(String colorHtml) {
    Matcher colorMatcher = colorPattern.matcher(colorHtml);
    StringBuilder sb = new StringBuilder();
    while (colorMatcher.find()) {
      sb.append(colorMatcher.group(1)).append(" ");
    }
    if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  static final String decode(final String in) {
    String working = in;
    int index;
    index = working.indexOf("&#");
    while (index > -1) {
      int numStart = index + 2;
      int numFinish = working.indexOf(';', numStart);
      if (numFinish < 0) break;
      String substring = working.substring(numStart, numFinish);

      int number = Integer.parseInt(substring);

      String stringStart = working.substring(0, index);
      String stringEnd = working.substring(numFinish+1);
      working = stringStart + ((char) number) + stringEnd;
      index = working.indexOf("&#");
    }
    return working;
  }
}
