<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<table class="table table-bordered">
    <c:forEach items="${folders}" var="folder">
        <tr>
            <td><a href="/mail/${folder.name}">${folder.name}
                <c:if test="${folder.unreadMessageCount > 0}">
                    <span class="badge pull-right">${folder.unreadMessageCount}</span>
                </c:if></a></td>
        </tr>
    </c:forEach>
</table>