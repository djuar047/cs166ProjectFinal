/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class AirlineManagement {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of AirlineManagement
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public AirlineManagement(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end AirlineManagement

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            AirlineManagement.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      AirlineManagement esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the AirlineManagement object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new AirlineManagement (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
               String[] userInfo = authorisedUser.split("\\|");
               String username = userInfo[0];
               String userType = userInfo[1];
               String specificID = userInfo.length > 2 ? userInfo[2] : "";

              boolean usermenu = true;
              while(usermenu) {
               System.out.println("\nMAIN MENU (" + userType + ")");
               System.out.println("----------------------");

                //**the following functionalities should only be able to be used by Management**
                if (userType.equals("Management")) {

                System.out.println("1. View Flights");
                System.out.println("2. View Flight Seats");
                System.out.println("3. View Flight Status");
                System.out.println("4. View Flights of the day");  
                System.out.println("5. View Full Order ID History");
                System.out.println("6. View traveler's information");
                System.out.println("7. View plane information and last repair date");
                System.out.println("8. View all repairs made by a technician");
                System.out.println("9. List all the dates and the codes for repairs performed");
                System.out.println("10. View all the statistics a flight");

                }

                //**the following functionalities should only be able to be used by customers**
                else if (userType.equals("Customer")) {

                System.out.println("11. Search Flights");
                System.out.println("12. Search ticket costs");
                System.out.println("13. Search airplane make and model");
                System.out.println("14. Make a reservation for a flight");

                }

                //**the following functionalities should ony be able to be used by Technicians**
                else if (userType.equals("Technician")) {

                System.out.println("15. View all the repairs performed for a plane");
                System.out.println("16. View all the requests made by a pilot");
                System.out.println("17. Make a repair entry");

                }
                

                //**the following functionalities should ony be able to be used by Pilots**
                else if (userType.equals("Pilot")) {
                   System.out.println("18. Maintenace Request");
                }

               

                System.out.println("20. Log out");
                switch (readChoice()){
                  // Management only ----------------
                   // view flights
                   case 1: 
                   if (userType.equals("Management")) 
                     feature1(esql); 
                   else 
                     System.out.println("Unauthorized access!"); 
                   break;

                   // view flight seats
                   case 2: 
                   if (userType.equals("Management"))
                     feature2(esql); 
                     else
                     System.out.println("Unauthorized access!");
                     break;

                   // view flight status
                   case 3: 
                   if (userType.equals("Management")) 
                     feature3(esql); 
                   else
                     System.out.println("Unauthorized access!");
                   break;

                   // view flights of the day
                   case 4: 
                     if (userType.equals("Management")) 
                        feature4(esql); 
                     else
                        System.out.println("Unauthorized access!");
                     break;

                   // view full order ID History
                   case 5: 
                     if (userType.equals("Management")) 
                        feature5(esql); 
                     else
                        System.out.println("Unauthorized access!");
                     break;

                   // View traveler information
                   case 6: 
                     if (userType.equals("Management"))
                        feature6(esql); 
                     else
                        System.out.println("Unauthorized access!");
                     break;

                   // View plane information 
                   case 7: 
                     if (userType.equals("Management")) 
                        feature7(esql); 
                     else 
                        System.out.println("Unauthorized access!");
                     break;

                   // view all repairs made by a tech
                   case 8: 
                     if (userType.equals("Management") || userType.equals("Technician")) 
                        feature8(esql); 
                     else 
                        System.out.println("Unauthorized access!");
                     break;

                   // list all the dates for repairs 
                   case 9: 
                     if (userType.equals("Management") || userType.equals("Technician")) 
                        feature9(esql); 
                     else 
                        System.out.println("Unauthorized access!");
                     break;
                   // view flight stats
                   case 10: 
                     if (userType.equals("Management")) 
                        feature10(esql); 
                     else 
                        System.out.println("Unauthorized access!");
                     break;

                   // Customers only ----------------
                   // Search flights
                   case 11: 
                    if (userType.equals("Customer"))
                        feature11(esql); 
                    else
                        System.out.println("Unauthorized access!");
                    break;

                   // Search ticket costs
                   case 12: 
                    if (userType.equals("Customer"))
                        feature12(esql); 
                    else
                        System.out.println("Unauthorized access!");
                    break;

                   // Search airplane make and model
                   case 13: 
                     if (userType.equals("Customer")) 
                        feature13(esql); 
                     else 
                        System.out.println("Unauthorized access!");
                     break;

                   // Make a reservation
                    case 14: 
                     if (userType.equals("Customer")) 
                        feature14(esql); 
                     else 
                        System.out.println("Unauthorized access!");
                     break;

                   // Technicians only --------------
                   // view all repairs made for a plane
                   case 15: 
                     if (userType.equals("Technician")) 
                        feature15(esql); 
                     else 
                        System.out.println("Unauthorized access!");
                     break;
                   // View pilot repair requests
                   case 16: 
                     if (userType.equals("Technician")) 
                        feature16(esql); 
                     else
                        System.out.println("Unauthorized access!");
                     break;
                     
                   // Make a repair complete entry
                   case 17: 
                     if (userType.equals("Technician"))
                        feature17(esql); 
                     else
                        System.out.println("Unauthorized access!");
                     break;

                   // Pilots only -------------------
                   // Make a repair request
                   case 18: 
                     if (userType.equals("Pilot"))
                        feature18(esql); 
                     else
                        System.out.println("Unauthorized access!");
                     break;

                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         System.out.println("\n");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            System.out.println("\n");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(AirlineManagement esql){
         try {
        System.out.println("\nCreate New User");
        System.out.print("Enter username: ");
        String username = in.readLine().trim();
        System.out.print("Enter password: ");
        String password = in.readLine().trim();
        System.out.print("Enter user type (Management/Customer/Pilot/Technician): ");
        String userType = in.readLine().trim();
        
        String specificID = "";
        if (!userType.equals("Management")) {
            System.out.print("Enter your role-specific ID: ");
            specificID = in.readLine().trim();
            
            // Verify ID exists in relevant table
            String checkQuery = "";
            switch(userType) {
                case "Customer":
                    checkQuery = "SELECT CustomerID FROM Customer WHERE CustomerID = " + specificID;
                    break;
                case "Pilot":
                    checkQuery = String.format("SELECT PilotID FROM Pilot WHERE PilotID = '%s'", specificID);
                    break;
                case "Technician":
                    checkQuery = String.format("SELECT TechnicianID FROM Technician WHERE TechnicianID = '%s'", specificID);
                    break;
            }
            
            if (esql.executeQuery(checkQuery) == 0) {
                System.out.println("Invalid ID! No such " + userType + " found.");
                return;
            }
        }

        String insertQuery = String.format(
            "INSERT INTO User_ (Username, Password, UserType, SpecificID) " +
            "VALUES ('%s', '%s', '%s', '%s')",
            username.replace("'", "''"),
            password.replace("'", "''"),
            userType.replace("'", "''"),
            specificID.replace("'", "''")
        );
        
        esql.executeUpdate(insertQuery);
        System.out.println("User created successfully!");
    } catch (Exception e) {
        System.err.println("Error creating user: " + e.getMessage());
    }

   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(AirlineManagement esql){
      
     try {
        System.out.print("\nUsername: ");
        String username = in.readLine().trim();
        System.out.print("Password: ");
        String password = in.readLine().trim();
        
        String query = String.format(
            "SELECT UserType, SpecificID FROM User_ " +
            "WHERE Username = '%s' AND Password = '%s'",
            username.replace("'", "''"),
            password.replace("'", "''")
        );
        
        List<List<String>> result = esql.executeQueryAndReturnResult(query);
        if (result.isEmpty()) {
            System.out.println("Invalid credentials!");
            return null;
        }
        
        List<String> row = result.get(0);
        return username + "|" + row.get(0) + "|" + row.get(1);
    } catch (Exception e) {
        System.err.println("Login error: " + e.getMessage());
        return null;
    }
   }//end

// Rest of the functions definition go in here

   // feature 1 -----------------------------------------------------------------------------------------
   public static void feature1(AirlineManagement esql) {
      try {
        // flight number input
        System.out.print("Enter Flight Number: ");
        String flightNumber = in.readLine();
        flightNumber = "'" + flightNumber + "'";

        // find all the flights 
        String query = "SELECT DayOfWeek, DepartureTime, ArrivalTime " +
                       "FROM Schedule WHERE FlightNumber = " + flightNumber + ";";
      // output data
      System.out.println("-----------------------------------");
        esql.executeQueryAndPrintResult(query);
      System.out.println("-----------------------------------\n");
      System.out.println("\n");
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
   }

   // feature 2 -----------------------------------------------------------------------------------------
   
   public static void feature2(AirlineManagement esql) {
      try {
        System.out.print("Enter Flight Number: ");
        String flightNumber = in.readLine();

        System.out.print("Enter Flight Date (YYYY-MM-DD): ");
        String flightDate = in.readLine();

         String instanceQuery = String.format(
            "SELECT FlightInstanceID FROM FlightInstance WHERE FlightNumber = '%s' AND FlightDate = '%s';",
            flightNumber, flightDate);

        List<List<String>> result = esql.executeQueryAndReturnResult(instanceQuery);

        if (result.size() == 0) {
            System.out.println("No flight instance found.");
            return;
        }
        String flightInstanceID = result.get(0).get(0);

        String seatsQuery = String.format(
            "SELECT SeatsTotal, SeatsSold, (SeatsTotal - SeatsSold) AS SeatsAvailable FROM FlightInstance WHERE FlightInstanceID = '%s';",
            flightInstanceID);

        esql.executeQueryAndPrintResult(seatsQuery);

    } 
    catch (Exception e) {
        System.err.println(e.getMessage());
    }

   }

   // feature 3 -----------------------------------------------------------------------------------------
   public static void feature3(AirlineManagement esql) {
      try {
        System.out.print("Enter Flight Number: ");
        String flightNumber = in.readLine();
        flightNumber = "'" + flightNumber + "'";

        System.out.print("Enter Flight Date (YYYY-MM-DD): ");
        String flightDate = in.readLine();
        flightDate = "'" + flightDate + "'";

        String instanceQuery = "SELECT FlightInstanceID FROM FlightInstance WHERE FlightNumber = "
                                + flightNumber + " AND FlightDate = " + flightDate + ";";

        List<List<String>> result = esql.executeQueryAndReturnResult(instanceQuery);
        if (result.size() == 0) {
            System.out.println("No flight instance found.");
            return;
        }

        String flightInstanceID = result.get(0).get(0);
        String query = "SELECT DepartedOnTime, ArrivedOnTime FROM FlightInstance WHERE FlightInstanceID = " 
                        + flightInstanceID + ";";

        esql.executeQueryAndPrintResult(query);

    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
   }
   // feature 4 -----------------------------------------------------------------------------------------
   public static void feature4(AirlineManagement esql) {
      try {
        System.out.print("Enter Flight Date (YYYY-MM-DD): ");
        String flightDate = in.readLine();
        flightDate = "'" + flightDate + "'";

        String query = "SELECT FlightInstanceID, FlightNumber, FlightDate FROM FlightInstance WHERE FlightDate = " + flightDate + ";";

        esql.executeQueryAndPrintResult(query);
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }
   }
   // feature 5 -----------------------------------------------------------------------------------------
   public static void feature5(AirlineManagement esql) {
          try {
        //FlightNumber user input
        System.out.print("Enter Flight Number: ");
        String flightNumber = in.readLine();
        flightNumber = "'" + flightNumber + "'";  // surround with single quotes for SQL

        //FlightDate user input
        System.out.print("Enter Flight Date (YYYY-MM-DD): ");
        String flightDate = in.readLine();
        flightDate = "'" + flightDate + "'";

        // Get the FlightInstanceID
        String instanceQuery = "SELECT FlightInstanceID FROM FlightInstance WHERE FlightNumber = "
                                + flightNumber + " AND FlightDate = " + flightDate + ";";

        List<List<String>> result = esql.executeQueryAndReturnResult(instanceQuery);
        // No flights found 
        if (result.size() == 0) {
            System.out.println("No flight instance found.");
            return;
        }

        String flightInstanceID = result.get(0).get(0);

        // output the data for each query 

        // reservations
        System.out.println("\nPassengers with reservations (reserved):");
        String reservedQuery = "SELECT CustomerID FROM Reservation WHERE FlightInstanceID = " 
                                + flightInstanceID + " AND Status = 'reserved';";
        esql.executeQueryAndPrintResult(reservedQuery);

        // waitlist
        System.out.println("\nPassengers on waitlist:");
        String waitlistQuery = "SELECT CustomerID FROM Reservation WHERE FlightInstanceID = " 
                                + flightInstanceID + " AND Status = 'waitlist';";
        esql.executeQueryAndPrintResult(waitlistQuery);

        // people that actually flew
        System.out.println("\nPassengers who actually flew:");
        String flownQuery = "SELECT CustomerID FROM Reservation WHERE FlightInstanceID = " 
                                + flightInstanceID + " AND Status = 'flown';";
        esql.executeQueryAndPrintResult(flownQuery);

    } catch (Exception e) {
        System.err.println(e.getMessage());
    }
   }

   // feature 6 -----------------------------------------------------------------------------------------
   public static void feature6(AirlineManagement esql) {
      try {
        // grab reservation input from user
        System.out.print("Enter ReservationID: ");
        String reservationID = in.readLine();

        // compute the user's reservation
        String customerIDQuery = String.format(
            "SELECT CustomerID FROM Reservation WHERE ReservationID = '%s';", reservationID);

        List<List<String>> result = esql.executeQueryAndReturnResult(customerIDQuery);

        // No reservation found
        if (result.size() == 0) {
            System.out.println("No reservation found for that ReservationID.");
            return;
        }

        String customerID = result.get(0).get(0);

        // Query to get all the customer's information
        String customerQuery = String.format(
            "SELECT FirstName, LastName, Gender, DOB, Address, Phone, Zip FROM Customer WHERE CustomerID = '%s';",
            customerID);
         // Output query
        esql.executeQueryAndPrintResult(customerQuery);

    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
   }
   // feature 7 -----------------------------------------------------------------------------------------

public static void feature7(AirlineManagement esql) {
    try {
        System.out.print("\nEnter Plane ID: ");
        String planeID = in.readLine();
        
        String query = String.format(
            "SELECT Make, Model, " +
            "EXTRACT(YEAR FROM CURRENT_DATE) - Year AS Age, " +
            "LastRepairDate " +
            "FROM Plane " +
            "WHERE PlaneID = '%s'",
            planeID.replace("'", "''")
        );
        
        int rowCount = esql.executeQueryAndPrintResult(query);
        if (rowCount == 0) {
            System.out.println("No plane found with ID: " + planeID);
        }
    } catch (Exception e) {
        System.err.println("Error retrieving plane details: " + e.getMessage());
    }
}

   // feature 8 -----------------------------------------------------------------------------------------

public static void feature8(AirlineManagement esql) {
    try {
        System.out.print("\nEnter Technician ID: ");
        String techID = in.readLine();
        
        String query = String.format(
            "SELECT RepairID, PlaneID, RepairCode, RepairDate " +
            "FROM Repair " +
            "WHERE TechnicianID = '%s' " +
            "ORDER BY RepairDate DESC",
            techID.replace("'", "''")
        );
        
        int rowCount = esql.executeQueryAndPrintResult(query);
        if (rowCount == 0) {
            System.out.println("No repairs found for technician: " + techID);
        }
    } catch (Exception e) {
        System.err.println("Error retrieving repairs: " + e.getMessage());
    }
}

   // feature 9 -----------------------------------------------------------------------------------------

public static void feature9(AirlineManagement esql) {
    try {
        System.out.print("\nEnter Plane ID: ");
        String planeID = in.readLine();
        System.out.print("Enter Start Date (YYYY-MM-DD): ");
        String startDate = in.readLine();
        System.out.print("Enter End Date (YYYY-MM-DD): ");
        String endDate = in.readLine();
        
        String query = String.format(
            "SELECT RepairDate, RepairCode " +
            "FROM Repair " +
            "WHERE PlaneID = '%s' " +
            "AND RepairDate BETWEEN '%s' AND '%s' " +
            "ORDER BY RepairDate DESC",
            planeID.replace("'", "''"),
            startDate.replace("'", "''"),
            endDate.replace("'", "''")
        );
        
        int rowCount = esql.executeQueryAndPrintResult(query);
        if (rowCount == 0) {
            System.out.println("No repairs found for plane " + planeID + 
                               " between " + startDate + " and " + endDate);
        }
    } catch (Exception e) {
        System.err.println("Error retrieving repairs: " + e.getMessage());
    }
}

   // feature 10 -----------------------------------------------------------------------------------------

 public static void feature10(AirlineManagement esql) {
    try {
        System.out.print("\nEnter Flight Number: ");
        String flightNum = in.readLine();
        System.out.print("Enter Start Date (YYYY-MM-DD): ");
        String startDate = in.readLine();
        System.out.print("Enter End Date (YYYY-MM-DD): ");
        String endDate = in.readLine();
        
        String query = String.format(
            "SELECT " +
            "  COUNT(*) AS DaysOperated, " +
            "  SUM(SeatsSold) AS TotalSold, " +
            "  SUM(SeatsTotal - SeatsSold) AS TotalUnsold " +
            "FROM FlightInstance " +
            "WHERE FlightNumber = '%s' " +
            "  AND FlightDate BETWEEN '%s' AND '%s'",
            flightNum.replace("'", "''"),
            startDate.replace("'", "''"),
            endDate.replace("'", "''")
        );
        
        List<List<String>> result = esql.executeQueryAndReturnResult(query);
        if (result.isEmpty()) {
            System.out.println("No data found for flight " + flightNum + 
                               " between " + startDate + " and " + endDate);
        } else {
            List<String> row = result.get(0);
            System.out.println("\nFlight Statistics (" + flightNum + " " + startDate + " to " + endDate + "):");
            System.out.println("Days operated: " + row.get(0));
            System.out.println("Total tickets sold: " + row.get(1));
            System.out.println("Total unsold tickets: " + row.get(2));
        }
    } catch (Exception e) {
        System.err.println("Error retrieving flight statistics: " + e.getMessage());
    }
}  

   // feature 11 -----------------------------------------------------------------------------------------
  
  public static void feature11(AirlineManagement esql) {
    try {

      // user inputs 


        // departure city
        System.out.print("Enter Departure City: ");
        String depCity = in.readLine();
        depCity = "'" + depCity + "'";

        // arrival city
        System.out.print("Enter Arrival City: ");
        String arrCity = in.readLine();
        arrCity = "'" + arrCity + "'";

        //Flight Date
        System.out.print("Enter Flight Date (YYYY-MM-DD): ");
        String flightDate = in.readLine();
        flightDate = "'" + flightDate + "'";

        // computing using the user's inputs 

        // All flights on a given date
        String searchQuery = "SELECT F.FlightNumber, S.DepartureTime, S.ArrivalTime, FI.NumOfStops " +
                "FROM Flight F, Schedule S, FlightInstance FI " +
                "WHERE F.FlightNumber = FI.FlightNumber AND F.FlightNumber = S.FlightNumber " +
                "AND F.DepartureCity = " + depCity + " AND F.ArrivalCity = " + arrCity + " " +
                "AND FI.FlightDate = " + flightDate + ";";


         // output all flights data
        esql.executeQueryAndPrintResult(searchQuery);

        // on-time record (as percentage)
        System.out.println("\nOn-Time Record (Historical %):");

        String onTimeQuery = "SELECT FI.FlightNumber, " +
                "ROUND(100.0 * SUM(CASE WHEN DepartedOnTime THEN 1 ELSE 0 END)/COUNT(*), 2) AS DepartedOnTimePercent, " +
                "ROUND(100.0 * SUM(CASE WHEN ArrivedOnTime THEN 1 ELSE 0 END)/COUNT(*), 2) AS ArrivedOnTimePercent " +
                "FROM Flight F, FlightInstance FI " +
                "WHERE F.FlightNumber = FI.FlightNumber " +
                "AND F.DepartureCity = " + depCity + " AND F.ArrivalCity = " + arrCity + " " +
                "GROUP BY FI.FlightNumber;";

        // output on-time data

        esql.executeQueryAndPrintResult(onTimeQuery);

    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
}

// feature 12 -----------------------------------------------------------------------------------------

public static void feature12(AirlineManagement esql) {
    try {
        // Flight number input
        System.out.print("Enter Flight Number: ");
        String flightNumber = in.readLine();
        flightNumber = "'" + flightNumber + "'";

        // search flight ticket costs for flights (might have multiple dates)
        String query = "SELECT FlightInstanceID, FlightDate, TicketCost " +
                       "FROM FlightInstance WHERE FlightNumber = " + flightNumber + ";";

         // output data
        esql.executeQueryAndPrintResult(query);

    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
}

// feature 13 -----------------------------------------------------------------------------------------

public static void feature13(AirlineManagement esql) {
    try {
        System.out.print("\nEnter Flight Number: ");
        String flightNum = in.readLine();
        
        String query = String.format(
            "SELECT p.Make, p.Model " +
            "FROM Flight f " +
            "JOIN Plane p ON f.PlaneID = p.PlaneID " +
            "WHERE f.FlightNumber = '%s'",
            flightNum.replace("'", "''")
        );
        
        int rowCount = esql.executeQueryAndPrintResult(query);
        if (rowCount == 0) {
            System.out.println("No airplane found for flight: " + flightNum);
        }
    } catch (Exception e) {
        System.err.println("Error retrieving airplane type: " + e.getMessage());
    }
}

// feature 14 -----------------------------------------------------------------------------------------

public static void feature14(AirlineManagement esql) {
    try {
        // Get user inputs
        System.out.print("\nEnter Customer ID: ");
        String customerID = in.readLine();
        System.out.print("Enter Flight Instance ID: ");
        String flightInstanceID = in.readLine();

        // Start transaction
        esql.beginTransaction();
        
        try {
            // Check flight capacity and lock row
            String capacityQuery = String.format(
                "SELECT SeatsTotal, SeatsSold " +
                "FROM FlightInstance " +
                "WHERE FlightInstanceID = %s FOR UPDATE",  // FOR UPDATE locks the row
                flightInstanceID
            );
            
            List<List<String>> capacityResult = esql.executeQueryAndReturnResult(capacityQuery);
            if (capacityResult.isEmpty()) {
                System.out.println("Invalid Flight Instance ID");
                esql.rollback();
                return;
            }
            
            int seatsTotal = Integer.parseInt(capacityResult.get(0).get(0));
            int seatsSold = Integer.parseInt(capacityResult.get(0).get(1));
            int availableSeats = seatsTotal - seatsSold;

            // Generate unique reservation ID
            String reservationID = "RES" + System.currentTimeMillis() + (int)(Math.random() * 1000);
            
            if (availableSeats > 0) {
                // Make reservation
                String reservationQuery = String.format(
                    "INSERT INTO Reservation (ReservationID, CustomerID, FlightInstanceID, Status) " +
                    "VALUES ('%s', %s, %s, 'reserved')",
                    reservationID, customerID, flightInstanceID
                );
                
                String updateQuery = String.format(
                    "UPDATE FlightInstance " +
                    "SET SeatsSold = SeatsSold + 1 " +
                    "WHERE FlightInstanceID = %s",
                    flightInstanceID
                );
                
                esql.executeUpdate(reservationQuery);
                esql.executeUpdate(updateQuery);
                esql.commit();
                System.out.println("Reservation successful! ID: " + reservationID);
            } else {
                // Join waitlist
                String waitlistQuery = String.format(
                    "INSERT INTO Reservation (ReservationID, CustomerID, FlightInstanceID, Status) " +
                    "VALUES ('%s', %s, %s, 'waitlist')",
                    reservationID, customerID, flightInstanceID
                );
                
                esql.executeUpdate(waitlistQuery);
                esql.commit();
                System.out.println("Flight is full. You've been added to waitlist. ID: " + reservationID);
            }
        } catch (Exception e) {
            esql.rollback();
            throw e;
        }
    } catch (Exception e) {
        System.err.println("Error processing reservation: " + e.getMessage());
    }
}

// feature 15 -----------------------------------------------------------------------------------------


public static void feature15(AirlineManagement esql) {
    try {
        // plane ID input
        System.out.print("Enter Plane ID: ");
        String planeID = in.readLine();
        planeID = "'" + planeID + "'";

        // start date
        System.out.print("Enter Start Date (YYYY-MM-DD): ");
        String startDate = in.readLine();
        startDate = "'" + startDate + "'";

        // end date
        System.out.print("Enter End Date (YYYY-MM-DD): ");
        String endDate = in.readLine();
        endDate = "'" + endDate + "'";

        // list all the dates and the codes for repair for a plane within the range specified
        String query = "SELECT RepairDate, RepairCode " +
                       "FROM Repair " +
                       "WHERE PlaneID = " + planeID + 
                       " AND RepairDate BETWEEN " + startDate + " AND " + endDate + 
                       " ORDER BY RepairDate;";

         // output data
        esql.executeQueryAndPrintResult(query);

    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
}

public void beginTransaction() throws SQLException {
    _connection.setAutoCommit(false);
}

public void commit() throws SQLException {
    _connection.commit();
    _connection.setAutoCommit(true);
}

public void rollback() {
    try {
        _connection.rollback();
        _connection.setAutoCommit(true);
    } catch (SQLException e) {
        System.err.println("Rollback failed: " + e.getMessage());
    }
}


// feature 16 -----------------------------------------------------------------------------------------

public static void feature16(AirlineManagement esql) {
    try {
        // pilot ID input
        System.out.print("Enter Pilot ID: ");
        String pilotID = in.readLine();
        pilotID = "'" + pilotID + "'";

         // find all the maintenance requests made by the pilot

        String query = "SELECT RequestID, PlaneID, RepairCode, RequestDate " +
                       "FROM MaintenanceRequest " +
                       "WHERE PilotID = " + pilotID + ";";


         // output data
        esql.executeQueryAndPrintResult(query);

    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
}

// feature 17 -----------------------------------------------------------------------------------------

public static void feature17(AirlineManagement esql) {
    try {
        System.out.println("\nRecording a Repair...");
        System.out.print("Enter Technician ID: ");
        String techID = in.readLine().trim();
        System.out.print("Enter Plane ID: ");
        String planeID = in.readLine().trim();
        System.out.print("Enter Repair Code: ");
        String repairCode = in.readLine().trim();
        
        // Default to today's date if not provided
        System.out.print("Enter Repair Date (YYYY-MM-DD) [leave blank for today]: ");
        String repairDateStr = in.readLine().trim();
        if (repairDateStr.isEmpty()) {
            repairDateStr = java.time.LocalDate.now().toString();
        }
        
        esql.beginTransaction();
        
        // Insert repair record
        String insertRepair = String.format(
            "INSERT INTO Repair (PlaneID, RepairCode, RepairDate, TechnicianID) " +
            "VALUES ('%s', '%s', '%s', '%s')",
            planeID.replace("'", "''"),
            repairCode.replace("'", "''"),
            repairDateStr.replace("'", "''"),
            techID.replace("'", "''")
        );
        esql.executeUpdate(insertRepair);
        
        // Update last repair date in Plane table
        String updatePlane = String.format(
            "UPDATE Plane SET LastRepairDate = '%s' WHERE PlaneID = '%s'",
            repairDateStr.replace("'", "''"),
            planeID.replace("'", "''")
        );
        esql.executeUpdate(updatePlane);
        
        esql.commit();
        System.out.println("Repair recorded successfully.");
    } catch (Exception e) {
        System.err.println("Error recording repair: " + e.getMessage());
        esql.rollback();
    }
}


// feature 18 -----------------------------------------------------------------------------------------

public static void feature18(AirlineManagement esql) {
    try {
        System.out.println("\nSubmit Maintenance Request");
        System.out.print("Enter Pilot ID: ");
        String pilotID = in.readLine().trim();
        System.out.print("Enter Plane ID: ");
        String planeID = in.readLine().trim();
        System.out.print("Enter Repair Code: ");
        String repairCode = in.readLine().trim();

        // Default to today's date
        String requestDate = java.time.LocalDate.now().toString();

        esql.beginTransaction();

        // Insert maintenance request
        String insertQuery = String.format(
            "INSERT INTO MaintenanceRequest (PlaneID, RepairCode, RequestDate, PilotID) " +
            "VALUES ('%s', '%s', '%s', '%s')",
            planeID.replace("'", "''"),
            repairCode.replace("'", "''"),
            requestDate.replace("'", "''"),
            pilotID.replace("'", "''")
        );
        esql.executeUpdate(insertQuery);

        esql.commit();
        System.out.println("Maintenance request submitted successfully!");
    } catch (Exception e) {
        System.err.println("Error submitting maintenance request: " + e.getMessage());
        esql.rollback();
    }
}


}//end AirlineManagement

