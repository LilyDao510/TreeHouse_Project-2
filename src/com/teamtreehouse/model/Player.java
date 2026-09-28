package com.teamtreehouse.model;

import java.io.Serializable;

// One registered player. Immutable: nothing changes after it is created.
public class Player implements Comparable<Player>, Serializable {
  private static final long serialVersionUID = 1L;

  // Field names and constructor order must stay as is: Players.load() uses them
  private final String firstName;
  private final String lastName;
  private final int heightInInches;
  private final boolean previousExperience;

  public Player(String firstName, String lastName, int heightInInches, boolean previousExperience) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.heightInInches = heightInInches;
    this.previousExperience = previousExperience;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  // Example: "Joe Smith"
  public String getFullName() {
    return firstName + " " + lastName;
  }

  public int getHeightInInches() {
    return heightInInches;
  }

  public boolean isPreviousExperience() {
    return previousExperience;
  }

  @Override
  public String toString() {
    return String.format(
            "%s | Height: %d in | Experience: %s",
            getFullName(),
            heightInInches,
            previousExperience ? "Yes" : "No"
    );
  }

  // Sort by last name, then first name
  @Override
  public int compareTo(Player other) {
    int lastNameResult = lastName.compareTo(other.lastName);
    if (lastNameResult != 0) {
      return lastNameResult;
    }
    return firstName.compareTo(other.firstName);
  }

  // Players are equal when all fields match
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Player)) return false;

    Player other = (Player) obj;
    return heightInInches == other.heightInInches
            && previousExperience == other.previousExperience
            && firstName.equals(other.firstName)
            && lastName.equals(other.lastName);
  }

  // Must match equals(): equal players give the same hash
  @Override
  public int hashCode() {
    int result = firstName.hashCode();
    result = 31 * result + lastName.hashCode();
    result = 31 * result + heightInInches;
    result = 31 * result + (previousExperience ? 1 : 0);
    return result;
  }
}
