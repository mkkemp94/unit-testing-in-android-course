package com.techyourchance.unittesting.screens.questionslist;

import com.techyourchance.unittesting.questions.FetchLastActiveQuestionsUseCase2;
import com.techyourchance.unittesting.questions.Question;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;

import java.util.List;

public class QuestionsListController2
{
    private final FetchLastActiveQuestionsUseCase2 mFetchLastActiveQuestionsUseCase;
    private final ScreensNavigator mScreensNavigator;
    private final ToastsHelper mToastsHelper;
    private final QuestionsListViewMvc mViewMvc;
    
    private List<Question> mQuestions;
    
    public QuestionsListController2(FetchLastActiveQuestionsUseCase2 fetchLastActiveQuestionsUseCase,
                                    ScreensNavigator screensNavigator,
                                    ToastsHelper toastsHelper,
                                    QuestionsListViewMvc mViewMvc)
    {
        mFetchLastActiveQuestionsUseCase = fetchLastActiveQuestionsUseCase;
        mScreensNavigator = screensNavigator;
        mToastsHelper = toastsHelper;
        this.mViewMvc = mViewMvc;
    }
    
    public void onStart()
    {
        if (mQuestions != null)
        {
            mViewMvc.bindQuestions(mQuestions);
        }
        else
        {
            mViewMvc.showProgressIndication();
            mFetchLastActiveQuestionsUseCase.fetchLastActiveQuestions(new FetchLastActiveQuestionsUseCase2.Callback()
            {
                @Override
                public void gotQuestions(final List<Question> questions)
                {
                    mQuestions = questions;
                    mViewMvc.hideProgressIndication();
                    mViewMvc.bindQuestions(questions);
                }
                
                @Override
                public void failure(final String error)
                {
                    mViewMvc.hideProgressIndication();
                    mToastsHelper.showUseCaseError();
                }
            });
        }
    }
    
    public void onQuestionClicked(Question question)
    {
        mScreensNavigator.toQuestionDetails(question.getId());
    }
}
