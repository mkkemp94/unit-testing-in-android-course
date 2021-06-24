package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class FetchUserProfileUseCaseSyncTest
{
    UserProfileHttpEndpointSyncTd endpointTd;
    UsersCacheTd usersCacheTd;
    
    FetchUserProfileUseCaseSync SUT;
    
    private static final String FULL_NAME = "full name";
    private static final String IMAGE_URL = "image url";
    private static final String USER_ID = "user id";
    
    @Before
    public void setUp() throws Exception
    {
        endpointTd = new UserProfileHttpEndpointSyncTd();
        usersCacheTd = new UsersCacheTd();
        SUT = new FetchUserProfileUseCaseSync(
                endpointTd,
                usersCacheTd
        );
    }
    
    // make sure user id is passed to endpoint success
    
    @Test
    public void fetchUserProfileSync_success_userIdPassedToEndpoint()
    {
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(endpointTd.userId, is(USER_ID));
    }
    
    // make sure user is cached when success
    
    @Test
    public void fetchUserProfileSync_success_userIsCached()
    {
        SUT.fetchUserProfileSync(USER_ID);
        User cachedUser = usersCacheTd.getUser(USER_ID);
        assertThat(cachedUser.getFullName(), is(FULL_NAME));
        assertThat(cachedUser.getUserId(), is(USER_ID));
        assertThat(cachedUser.getImageUrl(), is(IMAGE_URL));
    }
    
    // make sure we get correct error response when auth error occurs
    
    @Test
    public void fetchUserProfileSync_authError_returnsFailure()
    {
        endpointTd.authError = true;
        FetchUserProfileUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }
    
    // make sure we get correct error response when server error occurs
    
    @Test
    public void fetchUserProfileSync_serverError_returnsFailure()
    {
        endpointTd.serverError = true;
        FetchUserProfileUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }
    
    // make sure we get correct error response when general error occurs
    
    @Test
    public void fetchUserProfileSync_generalError_returnsFailure()
    {
        endpointTd.generalError = true;
        FetchUserProfileUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }
    
    // make sure we get correct error response when network error occurs
    
    @Test
    public void fetchUserProfileSync_networkError_returnsNetworkError()
    {
        endpointTd.networkError = true;
        FetchUserProfileUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(FetchUserProfileUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }
    
    // make sure user is not cached when auth error occurs
    
    @Test
    public void fetchUserProfileSync_authError_userIsNotCached()
    {
        endpointTd.authError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(usersCacheTd.getUser(USER_ID), is(nullValue()));
    }
    
    // make sure user is not cached when server error occurs
    
    @Test
    public void fetchUserProfileSync_serverError_userIsNotCached()
    {
        endpointTd.serverError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(usersCacheTd.getUser(USER_ID), is(nullValue()));
    }
    
    // make sure user is not cached when general error occurs
    
    @Test
    public void fetchUserProfileSync_generalError_userIsNotCached()
    {
        endpointTd.generalError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(usersCacheTd.getUser(USER_ID), is(nullValue()));
    }
    
    // make sure user is not cached when network error occurs
    
    @Test
    public void fetchUserProfileSync_networkError_userIsNotCached()
    {
        endpointTd.networkError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(usersCacheTd.getUser(USER_ID), is(nullValue()));
    }
    
    // -----------------------------------------------------
    // Helper classes
    
    // When I call the endpoint, I expect the user will be cached
    
    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync
    {
        // cached user id
        public String userId;
        
        // mock(?) status flags
        public boolean authError = false;
        public boolean serverError = false;
        public boolean generalError = false;
        public boolean networkError = false;
        
        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException
        {
            // cache the user id to show that it was passed to endpoint
            this.userId = userId;
            
            if (authError)
            {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, userId, "", "");
            }
            else if (serverError)
            {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, userId, "", "");
            }
            else if (generalError)
            {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, userId, "", "");
            }
            else if (networkError)
            {
                throw new NetworkErrorException();
            }
            else
            {
                // no error. return success.
                // assume the name and email data that reached here was the given data.
                return new EndpointResult(EndpointResultStatus.SUCCESS, userId, FULL_NAME, IMAGE_URL);
            }
        }
    }
    
    private static class UsersCacheTd implements UsersCache
    {
        public ArrayList<User> users = new ArrayList<>(1);
        
        @Override
        public void cacheUser(User user)
        {
            // cache the user data
            // this is a mock of the actual implementation
            User existingUser = getUser(user.getUserId());
            if (existingUser != null)
            {
                users.remove(existingUser);
            }
            
            users.add(user);
        }
        
        @Nullable
        @Override
        public User getUser(String userId)
        {
            for (User user : users)
            {
                if (user.getUserId().equals(userId))
                {
                    return user;
                }
            }
            return null;
        }
    }
}