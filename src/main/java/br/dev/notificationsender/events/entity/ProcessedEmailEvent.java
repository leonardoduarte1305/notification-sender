package br.dev.notificationsender.events.entity;

import br.dev.notificationsender.events.contratos.enumx.EventStatus;
import br.dev.notificationsender.events.contratos.enumx.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;

@Slf4j
@Getter
@Setter
@Entity
@Table(name = "processed_email_events")
@NoArgsConstructor
public class ProcessedEmailEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, updatable = false, unique = true)
    private UUID eventId;

    @Enumerated(value = STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    @Enumerated(value = STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public ProcessedEmailEvent(UUID eventId, EventType eventType, EventStatus status) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.processedAt = Instant.now();
        this.status = status;
    }

    public void marcarComoFinalizado(EventType eventType) {
        this.eventType = eventType;
        this.processedAt = Instant.now();
        this.status = EventStatus.FINISHED;
    }

    public void marcarComoFalha(EventType eventType) {
        this.eventType = eventType;
        this.processedAt = Instant.now();
        this.status = EventStatus.FAILED;
    }

}
