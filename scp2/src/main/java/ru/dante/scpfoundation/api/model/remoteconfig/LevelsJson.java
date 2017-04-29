package ru.dante.scpfoundation.api.model.remoteconfig;

import java.util.List;

/**
 * Created by mohax on 29.04.2017.
 * <p>
 * for scp_ru
 */
public class LevelsJson {

    public List<Level> levels;

    public static class Level {

        public int id;
        public String title;
        public int score;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Level level = (Level) o;

            return id == level.id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return "Level{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", score=" + score +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LevelsJson{" +
                "levels=" + levels +
                '}';
    }
}