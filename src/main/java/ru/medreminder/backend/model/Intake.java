package ru.medreminder.backend.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "intakes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Intake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Запланированное время приёма
    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    // Принято ли лекарство
    @Builder.Default
    private boolean taken = false;

    // Когда реально было принято (если taken = true)
    private LocalDateTime takenAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
