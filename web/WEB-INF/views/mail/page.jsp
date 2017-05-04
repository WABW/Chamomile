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
            <div class="row" style="padding-bottom: 10px">
                <div class="btn-group" role="group" style="padding-left: 11px; padding-right: 22px">
                    <button type="submit" class="btn btn-default" formaction="/mail/action/refresh">
                        &nbsp<span class="glyphicon glyphicon-repeat"></span>&nbsp
                    </button>
                </div>

                <div class="btn-group" role="group" style="padding-right: 11px">
                    <button class="btn btn-default" type="submit" formaction="/mail/action/trash">
                        &nbsp<span class="glyphicon glyphicon-trash"></span>&nbsp
                    </button>
                    <div class="btn-group" role="group">
                        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            &nbsp<span class="glyphicon glyphicon-folder-open"></span>&nbsp
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
                <div class="panel panel-default" style="border-radius: 0px">
                    <table class="table table-hover">

                        <c:if test="${0 == openedFolder.messageCount}">
                            <tr>
                                <td class="text-center">
                                    没有新邮件！
                                </td>
                            </tr>
                        </c:if>
                        <c:forEach items="${messages}" var="message">
                            <c:set value="${message.isSet(Flag_SEEN)}" var="isSeen"></c:set>
                            <c:set value="${message.isSet(Flag_FLAGGED)}" var="isFlagged"></c:set>
                            <tr>
                                <td class="${isSeen ? "" : 'text-info'}">
                                    <input type="checkbox" id="mailNumbers[]" name="mailNumbers[]" value="${message.messageNumber}"/>
                                    <c:if test="${isFlagged}">
                                        <span class="glyphicon glyphicon-flag text-danger"></span>
                                    </c:if>
                                    <c:choose>
                                        <c:when test="${!isSeen}">
                                            [未读] ${message.subject}
                                        </c:when>
                                        <c:otherwise>
                                            ${message.subject}
                                        </c:otherwise>
                                    </c:choose>

                                </td>
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