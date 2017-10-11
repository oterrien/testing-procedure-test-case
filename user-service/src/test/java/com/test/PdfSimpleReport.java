package com.test;

import com.itextpdf.text.DocumentException;
import gutenberg.itext.FontModifier;
import gutenberg.itext.Styles;
import gutenberg.itext.model.Markdown;
import org.apache.commons.io.IOUtils;
import tzatziki.analysis.exec.gson.JsonIO;
import tzatziki.analysis.exec.model.FeatureExec;
import tzatziki.pdf.support.Configuration;
import tzatziki.pdf.support.DefaultPdfReportBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class PdfSimpleReport {
    public void generate() throws IOException, DocumentException {
        List<FeatureExec> execs = loadExec(new File(buildDir(), "exec.json"));

        File fileOut = new File(buildDir(), "report.pdf");

        new DefaultPdfReportBuilder()
                .using(new Configuration()
                        .displayFeatureTags(true)
                        .displayScenarioTags(true)
                                /*.declareProperty("imageDir",
                                        new File(baseDir(), "/src/test/resources/feature/images").toURI().toString())*/
                        .adjustFont(Styles.TABLE_HEADER_FONT, new FontModifier().size(10.0f))
                )
                .title("user-service")
                .subTitle("Technical & Functional specifications")
                .markup(Markdown.fromUTF8(new FileInputStream(baseDir() + "/preamble.md")))
                .features(execs)
                .sampleSteps()
                .generate(fileOut);
    }

    private static File buildDir() {
        String baseDir = TestSettings.BUILD_DIR.getValue();
        return new File(baseDir);
    }

    private static File baseDir() {
        String baseDir = TestSettings.BASE_DIR.getValue();
        return new File(baseDir);
    }

    private static List<FeatureExec> loadExec(File file) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            return new JsonIO().load(in);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}