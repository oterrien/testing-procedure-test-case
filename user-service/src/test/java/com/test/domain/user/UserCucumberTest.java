package com.test.domain.user;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/domain/user", glue = "com.test.domain.user", tags = {"@Business", "~@Ignore"})
public class UserCucumberTest {


}
