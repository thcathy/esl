<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
  <jsp:include page="/htmlhead.xhtml" />
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/main.css" />
  <title>FunFunSpell.com News 最新消息: <c:out value="${param['t']}" /></title> 
</head>
<body bgcolor="#FFFFFF">
	<table width="786" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td colspan="4">
				<img src="<%=request.getContextPath() %>/images/graphic/news_01.gif" width="786" height="119" alt="" /></td>
		</tr>
		<tr>
			<td background="<%=request.getContextPath() %>/images/graphic/news_02.gif"></td>
			<td colspan="2" bgcolor="#FFFFFF" align="left" valign="top" class="innerContent">
				<div><c:import charEncoding="UTF-8" url="/public/news/html/${param['l']}" /></div></td>
			<td background="<%=request.getContextPath() %>/images/graphic/news_04.gif"></td>
		</tr>
		<tr>
			<td>
				<img src="<%=request.getContextPath() %>/images/graphic/news_05.gif" width="25" height="61"  /></td>
			<td valign="bottom" bgcolor="#FFFFFF">
				<table width="100%"><tr>
                	<td><a href="http://www.funfunspell.com">Fun Fun Spell</a></td>
                    <td align="right">Copyright @ 2008 FunFunSpell.com</td>
                </tr></table>	
			</td>
			<td colspan="2">
				<img src="<%=request.getContextPath() %>/images/graphic/news_07.gif" width="188" height="61"  /></td>
		</tr>
		<tr>
			<td colspan="4">
				<img src="<%=request.getContextPath() %>/images/graphic/news_08.gif" width="786" height="126"  /></td>
		</tr>
		<tr>
			<td>
				<img src="<%=request.getContextPath() %>/images/graphic/spacer.gif" width="25" height="1"  /></td>
			<td>
				<img src="<%=request.getContextPath() %>/images/graphic/spacer.gif" width="573" height="1"  /></td>
			<td>
				<img src="<%=request.getContextPath() %>/images/graphic/spacer.gif" width="160" height="1"  /></td>
			<td>
				<img src="<%=request.getContextPath() %>/images/graphic/spacer.gif" width="28" height="1"  /></td>
		</tr>
	</table>
</body>
</html>


