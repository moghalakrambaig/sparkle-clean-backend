package com.housecleaning.controller;

import com.housecleaning.model.Booking;
import com.housecleaning.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bookings") // 🔑 removed the extra "/api"
@CrossOrigin(origins = {
        "https://sparklecleaning.vercel.app",
        "http://localhost:5173"
}) // Allow React frontend
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;

    // ✅ Generic API Response wrapper
    public record ApiResponse<T>(boolean success, T data, String message) {
    }

    // ✅ Create new booking
    @PostMapping
    public ResponseEntity<ApiResponse<Booking>> createBooking(@RequestBody Booking booking) {
        Booking created = bookingService.createBooking(booking);
        return ResponseEntity.ok(new ApiResponse<>(true, created, "Booking created successfully"));
    }

    // ✅ Get all bookings
    @GetMapping
    public ResponseEntity<ApiResponse<List<Booking>>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(new ApiResponse<>(true, bookings, "Bookings fetched successfully"));
    }

    // ✅ Update booking status
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Booking>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Optional<Booking> updated = bookingService.updateBookingStatus(id, status);
        return updated.map(booking -> ResponseEntity.ok(new ApiResponse<>(true, booking, "Booking status updated")))
                .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse<>(false, null, "Booking not found")));
    }

    // ✅ Delete booking by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBooking(@PathVariable Long id) {
        boolean deleted = bookingService.deleteBooking(id);
        if (deleted) {
            return ResponseEntity.ok(new ApiResponse<>(true, null, "Booking deleted successfully"));
        }
        return ResponseEntity.status(404)
                .body(new ApiResponse<>(false, null, "Booking not found"));
    }

    // ✅ Get booking by bookingNumber
    @GetMapping("/number/{bookingNumber}")
    public ResponseEntity<ApiResponse<Booking>> getBookingByNumber(@PathVariable String bookingNumber) {
        Optional<Booking> booking = bookingService.getBookingByNumber(bookingNumber);
        return booking.map(b -> ResponseEntity.ok(new ApiResponse<>(true, b, "Booking found")))
                .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse<>(false, null, "Booking not found")));
    }
}
