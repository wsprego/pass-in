package com.wsprego.passin.services;

import com.wsprego.passin.domain.attendee.Attendee;
import com.wsprego.passin.domain.checkin.CheckIn;
import com.wsprego.passin.domain.checkin.Exception.CheckInAlreadyExistsException;
import com.wsprego.passin.repositories.CheckInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final CheckInRepository checkInRepository;

    public void registerCheckIn(Attendee attendee){

        this.verifyCheckInExists(attendee.getId());
        CheckIn newCheckIn = new CheckIn();

        newCheckIn.setAttendee(attendee);
        newCheckIn.setCreatedAt(LocalDateTime.now());
        this.checkInRepository.save(newCheckIn);
    }

    private void verifyCheckInExists(String attendeeId){
        Optional<CheckIn> isCheckedIn = this.getCheckIn(attendeeId);
        if(isCheckedIn.isPresent()) throw new CheckInAlreadyExistsException("Attendee already checked in");

    }

    public Optional<CheckIn> getCheckIn(String attendeeId){
        return  this.checkInRepository.findByAttendeeId(attendeeId);
    }

}
