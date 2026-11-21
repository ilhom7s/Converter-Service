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
@Table(name = "outbox_message")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OutBoxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "sent_at")
    private Instant sendAt;
    @EqualsAndHashCode.Include
    @Column(name = "play_load",nullable = false)
    private String playLoad;
    @EqualsAndHashCode.Include
    @Column(name = "publish_at")
    private Instant publishAt;
    private boolean sent = false;
    private int retry = 0;
    @EqualsAndHashCode.Include
    @Column(name = "user_id",unique = true,nullable = false)
    private Long userId;
    @Column(name = "last_retry_attempt")
    private Instant lastRetryAttempt;

}
