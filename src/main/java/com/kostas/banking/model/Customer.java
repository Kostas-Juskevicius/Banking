package com.kostas.banking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Check(constraints = "date_of_birth <= CURRENT_DATE")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 60) // NOTE: BCrypt hashes are always 60 chars
    private String passwordHash;

    public void setPassword(String plainPassword) {
        this.passwordHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public boolean verifyPassword(String plainPassword) {
        return BCrypt.checkpw(plainPassword, this.passwordHash);
    }
}
