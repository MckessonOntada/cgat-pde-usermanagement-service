package com.mckesson.cgat.pde.usermanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.NumericBooleanConverter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "USERS")
@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
public class User extends UUIDBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    private String email;
    private String displayName;

    @Convert(converter = NumericBooleanConverter.class)
    private Boolean isActive;
    @Column(name = "is_syncwithchartabstraction")
    private Integer isSyncWithChartAbstraction;
    @Column(name = "last_logged_in_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoggedInTime;
    // Relation with role
    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ROLE",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "id")}
    )
    private List<Role> roles;
}
