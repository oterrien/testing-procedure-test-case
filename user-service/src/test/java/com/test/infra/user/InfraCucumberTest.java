package com.test.infra.user;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/infra/user",
        glue = "com.test.infra.user",
        //tag = {"@Infra", "~@Ignore"}
        tags = {/*"@UserSession", */"@CreateUser"/*, "@UpdateUser", "@DeleteUser", "@ReadUser", "@ResetPassword", "@CheckPassword"*/},
        format = "tzatziki.analysis.exec.gson.JsonEmitterReport:target")
public class InfraCucumberTest {

}
