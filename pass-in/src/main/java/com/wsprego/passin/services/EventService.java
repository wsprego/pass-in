package com.wsprego.passin.services;

import com.wsprego.passin.domain.attendee.Attendee;
import com.wsprego.passin.domain.event.Event;
import com.wsprego.passin.domain.event.exceptions.EventNotFoundException;
import com.wsprego.passin.dto.eventDto.EventIdDTO;
import com.wsprego.passin.dto.eventDto.EventRequestDTO;
import com.wsprego.passin.dto.eventDto.EventResponseDTO;
import com.wsprego.passin.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepositorynt;
    private final AttendeeService attendeeService;

    public EventResponseDTO getEventDetail(String eventId){
        Event event = this.eventRepositorynt.findById(eventId)
                .orElseThrow(
                () -> new EventNotFoundException("Event not found with id"+ eventId));

        List<Attendee> attendeeList = this.attendeeService.getAllAttendeesFromEvent(eventId);
        return new EventResponseDTO(event, attendeeList.size());
    } //list events

    public EventIdDTO createEvent(EventRequestDTO eventDTO){
        Event newEvent= new Event();

        newEvent.setTitle(eventDTO.title());
        newEvent.setDetails(eventDTO.details());
        newEvent.setMaximumAttendees(eventDTO.maximumAttendees());
        newEvent.setSlug(this.createSlug(eventDTO.title()));

        this.eventRepositorynt.save(newEvent);
        return new EventIdDTO(newEvent.getId());
    } //create of events

    private String createSlug(String text){
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("[\\p{InCOMBINING_DIACRITICAL_MARKS}]", "")
                .replaceAll("[^\\w\\s]", "")
                .replaceAll("\\s+","-")
                .toLowerCase();
    } //tratamento de palavras, acentos e espaço. ex: São Paulo = sa~o-paulo

}
