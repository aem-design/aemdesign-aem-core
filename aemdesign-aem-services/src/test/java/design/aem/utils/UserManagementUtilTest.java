package design.aem.utils;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Test;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserManagementUtilTest {

//    @Test
//    public void testGetUserManager() throws Exception {
//        // Setup
//        final Session session = null;
//        final UserManager expectedResult = null;
//
//        // Run the test
//        final UserManager result = UserManagementUtil.getUserManager(session);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetUserManager_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final Session session = null;
//
//        // Run the test
//        UserManagementUtil.getUserManager(session);
//    }
//
//    @Test
//    public void testGetCurrentUser() throws Exception {
//        // Setup
//        final Session session = null;
//        final User expectedResult = null;
//
//        // Run the test
//        final User result = UserManagementUtil.getCurrentUser(session);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetCurrentUser_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final Session session = null;
//
//        // Run the test
//        UserManagementUtil.getCurrentUser(session);
//    }
//
//    @Test
//    public void testGetCurrentUser1() throws Exception {
//        // Setup
//        final ResourceResolver resourceResolver = null;
//        final User expectedResult = null;
//
//        // Run the test
//        final User result = UserManagementUtil.getCurrentUser(resourceResolver);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetCurrentUser_ThrowsRepositoryException1() throws Exception {
//        // Setup
//        final ResourceResolver resourceResolver = null;
//
//        // Run the test
//        UserManagementUtil.getCurrentUser(resourceResolver);
//    }
//
//    @Test
//    public void testGetAuthorizable() throws Exception {
//        // Setup
//        final Session session = null;
//        final String authorizableId = "authorizableId";
//        final Authorizable expectedResult = null;
//
//        // Run the test
//        final Authorizable result = UserManagementUtil.getAuthorizable(session, authorizableId);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetAuthorizable_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final Session session = null;
//        final String authorizableId = "authorizableId";
//
//        // Run the test
//        UserManagementUtil.getAuthorizable(session, authorizableId);
//    }
//
//    @Test
//    public void testGetUser() throws Exception {
//        // Setup
//        final Session session = null;
//        final String userId = "userId";
//        final User expectedResult = null;
//
//        // Run the test
//        final User result = UserManagementUtil.getUser(session, userId);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetUser_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final Session session = null;
//        final String userId = "userId";
//
//        // Run the test
//        UserManagementUtil.getUser(session, userId);
//    }
//
//    @Test
//    public void testGetGroup() throws Exception {
//        // Setup
//        final Session session = null;
//        final String groupId = "groupId";
//        final Group expectedResult = null;
//
//        // Run the test
//        final Group result = UserManagementUtil.getGroup(session, groupId);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetGroup_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final Session session = null;
//        final String groupId = "groupId";
//
//        // Run the test
//        UserManagementUtil.getGroup(session, groupId);
//    }
//
//    @Test
//    public void testGetDisplayName() {
//        // Setup
//        final ResourceResolver resourceResolver = null;
//        final String authorizableId = "authorizableId";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = UserManagementUtil.getDisplayName(resourceResolver, authorizableId);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testIsActive() throws Exception {
//        // Setup
//        final Session session = null;
//        final Authorizable authorizable = null;
//
//        // Run the test
//        final boolean result = UserManagementUtil.isActive(session, authorizable);
//
//        // Verify the results
//        assertTrue(result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testIsActive_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final Session session = null;
//        final Authorizable authorizable = null;
//
//        // Run the test
//        UserManagementUtil.isActive(session, authorizable);
//    }
//
//    @Test
//    public void testGetEmail() throws Exception {
//        // Setup
//        final Authorizable authorizable = null;
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = UserManagementUtil.getEmail(authorizable);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetEmail_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final Authorizable authorizable = null;
//
//        // Run the test
//        UserManagementUtil.getEmail(authorizable);
//    }
//
//    @Test
//    public void testGetNameAndEmail() throws Exception {
//        // Setup
//        final ResourceResolver resourceResolver = null;
//        final Authorizable authorizable = null;
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = UserManagementUtil.getNameAndEmail(resourceResolver, authorizable);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetNameAndEmail_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final ResourceResolver resourceResolver = null;
//        final Authorizable authorizable = null;
//
//        // Run the test
//        UserManagementUtil.getNameAndEmail(resourceResolver, authorizable);
//    }
//
//    @Test
//    public void testDisableUser() throws Exception {
//        // Setup
//        final User user = null;
//
//        // Run the test
//        UserManagementUtil.disableUser(user);
//
//        // Verify the results
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testDisableUser_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final User user = null;
//
//        // Run the test
//        UserManagementUtil.disableUser(user);
//    }
//
//    @Test
//    public void testDisableUser1() throws Exception {
//        // Setup
//        final User user = null;
//        final String reason = "reason";
//
//        // Run the test
//        UserManagementUtil.disableUser(user, reason);
//
//        // Verify the results
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testDisableUser_ThrowsRepositoryException1() throws Exception {
//        // Setup
//        final User user = null;
//        final String reason = "reason";
//
//        // Run the test
//        UserManagementUtil.disableUser(user, reason);
//    }
//
//    @Test
//    public void testEnableUser() throws Exception {
//        // Setup
//        final User user = null;
//
//        // Run the test
//        UserManagementUtil.enableUser(user);
//
//        // Verify the results
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testEnableUser_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final User user = null;
//
//        // Run the test
//        UserManagementUtil.enableUser(user);
//    }
//
//    @Test
//    public void testGetSingleValuedProperty() throws Exception {
//        // Setup
//        final Authorizable authorizable = null;
//        final String property = "property";
//        final Value expectedResult = null;
//
//        // Run the test
//        final Value result = UserManagementUtil.getSingleValuedProperty(authorizable, property);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetSingleValuedProperty_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final Authorizable authorizable = null;
//        final String property = "property";
//
//        // Run the test
//        UserManagementUtil.getSingleValuedProperty(authorizable, property);
//    }
//
//    @Test
//    public void testGetSingleValuedPropertyString() throws Exception {
//        // Setup
//        final Authorizable authorizable = null;
//        final String property = "property";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = UserManagementUtil.getSingleValuedPropertyString(authorizable, property);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetSingleValuedPropertyString_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final Authorizable authorizable = null;
//        final String property = "property";
//
//        // Run the test
//        UserManagementUtil.getSingleValuedPropertyString(authorizable, property);
//    }
//
//    @Test
//    public void testProvideAuthorizablesHavingEmail() throws Exception {
//        // Setup
//        final Authorizable rootAuthorizable = null;
//        final Collection<Authorizable> expectedResult = Arrays.asList();
//
//        // Run the test
//        final Collection<Authorizable> result = UserManagementUtil.provideAuthorizablesHavingEmail(rootAuthorizable);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testProvideAuthorizablesHavingEmail_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final Authorizable rootAuthorizable = null;
//
//        // Run the test
//        UserManagementUtil.provideAuthorizablesHavingEmail(rootAuthorizable);
//    }
}
