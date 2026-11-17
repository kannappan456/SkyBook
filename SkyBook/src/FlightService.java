import java.util.*;

public class FlightService {

    private final List<Flight> flights = new ArrayList<>();

    private final Map<String, Booking> bookings =
            new LinkedHashMap<>();

    private final Map<String, Queue<Booking>> waitlists =
            new HashMap<>();

    private final PricingEngine pricingEngine =
            new PricingEngine();

    public FlightService() {
        loadSampleFlights();
    }

    private void loadSampleFlights() {

        flights.add(new Flight(
                "AI101", "Air India",
                "Bangalore", "Delhi",
                "2026-12-10", "06:30",
                4, 2, 5500, 12000
        ));

        flights.add(new Flight(
                "6E202", "IndiGo",
                "Bangalore", "Mumbai",
                "2026-12-11", "09:15",
                5, 2, 4200, 9500
        ));

        flights.add(new Flight(
                "UK303", "Vistara",
                "Delhi", "Bangalore",
                "2026-12-12", "14:00",
                4, 2, 6000, 14000
        ));

        flights.add(new Flight(
                "AI404", "Air India",
                "Mumbai", "Chennai",
                "2026-12-13", "18:45",
                3, 2, 4800, 10500
        ));

        flights.add(new Flight(
                "6E505", "IndiGo",
                "Bangalore", "Hyderabad",
                "2026-12-14", "11:30",
                5, 2, 3500, 8500
        ));
    }

    private String waitlistKey(Flight flight,
                               TravelClass travelClass) {

        return flight.getFlightNumber() + "-" + travelClass;
    }

    public List<Flight> getFlights() {
        return Collections.unmodifiableList(flights);
    }

    public Collection<Booking> getAllBookings() {
        return Collections.unmodifiableCollection(
                bookings.values()
        );
    }

    public Flight findFlight(String flightNumber) {

        for (Flight flight : flights) {
            if (flight.getFlightNumber().equalsIgnoreCase(
                    flightNumber.trim())) {
                return flight;
            }
        }

        return null;
    }

    public List<Flight> searchFlights(String source,
                                      String destination,
                                      String date) {

        List<Flight> results = new ArrayList<>();

        for (Flight flight : flights) {

            boolean sourceMatch = flight.getSource()
                    .equalsIgnoreCase(source.trim());

            boolean destinationMatch = flight.getDestination()
                    .equalsIgnoreCase(destination.trim());

            boolean dateMatch = flight.getDate()
                    .equals(date.trim());

            if (sourceMatch && destinationMatch && dateMatch) {
                results.add(flight);
            }
        }

        return results;
    }

    public double getFare(Flight flight,
                          TravelClass travelClass) {

        return pricingEngine.calculateFare(flight, travelClass);
    }

    public Booking bookFlight(Passenger passenger,
                              Flight flight,
                              TravelClass travelClass) {

        if (flight == null || !flights.contains(flight)) {
            throw new IllegalArgumentException("Invalid flight.");
        }

        double fare = pricingEngine.calculateFare(
                flight, travelClass
        );

        String pnr = PNRGenerator.generatePNR();

        Booking booking;

        if (flight.reserveSeat(travelClass)) {

            booking = new Booking(
                    pnr, passenger, flight,
                    travelClass, fare,
                    BookingStatus.CONFIRMED
            );

            pricingEngine.recordConfirmation(flight, travelClass);

        } else {

            booking = new Booking(
                    pnr, passenger, flight,
                    travelClass, fare,
                    BookingStatus.WAITLISTED
            );

            String key = waitlistKey(flight, travelClass);

            Queue<Booking> queue = waitlists.computeIfAbsent(
                    key, k -> new LinkedList<>()
            );

            queue.add(booking);
            booking.setWaitlistPosition(queue.size());
        }

        bookings.put(pnr, booking);

        return booking;
    }

    public Booking findBooking(String pnr) {
        if (pnr == null) {
            return null;
        }

        return bookings.get(pnr.trim().toUpperCase());
    }

    public List<Booking> getWaitlistedBookings(
            Flight flight, TravelClass travelClass) {

        String key = waitlistKey(flight, travelClass);

        Queue<Booking> queue = waitlists.get(key);

        if (queue == null) {
            return Collections.emptyList();
        }

        List<Booking> result = new ArrayList<>();
        int position = 1;

        for (Booking booking : queue) {
            if (booking.getStatus() == BookingStatus.WAITLISTED) {
                booking.setWaitlistPosition(position++);
                result.add(booking);
            }
        }

        return result;
    }

    public Booking cancelBooking(String pnr) {

        Booking booking = findBooking(pnr);

        if (booking == null) {
            throw new IllegalArgumentException("PNR not found.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Already cancelled.");
        }

        Flight flight = booking.getFlight();
        TravelClass travelClass = booking.getTravelClass();

        if (booking.getStatus() == BookingStatus.CONFIRMED) {

            flight.releaseSeat(travelClass);
            booking.setStatus(BookingStatus.CANCELLED);

            promoteWaitlistedPassenger(flight, travelClass);

        } else {

            Queue<Booking> queue = waitlists.get(
                    waitlistKey(flight, travelClass)
            );

            if (queue != null) {
                queue.remove(booking);
            }

            booking.setStatus(BookingStatus.CANCELLED);
            updateWaitlistPositions(flight, travelClass);
        }

        return booking;
    }

    private void promoteWaitlistedPassenger(
            Flight flight, TravelClass travelClass) {

        String key = waitlistKey(flight, travelClass);

        Queue<Booking> queue = waitlists.get(key);

        if (queue == null) {
            return;
        }

        while (!queue.isEmpty()) {

            Booking next = queue.poll();

            if (next.getStatus() != BookingStatus.WAITLISTED) {
                continue;
            }

            if (flight.reserveSeat(travelClass)) {

                double newFare = pricingEngine.calculateFare(
                        flight, travelClass
                );

                next.setFare(newFare);
                next.setStatus(BookingStatus.CONFIRMED);
                next.setWaitlistPosition(0);

                pricingEngine.recordConfirmation(
                        flight, travelClass
                );

                System.out.println(
                        "\n*** WAITLIST PROMOTION ***"
                );

                System.out.println(
                        "Passenger: " + next.getPassenger().getName()
                );

                System.out.println(
                        "PNR: " + next.getPnr()
                );

                System.out.printf(
                        "Updated fare: Rs. %.2f%n", newFare
                );

                System.out.println(
                        "Status: CONFIRMED"
                );

                break;
            }
        }

        updateWaitlistPositions(flight, travelClass);
    }

    private void updateWaitlistPositions(
            Flight flight, TravelClass travelClass) {

        Queue<Booking> queue = waitlists.get(
                waitlistKey(flight, travelClass)
        );

        if (queue == null) {
            return;
        }

        int position = 1;

        for (Booking booking : queue) {
            if (booking.getStatus() == BookingStatus.WAITLISTED) {
                booking.setWaitlistPosition(position++);
            }
        }
    }

    public double upgradeToBusiness(String pnr) {

        Booking booking = findBooking(pnr);

        if (booking == null) {
            throw new IllegalArgumentException("PNR not found.");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Only confirmed bookings can be upgraded."
            );
        }

        if (booking.getTravelClass() != TravelClass.ECONOMY) {
            throw new IllegalStateException(
                    "Only Economy passengers can upgrade."
            );
        }

        Flight flight = booking.getFlight();

        double businessFare = pricingEngine.calculateFare(
                flight, TravelClass.BUSINESS
        );

        double difference = Math.max(
                0, businessFare - booking.getFare()
        );

        if (flight.getAvailableSeats(TravelClass.BUSINESS) <= 0) {
            throw new IllegalStateException(
                    "No Business seats available."
            );
        }

        flight.reserveSeat(TravelClass.BUSINESS);
        flight.releaseSeat(TravelClass.ECONOMY);

        booking.setTravelClass(TravelClass.BUSINESS);
        booking.setFare(businessFare);

        pricingEngine.recordConfirmation(
                flight, TravelClass.BUSINESS
        );

        promoteWaitlistedPassenger(
                flight, TravelClass.ECONOMY
        );

        return difference;
    }

    public int getWaitlistCount(Flight flight,
                                TravelClass travelClass) {

        return getWaitlistedBookings(
                flight, travelClass
        ).size();
    }

    public PricingEngine getPricingEngine() {
        return pricingEngine;
    }
}