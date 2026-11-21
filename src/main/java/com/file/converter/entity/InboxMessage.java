package com.file.converter.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "consumer_inbox")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;
    @Column(name = "message_id", nullable = false, updatable = false,unique = true)
    @EqualsAndHashCode.Include
    private String messageId;
    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String topic;
    @Column(name = "play_load",nullable = false)
    @EqualsAndHashCode.Include
    private String payLoad;
    @Column(name = "processed_at")
    private Instant processedAt;
    private boolean proceed = false;
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    @Column(name = "received_at")
    private Instant receivedAt = Instant.now();
}
