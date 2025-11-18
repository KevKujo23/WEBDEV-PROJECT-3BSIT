<%-- 
    Document   : ratings
    Created on : Nov 12, 2025, 10:48:39 PM
    Author     : Alexander
    Velo, Alexander E. | 231514
--%>

<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<div class="container py-4">
    <h4>Moderate Ratings</h4>
    <c:if test="${param.msg=='deleted'}"><div class="alert alert-success">Rating deleted.</div></c:if>
    <table class="table table-striped table-hover align-middle">
        <thead>
            <tr>
                <th>ID</th><th>User</th><th>Professor</th><th>Subject</th>
                <th>C</th><th>F</th><th>E</th><th>K</th>
                <th>Comment</th><th>Actions</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach var="r" items="${ratings}">
            <tr>
                <td>${r.ratingId}</td>
                <td>${r.userId}</td>
                <td>${r.professorName}</td>
                <td>${r.subjectCode}</td>
                <td>${r.clarity}</td>
                <td>${r.fairness}</td>
                <td>${r.engagement}</td>
                <td>${r.knowledge}</td>
                <td class="text-truncate" style="max-width:280px">${r.comment}</td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/admin/ratings/delete" onsubmit="return confirm('Delete this rating?')">
                        <input type="hidden" name="ratingId" value="${r.ratingId}"/>
                        <button class="btn btn-sm btn-outline-danger">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
