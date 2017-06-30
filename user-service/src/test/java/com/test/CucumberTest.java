package com.test;

import com.test.userservice.business.UserService;
import com.test.userservice.business.UserServiceWithAuthorization;
import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.junit.Cucumber;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features")
public class CucumberTest {

    public static class StepDefinitions {

        private Map<String, Object> context = new HashMap<>();

        private UserServiceWithAuthorization userService;

        @Before
        public void setUp() {

        }

        @After
        public void tearDown() {
            context.clear();
        }

        //region GIVEN
        @Given("I am a user with role '(.*)'")
        public void given_I_am_a_user(User.Role userRole) {

            User user = new User();
            user.setLogin("myLogin");
            user.setPassword("anyPassword");
            user.setRole(userRole);

            userService = new UserServiceWithAuthorization(new UserService(new UserRepositoryMock()), user);
        }
        //endregion

        //region WHEN
        @When("I want to create a user with role '(.*)'")
        public void when_I_want_to_create_a_user_with_a_given_role(User.Role userRole) {

            User user = new User();
            user.setLogin("anyLogin");
            user.setPassword("anyPassword");
            user.setRole(userRole);

            create_a_user(user);
        }

        private void create_a_user(User user) {
            try {
                int id = userService.create(user);
                context.put("ID", id);
            } catch (Exception e) {
                context.put("EXCEPTION", e);
            }
        }
        //endregion

        // region THEN
        @Then("I am not authorized")
        public void then_I_am_not_authorized() {

            Assertions.assertThat(context.get("EXCEPTION")).isNotNull();
            Exception e = (Exception) context.get("EXCEPTION");
            Assertions.assertThat(e).isInstanceOf(UserServiceWithAuthorization.NotAuthorizedException.class);
        }

        @Then("the user is created")
        public void then_user_is_created() {

            Assertions.assertThat(context.get("ID")).isNotNull();
            int id = ((Integer) context.get("ID")).intValue();
            Assertions.assertThat(id).isPositive();
        }
        //endregion


    }


}
