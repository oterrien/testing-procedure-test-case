package com.test.infra.user;

import com.test.Application;
import com.test.ScenarioContext;
import com.test.domain.user.api.IUser;
import com.test.infra.user.rest.authentication.AuthenticationHttpFilter;
import com.test.infra.user.rest.authentication.SessionProviderService;
import com.test.infra.user.rest.authentication.UserSessionProviderService;
import com.test.infra.user.rest.UserMapperService;
import com.test.infra.user.rest.UserPayload;
import com.test.infra.user.persistence.UserEntity;
import com.test.infra.user.persistence.UserJpaRepository;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sun.misc.BASE64Encoder;

import java.lang.reflect.Field;
import java.util.UUID;

import static com.test.JSonUtils.parseFromJson;
import static com.test.JSonUtils.serializeToJson;
import static com.test.domain.user.api.IUser.Role;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ContextConfiguration(classes = Application.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class StepDefinitions {

    private ScenarioContext scenarioContext = new ScenarioContext();

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserSessionProviderService<UserEntity> userSessionProviderService;

    @Autowired
    private SessionProviderService<UserEntity> sessionProviderService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserMapperService userMapperService;

    @Before
    public void setUp() throws Exception {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).
                addFilters(new AuthenticationHttpFilter(userSessionProviderService, sessionProviderService, userJpaRepository)).
                build();

        userJpaRepository.deleteAll();

        UserEntity user = new UserEntity();
        user.setLogin("admin");
        user.setPassword("adminPassword");
        user.setRole(Role.ADMIN);
        userJpaRepository.save(user);

        user = new UserEntity();
        user.setLogin("anotherAdmin");
        user.setPassword("adminPassword");
        user.setRole(Role.ADMIN);
        userJpaRepository.save(user);

        user = new UserEntity();
        user.setLogin("client");
        user.setPassword("clientPassword");
        user.setRole(Role.CLIENT);
        userJpaRepository.save(user);

        user = new UserEntity();
        user.setLogin("anotherClient");
        user.setPassword("clientPassword");
        user.setRole(Role.CLIENT);
        userJpaRepository.save(user);

        user = new UserEntity();
        user.setLogin("advisor");
        user.setPassword("advisorPassword");
        user.setRole(Role.ADVISOR);
        userJpaRepository.save(user);

        user = new UserEntity();
        user.setLogin("anotherAdvisor");
        user.setPassword("advisorPassword");
        user.setRole(Role.ADVISOR);
        userJpaRepository.save(user);
    }

    @After
    public void tearDown() throws Exception {
        scenarioContext.clear();
    }

    //region GIVEN
    @Given("I am the user '(.*)'")
    public void given_I_am_a_user(String login) {

        UserEntity user = userJpaRepository.findByLogin(login);
        scenarioContext.put("ME", user);
    }

    @Given("the user '(.*)'")
    public void given_the_user(String login) {

        UserEntity user = userJpaRepository.findByLogin(login);
        scenarioContext.put("USER", user);
    }
    //endregion

    //region WHEN
    @When("I want to create a user with role '(.*)'")
    public void when_I_want_to_create_a_user_with_a_given_role(Role userRole) throws Exception {

        UserPayload userPayload = new UserPayload();
        userPayload.setLogin(UUID.randomUUID().toString());
        userPayload.setPassword(UUID.randomUUID().toString());
        userPayload.setRole(userRole);

        UserEntity me = scenarioContext.get("ME", UserEntity.class);

        MvcResult result = mockMvc.perform(post("/api/v1/users").
                header("Authorization", generateAuthorizationHeader(me)).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(serializeToJson(userPayload))).
                andReturn();

        HttpStatus httpStatus = HttpStatus.valueOf(result.getResponse().getStatus());

        switch (httpStatus) {
            case OK:
            case ACCEPTED:
                UserPayload userPayloadResult = parseFromJson(result.getResponse().getContentAsString(), UserPayload.class);
                scenarioContext.put("CREATED_USER", userPayloadResult);
                break;
            default:
                String message = result.getResponse().getContentAsString();
                scenarioContext.put("ERR_STATUS_CODE", httpStatus);
                scenarioContext.put("ERR_MESSAGE", message);
        }
    }

    @When("I want to read my information")
    public void when_I_want_to_read_my_information() throws Exception {

        UserEntity me = scenarioContext.get("ME", UserEntity.class);

        when_I_want_to_find_a_user(me.getId());
    }

    @When("I want to find this user")
    public void when_I_want_to_find_this_user() throws Exception {

        UserEntity user = scenarioContext.get("USER", UserEntity.class);

        when_I_want_to_find_a_user(user.getId());
    }

    public void when_I_want_to_find_a_user(int id) throws Exception {

        UserEntity me = scenarioContext.get("ME", UserEntity.class);

        MvcResult result = mockMvc.perform(get("/api/v1/users/{id}", id).
                header("Authorization", generateAuthorizationHeader(me)).
                contentType(MediaType.APPLICATION_JSON_VALUE)).
                andReturn();

        HttpStatus httpStatus = HttpStatus.valueOf(result.getResponse().getStatus());

        switch (httpStatus) {
            case OK:
            case ACCEPTED:
                UserPayload userPayload = parseFromJson(result.getResponse().getContentAsString(), UserPayload.class);
                scenarioContext.put("FOUND_USER", userPayload);
                break;
            default:
                String message = result.getResponse().getContentAsString();
                scenarioContext.put("ERR_STATUS_CODE", httpStatus);
                scenarioContext.put("ERR_MESSAGE", message);
        }
    }

    @When("I want to set (.*) = '(.*)' for this user")
    public void when_I_want_to_find_a_user(String field, String value) throws Exception {

        UserEntity user = scenarioContext.get("USER", UserEntity.class);

        Field reflectField = user.getClass().getDeclaredField(field);
        reflectField.setAccessible(true);

        if (field.equalsIgnoreCase("role")) {
            reflectField.set(user, Role.valueOf(value));
        } else {
            reflectField.set(user, value);
        }

        scenarioContext.put(field, value);

        UserEntity me = scenarioContext.get("ME", UserEntity.class);

        MvcResult result = mockMvc.perform(put("/api/v1/users/{id}", user.getId()).
                content(serializeToJson(userMapperService.convert(user))).
                header("Authorization", generateAuthorizationHeader(me)).
                contentType(MediaType.APPLICATION_JSON_VALUE)).
                andReturn();

        HttpStatus httpStatus = HttpStatus.valueOf(result.getResponse().getStatus());

        switch (httpStatus) {
            case OK:
            case ACCEPTED:
                UserPayload userPayload = parseFromJson(result.getResponse().getContentAsString(), UserPayload.class);
                scenarioContext.put("FOUND_USER", userPayload);
                break;
            default:
                String message = result.getResponse().getContentAsString();
                scenarioContext.put("ERR_STATUS_CODE", httpStatus);
                scenarioContext.put("ERR_MESSAGE", message);
        }
    }

    @When("I want to delete this user")
    public void when_I_want_to_delete_this_user() throws Exception {

        UserEntity me = scenarioContext.get("ME", UserEntity.class);
        UserEntity user = scenarioContext.get("USER", UserEntity.class);

        MvcResult result = mockMvc.perform(delete("/api/v1/users/{id}", user.getId()).
                header("Authorization", generateAuthorizationHeader(me)).
                contentType(MediaType.APPLICATION_JSON_VALUE)).
                andReturn();

        HttpStatus httpStatus = HttpStatus.valueOf(result.getResponse().getStatus());

        switch (httpStatus) {
            case NO_CONTENT:
                when_I_want_to_find_a_user(user.getId());
                break;
            default:
                String message = result.getResponse().getContentAsString();
                scenarioContext.put("ERR_STATUS_CODE", httpStatus);
                scenarioContext.put("ERR_MESSAGE", message);
        }
    }
    //endregion

    // region THEN
    @Then("I am not authorized")
    public void then_I_am_not_authorized() {

        HttpStatus errHttpStatus = scenarioContext.get("ERR_STATUS_CODE", HttpStatus.class);
        String errMessage = scenarioContext.get("ERR_MESSAGE", String.class);

        Assertions.assertThat(errHttpStatus).isNotNull();
        Assertions.assertThat(errHttpStatus).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
        Assertions.assertThat(errMessage).contains("not authorized");
    }

    @Then("this user is created")
    public void then_user_is_created() throws Exception {

        UserPayload createdUser = scenarioContext.get("CREATED_USER", UserPayload.class);
        Assertions.assertThat(createdUser).isNotNull();
    }

    @Then("this user is deleted")
    public void then_user_is_deleted() throws Exception {

        HttpStatus errHttpStatus = scenarioContext.get("ERR_STATUS_CODE", HttpStatus.class);

        Assertions.assertThat(errHttpStatus).isNotNull();
        Assertions.assertThat(errHttpStatus).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    }

    @Then("the (.*) or this user is updated")
    public void then_user_is_updated(String field) throws Throwable {

        UserPayload user = scenarioContext.get("FOUND_USER", UserPayload.class);
        Field reflectField = user.getClass().getDeclaredField(field);
        reflectField.setAccessible(true);

        Object expected = scenarioContext.get(field);
        Object actual = reflectField.get(user);

        if (expected instanceof Role) {
            expected = ((Role) expected).name();
            actual = ((Role) actual).name();
        }

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Then("my information are available")
    public void then_my_information_are_available() {
        UserPayload user = scenarioContext.get("FOUND_USER", UserPayload.class);
        UserEntity me = scenarioContext.get("ME", UserEntity.class);
        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(userMapperService.convert(me)).isEqualTo(user);
    }
    //endregion

    private static final String generateAuthorizationHeader(IUser user) {
        String myCredentialsInBase64 = new BASE64Encoder().encode((user.getLogin() + ":" + user.getPassword()).getBytes());
        return "Basic " + myCredentialsInBase64;
    }
}