<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="user" value="${sessionScope.user}" />
<c:set var="loggedIn" value="${not empty user}" />
<c:set var="isAdmin" value="${loggedIn and user.role == 'ADMIN'}" />

<nav class="top-nav">
  <!-- Browse (public) -->
  <a href="<c:url value='/professors'/>">Browse</a>

  <!-- Student -->
  <c:if test="${loggedIn and not isAdmin}">
    <a href="${ctx}/do.profile">Profile</a>
    <a href="${ctx}/do.logout" class="logout-link">Logout</a>
  </c:if>

  <!-- Admin -->
  <c:if test="${loggedIn and isAdmin}">
    <a href="${ctx}/admin">Admin</a>
    <a href="${ctx}/do.profile">Profile</a>
    <a href="${ctx}/do.logout" class="logout-link">Logout</a>
  </c:if>

  <!-- Guest -->
  <c:if test="${not loggedIn}">
    <a href="${ctx}/login.jsp">Login</a>
    <a href="${ctx}/register.jsp">Register</a>
  </c:if>
</nav>
