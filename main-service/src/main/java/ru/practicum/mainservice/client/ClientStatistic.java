package ru.practicum.mainservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.mainservice.dto.HitDTO;
import ru.practicum.mainservice.dto.StatDTO;
import ru.practicum.mainservice.exception.APIException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ClientStatistic {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestTemplate rest;

    @Autowired
    public ClientStatistic(@Value("${statistic.server.url}") String serverUrl, RestTemplateBuilder builder) {
        rest = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void create(HttpServletRequest request) {
        HitDTO hitDto = new HitDTO(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now().format(DF)
        );
        log.info("send /hit to dto={}", hitDto);
        makeAndSendRequest(HttpMethod.POST, "/hit", null, hitDto, new ParameterizedTypeReference<Object>() {
        });
    }

    public ResponseEntity<List<StatDTO>> getStats(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        if (uris != null) {
            parameters.put("uris", String.join(",", uris));
        }
        if (start != null) {
            parameters.put("start", start);
        }
        if (end != null) {
            parameters.put("end", end);
        }
        parameters.put("unique", unique);
        return makeAndSendRequest(
                HttpMethod.GET,
                "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                parameters,
                null,
                new ParameterizedTypeReference<List<StatDTO>>() {
                });
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(HttpMethod method, String path, Map<String, Object> parameters, T body, ParameterizedTypeReference<R> typeReference) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<R> serverResponse;
        try {
            if (parameters != null) {
                serverResponse = rest.exchange(path, method, requestEntity, typeReference, parameters);
            } else {
                serverResponse = rest.exchange(path, method, requestEntity, typeReference);
            }
        } catch (HttpStatusCodeException e) {
            throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, e.getResponseBodyAsString(), "ClientStatistic error");
        }

        return serverResponse;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
