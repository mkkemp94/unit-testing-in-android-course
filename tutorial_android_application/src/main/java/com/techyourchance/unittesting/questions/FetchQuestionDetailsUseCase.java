package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.common.BaseObservable;
import com.techyourchance.unittesting.common.time.TimeProvider;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

public class FetchQuestionDetailsUseCase extends BaseObservable<FetchQuestionDetailsUseCase.Listener> {

    public interface Listener {
        void onQuestionDetailsFetched(QuestionDetails questionDetails);
        void onQuestionDetailsFetchFailed();
    }

    private static final int CACHE_TIMEOUT_MS = 60000;

    private final FetchQuestionDetailsEndpoint mFetchQuestionDetailsEndpoint;
    private final TimeProvider mTimeProvider;

    private QuestionSchema questionSchemaCache;
    private long lastCachedTimeStamp;

    public FetchQuestionDetailsUseCase(FetchQuestionDetailsEndpoint fetchQuestionDetailsEndpoint,
                                       TimeProvider timeProvider) {
        mFetchQuestionDetailsEndpoint = fetchQuestionDetailsEndpoint;
        mTimeProvider = timeProvider;
    }

    public void fetchQuestionDetailsAndNotify(String questionId)
    {
        if (cachedDataIsValid(questionId))
        {
            notifySuccess(questionSchemaCache);
        }
        else
        {
            mFetchQuestionDetailsEndpoint.fetchQuestionDetails(questionId, new FetchQuestionDetailsEndpoint.Listener()
            {
                @Override
                public void onQuestionDetailsFetched(QuestionSchema question)
                {
                    notifySuccess(question);
                }

                @Override
                public void onQuestionDetailsFetchFailed()
                {
                    notifyFailure();
                }
            });
        }
    }

    private boolean cachedDataIsValid(String questionId)
    {
        return questionSchemaCache != null &&
                questionSchemaCache.getId().equals(questionId) &&
                mTimeProvider.getCurrentTimestamp() < lastCachedTimeStamp + CACHE_TIMEOUT_MS;
    }

    private void notifyFailure() {
        for (Listener listener : getListeners()) {
            listener.onQuestionDetailsFetchFailed();
        }
    }

    private void notifySuccess(QuestionSchema questionSchema) {
        questionSchemaCache = questionSchema;
        lastCachedTimeStamp = mTimeProvider.getCurrentTimestamp();

        for (Listener listener : getListeners()) {
            listener.onQuestionDetailsFetched(
                    new QuestionDetails(
                            questionSchema.getId(),
                            questionSchema.getTitle(),
                            questionSchema.getBody()
                    ));
        }
    }
}
