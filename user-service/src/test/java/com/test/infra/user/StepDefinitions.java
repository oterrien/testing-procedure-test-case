package com.test.infra.user;

import com.test.Application;
import com.test.JSonUtils;
import com.test.ScenarioContext;
import com.test.domain.user.api.IUser;
import com.test.infra.user.persistence.UserEntity;
import com.test.infra.user.persistence.UserJpaRepository;
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
    }

    @After
    public void tearDown() throws Exception {
        scenarioContext.clear();
    }

    //region GIVEN
    @Given("following users has been created")
    public void givenTheFollowingUserHasBeenCreated(List<UserEntity> user) {
        userJpaRepository.save(user);
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
    //endregion

    //region WHEN
    @When("I want to create a user with role '(.*)'")
    public void whenIWantToCreateAUserWithAGivenRole(Role userRole) throws Exception {

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
                scenarioContext.put("CREATED_USER_ID", userPayloadResult.getId());
                break;
            default:
                scenarioContext.put("ERR_STATUS_CODE", httpStatus);
        }
    }

    @When("I want to read my information")
    public void whenIWantToReadMyInformation() throws Exception {

        UserEntity me = scenarioContext.get("ME", UserEntity.class);
        findAUserById(me.getId()).ifPresent(u -> scenarioContext.put("FOUND_ME", u));
    }

    @When("I want to find this user")
    public void whenIWantToFindThisUser() throws Exception {

        UserEntity user = scenarioContext.get("USER", UserEntity.class);
        findAUserById(user.getId()).ifPresent(u -> scenarioContext.put("FOUND_USER", u));
    }

    private Optional<UserPayload> findAUserById(int id) throws Exception {

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
                scenarioContext.put("FOUND_USER_ID", userPayload.getId());
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

    private void setFieldValue(String field, String value, Object object) throws Exception {

        Field reflectField = object.getClass().getDeclaredField(field);
        reflectField.setAccessible(true);

        if (field.equalsIgnoreCase("role")) {
            reflectField.set(object, Role.valueOf(value));
        } else {
            reflectField.set(object, value);
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
                content(newPassword).
                contentType(MediaType.TEXT_PLAIN)).
                andReturn();

        HttpStatus httpStatus = HttpStatus.valueOf(result.getResponse().getStatus());

        scenarioContext.put("password", newPassword);

        switch (httpStatus) {
            case OK:
            case ACCEPTED:
                scenarioContext.put("UPDATED_USER_ID", me.getId());
                user.setPassword(newPassword);
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

        MvcResult result = mockMvc.perform(post("/api/v1/users/{id}/password", user.getId()).
                header("Authorization", generateAuthorizationHeader(me)).
                content(newPassword).
                contentType(MediaType.TEXT_PLAIN)).
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

                if (expected instanceof Role) {
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
                Object actual = reflectField.get(user);

                Assertions.assertThat(actual).isEqualTo(expected);
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
        String myCredentialsInBase64 = new BASE64Encoder().encode((user.getLogin() + ":" + user.getPassword()).getBytes());
        return "Basic " + myCredentialsInBase64;
    }
}