package design.aem.utils.components;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurityUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtilTest.class);

//    @Mock
//    Authorizable authorizable;
//
//    @Mock
//    UserProperties userProperties;
//
//    @Mock
//    User user;
//
//    @Mock
//    Group group;

    @Test
    public void testClass() {
        // Run the test
        SecurityUtil test = new SecurityUtil();

        // Verify the results
        assertNotNull(test);
    }

    @Test
    public void testIsUserMemberOf() throws Exception {
        // Setup
        String userPath = "/home/users/a/admin";
        final List<String> groups = new ArrayList<String>();
        groups.add("administrators");

        final List<Group> memberOf = new ArrayList<Group>();


        Authorizable adminUserAuth = mock(Authorizable.class);
        User adminUser = mock(User.class);
        Group administrators = mock(Group.class);

        when(adminUser.isAdmin()).thenReturn(true);

        memberOf.add(administrators);

        when(adminUserAuth.memberOf()).thenReturn(memberOf.iterator());

        // Run the test
        final boolean result = SecurityUtil.isUserMemberOf(adminUserAuth, groups);

        LOGGER.error("result={}", result);

        // Verify the results
        assertTrue(result);
    }


//
//    @Test
//    public void testGetUserEmail() {
//        // Setup
//        final UserManager _userManager = null;
//        final UserPropertiesManager _userPropertiesManager = null;
//        final String userId = "userId";
//        final String defaultValue = "defaultValue";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = SecurityUtil.getUserEmail(_userManager, _userPropertiesManager, userId, defaultValue);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testGetUserFullName() {
//        // Setup
//        final UserManager _userManager = null;
//        final UserPropertiesManager _userPropertiesManager = null;
//        final String userId = "userId";
//        final String defaultValue = "defaultValue";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = SecurityUtil.getUserFullName(_userManager, _userPropertiesManager, userId, defaultValue);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
}
