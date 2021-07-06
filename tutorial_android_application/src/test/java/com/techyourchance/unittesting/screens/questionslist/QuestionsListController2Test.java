package com.techyourchance.unittesting.screens.questionslist;

import com.techyourchance.unittesting.constant.QuestionsHelper;
import com.techyourchance.unittesting.questions.FetchLastActiveQuestionsUseCase2;
import com.techyourchance.unittesting.questions.Question;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;


@RunWith(MockitoJUnitRunner.class)
public class QuestionsListController2Test
{
    // region constants
    private static final List<Question> QUESTIONS = QuestionsHelper.getQuestionsList();
    private static final Question QUESTION = QuestionsHelper.getQuestion();
    // endregion constants
    
    // region helper fields
    FetchLastActiveQuestionsUseCase2Td fetchLastActiveQuestionsUseCaseTd;
    QuestionsListController2 SUT;
    @Mock ScreensNavigator screensNavigator;
    @Mock ToastsHelper toastsHelper;
    @Mock QuestionsListViewMvc viewMvc;
    // endregion helper fields
    
    @Before
    public void setup()
    {
        fetchLastActiveQuestionsUseCaseTd = new FetchLastActiveQuestionsUseCase2Td();
        SUT = new QuestionsListController2(
                fetchLastActiveQuestionsUseCaseTd,
                screensNavigator,
                toastsHelper,
                viewMvc
        );
        
    }
    
    @Test
    public void onStart_progressIndicationShown() throws Exception
    {
        // Arrange
        success();
        
        // Act
        SUT.onStart();
        
        // Assert
        verify(viewMvc).showProgressIndication();
    }
    
    @Test
    public void onStart_successfulResponse_progressIndicationHidden() throws Exception
    {
        // Arrange
        success();
        
        // Act
        SUT.onStart();
        
        // Assert
        verify(viewMvc).hideProgressIndication();
    }
    
    @Test
    public void onStart_failureResponse_progressIndicationHidden() throws Exception
    {
        // Arrange
        failure();
        
        // Act
        SUT.onStart();
        
        // Assert
        verify(viewMvc).hideProgressIndication();
    }
    
    @Test
    public void onStart_onSuccessFulQuestionFetch_bindQuestionsToView() throws Exception
    {
        // Arrange
        success();
        
        // Act
        SUT.onStart();
        
        // Assert
        verify(viewMvc).bindQuestions(QUESTIONS);
    }
    
    @Test
    public void onStart_secondTimeAfterSuccessfulResponse_questionBoundToViewFromCache() throws Exception
    {
        // Arrange
        success();
        
        // Act
        SUT.onStart();
        SUT.onStart();
        
        // Assert
        verify(viewMvc, times(2)).bindQuestions(QUESTIONS);
        assertThat(fetchLastActiveQuestionsUseCaseTd.getCallCount(), is(1));
    }
    
    @Test
    public void onStart_failure_errorToastShown() throws Exception
    {
        // Arrange
        failure();
        
        // Act
        SUT.onStart();
        
        // Assert
        verify(toastsHelper).showUseCaseError();
    }
    
    @Test
    public void onStart_failure_questionsNotBoundToView() throws Exception
    {
        // Arrange
        failure();
        
        // Act
        SUT.onStart();
        
        // Assert
        verify(viewMvc, never()).bindQuestions(any(List.class));
    }
    
    @Test
    public void onQuestionClicked_navigatedToQuestionDetailsScreen() throws Exception
    {
        // Arrange
        
        // Act
        SUT.onQuestionClicked(QUESTION);
        
        // Assert
        verify(screensNavigator).toQuestionDetails(QUESTION.getId());
    }
    
    // region helper methods
    private void success()
    {
        // no op
    }
    
    private void failure()
    {
        fetchLastActiveQuestionsUseCaseTd.failure = true;
    }
    // endregion helper methods
    
    // region helper classes
    public static class FetchLastActiveQuestionsUseCase2Td extends FetchLastActiveQuestionsUseCase2
    {
        public boolean failure = false;
        private int callCount = 0;
    
        public FetchLastActiveQuestionsUseCase2Td()
        {
            super(null);
        }
    
        @Override
        public void fetchLastActiveQuestions(final Callback callback)
        {
            callCount++;
            
            if (failure)
            {
                callback.failure("some error");
            }
            else
            {
                callback.gotQuestions(QUESTIONS);
            }
        }
    
        public int getCallCount()
        {
            return callCount;
        }
    }
    // endregion helper classes
}