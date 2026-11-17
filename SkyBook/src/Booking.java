public class Booking {

    private final String pnr;
    private final Passenger passenger;
    private final Flight flight;

    private TravelClass travelClass;
    private double fare;
    private BookingStatus status;
    private int waitlistPosition;

    public Booking(String pnr, Passenger passenger,
                   Flight flight, TravelClass travelClass,
                   double fare, BookingStatus status) {

        this.pnr = pnr;
        this.passenger = passenger;
        this.flight = flight;
        this.travelClass = travelClass;
        this.fare = fare;
        this.status = status;
        this.waitlistPosition = 0;
    }

    public String getPnr() {
        return pnr;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public TravelClass getTravelClass() {
        return travelClass;
    }

    public double getFare() {
        return fare;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public int getWaitlistPosition() {
        return waitlistPosition;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public void setTravelClass(TravelClass travelClass) {
        this.travelClass = travelClass;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public void setWaitlistPosition(int position) {
        this.waitlistPosition = position;
    }
}