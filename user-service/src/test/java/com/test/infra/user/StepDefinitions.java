package com.test.infra.user;

import com.test.Application;
import com.test.domain.user.api.UserRole;
import com.test.domain.user.business.UserServiceWithAuthorization;
import com.test.infra.user.authentication.AuthenticationHttpFilter;
import com.test.infra.user.authentication.SessionProviderService;
import com.test.infra.user.authentication.UserSessionProviderService;
import com.test.infra.user.rest.UserPayload;
import com.test.infra.user.service.repository.UserEntity;
import com.test.infra.user.service.repository.UserJpaRepository;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sun.misc.BASE64Encoder;

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserSessionProviderService<UserEntity> userSessionProviderService;

    @Autowired
    private SessionProviderService<UserEntity> sessionProviderService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).
                addFilters(new AuthenticationHttpFilter(userSessionProviderService, sessionProviderService, userJpaRepository)).
                build();

        ScriptUtils.executeSqlScript(
                jdbcTemplate.getDataSource().getConnection(),
                new ClassPathResource("clean_users.sql")
        );

        ScriptUtils.executeSqlScript(
                jdbcTemplate.getDataSource().getConnection(),
                new ClassPathResource("add_admin_user.sql")
        );
    }

    @After
    public void tearDown() throws Exception {
        context.clear();
    }

    //region GIVEN
    @Given("I am '(.*)' user")
    public void given_I_am_a_user(String login) {

        context.put("USER_LOGIN", login);
    }

    @Given("my password is '(.*)'")
    public void given_my_password(String password) {

        context.put("USER_PASSWORD", password);
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

            String myCredentialsInBase64 = new BASE64Encoder().encode((context.get("USER_LOGIN") + ":" + context.get("USER_PASSWORD")).getBytes());

            MvcResult result = mockMvc.perform(post("/api/v1/users").
                    header("Authorization", "Basic " + myCredentialsInBase64).
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

        String myCredentialsInBase64 = new BASE64Encoder().encode((context.get("USER_LOGIN") + ":" + context.get("USER_PASSWORD")).getBytes());

        MvcResult result = mockMvc.perform(get("/api/v1/users/{id}", id).
                header("Authorization", "Basic " + myCredentialsInBase64).
                contentType(MediaType.APPLICATION_JSON_VALUE)).
                andReturn();

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        UserPayload payload = parseFromJson(result.getResponse().getContentAsString(), UserPayload.class);

        Assertions.assertThat(payload).isNotNull();


    }
    //endregion


}