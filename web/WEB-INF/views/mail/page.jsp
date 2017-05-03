<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<div class="container" style="margin-top: 44px">
    <div class="row">
        <div class="col-lg-3" style="padding: 0px">
            <table class="table table-bordered">
                <tr>
                    <th>邮箱</th>
                    <th>邮件数量</th>
                </tr>
                <c:forEach items="${folders}" var="folder">
                    <tr>
                        <td>${folder.name}</td>
                        <td>${folder.messageCount}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        <div class="col-lg-9" style="padding: 0px">
            <div class="panel panel-default">
                <div class="panel-heading">
                    heading
                </div>
                <%--<div class="panel-body">--%>

                <%--</div>--%>
                <table class="table">
                    <c:forEach items="${openedFolder.messages}" var="message">
                        <tr>
                            <td>${message.subject}</td>
                        </tr>
                    </c:forEach>
                </table>
                <div class="panel-footer">
                    footer
                </div>
            </div>
        </div>
    </div>
</div>




<jsp:include page="../../component/Bootstrap-Script.jsp"/>
</body>
</html>