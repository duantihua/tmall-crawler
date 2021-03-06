package com.github.duantihua.tmcrawler;

import java.util.Map;

public interface Writer {

  public boolean write(Map<ProductAttribute, Object> data) throws Exception;

  public void close();
}
