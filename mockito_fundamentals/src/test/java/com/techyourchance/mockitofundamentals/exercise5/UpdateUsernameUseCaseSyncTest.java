package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UpdateUsernameUseCaseSyncTest
{
    public static final String USER_NAME = "user_name";
    public static final String USER_ID = "user_id";
    
    UpdateUsernameUseCaseSync SUT;
    
    UpdateUsernameHttpEndpointSync endpointSync;
    UsersCache usersCache;
    EventBusPoster eventBusPoster;
    
    @Before
    public void setUp()
    {
        endpointSync = mock(UpdateUsernameHttpEndpointSync.class);
        usersCache = mock(UsersCache.class);
        eventBusPoster = mock(EventBusPoster.class);
        SUT = new UpdateUsernameUseCaseSync(endpointSync, usersCache, eventBusPoster);
    }
    
    @Test
    public void updateUsername_success_userNameAndIdPassedToEndpoint() throws NetworkErrorException
    {
        setup_onEndpointSyncUpdateUsername_returnSuccess();
        
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        
        // I'm passing user id and name to the SUT
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        
        // And want to make sure that as some point the endpoint's updateUsername() method
        // is called with the correct arguments
        verify(endpointSync).updateUsername(ac.capture(), ac.capture());
        
        List<String> captures = ac.getAllValues();
        assertThat(captures.get(0), is(USER_ID));
        assertThat(captures.get(1), is(USER_NAME));
    }
    
    @Test
    public void updateUsername_success_userCached() throws NetworkErrorException
    {
        setup_onEndpointSyncUpdateUsername_returnSuccess();
        
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        
        // I'm passing user id and name to the SUT
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        
        // And want to make sure that as some point the user is cached
        verify(usersCache).cacheUser(ac.capture());
        
        List<User> captures = ac.getAllValues();
        assertThat(captures.get(0).getUserId(), is(USER_ID));
        assertThat(captures.get(0).getUsername(), is(USER_NAME));
    }
    
    @Test
    public void updateUsername_authError_userNotCached() throws Exception
    {
        setup_onEndpointSyncUpdateUsername_returnAuthError();
    
        // I'm passing user id and name to the SUT
        SUT.updateUsernameSync(USER_ID, USER_NAME);
    
        // And want to make sure that as some point the user is NOT cached
        verifyNoMoreInteractions(usersCache);
    }
    
    @Test
    public void updateUsername_generalError_userNotCached() throws Exception
    {
        setup_onEndpointSyncUpdateUsername_returnGeneralError();
        
        // I'm passing user id and name to the SUT
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        
        // And want to make sure that as some point the user is NOT cached
        verifyNoMoreInteractions(usersCache);
    }
    
    @Test
    public void updateUsername_serverError_userNotCached() throws Exception
    {
        setup_onEndpointSyncUpdateUsername_returnServerError();
        
        // I'm passing user id and name to the SUT
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        
        // And want to make sure that as some point the user is NOT cached
        verifyNoMoreInteractions(usersCache);
    }
    
    @Test
    public void updateUsername_networkException_userNotCached() throws Exception
    {
        setup_onEndpointSyncUpdateUsername_throwNetworkException();
        
        // I'm passing user id and name to the SUT
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        
        // And want to make sure that as some point the user is NOT cached
        verifyNoMoreInteractions(usersCache);
    }
    
    @Test
    public void updateUsername_success_postsUserDetailsChangedEventToEventBus() throws NetworkErrorException
    {
        setup_onEndpointSyncUpdateUsername_returnSuccess();
        
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        
        // I'm passing user id and name to the SUT
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        
        // And want to make sure that UserDetailsChangedEvent is posted to event bus
        verify(eventBusPoster).postEvent(ac.capture());
        
        List<Object> captures = ac.getAllValues();
        assertThat(captures.get(0), is(instanceOf(UserDetailsChangedEvent.class)));
    }
    
    @Test
    public void updateUsername_authError_postsNoEventToEventBus() throws NetworkErrorException
    {
        setup_onEndpointSyncUpdateUsername_returnAuthError();
        
        // I'm passing user id and name to the SUT
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        
        // And want to make sure that nothing is posted to event bus
        verifyNoMoreInteractions(eventBusPoster);
    }
    
    @Test
    public void updateUsername_generalError_postsNoEventToEventBus() throws NetworkErrorException
    {
        setup_onEndpointSyncUpdateUsername_returnGeneralError();
        
        // I'm passing user id and name to the SUT
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        
        // And want to make sure that nothing is posted to event bus
        verifyNoMoreInteractions(eventBusPoster);
    }
    
    @Test
    public void updateUsername_serverError_postsNoEventToEventBus() throws NetworkErrorException
    {
        setup_onEndpointSyncUpdateUsername_returnServerError();
        
        // I'm passing user id and name to the SUT
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        
        // And want to make sure that nothing is posted to event bus
        verifyNoMoreInteractions(eventBusPoster);
    }
    
    @Test
    public void updateUsername_networkException_postsNoEventToEventBus() throws NetworkErrorException
    {
        setup_onEndpointSyncUpdateUsername_throwNetworkException();
        
        // I'm passing user id and name to the SUT
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        
        // And want to make sure that nothing is posted to event bus
        verifyNoMoreInteractions(eventBusPoster);
    }
    
    @Test
    public void updateUsername_success_successReturned() throws NetworkErrorException
    {
        setup_onEndpointSyncUpdateUsername_returnSuccess();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS));
    }
    
    @Test
    public void updateUsername_authError_failureReturned() throws NetworkErrorException
    {
        setup_onEndpointSyncUpdateUsername_returnAuthError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }
    
    @Test
    public void updateUsername_generalError_failureReturned() throws NetworkErrorException
    {
        setup_onEndpointSyncUpdateUsername_returnGeneralError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }
    
    @Test
    public void updateUsername_serverError_failureReturned() throws NetworkErrorException
    {
        setup_onEndpointSyncUpdateUsername_returnServerError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }
    
    @Test
    public void updateUsername_networkException_networkErrorReturned() throws NetworkErrorException
    {
        setup_onEndpointSyncUpdateUsername_throwNetworkException();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }
    
    // -------------------------------------------------------------------
    // Additional setup
    
    private void setup_onEndpointSyncUpdateUsername_returnSuccess() throws NetworkErrorException
    {
        when(
                endpointSync.updateUsername(any(String.class), any(String.class))
        ).thenReturn(
                new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS,
                        USER_ID,
                        USER_NAME
                )
        );
    }
    
    private void setup_onEndpointSyncUpdateUsername_returnAuthError() throws NetworkErrorException
    {
        when(
                endpointSync.updateUsername(any(String.class), any(String.class))
        ).thenReturn(
                new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR,
                        USER_ID,
                        USER_NAME
                )
        );
    }
    
    private void setup_onEndpointSyncUpdateUsername_returnGeneralError() throws NetworkErrorException
    {
        when(
                endpointSync.updateUsername(any(String.class), any(String.class))
        ).thenReturn(
                new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR,
                        USER_ID,
                        USER_NAME
                )
        );
    }
    
    private void setup_onEndpointSyncUpdateUsername_returnServerError() throws NetworkErrorException
    {
        when(
                endpointSync.updateUsername(any(String.class), any(String.class))
        ).thenReturn(
                new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR,
                        USER_ID,
                        USER_NAME
                )
        );
    }
    
    private void setup_onEndpointSyncUpdateUsername_throwNetworkException() throws NetworkErrorException
    {
        doThrow(
                new NetworkErrorException()
        ).when
                (endpointSync).updateUsername(any(String.class), any(String.class));
    }
}