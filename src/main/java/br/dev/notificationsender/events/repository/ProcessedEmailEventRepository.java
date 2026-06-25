package br.dev.notificationsender.events.repository;

import br.dev.notificationsender.events.contratos.enumx.EventStatus;
import br.dev.notificationsender.events.entity.ProcessedEmailEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProcessedEmailEventRepository extends JpaRepository<ProcessedEmailEvent, Long> {

    boolean existsByEventIdAndStatus(UUID eventId, EventStatus finished);

    Optional<ProcessedEmailEvent> findByEventId(UUID eventId);

}
