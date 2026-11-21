package com.file.converter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;


@Entity
@Table(name = "shedlock")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Shedlock {
    @Column(name = "name")
    @Id
    @EqualsAndHashCode.Include
    private String name;
    @Column(name = "lock_until")
    private Instant lockUntil;
    @EqualsAndHashCode.Include
    @Column(name = "locked_at")
    private Instant lockedAt;
    @Column(name = "locked_by")
    private String lockedBy;



}
