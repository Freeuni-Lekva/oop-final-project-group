<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/fragments/header.jspf" %>

<main class="page-container">
    <div class="page-header">
        <h1>Quiz Results</h1>
    </div>

    <section class="results-summary card">
        <h2>Quiz Complete!</h2>
        <p>Your final score is ${finalScore} out of ${totalQuestions}.</p>
    </section>

    <section class="results-breakdown">
        <h3>Detailed Breakdown</h3>
        <p><em>(Detailed results breakdown will be implemented here.)</em></p>
    </section>

    <a href="${pageContext.request.contextPath}/quizzes" class="btn btn-primary">Back to Quiz List</a>
</main>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>