package design.aem.models.v2.lists;

public class NewsList extends List {
    @Override
    protected String getComponentName() {
        return "newslist";
    }

    @Override
    protected String[] getDetailsComponentLookupNames() {
        return new String[]{"news-details", "generic-details"};
    }
}
