package de.finnk.ghostnet.repository;

import de.finnk.ghostnet.model.GhostNet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GhostNetRepository extends JpaRepository<GhostNet, Long> {
}