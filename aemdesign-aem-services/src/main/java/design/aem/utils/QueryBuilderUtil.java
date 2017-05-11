package design.aem.utils;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

public final class QueryBuilderUtil {
    private QueryBuilderUtil() { }

    public static Collection<String> queryForPaths(ResourceResolver resourceResolver, Map queryMap) throws RepositoryException {
        return queryForPaths(resourceResolver.adaptTo(QueryBuilder.class), resourceResolver.adaptTo(Session.class), queryMap);
    }

    public static Collection<String> queryForPaths(QueryBuilder queryBuilder, Session session, Map queryMap) throws RepositoryException {
        Collection<String> collectedPaths = new LinkedHashSet<String>();

        Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);

        SearchResult result = query.getResult();
        for (Hit hit : result.getHits()) {
            String path = hit.getPath();
            collectedPaths.add(path);
        }
        return collectedPaths;
    }
}
