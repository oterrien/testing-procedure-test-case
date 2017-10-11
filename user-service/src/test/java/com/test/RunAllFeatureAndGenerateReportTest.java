package com.test;

import com.test.infra.user.InfraCucumberTest;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({InfraCucumberTest.class})
public class RunAllFeatureAndGenerateReportTest {

    @AfterClass
    public static void generateExecutionReport() throws Exception {
        new PdfSimpleReport().generate();
    }
}