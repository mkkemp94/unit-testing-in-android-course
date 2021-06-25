package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;

import org.jetbrains.annotations.Nullable;

interface FetchUserUseCaseSync {

    enum Status {
        SUCCESS,
        FAILURE,
        NETWORK_ERROR
    }

    class UseCaseResult {
        private final Status mStatus;

        @Nullable
        private final User mUser;

        public UseCaseResult(Status status, @Nullable User user) {
            mStatus = status;
            mUser = user;
        }

        public Status getStatus() {
            return mStatus;
        }

        @Nullable
        public User getUser() {
            return mUser;
        }
    }

    UseCaseResult fetchUserSync(String userId) throws NetworkErrorException;

}
