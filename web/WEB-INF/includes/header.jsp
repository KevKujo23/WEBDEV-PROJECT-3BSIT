<%-- 
    Document   : header
    Created on : Nov 12, 2025, 10:44:25?PM
    Author     : Alexander
    Velo, Alexander E. | 231514
--%>

<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>

            <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
                <div class="container">
                    <a class="navbar-brand" href="${pageContext.request.contextPath}/professors">Rate My Professor</a>
                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#topNav">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="topNav">
                        <ul class="navbar-nav me-auto">

                <!-- Always visible -->
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/professors">Professors</a>
                </li>

                <!-- Students ONLY -->
                <c:if test="${sessionScope.role == 'student'}">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/profile">Profile</a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/my-ratings">My Ratings</a>
                    </li>
                </c:if>

                <!-- Admin ONLY -->
                <c:if test="${sessionScope.role == 'admin'}">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin">Admin</a>
                    </li>
                </c:if>

            </ul>

            <c:choose>
                <c:when test="${not empty sessionScope.userId}">
                    <span class="navbar-text me-3 small">
                        Logged in as <strong>${sessionScope.username}</strong> (${sessionScope.email})
                    </span>
                    <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/logout">Logout</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-outline-light btn-sm me-2" href="${pageContext.request.contextPath}/index.jsp">Login</a>
                    <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/register">Register</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
