package com.wsprego.passin.controllers;

import com.wsprego.passin.dto.attendee.AttendeesListResponseDTO;
import com.wsprego.passin.dto.eventDto.EventIdDTO;
import com.wsprego.passin.dto.eventDto.EventRequestDTO;
import com.wsprego.passin.dto.eventDto.EventResponseDTO;
import com.wsprego.passin.services.AttendeeService;
import com.wsprego.passin.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final AttendeeService attendeeService;

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable String eventId) {
        EventResponseDTO event = this.eventService.getEventDetail(eventId);
        return ResponseEntity.ok(event);
    } //listar evento pelo id - list events by id

    @PostMapping
    public ResponseEntity<EventIdDTO> createEvent(@RequestBody EventRequestDTO body, UriComponentsBuilder uriComponentsBuilder) {
        EventIdDTO eventIdDTO = this.eventService.createEvent(body);

        var uri = uriComponentsBuilder.path("/events/{eventId}").buildAndExpand(eventIdDTO.eventId()).toUri();

        return ResponseEntity.created(uri).body(eventIdDTO);
    } //cria novos eventos - create new events

    @GetMapping("/attendees/{eventId}")
    public ResponseEntity<AttendeesListResponseDTO> getEventAttendees(@PathVariable String eventId) {
        AttendeesListResponseDTO attendeesListResponse = this.attendeeService.getEventAttendee(eventId);
        return ResponseEntity.ok(attendeesListResponse);
    }

}


