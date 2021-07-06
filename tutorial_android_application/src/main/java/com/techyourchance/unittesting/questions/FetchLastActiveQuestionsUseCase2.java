package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.networking.questions.FetchLastActiveQuestionsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import java.util.ArrayList;
import java.util.List;

public class FetchLastActiveQuestionsUseCase2
{
    public interface Callback
    {
        void gotQuestions(List<Question> questions);

        void failure(String error);
    }
    
    private final FetchLastActiveQuestionsEndpoint networkDataSource;
    
    public FetchLastActiveQuestionsUseCase2(FetchLastActiveQuestionsEndpoint fetchLastActiveQuestionsEndpoint)
    {
        networkDataSource = fetchLastActiveQuestionsEndpoint;
    }
    
    public void fetchLastActiveQuestions(final Callback callback)
    {
        networkDataSource.fetchLastActiveQuestions(new FetchLastActiveQuestionsEndpoint.Listener()
        {
            @Override
            public void onQuestionsFetched(List<QuestionSchema> schemas)
            {
                final List<Question> questions = convertSchemasToQuestions(schemas);
                callback.gotQuestions(questions);
            }
            
            @Override
            public void onQuestionsFetchFailed()
            {
                callback.failure("Failed to get questions");
            }
        });
    }
    
    private List<Question> convertSchemasToQuestions(final List<QuestionSchema> schemas)
    {
        final ArrayList<Question> questions = new ArrayList<>();
        for (QuestionSchema schema : schemas)
        {
            questions.add(new Question(schema.getId(), schema.getTitle()));
        }
        return questions;
    }
}
