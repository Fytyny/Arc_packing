package io.arcapplication.domain;

import io.arcapplication.exception.ArcSettingsException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class ArcIntersectionTest {


    @Test
    public void testEdgeEdge1(){
        ArcSettings settings = new ArcSettings(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.valueOf(6));

        Arc arc1 = new Arc(1, 0, 90, settings);
        Arc arc2 = new Arc(0,0,0,settings);
        Point point = Point.rotatePoint(Point.getPointOf(1, -10), Point.getPointOf(1, 0), 90);

        Assert.assertTrue(Arc.areIntersecting(arc1,arc2));

        Point arc1MiddlePoint = Point.getPointOf(11, 0);
        Assert.assertTrue(arc1.circleMiddlePoint().equals(arc1MiddlePoint));

        Point arc2MiddlePoint = Point.getPointOf(0,-10);
        Assert.assertTrue(arc1.circleMiddlePoint().equals(arc1MiddlePoint));
    }

    @Test
    public void testEdgeEdge2(){
        ArcSettings settings = new ArcSettings(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.valueOf(6));

        Arc arc1 = new Arc(BigDecimal.ONE, BigDecimal.valueOf(2.5d), BigDecimal.valueOf(90), settings);
        Arc arc2 = new Arc(0,0,0,settings);
        Point point = Point.rotatePoint(Point.getPointOf(1, -10), Point.getPointOf(1, 0), 90);

        Assert.assertTrue(Arc.areIntersecting(arc1,arc2));

        Point arc1MiddlePoint = Point.getPointOf(11, 2.5);
        Assert.assertTrue(arc1.circleMiddlePoint().equals(arc1MiddlePoint));

        Point arc2MiddlePoint = Point.getPointOf(0,-10);
        Assert.assertTrue(arc1.circleMiddlePoint().equals(arc1MiddlePoint));
    }

    @Test
    public void testEdgeEdge3(){
        ArcSettings settings = new ArcSettings(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.valueOf(6));

        Arc arc1 = new Arc(BigDecimal.ONE, BigDecimal.valueOf(2.5d), BigDecimal.valueOf(90), settings);
        Arc arc2 = new Arc(0,0,0,settings);
        Point point = Point.rotatePoint(Point.getPointOf(1, -10), Point.getPointOf(1, 0), 90);

        Assert.assertTrue(Arc.areIntersecting(arc1,arc2));

        Point arc1MiddlePoint = Point.getPointOf(11, 2.5);
        Assert.assertTrue(arc1.circleMiddlePoint().equals(arc1MiddlePoint));

        Point arc2MiddlePoint = Point.getPointOf(0,-10);
        Assert.assertTrue(arc1.circleMiddlePoint().equals(arc1MiddlePoint));
    }

    @Test
    public void testEdgeEdge4(){
        ArcSettings settings = new ArcSettings(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.valueOf(6));

        Arc arc1 = new Arc(BigDecimal.valueOf(3), BigDecimal.valueOf(2.5d), BigDecimal.valueOf(90), settings);
        Arc arc2 = new Arc(0,0,0,settings);
        Point point = Point.rotatePoint(Point.getPointOf(1, -10), Point.getPointOf(1, 0), 90);

        Assert.assertTrue(Arc.areIntersecting(arc1,arc2));

        Point arc1MiddlePoint = Point.getPointOf(13, 2.5);
        Assert.assertTrue(arc1.circleMiddlePoint().equals(arc1MiddlePoint));

        Point arc2MiddlePoint = Point.getPointOf(0,-10);
        Assert.assertTrue(arc1.circleMiddlePoint().equals(arc1MiddlePoint));
    }

    @Test
    public void testEdgeEdge5(){
        ArcSettings settings = new ArcSettings(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.valueOf(6));

        Arc arc1 = new Arc(0,0,0,settings);
        Point arc1MiddlePoint = arc1.circleMiddlePoint();
        Point arc1ArcPoint = arc1.getPoint();
        Point rotatePoint = Point.rotatePoint(arc1ArcPoint, arc1MiddlePoint, 10);

        Arc arc2 = new Arc(rotatePoint.getX(),rotatePoint.getY(), BigDecimal.TEN,settings);
        Point arc2MiddlePoint = arc2.circleMiddlePoint();

        Assert.assertTrue(arc1MiddlePoint.equals(arc2MiddlePoint));
        Assert.assertTrue(Arc.areIntersecting(arc1,arc2));

        Point rotatePoint2 = Point.rotatePoint(arc1ArcPoint, arc1MiddlePoint, 10.00000000001);
        Arc arc3 = new Arc(rotatePoint2.getX(),rotatePoint2.getY(),BigDecimal.valueOf(10.00000000001),settings);
        Point arc3MiddlePoint = arc3.circleMiddlePoint();

        Assert.assertTrue(arc1MiddlePoint.equals(arc3MiddlePoint));
        Assert.assertFalse(Arc.areIntersecting(arc1,arc3));
    }
    @Test
    public void testEdgeEdge51(){
        ArcSettings settings = new ArcSettings(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.valueOf(6));

        Arc arc1 = new Arc(0,0,0,settings);
        Point arc1MiddlePoint = arc1.circleMiddlePoint();
        Point arc1ArcPoint = arc1.getPoint();
        Point rotatePoint = Point.rotatePoint(arc1ArcPoint, arc1MiddlePoint, -10);

        Arc arc2 = new Arc(rotatePoint.getX(),rotatePoint.getY(), BigDecimal.TEN.negate(),settings);
        Point arc2MiddlePoint = arc2.circleMiddlePoint();

        Assert.assertTrue(arc1MiddlePoint.equals(arc2MiddlePoint));
        Assert.assertTrue(Arc.areIntersecting(arc1,arc2));

        Point rotatePoint2 = Point.rotatePoint(arc1ArcPoint, arc1MiddlePoint, -10.00000000001);
        Arc arc3 = new Arc(rotatePoint2.getX(),rotatePoint2.getY(),BigDecimal.valueOf(10.00000000001).negate(),settings);
        Point arc3MiddlePoint = arc3.circleMiddlePoint();

        Assert.assertTrue(arc1MiddlePoint.equals(arc3MiddlePoint));
        Assert.assertFalse(Arc.areIntersecting(arc1,arc3));
    }
    @Test
    public void testEdgeEdge6(){
        ArcSettings settings = new ArcSettings(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.valueOf(6));

        Arc arc1 = new Arc(0,0,0,settings);
        Point arc1MiddlePoint = arc1.circleMiddlePoint();
        Point arc1ArcPoint = arc1.getPoint();
        Point rotatePoint = Point.rotatePoint(arc1ArcPoint, arc1MiddlePoint, 350);

        Arc arc2 = new Arc(rotatePoint.getX(),rotatePoint.getY(), BigDecimal.valueOf(350),settings);
        Point arc2MiddlePoint = arc2.circleMiddlePoint();

        Assert.assertTrue(arc1MiddlePoint.equals(arc2MiddlePoint));
        Assert.assertTrue(Arc.areIntersecting(arc1,arc2));

        Point rotatePoint2 = Point.rotatePoint(arc1ArcPoint, arc1MiddlePoint, 349.999999);
        Arc arc3 = new Arc(rotatePoint2.getX(),rotatePoint2.getY(),BigDecimal.valueOf(349.999999),settings);
        Point arc3MiddlePoint = arc3.circleMiddlePoint();

        Assert.assertTrue(arc1MiddlePoint.equals(arc3MiddlePoint));
        Assert.assertFalse(Arc.areIntersecting(arc1,arc3));
    }

    @Test
    public void testArcArc1(){
        ArcSettings settings = new ArcSettings(BigDecimal.valueOf(2),BigDecimal.valueOf(61.5),BigDecimal.valueOf(0.5));

        Arc arc1 = new Arc(-2,0,90,settings);
        Arc arc2 = new Arc(BigDecimal.valueOf(-2.3d),BigDecimal.ZERO,BigDecimal.valueOf(270),settings);

        Point arc1MiddlePoint = arc1.circleMiddlePoint();
        Point arc2MiddlePoint = arc2.circleMiddlePoint();

        Assert.assertTrue(arc1MiddlePoint.equals(Point.getPointOf(0,0)));
        Assert.assertTrue(arc2MiddlePoint.equals(Point.getPointOf(-4.3,0)));

        Assert.assertTrue(Arc.areIntersecting(arc1,arc2));
    }

    @Test
    public void testArcArc2(){
        ArcSettings settings = new ArcSettings(BigDecimal.valueOf(2),BigDecimal.valueOf(61.5),BigDecimal.valueOf(0.5));

        Arc arc1 = new Arc(-2,0,90,settings);
        Arc arc2 = new Arc(BigDecimal.valueOf(-2.5d),BigDecimal.ZERO,BigDecimal.valueOf(270),settings);

        Point arc1MiddlePoint = arc1.circleMiddlePoint();
        Point arc2MiddlePoint = arc2.circleMiddlePoint();

        Assert.assertTrue(arc1MiddlePoint.equals(Point.getPointOf(0,0)));
        Assert.assertTrue(arc2MiddlePoint.equals(Point.getPointOf(-4.5,0)));

        Assert.assertTrue(Arc.areIntersecting(arc1,arc2));
    }

    @Test
    public void testArcArc3(){
        ArcSettings settings = new ArcSettings(BigDecimal.valueOf(2),BigDecimal.valueOf(61.5),BigDecimal.valueOf(0.5));

        Arc arc1 = new Arc(-2,0,90,settings);
        Arc arc2 = new Arc(BigDecimal.valueOf(-2.500001d),BigDecimal.ZERO,BigDecimal.valueOf(270),settings);

        Point arc1MiddlePoint = arc1.circleMiddlePoint();
        Point arc2MiddlePoint = arc2.circleMiddlePoint();

        Assert.assertTrue(arc1MiddlePoint.equals(Point.getPointOf(0,0)));
        Assert.assertTrue(arc2MiddlePoint.equals(Point.getPointOf(-4.500001,0)));

        Assert.assertFalse(Arc.areIntersecting(arc1,arc2));
    }

    @Test
    public void testArcEdge(){
        ArcSettings settings = new ArcSettings(BigDecimal.valueOf(1.5),BigDecimal.valueOf(60),BigDecimal.valueOf(1.4));

        Arc arc1 = new Arc(BigDecimal.ZERO, BigDecimal.valueOf(1.5),BigDecimal.ZERO,settings);
        Arc arc2 = new Arc(BigDecimal.valueOf(-1.299180035),BigDecimal.valueOf(0.9469052273),BigDecimal.valueOf(299.8635946073),settings);

        Point point = arc2.circleMiddlePoint();
        System.out.println(point);

        Assert.assertTrue(arc1.circleMiddlePoint().equals(Point.getPointOf(0,0)));
        Assert.assertTrue(Arc.areIntersecting(arc1,arc2));

    }

    @Test
    public void testArcEdge2(){
        ArcSettings settings = new ArcSettings(BigDecimal.valueOf(1.5),BigDecimal.valueOf(60),BigDecimal.valueOf(1.4));

        Point intersectPoint = Point.getPointOf(0, 1.5);
        Point rotatePoint = Point.rotatePoint(intersectPoint, Point.getPointOf(0, 0), -30);
        System.out.println(rotatePoint);

        Point arc2Middle = Point.getPointOf(-0.7,1.5);

        Arc arc1 = new Arc(rotatePoint.getX(), rotatePoint.getY(),BigDecimal.valueOf(330),settings);
        Arc arc2 = new Arc(arc2Middle.getX(),arc2Middle.getY(),BigDecimal.valueOf(270),settings);

        Point point = arc2.circleMiddlePoint();
        System.out.println(point);

        System.out.println(arc1.circleMiddlePoint());
        Assert.assertTrue(arc1.isPartOfArc(intersectPoint));
        Assert.assertTrue(arc2.isPartOfArc(intersectPoint));

        Point supposedArc2Middle = Point.getPointOf(-2.2,1.5);

        Assert.assertTrue(supposedArc2Middle.equals(arc2.circleMiddlePoint()));

        Assert.assertTrue(Arc.areIntersecting(arc1,arc2));

    }

    @Test
    public void testArcEdge3(){
        ArcSettings settings = new ArcSettings(BigDecimal.valueOf(1.5),BigDecimal.valueOf(60),BigDecimal.valueOf(1.4));

        Point intersectPoint = Point.getPointOf(0, 1.5);
        Point rotatePoint = Point.rotatePoint(intersectPoint, Point.getPointOf(0, 0), -30);
        System.out.println(rotatePoint);

        Point arc2Middle = Point.getPointOf(-0.71,1.5);

        Arc arc1 = new Arc(rotatePoint.getX(), rotatePoint.getY(),BigDecimal.valueOf(330),settings);
        Arc arc2 = new Arc(arc2Middle.getX(),arc2Middle.getY(),BigDecimal.valueOf(270),settings);

        Point point = arc2.circleMiddlePoint();
        System.out.println(point);

        System.out.println(arc1.circleMiddlePoint());

        Assert.assertFalse(Arc.areIntersecting(arc1,arc2));

    }
    @Test
    public void testArcEdge4(){
        ArcSettings settings = new ArcSettings(BigDecimal.valueOf(1.5),BigDecimal.valueOf(60),BigDecimal.valueOf(1.4));

        Point intersectPoint = Point.getPointOf(0, 1.5);
        Point rotatePoint = Point.rotatePoint(intersectPoint, Point.getPointOf(0, 0), -30);
        System.out.println(rotatePoint);

        Point arc2Middle = Point.getPointOf(-0.69,1.5);

        Arc arc1 = new Arc(rotatePoint.getX(), rotatePoint.getY(),BigDecimal.valueOf(330),settings);
        Arc arc2 = new Arc(arc2Middle.getX(),arc2Middle.getY(),BigDecimal.valueOf(270),settings);

        Point point = arc2.circleMiddlePoint();
        System.out.println(point);


        Assert.assertTrue(Arc.areIntersecting(arc1,arc2));

    }
}
