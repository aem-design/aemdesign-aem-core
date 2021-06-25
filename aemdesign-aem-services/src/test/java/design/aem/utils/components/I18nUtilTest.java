package design.aem.utils.components;

import com.day.cq.i18n.I18n;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class I18nUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(I18nUtilTest.class);

    @Mock
    I18n i18n;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = openMocks(this);
    }

    @AfterEach
    void close() throws Exception {
        closeable.close();
    }

    @Test
    public void testClass() {
        // Run the test
        I18nUtil test = new I18nUtil();

        // Verify the results
        Assertions.assertNotNull(test);
    }

    @Test
    public void testGetDefaultLabelIfEmpty() {
        // Setup
        final String currentLabel = "currentLabel";
        final String currentCategory = "currentCategory";
        final String defaultCode = "defaultCode";
        final String defaultCategory = "defaultCategory";

        final String params = "params";
        final String expectedResult = "result";

        when(i18n.get(defaultCode, defaultCategory, params)).thenReturn(expectedResult);
        when(i18n.get(currentLabel, currentCategory, params)).thenReturn(expectedResult);

        // Run the test
        final String result = I18nUtil.getDefaultLabelIfEmpty(currentLabel, currentCategory, defaultCode, defaultCategory, i18n, params);


        // Verify the results
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testGetDefaultLabelIfEmptyNoLabel() {
        // Setup
        final String currentLabel = "";
        final String currentCategory = "currentCategory";
        final String defaultCode = "defaultCode";
        final String defaultCategory = "defaultCategory";

        final String params = "params";
        final String expectedResult = "result";


        when(i18n.get(defaultCode, defaultCategory, params)).thenReturn(expectedResult);
        when(i18n.get(currentLabel, currentCategory, params)).thenReturn(expectedResult);

        // Run the test
        final String result = I18nUtil.getDefaultLabelIfEmpty(currentLabel, currentCategory, defaultCode, defaultCategory, i18n, params);


        // Verify the results
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testGetDefaultLabelIfEmptyMissingLabel() {
        // Setup
        final String currentLabel = "";
        final String currentCategory = "currentCategory";
        final String defaultCode = "defaultCode";
        final String defaultCategory = "defaultCategory";

        final String params = "params";
        final String expectedResult = "Missing Label";


        when(i18n.get(defaultCode, defaultCategory, params)).thenReturn("");
        when(i18n.get(currentLabel, currentCategory, params)).thenReturn("");

        // Run the test
        final String result = I18nUtil.getDefaultLabelIfEmpty(currentLabel, currentCategory, defaultCode, defaultCategory, i18n, params);


        // Verify the results
        Assertions.assertEquals(expectedResult, result);
    }


    @Test
    public void testGetDefaultLabelIfEmptyDefault() {
        // Setup
        final String defaultLabel = "defaultLabel";
        final String defaultCode = "defaultCode";
        final String defaultCategory = "defaultCategory";

        final String params = "params";
        final String expectedResult = "result";


        when(i18n.get(defaultCode, defaultCategory, params)).thenReturn(expectedResult);

        // Run the test
        final String result = I18nUtil.getDefaultLabelIfEmpty(defaultCode, defaultCategory, defaultLabel, i18n, params);


        // Verify the results
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testGetDefaultLabelIfEmptyDefaultEmpty() {
        // Setup
        final String defaultLabel = "defaultLabel";
        final String defaultCode = "defaultCode";
        final String defaultCategory = "defaultCategory";

        final String params = "params";


        when(i18n.get(defaultCode, defaultCategory, params)).thenReturn("");

        // Run the test
        final String result = I18nUtil.getDefaultLabelIfEmpty(defaultCode, defaultCategory, defaultLabel, i18n, params);


        // Verify the results
        Assertions.assertEquals(defaultLabel, result);
    }
}
