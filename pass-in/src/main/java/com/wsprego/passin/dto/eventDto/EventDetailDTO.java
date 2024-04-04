package com.wsprego.passin.dto.eventDto;

public record EventDetailDTO(String id,
                             String title,
                             String details,
                             String slug,
                             Integer maximumAttendees,
                             Integer attendeesAmount) {



}
