package com.wsprego.passin.services;

import com.wsprego.passin.domain.attendee.Attendee;
import com.wsprego.passin.domain.attendee.exceptions.AttendeeAlreadyExistException;
import com.wsprego.passin.domain.attendee.exceptions.AttendeeNotFoundException;
import com.wsprego.passin.domain.checkin.CheckIn;
import com.wsprego.passin.dto.attendee.AttendeeBadgeResponseDTO;
import com.wsprego.passin.dto.attendee.AttendeesDetails;
import com.wsprego.passin.dto.attendee.AttendeesListResponseDTO;
import com.wsprego.passin.dto.attendee.AttendeeBadgeDTO;
import com.wsprego.passin.repositories.AttendeeRepository;
import com.wsprego.passin.repositories.CheckInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendeeService {

    private final AttendeeRepository attendeeRepository;
    private final CheckInService checkInService;

    public List<Attendee> getAllAttendeesFromEvent(String eventId){
        return this.attendeeRepository.findByEventId(eventId);

    }

    public AttendeesListResponseDTO getEventAttendee(String eventId){
        List<Attendee> attendeeList = this.getAllAttendeesFromEvent(eventId);

        List<AttendeesDetails> attendeesDetailsList = attendeeList.stream().map(attendee -> {
            Optional<CheckIn> checkIn = this.checkInService.getCheckIn(attendee.getId());
            LocalDateTime checkInAt = checkIn.<LocalDateTime>map(CheckIn::getCreatedAt).orElse(null);
            return  new AttendeesDetails(attendee.getId(), attendee.getName(), attendee.getEmail(), attendee.getCreatedAt(), checkInAt);
        }).toList();

        return new AttendeesListResponseDTO(attendeesDetailsList);
    }

    public void verifyAttendeeSubscription(String email, String eventId){
       Optional<Attendee> isAttendeeResgistered = this.attendeeRepository.findByEventIdAndEmail(eventId, email);
       if(isAttendeeResgistered.isPresent()) throw new AttendeeAlreadyExistException("Attendee is already registered");

    }


    public Attendee resgisterAttendee(Attendee newAttendee){
        this.attendeeRepository.save(newAttendee);
        return newAttendee;
    }

    public void checkInAttendee(String attendeeId){

        Attendee attendee = this.getAttendee(attendeeId);
        this.checkInService.registerCheckIn(attendee);

    }

    private Attendee getAttendee(String attendeeId){
        return this.attendeeRepository.findById(attendeeId)
                .orElseThrow(
                        () -> new AttendeeNotFoundException("Attendee not found with ID"+ attendeeId));
    }

    public AttendeeBadgeResponseDTO getAttendeeBadge(String attendeeId, UriComponentsBuilder uriComponentsBuilder){
        Attendee attendee = this.getAttendee(attendeeId);

        var uri = uriComponentsBuilder.path("/attendees/{attendeeId}/check-in").buildAndExpand(attendeeId).toUri().toString();

        AttendeeBadgeDTO badgeDTO = new AttendeeBadgeDTO(attendee.getName(), attendee.getEmail(), uri, attendee.getEvent().getId());

        return new AttendeeBadgeResponseDTO(badgeDTO);

    }

}
