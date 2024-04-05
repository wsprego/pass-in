package com.wsprego.passin.services;

import com.wsprego.passin.domain.attendee.Attendee;
import com.wsprego.passin.domain.event.Event;
import com.wsprego.passin.domain.event.exceptions.EventFullException;
import com.wsprego.passin.domain.event.exceptions.EventNotFoundException;
import com.wsprego.passin.dto.attendee.AttendeeIdDTO;
import com.wsprego.passin.dto.attendee.AttendeeRequestDTO;
import com.wsprego.passin.dto.eventDto.EventIdDTO;
import com.wsprego.passin.dto.eventDto.EventRequestDTO;
import com.wsprego.passin.dto.eventDto.EventResponseDTO;
import com.wsprego.passin.repositories.AttendeeRepository;
import com.wsprego.passin.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepositorynt;
    private final AttendeeService attendeeService;

    public EventResponseDTO getEventDetail(String eventId){

        Event event = this.getEventById(eventId);

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
    } //create events - criar evento

    public AttendeeIdDTO registerAttendeeOnEvent(String eventId, AttendeeRequestDTO attendeeRequestDTO){
        this.attendeeService.verifyAttendeeSubscription(attendeeRequestDTO.email(), eventId);

        Event event = this.getEventById(eventId);

        List<Attendee> attendeeList = this.attendeeService.getAllAttendeesFromEvent(eventId);

        if(event.getMaximumAttendees() <= attendeeList.size()) throw new EventFullException("Event is full");

        //passa dados do participante e qual é o evento
        Attendee newAttendee = new Attendee();
        newAttendee.setName(attendeeRequestDTO.name());
        newAttendee.setEmail(attendeeRequestDTO.email());
        newAttendee.setEvent(event);
        newAttendee.setCreatedAt(LocalDateTime.now());
        this.attendeeService.resgisterAttendee(newAttendee);

        return new AttendeeIdDTO(newAttendee.getId());

    } //verify if attendee exist the in event and the maximum of attendees is not full
    // verifica se o participante existe no evento e o numero maximo de paricipantes não foi utrapassado

    private Event getEventById(String eventId){
        return this.eventRepositorynt.findById(eventId)
                .orElseThrow(
                () -> new EventNotFoundException("Event not found with id"+ eventId));
    } //pegar o id do evento e passar para outros metodos

    private String createSlug(String text){
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("[\\p{InCOMBINING_DIACRITICAL_MARKS}]", "")
                .replaceAll("[^\\w\\s]", "")
                .replaceAll("\\s+","-")
                .toLowerCase();
    } //tratamento de palavras, acentos e espaço. ex: São Paulo = sa~o-paulo

}
