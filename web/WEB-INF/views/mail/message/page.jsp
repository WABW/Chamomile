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
            <div class="row" style="padding-bottom: 10px">
                <div class="btn-group" role="group" style="padding-left: 11px; padding-right: 22px">
                    <a href="/mail/message/action/back">
                        <div class="btn btn-default">
                            &nbsp;<span class="glyphicon glyphicon-arrow-left"></span>&nbsp;
                        </div>
                    </a>
                </div>

                <div class="btn-group" role="group" style="padding-right: 11px">
                    <button class="btn btn-default" type="submit" formaction="/mail/action/trash">
                        &nbsp;<span class="glyphicon glyphicon-trash"></span>&nbsp;
                    </button>
                    <div class="btn-group" role="group">
                        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            &nbsp;<span class="glyphicon glyphicon-folder-open"></span>&nbsp;
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu">
                            <c:forEach items="${folders}" var="folderMoveTo">
                                <li>
                                    <button type="submit" id="moveButton_${folderMoveTo.name}" style="display: none;" formaction="/mail/action/move/${folderMoveTo.name}">Hidden Unread Button</button>
                                    <a href="#" onclick="$('#moveButton_${folderMoveTo.name}').click();">移动至${folderMoveTo.name}</a>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
                <div class="btn-group" role="group">
                    <button class="btn btn-default" type="submit" formaction="/mail/action/read">
                        标记为已读
                    </button>
                    <div class="btn-group" role="group">
                        <div class="dropdown">
                            <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                                更多操作
                                <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu" aria-labelledby="dropdownMenu1">
                                <li>
                                    <button type="submit" id="unreadButton" style="display: none;" formaction="/mail/action/unread">Hidden Unread Button</button>
                                    <a href="#" onclick="$('#unreadButton').click();">标记为未读</a>
                                </li>
                                <li>
                                    <button type="submit" id="flagButton" style="display: none;" formaction="/mail/action/flag">Hidden Flag Button</button>
                                    <a href="#" onclick="$('#flagButton').click();">添加旗标</a>
                                </li>
                                <li>
                                    <button type="submit" id="unflagButton" style="display: none;" formaction="/mail/action/unflag">Hidden Unread Button</button>
                                    <a href="#" onclick="$('#unflagButton').click();">移除旗标</a>
                                </li>
                                <li role="separator" class="divider"></li>
                                <li><a href="#">Separated link</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <table class="table table-responsive" cellpadding="0" role="presentation">
                    <tr>
                        <td>
                            <%--<div style="width: 581px;"></div>--%>
                            <div style="border-bottom-width: 1px; border-bottom-color: gray; border-bottom-style: solid;">
                                <div class="h4">${message.subject}</div>
                            </div>
                            <div class="small">
                                <div>
                                    <table>
                                        <tbody>
                                        <tr>
                                            <td>${decoder.decodeText(message.from[0])}</td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>
                                        <tr>
                                            row 2
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div>
                                Content
                            </div>
                        </td>
                    </tr>
                </table>
                <%--<div class="panel panel-default" style="border-radius: 0px">--%>
                    <%--<table class="table table-hover">--%>

                    <%--</table>--%>
                    <%--<div class="panel-footer small">--%>
                        <%--&nbsp;--%>
                        <%--<c:if test="${openedFolder.unreadMessageCount > 0}">--%>
                            <%--有 ${openedFolder.unreadMessageCount} 封邮件未读--%>
                            <%--<button type="submit" id="markAllReadButton" style="display: none;" formaction="/mail/action/markAllRead">Hidden Unread Button</button>--%>
                            <%--<a href="#" onclick="$('#markAllReadButton').click();">全部标记为已读</a>--%>
                        <%--</c:if>--%>
                        <%--<div class="pull-right">--%>
                            <%--共 ${openedFolder.messageCount} 封邮件--%>
                        <%--</div>--%>
                    <%--</div>--%>
                <%--</div>--%>
            </div>
        </sf:form>
    </div>
</div>

<jsp:include page="/WEB-INF/component/Bootstrap-Script.jsp"/>
</body>
</html>