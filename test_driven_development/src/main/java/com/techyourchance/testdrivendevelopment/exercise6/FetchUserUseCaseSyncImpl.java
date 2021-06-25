package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status.SUCCESS;
import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status.FAILURE;

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync
{
    private final FetchUserHttpEndpointSync fetchUserHttpEndpointSync;
    private final UsersCache usersCache;
    
    public FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync fetchUserHttpEndpointSync,
                                    UsersCache usersCache)
    {
    
        this.fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        this.usersCache = usersCache;
    }
    
    @Override
    public UseCaseResult fetchUserSync(String userId)
    {
        if (usersCache.getUser(userId) != null)
        {
            return new UseCaseResult(
                    SUCCESS,
                    usersCache.getUser(userId)
            );
        }
        
        try
        {
            final FetchUserHttpEndpointSync.EndpointResult endpointResult = fetchUserHttpEndpointSync.fetchUserSync(userId);
            switch (endpointResult.getStatus())
            {
                case SUCCESS:
                {
                    final User user = new User(
                            endpointResult.getUserId(),
                            endpointResult.getUsername()
                    );
                    
                    usersCache.cacheUser(user);
                    
                    return new UseCaseResult(
                            SUCCESS,
                            user
                    );
                }
                case AUTH_ERROR:
                case GENERAL_ERROR:
                    return new UseCaseResult(
                            FAILURE,
                            null
                    );
                default:
                    throw new RuntimeException("invalid");
            }
        }
        catch (NetworkErrorException e)
        {
            e.printStackTrace();
            return new UseCaseResult(Status.NETWORK_ERROR, null);
        }
    }
}
