package io.arcapplication.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Line2D;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ArcTest {

    ArcSettings settings;
    Arc arc;
    BigDecimal firstRadius;
    BigDecimal secondRadius;

    @Before
    public void before(){
        settings = new ArcSettings(BigDecimal.valueOf(3),BigDecimal.valueOf(25),BigDecimal.ONE);
        arc = new Arc(BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.valueOf(15),settings);
        BigDecimal divide = settings.getD().divide(BigDecimal.valueOf(2), 5, BigDecimal.ROUND_HALF_EVEN);
        firstRadius = settings.getRadius().add(divide);
        secondRadius = settings.getRadius().subtract(divide);
    }

    @Test
    public void test(){

    }

    @Test
    public void findDegree() {
        Assert.assertEquals(90d,arc.findDegree(BigDecimal.ZERO,BigDecimal.ONE).doubleValue(),0.01);
    }
    @Test
    public void findDegree2() {
        Assert.assertEquals(45d,arc.findDegree(BigDecimal.ONE,BigDecimal.ONE).doubleValue(),0.01);
    }
    @Test
    public void findDegree3() {
        Assert.assertEquals(135d,arc.findDegree(BigDecimal.ONE.negate(),BigDecimal.ONE).doubleValue(),0.01);
    }
    @Test
    public void  lineInteresect(){
        double x1 = 0;
        double y1 = 0;
        double x2 = 2;
        double y2 = 2;

        double x3 = 0;
        double y3 = 3;
        double x4 = 5;
        double y4 = 3;

        boolean b = Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4);
        Assert.assertFalse(b);
    }

    // circle middle point is 0,-3
    @Test
    public void quickLineCircleCheckTouchCircle() {
        Point lineStart = Point.getPointOf(-4,0);
        Point lineEnd = Point.getPointOf(4,0);

        boolean b = Arc.lineCircleCheck(lineStart, lineEnd, arc, settings.getRadius());
        Assert.assertTrue(b);
    }

    @Test
    public void quickLineCircleCheckTroughCircle() {
        Point lineStart = Point.getPointOf(-4,0.45);
        Point lineEnd = Point.getPointOf(4,0.45);
        BigDecimal alfa = arc.getArcSettings().getAlfa();
        System.out.println(alfa);
        Point circleMiddle = arc.circleMiddlePoint();
        System.out.println(circleMiddle);
        boolean b = Arc.lineCircleCheck(lineStart, lineEnd, arc,firstRadius);
        Assert.assertTrue(b);
    }
    @Test
    public void quickLineCircleCheckThroughCircleButNotSection() {
        Point lineStart = Point.getPointOf(-6,0);
        Point lineEnd = Point.getPointOf(-4,0);

        boolean b = Arc.lineCircleCheck(lineStart, lineEnd, arc, settings.getRadius());
        Assert.assertFalse(b);
    }
    @Test
    public void quickLineCircleCheckTroughCircleButNotInArc() {
        Point lineStart = Point.getPointOf(-4,-4);
        Point lineEnd = Point.getPointOf(4,-4);

        boolean b = Arc.lineCircleCheck(lineStart, lineEnd, arc, settings.getRadius());
        Assert.assertFalse(b);
    }

    @Test
    public void cramerLineIntersectionNoIntersection(){
        Point e = Point.getPointOf(0,0);
        Point l = Point.getPointOf(5,5);
        Point s = Point.getPointOf(11,12);
        Point k = Point.getPointOf( 6, 6);
        Assert.assertFalse(Arc.cramerLineIntersect(e,l,s,k));
    }
    @Test
    public void cramerLineIntersectionNoIntersectionLineNotIntersectingAtAll(){
        Point e = Point.getPointOf(0,0);
        Point l = Point.getPointOf(5,5);
        Point s = Point.getPointOf(0,1);
        Point k = Point.getPointOf( 5, 6);
        Assert.assertFalse(Arc.cramerLineIntersect(e,l,s,k));
    }
    @Test
    public void cramerLineIntersectionNoIntersectionButOnSameLine(){
        Point e = Point.getPointOf(0,0);
        Point l = Point.getPointOf(5,5);
        Point s = Point.getPointOf(11,11);
        Point k = Point.getPointOf( 6, 6);
        Assert.assertFalse(Arc.cramerLineIntersect(e,l,s,k));
    }
    @Test
    public void cramerLineIntersectionAreIntersecing(){
        Point e = Point.getPointOf(-1,-1);
        Point l = Point.getPointOf(3,3);
        Point s = Point.getPointOf(-3,3);
        Point k = Point.getPointOf( 1, -1);
        boolean b = Line2D.linesIntersect(e.getX().doubleValue(), e.getY().doubleValue(), l.getX().doubleValue(), l.getY().doubleValue(), s.getX().doubleValue(), s.getY().doubleValue(), k.getX().doubleValue(), k.getY().doubleValue());
        Assert.assertTrue(b);
        Assert.assertTrue(Arc.cramerLineIntersect(e,l,s,k));
    }

}