<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/fragments/header.jspf" %>
<%@ include file="/WEB-INF/fragments/navigation.jspf" %>

<main class="page-container">
    <div class="page-header">
        <h1>Available Quizzes</h1>
    </div>
    <div class="quiz-grid">
        <c:forEach var="quiz" items="${quizList}">
            <div class="card quiz-card">
                <h3><c:out value="${quiz.title}" /></h3>
                <p><c:out value="${quiz.description}" /></p>
                <a href="${pageContext.request.contextPath}/quiz/start?id=${quiz.quizId}" class="btn btn-primary">Start Quiz</a>
            </div>
        </c:forEach>
    </div>
</main>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>