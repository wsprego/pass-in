package com.wsprego.passin.repositories;

import com.wsprego.passin.domain.attendee.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendeeRepository extends JpaRepository<Attendee, String> {
}
