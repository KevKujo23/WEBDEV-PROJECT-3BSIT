<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="container py-4">

    <h4 class="mb-3">My Ratings</h4>
    <p class="text-muted mb-3">
        These are the ratings you have submitted for professors.
    </p>

    <c:if test="${empty ratings}">
        <div class="alert alert-info">
            You have not submitted any ratings yet.
        </div>
    </c:if>

    <c:if test="${not empty ratings}">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
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
                <c:forEach var="r" items="${ratings}">
                    <tr>
                        <td>
                            <c:out value="${r.professorName}"/>
                        </td>
                        <td>
                            <c:out value="${r.subjectCode}"/>
                        </td>
                        <td><c:out value="${r.clarity}"/></td>
                        <td><c:out value="${r.fairness}"/></td>
                        <td><c:out value="${r.engagement}"/></td>
                        <td><c:out value="${r.knowledge}"/></td>
                        <td>
                            <fmt:formatNumber
                                    value="${(r.clarity + r.fairness + r.engagement + r.knowledge) / 4.0}"
                                    minFractionDigits="1"
                                    maxFractionDigits="1"/>
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

    <a href="${pageContext.request.contextPath}/professors"
       class="btn btn-outline-secondary btn-sm mt-3">
        ← Back to Professors
    </a>

</div>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
