<%-- 
    Document   : register
    Created on : Nov 12, 2025, 11:21:47 PM
    Author     : Alexander
    Velo, Alexander E. | 231514
--%>

<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="container py-4" style="max-width:720px;">
    <h4 class="mb-3">Create an account</h4>

    <c:if test="${not empty formError}">
        <div class="alert alert-danger">${formError}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/register" class="needs-validation" novalidate>
        <div class="mb-3">
            <label class="form-label">Student Number (e.g., 2021-12345)</label>
            <input type="text" name="studentNumber" class="form-control" value="${form.studentNumber}" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Email (must end with @uap.asia)</label>
            <input type="email" name="username" class="form-control" value="${form.username}" required>
            <div class="form-text">Your email will also be your username.</div>
        </div>

        <div class="row g-3">
            <div class="col-md-6">
                <label class="form-label">Password</label>
                <input type="password" name="password" class="form-control" required>
            </div>
            <div class="col-md-6">
                <label class="form-label">Confirm Password</label>
                <input type="password" name="confirm" class="form-control" required>
            </div>
        </div>

        <div class="mt-3">
            <label class="form-label">Department</label>
            <select name="deptId" class="form-select" required>
                <option value="">Select department</option>
                <c:forEach var="d" items="${departments}">
                    <option value="${d.deptId}" ${form.deptId==d.deptId ? 'selected' : ''}>${d.deptCode}</option>
                </c:forEach>
            </select>
        </div>

        <div class="mt-3 d-flex gap-2">
            <button class="btn btn-primary">Register</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/index.jsp">Cancel</a>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
