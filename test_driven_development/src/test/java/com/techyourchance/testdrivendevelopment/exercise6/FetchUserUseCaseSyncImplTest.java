package com.techyourchance.testdrivendevelopment.exercise6;

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

import java.util.List;

import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status.FAILURE;
import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status.NETWORK_ERROR;
import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status.SUCCESS;
import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointStatus.AUTH_ERROR;
import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointStatus.GENERAL_ERROR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSyncImplTest
{
    // region constants
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final User USER = new User(USER_ID, USER_NAME);
    // endregion constants
    
    // region helper fields
    @Mock FetchUserHttpEndpointSync fetchUserHttpEndpointSyncMock;
    @Mock UsersCache usersCacheMock;
    // endregion helper fields
    
    FetchUserUseCaseSyncImpl SUT;
    
    @Before
    public void setup() throws Exception
    {
        SUT = new FetchUserUseCaseSyncImpl(fetchUserHttpEndpointSyncMock, usersCacheMock);
        success();
    }
    
    // test: fetchUserSync - success - returns success response
    
    @Test
    public void fetchUserSync_notInCache_endpointSuccess_successReturned() throws Exception
    {
        // Arrange
        
        // Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
    
        // Assert
        assertThat(useCaseResult.getStatus(), is(SUCCESS));
    }
    
    // test: fetchUserSync - not in cache - authError - returns failure response
    
    @Test
    public void fetchUserSync_notInCache_endpointAuthError_failureReturned() throws Exception
    {
        // Arrange
        authError();
        
        // Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
    
        // Assert
        assertThat(useCaseResult.getStatus(), is(FAILURE));
    }
    
    // test: fetchUserSync - authError - user is not cached
    
    @Test
    public void fetchUserSync_notInCache_endpointAuthError_userIsNotCached() throws Exception
    {
        // Arrange
        authError();
        
        // Act
        SUT.fetchUserSync(USER_ID);
        
        // Assert
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }
    
    // test: fetchUserSync - authError - user is not cached
    
    @Test
    public void fetchUserSync_notInCache_endpointAuthError_nullUserReturned() throws Exception
    {
        // Arrange
        authError();
        
        // Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
    
        // Assert
        assertThat(useCaseResult.getUser(), nullValue());
    }
    
    // test: fetchUserSync - generalError - returns failure response
    
    @Test
    public void fetchUserSync_notInCache_endpointGeneralError_failureReturned() throws Exception
    {
        // Arrange
        generalError();
        
        // Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        
        // Assert
        assertThat(useCaseResult.getStatus(), is(FAILURE));
    }
    
    // test: fetchUserSync - generalError - user is not cached
    
    @Test
    public void fetchUserSync_notInCache_endpointGeneralError_userIsNotCached() throws Exception
    {
        // Arrange
        generalError();
        
        // Act
        SUT.fetchUserSync(USER_ID);
        
        // Assert
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }
    
    // test: fetchUserSync - authError - user is not cached
    
    @Test
    public void fetchUserSync_notInCache_endpointGeneralError_nullUserReturned() throws Exception
    {
        // Arrange
        generalError();
        
        // Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        
        // Assert
        assertThat(useCaseResult.getUser(), nullValue());
    }
    
    // test: fetchUserSync - networkException - returns network exception response
    
    @Test
    public void fetchUserSync_notInCache_networkException_networkErrorReturned() throws Exception
    {
        // Arrange
        networkException();
        
        // Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        
        // Assert
        assertThat(useCaseResult.getStatus(), is(NETWORK_ERROR));
    }
    
    // test: fetchUserSync - networkException - user is not cached
    
    @Test
    public void fetchUserSync_notInCache_networkException_userIsNotCached() throws Exception
    {
        // Arrange
        networkException();
        
        // Act
        SUT.fetchUserSync(USER_ID);
        
        // Assert
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }
    
    // test: fetchUserSync - network exception - null user returned
    
    @Test
    public void fetchUserSync_notInCache_endpointNetworkException_nullUserReturned() throws Exception
    {
        // Arrange
        networkException();
        
        // Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        
        // Assert
        assertThat(useCaseResult.getUser(), nullValue());
    }
    
    // test: fetchUserSync - user is not in cache - fetched from endpoint
    
    @Test
    public void fetchUserSync_cacheIsNeverAccessed_userIdPassedToEndpoint() throws Exception
    {
        // Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        
        // Act
        SUT.fetchUserSync(USER_ID);
        
        // Assert
        verify(fetchUserHttpEndpointSyncMock).fetchUserSync(ac.capture());
        List<String> captures = ac.getAllValues();
        assertThat(captures.get(0), is(USER_ID));
    }
    
    // test: fetchUserSync - user is fetched from server - user should now be in cache
    
    @Test
    public void fetchUserSync_notInCache_endpointSuccess_userIsCached() throws Exception
    {
        // Arrange
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        
        // Act
        SUT.fetchUserSync(USER_ID);
        
        // Assert
        verify(usersCacheMock).cacheUser(ac.capture());
        List<User> captures = ac.getAllValues();
        assertThat(captures.get(0).getUserId(), is(USER_ID));
    }
    
    // test: fetchUserSync - user is in cache - user should be returned from cache and endpoint never accessed
    
    @Test
    public void fetchUserSync_userIsInCache_endpointNeverAccessed() throws Exception
    {
        // Arrange
        setupUserInCache();
        
        // Act
        SUT.fetchUserSync(USER_ID);
        
        // Assert
        verifyNoMoreInteractions(fetchUserHttpEndpointSyncMock);
    }
    
    // region helper methods
    
    public void success() throws NetworkErrorException
    {
        when(
                fetchUserHttpEndpointSyncMock.fetchUserSync(any(String.class))
        ).thenReturn(
                new FetchUserHttpEndpointSync.EndpointResult(FetchUserHttpEndpointSync.EndpointStatus.SUCCESS,
                        USER_ID,
                        USER_NAME
                )
        );
    }
    
    public void authError() throws NetworkErrorException
    {
        when(
                fetchUserHttpEndpointSyncMock.fetchUserSync(any(String.class))
        ).thenReturn(
                new FetchUserHttpEndpointSync.EndpointResult(AUTH_ERROR,
                        USER_ID,
                        USER_NAME
                )
        );
    }
    
    public void generalError() throws NetworkErrorException
    {
        when(
                fetchUserHttpEndpointSyncMock.fetchUserSync(any(String.class))
        ).thenReturn(
                new FetchUserHttpEndpointSync.EndpointResult(GENERAL_ERROR,
                        USER_ID,
                        USER_NAME
                )
        );
    }
    
    public void networkException() throws NetworkErrorException
    {
        when(
                fetchUserHttpEndpointSyncMock.fetchUserSync(any(String.class))
        ).thenThrow(NetworkErrorException.class);
    }
    
    private void setupUserInCache()
    {
        when(
                usersCacheMock.getUser(USER_ID)
        ).thenReturn(
                USER
        );
    }
    
    // endregion helper methods
    
    // region helper classes
    
    // endregion helper classes
}