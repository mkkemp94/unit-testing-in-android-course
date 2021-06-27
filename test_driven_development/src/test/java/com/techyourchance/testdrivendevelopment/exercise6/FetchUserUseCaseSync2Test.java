package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status;
import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSync2Test
{
    // region constants
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final User USER = new User(USER_ID, USER_NAME);
    // endregion constants
    
    // region helper fields
    FetchUserHttpEndpointSyncTd fetchUserHttpEndpointSyncTd;
    @Mock UsersCache usersCacheMock;
    // endregion helper fields
    
    FetchUserUseCaseSync2 SUT;
    
    @Before
    public void setup()
    {
        fetchUserHttpEndpointSyncTd = new FetchUserHttpEndpointSyncTd();
        SUT = new FetchUserUseCaseSync2(fetchUserHttpEndpointSyncTd, usersCacheMock);
    }
    
    @Test
    public void fetchUserSync_correctUserIdPassedToCache()
    {
        // Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        setup_userInCache();
        
        // Act
        SUT.fetchUserSync(USER_ID);
        
        // Assert
        verify(usersCacheMock).getUser(ac.capture());
        assertThat(ac.getValue(), is(USER_ID));
    }
    
    @Test
    public void fetchUserSync_inCache_returnSuccess()
    {
        // Arrange
        setup_userInCache();
        
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        
        // Assert
        assertThat(result.getStatus(), is(Status.SUCCESS));
    }
    
    @Test
    public void fetchUserSync_inCacheSuccess_returnsUserWithCorrectUserId()
    {
        // Arrange
        setup_userInCache();
        
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
    
        // Assert
        assertThat(result.getUser().getUserId(), is(USER_ID));
    }
    
    @Test
    public void fetchUserSync_notInCache_fetchFromEndpointSuccess_returnsSuccess()
    {
        // Arrange
        setup_userNotInCache();
        
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        
        // Assert
        assertThat(result.getStatus(), is(Status.SUCCESS));
    }
    
    @Test
    public void fetchUserSync_notInCache_fetchFromEndpointSuccess_returnsUserWithCorrectUserId()
    {
        // Arrange
        setup_userNotInCache();
        
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        
        // Assert
        assertThat(result.getUser().getUserId(), is(USER_ID));
    }
    
    @Test
    public void fetchUserSync_notInCache_fetchFromEndpointSuccess_cachesUser()
    {
        // Arrange
        setup_userNotInCache();
        
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        
        // Act
        SUT.fetchUserSync(USER_ID);
        
        // Assert
        verify(usersCacheMock).cacheUser(ac.capture());
        assertThat(ac.getValue().getUserId(), is(USER_ID));
    }
    
    @Test
    public void fetchUserSync_notInCache_fetchFromEndpointAuthError_returnsFailure()
    {
        // Arrange
        setup_userNotInCache();
        setup_endpointAuthError();
        
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
    
        // Assert
        assertThat(result.getStatus(), is(Status.FAILURE));
    }
    
    // fetchUserSync_notInCache_fetchFromEndpointAuthError_userNameEmpty
    
    @Test
    public void fetchUserSync_notInCache_fetchFromEndpointAuthError_userNull()
    {
        // Arrange
        setup_userNotInCache();
        setup_endpointAuthError();
        
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        
        // Assert
        assertNull(result.getUser());
    }
    
    @Test
    public void fetchUserSync_notInCache_fetchFromEndpointGeneralError_returnsFailure()
    {
        // Arrange
        setup_userNotInCache();
        setup_endpointGeneralError();
        
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        
        // Assert
        assertThat(result.getStatus(), is(Status.FAILURE));
    }
    
    // fetchUserSync_notInCache_fetchFromEndpointGeneralError_userNameEmpty
    
    @Test
    public void fetchUserSync_notInCache_fetchFromEndpointGeneralError_userNotCached()
    {
        // Arrange
        setup_userNotInCache();
        setup_endpointGeneralError();
        
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        
        // Assert
        assertNull(result.getUser());
    }
    
    @Test
    public void fetchUserSync_notInCache_fetchFromEndpointNetworkError_returnsFailure()
    {
        // Arrange
        setup_userNotInCache();
        setup_endpointNetworkError();
        
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        
        // Assert
        assertThat(result.getStatus(), is(Status.NETWORK_ERROR));
    }
    
    @Test
    public void fetchUserSync_notInCache_fetchFromEndpointNetworkError_userNull()
    {
        // Arrange
        setup_userNotInCache();
        setup_endpointNetworkError();
        
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        
        // Assert
        assertNull(result.getUser());
    }
    
    @Test
    public void fetchUserSync_inCache_endpointNotPolled()
    {
        // Arrange
        setup_userInCache();
        
        // Act
        SUT.fetchUserSync(USER_ID);
        
        // Assert
        assertThat(fetchUserHttpEndpointSyncTd.requestCount, is(0));
    }
    
    @Test
    public void fetchUserSync_notInCache_endpointPolledSingleTime()
    {
        // Arrange
        setup_userNotInCache();
        
        // Act
        SUT.fetchUserSync(USER_ID);
        
        // Assert
        assertThat(fetchUserHttpEndpointSyncTd.requestCount, is(1));
    }
    
    // region helper methods
    private void setup_userInCache()
    {
        when(usersCacheMock.getUser(anyString()))
                .thenReturn(USER);
    }
    
    private void setup_userNotInCache()
    {
        when(usersCacheMock.getUser(anyString()))
                .thenReturn(null);
    }
    
    private void setup_endpointAuthError()
    {
        fetchUserHttpEndpointSyncTd.authError = true;
    }
    
    private void setup_endpointGeneralError()
    {
        fetchUserHttpEndpointSyncTd.generalError = true;
    }
    
    private void setup_endpointNetworkError()
    {
        fetchUserHttpEndpointSyncTd.networkError = true;
    }
    // endregion helper methods
    
    // region helper classes
    private static class FetchUserHttpEndpointSyncTd implements FetchUserHttpEndpointSync
    {
        public boolean authError = false;
        public boolean generalError = false;
        public boolean networkError = false;
        public int requestCount = 0;
    
        @Override
        public EndpointResult fetchUserSync(String userId) throws NetworkErrorException
        {
            requestCount++;
            
            if (authError)
            {
                return new EndpointResult(EndpointStatus.AUTH_ERROR, userId, "");
            }
            else if (generalError)
            {
                return new EndpointResult(EndpointStatus.GENERAL_ERROR, userId, "");
            }
            else if (networkError)
            {
                throw new NetworkErrorException();
            }
            else
            {
                return new EndpointResult(EndpointStatus.SUCCESS, userId, USER_NAME);
            }
        }
    }
    // endregion helper classes
}