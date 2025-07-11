package models;

import java.util.Objects;


public class Achievements {
        private int id;
        private String name;
        private String description;
        private String iconUrl;

        /**
         * Constructor to initialize all fields of the Achievements class.
         *
         * @param id           Unique ID of the achievement
         * @param name         Name of the achievement
         * @param description  Description of the achievement
         * @param iconUrl      Icon URL representing the achievement visually
         */
        public Achievements(int id, String name, String description, String iconUrl) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.iconUrl = iconUrl;
        }


        /**
         * Returns the ID of the achievement.
         *
         * @return id
         */
        public int getId() {
            return id;
        }


        /**
         * Returns the name of the achievement.
         *
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the description of the achievement.
         *
         * @return description
         */
        public String getDescription() {
            return description;
        }


        /**
         * Returns the icon url of the achievement.
         *
         * @return iconUrl
         */
        public String getIconUrl() {
            return iconUrl;
        }

        /**
         * Checks Whether this achievement is equals to another object
         * Two Achievements are considered equal if all their fields match.
         *
         * @param o The object to compare with
         * @return true if objects are equal, false otherwise
         */
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Achievements that = (Achievements) o;
            return id == that.id && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(iconUrl, that.iconUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, description, iconUrl);
        }
}

