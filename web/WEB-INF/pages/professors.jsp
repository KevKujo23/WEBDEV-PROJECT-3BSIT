<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="container py-4">

    <!-- Page header / context -->
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h3 class="mb-0">Professors</h3>
            <small class="text-muted">
                <c:choose>
                    <c:when test="${empty professors}">
                        No professors found for the current filters.
                    </c:when>
                    <c:otherwise>
                        Showing
                        ${fn:length(professors)}
                        professor<c:if test="${fn:length(professors) ne 1}">s</c:if>
                    </c:otherwise>
                </c:choose>
            </small>
        </div>
    </div>

    <!-- Filters -->
    <form class="row gy-2 gx-3 align-items-end mb-3"
          method="get"
          action="${pageContext.request.contextPath}/professors">

        <div class="col-md-3">
            <label class="form-label">Department</label>
            <select class="form-select" name="deptId">
                <option value="">All</option>
                <c:forEach var="d" items="${departments}">
                    <option value="${d.deptId}" ${param.deptId==d.deptId ? 'selected' : ''}>
                        ${d.deptCode}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="col-md-3">
            <label class="form-label">Subject</label>
            <select class="form-select" name="subjectId">
                <option value="">All</option>
                <c:forEach var="s" items="${subjects}">
                    <option value="${s.subjectId}" ${param.subjectId==s.subjectId ? 'selected' : ''}>
                        ${s.subjectCode} - ${s.subjectName}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="col-md-3">
            <label class="form-label">Search</label>
            <input type="text"
                   class="form-control"
                   name="q"
                   value="${param.q}"
                   placeholder="Professor or subject code">
        </div>

        <div class="col-md-3">
            <button class="btn btn-dark w-100" type="submit">Filter</button>
        </div>
    </form>

    <hr/>

    <!-- Results -->
    <c:choose>
    <c:when test="${empty professors}">
        <div class="alert alert-info mt-3">
            No professors match your current filters.
            <a href="${pageContext.request.contextPath}/professors" class="alert-link">
                Clear filters
            </a>
            and try again.
        </div>
    </c:when>

    <c:otherwise>
        <div class="row row-cols-1 row-cols-md-2 g-3">
            <c:forEach var="row" items="${professors}">
                <div class="col">
                    <div class="card shadow-sm h-100">
                        <div class="card-body d-flex flex-column">

                            <h5 class="card-title mb-1">${row.professorName}</h5>

                            <div class="text-muted small mb-1">
                                ${row.deptCode}
                            </div>

                            <div class="mb-2">
                                <strong>${row.subjectCode}</strong> — ${row.subjectName}
                            </div>

                            <c:choose>
                                <c:when test="${row.ratingCount gt 0}">
                                    <div class="mb-2 small">

                                        <div>
                                            <strong>
                                                Avg Rating:
                                                <fmt:formatNumber value="${row.avgOverall}"
                                                                  minFractionDigits="1"
                                                                  maxFractionDigits="1"/>
                                            </strong>
                                            <span class="text-muted">
                                                (${row.ratingCount}
                                                rating<c:if test="${row.ratingCount ne 1}">s</c:if>)
                                            </span>
                                        </div>

                                        <div class="text-muted">
                                            Lowest:
                                            <fmt:formatNumber value="${row.minOverall}"
                                                              minFractionDigits="1"
                                                              maxFractionDigits="1"/>
                                            &nbsp;•&nbsp;
                                            Highest:
                                            <fmt:formatNumber value="${row.maxOverall}"
                                                              minFractionDigits="1"
                                                              maxFractionDigits="1"/>
                                        </div>

                                        <div class="mt-2">
                                            <span class="badge bg-light text-dark me-1">
                                                Clarity:
                                                <fmt:formatNumber value="${row.avgClarity}"
                                                                  minFractionDigits="1"
                                                                  maxFractionDigits="1"/>
                                            </span>
                                            <span class="badge bg-light text-dark me-1">
                                                Fairness:
                                                <fmt:formatNumber value="${row.avgFairness}"
                                                                  minFractionDigits="1"
                                                                  maxFractionDigits="1"/>
                                            </span>
                                            <span class="badge bg-light text-dark me-1">
                                                Engagement:
                                                <fmt:formatNumber value="${row.avgEngagement}"
                                                                  minFractionDigits="1"
                                                                  maxFractionDigits="1"/>
                                            </span>
                                            <span class="badge bg-light text-dark">
                                                Knowledge:
                                                <fmt:formatNumber value="${row.avgKnowledge}"
                                                                  minFractionDigits="1"
                                                                  maxFractionDigits="1"/>
                                            </span>
                                        </div>

                                        <c:if test="${not empty row.lastRatingDate}">
                                            <div class="text-muted mt-2">
                                                Last Rating:
                                                <fmt:formatDate value="${row.lastRatingDate}"
                                                                pattern="yyyy-MM-dd HH:mm"/>
                                            </div>
                                        </c:if>

                                    </div>
                                </c:when>

                                <c:otherwise>
                                    <div class="mb-2 text-muted small">
                                        No ratings yet.
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <!-- Actions pinned to bottom of card -->
                            <div class="mt-auto pt-2">
                                <c:if test="${sessionScope.role eq 'student'}">
                                    <div class="d-flex gap-2">
                                        <a class="btn btn-outline-primary btn-sm flex-fill"
                                           href="${pageContext.request.contextPath}/rating?action=form&profSubjectId=${row.profSubjectId}">
                                            Rate / Edit Rating
                                        </a>
                                        <a class="btn btn-outline-secondary btn-sm flex-fill"
                                           href="${pageContext.request.contextPath}/professor?profId=${row.profId}">
                                            View full profile
                                        </a>
                                    </div>
                                </c:if>

                                <c:if test="${sessionScope.role ne 'student'}">
                                    <span class="text-muted small">
                                        Only students can submit ratings.
                                    </span>
                                </c:if>
                            </div>

                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

</div>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
