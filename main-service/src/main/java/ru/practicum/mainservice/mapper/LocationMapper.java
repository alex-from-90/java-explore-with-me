package ru.practicum.mainservice.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.mainservice.dto.location.CreateLocationDTO;
import ru.practicum.mainservice.dto.location.LocationDTO;
import ru.practicum.mainservice.model.Location;

@Component
public class LocationMapper {
    public Location fromDto(CreateLocationDTO dto) {
        if (dto == null)
            return null;
        Location location = new Location();
        location.setLon(dto.getLon());
        location.setLat(dto.getLat());
        return location;
    }

    public LocationDTO toModel(Location location) {
        if (location == null)
            return null;
        LocationDTO dto = new LocationDTO();
        dto.setId(location.getId());
        dto.setLon(location.getLon());
        dto.setLat(location.getLat());
        return dto;
    }
}
