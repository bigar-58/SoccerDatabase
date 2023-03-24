import java.util.Scanner;
import java.sql.* ;

public class Soccer {


    public static void main(String[] args) throws SQLException {
        String url = "jdbc:db2://winter2023-comp421.cs.mcgill.ca:50000/cs421";

        String userid = "cs421g134";
        String password = "Bhp8D#pC";

        SqlConnection sqlConnection = new SqlConnection(url, userid, password);
        Queries queries = new Queries(sqlConnection);

        Scanner keyboard = new Scanner(System.in);

        int menuOption;

        while(true) {
            System.out.println("Soccer Main Menu");
            System.out.println("\t1. List information of matches of a country");
            System.out.println("\t2. Insert initial player information for a match");
            System.out.println("\t3. Insert a goal");
            System.out.println("\t4. Exit application");
            System.out.print("Please Enter Your Option: ");
            menuOption = keyboard.nextInt();

            switch (menuOption) {
                case 1:
                    queries.listMatchesByCountry();
                    break;
                case 2:
                    queries.insertInitialPlayerInfo();
                    break;
                case 3:
                    queries.insertGoal();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

}