<%-- 
    Document   : professors
    Created on : Nov 12, 2025, 10:47:32 PM
    Author     : Alexander
    Velo, Alexander E. | 231514
--%>

<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4>Professors</h4>
        <c:if test="${param.msg=='saved'}"><div class="alert alert-success">Professor saved.</div></c:if>
        <c:if test="${param.msg=='updated'}"><div class="alert alert-success">Professor updated.</div></c:if>
        <c:if test="${param.msg=='deleted'}"><div class="alert alert-success">Professor deleted.</div></c:if>

            <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/professors/new">Add Professor</a>
    </div>
    <table class="table table-striped table-hover align-middle">
        <thead><tr><th>ID</th><th>Name</th><th>Department</th><th>Actions</th></tr></thead>
        <tbody>
            <c:forEach var="p" items="${professors}">
                <tr>
                    <td>${p.profId}</td>
                    <td>${p.name}</td>
                    <td>${p.deptCode}</td>
                    <td class="d-flex gap-2">
                        <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/admin/professors/edit?id=${p.profId}">Edit</a>
                        <form method="post" action="${pageContext.request.contextPath}/admin/professors/delete" onsubmit="return confirm('Delete this professor? This will cascade delete related assignments and ratings.')">
                            <input type="hidden" name="id" value="${p.profId}"/>
                            <button class="btn btn-sm btn-outline-danger">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
