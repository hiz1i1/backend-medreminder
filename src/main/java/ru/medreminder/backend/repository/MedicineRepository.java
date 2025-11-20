package ru.medreminder.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import ru.medreminder.backend.model.Medicine;
import ru.medreminder.backend.model.User;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findAllByUser(User user);

    void deleteByIdAndUser(Long id, User user);

    boolean existsByIdAndUser(Long id, User user);
}
