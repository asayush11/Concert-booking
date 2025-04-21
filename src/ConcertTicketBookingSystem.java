package src;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class ConcertTicketBookingSystem {
    private static ConcertTicketBookingSystem instance;
    private final List<Concert> concerts;
    private final Map<String, Booking> bookings;
    private final Lock lock = new ReentrantLock();

    private ConcertTicketBookingSystem() {
        concerts = new ArrayList<>();
        bookings = new ConcurrentHashMap<>();
    }

    public static synchronized ConcertTicketBookingSystem getInstance() {
        if (instance == null) {
            instance = new ConcertTicketBookingSystem();
        }
        return instance;
    }

    public Concert addConcert(String artist, String venue, LocalDateTime dateTime, int numberOfSeats) {
        List<Seat> seats = generateSeats(numberOfSeats);
        Concert concert = new Concert("C" + UUID.randomUUID().toString().substring(0,8), artist, venue, dateTime, seats);
        concerts.add(concert);
        return concert;
    }


    public List<Concert> searchConcerts(String artist, String venue, LocalDateTime dateTime) {
        return concerts.stream()
                .filter(concert -> concert.getArtist().equalsIgnoreCase(artist) ||
                        concert.getVenue().equalsIgnoreCase(venue) ||
                        concert.getDateTime().equals(dateTime))
                .collect(Collectors.toList());
    }

    public Booking bookTickets(User user, Concert concert, int numberOfSeats, SeatType seatType) {
        lock.lock();
            // Check seat availability and book seats
            List<Seat> seats = selectSeats(concert, numberOfSeats, seatType);
            if (seats == null || seats.isEmpty()) {
                System.out.println("No available seats of type " + seatType);
                lock.unlock();
                return null;
            }
            seats.forEach(Seat::book);

            // Create booking
            String bookingId = generateBookingId();
            Booking booking = new Booking(bookingId, user, concert, seats);
            bookings.put(bookingId, booking);

            // Process payment
            processPayment(booking);

            // Confirm booking
            booking.confirmBooking();

            System.out.println("Booking " + booking.getId() + " - " + booking.getSeats().size() + " seats booked");

            lock.unlock();
            return booking;
    }

    public void cancelBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking != null) {
            booking.cancelBooking();
            bookings.remove(bookingId);
        }
    }

    private void processPayment(Booking booking) {
        System.out.println("Pay " + booking.getTotalPrice());
    }

    private String generateBookingId() {
        return "BKG" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static List<Seat> selectSeats(Concert concert, int numberOfSeats, SeatType seatType) {
        List<Seat> availableSeats = concert.getSeats().stream()
                .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE && seat.getSeatType() == seatType)
                .limit(numberOfSeats)
                .toList();
        List<Seat> selectedSeats = new ArrayList<>(availableSeats);
        if(selectedSeats.size()<numberOfSeats) {
            return null;
        }
        return selectedSeats;
    }

    private static List<Seat> generateSeats(int numberOfSeats) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= numberOfSeats; i++) {
            String seatNumber = "S" + i;
            SeatType seatType = (i <= 10) ? SeatType.VIP : (i <= 30) ? SeatType.PREMIUM : SeatType.REGULAR;
            double price = (seatType == SeatType.VIP) ? 100.0 : (seatType == SeatType.PREMIUM) ? 75.0 : 50.0;
            seats.add(new Seat(seatNumber, seatType, price));
        }
        return seats;
    }
}
