package com.file.converter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;



@Entity
@Table(name = "shedlock")
@Getter
@Setter
@NoArgsConstructor
public class Shedlock {
    @Column(name = "name")
    @Id
    private String name;
    private Instant lockUntil;
    private Instant lockedAt;
    private String lockedBy;



}
