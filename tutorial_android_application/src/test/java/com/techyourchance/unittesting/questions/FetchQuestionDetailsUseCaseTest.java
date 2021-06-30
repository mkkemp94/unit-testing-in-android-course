package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest
{
    // region constants
    public static final String QUESTION_ID = "question id";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    // endregion constants
    
    // region helper fields
    @Mock FetchQuestionDetailsUseCase.Listener listener1;
    @Mock FetchQuestionDetailsUseCase.Listener listener2;
    EndpointTd endpoint;
    FetchQuestionDetailsUseCase SUT;
    // endregion helper fields
    
    @Before
    public void setup() throws Exception
    {
        endpoint = new EndpointTd();
        SUT = new FetchQuestionDetailsUseCase(endpoint);
    }
    
    @Test
    public void fetchQuestionDetails_givenQuestionId_passesCorrectQuestionIdToEndpoint() throws Exception
    {
        // Arrange
        success();
        
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        
        // Assert
        assertThat(endpoint.questionId, is(QUESTION_ID));
    }
    
    @Test
    public void fetchQuestionDetails_success_registeredListenersGetQuestionDetails() throws Exception
    {
        // Arrange
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        
        // Assert
        verify(listener1).onQuestionDetailsFetched(getQuestionDetails());
        verify(listener2).onQuestionDetailsFetched(getQuestionDetails());
    }
    
    @Test
    public void fetchQuestionDetails_success_unregisteredListenersDoNotGetQuestionDetails() throws Exception
    {
        // Arrange
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.unregisterListener(listener2);
        
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        
        // Assert
        verify(listener1).onQuestionDetailsFetched(getQuestionDetails());
        verifyNoMoreInteractions(listener2);
    }
    
    @Test
    public void fetchQuestionDetails_failure_listenersGetFailureCallback() throws Exception
    {
        // Arrange
        failure();
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        
        // Assert
        verify(listener1).onQuestionDetailsFetchFailed();
        verify(listener2).onQuestionDetailsFetchFailed();
    }
    
    // region helper methods
    private QuestionDetails getQuestionDetails()
    {
        return new QuestionDetails(ID, TITLE, BODY);
    }
    
    private QuestionSchema getQuestionSchema()
    {
        return new QuestionSchema(TITLE, ID, BODY);
    }
    
    private void success()
    {
        // no op
    }
    
    private void failure()
    {
        endpoint.failure = true;
    }
    // endregion helper methods
    
    // region helper classes
    private class EndpointTd extends FetchQuestionDetailsEndpoint
    {
        public String questionId;
        public boolean failure = false;
        
        public EndpointTd()
        {
            super(null);
        }
        
        @Override
        public void fetchQuestionDetails(final String questionId, final Listener listener)
        {
            this.questionId = questionId;
            
            if (failure)
            {
                listener.onQuestionDetailsFetchFailed();
            }
            else
            {
                listener.onQuestionDetailsFetched(getQuestionSchema());
            }
        }
    }
    // endregion helper classes
}