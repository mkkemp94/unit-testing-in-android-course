package com.techyourchance.unittesting.screens.questiondetails;

import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase;
import com.techyourchance.unittesting.questions.QuestionDetails;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;
import com.techyourchance.unittesting.testdata.QuestionsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class QuestionDetailsControllerTest
{
    // region constants
    private static final QuestionDetails QUESTION_DETAILS = QuestionsTestData.getQuestionDetails();
    // endregion constants

    // region helper fields
    QuestionDetailsController SUT;
    FetchQuestionDetailsUseCaseTd fetchQuestionDetailsUseCaseTd;
    @Mock ScreensNavigator screensNavigatorMock;
    @Mock ToastsHelper toastsHelperMock;
    @Mock QuestionDetailsViewMvc viewMock;
    // endregion helper fields

    @Before
    public void setup()
    {
        fetchQuestionDetailsUseCaseTd = new FetchQuestionDetailsUseCaseTd();
        SUT = new QuestionDetailsController(fetchQuestionDetailsUseCaseTd, screensNavigatorMock, toastsHelperMock);
        SUT.bindView(viewMock);

        success();
    }

    @Test
    public void start_listenersRegistered() throws Exception
    {
        // Arrange

        // Act
        SUT.start();

        // Assert
        fetchQuestionDetailsUseCaseTd.verifyListenerRegistered(SUT);
    }

    @Test
    public void stop_listenersUnregistered() throws Exception
    {
        // Arrange

        // Act
        SUT.start();
        SUT.stop();

        // Assert
        fetchQuestionDetailsUseCaseTd.verifyListenerUnregistered(SUT);
    }

    @Test
    public void start_showProgressIndication() throws Exception
    {
        // Arrange

        // Act
        SUT.start();

        // Assert
        verify(viewMock).showProgressIndication();
    }

    @Test
    public void start_onQuestionDetailsFetchedSuccess_hideProgressIndication() throws Exception
    {
        // Arrange

        // Act
        SUT.start();

        // Assert
        verify(viewMock).hideProgressIndication();
    }

    @Test
    public void start_onQuestionDetailsFetchedFailure_hideProgressIndication() throws Exception
    {
        // Arrange
        failure();

        // Act
        SUT.onQuestionDetailsFetchFailed();

        // Assert
        verify(viewMock).hideProgressIndication();
    }

    @Test
    public void start_onQuestionDetailsFetchedSuccess_bindsQuestionToView() throws Exception
    {
        // Arrange
        success();

        // Act
        SUT.start();

        // Assert
        verify(viewMock).bindQuestion(QUESTION_DETAILS);
    }

    @Test
    public void start_onQuestionDetailsFetchedFailure_doesNotBindQuestionToView() throws Exception
    {
        // Arrange
        failure();

        // Act
        SUT.start();

        // Assert
        verify(viewMock, never()).bindQuestion(any(QuestionDetails.class));
    }

    @Test
    public void start_onQuestionDetailsFetchedSuccess_doesNotShowErrorToast() throws Exception
    {
        // Arrange

        // Act
        SUT.start();

        // Assert
        verify(toastsHelperMock, never()).showUseCaseError();
    }

    @Test
    public void start_onQuestionDetailsFetchedFailure_showsErrorToast() throws Exception
    {
        // Arrange
        failure();

        // Act
        SUT.start();

        // Assert
        verify(toastsHelperMock).showUseCaseError();
    }

    @Test
    public void onNavigateUpClicked_screenNavigatorNavigatesToPreviousScreen() throws Exception
    {
        // Arrange

        // Act
        SUT.onNavigateUpClicked();

        // Assert
        verify(screensNavigatorMock).navigateUp();
    }

//    @Test
//    public void onStart_clickedTwice_getsQuestionFromCacheOnSecondClick() throws Exception
//    {
//        // Arrange
//
//        // Act
//        SUT.start();
//        SUT.start();
//
//        // Assert
//        verify(viewMock, times(2)).bindQuestion(QUESTION_DETAILS);
//        assertThat(fetchQuestionDetailsUseCaseTd.callCount, is(1));
//    }

    // region helper methods
    private void success()
    {
        // no op
    }

    private void failure()
    {
        fetchQuestionDetailsUseCaseTd.failure = true;
    }
    // endregion helper methods

    // region helper classes
    private static class FetchQuestionDetailsUseCaseTd extends FetchQuestionDetailsUseCase
    {
        public boolean failure = false;
        public int callCount = 0;

        public FetchQuestionDetailsUseCaseTd()
        {
            super(null);
        }

        @Override
        public void fetchQuestionDetailsAndNotify(final String questionId)
        {
            callCount++;

            if (failure)
            {
                for (FetchQuestionDetailsUseCase.Listener listener : getListeners())
                {
                    listener.onQuestionDetailsFetchFailed();
                }
            }
            else
            {
                for (FetchQuestionDetailsUseCase.Listener listener : getListeners())
                {
                    listener.onQuestionDetailsFetched(QUESTION_DETAILS);
                }
            }

        }

        public void verifyListenerRegistered(final FetchQuestionDetailsUseCase.Listener candidate)
        {
            for (FetchQuestionDetailsUseCase.Listener listener : getListeners())
            {
                if (candidate == listener)
                {
                    return;
                }
            }
            throw new RuntimeException("listener not registered");
        }

        public void verifyListenerUnregistered(final QuestionDetailsController candidate)
        {
            for (FetchQuestionDetailsUseCase.Listener listener : getListeners())
            {
                if (candidate == listener)
                {
                    throw new RuntimeException("listener is still registered");
                }
            }
        }
    }
    // endregion helper classes
}