package com.test.infra.user;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/infra/user", glue = "com.test.infra.user", tags = {"@Infra", "~@Ignore"/*, "@SessionUser", "@CreateUser", "@UpdateUser", "@DeleteUser", "@ReadUser", "@ResetPassword", "@CheckPassword"*/})
public class InfraCucumberTest {


}
