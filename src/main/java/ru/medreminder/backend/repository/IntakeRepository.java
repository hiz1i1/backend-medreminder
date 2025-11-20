package ru.medreminder.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.medreminder.backend.model.Intake;
import ru.medreminder.backend.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface IntakeRepository extends JpaRepository<Intake, Long> {

    // Все приёмы в диапазоне дат (для "сегодня")
    List<Intake> findByScheduledAtBetween(LocalDateTime from, LocalDateTime to);

    void deleteByMedicineId(Long medicineId);

    List<Intake> findByScheduledAtBetweenAndUser(LocalDateTime from,
                                                 LocalDateTime to,
                                                 User user);

    List<Intake> findAllByUser(User user);

    boolean existsByIdAndUser(Long id, User user);

    void deleteByMedicineIdAndUser(Long medicineId, User user);

}
