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
    <title>Register</title>
    <link rel="stylesheet" href="css/styles.css">
    <style>.notice{margin:1rem 0;padding:.75rem;border:1px solid #ccc;border-radius:.5rem;display:none}</style>
</head>
<body>

<jsp:include page="/WEB-INF/header.jsp"/>

<div class="register-container">
    <h2>Create your account</h2>
    <div id="msg" class="notice"></div>

    <form method="post" action="do.register" autocomplete="off">
        <div class="form-group">
            <label>First Name
                <input type="text" name="firstName" required>
            </label>
        </div>

        <div class="form-group">
            <label>Last Name
                <input type="text" name="lastName" required>
            </label>
        </div>

        <div class="form-group">
            <label>Email (@uap.asia)
                <input type="email" name="email" required>
            </label>
        </div>

        <div class="form-group">
            <label>Department
                <select name="department" required>
                    <option value="">Select…</option>
                    <option>SSE</option>
                    <option>SMN</option>
                    <option>SLG</option>
                    <option>SCM</option>
                    <option>SEC</option>
                </select>
            </label>
        </div>

        <div class="form-group">
            <label>Username
                <input type="text" name="username" minlength="3" required>
            </label>
        </div>

        <div class="form-group">
            <label>Password
                <input type="password" name="password" minlength="5" required>
            </label>
        </div>

        <div class="form-group">
            <label>Confirm Password
                <input type="password" name="confirm" minlength="5" required>
            </label>
        </div>

        <button type="submit">Create account</button>
    </form>

    <div class="register-link">
        Already have an account? <a href="login.jsp">Login</a>
    </div>
</div>

<jsp:include page="/WEB-INF/footer.jsp"/>

<script>
    (function(){
        var p=new URLSearchParams(location.search);
        var m=document.getElementById('msg'); var t="";
        if(p.get('status')==='invalid') {
            t="Please complete all fields correctly (email must end with @uap.asia, password ≥ 5, unique username).";
        }
        if(t){ m.textContent=t; m.style.display='block'; }
    })();
</script>

</body>
</html>

