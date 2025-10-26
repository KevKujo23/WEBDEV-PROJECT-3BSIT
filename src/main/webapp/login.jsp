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
    <title>Login</title>
    <link rel="stylesheet" href="css/styles.css">
    <style>.notice{margin:1rem 0;padding:.75rem;border:1px solid #ccc;border-radius:.5rem;display:none}</style>
</head>
<body>

<jsp:include page="/WEB-INF/header.jsp"/>

<div class="login-container">
    <h2>Login</h2>

    <div id="msg" class="notice"></div>

    <form method="post" action="do.login" autocomplete="off">
        <div class="form-group">
            <label>Username</label>
            <input type="text" name="username" required>
        </div>

        <div class="form-group">
            <label>Password</label>
            <input type="password" name="password" minlength="5" required>
        </div>

        <div class="form-group">
            <label><input type="checkbox" name="remember"> Remember me</label>
        </div>

        <button type="submit">Login</button>
    </form>

    <div class="register-link">
        Don’t have an account? <a href="register.jsp">Register</a>
    </div>
</div>

<jsp:include page="/WEB-INF/footer.jsp"/>

<script>
    (function(){
        var p=new URLSearchParams(location.search);
        var m=document.getElementById('msg'); var t="";
        if(p.get('status')==='unauthorized') t="Please login to continue.";
        if(p.get('loggedout')==='1') t="You have been logged out.";
        if(p.get('error')==='1') t="Invalid username or password.";
        if(t){ m.textContent=t; m.style.display='block'; }
    })();
</script>

</body>
</html>


