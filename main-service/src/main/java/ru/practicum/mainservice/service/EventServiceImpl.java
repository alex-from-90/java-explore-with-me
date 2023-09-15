package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.client.ClientStatistic;
import ru.practicum.mainservice.dto.StatDTO;
import ru.practicum.mainservice.enums.EventSort;
import ru.practicum.mainservice.enums.EventState;
import ru.practicum.mainservice.enums.StatusRequest;
import ru.practicum.mainservice.enums.UpdateEventState;
import ru.practicum.mainservice.exception.APIException;
import ru.practicum.mainservice.exception.BadRequestException;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
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
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ClientStatistic clientStatistic;

    public static Specification<Event> isOnlyTheFilter(AdminEventFilter filter) {
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

    public static Specification<Event> isOnlyTheFilter(EventFilter filter) {
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

    @Override
    @Transactional(readOnly = true)
    public Event getById(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found", eventId)));
    }

    @Override
    @Transactional
    public Event createEvent(int userId, int categoryId, Event event) {
        checkEventDate(event);
        Category category = categoryService.getById(categoryId);
        User initiator = userService.getUserById(userId);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        if (event.getLocation().getId() == null)
            locationRepository.save(event.getLocation());
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event updateEvent(int userId, Integer categoryId, Event event, UpdateEventState state) {
        Event fromDB = getById(event.getId());
        if (!fromDB.getInitiator().getId().equals(userId))
            throw new ConflictException("Only owner user can update event");
        if (EventState.PUBLISHED.equals(fromDB.getState()))
            throw new ConflictException("Only pending or canceled events can be changed");
        if (Objects.nonNull(state)) {
            switch (state) {
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
            }
        }
        checkEventDate(event);
        updateEvent(fromDB, event, categoryId);
        return fromDB;
    }

    private void checkEventDate(Event event) {
        if (event.getEventDate() != null && event.getEventDate().isBefore(LocalDateTime.now().plus(2, ChronoUnit.HOURS))) {
            throw new BadRequestException("Event date most been 2 hours after now");
        }
    }

    @Override
    @Transactional
    public Event updateAdminEvent(Integer categoryId, Event event, UpdateEventState state) {
        Event fromDB = getById(event.getId());
        if (state != null && !EventState.PENDING.equals(fromDB.getState())) {
            if (UpdateEventState.PUBLISH_EVENT.equals(state))
                throw new ConflictException("Only pending events can be published");
            if (UpdateEventState.REJECT_EVENT.equals(state))
                throw new ConflictException("Only pending events can be canceled");
        }
        checkEventDate(event);
        if (Objects.nonNull(state)) {
            switch (state) {
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    break;
            }
        }
        updateEvent(fromDB, event, categoryId);
        return fromDB;
    }

    @Override
    public void updateEvent(Event fromDB, Event event, Integer categoryId) {
        if (Objects.nonNull(categoryId)) {
            Category category = categoryService.getById(categoryId);
            fromDB.setCategory(category);
        }
        if (Objects.nonNull(event.getAnnotation()))
            fromDB.setAnnotation(event.getAnnotation());
        if (Objects.nonNull(event.getDescription()))
            fromDB.setDescription(event.getDescription());
        if (Objects.nonNull(event.getEventDate()))
            fromDB.setEventDate(event.getEventDate());
        if (Objects.nonNull(event.getLocation()) && !fromDB.getLocation().equals(event.getLocation())) {
            locationRepository.save(event.getLocation());
            fromDB.setLocation(event.getLocation());
        }
        if (Objects.nonNull(event.getPaid()))
            fromDB.setPaid(event.getPaid());
        if (Objects.nonNull(event.getParticipantLimit()))
            fromDB.setParticipantLimit(event.getParticipantLimit());
        if (Objects.nonNull(event.getRequestModeration()))
            fromDB.setRequestModeration(event.getRequestModeration());
        if (Objects.nonNull(event.getState()))
            fromDB.setState(event.getState());
        if (Objects.nonNull(event.getTitle()))
            fromDB.setTitle(event.getTitle());
        if (Objects.nonNull(event.getPublishedDate()))
            fromDB.setPublishedDate(event.getPublishedDate());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getAll(int userId, int from, int size) {
        Pageable pageable = new OffsetBasedPageRequest(from, size);
        return eventRepository.findAllByInitiatorId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Event getByInitiatorAndId(int userId, int eventId) {
        return eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found", eventId)));
    }

    @Override
    @Transactional(readOnly = true)
    public Event getPublishedEventById(int eventId) {
        Event event = getById(eventId);
        if (!EventState.PUBLISHED.equals(event.getState()))
            throw new NotFoundException(String.format("Event with id=%s was not found", eventId));
        return event;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findEvents(EventFilter eventFilter) {
        if (
                Objects.nonNull(eventFilter.getRangeStart())
                        && Objects.nonNull(eventFilter.getRangeEnd())
                        && eventFilter.getRangeStart().isAfter(eventFilter.getRangeEnd())
        ) throw new BadRequestException("range start after range end");
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
        return events;
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
    public Map<Integer, Integer> getConfirmedRequests(List<Event> events) {
        List<EventRequestsView> requestsViews = eventRepository.findAllRequestByEventAndStatus(events, StatusRequest.CONFIRMED);
        return requestsViews.stream().collect(Collectors.toMap(EventRequestsView::getEventId, EventRequestsView::getRequests));
    }

    @Override
    public List<Event> findEvents(AdminEventFilter eventFilter) {
        Pageable pageable = new OffsetBasedPageRequest(eventFilter.getFrom(), eventFilter.getSize());
        return eventRepository.findAll(isOnlyTheFilter(eventFilter), pageable).getContent();
    }

    @Override
    public List<Event> findAllByIds(List<Integer> eventIds) {
        return eventRepository.findAllById(eventIds);
    }

    @Override
    @Async
    public void addStatistic(HttpServletRequest request) {
        clientStatistic.create(request);
    }
}
