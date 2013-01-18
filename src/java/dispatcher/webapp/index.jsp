<%@page language="java" import="com.renren.dp.xlog.dispatcher.SystemManager" contentType="text/html; charset=utf-8" %>
<%@page import="java.util.List" %>
<%@page import="com.renren.dp.xlog.storage.QueueCounter" %>
<%@page import="com.renren.dp.xlog.dispatcher.SystemManager.ParameterMode" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="css/commons.css" type="text/css" />
<title>xlog dispatcher后台管理</title>
<script>
	function operate(operator){
	   if(operator=="start"){
	   	   tip="启动";
	   }else if(operator=="stop"){
	   	   tip="停止";
	   }else{
	   	   tip="退出";
	    }
		if(confirm("确定要"+tip+"吗?")){
			window.location.href="/control?operator="+operator;
		}		
	}
</script>
</head>
<body style="text-align: center">
<%
	SystemManager sm=new SystemManager();
	int status=sm.getDispatcherStatus();
	String displayStatus="停止";
	String displayStartDate="";
	if(status==1){
		displayStatus="启动";
		displayStartDate=sm.getStartDate();
	}
	List<QueueCounter> queueCounters=sm.getQueueInfo();
	List<ParameterMode> params=sm.getParameters();
%>
<br/>
<p align="center"><font size="4">Xlog Dispatcher后台管理</font></p>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr><td>&nbsp;</td></tr>
<tr><td><div class="title1" /></td></tr>
<tr><td>&nbsp;</td></tr>
</table>
<p align="left"><font size="3"><b>&nbsp;基本信息</b></font></p>
<table width="20%" class="tabinfo1" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td nowrap="nowrap">系统状态:</td>
		<td nowrap="nowrap"><%=displayStatus %></td>
		<%
			if(status==0){
		%>
		<td nowrap="nowrap"><input type="button" id="start" name="start" onClick="operate('start');" value="启动"/></td>	
		<td nowrap="nowrap"><input type="button" id="stop" name="stop" value="停止" disabled="true"/></td>
		<td nowrap="nowrap"><input type="button" id="logout" name="logout" onClick="operate('logout');" value="退出"/></td>
		<%
			}else{
		%>
		<td nowrap="nowrap"><input type="button" id="start" name="start" value="启动"  disabled="true"/></td>	
		<td nowrap="nowrap"><input type="button" id="stop" name="stop" onClick="operate('stop');" value="停止"/></td>
		<td nowrap="nowrap"><input type="button" id="logout" name="logout" onClick="operate('logout');" value="退出"/></td>
		<%
			}
		%>
	</tr>
	<tr>
		<td nowrap="nowrap">启动时间:</td>
		<td nowrap="nowrap"><%=displayStartDate %></td>
		<td nowrap="nowrap" colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td nowrap="nowrap">编译作者:</td>
		<td nowrap="nowrap"><%=sm.getVersion().user() %></td>
		<td nowrap="nowrap" colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td nowrap="nowrap">版本编译:</td>
		<td nowrap="nowrap"><%=sm.getVersion().date() %></td>
		<td nowrap="nowrap" colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td nowrap="nowrap">Git URL:</td>
		<td nowrap="nowrap"><%=sm.getVersion().url() %></td>
		<td nowrap="nowrap" colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td nowrap="nowrap">Git版本号:</td>
		<td nowrap="nowrap"><%=sm.getVersion().revision() %></td>
		<td nowrap="nowrap" colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td nowrap="nowrap">缓存数据:</td>
		<td nowrap="nowrap"><%=sm.getCacheFilesSize() %></td>
		<td nowrap="nowrap" colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td nowrap="nowrap">失败数据:</td>
		<td nowrap="nowrap"><%=sm.getSendFailureFileSize() %></td>
		<td nowrap="nowrap" colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td nowrap="nowrap">内存信息:</td>
		<td nowrap="nowrap"><%=sm.getMemoryInfo() %></td>
		<td nowrap="nowrap" colspan="2">&nbsp;</td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr><td>&nbsp;</td></tr>
<tr><td><div class="title1" /></td></tr>
<tr><td>&nbsp;</td></tr>
</table>
<p align="left"><font size="3"><b>&nbsp;队列信息</b></font></p>
<table width="100%" class="tabinfo1" border="1" cellspacing="3" cellpadding="0">
	<tr>
		<td align="left" width="20%">队列名称</td>
		<td align="left" width="16%">队列容量</td>
		<td align="left" width="16%">当前队列请求数</td>
		<td align="left" width="16%">成功请求数</td>
		<td align="left" width="16%">失败请求数</td>
		<td align="left" width="16%">队列RPS</td>
	</tr>
	<% int totalQueueCapacity=0;
	   int totalCurrentQueueSize=0;
	   int totalSuccessCount=0;
	   int totalFailureCount=0;
	   int totalRPS=0;
	   for(QueueCounter counter : queueCounters) { 
	      String queueName=counter.getQueueName();
	      session.removeAttribute(queueName);
	      session.setAttribute(queueName,counter);
	      totalQueueCapacity+=counter.getQueueCapacity();
	      totalCurrentQueueSize+=counter.getCurrentQueueSize();
	      totalSuccessCount+=counter.getSuccessCount();
	      totalFailureCount+=counter.getFailureCount();
	      totalRPS+=counter.getQueueRPS();
	%>
		<tr>
			<td align="left"><a href="categories.jsp?queueName=<%=queueName %>"><%=queueName %></a></td>
			<td align="left"><%=counter.getQueueCapacity() %></td>
			<td align="left"><%=counter.getCurrentQueueSize() %></td>
			<td align="left"><%=counter.getSuccessCount() %></td>
			<td align="left"><%=counter.getFailureCount() %></td>
			<td align="left"><%=counter.getQueueRPS() %></td>
		</tr>
	<% } %>
	<tr>
		<td align="left" width="20%"><font size="2"><b>汇总信息:</b></font></td>
		<td align="left" width="16%"><font size="2"><b><%=totalQueueCapacity %></b></font></td>
		<td align="left" width="16%"><font size="2"><b><%=totalCurrentQueueSize %></b></font></td>
		<td align="left" width="16%"><font size="2"><b><%=totalSuccessCount %></b></font></td>
		<td align="left" width="16%"><font size="2"><b><%=totalFailureCount %></b></font></td>
		<td align="left" width="16%"><font size="2"><b><%=totalRPS %></b></font></td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr><td>&nbsp;</td></tr>
<tr><td><div class="title1" /></td></tr>
<tr><td>&nbsp;</td></tr>
</table>
<p align="left"><font size="3"><b>&nbsp;系统参数</b></font></p>
<table width="100%" class="tabinfo1" border="1" cellspacing="3" cellpadding="0">
	<tr>
		<td align="left" width="25%">参数名称</td>
		<td align="left" width="25%">参数值</td>
		<td align="left" width="50%">参数描述</td>
	</tr>
	<%
	   for(ParameterMode pm:params){
	%>
	<tr>
		<td align="left" width="20%"><%=pm.getKey() %></td>
		<td align="left" width="30%"><%=pm.getValue() %></td>
		<td align="left" width="50%"><%=pm.getDescription() %></td>
	</tr>
	<% } %>
</table>
</body>
</html>