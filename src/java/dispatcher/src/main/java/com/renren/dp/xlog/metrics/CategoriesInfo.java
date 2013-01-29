package com.renren.dp.xlog.metrics;

public class CategoriesInfo implements Cloneable {
  private long successCount = 0;
  private long failCount = 0;
  private String category;
  private long categoryRPS = 0;

  protected CategoriesInfo(String category) {
    this.category = category;
  }

  public long getSuccessCount() {
    return successCount;
  }

  protected void incSuccessCount() {
    this.successCount++;
  }

  protected void setSuccessCount(long successCount) {
    this.successCount=successCount;
  }
  
  public long getFailCount() {
    return failCount;
  }

  protected void incFailCount() {
    this.failCount++;
  }

  public String getCategory() {
    return category;
  }

  public long getCategoryRPS() {
    return categoryRPS;
  }

  public void setCategoryRPS(long categoryRPS) {
    this.categoryRPS = categoryRPS;
  }
}
