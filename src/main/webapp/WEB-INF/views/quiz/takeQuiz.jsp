<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/fragments/header.jspf" %>

<main class="quiz-container card">
    <div class="quiz-header">
        <h2>Quiz in Progress</h2>
        <p>Question ${questionNumber} of ${totalQuestions}</p>
        <c:set var="progressPercentage" value="${(questionNumber * 1.0 / totalQuestions) * 100}" />
        <div class="progress-bar">
            <div class="progress-bar-inner" data-progress="${progressPercentage}"></div>
        </div>
    </div>

    <form action="${pageContext.request.contextPath}/quiz/answer" method="post">
        <input type="hidden" name="questionId" value="${currentQuestion.questionId}" />

        <section class="question-area">
            <p class="question-text">${currentQuestion.questionText}</p>
        </section>

        <section class="answers-area">
            <c:choose>
                <c:when test="${currentQuestion.questionType == 'MULTIPLE_CHOICE'}">
                    <c:forEach var="option" items="${currentQuestion.options}">
                        <div class="form-group">
                            <input type="radio" name="optionId" id="option-${option.key}" value="${option.key}" required>
                            <label for="option-${option.key}"><c:out value="${option.key}" /></label>
                        </div>
                    </c:forEach>
                </c:when>
                <c:when test="${currentQuestion.questionType == 'FILL_IN_BLANK'}">
                    <p>Please fill in the blanks below:</p>
                    <c:forEach begin="0" end="${currentQuestion.correctAnswers.size() - 1}" var="i">
                        <div class="form-group">
                            <label for="blank-${i}">Blank #${i + 1}:</label>
                            <input type="text" id="blank-${i}" name="userAnswers" class="form-input" required>
                        </div>
                    </c:forEach>
                </c:when>
                <c:when test="${currentQuestion.questionType == 'MULTI_ANSWER_UNORDERED'}">
                    <c:forEach var="option" items="${currentQuestion.options}">
                        <div class="form-group">
                            <input type="checkbox" name="userAnswers" id="option-${option.key}" value="${option.key}">
                            <label for="option-${option.key}"><c:out value="${option.key}" /></label>
                        </div>
                    </c:forEach>
                </c:when>
            </c:choose>
        </section>

        <div class="quiz-controls">
            <button type="submit" class="btn btn-primary">Next</button>
        </div>
    </form>
</main>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>