package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FetchUserUseCaseSync2 implements FetchUserUseCaseSync
{
    private final UsersCache usersCache;
    private final FetchUserHttpEndpointSync fetchUserHttpEndpointSync;
    
    public FetchUserUseCaseSync2(FetchUserHttpEndpointSync fetchUserHttpEndpointSync, UsersCache usersCache)
    {
        this.usersCache = usersCache;
        this.fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
    }
    
    public UseCaseResult fetchUserSync(String userId)
    {
        final User cachedUser = usersCache.getUser(userId);
        
        if (cachedUser != null)
        {
            return new UseCaseResult(Status.SUCCESS, new User(cachedUser.getUserId(), cachedUser.getUsername()));
        }
        else
        {
            try
            {
                final EndpointResult endpointResult = fetchUserHttpEndpointSync.fetchUserSync(userId);
    
                switch (endpointResult.getStatus())
                {
                    case AUTH_ERROR:
                    case GENERAL_ERROR:
                        return new UseCaseResult(Status.FAILURE, null);
                    case SUCCESS:
                        final User endpointUser = new User(endpointResult.getUserId(), endpointResult.getUsername());
                        usersCache.cacheUser(endpointUser);
                        return new UseCaseResult(Status.SUCCESS, endpointUser);
                    default:
                        throw new RuntimeException("invalid endpoint status: " + endpointResult.getStatus());
                }
            }
            catch (NetworkErrorException e)
            {
                e.printStackTrace();
                return new UseCaseResult(Status.NETWORK_ERROR, null);
            }
        }
    }
}
