package design.aem.utils.components;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
    public void testIsAdminUserMemberOfGroup() throws Exception {
        // Setup
        String userPath = "/home/users/a/admin";
        final List<String> groups = new ArrayList<String>();
        groups.add("administrators");

        final List<Group> memberOf = new ArrayList<Group>();


        Authorizable adminUserAuth = mock(Authorizable.class, withSettings().extraInterfaces(User.class)); mock(Authorizable.class);

        User adminUser = (User)adminUserAuth;

        when(adminUser.isAdmin()).thenReturn(true);

        Group administrators = mock(Group.class);
        Principal adminPrinciple = mock(Principal.class);


        when(administrators.getPrincipal()).thenReturn(adminPrinciple);
        when(adminPrinciple.getName()).thenReturn("administrators");

        memberOf.add(administrators);

        when(adminUserAuth.memberOf()).thenReturn(memberOf.iterator());


        // Run the test
        final boolean result = SecurityUtil.isUserMemberOf(adminUserAuth, groups);

        LOGGER.error("result={}", result);

        // Verify the results
        assertTrue(result);
    }

    @Test
    public void testIsUserMemberOfGroup() throws Exception {
        // Setup
        String userPath = "/home/users/a/admin";
        final List<String> groups = new ArrayList<String>();
        groups.add("administrators");

        final List<Group> memberOf = new ArrayList<Group>();


        Authorizable adminUserAuth = mock(Authorizable.class, withSettings().extraInterfaces(User.class)); mock(Authorizable.class);

        User adminUser = (User)adminUserAuth;

        when(adminUser.isAdmin()).thenReturn(false);

        Group administrators = mock(Group.class);
        Group users = mock(Group.class);
        Principal adminPrinciple = mock(Principal.class);
        Principal usersPrinciple = mock(Principal.class);


        when(administrators.getPrincipal()).thenReturn(adminPrinciple);
        when(adminPrinciple.getName()).thenReturn("administrators");
        when(users.getPrincipal()).thenReturn(usersPrinciple);
        when(usersPrinciple.getName()).thenReturn("users");

        memberOf.add(users);
        memberOf.add(administrators);

        when(adminUserAuth.memberOf()).thenReturn(memberOf.iterator());


        // Run the test
        final boolean result = SecurityUtil.isUserMemberOf(adminUserAuth, groups);

        LOGGER.error("result={}", result);

        // Verify the results
        assertTrue(result);
    }

    @Test
    public void testIsUserWithoutMembershipMemberOfGroup() throws Exception {
        // Setup
        String userPath = "/home/users/a/admin";
        final List<String> groups = new ArrayList<String>();
        groups.add("administrators");

        final List<Group> memberOf = new ArrayList<Group>();


        Authorizable adminUserAuth = mock(Authorizable.class, withSettings().extraInterfaces(User.class)); mock(Authorizable.class);

        User adminUser = (User)adminUserAuth;

        when(adminUser.isAdmin()).thenReturn(false);

        when(adminUserAuth.memberOf()).thenReturn(memberOf.iterator());


        // Run the test
        final boolean result = SecurityUtil.isUserMemberOf(adminUserAuth, groups);

        LOGGER.error("result={}", result);

        // Verify the results
        assertFalse(result);
    }

    @Test
    public void testIsUserWithMembershipMemberOfGroup() throws Exception {
        // Setup
        String userPath = "/home/users/a/admin";
        final List<String> groups = new ArrayList<String>();
        groups.add("administrators");

        final List<Group> memberOf = new ArrayList<Group>();


        Authorizable adminUserAuth = mock(Authorizable.class, withSettings().extraInterfaces(User.class)); mock(Authorizable.class);

        User adminUser = (User)adminUserAuth;

        when(adminUser.isAdmin()).thenReturn(false);

        Group users = mock(Group.class);
        Principal usersPrinciple = mock(Principal.class);


        when(users.getPrincipal()).thenReturn(usersPrinciple);
        when(usersPrinciple.getName()).thenReturn("users");

        memberOf.add(users);


        when(adminUserAuth.memberOf()).thenReturn(memberOf.iterator());


        // Run the test
        final boolean result = SecurityUtil.isUserMemberOf(adminUserAuth, groups);

        LOGGER.error("result={}", result);

        // Verify the results
        assertFalse(result);
    }


}
