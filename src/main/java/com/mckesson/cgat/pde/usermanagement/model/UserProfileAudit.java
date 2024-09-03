package com.mckesson.cgat.pde.usermanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "USER_PROFILE_AUDIT")
@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
public class UserProfileAudit extends AuditColumns implements Serializable {

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

    @Column(name = "ACTION", nullable = false, length = 50)
    private String action;

    @Column(name = "STUDY_ID")
    private Integer studyId;

    @Column(name = "LEVEL", nullable = false, length = 50)
    private String level;

    @Column(name = "NEW_VALUE", nullable = false, length = 4000)
    private String newValue;

    @Column(name = "OLD_VALUE", nullable = false, length = 4000)
    private String oldValue;
}
