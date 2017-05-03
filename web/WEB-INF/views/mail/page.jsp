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

    <jsp:include page="../../component/Bootstrap-CSS.jsp"/>
</head>
<body>

<jsp:include page="/WEB-INF/component/navbar.jsp"/>

<div class="container-fluid" style="padding: 0px">
    <div class="col-md-2">
        <div class="row">

        </div>
        <div class="row">
            <table class="table table-bordered">
                <c:forEach items="${folders}" var="folder">
                    <tr>
                        <td><a href="/mail/${folder.name}">${folder.name}
                            <c:if test="${folder.messageCount>0}">
                                <span class="badge pull-right">${folder.messageCount}</span>
                            </c:if></a></td>
                    </tr>
                </c:forEach>
            </table>
         </div>
    </div>
    <div class="col-md-10">
        <sf:form method="post">
            <div class="row">
                <input type="submit" formaction="/mail/action/read" value="标记已读 ${openedFolder.fullName}">
            </div>
            <div class="row">
                <div class="panel panel-default" style="border-radius: 0px">
                    <table class="table table-hover">

                        <c:if test="${0 == openedFolder.messageCount}">
                            <tr>
                                <td class="text-center">
                                    没有新邮件！
                                </td>
                            </tr>
                        </c:if>
                        <c:forEach items="${openedFolder.messages}" var="message">
                            <tr>
                                <td><input type="checkbox" id="mailNumbers[]" name="mailNumbers[]" value="${message.messageNumber}"/> ${message.subject}</td>
                            </tr>
                        </c:forEach>
                    </table>
                    <div class="panel-footer">
                        footer
                    </div>
                </div>
            </div>
        </sf:form>
    </div>
</div>


<jsp:include page="../../component/Bootstrap-Script.jsp"/>
</body>
</html>