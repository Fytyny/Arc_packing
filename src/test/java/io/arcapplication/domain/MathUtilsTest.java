package io.arcapplication.domain;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class MathUtilsTest {

    @Test
    public void distanceTest(){
        Point a = Point.getPointOf(2,5);
        Point b = Point.getPointOf(5,9);

        BigDecimal r = BigDecimal.valueOf(4);

        BigDecimal distance = MathUtils.distance(a, b);

        System.out.println(distance.toString());

        assertTrue(distance.compareTo(BigDecimal.valueOf(5)) == 0);
        assertTrue(distance.compareTo(r) > 0);

    }

    @Test
    public void distanceHarderTest(){

        Point a = Point.getPointOf(2,-4);
        Point b = Point.getPointOf(-3,5);

        BigDecimal result = BigDecimal.valueOf(Math.sqrt(106));

        BigDecimal distance = MathUtils.distance(a, b);

        Assert.assertEquals(result.doubleValue(), distance.doubleValue(),1E-5);


    }

    @Test
    public void centerOfSectionTest(){
        Point a = Point.getPointOf(-2,3);
        Point b = Point.getPointOf(4,-9);

        Point expected = Point.getPointOf(1,-3);

        Point result = MathUtils.centerOfSection(a,b);

        Assert.assertEquals(expected.getX().doubleValue(),result.getX().doubleValue(), 1E-5);
        Assert.assertEquals(expected.getY().doubleValue(),result.getY().doubleValue(), 1E-5);
    }

    @Test
    public void intersectingTest(){


    }

}