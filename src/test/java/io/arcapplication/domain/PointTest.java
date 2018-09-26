package io.arcapplication.domain;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;


import static com.sun.org.apache.xalan.internal.lib.ExsltMath.cos;
import static java.lang.Math.sin;

import static org.junit.Assert.*;


public class PointTest {

    @Test
    public void rotate(){

        Point toRotate = Point.getPointOf(0,-1);
        Point around = Point.getPointOf(0,0);

        Point point = Point.rotatePoint(toRotate, around, 90);

        Assert.assertTrue(point.equals(Point.getPointOf(1,0)));


    }


    @Test
    public void bigDecimalTest(){
        BigDecimal bigDecimal = BigDecimal.valueOf(-1.45).setScale(1, RoundingMode.HALF_EVEN);
        System.out.println(bigDecimal);
    }
    @Test
    public void degreesTest(){
        double theta = (double) 2/5;
        double asin = Math.acos(theta);
        double v = Math.toDegrees(asin);
        System.out.println(v);
    }

    @Test
    public void radiusTest(){
        double rad = 3;
        Point a = Point.getPointOf(0,0);
        Point b = Point.getPointOf(5,0);
        BigDecimal distance = MathUtils.distance(a, b);
        System.out.println(distance);
        Vector point = Vector.vectorOf(b, a);
        Vector point1 = Vector.scaleVector(distance, point, BigDecimal.valueOf(rad));
        a = b.addVector(point1);
        System.out.println(a);

        BigDecimal triangleBase = distance.divide(BigDecimal.valueOf(2),15,BigDecimal.ROUND_UP);
        double cos = triangleBase.divide(BigDecimal.valueOf(rad),15,BigDecimal.ROUND_UP).doubleValue();
        double v = Math.toDegrees(Math.acos(cos));
        double degree = 0;

        Point rotate1 = Point.rotatePoint(a,b,v);
        System.out.println(rotate1);
        Point rotate2 = Point.rotatePoint(a,b,-v);
        System.out.println(rotate2);

    }

    private Point midpoint(Point a, Point b, double rad){
        return Point.getPointOf(a.getX().add(b.getX()).divide(BigDecimal.valueOf(3),5,BigDecimal.ROUND_DOWN),a.getY().add(b.getY().divide(BigDecimal.valueOf(2))));
    }

}