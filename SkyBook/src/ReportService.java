public class ReportService {

    public static void displayReports(FlightService service) {

        System.out.println("\n==============================================");
        System.out.println("          FLIGHT LOAD FACTOR REPORT");
        System.out.println("==============================================");

        for (Flight flight : service.getFlights()) {

            System.out.println("\nFlight: " +
                    flight.getFlightNumber());

            System.out.println("Route: " +
                    flight.getSource() + " -> " +
                    flight.getDestination());

            for (TravelClass travelClass : TravelClass.values()) {

                int capacity = flight.getCapacity(travelClass);
                int occupied = flight.getOccupied(travelClass);
                int available = flight.getAvailableSeats(travelClass);

                double loadFactor = capacity == 0 ? 0 :
                        (occupied * 100.0 / capacity);

                int waitlisted = service.getWaitlistCount(
                        flight, travelClass
                );

                System.out.println("\nClass: " + travelClass);
                System.out.println("Total Capacity: " + capacity);
                System.out.println("Confirmed Seats: " + occupied);
                System.out.println("Available Seats: " + available);
                System.out.println("Waitlisted Passengers: " + waitlisted);

                System.out.printf(
                        "Load Factor: %.2f%%%n", loadFactor
                );
            }
        }

        System.out.println("\n==============================================");
    }
}