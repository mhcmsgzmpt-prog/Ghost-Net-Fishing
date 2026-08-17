package de.finnk.ghostnet.repository;

import de.finnk.ghostnet.model.ReportingPerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportingPersonRepository extends JpaRepository<ReportingPerson, Long> {
}