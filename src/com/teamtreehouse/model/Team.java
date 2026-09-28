package com.teamtreehouse.model;

import java.util.HashSet;
import java.util.Set;

// A team: name, coach, and up to 11 players.
// players is a Set, so the same player can never be added twice.
public class Team implements Comparable<Team> {

    // Most players allowed on one team
    public static final int MAX_PLAYERS = 11;

    private final String teamName;
    private final String coachName;
    private final Set<Player> players;

    public Team(String teamName, String coachName) {
        this.teamName = teamName;
        this.coachName = coachName;
        this.players = new HashSet<>();
    }

    public String getTeamName() {
        return teamName;
    }

    public String getCoachName() {
        return coachName;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean isFull() {
        return players.size() >= MAX_PLAYERS;
    }

    // Returns false if the team is full or the player is already on it
    public boolean addPlayer(Player player) {
        if (isFull()) {
            return false;
        }
        return players.add(player);
    }

    // Returns true if the player was on the team
    public boolean removePlayer(Player player) {
        return players.remove(player);
    }

    @Override
    public String toString() {
        return String.format("%s (Coach: %s) - %d/%d players",
                teamName, coachName, getPlayerCount(), MAX_PLAYERS);
    }

    // Sort teams alphabetically by name (ignoring case)
    @Override
    public int compareTo(Team other) {
        return teamName.compareToIgnoreCase(other.teamName);
    }
}