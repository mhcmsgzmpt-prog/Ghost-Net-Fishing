package de.finnk.ghostnet.repository;

import de.finnk.ghostnet.model.RecoveringPerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecoveringPersonRepository extends JpaRepository<RecoveringPerson, Long> {
}