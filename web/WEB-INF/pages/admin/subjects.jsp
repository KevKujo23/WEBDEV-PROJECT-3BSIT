<%-- 
    Document   : subjects
    Created on : Nov 12, 2025, 10:48:06 PM
    Author     : Alexander
    Velo, Alexander E. | 231514
--%>

<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4>Subjects</h4>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/subjects/new">Add Subject</a>
    </div>
    <table class="table table-striped table-hover align-middle">
        <thead><tr><th>ID</th><th>Code</th><th>Name</th><th>Department</th><th>Actions</th></tr></thead>
        <tbody>
        <c:forEach var="s" items="${subjects}">
            <tr>
                <td>${s.subjectId}</td>
                <td>${s.subjectCode}</td>
                <td>${s.subjectName}</td>
                <td>${s.deptCode}</td>
                <td class="d-flex gap-2">
                    <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/admin/subjects/edit?id=${s.subjectId}">Edit</a>
                    <form method="post" action="${pageContext.request.contextPath}/admin/subjects/delete" onsubmit="return confirm('Delete this subject? This cascades to assignments and ratings.')">
                        <input type="hidden" name="id" value="${s.subjectId}"/>
                        <button class="btn btn-sm btn-outline-danger">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
