package ru.practicum.mainservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.enums.StatusRequest;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.view.EventRequestsView;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {
    List<Event> findAllByInitiatorId(int initiatorId, Pageable pageable);

    Optional<Event> findByInitiatorIdAndId(int initiatorId, int eventId);

    @Query("select count(1) as requests, r.event.id as eventId from Request r where r.status = :status and r.event in :events group by r.event.id")
    List<EventRequestsView> findAllRequestByEventAndStatus(List<Event> events, StatusRequest status);
}
