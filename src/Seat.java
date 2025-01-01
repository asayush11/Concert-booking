package src;

public class Seat {
    private final String seatNumber;
    private final SeatType seatType;
    private final double price;
    private SeatStatus status;

    public Seat(String seatNumber, SeatType seatType, double price) {
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.price = price;
        this.status = SeatStatus.AVAILABLE;
    }

    public synchronized void book() {
        if (status == SeatStatus.AVAILABLE) {
            status = SeatStatus.BOOKED;
        } else {
            throw new RuntimeException("Seat is already booked or reserved.");
        }
    }

    public synchronized void release() {
        if (status == SeatStatus.BOOKED) {
            status = SeatStatus.AVAILABLE;
        }
    }

    public double getPrice() {
        return price;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public SeatStatus getStatus() {
        return status;
    }
}
