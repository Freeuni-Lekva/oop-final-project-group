<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ include file="/WEB-INF/fragments/navigation.jspf" %>

<main class="page-container">
    <div class="page-header">
        <h1>Create a New Quiz</h1>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/create-quiz">
        <div >
            <label for="title">Quiz Title:</label>
            <input type="text" name="title" id="title" required class="form-control" />
        </div>

        <div >
            <label for="description">Description:</label>
            <textarea name="description" id="description" class="form-control"></textarea>
        </div>


        <button type="button" id="add-question-btn">Add Question</button>
        <button type="submit">Create Quiz</button>

        <!-- Placeholder for dynamic questions -->
        <div id="questions-container">
            <!-- JavaScript will inject question blocks here -->

        </div>

    </form>
</main>

<script src="${pageContext.request.contextPath}/js/createQuiz.js"></script>