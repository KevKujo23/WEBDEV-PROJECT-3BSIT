<%-- 
    Document   : admin
    Created on : Nov 11, 2025, 8:13:58 PM
    Author     : Kevin
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Login</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>

<jsp:include page="/WEB-INF/header.jsp"/>

<div class="login-container">
  <h2>Login</h2>

  <!-- Notices -->
  <c:choose>
    <c:when test="${param.status == 'unauthorized'}">
      <div class="notice info">Please log in to continue.</div>
    </c:when>
    <c:when test="${param.loggedout == '1' || param.logout == 'ok'}">
      <div class="notice">You have been logged out.</div>
    </c:when>
    <c:when test="${param.error == '1'}">
      <div class="notice error">Invalid username or password.</div>
    </c:when>
  </c:choose>

  <form method="post" action="${pageContext.request.contextPath}/do.login" autocomplete="off">
    <div class="form-group">
      <label>Email or Student #</label>
      <input type="text" name="login" required>
    </div>

    <div class="form-group">
      <label>Password</label>
      <input type="password" name="password" minlength="5" required>
    </div>

    <label style="display:flex;gap:.5rem;align-items:center;margin:.5rem 0 1rem">
      <input type="checkbox" name="remember">
      Remember me
    </label>

    <button type="submit">Login</button>
  </form>

  <div class="register-link">
    Don’t have an account?
    <a href="${pageContext.request.contextPath}/register.jsp">Register</a>
  </div>
</div>

<jsp:include page="/WEB-INF/footer.jsp"/>

</body>
</html>
