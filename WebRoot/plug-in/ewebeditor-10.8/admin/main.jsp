<%@ page contentType="text/html;charset=GBK" pageEncoding="GBK" session="true"%>
<%request.setCharacterEncoding("GBK");%>
<jsp:useBean id="eWebEditor" class="ewebeditor.admin.main_jsp" scope="page"/>
<%
eWebEditor.Load(pageContext);
%>
