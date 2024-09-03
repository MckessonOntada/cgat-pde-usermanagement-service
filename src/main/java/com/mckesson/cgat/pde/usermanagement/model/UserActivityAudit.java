package com.mckesson.cgat.pde.usermanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "USER_ACTIVITY_AUDIT")
@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
public class UserActivityAudit extends AuditColumns implements Serializable {

    private static final String SEQUENCE_GENERATOR_NAME = "SEQ_USER_PROFILE_AUDIT";

    @Id
    @GeneratedValue(generator = SEQUENCE_GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = SEQUENCE_GENERATOR_NAME,
            sequenceName = SEQUENCE_GENERATOR_NAME,
            allocationSize = 1)
    private Long id;

    @Column(name = "USER_ID", nullable = false, length = 50)
    private String userId;

    @Column(name = "ACTIVITY", nullable = false, length = 50)
    private String activity;

    @Column(name = "ACTIVITY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date activityDate;
}
