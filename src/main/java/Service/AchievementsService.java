package Service;

import dao.AchievementsDao;
import model.Achievements;

public class AchievementsService {
    private AchievementsDao achievementsDao = new AchievementsDao();

    /**
     * Attempts to give a specific achievement to a user, if they do not already have it.
     *
     * @param userId The ID of the user.
     * @param achievementName The name of the achievement.
     * @return true if the achievement was given (i.e., user did not already have it), false otherwise.
     */
    public boolean giveAchivementIfNotGiven(int userId, String achievementName){
        Achievements achievement = achievementsDao.getAchievementByName(achievementName);
        if (!achievementsDao.userHasAchievement(userId, achievement.getId())) {
            achievementsDao.giveAchievementToUser(userId, achievement);
            return true;
        }
        return false;
    }


    /**
     * Evaluates the user's quiz activity and awards achievements accordingly.
     * Achievements awarded based on thresholds:
     * - "Amateur Author" for creating ≥ 1 quiz
     * - "Prolific Author" for creating ≥ 5 quizzes
     * - "Prodigious Author" for creating ≥ 10 quizzes
     * - "Quiz Machine" for attempting ≥ 10 quizzes
     * @param userId The ID of the user to check.
     */
    public void giveAchievements(int userId) {
        int quizzesCreated = achievementsDao.getCreatedCount(userId);
        int quizzesAttempted = achievementsDao.getAttemptCount(userId);
        if (quizzesCreated >= 1) {
            giveAchivementIfNotGiven(userId, "Amateur Author");
        }
        if (quizzesCreated >= 5) {
            giveAchivementIfNotGiven(userId, "Prolific Author");
        }
        if (quizzesCreated >= 10) {
            giveAchivementIfNotGiven(userId, "Prodigious Author");
        }
        if (quizzesAttempted >= 10) {
            giveAchivementIfNotGiven(userId, "Quiz Machine");
        }
    }

}
