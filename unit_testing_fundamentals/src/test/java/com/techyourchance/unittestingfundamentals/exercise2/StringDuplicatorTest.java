package com.techyourchance.unittestingfundamentals.exercise2;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringDuplicatorTest {

    StringDuplicator SUT;
    
    @Before
    public void setUp() throws Exception
    {
        SUT = new StringDuplicator();
    }
    
    @Test
    public void duplicate_emptyString_returnsEmptyString()
    {
        String result = SUT.duplicate("");
        assertThat(result, is(""));
    }
    
    @Test
    public void duplicate_lowercaseSingleLetter_returnsDuplicateLetter()
    {
        String result = SUT.duplicate("a");
        assertThat(result, is("aa"));
    }
    
    @Test
    public void duplicate_uppercaseSingleLetter_returnsDuplicateLetter()
    {
        String result = SUT.duplicate("B");
        assertThat(result, is("BB"));
    }
    
    @Test
    public void duplicate_uppercaseWord_returnsDuplicateWord()
    {
        String result = SUT.duplicate("DOG");
        assertThat(result, is("DOGDOG"));
    }
    
    @Test
    public void duplicate_lowercaseWord_returnsDuplicateWord()
    {
        String result = SUT.duplicate("antman");
        assertThat(result, is("antmanantman"));
    }
    
    @Test
    public void duplicate_mixedCaseWord_returnsDuplicateWord()
    {
        String result = SUT.duplicate("cAtSrDuMb");
        assertThat(result, is("cAtSrDuMbcAtSrDuMb"));
    }
}