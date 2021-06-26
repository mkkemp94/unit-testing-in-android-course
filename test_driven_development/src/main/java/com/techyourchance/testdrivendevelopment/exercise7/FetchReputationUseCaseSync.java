package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

public class FetchReputationUseCaseSync
{
    private final GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock;
    
    public FetchReputationUseCaseSync(GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock)
    {
        this.getReputationHttpEndpointSyncMock = getReputationHttpEndpointSyncMock;
    }
    
    public Result fetchReputation()
    {
        final GetReputationHttpEndpointSync.EndpointResult reputationSync = getReputationHttpEndpointSyncMock.getReputationSync();
        
        switch (reputationSync.getStatus())
        {
            case NETWORK_ERROR:
            case GENERAL_ERROR:
                return new Result(Result.Status.FAILURE, reputationSync.getReputation());
            case SUCCESS:
                return new Result(Result.Status.SUCCESS, reputationSync.getReputation());
            default:
                throw new RuntimeException("invalid status: " + reputationSync.getStatus());
        }
    }
    
    public static class Result
    {
        private final int reputation;
        
        public int getReputation()
        {
            return reputation;
        }
        
        public Status getStatus()
        {
            return status;
        }
        
        public enum Status
        {
            FAILURE, SUCCESS
        }
        
        private final Status status;
        
        Result(Status status, int reputation)
        {
            
            this.status = status;
            this.reputation = reputation;
        }
    }
}
