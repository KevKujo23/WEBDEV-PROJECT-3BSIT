<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${sessionScope.role ne 'student'}">
    <c:redirect url="/"/>
</c:if>

<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="container py-4" style="max-width:720px;">
    <h4 class="mb-3">
        <c:choose>
            <c:when test="${empty existing}">Create Rating</c:when>
            <c:otherwise>Edit Rating</c:otherwise>
        </c:choose>
    </h4>

    <c:if test="${not empty formError}">
        <div class="alert alert-danger">${formError}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/rating" class="needs-validation" novalidate>
        <input type="hidden" name="action" value="save"/>
        <input type="hidden" name="profSubjectId" value="${profSubjectId}"/>

        <c:set var="valClarity"    value="${empty existing ? 5 : existing.clarity}"/>
        <c:set var="valFairness"   value="${empty existing ? 5 : existing.fairness}"/>
        <c:set var="valEngagement" value="${empty existing ? 5 : existing.engagement}"/>
        <c:set var="valKnowledge"  value="${empty existing ? 5 : existing.knowledge}"/>

        <div class="row g-4">
            <div class="col-md-6">
                <label class="form-label">Clarity</label>
                <div class="d-flex gap-2">
                    <c:forEach var="i" begin="1" end="5">
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="clarity" id="clarity${i}" value="${i}" ${i==valClarity?'checked':''} required>
                            <label class="form-check-label" for="clarity${i}">${i}</label>
                        </div>
                    </c:forEach>
                </div>
            </div>
            <div class="col-md-6">
                <label class="form-label">Fairness</label>
                <div class="d-flex gap-2">
                    <c:forEach var="i" begin="1" end="5">
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="fairness" id="fairness${i}" value="${i}" ${i==valFairness?'checked':''} required>
                            <label class="form-check-label" for="fairness${i}">${i}</label>
                        </div>
                    </c:forEach>
                </div>
            </div>
            <div class="col-md-6">
                <label class="form-label">Engagement</label>
                <div class="d-flex gap-2">
                    <c:forEach var="i" begin="1" end="5">
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="engagement" id="engagement${i}" value="${i}" ${i==valEngagement?'checked':''} required>
                            <label class="form-check-label" for="engagement${i}">${i}</label>
                        </div>
                    </c:forEach>
                </div>
            </div>
            <div class="col-md-6">
                <label class="form-label">Knowledge</label>
                <div class="d-flex gap-2">
                    <c:forEach var="i" begin="1" end="5">
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="knowledge" id="knowledge${i}" value="${i}" ${i==valKnowledge?'checked':''} required>
                            <label class="form-check-label" for="knowledge${i}">${i}</label>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="mt-3">
            <label class="form-label">Comment (required)</label>
            <textarea name="comment" rows="4" class="form-control" required>${empty existing ? '' : existing.comment}</textarea>
        </div>

        <div class="mt-3 d-flex gap-2">
            <button class="btn btn-primary">Save</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/professors">
                Cancel
            </a>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
