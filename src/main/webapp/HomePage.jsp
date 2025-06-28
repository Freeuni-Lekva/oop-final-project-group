<%@ page import="java.util.List" %>
<%@ page import="com.quizwebsite.friendship.FriendRequest" %>
<%@ page import="User.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Home Page</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Welcome, <%= ((User) session.getAttribute("user")).getUsername() %>!</h1>

        <% List<FriendRequest> requests = (List<FriendRequest>) request.getAttribute("friendRequests"); %>
        <% if (requests != null && !requests.isEmpty()) { %>
            <div class="friend-requests">
                <h2>Pending Friend Requests</h2>
                <ul>
                    <% for (FriendRequest req : requests) { %>
                        <li>
                            <span><%= req.getRequesterUsername() %> wants to be your friend.</span>
                            <form action="<%= request.getContextPath() %>/manageFriendRequest" method="post" style="display: inline;">
                                <input type="hidden" name="requestId" value="<%= req.getRequestId() %>">
                                <input type="hidden" name="action" value="accept">
                                <button type="submit">Accept</button>
                            </form>
                            <form action="<%= request.getContextPath() %>/manageFriendRequest" method="post" style="display: inline;">
                                <input type="hidden" name="requestId" value="<%= req.getRequestId() %>">
                                <input type="hidden" name="action" value="reject">
                                <button type="submit">Reject</button>
                            </form>
                        </li>
                    <% } %>
                </ul>
            </div>
        <% } %>

        <div class="friend-list">
            <h2>My Friends</h2>
            <% List<User> friends = (List<User>) request.getAttribute("friends"); %>
            <% if (friends != null && !friends.isEmpty()) { %>
                <ul>
                    <% for (User friend : friends) { %>
                        <li>
                            <span><%= friend.getUsername() %></span>
                            <form action="<%= request.getContextPath() %>/removeFriend" method="post" style="display: inline;">
                                <input type="hidden" name="friendId" value="<%= friend.getId() %>">
                                <button type="submit">Remove</button>
                            </form>
                        </li>
                    <% } %>
                </ul>
            <% } else { %>
                <p>You have no friends yet.</p>
            <% } %>
        </div>

        <div class="user-search">
            <h2>Search for Users</h2>
            <form id="search-form">
                <input type="text" id="search-query" name="query" placeholder="Enter username">
                <button type="submit">Search</button>
            </form>
            <div id="search-results"></div>
        </div>

        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </div>

    <script>
        document.getElementById('search-form').addEventListener('submit', function(event) {
            event.preventDefault();
            const query = document.getElementById('search-query').value;
            const searchResultsContainer = document.getElementById('search-results');

            fetch('<%= request.getContextPath() %>/searchUsers?query=' + encodeURIComponent(query))
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(users => {
                    searchResultsContainer.innerHTML = ''; // Clear previous results
                    if (users.length > 0) {
                        users.forEach(user => {
                            const userElement = document.createElement('div');
                            userElement.className = 'search-result-item';

                            const usernameSpan = document.createElement('span');
                            usernameSpan.textContent = user.username;
                            userElement.appendChild(usernameSpan);

                            const form = document.createElement('form');
                            form.action = '<%= request.getContextPath() %>/sendFriendRequest';
                            form.method = 'post';
                            form.style.display = 'inline';

                            const recipientIdInput = document.createElement('input');
                            recipientIdInput.type = 'hidden';
                            recipientIdInput.name = 'recipientId';
                            recipientIdInput.value = user.userId;
                            form.appendChild(recipientIdInput);

                            const addButton = document.createElement('button');
                            addButton.type = 'submit';
                            addButton.textContent = 'Add Friend';
                            form.appendChild(addButton);

                            userElement.appendChild(form);
                            searchResultsContainer.appendChild(userElement);
                        });
                    } else {
                        searchResultsContainer.innerHTML = '<p>No users found.</p>';
                    }
                })
                .catch(error => {
                    console.error('Error searching for users:', error);
                    searchResultsContainer.innerHTML = '<p>Error searching for users. Please try again.</p>';
                });
        });
    </script>
</body>
</html>