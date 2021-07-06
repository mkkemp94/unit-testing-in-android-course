package com.techyourchance.unittesting.constant;

import com.techyourchance.unittesting.networking.questions.QuestionSchema;
import com.techyourchance.unittesting.questions.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionsHelper
{
    public static Question getQuestion()
    {
        return new Question("id", "title");
    }
    
    public static List<Question> getQuestionsList()
    {
        ArrayList<Question> questions = new ArrayList<>();
        questions.add(new Question("id1", "title1"));
        questions.add(new Question("id2", "title2"));
        return questions;
    }
    
    public static List<QuestionSchema> getQuestionSchemasList()
    {
        ArrayList<QuestionSchema> schemas = new ArrayList<>();
        schemas.add(new QuestionSchema("title1", "id1", "body1"));
        schemas.add(new QuestionSchema( "title2", "id2", "body2"));
        return schemas;
    }
}
