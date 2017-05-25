<%@ page import="javax.mail.internet.MimeUtility" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->

    <title>Title</title>

    <jsp:include page="/WEB-INF/component/Bootstrap-CSS.jsp"/>
</head>
<body>

<jsp:include page="/WEB-INF/component/navbar.jsp"/>

<div class="container-fluid" style="padding: 0px">
    <div class="col-md-2">
        <div class="row">

        </div>
        <div class="row">
            <jsp:include page="/WEB-INF/component/folderNavTable.jsp"/>
        </div>
    </div>
    <div class="col-md-10">
        <sf:form method="post">
            收件人邮箱地址：<br/><input name="emailaddress1" maxlength="25" style = "width:449"><br/>
            <hr  align="left" style="height:10px;width: 449px;border:none;border-top:10px groove skyblue;" />
            邮箱主题：<br/><input name="subject1" type="text" maxlength="25" style = "width:449"/><br/>
            <hr align="left" style="height:2px;width: 449px; background: black" />
            邮箱正文：<br/><input name="content1" type="text" maxlength="25" style="width:449px"><br/>
            <br/><button   class="btn btn-default" type="submit">发送</button>
        </sf:form>
    </div>
</div>

<jsp:include page="/WEB-INF/component/Bootstrap-Script.jsp"/>
</body>
</html>