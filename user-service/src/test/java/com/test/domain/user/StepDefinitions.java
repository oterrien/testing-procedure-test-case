package com.test.domain.user;

import com.test.ScenarioContext;
import com.test.domain.user.api.IPassword;
import com.test.domain.user.api.IUserService;
import com.test.domain.user.api.NotAuthorizedException;
import com.test.domain.user.api.UserServiceFactory;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.assertj.core.api.Assertions;
import org.junit.After;

import java.lang.reflect.Field;
import java.util.Optional;

import static com.test.domain.user.api.IUser.Role;

public class StepDefinitions {

    private ScenarioContext scenarioContext = new ScenarioContext();

    private IUserService<User> userService;

    private UserRepositoryMock repositoryMock = new UserRepositoryMock();

    @After
    public void tearDown() {
        repositoryMock.deleteAll();
        scenarioContext.clear();
    }

    //region GIVEN
    @Given("I am a user with role '(.*)'")
    public void givenIAmAUserWithRole(Role userRole) {

        User user = new User();
        user.setLogin("my" + userRole.name());
        user.setPassword(new User.Password("anyPassword"));
        user.setRole(userRole);
        repositoryMock.create(user);

        userService = UserServiceFactory.getInstance().create(repositoryMock, user);

        scenarioContext.put("ME", user);
    }

    @Given("my password is '(.*)'")
    public void givenMyPasswordIs(String password) {

        User me = scenarioContext.get("ME", User.class);
        me.setPassword(new User.Password(password));
    }

    @Given("a user with role '(.*)'")
    public void givenAUserWithRole(Role userRole) {

        User user = new User();
        user.setLogin("my" + userRole.name());
        user.setPassword(new User.Password("anyPassword"));
        user.setRole(userRole);
        repositoryMock.create(user);

        scenarioContext.put("USER", user);
    }

    @Given("his password is '(.*)'")
    public void givenHisPasswordIs(String password) {

        User user = scenarioContext.get("USER", User.class);
        user.setPassword(new User.Password(password));
    }
    //endregion

    //region WHEN
    @When("I want to create a user with role '(.*)'")
    public void whenIWantToCreateAUserWithAGivenRole(Role userRole) {

        User user = new User();
        user.setLogin("anyLogin");
        user.setPassword(new User.Password("anyPassword"));
        user.setRole(userRole);

        try {
            int id = userService.create(user);
            scenarioContext.put("CREATED_ID", id);
        } catch (Exception e) {
            scenarioContext.put("EXCEPTION", e);
        }
    }

    @When("I want to set (.*) = '(.*)' for this user")
    public void whenIWantToSetAFieldValueForThisUser(String field, String value) throws Throwable {

        User user = scenarioContext.get("USER", User.class);

        Field reflectField = user.getClass().getDeclaredField(field);
        reflectField.setAccessible(true);

        if (field.equalsIgnoreCase("role")) {
            reflectField.set(user, Role.valueOf(value));
        } else if (field.equalsIgnoreCase("password")) {
            reflectField.set(user, new User.Password(value));
        } else {
            reflectField.set(user, value);
        }

        try {
            userService.update(user.getId(), user);
            scenarioContext.put(field, value);
        } catch (Exception e) {
            scenarioContext.put("EXCEPTION", e);
        }
    }

    @When("I want to read my information")
    public void whenIWantToReadMyInformation() throws Throwable {

        User me = scenarioContext.get("ME", User.class);

        try {
            Optional<User> result = userService.get(me.getId());
            scenarioContext.put("ME?", result);
        } catch (Exception e) {
            scenarioContext.put("EXCEPTION", e);
        }
    }

    @When("I want to find this user")
    public void whenIWantToFindThisUser() throws Throwable {

        User me = scenarioContext.get("USER", User.class);

        try {
            Optional<User> result = userService.get(me.getId());
            scenarioContext.put("USER?", result);
        } catch (Exception e) {
            scenarioContext.put("EXCEPTION", e);
        }
    }

    @When("I want to delete this user")
    public void whenIWantToDeleteThisUser() throws Throwable {

        User user = scenarioContext.get("USER", User.class);

        try {
            userService.delete(user.getId());
        } catch (Exception e) {
            scenarioContext.put("EXCEPTION", e);
        }
    }

    @When("I want to change my password to '(.*)'")
    public void whenIWantToChangeMyPasswordTo(String newPassword) throws Throwable {

        User me = scenarioContext.get("ME", User.class);

        try {
            userService.resetPassword(me.getId(), new User.Password(newPassword));
            scenarioContext.put("NEW_PASSWORD", newPassword);
        } catch (Exception e) {
            scenarioContext.put("EXCEPTION", e);
        }
    }

    @When("I want to change password of this user to '(.*)'")
    public void whenIWantToChangeUserPasswordTo(String newPassword) throws Throwable {

        User user = scenarioContext.get("USER", User.class);

        try {
            userService.resetPassword(user.getId(), new User.Password(newPassword));
            scenarioContext.put("NEW_PASSWORD", newPassword);
        } catch (Exception e) {
            scenarioContext.put("EXCEPTION", e);
        }
    }

    @When("I want to check my password is '(.*)'")
    public void whenIWantToCheckMyPasswordIs(String password) throws Throwable {

        User user = scenarioContext.get("ME", User.class);

        try {
            boolean isCorrect = userService.isPasswordCorrect(user.getId(), new User.Password(password));
            scenarioContext.put("IS_CORRECT", isCorrect);
        } catch (Exception e) {
            scenarioContext.put("EXCEPTION", e);
        }
    }

    @When("I want to check password of this user is '(.*)'")
    public void whenIWantToCheckUserPasswordIs(String password) throws Throwable {

        User user = scenarioContext.get("USER", User.class);

        try {
            boolean isCorrect = userService.isPasswordCorrect(user.getId(), new User.Password(password));
            scenarioContext.put("IS_CORRECT", isCorrect);
        } catch (Exception e) {
            scenarioContext.put("EXCEPTION", e);
        }
    }
    //endregion

    // region THEN
    @Then("I am not authorized")
    public void thenIAmNotAuthorized() {

        Assertions.assertThat(scenarioContext.get("EXCEPTION", Exception.class)).isNotNull();
        Exception e = scenarioContext.get("EXCEPTION", Exception.class);
        Assertions.assertThat(e).isInstanceOf(NotAuthorizedException.class);
    }

    @Then("my information are available")
    public void thenIAmAuthorized() {

        Assertions.assertThat(scenarioContext.get("EXCEPTION", Exception.class)).isNull();
        Optional result = scenarioContext.get("ME?", Optional.class);
        Assertions.assertThat(result).isPresent();
    }

    @Then("this user is created")
    public void thenUserIsCreated() {

        Assertions.assertThat(scenarioContext.get("CREATED_ID", Integer.class)).isNotNull();
        int id = scenarioContext.get("CREATED_ID", Integer.class);
        Assertions.assertThat(id).isPositive();
    }

    @Then("the (.*) of this user is updated")
    public void thenUserIsUpdated(String field) throws Throwable {

        User user = scenarioContext.get("USER", User.class);

        user = repositoryMock.find(user.getId()).get();

        Field reflectField = user.getClass().getDeclaredField(field);
        reflectField.setAccessible(true);

        Object expected = scenarioContext.get(field);
        Object actual = reflectField.get(user);

        if (field.equalsIgnoreCase("password")) {
            IPassword expectedPassword = new User.Password(expected.toString()).encoded();
            Assertions.assertThat((IPassword) actual).isEqualByComparingTo(expectedPassword);
            return;
        }

        if (field.equalsIgnoreCase("role")) {
            expected = ((Role) expected).name();
            actual = ((Role) actual).name();
        }

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Then("this user is deleted")
    public void thenUserIsDeleted() {

        Assertions.assertThat(scenarioContext.get("EXCEPTION", Exception.class)).isNull();

        User user = scenarioContext.get("USER", User.class);
        Optional<User> result = repositoryMock.find(user.getId());
        Assertions.assertThat(result.orElse(null)).isNull();
    }

    @Then("my password is changed")
    public void thenMyPasswordIsChanged() {

        Assertions.assertThat(scenarioContext.get("EXCEPTION", Exception.class)).isNull();

        User user = scenarioContext.get("ME", User.class);
        IPassword newPassword = new User.Password(scenarioContext.get("NEW_PASSWORD", String.class));
        Optional<User> result = repositoryMock.find(user.getId());
        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getPassword()).isEqualByComparingTo(newPassword);
    }

    @Then("this password is (correct|not correct)")
    public void thenTheStatusOfPasswordIsCorrectOrNot(String correctOrNotCorrect) {

        Assertions.assertThat(scenarioContext.get("EXCEPTION", Exception.class)).isNull();

        boolean expected;
        switch (correctOrNotCorrect) {
            case "correct":
                expected = true;
                break;
            case "not correct":
                expected = false;
                break;
            default:
                throw new PendingException("not yet implemented");
        }

        Boolean actual = scenarioContext.get("IS_CORRECT", Boolean.class);
        Assertions.assertThat(actual).isEqualTo(expected);
    }
    //endregion

}