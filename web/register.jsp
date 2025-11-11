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
  <meta charset="UTF-8" />
  <title>Register</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
  <style>
    .notice{margin:1rem 0;padding:.75rem;border:1px solid #ccc;border-radius:.5rem;display:none}
    .notice.show{display:block}
  </style>
</head>
<body>

  <jsp:include page="/WEB-INF/header.jsp"/>

  <div class="register-container">
    <h2>Create your account</h2>

    <!-- Status / error banner (no scriptlets, just JSTL/EL) -->
    <c:choose>
      <c:when test="${param.status == 'invalid'}">
        <div class="notice show">
          Please complete all fields correctly:
          email must end with <strong>@uap.asia</strong>, password ≥ 5 chars, and choose a department.
        </div>
      </c:when>
      <c:when test="${param.status == 'exists'}">
        <div class="notice show">
          That student number or email already exists. Try logging in or use a different one.
        </div>
      </c:when>
      <c:when test="${param.status == 'error'}">
        <div class="notice show">
          Something went wrong while creating your account. Please try again.
        </div>
      </c:when>
    </c:choose>

    <!-- Matches RegistrationServlet#doPost parameter names exactly -->
    <form method="post" action="${pageContext.request.contextPath}/do.register" autocomplete="off">
      <div class="form-group">
        <label>First Name
          <input type="text" name="firstName" required />
        </label>
      </div>

      <div class="form-group">
        <label>Last Name
          <input type="text" name="lastName" required />
        </label>
      </div>

      <div class="form-group">
        <label>Student Number
          <input type="text" name="studentNumber" minlength="3" required />
        </label>
      </div>

      <div class="form-group">
        <label>Email (<code>@uap.asia</code>)
          <!-- Basic browser-side hinting; server still validates -->
          <input type="email" name="email" required pattern=".+@uap\.asia" />
        </label>
      </div>

      <!-- IMPORTANT: deptId must be numeric (your servlet expects an int > 0) -->
      <!-- Update the values to match your actual departments.dept_id rows -->
      <div class="form-group">
        <label>Department
          <select name="deptId" required>
            <option value="">Select…</option>
            <option value="1">SSE</option>
            <option value="2">SMN</option>
            <option value="3">SLG</option>
            <option value="4">SCM</option>
            <option value="5">SEC</option>
            <option value="6">CAS</option>
          </select>
        </label>
      </div>

      <div class="form-group">
        <label>Year Level
          <select name="yearLevel" required>
            <option value="1">1st Year</option>
            <option value="2">2nd Year</option>
            <option value="3">3rd Year</option>
            <option value="4">4th Year</option>
          </select>
        </label>
      </div>

      <div class="form-group">
        <label>Password
          <input type="password" name="password" minlength="5" required />
        </label>
      </div>

      <div class="form-group">
        <label>Confirm Password
          <input type="password" name="confirm" minlength="5" required />
        </label>
      </div>

      <button type="submit">Create account</button>
    </form>

    <div class="register-link" style="margin-top:1rem">
      Already have an account? <a href="${pageContext.request.contextPath}/login.jsp">Login</a>
    </div>
  </div>

  <jsp:include page="/WEB-INF/footer.jsp"/>

</body>
</html>
