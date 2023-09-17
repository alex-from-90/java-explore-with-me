package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.enums.StatusRequest;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.Request;
import ru.practicum.mainservice.model.User;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findAllByRequester(User requester);

    List<Request> findAllByEvent(Event event);

    @Query("select count(1) from Request r where r.event.id = :eventId and r.status in :status")
    long getEventRequestCountByStatus(int eventId, StatusRequest status);

    long countByEventAndRequesterAndStatusIn(Event event, User requester, List<StatusRequest> pending);
}
