package com.file.converter.entity;



import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
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
public class OutBoxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    private Instant sendAt;
    private String playLoad;
    private Instant publishAt;
    private boolean sent = false;
    private int retry = 0;
    private Long userId;
    private Instant lastRetryAttempt;

}
