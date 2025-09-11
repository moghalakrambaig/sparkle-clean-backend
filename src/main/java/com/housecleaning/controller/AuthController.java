package com.housecleaning.controller;

import com.housecleaning.model.AdminPassword;
import com.housecleaning.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // allow frontend React app
@RequiredArgsConstructor // ✅ generates constructor injection automatically
public class AuthController {

    private final AuthService authService;

    // ✅ DTO for login payload
    public record LoginRequest(String password) {}

    public record ApiResponse<T>(boolean success, T data, String message) {}

    // ✅ Login with password
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@RequestBody LoginRequest request) {
        boolean success = authService.validatePassword(request.password());

        if (success) {
            return ResponseEntity.ok(new ApiResponse<>(true, null, "Login successful"));
        } else {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, null, "Invalid password"));
        }
    }

    // ✅ Get all passwords
    @GetMapping("/getallpasswords")
    public ResponseEntity<ApiResponse<List<AdminPassword>>> getPasswords() {
        List<AdminPassword> passwords = authService.getAllPasswords();
        return ResponseEntity.ok(new ApiResponse<>(true, passwords, "Fetched successfully"));
    }

    // ✅ Add a new password
    @PostMapping("/passwords")
    public ResponseEntity<ApiResponse<AdminPassword>> addPassword(@RequestBody AdminPassword password) {
        AdminPassword saved = authService.addPassword(password);
        return ResponseEntity.ok(new ApiResponse<>(true, saved, "Password added successfully"));
    }

    // ✅ Delete password by ID
    @DeleteMapping("/passwords/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePassword(@PathVariable Long id) {
        boolean deleted = authService.deletePassword(id);
        if (deleted) {
            return ResponseEntity.ok(new ApiResponse<>(true, null, "Password deleted successfully"));
        } else {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(false, null, "Password not found"));
        }
    }
}
