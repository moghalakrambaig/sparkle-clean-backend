package com.housecleaning.service;

import com.housecleaning.model.Booking;
import com.housecleaning.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;

    // ✅ Create new booking
    public Booking createBooking(Booking booking) {
        booking.setStatus(Booking.Status.Pending);

        if (booking.getBookingNumber() == null || booking.getBookingNumber().isBlank()) {
            booking.setBookingNumber(generateBookingNumber());
        }

        Booking saved = bookingRepository.save(booking);
        log.info("✅ New booking created: {}", saved.getBookingNumber());
        return saved;
    }

    // ✅ Get all bookings
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // ✅ Update booking status
    public Optional<Booking> updateBookingStatus(Long id, String status) {
        return bookingRepository.findById(id).flatMap(booking -> {
            return normalizeStatus(status).map(newStatus -> {
                booking.setStatus(newStatus);
                Booking updated = bookingRepository.save(booking);
                log.info("✅ Updated booking {} to status {}", updated.getBookingNumber(), newStatus);
                return updated;
            });
        });
    }

    // ✅ Delete booking by ID
    public boolean deleteBooking(Long id) {
        try {
            bookingRepository.deleteById(id);
            log.info("🗑️ Deleted booking with ID {}", id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            log.warn("❌ Attempted to delete non-existing booking ID {}", id);
            return false;
        }
    }

    // ✅ Get booking by bookingNumber
    public Optional<Booking> getBookingByNumber(String bookingNumber) {
        return bookingRepository.findByBookingNumber(bookingNumber);
    }

    // --- Helper: Generate a unique booking number ---
    private String generateBookingNumber() {
        return "SPK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // --- Helper: Normalize status safely ---
    private Optional<Booking.Status> normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Booking.Status.valueOf(
                status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase()
            ));
        } catch (IllegalArgumentException e) {
            log.warn("❌ Invalid status value provided: {}", status);
            return Optional.empty();
        }
    }
}
