package com.hiss.avalor_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private LocalDateTime expiryDate;

}
