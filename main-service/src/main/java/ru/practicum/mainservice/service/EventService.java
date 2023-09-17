package ru.practicum.mainservice.service;

import ru.practicum.mainservice.dto.event.CreateEventDTO;
import ru.practicum.mainservice.dto.event.EventDTO;
import ru.practicum.mainservice.dto.event.ShortEventDTO;
import ru.practicum.mainservice.dto.event.UpdateEventDTO;
import ru.practicum.mainservice.dto.filter.AdminEventFilterDTO;
import ru.practicum.mainservice.dto.filter.EventFilterDTO;
import ru.practicum.mainservice.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface EventService {
    Event getEventById(int eventId);

    EventDTO createEvent(int userId, CreateEventDTO dto);

    EventDTO updateEvent(int userId, int eventId, UpdateEventDTO dto);

    EventDTO updateAdminEvent(int eventId, UpdateEventDTO dto);

    void updateEvent(Event fromDB, UpdateEventDTO dto);

    List<ShortEventDTO> getAll(int userId, int from, int size);

    EventDTO getByInitiatorAndId(int userId, int eventId);

    EventDTO getPublishedEventById(int eventId);

    List<ShortEventDTO> findEvents(EventFilterDTO eventFilter);

    List<EventDTO> findEvents(AdminEventFilterDTO eventFilter);

    List<EventDTO> findAllByIds(List<Integer> eventIds);

    List<Event> findAllEventByIds(List<Integer> eventIds);

    void addStatistic(HttpServletRequest request);

    Map<Integer, Integer> getViews(List<Event> events);

    Map<Integer, Integer> getConfirmedRequests(List<Event> events);
}
