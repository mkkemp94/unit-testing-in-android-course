package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncTest
{
    // region constants
    public static final int SUCCESS_REPUTATION = 100;
    public static final int FAILURE_REPUTATION = 0;
    // endregion constants
    
    // region helper fields
    @Mock GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock;
    // endregion helper fields
    
    FetchReputationUseCaseSync SUT;
    
    @Before
    public void setup() throws Exception
    {
        SUT = new FetchReputationUseCaseSync(getReputationHttpEndpointSyncMock);
        success();
    }
    
    @Test
    public void fetchReputation_success_successResultReturned() throws Exception
    {
        // Arrange
        
        // Act
        FetchReputationUseCaseSync.Result result = SUT.fetchReputation();
        
        // Assert
        assertThat(result.getStatus(), is(FetchReputationUseCaseSync.Result.Status.SUCCESS));
    }
    
    @Test
    public void fetchReputation_success_fetchedReputationReturned() throws Exception
    {
        // Arrange
        
        // Act
        FetchReputationUseCaseSync.Result result = SUT.fetchReputation();
        
        // Assert
        assertThat(result.getReputation(), is(SUCCESS_REPUTATION));
    }
    
    @Test
    public void fetchReputation_generalError_failureStatusReturned() throws Exception
    {
        // Arrange
        generalError();
        
        // Act
        FetchReputationUseCaseSync.Result result = SUT.fetchReputation();
        
        // Assert
        assertThat(result.getStatus(), is(FetchReputationUseCaseSync.Result.Status.FAILURE));
    }
    
    @Test
    public void fetchReputation_generalError_reputation0Returned() throws Exception
    {
        // Arrange
        generalError();
        
        // Act
        FetchReputationUseCaseSync.Result result = SUT.fetchReputation();
        
        // Assert
        assertThat(result.getReputation(), is(FAILURE_REPUTATION));
    }
    
    @Test
    public void fetchReputation_networkError_failureStatusReturned() throws Exception
    {
        // Arrange
        networkError();
        
        // Act
        FetchReputationUseCaseSync.Result result = SUT.fetchReputation();
        
        // Assert
        assertThat(result.getStatus(), is(FetchReputationUseCaseSync.Result.Status.FAILURE));
    }
    
    @Test
    public void fetchReputation_networkError_reputation0Returned() throws Exception
    {
        // Arrange
        networkError();
        
        // Act
        FetchReputationUseCaseSync.Result result = SUT.fetchReputation();
        
        // Assert
        assertThat(result.getReputation(), is(FAILURE_REPUTATION));
    }
    
    // region helper methods
    private void success()
    {
        when(
                getReputationHttpEndpointSyncMock.getReputationSync()
        ).thenReturn(
                new GetReputationHttpEndpointSync.EndpointResult(
                        GetReputationHttpEndpointSync.EndpointStatus.SUCCESS,
                        SUCCESS_REPUTATION
                )
        );
    }
    
    private void generalError()
    {
        when(
                getReputationHttpEndpointSyncMock.getReputationSync()
        ).thenReturn(
                new GetReputationHttpEndpointSync.EndpointResult(
                        GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR,
                        FAILURE_REPUTATION
                )
        );
    }
    
    private void networkError()
    {
        when(
                getReputationHttpEndpointSyncMock.getReputationSync()
        ).thenReturn(
                new GetReputationHttpEndpointSync.EndpointResult(
                        GetReputationHttpEndpointSync.EndpointStatus.NETWORK_ERROR,
                        FAILURE_REPUTATION
                )
        );
    }
    // endregion helper methods
    
    // region helper classes
    
    // endregion helper classes
}