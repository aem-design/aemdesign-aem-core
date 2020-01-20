package design.aem.utils.components;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ParagraphUtilTest {

    @Test
    public void testClass() {
        ParagraphUtil test = new ParagraphUtil();
        assertNotNull(test);
    }
}
