package com.techyourchance.unittesting.testdata;

import com.techyourchance.unittesting.networking.questions.QuestionSchema;
import com.techyourchance.unittesting.questions.QuestionDetails;

public class QuestionDetailsTestData
{
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String BODY = "body";

    public static QuestionDetails getQuestionDetails()
    {
        return new QuestionDetails(ID, TITLE, BODY);
    }

    public static QuestionSchema getQuestionSchema()
    {
        return new QuestionSchema(TITLE, ID, BODY);
    }

    public static String getID()
    {
        return ID;
    }
}
