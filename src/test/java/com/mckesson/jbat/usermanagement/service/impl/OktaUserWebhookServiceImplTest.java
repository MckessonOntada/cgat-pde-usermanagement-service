package com.mckesson.jbat.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.model.RolePermissionMapping;
import com.mckesson.cgat.pde.usermanagement.model.Users;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import com.mckesson.cgat.pde.usermanagement.service.impl.OktaUserWebhookServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class OktaUserWebhookServiceImplTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    OktaUserWebhookServiceImpl service;

    @Mock
    HttpHeaders headers;

    @Test
    void getCommandsForTokenInlineWebhook_no_user_found() {
        JSONObject requestBody = new JSONObject(getOktaInlineTokenHookRequest());

        JSONObject response = service.getCommandsForTokenInlineWebhook(requestBody);
        assertFalse(Objects.isNull(response));
        assertTrue(response.has("commands"));
        assertTrue(response.getJSONArray("commands").isEmpty());
    }

    @Test
    void getCommandsForTokenInlineWebhook_user_found() {
        JSONObject requestBody = new JSONObject(getOktaInlineTokenHookRequest());
        Users user = new Users();
        user.setEmail("test@test.com");
        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setName("ADMIN");
        List<RolePermissionMapping> rolePermissions = new ArrayList<>();
        RolePermissionMapping rolePermissionMapping = new RolePermissionMapping();
        Permission permission = new Permission();
        permission.setName("BULK_UPLOAD_USERS");
        rolePermissionMapping.setPermission(permission);
        role.setRolePermissionMappings(rolePermissions);
        roles.add(role);
        user.setRoles(roles);
        user.setId(UUID.randomUUID());
        Mockito.when(userRepository.findByEmail(anyString())).thenReturn(user);
        JSONObject response = service.getCommandsForTokenInlineWebhook(requestBody);
        assertFalse(Objects.isNull(response));
        assertTrue(response.has("commands"));
        assertFalse(response.getJSONArray("commands").isEmpty());
        assertEquals(4, response.getJSONArray("commands").length()
        );
    }

    private String getOktaInlineTokenHookRequest() {
        return "{\n" +
                "    \"eventId\": \"P2YDDPPPSgySzGJ3bkAtnw\",\n" +
                "    \"eventTime\": \"2023-10-04T15:37:27.000Z\",\n" +
                "    \"eventType\": \"com.okta.oauth2.tokens.transform\",\n" +
                "    \"eventTypeVersion\": \"1.0\",\n" +
                "    \"contentType\": \"application/json\",\n" +
                "    \"cloudEventVersion\": \"0.1\",\n" +
                "    \"source\": \"https://ikmg2-admin.oktapreview.com\",\n" +
                "    \"data\": {\n" +
                "        \"context\": {\n" +
                "            \"request\": {\n" +
                "                \"id\": \"753634d79fcce4d8e40a4771fa1b72c8\",\n" +
                "                \"method\": \"POST\",\n" +
                "                \"url\": {\n" +
                "                    \"value\": \"https://ikmg2-admin.oktapreview.com\"\n" +
                "                },\n" +
                "                \"ipAddress\": \"13.83.19.176\"\n" +
                "            },\n" +
                "            \"protocol\": {\n" +
                "                \"type\": \"OAUTH2.0\",\n" +
                "                \"request\": {\n" +
                "                    \"scope\": \"openid offline_access email\",\n" +
                "                    \"grant_type\": \"implicit\",\n" +
                "                    \"client_id\": \"0oa8z4dc5eTkZTisf1d7\"\n" +
                "                },\n" +
                "                \"issuer\": {\n" +
                "                    \"uri\": \"https://ikmg2.oktapreview.com/oauth2/aus15z2gtyeApTTXf1d7\"\n" +
                "                },\n" +
                "                \"client\": {\n" +
                "                    \"id\": \"0oa8z4dc5eTkZTisf1d7\",\n" +
                "                    \"name\": \"My Test JBAT App\",\n" +
                "                    \"type\": \"PUBLIC\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"session\": {\n" +
                "                \"id\": \"trszranV7ziQGGtzNO5vBpUqg\",\n" +
                "                \"userId\": \"00ua80j5xgeeWqkbq1d7\",\n" +
                "                \"login\": \"rohit.a@mckesson.com\",\n" +
                "                \"createdAt\": \"2023-10-04T15:33:16.000Z\",\n" +
                "                \"expiresAt\": \"2023-10-04T17:37:27.000Z\",\n" +
                "                \"status\": \"ACTIVE\",\n" +
                "                \"lastFactorVerification\": \"2023-10-04T12:12:39.000Z\",\n" +
                "                \"amr\": [\n" +
                "                    \"PASSWORD\"\n" +
                "                ],\n" +
                "                \"mfaActive\": false\n" +
                "            },\n" +
                "            \"user\": {\n" +
                "                \"id\": \"00ua80j5xgeeWqkbq1d7\",\n" +
                "                \"passwordChanged\": \"2023-09-25T12:05:19.000Z\",\n" +
                "                \"profile\": {\n" +
                "                    \"login\": \"rohit.a@mckesson.com\",\n" +
                "                    \"firstName\": \"Rohit\",\n" +
                "                    \"lastName\": \"A\",\n" +
                "                    \"locale\": \"en\",\n" +
                "                    \"timeZone\": \"America/Los_Angeles\"\n" +
                "                },\n" +
                "                \"_links\": {\n" +
                "                    \"groups\": {\n" +
                "                        \"href\": \"https://ikmg2.oktapreview.com/api/v1/users/00ua80j5xgeeWqkbq1d7/groups\"\n" +
                "                    },\n" +
                "                    \"factors\": {\n" +
                "                        \"href\": \"https://ikmg2.oktapreview.com/api/v1/users/00ua80j5xgeeWqkbq1d7/factors\"\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            \"policy\": {\n" +
                "                \"id\": \"00p9clqktyJroltDu1d7\",\n" +
                "                \"rule\": {\n" +
                "                    \"id\": \"0pr9clexx6eWb4ai61d7\"\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        \"identity\": {\n" +
                "            \"claims\": {\n" +
                "                \"sub\": \"00ua80j5xgeeWqkbq1d7\",\n" +
                "                \"email\": \"rohit.a@mckesson.com\",\n" +
                "                \"ver\": 1,\n" +
                "                \"iss\": \"https://ikmg2.oktapreview.com/oauth2/aus15z2gtyeApTTXf1d7\",\n" +
                "                \"aud\": \"0oa8z4dc5eTkZTisf1d7\",\n" +
                "                \"jti\": \"ID.0cnb32y8RCF-LE6BxoyrJxh0yd7IxvdJY7ZJRG5wXzw\",\n" +
                "                \"amr\": [\n" +
                "                    \"pwd\"\n" +
                "                ],\n" +
                "                \"idp\": \"00oxdyu3vL7nQe6Cp1d6\",\n" +
                "                \"nonce\": \"nonce\",\n" +
                "                \"auth_time\": 1000\n" +
                "            },\n" +
                "            \"token\": {\n" +
                "                \"lifetime\": {\n" +
                "                    \"expiration\": 3600\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        \"access\": {\n" +
                "            \"claims\": {\n" +
                "                \"ver\": 1,\n" +
                "                \"jti\": \"AT.IN5kUosTP6PaOdo8smKGrMH22yFvo0jjh5fLCu91VZM\",\n" +
                "                \"iss\": \"https://ikmg2.oktapreview.com/oauth2/aus15z2gtyeApTTXf1d7\",\n" +
                "                \"aud\": \"https://localhost:8080\",\n" +
                "                \"cid\": \"0oa8z4dc5eTkZTisf1d7\",\n" +
                "                \"uid\": \"00ua80j5xgeeWqkbq1d7\",\n" +
                "                \"auth_time\": 1000,\n" +
                "                \"sub\": \"rohit.a@mckesson.com\",\n" +
                "                \"email\": \"rohit.a@mckesson.com\"\n" +
                "            },\n" +
                "            \"token\": {\n" +
                "                \"lifetime\": {\n" +
                "                    \"expiration\": 3600\n" +
                "                }\n" +
                "            },\n" +
                "            \"scopes\": {\n" +
                "                \"openid\": {\n" +
                "                    \"id\": \"scp15z2gtz1R8szdP1d7\",\n" +
                "                    \"action\": \"GRANT\"\n" +
                "                },\n" +
                "                \"email\": {\n" +
                "                    \"id\": \"scp15z2gu1xvSGPbL1d7\",\n" +
                "                    \"action\": \"GRANT\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    @Test
    public void testValidateToken_WhenTokenIsMissing() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders = Mockito.spy(httpHeaders);
        boolean isValid = service.validateToken(headers);
        assertFalse(isValid);
    }

    @Test
    void buildUserRoleEmptyList() {
        List<Role> roles = new ArrayList<>();
        JSONArray result = service.buildUserRole(roles);
        assertEquals(0, result.length());
    }

    @Test
    void buildUserRoleSingleRole() {
        Role role = new Role();
        role.setName("System Admin");
        List<Role> roles = Arrays.asList(role);
        JSONArray result = service.buildUserRole(roles);
        assertEquals(1, result.length());
    }

    @Test
    void buildUserRoleMultipleRoles() {
        Role role1 = new Role();
        role1.setName("Admin");
        Role role2 = new Role();
        role2.setName("User");
        List<Role> roles = Arrays.asList(role1, role2);
        JSONArray result = service.buildUserRole(roles);
        assertEquals(2, result.length());
        assertEquals("Admin", result.getString(0));
        assertEquals("User", result.getString(1));
    }

    @Test
    void addCommandWithValidInputs() {
        JSONArray existingCommand = new JSONArray();
        service.addCommand("type1", "operation1", "claim1", existingCommand, "value1");
        assertEquals(1, existingCommand.length());
        JSONObject addedCommand = existingCommand.getJSONObject(0);
        assertEquals("type1", addedCommand.getString("type"));
        JSONArray accessPathValue = addedCommand.getJSONArray("value");
        assertEquals(1, accessPathValue.length());
        JSONObject rolePayload = accessPathValue.getJSONObject(0);
        assertEquals("operation1", rolePayload.getString("op"));
        assertEquals("/claims/claim1", rolePayload.getString("path"));
        assertEquals("value1", rolePayload.getString("value"));
    }

    @Test
    void addCommandWithEmptyClaimValue() {
        JSONArray existingCommand = new JSONArray();
        service.addCommand("type2", "operation2", "claim2", existingCommand, "");
        assertEquals(1, existingCommand.length());
        JSONObject addedCommand = existingCommand.getJSONObject(0);
        JSONArray accessPathValue = addedCommand.getJSONArray("value");
        JSONObject rolePayload = accessPathValue.getJSONObject(0);
        assertEquals("", rolePayload.getString("value"));
    }

    @Test
    public void testAddUpdateUserEvent_WithValidRequestBody() {
        JSONObject requestBody = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray events = new JSONArray();
        JSONObject event1 = new JSONObject();
        event1.put("eventType", "group.user_membership.add");
        JSONObject event2 = new JSONObject();
        event2.put("eventType", "group.user_membership.remove");
        events.put(event1);
        events.put(event2);
        data.put("events", events);
        requestBody.put("data", data);
        service.addUpdateUserEvent(requestBody);
    }

}