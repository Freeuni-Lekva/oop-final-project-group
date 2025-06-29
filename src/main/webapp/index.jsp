<%@ include file="WEB-INF/fragments/header.jspf" %>
<%@ include file="WEB-INF/fragments/navigation.jspf" %>

<main>
    <section class="hero">
        <h1>Welcome to the Ultimate Quiz Challenge!</h1>
        <p>Test your knowledge, challenge your friends, and climb the leaderboard.</p>
        <a href="${pageContext.request.contextPath}/quizzes" class="btn btn-primary">Browse Quizzes</a>
    </section>

    <div class="page-container">
        <h2>Why You'll Love Our Quizzes</h2>
        <section class="features-grid">
            <div class="card">
                <h3>Diverse Topics</h3>
                <p>From science to history to pop culture, we have a quiz for everyone.</p>
            </div>
            <div class="card">
                <h3>Compete with Friends</h3>
                <p>See how you stack up against your friends and other players.</p>
            </div>
            <div class="card">
                <h3>Instant Results</h3>
                <p>Get immediate feedback on your performance and see the correct answers.</p>
            </div>
        </section>
    </div>
</main>

<%@ include file="WEB-INF/fragments/footer.jspf" %>