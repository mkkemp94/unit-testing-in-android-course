package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntervalsAdjacencyDetectorTest {

    IntervalsAdjacencyDetector SUT;
    
    @Before
    public void setUp() throws Exception
    {
        SUT = new IntervalsAdjacencyDetector();
    }
    
    // interval1 before interval2 -- not adjacent
    
    @Test
    public void isAdjacent_interval1BeforeInterval2_returnsFalse()
    {
        Interval interval1 = new Interval(-5, -3);
        Interval interval2 = new Interval(2, 4);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }
    
    // interval1 right before interval2 -- adjacent
    
    @Test
    public void isAdjacent_interval1RightBeforeInterval2_returnsTrue()
    {
        Interval interval1 = new Interval(-5, 2);
        Interval interval2 = new Interval(2, 4);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }
    
    // interval1 overlapping interval2 at start -- not adjacent
    
    @Test
    public void isAdjacent_interval1OverlapsInterval2AtStart_returnsFalse()
    {
        Interval interval1 = new Interval(-5, 3);
        Interval interval2 = new Interval(2, 4);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }
    
    // interval1 contained within interval2 -- not adjacent
    
    @Test
    public void isAdjacent_interval1ContainedWithinInterval2_returnsFalse()
    {
        Interval interval1 = new Interval(2, 3);
        Interval interval2 = new Interval(1, 4);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }
    
    // interval1 surrounding interval2 -- not adjacent
    
    @Test
    public void isAdjacent_interval1SurroundsInterval2_returnsFalse()
    {
        Interval interval1 = new Interval(0, 5);
        Interval interval2 = new Interval(1, 4);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }
    
    // interval1 overlapping interval2 at end -- not adjacent
    
    @Test
    public void isAdjacent_interval1OverlapsInterval2AtEnd_returnsFalse()
    {
        Interval interval1 = new Interval(3, 5);
        Interval interval2 = new Interval(2, 4);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }
    
    // interval1 right after interval2 -- adjacent
    
    @Test
    public void isAdjacent_interval1RightAfterInterval2_returnsTrue()
    {
        Interval interval1 = new Interval(4, 9);
        Interval interval2 = new Interval(2, 4);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }
    
    // interval1 after interval2 -- not adjacent
    
    @Test
    public void isAdjacent_interval1AfterInterval2_returnsFalse()
    {
        Interval interval1 = new Interval(6, 22);
        Interval interval2 = new Interval(2, 4);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }
    
    // same interval - not adjacent
    
    @Test
    public void isAdjacent_interval1SameAsInterval2_returnsFalse()
    {
        Interval interval1 = new Interval(6, 22);
        Interval interval2 = new Interval(6, 22);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }
}