package com.mckesson.jbat.usermanagement.mapper;

import com.mckesson.cgat.pde.usermanagement.dto.UserResponse;
import com.mckesson.cgat.pde.usermanagement.mapper.Transformer;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TransformerTest {

    private Transformer transformer;

    @BeforeEach
    public void setUp() {
        transformer = new Transformer();
    }

    @Test
    public void testToUserDto() {
        User user = new User();
        Role role1 = new Role();
        role1.setName("SYSTEM_ADMIN");
        role1.setLabel("System Admin");
        role1.setShortLabel("Admin");
        role1.setRoleType("Admin");
        List<Role> roleList = new ArrayList<>();
        roleList.add(role1);
        user.setId(UUID.fromString("4c3163a2-53ad-40a1-922c-6277b696abb1"));
        user.setEmail("test@example.com");
        user.setDisplayName("Test User");
        user.setIsActive(true);
        user.setRoles(roleList);
        user.setUpdatedDate(new Date());
        UserResponse userResponse = transformer.toUserDto(user);
        assertEquals(user.getId().toString(), userResponse.getId());
        assertEquals(user.getEmail(), userResponse.getEmail());
        assertEquals(user.getDisplayName(), userResponse.getDisplayName());
        assertEquals(user.getIsActive(), userResponse.isActive());
        assertEquals(user.getRoles().size(), userResponse.getRoles().size());
        assertEquals(user.getUpdatedDate(), userResponse.getUpdatedDate());
    }
}
