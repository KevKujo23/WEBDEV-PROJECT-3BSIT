<%-- 
    Document   : index
    Created on : Nov 12, 2025, 10:47:17 PM
    Author     : Alexander
    Velo, Alexander E. | 231514
--%>

<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<div class="container py-4">
    <h4>Admin Dashboard</h4>
    <ul>
        <li><a href="${pageContext.request.contextPath}/admin/professors">Manage Professors</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/subjects">Manage Subjects</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/ratings">Moderate Ratings</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/import">CSV Import</a></li>
    </ul>
</div>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
