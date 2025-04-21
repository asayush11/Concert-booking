package src;

import java.time.LocalDateTime;
import java.util.List;
public class main {

    public static void main(String[] args){
        // Create concert ticket booking system instance
        ConcertTicketBookingSystem bookingSystem = ConcertTicketBookingSystem.getInstance();

        // Create concerts
        LocalDateTime dateTime = LocalDateTime.now().plusDays(30);
        Concert concert1 = bookingSystem.addConcert("Artist 1", "Venue 1", dateTime, 100);

        Concert concert2 = bookingSystem.addConcert("Artist 2", "Venue 2", LocalDateTime.now().plusDays(60), 50);

        // Create users
        User user1 = new User("U001", "John Doe", "john@example.com");
        User user2 = new User("U002", "Jane Smith", "jane@example.com");

        // Search concerts
        List<Concert> searchResults = bookingSystem.searchConcerts("Artist 1", "Venue 2", dateTime);
        System.out.println("Search Results:" + searchResults.size());
        for (Concert concert : searchResults) {
            System.out.println("Concert: " + concert.getArtist() + " at " + concert.getVenue() + " on " + concert.getDateTime());
        }

        // Book tickets
        Booking booking1 = bookingSystem.bookTickets(user1, concert1, 3, SeatType.REGULAR);

        Booking booking3 = bookingSystem.bookTickets(user1, concert1, 70, SeatType.REGULAR);

        Booking booking2 = bookingSystem.bookTickets(user2, concert2, 10, SeatType.VIP);

        // Cancel booking
        bookingSystem.cancelBooking(booking1.getId());

        // Book tickets again
        Booking booking4 = bookingSystem.bookTickets(user2, concert1, 70, SeatType.REGULAR);
    }

}
