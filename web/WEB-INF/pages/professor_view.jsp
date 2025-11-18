<%-- 
    Document   : professor_view
    Created on : Nov 17, 2025, 10:00:08 PM
    Author     : Kevin
--%>

<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/includes/header.jsp"/>

<c:set var="ov" value="${overview}" />

<div class="container py-4">

    <!-- Header / overview -->
    <div class="mb-4">
        <h3 class="mb-1">
            <c:out value="${ov.professorName}"/>
        </h3>
        <div class="text-muted">
            <c:out value="${ov.deptCode}"/> — <c:out value="${ov.deptName}"/>
        </div>

        <!-- Overall stats (only if there is at least 1 rating) -->
        <c:if test="${not empty ov.ratingCount and ov.ratingCount gt 0}">
            <div class="mt-3 small">
                <div>
                    <strong>
                        Overall Avg:
                        <fmt:formatNumber value="${ov.avgOverall}" minFractionDigits="1" maxFractionDigits="1"/>
                    </strong>
                    <span class="text-muted">
                        (${ov.ratingCount} rating<c:if test="${ov.ratingCount ne 1}">s</c:if>)
                    </span>
                </div>
                <div class="text-muted">
                    Lowest:
                    <fmt:formatNumber value="${ov.minOverall}" minFractionDigits="1" maxFractionDigits="1"/>
                    &nbsp;•&nbsp;
                    Highest:
                    <fmt:formatNumber value="${ov.maxOverall}" minFractionDigits="1" maxFractionDigits="1"/>
                </div>
                <div class="mt-2">
                    <span class="badge bg-light text-dark me-1">
                        Clarity:
                        <fmt:formatNumber value="${ov.avgClarity}" minFractionDigits="1" maxFractionDigits="1"/>
                    </span>
                    <span class="badge bg-light text-dark me-1">
                        Fairness:
                        <fmt:formatNumber value="${ov.avgFairness}" minFractionDigits="1" maxFractionDigits="1"/>
                    </span>
                    <span class="badge bg-light text-dark me-1">
                        Engagement:
                        <fmt:formatNumber value="${ov.avgEngagement}" minFractionDigits="1" maxFractionDigits="1"/>
                    </span>
                    <span class="badge bg-light text-dark">
                        Knowledge:
                        <fmt:formatNumber value="${ov.avgKnowledge}" minFractionDigits="1" maxFractionDigits="1"/>
                    </span>
                </div>
                <c:if test="${not empty ov.lastRatingDate}">
                    <div class="text-muted mt-2">
                        Last Rating:
                        <fmt:formatDate value="${ov.lastRatingDate}" pattern="yyyy-MM-dd HH:mm"/>
                    </div>
                </c:if>
            </div>
        </c:if>

        <!-- Message when there are zero ratings overall -->
        <c:if test="${empty ov.ratingCount or ov.ratingCount == 0}">
            <div class="mt-3 text-muted">
                This professor does not have any ratings yet.
            </div>
        </c:if>
    </div>

    <!-- Subjects taught with stats -->
    <div class="mb-4">
        <h5>Subjects taught</h5>
        <c:if test="${empty subjectStats}">
            <p class="text-muted small">No subjects assigned.</p>
        </c:if>
        <c:if test="${not empty subjectStats}">
            <div class="table-responsive">
                <table class="table table-sm align-middle">
                    <thead>
                    <tr>
                        <th>Subject</th>
                        <th>Avg</th>
                        <th>Lowest</th>
                        <th>Highest</th>
                        <th>Ratings</th>
                        <th>Clarity</th>
                        <th>Fairness</th>
                        <th>Engagement</th>
                        <th>Knowledge</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="s" items="${subjectStats}">
                        <tr>
                            <td>
                                <strong>${s.subjectCode}</strong>
                                <div class="text-muted small">${s.subjectName}</div>
                            </td>

                            <!-- Avg overall -->
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${s.ratingCount gt 0}">
                                        <fmt:formatNumber value="${s.avgOverall}" minFractionDigits="1" maxFractionDigits="1"/>
                                    </c:when>
                                    <c:otherwise>–</c:otherwise>
                                </c:choose>
                            </td>

                            <!-- Lowest -->
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${s.ratingCount gt 0}">
                                        <fmt:formatNumber value="${s.minOverall}" minFractionDigits="1" maxFractionDigits="1"/>
                                    </c:when>
                                    <c:otherwise>–</c:otherwise>
                                </c:choose>
                            </td>

                            <!-- Highest -->
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${s.ratingCount gt 0}">
                                        <fmt:formatNumber value="${s.maxOverall}" minFractionDigits="1" maxFractionDigits="1"/>
                                    </c:when>
                                    <c:otherwise>–</c:otherwise>
                                </c:choose>
                            </td>

                            <!-- Ratings count -->
                            <td class="text-center">
                                ${s.ratingCount}
                            </td>

                            <!-- Clarity -->
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${s.ratingCount gt 0}">
                                        <fmt:formatNumber value="${s.avgClarity}" minFractionDigits="1" maxFractionDigits="1"/>
                                    </c:when>
                                    <c:otherwise>–</c:otherwise>
                                </c:choose>
                            </td>

                            <!-- Fairness -->
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${s.ratingCount gt 0}">
                                        <fmt:formatNumber value="${s.avgFairness}" minFractionDigits="1" maxFractionDigits="1"/>
                                    </c:when>
                                    <c:otherwise>–</c:otherwise>
                                </c:choose>
                            </td>

                            <!-- Engagement -->
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${s.ratingCount gt 0}">
                                        <fmt:formatNumber value="${s.avgEngagement}" minFractionDigits="1" maxFractionDigits="1"/>
                                    </c:when>
                                    <c:otherwise>–</c:otherwise>
                                </c:choose>
                            </td>

                            <!-- Knowledge -->
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${s.ratingCount gt 0}">
                                        <fmt:formatNumber value="${s.avgKnowledge}" minFractionDigits="1" maxFractionDigits="1"/>
                                    </c:when>
                                    <c:otherwise>–</c:otherwise>
                                </c:choose>
                            </td>

                            <!-- Rate button -->
                            <td class="text-end">
                                <c:if test="${sessionScope.role eq 'student'}">
                                    <a class="btn btn-outline-primary btn-sm"
                                       href="${pageContext.request.contextPath}/rating?action=form&profSubjectId=${s.profSubjectId}">
                                        Rate
                                    </a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>

    <!-- Ratings & comments list -->
    <div class="mb-4">
        <h5>Ratings & Comments</h5>
        <c:if test="${empty ratings}">
            <p class="text-muted small">No ratings yet.</p>
        </c:if>
        <c:if test="${not empty ratings}">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                    <tr>
                        <th>Date</th>
                        <th>Subject</th>
                        <th>Clarity</th>
                        <th>Fairness</th>
                        <th>Engagement</th>
                        <th>Knowledge</th>
                        <th>Overall</th>
                        <th>Comment</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="r" items="${ratings}">
                        <tr>
                            <td>
                                <fmt:formatDate value="${r.dateSubmitted}" pattern="yyyy-MM-dd HH:mm"/>
                            </td>
                            <td>
                                <strong>${r.subjectCode}</strong>
                                <div class="text-muted small">${r.subjectName}</div>
                            </td>
                            <td>${r.clarity}</td>
                            <td>${r.fairness}</td>
                            <td>${r.engagement}</td>
                            <td>${r.knowledge}</td>
                            <td>
                                <fmt:formatNumber
                                        value="${(r.clarity + r.fairness + r.engagement + r.knowledge) / 4.0}"
                                        minFractionDigits="1" maxFractionDigits="1"/>
                            </td>
                            <td class="text-truncate" style="max-width:320px;">
                                <c:out value="${r.comment}"/>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>

    <a href="${pageContext.request.contextPath}/professors" class="btn btn-outline-secondary btn-sm">
        ← Back to Professors
    </a>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
