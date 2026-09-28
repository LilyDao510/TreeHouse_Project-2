import com.teamtreehouse.model.Player;
import com.teamtreehouse.model.Players;
import com.teamtreehouse.model.Team;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

//App for the soccer league organizer: build teams, assign players, print reports
public class LeagueManager {

  // Players not on any team yet (TreeSet = sorted alphabetically)
  private final Set<Player> availablePlayers;

  // All teams (sorted by team name)
  private final Set<Team> teams;

  // Reads the organizer's typing
  private final BufferedReader reader;

  private final Map<String, String> menu;

  public LeagueManager(Player[] masterPlayers) {
    this.availablePlayers = new TreeSet<>(Arrays.asList(masterPlayers));
    this.teams = new TreeSet<>();
    this.reader = new BufferedReader(new InputStreamReader(System.in));

    this.menu = new LinkedHashMap<>();
    menu.put("create", "Create a new team");
    menu.put("add", "Add a player to a team");
    menu.put("remove", "Remove a player from a team");
    menu.put("roster", "Print a team roster");
    menu.put("height", "View a team height report");
    menu.put("balance", "View the league balance report");
    menu.put("quit", "Exit the program");
  }

  public static void main(String[] args) {
    Player[] masterPlayers = Players.load();
    System.out.printf("There are %d registered players this season.%n", masterPlayers.length);

    LeagueManager leagueManager = new LeagueManager(masterPlayers);
    leagueManager.run();
  }

  // Shows the menu again and again until the organizer types "quit"
  public void run() {
    String choice = "";
    do {
      try {
        choice = promptAction();
        switch (choice) {
          case "create":
            createTeam();
            break;
          case "add":
            addPlayerToTeam();
            break;
          case "remove":
            removePlayerFromTeam();
            break;
          case "roster":
            printTeamRoster();
            break;
          case "height":
            printHeightReport();
            break;
          case "balance":
            printLeagueBalanceReport();
            break;
          case "quit":
            System.out.println("Thanks for organizing with LeagueManager!");
            break;
          default:
            System.out.printf("Unknown choice '%s'. Try again.%n", choice);
        }
      } catch (IOException ioe) {
        System.out.println("There was a problem reading your input.");
        ioe.printStackTrace();
      }
    } while (!choice.equals("quit"));
  }

  // Prints the menu and returns the command typed
  private String promptAction() throws IOException {
    System.out.printf("%n%d team(s) so far | %d player(s) available%n",
            teams.size(), availablePlayers.size());

    for (Map.Entry<String, String> option : menu.entrySet()) {
      System.out.printf("%s - %s%n", option.getKey(), option.getValue());
    }

    System.out.print("What do you want to do: ");
    String choice = reader.readLine();
    return choice.trim().toLowerCase();
  }

  // Asks until the answer is not blank
  private String promptForRequiredText(String prompt) throws IOException {
    String value;
    do {
      System.out.print(prompt);
      value = reader.readLine().trim();
      if (value.isEmpty()) {
        System.out.println("This can't be blank - please try again.");
      }
    } while (value.isEmpty());
    return value;
  }

  // Asks for a number from 1 to numberOfOptions, returns it as a list index
  private int promptForIndex(int numberOfOptions) throws IOException {
    while (true) {
      System.out.printf("Enter a number between 1 and %d: ", numberOfOptions);
      String input = reader.readLine().trim();
      try {
        int choice = Integer.parseInt(input);
        if (choice >= 1 && choice <= numberOfOptions) {
          return choice - 1;
        }
        System.out.println("That number is outside the list - try again.");
      } catch (NumberFormatException nfe) {
        System.out.println("Please enter a valid number.");
      }
    }
  }

  // Lists teams alphabetically and returns the one picked (null if no teams)
  private Team chooseTeam() throws IOException {
    if (teams.isEmpty()) {
      System.out.println("No teams have been created yet.");
      return null;
    }

    List<Team> teamChoices = new ArrayList<>(teams);

    System.out.println("\nChoose a team:");
    for (int i = 0; i < teamChoices.size(); i++) {
      System.out.printf("%d. %s%n", i + 1, teamChoices.get(i));
    }

    int selectedIndex = promptForIndex(teamChoices.size());
    return teamChoices.get(selectedIndex);
  }

  // Lists the given players alphabetically with their stats and returns the one picked
  private Player choosePlayer(Set<Player> players, String heading) throws IOException {
    if (players.isEmpty()) {
      System.out.println("There are no players to choose from.");
      return null;
    }

    // TreeSet sorts players by last name, then first name
    List<Player> playerChoices = new ArrayList<>(new TreeSet<>(players));

    System.out.println("\n" + heading);
    for (int i = 0; i < playerChoices.size(); i++) {
      System.out.printf("%d. %s%n", i + 1, playerChoices.get(i));
    }

    int selectedIndex = promptForIndex(playerChoices.size());
    return playerChoices.get(selectedIndex);
  }

  // Asks for a team name and coach name, then saves the new team
  private void createTeam() throws IOException {
    String teamName = promptForRequiredText("Enter the team name: ");
    String coachName = promptForRequiredText("Enter the coach name: ");

    Team newTeam = new Team(teamName, coachName);
    boolean wasAdded = teams.add(newTeam); // false if the name already exists

    if (wasAdded) {
      System.out.printf("Team '%s' was created with coach %s.%n", teamName, coachName);
    } else {
      System.out.printf("A team named '%s' already exists.%n", teamName);
    }
  }

  // Moves one available player onto a team (max 11 per team)
  private void addPlayerToTeam() throws IOException {
    Team chosenTeam = chooseTeam();
    if (chosenTeam == null) {
      return;
    }

    if (chosenTeam.isFull()) {
      System.out.printf("'%s' already has the max of %d players.%n",
              chosenTeam.getTeamName(), Team.MAX_PLAYERS);
      return;
    }

    Player chosenPlayer = choosePlayer(availablePlayers, "Choose an available player:");
    if (chosenPlayer == null) {
      return;
    }

    if (chosenTeam.addPlayer(chosenPlayer)) {
      availablePlayers.remove(chosenPlayer);
      System.out.printf("%s was added to %s.%n", chosenPlayer.getFullName(), chosenTeam.getTeamName());
    } else {
      System.out.println("That player could not be added.");
    }
  }

  // Takes one player off a team and puts them back in the available pool
  private void removePlayerFromTeam() throws IOException {
    Team chosenTeam = chooseTeam();
    if (chosenTeam == null) {
      return;
    }

    if (chosenTeam.getPlayers().isEmpty()) {
      System.out.printf("'%s' doesn't have any players yet.%n", chosenTeam.getTeamName());
      return;
    }

    Player chosenPlayer = choosePlayer(chosenTeam.getPlayers(),
            "Choose a player to remove from " + chosenTeam.getTeamName() + ":");
    if (chosenPlayer == null) {
      return;
    }

    if (chosenTeam.removePlayer(chosenPlayer)) {
      availablePlayers.add(chosenPlayer);
      System.out.printf("%s was removed from %s.%n", chosenPlayer.getFullName(), chosenTeam.getTeamName());
    } else {
      System.out.println("That player could not be removed.");
    }
  }

  // Prints a team's players alphabetically with their stats
  private void printTeamRoster() throws IOException {
    Team chosenTeam = chooseTeam();
    if (chosenTeam == null) {
      return;
    }

    System.out.printf("%nRoster for %s (Coach %s):%n", chosenTeam.getTeamName(), chosenTeam.getCoachName());

    if (chosenTeam.getPlayers().isEmpty()) {
      System.out.println("This team doesn't have any players yet.");
      return;
    }

    Set<Player> sortedRoster = new TreeSet<>(chosenTeam.getPlayers());

    for (Player player : sortedRoster) {
      System.out.println("- " + player);
    }
  }

  // Prints a team's players grouped by height
  private void printHeightReport() throws IOException {
    Team chosenTeam = chooseTeam();
    if (chosenTeam == null) {
      return;
    }

    if (chosenTeam.getPlayers().isEmpty()) {
      System.out.printf("'%s' doesn't have any players yet.%n", chosenTeam.getTeamName());
      return;
    }

    // height range -> players in that range
    Map<String, List<Player>> heightRanges = new LinkedHashMap<>();
    heightRanges.put("35-40 inches", new ArrayList<>());
    heightRanges.put("41-46 inches", new ArrayList<>());
    heightRanges.put("47-50 inches", new ArrayList<>());

    Set<Player> sortedRoster = new TreeSet<>(chosenTeam.getPlayers());

    // Put each player in the range that fits their height
    for (Player player : sortedRoster) {
      int height = player.getHeightInInches();
      if (height <= 40) {
        heightRanges.get("35-40 inches").add(player);
      } else if (height <= 46) {
        heightRanges.get("41-46 inches").add(player);
      } else {
        heightRanges.get("47-50 inches").add(player);
      }
    }

    System.out.printf("%nHeight report for %s:%n", chosenTeam.getTeamName());
    for (Map.Entry<String, List<Player>> range : heightRanges.entrySet()) {
      System.out.printf("%n%s - %d player(s)%n", range.getKey(), range.getValue().size());
      for (Player player : range.getValue()) {
        System.out.println("  - " + player);
      }
    }
  }

  // Counts experienced / inexperienced players for each team
  private Map<Team, Map<String, Integer>> buildLeagueBalanceReport() {
    Map<Team, Map<String, Integer>> balanceReport = new LinkedHashMap<>();

    for (Team team : teams) {
      int experiencedCount = 0;
      int inexperiencedCount = 0;

      for (Player player : team.getPlayers()) {
        if (player.isPreviousExperience()) {
          experiencedCount++;
        } else {
          inexperiencedCount++;
        }
      }

      Map<String, Integer> teamCounts = new LinkedHashMap<>();
      teamCounts.put("Experienced", experiencedCount);
      teamCounts.put("Inexperienced", inexperiencedCount);
      balanceReport.put(team, teamCounts);
    }

    return balanceReport;
  }

  // Prints the experience counts for every team
  private void printLeagueBalanceReport() {
    if (teams.isEmpty()) {
      System.out.println("No teams have been created yet.");
      return;
    }

    Map<Team, Map<String, Integer>> balanceReport = buildLeagueBalanceReport();

    System.out.println("\nLeague Balance Report:");
    for (Map.Entry<Team, Map<String, Integer>> entry : balanceReport.entrySet()) {
      Team team = entry.getKey();
      Map<String, Integer> counts = entry.getValue();

      System.out.printf("%s | Experienced: %d | Inexperienced: %d%n",
              team.getTeamName(), counts.get("Experienced"), counts.get("Inexperienced"));
    }
  }
}