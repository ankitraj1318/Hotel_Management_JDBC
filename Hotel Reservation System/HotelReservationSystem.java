import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "";
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try{
           Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.println();

                int choice = scanner.nextInt();
                switch (choice){
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    case 4:
                        updateReservation(connection, scanner);
                        break;
                    case 5:
                        deleteReservation(connection, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Inavlid Choice. Try Again.");
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter Guest Name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.println("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter Contact Number: ");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) " +
                    "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "');";

            try (Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);

                if(affectedRows > 0){
                    System.out.println("Reservation Successful!!!");
                }else{
                    System.out.println("Reservation failed!!!");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    private static void viewReservations(Connection connection){
        String sql = "SELECT * from reservations;";

        try( Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

            System.out.println("Current Reservations: ");
            System.out.println();
            while(resultSet.next()){
                int reservationId = resultSet.getInt("reservation_id");
                System.out.println("Reservation Id: "+reservationId);
                String guestName = resultSet.getString("guest_name");
                System.out.println("Guest Name: "+guestName);
                int roomNumber = resultSet.getInt("room_number");
                System.out.println("Room Number: "+roomNumber);
                String contactNUmber = resultSet.getString("contact_number");
                System.out.println("Contact NUmber: "+contactNUmber);
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();
                System.out.println("Reservation Date: "+reservationDate);
                System.out.println();
                System.out.println("==================");
                System.out.println();
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void getRoomNumber(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter Reservation Id: ");
            int reservationId = scanner.nextInt();
            System.out.println("Enter Guest Name: ");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservations WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "';";

            try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){

                if(resultSet.next()){
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room Number for reservation Id "+ reservationId + " and Guest "+ guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and Guest Name.");
                }
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

    private static void updateReservation(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter Reservation Id to update:");
            int reservationId  = scanner.nextInt();
            scanner.nextLine();
            if(!reservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the given Id.");
            }

            System.out.println("Enter new Guest Name: ");
            String newGuestName = scanner.nextLine();
            System.out.println("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.println("Enter new Contact Number: ");
            String newContactNUmber = scanner.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', "+
                    "room_number = " + newRoomNumber + ", "+
                    "contact_number = '" + newContactNUmber + "' "+
                    "WHERE reservation_id = " + reservationId+";";

            try(Statement statement = connection.createStatement()){
                int affectedRow = statement.executeUpdate(sql);

                if(affectedRow > 0){
                    System.out.println("Reservation Updated Successfully!!");
                }else{
                    System.out.println("Reservation update Failed!!");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter the reservation id to delete: ");
            int reservationId = scanner.nextInt();

            if(!reservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the given Id.");
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = "+ reservationId +";";

            try(Statement statement = connection.createStatement()){
                int affectedRow = statement.executeUpdate(sql);
                if(affectedRow > 0){
                    System.out.println("Reservation Deleted Successfully.");
                }else{
                    System.out.println("Failed to Delete the Reservation.");
                }
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId){
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = "+reservationId + ";";

            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){

                return resultSet.next(); // If there's a result the reservation Exists
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return false; //Handle database errors as needed
        }
    }

    public static void exit() throws InterruptedException {
        System.out.println("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(500);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!");
    }
}