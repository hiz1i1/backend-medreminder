package ru.medreminder.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.medreminder.backend.model.Medicine;
import ru.medreminder.backend.repository.MedicineRepository;
import ru.medreminder.backend.repository.IntakeRepository;
import ru.medreminder.backend.service.CurrentUserService;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MedicineController {

    private final MedicineRepository medicineRepository;
    private final IntakeRepository intakeRepository;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<Medicine> getAll() {
        var user = currentUserService.getCurrentUser();
        return medicineRepository.findAllByUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getById(@PathVariable Long id) {
        var user = currentUserService.getCurrentUser();

        return medicineRepository.findById(id)
                .filter(med -> med.getUser().getId().equals(user.getId()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Medicine create(@RequestBody Medicine medicine) {
        var user = currentUserService.getCurrentUser();

        if (medicine.getId() == null) {
            medicine.setActive(true);
        }
        medicine.setUser(user);
        return medicineRepository.save(medicine);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Medicine> deactivate(@PathVariable Long id) {
        var user = currentUserService.getCurrentUser();

        return medicineRepository.findById(id)
                .filter(med -> med.getUser().getId().equals(user.getId()))
                .map(med -> {
                    med.setActive(false);
                    return ResponseEntity.ok(medicineRepository.save(med));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        var user = currentUserService.getCurrentUser();

        var medicine = medicineRepository.findById(id)
                .filter(med -> med.getUser().getId().equals(user.getId()))
                .orElse(null);

        if (medicine == null) {
            return ResponseEntity.notFound().build();
        }

        intakeRepository.deleteByMedicineIdAndUser(id, user);
        medicineRepository.delete(medicine);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medicine> update(
            @PathVariable Long id,
            @RequestBody Medicine updated) {

        var user = currentUserService.getCurrentUser();

        return medicineRepository.findById(id)
                .filter(med -> med.getUser().getId().equals(user.getId()))
                .map(med -> {
                    med.setName(updated.getName());
                    med.setDosage(updated.getDosage());
                    med.setDescription(updated.getDescription());
                    med.setActive(updated.isActive());
                    return ResponseEntity.ok(medicineRepository.save(med));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
