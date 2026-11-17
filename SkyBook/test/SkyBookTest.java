
public class SkyBookTest {

    private static int passed = 0;

    private static void check(boolean condition, String message) {

        if (!condition) {
            throw new AssertionError("FAILED: " + message);
        }

        passed++;

        System.out.println("[PASS] " + message);
    }

    private static Passenger passenger(String name) {
        return new Passenger(
                name,
                20,
                name.toLowerCase() + "@test.com"
        );
    }

    public static void main(String[] args) {

        System.out.println("\n==================================");
        System.out.println("       SKYBOOK TEST SUITE");
        System.out.println("==================================");

        FlightService service = new FlightService();

        Flight flight = service.findFlight("AI101");

        // TEST 1: Flight search
        check(
                flight != null,
                "Flight search"
        );

        // TEST 2: Initial seat capacity
        check(
                flight.getAvailableSeats(TravelClass.ECONOMY) == 4,
                "Initial Economy capacity"
        );

        // TEST 3: First booking
        Booking b1 = service.bookFlight(
                passenger("Arun"),
                flight,
                TravelClass.ECONOMY
        );

        check(
                b1.getStatus() == BookingStatus.CONFIRMED,
                "First booking confirmation"
        );

        // TEST 4: Second booking
        Booking b2 = service.bookFlight(
                passenger("Rahul"),
                flight,
                TravelClass.ECONOMY
        );

        check(
                b2.getStatus() == BookingStatus.CONFIRMED,
                "Second booking confirmation"
        );

        // TEST 5: Dynamic pricing
        Booking b3 = service.bookFlight(
                passenger("Kiran"),
                flight,
                TravelClass.ECONOMY
        );

        check(
                b3.getFare() == 6050.0,
                "Dynamic fare increases by 10% after two bookings"
        );

        // TEST 6: Fourth booking
        Booking b4 = service.bookFlight(
                passenger("Vijay"),
                flight,
                TravelClass.ECONOMY
        );

        check(
                b4.getStatus() == BookingStatus.CONFIRMED,
                "Fourth booking confirmation"
        );

        // TEST 7: Full capacity and waitlisting
        Booking b5 = service.bookFlight(
                passenger("Suresh"),
                flight,
                TravelClass.ECONOMY
        );

        check(
                b5.getStatus() == BookingStatus.WAITLISTED,
                "Waitlist when Economy seats are full"
        );

        // TEST 8: Waitlist position
        check(
                b5.getWaitlistPosition() == 1,
                "Initial waitlist position"
        );

        // TEST 9: PNR lookup
        check(
                service.findBooking(b1.getPnr()) == b1,
                "PNR-based booking lookup"
        );

        // TEST 10: Cancellation
        service.cancelBooking(b1.getPnr());

        check(
                b1.getStatus() == BookingStatus.CANCELLED,
                "Booking cancellation"
        );

        // TEST 11: Automatic FIFO promotion
        check(
                b5.getStatus() == BookingStatus.CONFIRMED,
                "Automatic waitlist promotion"
        );

        // TEST 12: Seat availability after promotion
        check(
                flight.getAvailableSeats(TravelClass.ECONOMY) == 0,
                "Seat count maintained after promotion"
        );

        // TEST 13: Business upgrade
        service.upgradeToBusiness(b2.getPnr());

        check(
                b2.getTravelClass() == TravelClass.BUSINESS,
                "Economy to Business upgrade"
        );

        // TEST 14: Business seat occupied
        check(
                flight.getOccupied(TravelClass.BUSINESS) == 1,
                "Business seat allocation"
        );

        // TEST 15: Economy seat released
        check(
                flight.getOccupied(TravelClass.ECONOMY) == 3,
                "Economy seat released after upgrade"
        );

        // TEST 16: Invalid PNR
        check(
                service.findBooking("INVALID") == null,
                "Invalid PNR handling"
        );

        // TEST 17: Fill the released Economy seat
        Booking filler = service.bookFlight(
                passenger("Ramesh"),
                flight,
                TravelClass.ECONOMY
        );

        check(
                filler.getStatus() == BookingStatus.CONFIRMED,
                "Booking uses released Economy seat"
        );

        // TEST 18: Additional waitlisted booking
        Booking b6 = service.bookFlight(
                passenger("Manoj"),
                flight,
                TravelClass.ECONOMY
        );

        check(
                b6.getStatus() == BookingStatus.WAITLISTED,
                "Additional waitlisted booking"
        );

        // TEST 19: Cancellation of waitlisted booking
        service.cancelBooking(b6.getPnr());

        check(
                b6.getStatus() == BookingStatus.CANCELLED,
                "Waitlisted booking cancellation"
        );

        // TEST 20: Duplicate cancellation protection
        boolean duplicateBlocked = false;

        try {
            service.cancelBooking(b6.getPnr());
        } catch (IllegalStateException e) {
            duplicateBlocked = true;
        }

        check(
                duplicateBlocked,
                "Duplicate cancellation prevention"
        );

        System.out.println("\n==================================");
        System.out.println("TESTS PASSED: " + passed);
        System.out.println("TESTS FAILED: 0");
        System.out.println("RESULT: ALL TESTS PASSED");
        System.out.println("==================================");
    }
}
