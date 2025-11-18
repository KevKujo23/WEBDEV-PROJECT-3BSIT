<%-- 
    Document   : not_allowed
    Created on : Nov 18, 2025, 3:18:01 AM
    Author     : Kevin
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="container py-4">
    <div class="alert alert-warning">
        <strong>Access Restricted</strong><br/>
        ${msg}
    </div>

    <a href="${pageContext.request.contextPath}/" class="btn btn-secondary btn-sm">
        Back to Home
    </a>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>

