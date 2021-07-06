package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.constant.QuestionsHelper;
import com.techyourchance.unittesting.networking.questions.FetchLastActiveQuestionsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class FetchLastActiveQuestionsUseCase2Test
{
    // region constants
    private static final List<Question> QUESTIONS = QuestionsHelper.getQuestionsList();
    private static final List<QuestionSchema> QUESTION_SCHEMAS = QuestionsHelper.getQuestionSchemasList();
    // endregion constants
    
    // region helper fields
    FetchLastActiveQuestionsEndpointTd networkDataSource;
    FetchLastActiveQuestionsUseCase2 SUT;
    @Mock FetchLastActiveQuestionsUseCase2.Callback callback;
    // endregion helper fields
    
    @Before
    public void setup() throws Exception
    {
        networkDataSource = new FetchLastActiveQuestionsEndpointTd();
        SUT = new FetchLastActiveQuestionsUseCase2(networkDataSource);
    }
    
    @Test
    public void fetchLastActiveQuestion_success_callbackSuccessWithQuestions() throws Exception
    {
        // Arrange
        success();
        
        // Act
        SUT.fetchLastActiveQuestions(callback);
        
        // Assert
        verify(callback).gotQuestions(QUESTIONS);
    }
    
    @Test
    public void fetchLastActiveQuestion_failure_callbackFailure() throws Exception
    {
        // Arrange
        networkFailure();
    
        // Act
        SUT.fetchLastActiveQuestions(callback);
    
        // Assert
        verify(callback).failure(any(String.class));
    }
    
    // region helper methods
    private void success()
    {
        // no op
    }
    
    private void networkFailure()
    {
        networkDataSource.failure = true;
    }
    // endregion helper methods
    
    // region helper classes
    private static class FetchLastActiveQuestionsEndpointTd extends FetchLastActiveQuestionsEndpoint
    {
        public boolean failure = false;
    
        public FetchLastActiveQuestionsEndpointTd()
        {
            super(null);
        }
    
        @Override
        public void fetchLastActiveQuestions(final Listener listener)
        {
            if (failure)
            {
                listener.onQuestionsFetchFailed();
            }
            else
            {
                listener.onQuestionsFetched(QUESTION_SCHEMAS);
            }
        }
    }
    // endregion helper classes
}