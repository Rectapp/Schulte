package com.rectapp.schulte.model;

public class Game {
    public int level;
    public long time;
    public long date;

    public Game(int level, long time, long date) {
        this.level = level;
        this.time = time;
        this.date = date;
    }

    public Game(int level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        return level == game.level;
    }

    @Override
    public int hashCode() {
        return level;
    }
}
