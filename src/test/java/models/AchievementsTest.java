package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AchievementsTest {
    @Test
    void gettersTest() {
        Achievements achievement = new Achievements(1, "Amateur Author", "Created a quiz", "icon.png");

        assertEquals(1, achievement.getId());
        assertEquals("Amateur Author", achievement.getName());
        assertEquals("Created a quiz", achievement.getDescription());
        assertEquals("icon.png", achievement.getIconUrl());
    }

    @Test
    void equalsTest() {
        Achievements achievement1 = new Achievements(0, "1", "2", "3");
        Achievements achievement2 = new Achievements(0, "1", "2", "3");
        assertEquals(achievement1, achievement2);

        Achievements achievement3 = new Achievements(1, "2", "2", "3");
        assertNotEquals(achievement1, achievement3);

    }
}