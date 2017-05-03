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
    <div class="panel panel-default">
        <div class="panel-heading">
            Heading
        </div>
        <div class="panel-body">
            <jsp:include page="form.jsp"/>
        </div>
        <div class="panel-footer">
            Footer
        </div>
    </div>
</div>


<jsp:include page="../../component/Bootstrap-Script.jsp"/>
</body>
</html>