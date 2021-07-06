package com.techyourchance.unittesting.screens.questionslist;

import com.techyourchance.unittesting.constant.QuestionsHelper;
import com.techyourchance.unittesting.questions.FetchLastActiveQuestionsUseCase;
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
public class QuestionsListControllerTest
{
    // region constants
    private static final List<Question> QUESTIONS = QuestionsHelper.getQuestionsList();
    private static final Question QUESTION = QuestionsHelper.getQuestion();
    // endregion constants
    
    // region helper fields
    QuestionsListController SUT;
    FetchLastActiveQuestionsUseCaseTd useCaseTd;
    @Mock ScreensNavigator screensNavigator;
    @Mock ToastsHelper toastsHelper;
    @Mock QuestionsListViewMvc questionsListViewMvc;
    // endregion helper fields
    
    @Before
    public void setup()
    {
        useCaseTd = new FetchLastActiveQuestionsUseCaseTd();
        SUT = new QuestionsListController(useCaseTd, screensNavigator, toastsHelper);
        SUT.bindView(questionsListViewMvc);
    }
    
    @Test
    public void onStart_progressIndicationShown() throws Exception
    {
        // Arrange
        success();
        
        // Act
        SUT.onStart();
        
        // Assert
        verify(questionsListViewMvc).showProgressIndication();
    }
    
    @Test
    public void onStart_successfulResponse_progressIndicationHidden() throws Exception
    {
        // Arrange
        success();
        
        // Act
        SUT.onStart();
        
        // Assert
        verify(questionsListViewMvc).hideProgressIndication();
    }
    
    @Test
    public void onStart_failureResponse_progressIndicationHidden() throws Exception
    {
        // Arrange
        failure();
        
        // Act
        SUT.onStart();
        
        // Assert
        verify(questionsListViewMvc).hideProgressIndication();
    }
    
    @Test
    public void onStart_successfulResponse_questionsBoundToView() throws Exception
    {
        // Arrange
        success();
        
        // Act
        SUT.onStart();
        
        // Assert
        verify(questionsListViewMvc).bindQuestions(QUESTIONS);
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
        verify(questionsListViewMvc, never()).bindQuestions(any(List.class));
    }
    
    @Test
    public void onStart_listenersRegisters() throws Exception
    {
        // Arrange
        
        // Act
        SUT.onStart();
        
        // Assert
        verify(questionsListViewMvc).registerListener(SUT);
        useCaseTd.verifyListenerRegistered(SUT);
    }
    
    @Test
    public void onStop_listenersUnregisters() throws Exception
    {
        // Arrange
        
        // Act
        SUT.onStop();
        
        // Assert
        verify(questionsListViewMvc).unregisterListener(SUT);
        useCaseTd.verifyListenerNotRegistered(SUT);
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
    
    @Test
    public void onStart_secondTimeAfterSuccessFullResponse_questionsBoundToViewFromCache() throws Exception
    {
        // Arrange
        
        // Act
        SUT.onStart();
        SUT.onStart();
        
        // Assert
        
        verify(questionsListViewMvc, times(2)).bindQuestions(QUESTIONS);
        assertThat(useCaseTd.getCallCount(), is (1));
    }
    
    // region helper methods
    private void success()
    {
        // no op
    }
    
    private void failure()
    {
        useCaseTd.failure = true;
    }
    // endregion helper methods
    //
    // region helper classes
    private static class FetchLastActiveQuestionsUseCaseTd extends FetchLastActiveQuestionsUseCase
    {
        public boolean failure = false;
        private int callCount = 0;
    
        public FetchLastActiveQuestionsUseCaseTd()
        {
            super(null);
        }
        
        @Override
        public void fetchLastActiveQuestionsAndNotify()
        {
            callCount++;
            
            for (Listener listener : this.getListeners())
            {
                if (failure)
                {
                    listener.onLastActiveQuestionsFetchFailed();
                }
                else
                {
                    listener.onLastActiveQuestionsFetched(QUESTIONS);
                }
            }
        }
    
        public void verifyListenerRegistered(final QuestionsListController candidate)
        {
            for (Listener listener : getListeners())
            {
                if (listener == candidate)
                {
                    return;
                }
            }
            throw new RuntimeException("listener not registered");
        }
    
        public void verifyListenerNotRegistered(final QuestionsListController candidate)
        {
            for (Listener listener : getListeners())
            {
                if (listener == candidate)
                {
                    throw new RuntimeException("listener is still registered");
                }
            }
        }
    
        public int getCallCount()
        {
            return callCount;
        }
    }
    // endregion helper classes
}