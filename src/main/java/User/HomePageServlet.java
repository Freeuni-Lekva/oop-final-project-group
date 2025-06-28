package User;

import com.quizwebsite.friendship.FriendRequest;
import com.quizwebsite.friendship.FriendshipService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class HomePageServlet extends HttpServlet {
    private FriendshipService friendshipService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.friendshipService = (FriendshipService) getServletContext().getAttribute("friendshipService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("Welcome.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        List<FriendRequest> friendRequests = friendshipService.getPendingRequestsForUser(user.getId());
        request.setAttribute("friendRequests", friendRequests);

        List<User> friends = friendshipService.getFriendsForUser(user.getId());
        request.setAttribute("friends", friends);

        request.getRequestDispatcher("/HomePage.jsp").forward(request, response);
    }
}