<%--
  Created by IntelliJ IDEA.
  User: Kevin
  Date: 10/26/2025
  Time: 6:22 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Rate My Professor - Home</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body class="home">

<jsp:include page="/WEB-INF/header.jsp"/>

<div class="panel-container">
    <h2>Rate My Professor</h2>
    <p>Welcome! Use the links above to log in, register, browse, and contribute.</p>

    <h3>How it works</h3>
    <ol>
        <li>Register a student account, then log in.</li>
        <li>Add a professor (name + department).</li>
        <li>Add a rating (1 to 5) with an optional comment.</li>
    </ol>

    <p class="note">Tip: “Remember me” keeps you signed in.</p>
</div>

<jsp:include page="/WEB-INF/footer.jsp"/>

</body>
</html>
