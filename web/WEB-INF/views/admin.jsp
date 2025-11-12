<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>Admin Dashboard</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=7">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css?v=7">
</head>
<body class="admin">

<jsp:include page="/WEB-INF/header.jsp"/>

<div class="container">
  <h1 class="page-title">Admin Dashboard</h1>

  <c:if test="${not empty param.status}">
    <div id="toast" class="toast">${param.status}</div>
  </c:if>

  <div class="tabs">
    <button class="tab active" data-tab="tab-professors">Professors</button>
    <button class="tab" data-tab="tab-ratings">Ratings</button>
    <button class="tab" data-tab="tab-subjects">Subjects</button>
  </div>

  <!-- Professors -->
  <section id="tab-professors" class="tab-panel active">
    <div class="toolbar">
      <div class="left">
        <button id="btnAddProfessor" class="btn primary">+ Add Professor</button>
        <select id="filterDept" class="select">
          <option value="">All Departments</option>
          <c:forEach var="d" items="${departments}">
            <option value="${d.deptId}">${d.deptName}</option>
          </c:forEach>
        </select>
      </div>
      <div class="right">
        <input type="search" id="profSearch" class="input" placeholder="Search professor…">
      </div>
    </div>

    <div class="card">
      <table class="table" id="profTable">
        <thead>
          <tr><th>ID</th><th>Name</th><th>Department</th><th class="col-actions">Actions</th></tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${professors}">
          <tr data-id="${p.profId}"
              data-name="${empty p.fullName ? (p.firstName) : p.fullName} ${p.lastName}"
              data-dept="${p.deptId}"
              data-subjects="${profSubjectsCsv[p.profId]}">
            <td>${p.profId}</td>
            <td>
              <c:out value="${empty p.fullName ? (p.firstName) : p.fullName}"/> <c:out value="${p.lastName}"/>
            </td>
            <td>
              <c:forEach var="d" items="${departments}">
                <c:if test="${d.deptId == p.deptId}">
                  <span class="badge">${d.deptName}</span>
                </c:if>
              </c:forEach>
            </td>
            <td class="actions">
              <button class="btn ghost btnEditProf"
                      data-id="${p.profId}" data-name="${empty p.fullName ? (p.firstName) : p.fullName} ${p.lastName}" data-dept="${p.deptId}">Edit</button>
              <form method="post" action="${pageContext.request.contextPath}/do.admin.delete" class="inline confirmDelete">
                <input type="hidden" name="type" value="professor">
                <input type="hidden" name="id" value="${p.profId}">
                <button class="btn danger" type="submit">Delete</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
      <c:if test="${empty professors}"><div class="empty">No professors yet.</div></c:if>
    </div>
  </section>

  <<!-- Ratings (cards feed) -->
<section id="tab-ratings" class="tab-panel">
  <div class="toolbar rating-toolbar">
    <div class="left">
      <select id="scoreFilter" class="select">
        <option value="">All Scores</option>
        <option value="5">5★</option><option value="4">4★</option>
        <option value="3">3★</option><option value="2">2★</option>
        <option value="1">1★</option>
      </select>
      <input type="number" id="yearFilter" class="input sm" min="1990" max="2100" placeholder="Year">
      <select id="termFilter" class="select sm">
        <option value="">Any Term</option>
        <option>1st</option><option>2nd</option><option>Summer</option>
      </select>
    </div>
    <div class="right">
      <input type="search" id="ratingSearch" class="input" placeholder="Search by professor/comment…">
    </div>
  </div>

  <div id="ratingFeed" class="rating-feed">
    <c:forEach var="r" items="${ratings}">
      <article class="card rating-card"
               data-id="${r.ratingId}"
               data-score="${r.score}"
               data-year="${r.academicYear}"
               <c:choose>
                 <c:when test="${not empty r.term}">data-term="${r.term.dbValue}"</c:when>
                 <c:otherwise>data-term=""</c:otherwise>
               </c:choose>>
        <header>
          <div>
            <div class="who">
              <!-- Prefer name via map; fallback to ID -->
              <c:choose>
                <c:when test="${not empty profNameById}">
                  <c:out value="${profNameById[r.profId]}"/>
                </c:when>
                <c:otherwise>Prof #<c:out value="${r.profId}"/></c:otherwise>
              </c:choose>
              <span class="submeta">
                •
                <c:choose>
                  <c:when test="${not empty subjectLabelById}">
                    <c:out value="${subjectLabelById[r.subjectId]}"/>
                  </c:when>
                  <c:otherwise>Subject #<c:out value="${r.subjectId}"/></c:otherwise>
                </c:choose>
              </span>
            </div>
            <div class="submeta">
              AY <c:out value="${r.academicYear}"/>
              <c:choose>
                <c:when test="${not empty r.term}"> • <c:out value="${r.term.dbValue}"/></c:when>
              </c:choose>
              • <c:out value="${r.createdAt}"/>
            </div>
          </div>
          <div class="meta pill score">⭐ <c:out value="${r.score}"/>/5</div>
        </header>

        <div class="text"><c:out value="${r.comment}"/></div>

        <div class="pills">
          <span class="pill">ID: <c:out value="${r.ratingId}"/></span>
          <span class="pill">Prof: <c:out value="${r.profId}"/></span>
          <span class="pill">Subject: <c:out value="${r.subjectId}"/></span>
        </div>

        <footer class="actions">
          <form method="post" action="${pageContext.request.contextPath}/do.admin.delete" class="inline confirmDelete">
            <input type="hidden" name="type" value="rating">
            <input type="hidden" name="id" value="${r.ratingId}">
            <button class="btn danger" type="submit">Delete</button>
          </form>
        </footer>
      </article>
    </c:forEach>
  </div>

  <c:if test="${empty ratings}">
    <div class="empty card">No ratings yet.</div>
  </c:if>
</section>

  <!-- Subjects -->
  <section id="tab-subjects" class="tab-panel">
    <div class="toolbar">
      <div class="left">
        <button id="btnAddSubject" class="btn primary">+ Add Subject</button>
      </div>
      <div class="right">
        <input type="search" id="subjectSearch" class="input" placeholder="Search subject…">
      </div>
    </div>

    <div class="card">
      <table class="table" id="subjectTable">
        <thead>
          <tr><th>ID</th><th>Code</th><th>Title</th><th>Department</th><th class="col-actions">Actions</th></tr>
        </thead>
        <tbody>
        <c:forEach var="s" items="${subjectsFull}">
          <tr data-id="${s.id}" data-code="${s.code}" data-title="${s.title}" data-dept="${s.deptId}">
            <td>${s.id}</td>
            <td><code>${s.code}</code></td>
            <td>${s.title}</td>
            <td>${s.deptName}</td>
            <td class="actions">
              <button class="btn ghost btnEditSubject"
                      data-id="${s.id}" data-code="${s.code}" data-title="${s.title}" data-dept="${s.deptId}">Edit</button>
              <form method="post" action="${pageContext.request.contextPath}/do.admin.subject.delete" class="inline confirmDelete">
                <input type="hidden" name="id" value="${s.id}">
                <button class="btn danger" type="submit">Delete</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
      <c:if test="${empty subjectsFull}"><div class="empty">No subjects yet.</div></c:if>
    </div>
  </section>
</div>

<!-- Modal: Add/Edit Professor -->
<div id="modalBackdrop" class="modal-backdrop hidden"></div>

<div id="profModal" class="modal hidden" role="dialog" aria-modal="true" aria-labelledby="profModalTitle">
  <form method="post" action="${pageContext.request.contextPath}/do.admin.professor.save" class="modal-card" id="profForm">
    <div class="modal-header">
      <h3 id="profModalTitle">Add Professor</h3>
      <button type="button" class="icon-close" data-close>×</button>
    </div>
    <div class="modal-body">
      <input type="hidden" name="id" id="profId">

      <label class="field"><span>Name</span>
        <input type="text" name="name" id="profName" required>
      </label>

      <label class="field"><span>Department</span>
        <select name="departmentId" id="profDept" class="select" required>
          <c:forEach var="d" items="${departments}">
            <option value="${d.deptId}">${d.deptName}</option>
          </c:forEach>
        </select>
      </label>

      <label class="field"><span>Subjects</span>
        <select name="subjectIds" id="profSubjects" class="select" multiple size="6">
          <c:forEach var="s" items="${subjects}">
            <option value="${s.id}">${s.label}</option>
          </c:forEach>
        </select>
        <small class="hint">Hold Ctrl/⌘ to select multiple.</small>
      </label>
    </div>
    <div class="modal-footer">
      <button class="btn ghost" type="button" data-close>Cancel</button>
      <button class="btn primary" type="submit">Save</button>
    </div>
  </form>
</div>

<!-- Modal: Add/Edit Subject -->
<div id="subjectModal" class="modal hidden" role="dialog" aria-modal="true" aria-labelledby="subjectModalTitle">
  <form method="post" action="${pageContext.request.contextPath}/do.admin.subject.save" class="modal-card" id="subjectForm">
    <div class="modal-header">
      <h3 id="subjectModalTitle">Add Subject</h3>
      <button type="button" class="icon-close" data-close>×</button>
    </div>
    <div class="modal-body">
      <input type="hidden" name="id" id="subjectId">
      <label class="field"><span>Code</span>
        <input type="text" name="code" id="subjectCode" required>
      </label>
      <label class="field"><span>Title</span>
        <input type="text" name="title" id="subjectTitle" required>
      </label>
      <label class="field"><span>Department</span>
        <select name="departmentId" id="subjectDept" class="select" required>
          <c:forEach var="d" items="${departments}">
            <option value="${d.deptId}">${d.deptName}</option>
          </c:forEach>
        </select>
      </label>
    </div>
    <div class="modal-footer">
      <button class="btn ghost" type="button" data-close>Cancel</button>
      <button class="btn primary" type="submit">Save</button>
    </div>
  </form>
</div>

<script defer src="${pageContext.request.contextPath}/js/admin.js?v=7"></script>
</body>
</html>
