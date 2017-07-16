package com.test.infra.user;

import com.test.ScenarioContext;
import com.test.domain.common.JSonUtils;
import com.test.domain.user.api.model.IUser;
import com.test.domain.user.api.model.Role;
import com.test.infra.UserServiceStarter;
import com.test.infra.user.persistence.PasswordEntity;
import com.test.infra.user.persistence.RoleEntity;
import com.test.infra.user.persistence.UserEntity;
import com.test.infra.user.persistence.UserJpaRepository;
import com.test.infra.user.rest.PasswordPayload;
import com.test.infra.user.rest.UserMapperService;
import com.test.infra.user.rest.UserPayload;
import com.test.infra.user.rest.authentication.AuthenticationHttpFilter;
import com.test.infra.user.rest.authentication.SessionProviderService;
import com.test.infra.user.rest.authentication.UserSessionProviderService;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.test.JSonUtils.parseFromJson;
import static com.test.JSonUtils.serializeToJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ContextConfiguration(classes = UserServiceStarter.class)
@SpringBootTest(classes = UserServiceStarter.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
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
    }

    @After
    public void tearDown() throws Exception {
        scenarioContext.clear();
    }

    //region GIVEN
    @Given("following users has been created")
    public void givenTheFollowingUserHasBeenCreated(List<User> user) {

        user.forEach(u -> {
            UserEntity entity = new UserEntity();
            entity.setId(u.getId());
            entity.setLogin(u.getLogin());
            entity.setPassword(new PasswordEntity(u.getPassword()));
            entity.setRoleEntities(Stream.of(u.getRole().split(",")).distinct().map(p -> new RoleEntity(Role.valueOf(p), entity)).collect(Collectors.toSet()));
            userJpaRepository.save(entity);
        });
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class User {

        private int id;
        private String login;
        private String password;
        private String role;
    }

    @Given("I am the user '(.*)'")
    public void givenIAmAUser(String login) {

        UserEntity user = userJpaRepository.findByLogin(login);
        scenarioContext.put("ME", user);
    }

    @Given("another user is '(.*)'")
    public void givenTheUser(String login) {

        UserEntity user = userJpaRepository.findByLogin(login);
        scenarioContext.put("USER", user);
    }

    @Given("my credentials are: (.*)/(.*)")
    public void givenMyCredentials(String login, String password) {
        scenarioContext.put("LOGIN", login);
        scenarioContext.put("PASSWORD", password);
    }
    //endregion

    //region WHEN
    @When("I want to create a user with role '(.*)'")
    public void whenIWantToCreateAUserWithAGivenRole(Role userRole) throws Exception {

        UserPayload userPayload = new UserPayload();
        userPayload.setLogin(UUID.randomUUID().toString());
        userPayload.setPassword(new PasswordPayload(UUID.randomUUID().toString()));
        userPayload.getRoles().add(userRole);

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
                scenarioContext.put("CREATED_USER_ID", userPayloadResult.getId());
                break;
            default:
                scenarioContext.put("ERR_STATUS_CODE", httpStatus);
        }
    }

    @When("I want to read my information$")
    public void whenIWantToReadMyInformation() throws Exception {

        whenIWantToReadMyInformationWithMyCredentials();
    }

    @When("I want to read my information with my credentials")
    public void whenIWantToReadMyInformationWithMyCredentials() throws Exception {

        UserEntity me = scenarioContext.get("ME", UserEntity.class);
        String login = Optional.ofNullable(scenarioContext.get("LOGIN", String.class)).orElse(me.getLogin());
        String password = Optional.ofNullable(scenarioContext.get("PASSWORD", String.class)).orElse(me.getPassword().getValue());
        findAUserByIdAndCredentials(me.getId(), login, password).ifPresent(u -> scenarioContext.put("FOUND_ME", u));
    }

    @When("I want to read my information with my session token")
    public void whenIWantToReadMyInformationWithMySessionToken() throws Exception {

        UserEntity me = scenarioContext.get("ME", UserEntity.class);
        String sessionToken = scenarioContext.get("SESSION_TOKEN", String.class);
        findAUserByIdAndSessionToken(me.getId(), sessionToken).
                ifPresent(u -> scenarioContext.put("FOUND_ME", u));
    }

    @When("I want to find this user")
    public void whenIWantToFindThisUser() throws Exception {

        UserEntity me = scenarioContext.get("ME", UserEntity.class);
        UserEntity user = scenarioContext.get("USER", UserEntity.class);
        findAUserByIdAndCredentials(user.getId(), me.getLogin(), me.getPassword().getValue()).
                ifPresent(u -> scenarioContext.put("FOUND_USER", u));
    }

    private Optional<UserPayload> findAUserById(int id) throws Exception {
        UserEntity me = scenarioContext.get("ME", UserEntity.class);
        return findAUserByIdAndCredentials(id, me.getLogin(), me.getPassword().getValue());
    }

    private Optional<UserPayload> findAUserByIdAndCredentials(int id, String login, String password) throws Exception {

        MvcResult result = mockMvc.perform(get("/api/v1/users/{id}", id).
                header("Authorization", generateAuthorizationHeader(login, password)).
                contentType(MediaType.APPLICATION_JSON_VALUE)).
                andReturn();

        return readOperation(result);
    }

    private Optional<UserPayload> findAUserByIdAndSessionToken(int id, String sessionToken) throws Exception {

        MvcResult result = mockMvc.perform(get("/api/v1/users/{id}", id).
                header("session-token", sessionToken).
                contentType(MediaType.APPLICATION_JSON_VALUE)).
                andReturn();

        return readOperation(result);
    }

    private Optional<UserPayload> readOperation(MvcResult result) throws Exception {

        HttpStatus httpStatus = HttpStatus.valueOf(result.getResponse().getStatus());

        switch (httpStatus) {
            case OK:
            case ACCEPTED:
                UserPayload userPayload = parseFromJson(result.getResponse().getContentAsString(), UserPayload.class);
                scenarioContext.put("FOUND_USER_ID", userPayload.getId());
                scenarioContext.put("SESSION_TOKEN", result.getResponse().getHeader("session-token"));
                return Optional.of(userPayload);
            default:
                scenarioContext.put("ERR_STATUS_CODE", httpStatus);
                return Optional.empty();
        }
    }

    @When("I want to set (.*) = '(.*)' for this user")
    public void whenIWantToSetFieldValueForThisUser(String field, String value) throws Exception {

        UserEntity user = scenarioContext.get("USER", UserEntity.class);
        setFieldValue(field, value, user);

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
                scenarioContext.put("UPDATED_USER_ID", user.getId());
                break;
            default:
                scenarioContext.put("ERR_STATUS_CODE", httpStatus);
        }
    }

    private void setFieldValue(String field, String value, UserEntity user) throws Exception {

        Field reflectField = user.getClass().getDeclaredField(field);
        reflectField.setAccessible(true);

        if (field.equalsIgnoreCase("role")) {
            reflectField.set(user, Role.valueOf(value));
        } else if (field.equalsIgnoreCase("password")) {
            reflectField.set(user, new PasswordEntity(value));
        } else {
            reflectField.set(user, value);
        }

        scenarioContext.put(field, value);
    }

    @When("I want to delete this user")
    public void whenIWantToDeleteThisUser() throws Exception {

        UserEntity me = scenarioContext.get("ME", UserEntity.class);
        UserEntity user = scenarioContext.get("USER", UserEntity.class);

        MvcResult result = mockMvc.perform(delete("/api/v1/users/{id}", user.getId()).
                header("Authorization", generateAuthorizationHeader(me)).
                contentType(MediaType.APPLICATION_JSON_VALUE)).
                andReturn();

        HttpStatus httpStatus = HttpStatus.valueOf(result.getResponse().getStatus());

        switch (httpStatus) {
            case NO_CONTENT:
                scenarioContext.put("DELETED_USER_ID", user.getId());
                scenarioContext.put("ERR_STATUS_CODE", httpStatus);
                break;
            default:
                scenarioContext.put("ERR_STATUS_CODE", httpStatus);
        }
    }

    @When("I want to change (my|his) password to '(.*)'")
    public void whenIWantToChangeMyPassword(String myOrHis, String newPassword) throws Exception {

        UserEntity me = scenarioContext.get("ME", UserEntity.class);

        UserEntity user;
        switch (myOrHis) {
            case "my":
                user = scenarioContext.get("ME", UserEntity.class);
                break;
            case "his":
                user = scenarioContext.get("USER", UserEntity.class);
                break;
            default:
                throw new PendingException("not yet implemented");
        }

        MvcResult result = mockMvc.perform(patch("/api/v1/users/{id}/password", user.getId()).
                header("Authorization", generateAuthorizationHeader(me)).
                content(JSonUtils.serializeToJson(new PasswordPayload(newPassword))).
                contentType(MediaType.APPLICATION_JSON_VALUE)).
                andReturn();

        HttpStatus httpStatus = HttpStatus.valueOf(result.getResponse().getStatus());

        scenarioContext.put("password", newPassword);

        switch (httpStatus) {
            case OK:
            case ACCEPTED:
                scenarioContext.put("UPDATED_USER_ID", me.getId());
                user.setPassword(userMapperService.convert(new PasswordPayload(newPassword)));
                break;
            default:
                scenarioContext.put("ERR_STATUS_CODE", httpStatus);
        }
    }

    @When("I want to check (my|his) password is '(.*)'")
    public void whenIWantToChangePasswordOfThisUser(String myOrHis, String newPassword) throws Exception {

        UserEntity me = scenarioContext.get("ME", UserEntity.class);

        UserEntity user;
        switch (myOrHis) {
            case "my":
                user = scenarioContext.get("ME", UserEntity.class);
                break;
            case "his":
                user = scenarioContext.get("USER", UserEntity.class);
                break;
            default:
                throw new PendingException("not yet implemented");
        }

        MvcResult result = mockMvc.perform(get("/api/v1/users/{id}/password", user.getId()).
                header("Authorization", generateAuthorizationHeader(me)).
                param("value", newPassword).
                param("isEncoded", Boolean.toString(false))).
                andReturn();

        HttpStatus httpStatus = HttpStatus.valueOf(result.getResponse().getStatus());

        switch (httpStatus) {
            case OK:
            case ACCEPTED:
                scenarioContext.put("IS_CORRECT", Boolean.valueOf(result.getResponse().getContentAsString()));
                break;
            default:
                scenarioContext.put("ERR_STATUS_CODE", httpStatus);
        }
    }

    @When("I want to find all users")
    public void whenIWantToFindAllUsers() throws Exception {

        UserEntity me = scenarioContext.get("ME", UserEntity.class);

        MvcResult result = mockMvc.perform(get("/api/v1/users", me.getId()).
                header("Authorization", generateAuthorizationHeader(me)).
                contentType(MediaType.APPLICATION_JSON)).
                andReturn();

        HttpStatus httpStatus = HttpStatus.valueOf(result.getResponse().getStatus());

        switch (httpStatus) {
            case OK:
            case ACCEPTED:
                scenarioContext.put("FOUND_USERS", JSonUtils.parseFromJsonList(result.getResponse().getContentAsString(), UserPayload.class));
                break;
            default:
                scenarioContext.put("ERR_STATUS_CODE", httpStatus);
        }
    }
    //endregion

    // region THEN
    @Then("I should not be authorized")
    public void thenIAmNotAuthorized() {

        HttpStatus errHttpStatus = scenarioContext.get("ERR_STATUS_CODE", HttpStatus.class);

        Assertions.assertThat(errHttpStatus).isNotNull();
        Assertions.assertThat(errHttpStatus).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Then("I should be authorized")
    public void thenIAmAuthorized() {

        HttpStatus errHttpStatus = scenarioContext.get("ERR_STATUS_CODE", HttpStatus.class);
        Optional<HttpStatus> errHttpStatusOpt = Optional.ofNullable(errHttpStatus);

        if (errHttpStatusOpt.isPresent()) {
            Assertions.assertThat(errHttpStatus).isNotEqualByComparingTo(HttpStatus.UNAUTHORIZED);
        }
    }

    @Then("this user should be created")
    public void thenUserIsCreate() throws Exception {

        Integer createdUserId = scenarioContext.get("CREATED_USER_ID", Integer.class);
        Assertions.assertThat(findAUserById(createdUserId)).isPresent();
    }

    @Then("this user should be deleted")
    public void thenUserIsDeleted() throws Exception {

        HttpStatus errHttpStatus = scenarioContext.get("ERR_STATUS_CODE", HttpStatus.class);

        Assertions.assertThat(errHttpStatus).isNotNull();
        Assertions.assertThat(errHttpStatus).isEqualByComparingTo(HttpStatus.NO_CONTENT);

        Integer deletedUserId = scenarioContext.get("DELETED_USER_ID", Integer.class);
        Assertions.assertThat(findAUserById(deletedUserId)).isEmpty();
    }

    @Then("the (.*) or this user should be updated")
    public void thenUserIsUpdated(String field) throws Throwable {

        Integer updatedUserId = scenarioContext.get("UPDATED_USER_ID", Integer.class);

        Optional<UserPayload> userOpt = findAUserById(updatedUserId);
        if (userOpt.isPresent()) {
            try {
                UserPayload user = userOpt.get();

                Field reflectField = user.getClass().getDeclaredField(field);
                reflectField.setAccessible(true);

                Object expected = scenarioContext.get(field);
                Object actual = reflectField.get(user);

                if (field.equalsIgnoreCase("password")) {
                    PasswordPayload expectedPassword = new PasswordPayload(expected.toString());
                    Assertions.assertThat(userMapperService.convert((PasswordPayload) actual)).
                            isEqualByComparingTo(userMapperService.convert(expectedPassword));
                    return;
                }

                if (field.equalsIgnoreCase("role")) {
                    expected = ((Role) expected).name();
                    actual = ((Role) actual).name();
                }

                Assertions.assertThat(actual).isEqualTo(expected);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            Assertions.fail("User " + updatedUserId + " should be found");
        }
    }

    @Then("(my|his) information should be available")
    public void thenMyInformationAreAvailable(String myOrHis) {

        UserPayload user;
        switch (myOrHis) {
            case "my":
                user = scenarioContext.get("FOUND_ME", UserPayload.class);
                break;
            case "his":
                user = scenarioContext.get("FOUND_USER", UserPayload.class);
                break;
            default:
                throw new PendingException("not yet implemented");
        }

        Assertions.assertThat(user).isNotNull();
    }

    @Then("my password should be updated")
    public void thenMyPasswordShouldBeUpdated() throws Exception {

        Integer updatedUserId = scenarioContext.get("UPDATED_USER_ID", Integer.class);

        Optional<UserPayload> userOpt = findAUserById(updatedUserId);
        if (userOpt.isPresent()) {
            try {
                UserPayload user = userOpt.get();

                Field reflectField = user.getClass().getDeclaredField("password");
                reflectField.setAccessible(true);

                Object expected = scenarioContext.get("password");
                PasswordPayload actual = (PasswordPayload) reflectField.get(user);

                Assertions.assertThat(userMapperService.convert(actual)).isEqualByComparingTo(userMapperService.convert(new PasswordPayload((String) expected)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            Assertions.fail("User " + updatedUserId + " should be found");
        }
    }

    @Then("this password should (be correct|not be correct)")
    public void thenPasswordIsCorrectOrNot(String correctOrNotCorrect) {

        Boolean expected;
        switch (correctOrNotCorrect) {
            case "be correct":
                expected = true;
                break;
            case "not be correct":
                expected = false;
                break;
            default:
                throw new PendingException("not yet implemented");
        }

        Boolean actual = scenarioContext.get("IS_CORRECT", Boolean.class);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Then("I should be able to retrieve following users:")
    public void thenPasswordIsCorrectOrNot(List<String> logins) {

        List<UserPayload> foundUsers = scenarioContext.get("FOUND_USERS", List.class);

        Assertions.assertThat(foundUsers).isNotNull();
        Assertions.assertThat(foundUsers).isNotEmpty();

        foundUsers.stream().
                map(UserPayload::getLogin).
                forEach(login -> Assertions.assertThat(login).isIn(logins));
    }

    @Then("I should be able to retrieve only myself")
    public void thenIShouldBeAbleToRetrieveOnlyMyself() {

        List<UserPayload> foundUsers = scenarioContext.get("FOUND_USERS", List.class);

        UserEntity me = scenarioContext.get("ME", UserEntity.class);

        Assertions.assertThat(foundUsers).isNotNull();
        Assertions.assertThat(foundUsers).isNotEmpty();
        Assertions.assertThat(foundUsers.size()).isEqualTo(1);
        Assertions.assertThat(foundUsers.get(0).getId()).isEqualTo(me.getId());
    }
    //endregion

    private String generateAuthorizationHeader(IUser user) {
        return generateAuthorizationHeader(user.getLogin(), user.getPassword().getValue());
    }

    private String generateAuthorizationHeader(String login, String password) {
        String myCredentialsInBase64 = new BASE64Encoder().encode((login + ":" + password).getBytes());
        return "Basic " + myCredentialsInBase64;
    }
}