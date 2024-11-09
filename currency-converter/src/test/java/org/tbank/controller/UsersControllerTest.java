package org.tbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.tbank.dto.users.UserCreateDTO;
import org.tbank.mapper.UserMapper;
import org.tbank.model.Role;
import org.tbank.model.User;
import org.tbank.repository.RoleRepository;
import org.tbank.repository.UserRepository;
import java.util.HashMap;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;

    private User testUser;
    private User anotherTestUser;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("chuck.norris@google.com");
        createDTO.setFirstName("Chuck");
        createDTO.setLastName("Norris");
        createDTO.setPassword("some-password");

        testUser = userMapper.toUser(createDTO);
        userRepository.save(testUser);

        Role testRole = roleRepository.findByNameWithEagerUpload("USER").orElseThrow();
        testRole.addUser(testUser);
        roleRepository.save(testRole);

        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        UserCreateDTO anotherCreateDTO = new UserCreateDTO();
        anotherCreateDTO.setEmail("alice.norris@google.com");
        anotherCreateDTO.setFirstName("Alice");
        anotherCreateDTO.setLastName("Norris");
        anotherCreateDTO.setPassword("another-password");

        anotherTestUser = userMapper.toUser(anotherCreateDTO);
    }

    @AfterEach
    public void clean() {
        userRepository.deleteAll();
    }

    @Test
    public void testGet() throws Exception {
        var request = get("/api/v1/users/{id}", testUser.getId()).with(jwt());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("id").isEqualTo(testUser.getId()),
                v -> v.node("roleNames[0]").isEqualTo("USER")
        );
    }

    @Test
    public void testCreate() throws Exception {
        var request = post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(anotherTestUser));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByEmailWithEagerUpload(anotherTestUser.getEmail()).orElseThrow();

        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(anotherTestUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(anotherTestUser.getLastName());
        assertThat(user.getEmail()).isEqualTo(anotherTestUser.getEmail());
        assertThat(user.getPasswordDigest()).isNotEqualTo(anotherTestUser.getPasswordDigest());
    }

    @Test
    public void testCreateWithNotValidEmail() throws Exception {
        var dto = userMapper.toDto(testUser);
        dto.setEmail("");

        var request = post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate() throws Exception {
        var dto = userMapper.toDto(testUser);

        dto.setFirstName("New Name");
        dto.setLastName("New Last Name");

        var request = put("/api/v1/users/{id}", testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var user = userRepository.findByIdWithRoles(testUser.getId()).orElseThrow();

        assertThat(user.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(user.getLastName()).isEqualTo(dto.getLastName());
    }

    @Test
    public void testPartialUpdate() throws Exception {
        var dto = new HashMap<String, String>();
        dto.put("firstName", "Another First Name");

        var request = put("/api/v1/users/{id}", testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var author = userRepository.findByIdWithRoles(testUser.getId()).orElseThrow();

        assertThat(author.getLastName()).isEqualTo(testUser.getLastName());
        assertThat(author.getFirstName()).isEqualTo(dto.get("firstName"));
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/v1/users/{id}", testUser.getId()).with(token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(testUser.getId())).isEqualTo(false);
    }
}
