package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.common.time.TimeProvider;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;
import com.techyourchance.unittesting.testdata.QuestionDetailsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;


@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest
{
    // region constants
    private static final QuestionSchema QUESTION_SCHEMA = QuestionDetailsTestData.getQuestionSchema();
    private static final QuestionDetails QUESTION_DETAILS = QuestionDetailsTestData.getQuestionDetails();
    private static final String QUESTION_ID = QuestionDetailsTestData.getID();
    private static final String QUESTION_ID2 = QuestionDetailsTestData.getID() + "2";
    // endregion constants

    // region helper fields
    FetchQuestionDetailsUseCase SUT;
    EndpointTd endpointTd;
    @Mock TimeProvider timeProviderMock;
    @Mock FetchQuestionDetailsUseCase.Listener listener1;
    @Mock FetchQuestionDetailsUseCase.Listener listener2;
    // endregion helper fields

    @Before
    public void setup() throws Exception
    {
        endpointTd = new EndpointTd();
        SUT = new FetchQuestionDetailsUseCase(endpointTd, timeProviderMock);
        success();
    }

    @Test
    public void fetchQuestionDetailsAndNotify_success_notifiesListenersWithCorrectData() throws Exception
    {
        // Arrange
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);

        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);

        // Assert
        verify(listener1).onQuestionDetailsFetched(any(QuestionDetails.class));
        verify(listener2).onQuestionDetailsFetched(any(QuestionDetails.class));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_failure_notifiesListenersOfFailure() throws Exception
    {
        // Arrange
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        failure();

        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);

        // Assert
        verify(listener1).onQuestionDetailsFetchFailed();
        verify(listener2).onQuestionDetailsFetchFailed();
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeBeforeTimeout_getsFromCacheAndDoesNotCallEndpoint() throws Exception
    {
        // Arrange
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(0L);

        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(59999L);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);

        // Assert
        verify(listener1, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS);
        verify(listener2, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS);
        assertThat(endpointTd.callCount, is(1));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeAfterTimeout_getsFromEndpoint() throws Exception
    {
        // Arrange
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(0L);

        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(60001L);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);

        // Assert
        verify(listener1, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS);
        verify(listener2, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS);
        assertThat(endpointTd.callCount, is(2));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeWithDifferentQuestionIDBeforeTimeout_getsFromEndpoint() throws Exception
    {
        // Arrange
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(0L);

        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(59999L);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID2);

        // Assert
        verify(listener1, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS);
        verify(listener2, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS);
        assertThat(endpointTd.callCount, is(2));
    }

    // region helper methods
    private void failure()
    {
        endpointTd.failure = true;
    }

    private void success()
    {
        // no op
    }
    // endregion helper methods

    // region helper classes
    private static class EndpointTd extends FetchQuestionDetailsEndpoint
    {
        public boolean failure = false;
        public int callCount = 0;

        public EndpointTd()
        {
            super(null);
        }

        @Override
        public void fetchQuestionDetails(final String questionId, final Listener listener)
        {
            callCount++;

            if (failure)
            {
                listener.onQuestionDetailsFetchFailed();
            }
            else {
                listener.onQuestionDetailsFetched(QUESTION_SCHEMA);
            }
        }
    }
    // endregion helper classes
}