<%-- 
    Document   : register
    Created on : Oct 25, 2025, 2:13:59 PM
    Author     : Aldrin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" href="css/styles.css">
    </head>
    <body>
        <div class="register-container">
            <h2>Create Account</h2>
    <form action="register.html" method="post">
        
      <div class="form-group">
        <label for="fullname">Full Name:</label>
        <input type="text" id="fullname" name="fullname" required> <!-- REMOVE REQUIRED AFTER SERVLET CONFIGURATION -->
      </div>

      <div class="form-group">
        <label for="email">Email:</label>
        <input type="email" id="email" name="email" required> <!-- REMOVE REQUIRED AFTER SERVLET CONFIGURATION -->
      </div>

      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" required> <!-- REMOVE REQUIRED AFTER SERVLET CONFIGURATION -->
      </div>

      <div class="form-group">
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required> <!-- REMOVE REQUIRED AFTER SERVLET CONFIGURATION -->
      </div>

      <div class="form-group">
        <label for="confirm-password">Confirm Password:</label>
        <input type="password" id="confirm-password" name="confirm-password" required> <!-- REMOVE REQUIRED AFTER SERVLET CONFIGURATION -->
      </div>

      <button type="submit">Register</button>
    </form>

    <div class="register-link">
      <p>Already have an account? <a href="index.jsp">Login here</a></p>
    </div>
  </div>
</body>
</html>
