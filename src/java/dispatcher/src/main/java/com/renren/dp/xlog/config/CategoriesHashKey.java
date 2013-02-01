package com.renren.dp.xlog.config;

import java.util.HashMap;
import java.util.Map;

import dp.election.GenericHashBuilder;

public class CategoriesHashKey implements GenericHashBuilder<String> {

  private Map<String, Integer> map = new HashMap<String, Integer>();

  @Override
  public int hash(String category, int size) {
    if (map.containsKey(category)) {
      return map.get(category);
    } else {
      int count = 0;
      for (char c : category.toCharArray()) {
        count += c;
      }
      int i = Math.abs(count % size);
      map.put(category, i);
      return i;
    }
  }

}
