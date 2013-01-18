package com.renren.dp.xlog.config;

import dp.election.GenericHashBuilder;

public class CategoriesHashKey implements GenericHashBuilder<String> {

  @Override
  public int hash(String category, int size) {
    int count = 0;
    for (char c : category.toCharArray()) {
      count += c;
    }
    return Math.abs(count % size);
  }

}
