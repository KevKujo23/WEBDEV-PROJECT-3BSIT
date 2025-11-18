<%-- 
    Document   : subject_form
    Created on : Nov 12, 2025, 10:48:21 PM
    Author     : Alexander
    Velo, Alexander E. | 231514
--%>

<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<div class="container py-4" style="max-width:720px;">
    <h4 class="mb-3">
        <c:choose><c:when test="${isEdit}">Edit Subject</c:when><c:otherwise>Add Subject</c:otherwise></c:choose>
    </h4>
    <form method="post" action="${pageContext.request.contextPath}${isEdit?'/admin/subjects/update':'/admin/subjects/create'}">
        <c:if test="${isEdit}">
            <input type="hidden" name="subjectId" value="${subject.subjectId}"/>
        </c:if>
        <div class="mb-3">
            <label class="form-label">Department</label>
            <select class="form-select" name="deptId" required>
                <c:forEach var="d" items="${departments}">
                    <option value="${d.deptId}" ${isEdit && d.deptId==subject.deptId ? 'selected':''}>${d.deptCode}</option>
                </c:forEach>
            </select>
        </div>
        <div class="mb-3">
            <label class="form-label">Subject Code</label>
            <input type="text" class="form-control" name="subjectCode" value="${isEdit?subject.subjectCode:''}" required/>
        </div>
        <div class="mb-3">
            <label class="form-label">Subject Name</label>
            <input type="text" class="form-control" name="subjectName" value="${isEdit?subject.subjectName:''}" required/>
        </div>
        <div class="d-flex gap-2">
            <button class="btn btn-primary">${isEdit?'Update':'Create'}</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/subjects">Cancel</a>
        </div>
    </form>
</div>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
