package com.test.infra.user;

import com.test.Application;
import com.test.domain.user.api.UserRole;
import com.test.domain.user.business.UserServiceWithAuthorization;
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

import java.util.HashMap;
import java.util.Map;

import static com.test.JSonUtils.parseFromJson;
import static com.test.JSonUtils.serializeToJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ContextConfiguration(classes = Application.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StepDefinitions {

    private Map<String, Object> context = new HashMap<>();

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() {
        context.clear();
    }

    //region GIVEN
    @Given("I am a user with role '(.*)'")
    public void given_I_am_a_user(UserRole userRole) {

        context.put("USER_ROLE", userRole);
    }
    //endregion

    //region WHEN
    @When("I want to create a user with role '(.*)'")
    public void when_I_want_to_create_a_user_with_a_given_role(UserRole userRole) throws Exception {

        try {
            UserPayload userPayload = new UserPayload();
            userPayload.setLogin("myLogin");
            userPayload.setPassword("anyPassword");
            userPayload.setRole(userRole);

            MvcResult result = mockMvc.perform(post("/api/v1/users").
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                    content(serializeToJson(userPayload))).
                    andReturn();

            Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

            UserPayload userPayloadResult = parseFromJson(result.getResponse().getContentAsString(), UserPayload.class);

            context.put("ID", userPayloadResult.getId());

        } catch (UserServiceWithAuthorization.NotAuthorizedException e) {
            context.put("EXCEPTION", e);
        }
    }
    //endregion

    // region THEN
    @Then("I am not authorized")
    public void then_I_am_not_authorized() {

        Object ex = context.get("EXCEPTION");

        Assertions.assertThat(ex).isNotNull();
        Assertions.assertThat(ex).isInstanceOf(UserServiceWithAuthorization.NotAuthorizedException.class);
    }

    @Then("the user is created")
    public void then_user_is_created() throws Exception {

        Integer id = (Integer) context.get("ID");

        MvcResult result = mockMvc.perform(get("/api/v1/users/{id}", id).
                contentType(MediaType.APPLICATION_JSON_VALUE)).
                andReturn();

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        UserPayload payload = parseFromJson(result.getResponse().getContentAsString(), UserPayload.class);

        Assertions.assertThat(payload).isNotNull();


    }
    //endregion


}