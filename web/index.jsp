<%-- 
    Document   : index
    Created on : Nov 12, 2025, 10:43:57 PM
    Author     : Alexander
    Velo, Alexander E. | 231514
--%>

<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="container py-5" style="max-width:520px;">
    <h3 class="mb-3">Sign in</h3>

    <c:if test="${param.err=='login'}"><div class="alert alert-warning">Please sign in.</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>
    <c:if test="${param.msg=='loggedout'}"><div class="alert alert-success">You have been logged out.</div></c:if>

        <form method="post" action="${pageContext.request.contextPath}/login" novalidate>
        <div class="mb-3">
            <label class="form-label">Username (must end with @uap.asia)</label>
            <input type="email" name="username" class="form-control" placeholder="your.name@uap.asia" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Password</label>
            <input type="password" name="password" class="form-control" required>
        </div>
        <button class="btn btn-dark w-100">Sign in</button>
        <p class="mt-3 small">
            No account yet?
            <a href="${pageContext.request.contextPath}/register">Create one</a>.
        </p>

    </form>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
