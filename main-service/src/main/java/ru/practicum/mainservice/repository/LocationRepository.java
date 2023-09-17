package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
}
