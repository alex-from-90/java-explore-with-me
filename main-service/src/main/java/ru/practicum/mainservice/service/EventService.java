package ru.practicum.mainservice.service;

import ru.practicum.mainservice.enums.UpdateEventState;
import ru.practicum.mainservice.model.AdminEventFilter;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.EventFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface EventService {
    Event getById(int eventId);

    Event createEvent(int userId, int categoryId, Event event);

    Event updateEvent(int userId, Integer categoryId, Event event, UpdateEventState state);

    Event updateAdminEvent(Integer categoryId, Event event, UpdateEventState state);

    void updateEvent(Event fromDB, Event event, Integer categoryId);

    List<Event> getAll(int userId, int from, int size);

    Event getByInitiatorAndId(int userId, int eventId);

    Event getPublishedEventById(int eventId);

    List<Event> findEvents(EventFilter eventFilter);

    List<Event> findEvents(AdminEventFilter eventFilter);

    List<Event> findAllByIds(List<Integer> eventIds);

    void addStatistic(HttpServletRequest request);

    Map<Integer, Integer> getViews(List<Event> events);

    Map<Integer, Integer> getConfirmedRequests(List<Event> events);
}
