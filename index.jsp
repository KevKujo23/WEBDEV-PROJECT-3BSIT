<%-- 
    Document   : index
    Created on : Oct 25, 2025, 1:31:18 PM
    Author     : Aldrin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
         <link rel="stylesheet" href="css/styles.css"/>
        <title>Rate My Professor</title>
    </head>
    <body>
  <div class="login-container">
    <h2>Login</h2>
    <form action="do.login" method="post">
      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" required /> <!-- REMOVE REQUIRED AFTER SERVLET CONFIGURATION -->
      </div>
      <div class="form-group">
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required /> <!-- REMOVE REQUIRED AFTER SERVLET CONFIGURATION -->
      </div>
      <button type="submit">Login</button>
    </form>
    <div class="register-link">
      <p>Don't have an account? <a href="register.jsp">Register</a></p> 
    </div>
  </div>
</body>
</html>
