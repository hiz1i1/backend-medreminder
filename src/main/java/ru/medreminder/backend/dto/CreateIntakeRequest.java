package ru.medreminder.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

// DTO для создания нового приёма
@Data
public class CreateIntakeRequest {

    // ID лекарства, к которому относится приём
    private Long medicineId;

    // Когда нужно принять
    private LocalDateTime scheduledAt;
}
