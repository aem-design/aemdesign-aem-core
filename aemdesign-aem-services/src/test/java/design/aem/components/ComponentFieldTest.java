package design.aem.components;


import org.junit.jupiter.api.Test;

public class ComponentFieldTest {

    @Test
    public void ComponentFieldSimpleTest() {

        String[] field = new String[]{"text", "${ value ? value : label }", "data-text"};

        ComponentField expField = new ComponentField(field);
        expField.setValue("test");
        assert expField.getValue().equals("test");
        assert expField.getDefaultValue().equals("${ value ? value : label }");
        assert expField.getDataAttributeName().equals("data-text");
        assert expField.getFieldName().equals("text");

    }

    @Test
    public void ComponentFieldAdvancedTest() {

        ComponentField expField = new ComponentField("text", "test1", "data-text", "Tags", "${ value ? value : label }", "test2");
        assert expField.getValue().equals("test2");
        assert expField.getExpression().equals("${ value ? value : label }");
        assert expField.getDataAttributeName().equals("data-text");
        assert expField.getDefaultValue().equals("test1");
        assert expField.getFieldName().equals("text");
        assert expField.getMultiValueClass().equals("Tags");

        expField.setValue("test3");
        assert expField.getValue().equals("test3");

        expField.setExpression("${ value ? value : label2 }");
        assert expField.getExpression().equals("${ value ? value : label2 }");

        expField.setDataAttributeName("data-text1");
        assert expField.getDataAttributeName().equals("data-text1");

        expField.setDefaultValue("test2");
        assert expField.getDefaultValue().equals("test2");

        expField.setFieldName("text1");
        assert expField.getFieldName().equals("text1");

        expField.setMultiValueClass("Tags1");
        assert expField.getMultiValueClass().equals("Tags1");

    }
//
//    @Test
//    void getFieldName() {
//    }
//
//    @Test
//    void setFieldName() {
//    }
//
//    @Test
//    void getDefaultValue() {
//    }
//
//    @Test
//    void setDefaultValue() {
//    }
//
//    @Test
//    void getDataAttributeName() {
//    }
//
//    @Test
//    void setDataAttributeName() {
//    }
//
//    @Test
//    void getMultiValueClass() {
//    }
//
//    @Test
//    void setMultiValueClass() {
//    }
//
//    @Test
//    void getExpression() {
//    }
//
//    @Test
//    void setExpression() {
//    }
//
//    @Test
//    void getValue() {
//    }
//
//    @Test
//    void setValue() {
//    }
//
//    @Test
//    void testToString() {
//    }
}
