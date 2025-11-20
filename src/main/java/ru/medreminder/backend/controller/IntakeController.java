package ru.medreminder.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.medreminder.backend.dto.CreateIntakeRequest;
import ru.medreminder.backend.model.Intake;
import ru.medreminder.backend.model.Medicine;
import ru.medreminder.backend.repository.IntakeRepository;
import ru.medreminder.backend.repository.MedicineRepository;
import ru.medreminder.backend.service.CurrentUserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/intakes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IntakeController {

    private final IntakeRepository intakeRepository;
    private final MedicineRepository medicineRepository;
    private final CurrentUserService currentUserService;

    @GetMapping("/today")
    public List<Intake> getTodayIntakes() {
        var user = currentUserService.getCurrentUser();

        LocalDate today = LocalDate.now();
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = today.atTime(LocalTime.MAX);

        return intakeRepository.findByScheduledAtBetweenAndUser(from, to, user);
    }

    @PostMapping
    public ResponseEntity<Intake> create(@RequestBody CreateIntakeRequest request) {
        var user = currentUserService.getCurrentUser();

        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .filter(med -> med.getUser().getId().equals(user.getId()))
                .orElse(null);

        if (medicine == null) {
            return ResponseEntity.badRequest().build();
        }

        Intake intake = Intake.builder()
                .medicine(medicine)
                .scheduledAt(request.getScheduledAt())
                .taken(false)
                .user(user)
                .build();

        return ResponseEntity.ok(intakeRepository.save(intake));
    }

    @GetMapping
    public List<Intake> getAll() {
        var user = currentUserService.getCurrentUser();
        return intakeRepository.findAllByUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Intake> getById(@PathVariable Long id) {
        var user = currentUserService.getCurrentUser();
        return intakeRepository.findById(id)
                .filter(in -> in.getUser().getId().equals(user.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/take")
    public ResponseEntity<Intake> markAsTaken(@PathVariable Long id) {
        var user = currentUserService.getCurrentUser();

        return intakeRepository.findById(id)
                .filter(in -> in.getUser().getId().equals(user.getId()))
                .map(intake -> {
                    intake.setTaken(true);
                    intake.setTakenAt(LocalDateTime.now());
                    return ResponseEntity.ok(intakeRepository.save(intake));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Intake> update(
            @PathVariable Long id,
            @RequestBody CreateIntakeRequest request) {

        var user = currentUserService.getCurrentUser();

        var intake = intakeRepository.findById(id)
                .filter(in -> in.getUser().getId().equals(user.getId()))
                .orElse(null);

        if (intake == null) {
            return ResponseEntity.notFound().build();
        }

        var medicine = medicineRepository.findById(request.getMedicineId())
                .filter(med -> med.getUser().getId().equals(user.getId()))
                .orElse(null);

        if (medicine == null) {
            return ResponseEntity.badRequest().build();
        }

        intake.setScheduledAt(request.getScheduledAt());
        intake.setMedicine(medicine);

        return ResponseEntity.ok(intakeRepository.save(intake));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        var user = currentUserService.getCurrentUser();

        var intake = intakeRepository.findById(id)
                .filter(in -> in.getUser().getId().equals(user.getId()))
                .orElse(null);

        if (intake == null) {
            return ResponseEntity.notFound().build();
        }

        intakeRepository.delete(intake);
        return ResponseEntity.noContent().build();
    }
}
