<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="container py-4" style="max-width:900px;">

    <h4 class="mb-3">My Profile</h4>
    <p class="text-muted mb-4">Basic information and your rating activity.</p>

    <!-- Basic account info -->
    <div class="row g-3 mb-4">
        <div class="col-md-6">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <h5 class="card-title mb-3">
                        <c:out value="${sessionScope.username}"/>
                        <c:if test="${empty sessionScope.username}">
                            <span class="text-muted">Unnamed User</span>
                        </c:if>
                    </h5>

                    <dl class="row mb-0">
                        <dt class="col-sm-4">Email</dt>
                        <dd class="col-sm-8">
                            <c:out value="${sessionScope.email}"/>
                        </dd>

                        <dt class="col-sm-4">Role</dt>
                        <dd class="col-sm-8">
                            <c:out value="${sessionScope.role}"/>
                        </dd>

                        <dt class="col-sm-4">User ID</dt>
                        <dd class="col-sm-8">
                            <c:out value="${sessionScope.userId}"/>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>

        <!-- Rating stats -->
        <div class="col-md-6">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <h5 class="card-title mb-3">Rating Summary</h5>

                    <c:choose>
                        <c:when test="${ratingCount gt 0}">
                            <p class="mb-2">
                                <strong>Total ratings:</strong>
                                <c:out value="${ratingCount}"/>
                            </p>

                            <p class="mb-1 small">
                                <strong>Average Overall:</strong>
                                <fmt:formatNumber value="${avgOverall}" minFractionDigits="1" maxFractionDigits="1"/>
                            </p>
                            <p class="mb-1 small">
                                <strong>Clarity:</strong>
                                <fmt:formatNumber value="${avgClarity}" minFractionDigits="1" maxFractionDigits="1"/>
                                &nbsp;|&nbsp;
                                <strong>Fairness:</strong>
                                <fmt:formatNumber value="${avgFairness}" minFractionDigits="1" maxFractionDigits="1"/>
                            </p>
                            <p class="mb-1 small">
                                <strong>Engagement:</strong>
                                <fmt:formatNumber value="${avgEngagement}" minFractionDigits="1" maxFractionDigits="1"/>
                                &nbsp;|&nbsp;
                                <strong>Knowledge:</strong>
                                <fmt:formatNumber value="${avgKnowledge}" minFractionDigits="1" maxFractionDigits="1"/>
                            </p>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted small mb-0">
                                You have not submitted any ratings yet.
                            </p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

    <!-- Recent ratings -->
    <div class="mb-4">
        <h5 class="mb-3">Recent Ratings</h5>

        <c:if test="${empty recentRatings}">
            <p class="text-muted small">No recent ratings to show.</p>
        </c:if>

        <c:if test="${not empty recentRatings}">
            <div class="table-responsive">
                <table class="table table-sm table-hover align-middle">
                    <thead>
                    <tr>
                        <th>Professor</th>
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
                    <c:forEach var="r" items="${recentRatings}">
                        <tr>
                            <td><c:out value="${r.professorName}"/></td>
                            <td><c:out value="${r.subjectCode}"/></td>
                            <td><c:out value="${r.clarity}"/></td>
                            <td><c:out value="${r.fairness}"/></td>
                            <td><c:out value="${r.engagement}"/></td>
                            <td><c:out value="${r.knowledge}"/></td>
                            <td>
                                <fmt:formatNumber
                                        value="${(r.clarity + r.fairness + r.engagement + r.knowledge) / 4.0}"
                                        minFractionDigits="1" maxFractionDigits="1"/>
                            </td>
                            <td class="text-truncate" style="max-width:260px;">
                                <c:out value="${r.comment}"/>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>

    <div class="mb-3">
        <a class="btn btn-outline-primary btn-sm"
           href="${pageContext.request.contextPath}/my-ratings">
            View all my ratings
        </a>
        <a class="btn btn-outline-secondary btn-sm"
           href="${pageContext.request.contextPath}/professors">
            Browse professors
        </a>
    </div>

</div>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
