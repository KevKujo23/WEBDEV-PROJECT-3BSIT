<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <title>Browse Professors</title>
  <link rel="stylesheet" href="<c:url value='/css/styles.css'/>"/>
  <link rel="stylesheet" href="<c:url value='/css/professors-browse.css'/>"/>
</head>
<body class="page-professors">
  <jsp:include page="/WEB-INF/header.jsp"/>

  <main class="container">
    <section class="browse-header">
      <h1>Browse Professors</h1>
      <p class="muted">Find professors by name, course, department, and rating.</p>
    </section>

    <section class="browse-controls">
      <div class="control">
        <label for="q">Search name</label>
        <input id="q" type="search" placeholder="e.g., Santos or Maria" autocomplete="off"/>
      </div>
      <div class="control">
        <label for="course">Course</label>
        <input id="course" type="search" placeholder="e.g., CS123, Calculus" autocomplete="off"/>
      </div>
      <div class="control">
        <label for="dept">Department</label>
        <select id="dept">
          <option value="">All</option>
          <c:forEach var="d" items="${departments}">
            <option value="${d.deptId}">${d.deptName}</option>
          </c:forEach>
        </select>
      </div>
      <div class="control">
        <label for="minRating">Min rating</label>
        <input id="minRating" type="number" min="0" max="5" step="0.5" placeholder="0–5"/>
      </div>
      <div class="control">
        <label for="sort">Sort</label>
        <select id="sort">
          <option value="most">Most rated</option>
          <option value="highest">Highest rated</option>
          <option value="recent">Recently rated</option>
          <option value="az">A–Z</option>
        </select>
      </div>
    </section>

    <section class="recent-searches">
      <div class="recent-title">Recent searches</div>
      <div id="recent-chips" class="chips"></div>
    </section>

    <section id="grid" class="cards-grid" aria-live="polite"></section>

    <div id="loader" class="loader" hidden>Loading…</div>
    <div id="empty" class="empty-state" hidden>No professors found. Try a different search.</div>
  </main>

  <jsp:include page="/WEB-INF/footer.jsp"/>

  <script src="<c:url value='/js/professors-browse.js'/>" defer></script>
</body>
</html>
