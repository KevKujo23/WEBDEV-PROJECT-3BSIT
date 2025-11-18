<%-- 
    Document   : import
    Created on : Nov 12, 2025, 10:48:51 PM
    Author     : Alexander
    Velo, Alexander E. | 231514
--%>

<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<div class="container py-4" style="max-width:720px;">
    <h4 class="mb-3">CSV Import</h4>
    <p class="text-muted">Upload CSV to add Professors or Subjects.</p>
    <ul>
        <li><strong>professors</strong> CSV: name, dept_code</li>
        <li><strong>subjects</strong> CSV: dept_code, subject_code, subject_name</li>
    </ul>
    <form method="post" action="${pageContext.request.contextPath}/admin/import" enctype="multipart/form-data">
        <div class="mb-3">
            <label class="form-label">Type</label>
            <select name="type" class="form-select" required>
                <option value="professors">Professors</option>
                <option value="subjects">Subjects</option>
            </select>
        </div>
        <div class="mb-3">
            <label class="form-label">CSV File</label>
            <input type="file" name="file" class="form-control" accept=".csv" required/>
        </div>
        <button class="btn btn-primary">Upload</button>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin">Back</a>
    </form>
    <c:if test="${not empty importMsg}">
        <div class="alert alert-info mt-3">${importMsg}</div>
    </c:if>
</div>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
