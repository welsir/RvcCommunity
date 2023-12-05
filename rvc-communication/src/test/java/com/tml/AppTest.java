package com.tml;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        int[] cardPoints = {96,90,41,82,39,74,64,50,30};
        int k = 8;
        System.out.println(maxScore(cardPoints,k));
    }

    public static int maxScore(int[] cardPoints, int k) {
        int N = cardPoints.length - k;
        int sum = 0;
        int minSum = Integer.MAX_VALUE;
        int NSum = 0;
        for (int i=0;i<cardPoints.length;i++){
            sum += cardPoints[i];
            if (i < N){
                NSum += cardPoints[i];
            }else {
                minSum = Math.min(minSum,NSum);
                NSum += cardPoints[i];
                NSum -= cardPoints[i -N];
            }
        }
        minSum = Math.min(minSum,NSum);
        return sum-minSum;
    }
}
