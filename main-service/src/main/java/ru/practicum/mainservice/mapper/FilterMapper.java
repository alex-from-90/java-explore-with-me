package ru.practicum.mainservice.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.mainservice.dto.filter.AdminEventFilterDTO;
import ru.practicum.mainservice.dto.filter.EventFilterDTO;
import ru.practicum.mainservice.model.AdminEventFilter;
import ru.practicum.mainservice.model.EventFilter;

@Component
public class FilterMapper {
    public EventFilter eventFilter(EventFilterDTO dto) {
        EventFilter filter = new EventFilter();
        filter.setFrom(dto.getFrom());
        filter.setSize(dto.getSize());
        filter.setText(dto.getText());
        filter.setCategories(dto.getCategories());
        filter.setPaid(dto.getPaid());
        filter.setRangeEnd(dto.getRangeEnd());
        filter.setRangeStart(dto.getRangeStart());
        filter.setOnlyAvailable(dto.getOnlyAvailable());
        filter.setSort(dto.getSort());
        return filter;
    }

    public AdminEventFilter eventFilter(AdminEventFilterDTO dto) {
        AdminEventFilter filter = new AdminEventFilter();
        filter.setStates(dto.getStates());
        filter.setUsers(dto.getUsers());
        filter.setCategories(dto.getCategories());
        filter.setRangeEnd(dto.getRangeEnd());
        filter.setRangeStart(dto.getRangeStart());
        filter.setFrom(dto.getFrom());
        filter.setSize(dto.getSize());
        return filter;
    }
}
