import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final FlightService service =
            new FlightService();

    public static void main(String[] args) {

        System.out.println("\n");
        System.out.println("==================================================");
        System.out.println("       SKYBOOK FLIGHT RESERVATION SYSTEM");
        System.out.println("==================================================");
        System.out.println("       Dynamic-Fare Booking and Waitlist Engine");
        System.out.println("==================================================");

        boolean running = true;

        while (running) {

            displayMenu();

            int choice = readInt("Enter your choice: ");

            try {
                switch (choice) {

                    case 1:
                        displayFlights();
                        break;

                    case 2:
                        searchFlights();
                        break;

                    case 3:
                        bookFlight();
                        break;

                    case 4:
                        displayAllBookings();
                        break;

                    case 5:
                        displayWaitlist();
                        break;

                    case 6:
                        cancelBooking();
                        break;

                    case 7:
                        searchBooking();
                        break;

                    case 8:
                        upgradeBooking();
                        break;

                    case 9:
                        ReportService.displayReports(service);
                        break;

                    case 10:
                        running = false;
                        System.out.println(
                                "\nThank you for using SkyBook!"
                        );
                        System.out.println(
                                "Have a safe journey."
                        );
                        break;

                    default:
                        System.out.println(
                                "Invalid choice. Select 1-10."
                        );
                }

            } catch (IllegalArgumentException |
                     IllegalStateException e) {

                System.out.println("\nOperation failed: " +
                        e.getMessage());

            } catch (Exception e) {

                System.out.println(
                        "\nUnexpected error: " + e.getMessage()
                );
            }

            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    private static void displayMenu() {

        System.out.println("\n");
        System.out.println("----------------------------------------------");
        System.out.println("                 MAIN MENU");
        System.out.println("----------------------------------------------");
        System.out.println("1.  Display All Flights");
        System.out.println("2.  Search Flights");
        System.out.println("3.  Book Flight");
        System.out.println("4.  View All Bookings");
        System.out.println("5.  View Waitlist");
        System.out.println("6.  Cancel Booking");
        System.out.println("7.  Search Booking by PNR");
        System.out.println("8.  Upgrade Economy to Business");
        System.out.println("9.  Flight Load Factor Report");
        System.out.println("10. Exit");
        System.out.println("----------------------------------------------");
    }

    private static int readInt(String message) {

        while (true) {

            System.out.print(message);

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(
                        "Please enter a valid number."
                );
            }
        }
    }

    private static String readText(String message) {

        while (true) {

            System.out.print(message);

            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Input cannot be empty.");
        }
    }

    private static TravelClass selectTravelClass() {

        System.out.println("\nSelect Travel Class:");
        System.out.println("1. Economy");
        System.out.println("2. Business");

        int choice = readInt("Enter choice: ");

        if (choice == 1) {
            return TravelClass.ECONOMY;
        }

        if (choice == 2) {
            return TravelClass.BUSINESS;
        }

        throw new IllegalArgumentException(
                "Invalid travel class."
        );
    }

    private static void displayFlights() {

        System.out.println("\n================ AVAILABLE FLIGHTS ================");

        System.out.printf(
                "%-8s %-18s %-15s %-15s %-12s %-10s%n",
                "Flight", "Airline", "Source",
                "Destination", "Date", "Time"
        );

        System.out.println(
                "---------------------------------------------------------------"
        );

        for (Flight flight : service.getFlights()) {

            System.out.println(flight);

            System.out.println(
                    "   Economy: " +
                    flight.getAvailableSeats(TravelClass.ECONOMY) +
                    "/" + flight.getCapacity(TravelClass.ECONOMY) +
                    " | Business: " +
                    flight.getAvailableSeats(TravelClass.BUSINESS) +
                    "/" + flight.getCapacity(TravelClass.BUSINESS)
            );
        }
    }

    private static void searchFlights() {

        System.out.println("\n========== SEARCH FLIGHTS ==========");

        String source = readText("Enter source city: ");
        String destination = readText("Enter destination city: ");
        String date = readText("Enter date (YYYY-MM-DD): ");

        List<Flight> results = service.searchFlights(
                source, destination, date
        );

        if (results.isEmpty()) {
            System.out.println("No matching flights found.");
            return;
        }

        System.out.println("\nMatching Flights:");

        for (Flight flight : results) {

            System.out.println("\n" + flight);

            System.out.printf(
                    "Economy Fare: Rs. %.2f | Available: %d%n",
                    service.getFare(flight, TravelClass.ECONOMY),
                    flight.getAvailableSeats(TravelClass.ECONOMY)
            );

            System.out.printf(
                    "Business Fare: Rs. %.2f | Available: %d%n",
                    service.getFare(flight, TravelClass.BUSINESS),
                    flight.getAvailableSeats(TravelClass.BUSINESS)
            );
        }
    }

    private static void bookFlight() {

        System.out.println("\n========== FLIGHT BOOKING ==========");

        displayFlights();

        String flightNumber = readText(
                "\nEnter flight number: "
        );

        Flight flight = service.findFlight(flightNumber);

        if (flight == null) {
            System.out.println("Flight not found.");
            return;
        }

        System.out.println("\nEnter Passenger Details");

        String name = readText("Passenger name: ");
        int age = readInt("Passenger age: ");
        String email = readText("Passenger email: ");

        Passenger passenger = new Passenger(name, age, email);

        TravelClass travelClass = selectTravelClass();

        double fare = service.getFare(flight, travelClass);

        System.out.printf(
                "\nCalculated Fare: Rs. %.2f%n", fare
        );

        if (flight.getAvailableSeats(travelClass) > 0) {

            System.out.print(
                    "Confirm booking? (Y/N): "
            );

            String confirmation = scanner.nextLine().trim();

            if (!confirmation.equalsIgnoreCase("Y")) {
                System.out.println("Booking cancelled by user.");
                return;
            }

        } else {

            System.out.println(
                    "Sorry! Selected class is FULL."
            );

            System.out.print(
                    "Join the waitlist? (Y/N): "
            );

            String confirmation = scanner.nextLine().trim();

            if (!confirmation.equalsIgnoreCase("Y")) {
                System.out.println("Booking request cancelled.");
                return;
            }
        }

        Booking booking = service.bookFlight(
                passenger, flight, travelClass
        );

        System.out.println("\n================================");
        System.out.println("       BOOKING RESULT");
        System.out.println("================================");

        System.out.println("PNR: " + booking.getPnr());

        System.out.println(
                "Passenger: " + passenger.getName()
        );

        System.out.println(
                "Flight: " + flight.getFlightNumber()
        );

        System.out.println(
                "Travel Class: " + travelClass
        );

        System.out.printf(
                "Fare: Rs. %.2f%n", booking.getFare()
        );

        System.out.println(
                "Status: " + booking.getStatus()
        );

        if (booking.getStatus() == BookingStatus.WAITLISTED) {
            System.out.println(
                    "Waitlist Position: " +
                    booking.getWaitlistPosition()
            );
        }

        System.out.println("================================");
    }

    private static void displayAllBookings() {

        System.out.println("\n========== ALL BOOKINGS ==========");

        if (service.getAllBookings().isEmpty()) {
            System.out.println("No bookings available.");
            return;
        }

        for (Booking booking : service.getAllBookings()) {
            printBooking(booking);
        }
    }

    private static void displayWaitlist() {

        System.out.println("\n========== WAITLIST ==========");

        displayFlights();

        String flightNumber = readText(
                "\nEnter flight number: "
        );

        Flight flight = service.findFlight(flightNumber);

        if (flight == null) {
            System.out.println("Flight not found.");
            return;
        }

        TravelClass travelClass = selectTravelClass();

        List<Booking> waitlist =
                service.getWaitlistedBookings(
                        flight, travelClass
                );

        if (waitlist.isEmpty()) {
            System.out.println("No passengers on this waitlist.");
            return;
        }

        System.out.println("\nFIFO Waitlist:");

        for (Booking booking : waitlist) {

            System.out.println(
                    "Position: " + booking.getWaitlistPosition()
                    + " | PNR: " + booking.getPnr()
                    + " | Passenger: " +
                    booking.getPassenger().getName()
            );
        }
    }

    private static void cancelBooking() {

        System.out.println("\n========== CANCEL BOOKING ==========");

        String pnr = readText("Enter PNR: ");

        Booking booking = service.findBooking(pnr);

        if (booking == null) {
            System.out.println("Invalid PNR.");
            return;
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            System.out.println("Booking already cancelled.");
            return;
        }

        Booking cancelled = service.cancelBooking(pnr);

        System.out.println(
                "\nBooking " + cancelled.getPnr() +
                " cancelled successfully."
        );

        System.out.println(
                "Seat released successfully."
        );
    }

    private static void searchBooking() {

        System.out.println("\n========== PNR SEARCH ==========");

        String pnr = readText("Enter PNR: ");

        Booking booking = service.findBooking(pnr);

        if (booking == null) {
            System.out.println("No booking found for this PNR.");
            return;
        }

        printBooking(booking);
    }

    
private static void upgradeBooking() {

    System.out.println("\n========== BUSINESS CLASS UPGRADE ==========");

    String pnr = readText("Enter booking PNR: ");

    Booking booking = service.findBooking(pnr);

    if (booking == null) {
        System.out.println("\nERROR: Booking not found.");
        return;
    }

    if (booking.getStatus() == BookingStatus.CANCELLED) {
        System.out.println("\nERROR: Cancelled bookings cannot be upgraded.");
        return;
    }

    if (booking.getStatus() == BookingStatus.WAITLISTED) {
        System.out.println("\nERROR: Waitlisted bookings cannot be upgraded.");
        return;
    }

    if (booking.getTravelClass() == TravelClass.BUSINESS) {
        System.out.println("\nThis booking is already in Business class.");
        return;
    }

    System.out.println("\nBooking Details:");
    System.out.println("Passenger: " + booking.getPassenger().getName());
    System.out.println("PNR: " + booking.getPnr());
    System.out.println("Current Class: " + booking.getTravelClass());
    System.out.printf("Current Fare: Rs. %.2f%n", booking.getFare());

    try {

        double difference = service.upgradeToBusiness(pnr);

        System.out.println("\n==================================");
        System.out.println("       UPGRADE SUCCESSFUL");
        System.out.println("==================================");

        System.out.printf(
                "Additional fare payable: Rs. %.2f%n",
                difference
        );

        System.out.printf(
                "New total fare: Rs. %.2f%n",
                booking.getFare()
        );

        System.out.println("New Travel Class: BUSINESS");
        System.out.println("==================================");

    } catch (IllegalArgumentException | IllegalStateException e) {

        System.out.println("\nUPGRADE FAILED: " + e.getMessage());
    }
}


    private static void printBooking(Booking booking) {

        System.out.println("\n-----------------------------------");

        System.out.println("PNR: " + booking.getPnr());

        System.out.println(
                "Passenger: " +
                booking.getPassenger().getName()
        );

        System.out.println(
                "Age: " + booking.getPassenger().getAge()
        );

        System.out.println(
                "Email: " + booking.getPassenger().getEmail()
        );

        System.out.println(
                "Flight: " +
                booking.getFlight().getFlightNumber()
        );

        System.out.println(
                "Route: " +
                booking.getFlight().getSource() +
                " -> " +
                booking.getFlight().getDestination()
        );

        System.out.println(
                "Date: " + booking.getFlight().getDate()
        );

        System.out.println(
                "Class: " + booking.getTravelClass()
        );

        System.out.printf(
                "Fare: Rs. %.2f%n", booking.getFare()
        );

        System.out.println(
                "Status: " + booking.getStatus()
        );

        if (booking.getStatus() == BookingStatus.WAITLISTED) {

            System.out.println(
                    "Waitlist Position: " +
                    booking.getWaitlistPosition()
            );
        }

        System.out.println("-----------------------------------");
    }
}