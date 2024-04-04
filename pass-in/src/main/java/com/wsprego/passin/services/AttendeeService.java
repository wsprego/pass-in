package com.wsprego.passin.services;

import com.wsprego.passin.domain.attendee.Attendee;
import com.wsprego.passin.domain.checkin.CheckIn;
import com.wsprego.passin.dto.attendee.AttendeesDetails;
import com.wsprego.passin.dto.attendee.AttendeesListResponseDTO;
import com.wsprego.passin.repositories.AttendeeRepository;
import com.wsprego.passin.repositories.CheckInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendeeService {

    private final AttendeeRepository attendeeRepository;
    private final CheckInRepository checkInRepository;

    public List<Attendee> getAllAttendeesFromEvent(String eventId){
        return this.attendeeRepository.findByEventId(eventId);

    }

    public AttendeesListResponseDTO getEventAttendee(String eventId){
        List<Attendee> attendeeList = this.getAllAttendeesFromEvent(eventId);

        List<AttendeesDetails> attendeesDetailsList = attendeeList.stream().map(attendee -> {
            Optional<CheckIn> checkIn = this.checkInRepository.findByAttendeeId(attendee.getId());
            LocalDateTime checkInAt = checkIn.<LocalDateTime>map(CheckIn::getCreatedAt).orElse(null);
            return  new AttendeesDetails(attendee.getId(), attendee.getName(), attendee.getEmail(), attendee.getCreatedAt(), checkInAt);
        }).toList();

        return new AttendeesListResponseDTO(attendeesDetailsList);
    }

}
