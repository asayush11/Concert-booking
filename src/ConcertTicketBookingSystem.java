package src;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConcertTicketBookingSystem {
    private static ConcertTicketBookingSystem instance;
    private final List<Concert> concerts;
    private final Map<String, Booking> bookings;
    private final Object lock = new Object();

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

    public void addConcert(Concert concert) {
        concerts.add(concert);
    }


    public List<Concert> searchConcerts(String artist, String venue, LocalDateTime dateTime) {
        return concerts.stream()
                .filter(concert -> concert.getArtist().equalsIgnoreCase(artist) &&
                        concert.getVenue().equalsIgnoreCase(venue) &&
                        concert.getDateTime().equals(dateTime))
                .collect(Collectors.toList());
    }

    public Booking bookTickets(User user, Concert concert, List<Seat> seats) {
        if(seats.isEmpty()) throw new RuntimeException("Enough seats are not there");
        synchronized (lock) {
            // Check seat availability and book seats
            for (Seat seat : seats) {
                if (seat.getStatus() != SeatStatus.AVAILABLE) {
                    throw new RuntimeException("Seat " + seat.getSeatNumber() + " is not available.");
                }
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

            return booking;
        }
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
        return "BKG" + UUID.randomUUID();
    }
}
