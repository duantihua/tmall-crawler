package com.github.duantihua.tmcrawler;

import java.util.Map;

public class ProductAttribute {

  String name;

  String title;

  String valueType = "text";

  int length = 3000;

  public ProductAttribute(String n, String t) {
    this.name = n;
    this.title = t;
  }

  public ProductAttribute(String n, String t, String v) {
    this.name = n;
    this.title = t;
    this.valueType = v;
  }

  String getString(Map<ProductAttribute, Object> data) {
    Object value = data.get(this);
    if (null != value) return value.toString();
    else return "";
  }

  double getDouble(Map<ProductAttribute, Object> data) {
    Object value = data.get(this);
    if (null != value) return ((Number) value).doubleValue();
    else return 0;
  }

  long getLong(Map<ProductAttribute, Object> data) {
    Object value = data.get(this);
    if (null != value) return ((Number) value).longValue();
    else return 0L;
  }

  Object get(Map<ProductAttribute, Object> data) {
    return data.get(this);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return title + "[" + name + "]";
  }

  @Override
  public boolean equals(Object obj) {
    return name.equals(((ProductAttribute) obj).name);
  }

}
