package br.dev.notificationsender.events.repository;

import br.dev.notificationsender.events.entity.ProcessedEmailEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProcessedEmailEventRepository extends JpaRepository<ProcessedEmailEvent, Long> {

    Optional<ProcessedEmailEvent> findByEventId(UUID eventId);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO processed_email_events (event_id, event_type, processed_at, status, version)
            VALUES (:eventId, :eventType, :processedAt, 'PROCESSING', 0)
            ON CONFLICT (event_id) DO NOTHING
            """, nativeQuery = true)
    int reserveNewEvent(@Param("eventId") UUID eventId,
                        @Param("eventType") String eventType,
                        @Param("processedAt") Instant processedAt);

}
