<%-- 
    Document   : professor_form
    Created on : Nov 12, 2025, 10:47:48 PM
    Author     : Alexander
    Velo, Alexander E. | 231514
--%>

<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<div class="container py-4" style="max-width:820px;">
    <h4 class="mb-3">
        <c:choose><c:when test="${isEdit}">Edit Professor</c:when><c:otherwise>Add Professor</c:otherwise></c:choose>
            </h4>

    <c:if test="${not empty formError}">
        <div class="alert alert-danger">${formError}</div>
    </c:if>

    <form method="post"
          action="${pageContext.request.contextPath}${isEdit?'/admin/professors/update':'/admin/professors/create'}"
          onsubmit="this.querySelector('button[type=submit]').disabled = true;">
        <c:if test="${isEdit}">
            <input type="hidden" name="profId" value="${prof.profId}"/>
        </c:if>

        <div class="row g-3">
            <div class="col-md-6">
                <label class="form-label">Name</label>
                <input type="text" class="form-control" name="name" value="${isEdit?prof.name:''}" required/>
            </div>
            <div class="col-md-6">
                <label class="form-label">Department</label>
                <select class="form-select" name="deptId" required>
                    <c:forEach var="d" items="${departments}">
                        <option value="${d.deptId}" ${isEdit && d.deptId==prof.deptId ? 'selected':''}>${d.deptCode}</option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div class="mt-3">
            <label class="form-label">Subjects (hold Ctrl/Cmd to select multiple)</label>
            <select class="form-select" name="subjectIds" multiple size="10">
                <c:forEach var="s" items="${allSubjects}">
                    <option value="${s.subjectId}"
                            <c:if test="${selectedSubjectIds != null && selectedSubjectIds.contains(s.subjectId)}">selected</c:if>>
                        ${s.subjectCode} — ${s.subjectName}
                    </option>
                </c:forEach>
            </select>
            <div class="form-text">
                Tip: a professor won’t appear on the public Professors page until at least one subject is linked.
            </div>
        </div>

        <div class="d-flex gap-2 mt-3">
            <button class="btn btn-primary">${isEdit?'Update':'Create'}</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/professors">Cancel</a>
        </div>
    </form>
</div>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
