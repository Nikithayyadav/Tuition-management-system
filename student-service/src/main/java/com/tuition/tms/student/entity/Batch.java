package com.tuition.tms.student.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 * Batch entity mapping to the shared 'batch' table created and managed by Teacher Management.
 */
@Entity
@Table(name = "batch")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;
}
