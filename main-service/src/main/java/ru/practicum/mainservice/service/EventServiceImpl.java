package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.client.ClientStatistic;
import ru.practicum.mainservice.dto.StatDTO;
import ru.practicum.mainservice.dto.event.CreateEventDTO;
import ru.practicum.mainservice.dto.event.EventDTO;
import ru.practicum.mainservice.dto.event.ShortEventDTO;
import ru.practicum.mainservice.dto.event.UpdateEventDTO;
import ru.practicum.mainservice.dto.filter.AdminEventFilterDTO;
import ru.practicum.mainservice.dto.filter.EventFilterDTO;
import ru.practicum.mainservice.enums.EventSort;
import ru.practicum.mainservice.enums.EventState;
import ru.practicum.mainservice.enums.StatusRequest;
import ru.practicum.mainservice.enums.UpdateEventState;
import ru.practicum.mainservice.exception.APIException;
import ru.practicum.mainservice.mapper.EventMapper;
import ru.practicum.mainservice.mapper.LocationMapper;
import ru.practicum.mainservice.model.*;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.LocationRepository;
import ru.practicum.mainservice.util.OffsetBasedPageRequest;
import ru.practicum.mainservice.view.EventRequestsView;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ClientStatistic clientStatistic;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;

    @Override
    @Transactional(readOnly = true)
    public Event getEventById(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new APIException(
                        HttpStatus.NOT_FOUND,
                        String.format("Event with id=%s was not found", eventId),
                        "The required object was not found."));
    }

    @Override
    public EventDTO createEvent(int userId, CreateEventDTO dto) {
        checkEventDate(dto.getEventDate());
        Category category = categoryService.getCategoryById(dto.getCategory());
        User initiator = userService.getUserById(userId);
        Event event = eventMapper.toModel(dto);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        if (event.getLocation().getId() == null)
            locationRepository.save(event.getLocation());
        return eventMapper.toDto(eventRepository.save(event), 0, 0);
    }

    @Override
    public EventDTO updateEvent(int userId, int eventId, UpdateEventDTO dto) {
        Event fromDB = getEventById(eventId);
        if (!fromDB.getInitiator().getId().equals(userId))
            throw new APIException(
                    HttpStatus.CONFLICT,
                    "Only owner user can update event",
                    "For the requested operation the conditions are not met."
            );
        if (EventState.PUBLISHED.equals(fromDB.getState()))
            throw new APIException(
                    HttpStatus.CONFLICT,
                    "Only pending or canceled events can be changed",
                    "For the requested operation the conditions are not met."
            );
        if (Objects.nonNull(dto.getStateAction())) {
            switch (dto.getStateAction()) {
                case CANCEL_REVIEW:
                    fromDB.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    fromDB.setState(EventState.PENDING);
                    break;
            }
        }
        checkEventDate(dto.getEventDate());
        updateEvent(fromDB, dto);
        return toDto(Collections.singletonList(fromDB)).get(0);
    }

    private List<EventDTO> toDto(List<Event> events) {
        Map<Integer, Integer> views = getViews(events);
        Map<Integer, Integer> confirmedRequests = getConfirmedRequests(events);
        return events.stream().map(event -> eventMapper.toDto(
                event,
                views.getOrDefault(event.getId(), 0),
                confirmedRequests.getOrDefault(event.getId(), 0)
        )).collect(Collectors.toList());
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plus(2, ChronoUnit.HOURS))) {
            throw new APIException(
                    HttpStatus.BAD_REQUEST,
                    "Event date most been 2 hours after now",
                    "Incorrectly made request."
            );
        }
    }

    @Override
    public EventDTO updateAdminEvent(int eventId, UpdateEventDTO dto) {
        Event fromDB = getEventById(eventId);
        if (dto.getStateAction() != null && !EventState.PENDING.equals(fromDB.getState())) {
            if (UpdateEventState.PUBLISH_EVENT.equals(dto.getStateAction()))
                throw new APIException(
                        HttpStatus.CONFLICT,
                        "Only pending events can be published",
                        "For the requested operation the conditions are not met."
                );
            if (UpdateEventState.REJECT_EVENT.equals(dto.getStateAction()))
                throw new APIException(
                        HttpStatus.CONFLICT,
                        "Only pending events can be canceled",
                        "For the requested operation the conditions are not met."
                );
        }
        checkEventDate(dto.getEventDate());
        if (Objects.nonNull(dto.getStateAction())) {
            switch (dto.getStateAction()) {
                case REJECT_EVENT:
                    fromDB.setState(EventState.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    fromDB.setState(EventState.PUBLISHED);
                    fromDB.setPublishedDate(LocalDateTime.now());
                    break;
            }
        }
        updateEvent(fromDB, dto);
        return toDto(Collections.singletonList(fromDB)).get(0);
    }

    @Override
    public void updateEvent(Event fromDB, UpdateEventDTO dto) {
        if (Objects.nonNull(dto.getCategory())) {
            Category category = categoryService.getCategoryById(dto.getCategory());
            fromDB.setCategory(category);
        }
        if (Objects.nonNull(dto.getAnnotation()))
            fromDB.setAnnotation(dto.getAnnotation());
        if (Objects.nonNull(dto.getDescription()))
            fromDB.setDescription(dto.getDescription());
        if (Objects.nonNull(dto.getEventDate()))
            fromDB.setEventDate(dto.getEventDate());
        if (Objects.nonNull(dto.getLocation())) {
            Location location = locationMapper.fromDto(dto.getLocation());
            locationRepository.save(location);
            fromDB.setLocation(location);
        }
        if (Objects.nonNull(dto.getPaid()))
            fromDB.setPaid(dto.getPaid());
        if (Objects.nonNull(dto.getParticipantLimit()))
            fromDB.setParticipantLimit(dto.getParticipantLimit());
        if (Objects.nonNull(dto.getRequestModeration()))
            fromDB.setRequestModeration(dto.getRequestModeration());
        if (Objects.nonNull(dto.getTitle()))
            fromDB.setTitle(dto.getTitle());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShortEventDTO> getAll(int userId, int from, int size) {
        Pageable pageable = new OffsetBasedPageRequest(from, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        return events.stream().map(eventMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventDTO getByInitiatorAndId(int userId, int eventId) {
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new APIException(
                        HttpStatus.NOT_FOUND,
                        String.format("Event with id=%s was not found", eventId),
                        "The required object was not found."
                ));
        return toDto(Collections.singletonList(event)).get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDTO getPublishedEventById(int eventId) {
        Event event = getEventById(eventId);
        if (!EventState.PUBLISHED.equals(event.getState()))
            throw new APIException(
                    HttpStatus.NOT_FOUND,
                    String.format("Event with id=%s was not found", eventId),
                    "The required object was not found."
            );
        return toDto(Collections.singletonList(event)).get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShortEventDTO> findEvents(EventFilterDTO eventFilter) {
        if (
                Objects.nonNull(eventFilter.getRangeStart())
                        && Objects.nonNull(eventFilter.getRangeEnd())
                        && eventFilter.getRangeStart().isAfter(eventFilter.getRangeEnd())
        ) throw new APIException(
                HttpStatus.BAD_REQUEST,
                "range start after range end",
                "Incorrectly made request."
        );
        Pageable pageable;
        if (Objects.nonNull(eventFilter.getSort()) && eventFilter.getSort().equals(EventSort.EVENT_DATE))
            pageable = new OffsetBasedPageRequest(eventFilter.getFrom(), eventFilter.getSize(), Sort.by("eventDate"));
        else
            pageable = new OffsetBasedPageRequest(eventFilter.getFrom(), eventFilter.getSize());
        List<Event> events = eventRepository.findAll(isOnlyTheFilter(eventFilter), pageable).getContent();
        events = new LinkedList<>(events);
        if (Objects.nonNull(eventFilter.getSort()) && eventFilter.getSort().equals(EventSort.EVENT_DATE)) {
            final Map<Integer, Integer> views = getViews(events);
            events.sort(Comparator.comparingInt(o -> views.getOrDefault(o.getId(), 0)));
        }
        return events.stream().map(eventMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    public Map<Integer, Integer> getViews(List<Event> events) {
        List<String> uris = events.stream().map(event -> "/events/" + event.getId()).collect(Collectors.toList());
        log.info("uris - {}", uris);
        List<StatDTO> stats = clientStatistic.getStats(
                "2000-01-01 00:00:00",
                "3000-01-01 00:00:00",
                uris,
                true
        ).getBody();
        log.info("stats - {}", stats);
        if (Objects.isNull(stats))
            throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "ClientStatistic error", "ClientStatistic error");
        if (stats.isEmpty())
            return Collections.emptyMap();
        Map<Integer, Integer> views = new HashMap<>(uris.size() << 1);
        for (StatDTO state : stats) {
            String eventIdStr = state.getUri().substring(state.getUri().lastIndexOf('/') + 1);
            int eventId = Integer.parseInt(eventIdStr);
            views.put(eventId, state.getHits());
        }
        return views;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, Integer> getConfirmedRequests(List<Event> events) {
        List<EventRequestsView> requestsViews = eventRepository.findAllRequestByEventAndStatus(events, StatusRequest.CONFIRMED);
        return requestsViews.stream().collect(Collectors.toMap(EventRequestsView::getEventId, EventRequestsView::getRequests));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDTO> findEvents(AdminEventFilterDTO eventFilter) {
        Pageable pageable = new OffsetBasedPageRequest(eventFilter.getFrom(), eventFilter.getSize());
        List<Event> events = eventRepository.findAll(isOnlyTheFilter(eventFilter), pageable).getContent();
        return toDto(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDTO> findAllByIds(List<Integer> eventIds) {
        List<Event> events = eventRepository.findAllById(eventIds);
        return toDto(events);
    }

    @Override
    public List<Event> findAllEventByIds(List<Integer> eventIds) {
        return eventRepository.findAllById(eventIds);
    }

    @Override
    public void addStatistic(HttpServletRequest request) {
        clientStatistic.create(request);
    }

    private Specification<Event> isOnlyTheFilter(AdminEventFilterDTO filter) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (Objects.nonNull(filter.getUsers()) && !filter.getUsers().isEmpty())
                predicates.add(root.get("initiator").get("id").in(filter.getUsers()));
            if (Objects.nonNull(filter.getStates()) && !filter.getStates().isEmpty())
                predicates.add(root.get("state").in(filter.getStates()));
            if (Objects.nonNull(filter.getCategories()) && !filter.getCategories().isEmpty())
                predicates.add(root.get("category").get("id").in(filter.getCategories()));
            if (Objects.nonNull(filter.getRangeStart()))
                predicates.add(builder.greaterThanOrEqualTo(root.get("eventDate"), filter.getRangeStart()));
            if (Objects.nonNull(filter.getRangeEnd()))
                predicates.add(builder.lessThanOrEqualTo(root.get("eventDate"), filter.getRangeEnd()));
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Specification<Event> isOnlyTheFilter(EventFilterDTO filter) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (Objects.nonNull(filter.getText()) && !filter.getText().trim().isEmpty())
                predicates.add(builder.or(
                        builder.like(root.get("annotation"), '%' + filter.getText() + '%'),
                        builder.like(root.get("description"), '%' + filter.getText() + '%')
                ));
            if (Objects.nonNull(filter.getCategories()) && !filter.getCategories().isEmpty())
                predicates.add(root.get("category").get("id").in(filter.getCategories()));
            if (Objects.nonNull(filter.getPaid()))
                predicates.add(builder.equal(root.get("paid"), filter.getPaid()));
            if (Objects.nonNull(filter.getRangeStart()))
                predicates.add(builder.greaterThanOrEqualTo(root.get("eventDate"), filter.getRangeStart()));
            if (Objects.nonNull(filter.getRangeEnd()))
                predicates.add(builder.lessThanOrEqualTo(root.get("eventDate"), filter.getRangeEnd()));
            if (Objects.nonNull(filter.getOnlyAvailable()) && filter.getOnlyAvailable()) {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<Request> requestRoot = sub.from(Request.class);
                sub.select(builder.count(requestRoot)).where(builder.and(
                        builder.equal(requestRoot.get("event").get("id"), root.get("id")),
                        builder.equal(requestRoot.get("status"), StatusRequest.CONFIRMED.name())
                ));
                predicates.add(builder.greaterThan(root.get("participantLimit"), sub));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
