package com.housecleaning.service;

import com.housecleaning.model.AdminPassword;
import com.housecleaning.repository.AdminPasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminPasswordRepository repo;

    // ✅ Validate login (optimized query)
    public boolean validatePassword(String password) {
        return repo.findAll().stream()
                .anyMatch(p -> p.getPassword().equals(password));
    }

    // ✅ Fetch all admin passwords
    public List<AdminPassword> getAllPasswords() {
        return repo.findAll();
    }

    // ✅ Add a new password
    public AdminPassword addPassword(AdminPassword password) {
        return repo.save(password);
    }

    // ✅ Delete a password by ID (optimized)
    public boolean deletePassword(Long id) {
        try {
            repo.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
