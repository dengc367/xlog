<%@page language="java" contentType="text/html; charset=utf-8" %>
<%@page import="com.renren.dp.xlog.metrics.QueueCounter" %>
<%@page import="com.renren.dp.xlog.metrics.CategoriesInfo" %>
<%@page import="java.util.Collection" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="css/commons.css" type="text/css" />
<title>Categories信息</title>
</head>
<body style="text-align: center">
<%
  String queueName=request.getParameter("queueName");
  QueueCounter queueCounter=(QueueCounter)session.getAttribute(queueName);
  Collection<CategoriesInfo> categoriesList=queueCounter.getCategoriesInfos();
%>
<br/>
<p align="left"><font size="3"><b>队列<%=queueName %>&nbsp;Categories信息</b></font></p>
<table width="100%" class="tabinfo1" border="1" cellspacing="3" cellpadding="0">
  <tr>
    <td align="left" width="40%">Category名称</td>
    <td align="left" width="20%">成功请求数</td>
    <td align="left" width="20%">失败请求数</td>
    <td align="left" width="20%">Category RPS</td>
  </tr>
<%
   for(CategoriesInfo ci : categoriesList){
%>
<tr>
    <td align="left" width="40%"><%=ci.getCategory() %></td>
    <td align="left" width="20%"><%=ci.getSuccessCount() %></td>
    <td align="left" width="20%"><%=ci.getFailCount() %></td>
    <td align="left" width="20%"><%=ci.getCategoryRPS() %></td>
  </tr>
<% } %>
</table>
</body>
</html>