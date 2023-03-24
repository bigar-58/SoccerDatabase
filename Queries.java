import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Queries {
    SqlConnection connection;
    
    public Queries(SqlConnection connection) {
        this.connection = connection;
    }

    public void listMatchesByCountry() throws SQLException {
        Scanner keyboard = new Scanner(System.in);
        String countryName;

        System.out.print("Please enter the country name: ");
        countryName = keyboard.nextLine();

        String sql = "SELECT t1.ASSOCIATIONNAME as team_1, t2.ASSOCIATIONNAME as team_2, m.DATE, m.ROUND, COUNT(T.TID) as tickets, COUNT(g1.TEAM) as goals_team_1, COUNT(g2.TEAM) as goals_team_2\n" +
                "FROM TEAM t1\n" +
                "JOIN PARTICIPATE p1 ON p1.ASSOCIATIONURL = (SELECT ASSOCIATIONURL\n" +
                "                                            FROM TEAM\n" +
                "                                            WHERE ASSOCIATIONNAME = '"+ countryName +"')\n" +
                "JOIN MATCH M on M.MATCHID = p1.MATCHID\n" +
                "JOIN PARTICIPATE p2 ON p2.MATCHID = m.MATCHID AND p2.ASSOCIATIONURL != p1.ASSOCIATIONURL\n" +
                "JOIN TEAM t2 on t2.ASSOCIATIONURL = p2.ASSOCIATIONURL\n" +
                "LEFT JOIN TICKET T on M.MATCHID = T.MATCHID\n" +
                "LEFT JOIN GOAL g1 on M.MATCHID = g1.MATCHID AND g1.TEAM = t1.ASSOCIATIONURL\n" +
                "LEFT JOIN GOAL g2 on M.MATCHID = g2.MATCHID AND g2.TEAM = t2.ASSOCIATIONURL\n" +
                "WHERE t1.ASSOCIATIONURL = p1.ASSOCIATIONURL\n" +
                "GROUP BY t1.ASSOCIATIONNAME, t2.ASSOCIATIONNAME, m.DATE, m.ROUND, T.tid, g1.TEAM, g2.TEAM;\n";

        try (ResultSet result = connection.executeQuery(sql)) {

            while (result.next()) {
                if(result.getDate("date").before(new Date(System.currentTimeMillis()))) {
                    System.out.println(result.getString("team_1") + " " + result.getString("team_2") + " " + result.getString("date") + " " + result.getString("round") + " " + result.getString("goals_team_1") + " " + result.getString("goals_team_2") + " " + result.getString("tickets"));
                } else {
                    System.out.println(result.getString("team_1") + " " + result.getString("team_2") + " " + result.getString("date") + " " + result.getString("round") + " NULL NULL " +  result.getString("tickets"));
                }
                
            }
        }

        String input = null;
        while(input == null || input.length() != 1) {
            System.out.print("\nEnter [A] to find matches of another country, [P] to go to the previous menu:");

            input = keyboard.nextLine();

            if(input.charAt(0) == 'A')
                listMatchesByCountry();

            if(input.charAt(0) == 'P')
                return;
        }
    }

    public void insertInitialPlayerInfo() throws SQLException {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Matches:");

        Map<Object,ArrayList<String>> matches = new HashMap<>();

        String sql = "SELECT p1.MATCHID as matchId, t1.ASSOCIATIONNAME as team_1, t2.ASSOCIATIONNAME as team_2, M.DATE, M.ROUND\n" +
                "FROM PARTICIPATE p1\n" +
                "JOIN PARTICIPATE p2 on p1.MATCHID = p2.MATCHID AND p1.ASSOCIATIONURL < p2.ASSOCIATIONURL\n" +
                "JOIN MATCH M on M.MATCHID = p1.MATCHID\n" +
                "JOIN TEAM t1 on p1.ASSOCIATIONURL = t1.ASSOCIATIONURL\n" +
                "JOIN TEAM t2 on p2.ASSOCIATIONURL = t2.ASSOCIATIONURL\n" +
                "WHERE DAY(m.date) >= DAY(CURRENT_DATE) AND DAY(M.DATE) <= DAY(CURRENT_DATE) + 3\n" +
                "GROUP BY p1.MATCHID, t1.ASSOCIATIONNAME, t2.ASSOCIATIONNAME, M.DATE, M.ROUND\n";

        try (ResultSet result = connection.executeQuery(sql)) {
            while (result.next()) {
                ArrayList<String> match = new ArrayList<>();
                match.add(result.getString("team_1"));
                match.add(result.getString("team_2"));
                matches.put(Integer.parseInt(result.getString("matchId")), match);
                System.out.println("\t" + result.getString("matchId") + " " + result.getString("team_1") + " " + result.getString("team_2") + " " + result.getString("date") + " " + result.getString("round"));
            }
        }

        String input = null;

        int matchId = -1;
        while(input == null) {
            System.out.print("\nYou can enter [P] to go to the previous menu");

            System.out.print("\nPlease enter the match ID of the match you wish to select: ");

            input = keyboard.nextLine();

            if(input.charAt(0) == 'P')
                return;

            matchId = Integer.parseInt(input);
        }

        if(matchId == -1) return;
        
        System.out.println("The following countries are participating in the match: \n");
        for(Map.Entry<Object, ArrayList<String>> entry : matches.entrySet()) {
            if(entry.getKey().equals(matchId)) {
                System.out.println("\t1. " + entry.getValue().get(0));
                System.out.println("\t2. " + entry.getValue().get(1));
            }
        }


        String country = null;
        String countryName = null;
        while(country == null) {
            System.out.print("\nEnter the number of the country you want to select or [P] to go to the previous menu: ");
            country = keyboard.nextLine();
            if(country.charAt(0) == 'P')
                return;

            if(!country.equals("1") && !country.equals("2")) {
                country = null;
            } else {
                if(country.equals("1")) {
                    countryName = matches.get(matchId).get(0);
                } else {
                    countryName = matches.get(matchId).get(1);
                }
            }
        }

        String input1 = null;
        while(true) {
            String sql1 = "SELECT name, shirtnumber, detailedposition, minentered, minexited, yellowcards, receivedred\n" +
                    "FROM PLAYS\n" +
                    "JOIN TEAMMEMBER T on PLAYS.UUID = T.UUID\n" +
                    "WHERE T.ASSOCIATIONURL = (SELECT TEAM.ASSOCIATIONURL FROM TEAM WHERE ASSOCIATIONNAME = '"+ countryName +"') AND PLAYS.MATCHID = "+ matchId +";";

            List<String> playersPlaying = new ArrayList<>();
            try (ResultSet result = connection.executeQuery(sql1)) {
                System.out.println("The following players from "+countryName+" are already entered for match "+matchId+": \n");
                while (result.next()) {
                    playersPlaying.add(result.getString("name"));

                    if(result.getString("minexited") != null) {
                        System.out.println(result.getString("name") + " " + result.getString("shirtnumber") + " " + result.getString("detailedposition") + " " + " from minute " + result.getString("minentered") + " to minute " + result.getString("minexited") + " yellow: " + result.getString("yellowcards") + " red: " + (result.getBoolean("receivedred") ? "1" : "0"));
                    } else {
                        System.out.println(result.getString("name") + " " + result.getString("shirtnumber") + " " + result.getString("detailedposition") + " " + " from minute " + result.getString("minentered") + " to minute NULL yellow: " + result.getString("yellowcards") + " red: " + (result.getBoolean("receivedred") ? "1" : "0"));
                    }
                }
            }

            String sql2 = "SELECT name, shirtnumber, genposition\n" +
                    "FROM TEAMMEMBER T\n" +
                    "WHERE T.ASSOCIATIONURL = (SELECT TEAM.ASSOCIATIONURL FROM TEAM WHERE ASSOCIATIONNAME = '"+ countryName +"') AND T.UUID NOT IN (SELECT UUID FROM PLAYS WHERE MATCHID = "+ matchId +");";

            List<String> playersNotPlaying = new ArrayList<>();
            int j = 1;
            try (ResultSet result = connection.executeQuery(sql2)) {
                System.out.println("\nPossible players from " + countryName + " not yet selected:");
                while (result.next()) {
                    playersNotPlaying.add(result.getString("name"));
                    System.out.println(j + ". " + result.getString("name") + " " + result.getString("shirtnumber") + " " + result.getString("genposition"));
                    j++;
                }
            }


            if(playersNotPlaying.size() == 0 || playersPlaying.size() == 11) {
                char c = '0';
                while(c != 'P') {
                    System.out.print("\nEnter [P] to go to the previous menu: ");
                    c = keyboard.nextLine().charAt(0);

                    if(c == 'P')
                        return;

                    System.out.print("\nYou can enter a maximum of 11 players.");
                }
            } else {
                System.out.print("\nEnter the number of the player you want to insert or [P] to go to the previous menu: ");
            }


            input1 = keyboard.nextLine();

            if(input1.charAt(0) == 'P')
                return;

            int playerNumber = Integer.parseInt(input1);

            if(playerNumber < 1 || playerNumber > playersNotPlaying.size()) {
                System.out.println("Invalid player number");
                input1 = null;
                continue;
            }

            String player = playersNotPlaying.get(playerNumber - 1);

            // Ask for the position

            String position = null;
            while(position == null) {
                System.out.print("\nPlease enter the position of the player: ");

                position = keyboard.nextLine();
            }

            String sql3 = "INSERT INTO PLAYS (UUID, MATCHID, MINENTERED, MINEXITED, DETAILEDPOSITION, YELLOWCARDS, RECEIVEDRED)\n" +
                    "VALUES ((SELECT UUID FROM TEAMMEMBER WHERE NAME = '"+ player +"'), "+ matchId +", 0, 0, '"+ position +"', 0, false);";

            connection.executeUpdate(sql3);
        }
    }

    public void insertGoal() throws SQLException {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Matches:");

        String sql = "SELECT p1.MATCHID as matchId, t1.ASSOCIATIONNAME as team_1, t2.ASSOCIATIONNAME as team_2, M.DATE, M.ROUND\n" +
                "FROM PARTICIPATE p1\n" +
                "JOIN PARTICIPATE p2 on p1.MATCHID = p2.MATCHID AND p1.ASSOCIATIONURL < p2.ASSOCIATIONURL\n" +
                "JOIN MATCH M on M.MATCHID = p1.MATCHID\n" +
                "JOIN TEAM t1 on p1.ASSOCIATIONURL = t1.ASSOCIATIONURL\n" +
                "JOIN TEAM t2 on p2.ASSOCIATIONURL = t2.ASSOCIATIONURL\n" +
                "WHERE DAY(m.date) >= DAY(CURRENT_DATE) AND DAY(M.DATE) <= DAY(CURRENT_DATE) + 3\n" +
                "GROUP BY p1.MATCHID, t1.ASSOCIATIONNAME, t2.ASSOCIATIONNAME, M.DATE, M.ROUND\n";

        Map<Object,ArrayList<String>> matches = new HashMap<>();

        try (ResultSet result = connection.executeQuery(sql)) {
            while (result.next()) {
                ArrayList<String> match = new ArrayList<>();
                match.add(result.getString("team_1"));
                match.add(result.getString("team_2"));
                matches.put(Integer.parseInt(result.getString("matchId")), match);
                System.out.println("\t" + result.getString("matchId") + " " + result.getString("team_1") + " " + result.getString("team_2") + " " + result.getString("date") + " " + result.getString("round"));
            }
        }

        String input = null;

        int matchId = -1;

        while(input == null) {
            System.out.print("\nYou can enter [P] to go to the previous menu");

            System.out.print("\nPlease enter the match ID of the match you wish to select: ");

            input = keyboard.nextLine();

            if(input.charAt(0) == 'P')
                return;

            matchId = Integer.parseInt(input);
        }

        if(matchId == -1) return;


        System.out.println("The following countries are participating in the match: \n");
        for(Map.Entry<Object, ArrayList<String>> entry : matches.entrySet()) {
            if(entry.getKey().equals(matchId)) {
                System.out.println("\t1. " + entry.getValue().get(0));
                System.out.println("\t2. " + entry.getValue().get(1));
            }
        }


        String country = null;
        String countryName = null;
        while(country == null) {
            System.out.print("\nEnter the number of the country you want to select or [P] to go to the previous menu: ");
            country = keyboard.nextLine();
            if(country.charAt(0) == 'P')
                return;

            if(!country.equals("1") && !country.equals("2")) {
                country = null;
            } else {
                if(country.equals("1")) {
                    countryName = matches.get(matchId).get(0);
                } else {
                    countryName = matches.get(matchId).get(1);
                }
            }
        }
        

        String input1 = null;

        while(true) {
            String sql1 = "SELECT name, shirtnumber, detailedposition, minentered, minexited\n" +
                    "FROM PLAYS\n" +
                    "JOIN TEAMMEMBER T on PLAYS.UUID = T.UUID\n" +
                    "WHERE T.ASSOCIATIONURL = (SELECT TEAM.ASSOCIATIONURL FROM TEAM WHERE ASSOCIATIONNAME = '"+ countryName +"') AND PLAYS.MATCHID = "+ matchId +";";

            String sql_goals = "SELECT COUNT(*) as goals\n" +
                    "FROM GOAL\n" +
                    "WHERE MATCHID = "+ matchId +" AND TEAM = (SELECT TEAM.ASSOCIATIONURL FROM TEAM WHERE ASSOCIATIONNAME = '"+ countryName +"');";

            int goals = 0;
            try (ResultSet result = connection.executeQuery(sql_goals)) {
                while (result.next()) {
                    goals = Integer.parseInt(result.getString("goals"));
                }
            }

            System.out.println(countryName+" has scored "+goals+" goals so far in this match. \n");

            List<String> playersPlaying = new ArrayList<>();
            try (ResultSet result = connection.executeQuery(sql1)) {
                System.out.println("The following players from "+countryName+" are playing for the match "+matchId+": \n");
                int i = 1;
                while (result.next()) {
                    playersPlaying.add(result.getString("name"));
                    System.out.println(i + ". " + result.getString("name") + " " + result.getString("shirtnumber") + " " + result.getString("detailedposition") + " " + " from minute " + result.getString("minentered") + " to minute " + result.getString("minexited"));
                    i++;
                }
            }

            int player = -1;

            while(player == -1) {
                System.out.print("\nPlease enter the number of the player who scored the goal or enter [P] to go back: ");

                input1 = keyboard.nextLine();

                if(input1.charAt(0) == 'P')
                    return;

                player = Integer.parseInt(input1);
            }

            if(player < 1 || player > playersPlaying.size()) {
                System.out.println("Invalid player number");
                input1 = null;
                continue;
            }

            String player1 = playersPlaying.get(player - 1);

            String input2 = null;

            int minute = -1;

            while(minute == -1) {
                System.out.print("\nPlease enter the minute of the goal: ");

                input2 = keyboard.nextLine();

                if(input2.charAt(0) == 'P')
                    return;

                minute = Integer.parseInt(input2);
            }

            char inputPenalty = '0';
            while(inputPenalty != 'T' && inputPenalty != 'N') {
                System.out.print("\nPlease enter [T] if it was a penalty kick, else enter [N]: ");
                inputPenalty = keyboard.nextLine().charAt(0);
            }

            boolean isPenaltyKick = inputPenalty == 'T';

            String occurence = "1st half";
            if(minute > 45) occurence = "2nd half";
            if(minute > 90) occurence = "extra time";

            String sql2 = "INSERT INTO GOAL (TEAM, UUID, MATCHID, DURINGPENALTYKICK, MINUTE, OCCURRENCE)\n" +
                    "VALUES ((SELECT TEAM.ASSOCIATIONURL FROM TEAM WHERE ASSOCIATIONNAME = '"+ countryName +"'), (SELECT UUID FROM TEAMMEMBER WHERE NAME = '"+ player1 +"'), "+ matchId +", " + isPenaltyKick + ", "+ minute +", '"+ occurence +"');";
            connection.executeUpdate(sql2);
        }
    }

}
