package io.truthencode.games.dragonheir.model;

public enum School {
    Support(1), Wild(1), Burn(2), Frost(1), IceBlast(2), Dauntless(1), Thunderbolt(2), Rally(1), Summon(1), Toxin(1);

    private School(int season) {
        this.season = season;
    }

    private final int season;

    public int getSeason() {
        return season;
    }
}
