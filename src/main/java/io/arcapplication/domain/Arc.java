package io.arcapplication.domain;

import com.google.common.base.Preconditions;
import io.arcapplication.api.ArcInterface;
import io.arcapplication.exception.ArcSettingsException;

import java.awt.geom.Line2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static io.arcapplication.AppConfig.DOUBLE_SCALE;
import static io.arcapplication.AppConfig.ROUNDING_MODE;

public class Arc implements ArcInterface {
    private final BigDecimal fi;
    private final Point point;
    private ArcSettings arcSettings;
    private Point circleMiddle;

    public Arc(BigDecimal x, BigDecimal y, BigDecimal fi, ArcSettings arcSettings) {
        this.point = Point.getPointOf(x, y);
        this.fi = fi;
        this.arcSettings = arcSettings;
    }

    public ArcSettings getArcSettings() {
        return arcSettings;
    }

    public BigDecimal getFi() {
        return fi;
    }

    public Point getPoint() {
        return point;
    }

    public static boolean areIntersecting(Arc a, Arc b) throws ArcSettingsException {
        Preconditions.checkArgument(!a.getArcSettings().equals(b.arcSettings), "Arcs should have the same settings");
        return lineInter(a, b) || computeIntersectingPoints(a, b);
    }

    /**
     * get side lines from both and check
     *
     * @param a
     * @param b
     * @return true if intersects
     */
    private static boolean lineInter(Arc a, Arc b) {
        BigDecimal radius = a.getArcSettings().getRadius();
        Point one = a.circleMiddlePoint();
        Point two = b.circleMiddlePoint();
        BigDecimal divide = a.getArcSettings().getD().divide(BigDecimal.valueOf(2), DOUBLE_SCALE,ROUNDING_MODE);

        Point aROne = one.addVector(Point.getPointOf(BigDecimal.ZERO, radius.add(divide)));
        Point aRTwo = one.addVector(Point.getPointOf(BigDecimal.ZERO, radius.subtract(divide)));
        Point bROne = two.addVector(Point.getPointOf(BigDecimal.ZERO, radius.add(divide)));
        Point bRTwo = two.addVector(Point.getPointOf(BigDecimal.ZERO, radius.subtract(divide)));


        // two sections of a (one = middle)

        BigDecimal f = normalizeDegrees(BigDecimal.valueOf(90).add(a.getFi()));
        BigDecimal afi = a.getFi().divide(BigDecimal.valueOf(2), DOUBLE_SCALE,ROUNDING_MODE);

        Point aOneY = Point.rotatePoint(aROne, one, f.add(afi).doubleValue());
        Point aOneX = Point.rotatePoint(aRTwo, one, f.add(afi).doubleValue());

        Point aTwoY = Point.rotatePoint(aROne, one, f.subtract(afi).doubleValue());
        Point aTwoX = Point.rotatePoint(aRTwo, one, f.subtract(afi).doubleValue());

        //two sections of b (two = middle)

        f = normalizeDegrees(BigDecimal.valueOf(90).add(b.getFi()));
        BigDecimal bfi = b.getFi().divide(BigDecimal.valueOf(2), DOUBLE_SCALE,ROUNDING_MODE);

        Point bOneY = Point.rotatePoint(bROne, two, f.add(bfi).doubleValue());
        Point bOneX = Point.rotatePoint(bRTwo, two, f.add(bfi).doubleValue());

        Point bTwoY = Point.rotatePoint(bROne, two, f.subtract(bfi).doubleValue());
        Point bTwoX = Point.rotatePoint(bRTwo, two, f.subtract(bfi).doubleValue());

        //first check if points alone are not a part of the other arc

        if (b.isPartOfArc(aOneY) ||
                b.isPartOfArc(aOneX) ||
                b.isPartOfArc(aTwoX) ||
                b.isPartOfArc(aTwoY) ||
                a.isPartOfArc(bOneY) ||
                a.isPartOfArc(bOneX) ||
                a.isPartOfArc(bTwoX) ||
                a.isPartOfArc(bTwoY)
        ) {
            return true;
        }
        // line circle check
        if (lineCircleCheck(aOneY, aOneX, b, radius.add(divide)) ||
                lineCircleCheck(aOneY, aOneX, b, radius.subtract(divide)) ||

                lineCircleCheck(aTwoY, aTwoX, b, radius.add(divide)) ||
                lineCircleCheck(aTwoY, aTwoX, b, radius.subtract(divide)) ||

                lineCircleCheck(bOneY, bOneY, a, radius.add(divide)) ||
                lineCircleCheck(bOneY, bOneY, a, radius.subtract(divide)) ||

                lineCircleCheck(bTwoY, bTwoX, a, radius.add(divide)) ||
                lineCircleCheck(bTwoY, bTwoX, a, radius.subtract(divide))) {
            return true;
        }
        return quickLineCheck(aOneX, aOneY, bOneX, bOneY) ||
                quickLineCheck(aOneX, aOneY, bTwoX, bTwoY) ||
                quickLineCheck(aTwoX, aTwoY, bOneX, bOneY) ||
                quickLineCheck(aTwoX, aTwoY, bTwoX, bTwoY);
    }

    public static boolean quickLineCheck(Point aOneX, Point aOneY, Point bOneX, Point bOneY) {
        return Line2D.linesIntersect(aOneX.getX().doubleValue(), aOneX.getY().doubleValue(), aOneY.getX().doubleValue(), aOneY.getY().doubleValue(), bOneX.getX().doubleValue(),
                bOneX.getY().doubleValue(), bOneY.getX().doubleValue(), bOneY.getY().doubleValue());

    }

    /**
     * https://www.mathworks.com/matlabcentral/answers/401724-how-to-check-if-a-line-segment-intersects-a-circle
     * The geometrical explanation is easy: The cross-product replies the area of the parallelogram build by the normal of the line and the vector fro P1 to C. The area of this parallelogram is identical to the area of the rectangle build by the normal N and the vector orthogonal to N through C. The value of this area is the distance multiplied by 1 (the length of the N). Summary: The distance between the line and a point in 2D is the absolute value of the 3rd component of the cross-product between N and the vector from P1 (or P2) to C.
     * <p>
     * All you have to do is to compare, if this distance is smaller or equal to the radius.
     *
     * @param lineOne
     * @param lineTwo
     * @param circleMiddle
     * @param radius
     * @return
     */
    @Deprecated
    public static boolean quickLineCircleCheck(Point lineOne, Point lineTwo, Point circleMiddle, BigDecimal radius) {
        Point p12 = Point.vectorOf(lineOne, lineTwo);
        Point norm = Point.normalizeVector(p12);

        Point p1c = Point.vectorOf(lineOne, circleMiddle);
        //cross product
        BigDecimal v = norm.getX().multiply(p1c.getY()).subtract(
                norm.getY().multiply(p1c.getX())
        ).abs();
        System.out.println(v.toString());
        return v.compareTo(radius) <= 0;

    }

    /**
     * https://stackoverflow.com/questions/1073336/circle-line-segment-collision-detection-algorithm
     * E is the starting point of the ray,
     * L is the end point of the ray,
     * C is the center of sphere you're testing against
     * r is the radius of that sphere
     * Compute:
     * d = L - E ( Direction vector of ray, from start to end )
     * f = E - C ( Vector from center sphere to ray start )
     * <p>
     * Then the intersection is found by..
     * Plugging:
     * P = E + t * d
     * This is a parametric equation:
     * Px = Ex + tdx
     * Py = Ey + tdy
     * into
     * (x - h)2 + (y - k)2 = r2
     * (h,k) = center of circle.
     * <p>
     * Note: We've simplified the problem to 2D here, the solution we get applies also in 3D
     * <p>
     * to get:
     * <p>
     * Expand
     * x2 - 2xh + h2 + y2 - 2yk + k2 - r2 = 0
     * Plug
     * x = ex + tdx
     * y = ey + tdy
     * ( ex + tdx )2 - 2( ex + tdx )h + h2 + ( ey + tdy )2 - 2( ey + tdy )k + k2 - r2 = 0
     * Explode
     * ex2 + 2extdx + t2dx2 - 2exh - 2tdxh + h2 + ey2 + 2eytdy + t2dy2 - 2eyk - 2tdyk + k2 - r2 = 0
     * Group
     * t2( dx2 + dy2 ) + 2t( exdx + eydy - dxh - dyk ) + ex2 + ey2 - 2exh - 2eyk + h2 + k2 - r2 = 0
     * Finally,
     * t2( _d * _d ) + 2t( _e * _d - _d * _c ) + _e * _e - 2( _e*_c ) + _c * _c - r2 = 0
     * *Where _d is the vector d and * is the dot product.*
     * And then,
     * t2( _d * _d ) + 2t( _d * ( _e - _c ) ) + ( _e - _c ) * ( _e - _c ) - r2 = 0
     * Letting _f = _e - _c
     * t2( _d * _d ) + 2t( _d * _f ) + _f * _f - r2 = 0
     *
     * @param lineOne
     * @param lineTwo
     * @param toIntersect
     * @param radius
     * @return
     */
    public static boolean lineCircleCheck(Point lineOne, Point lineTwo, Arc toIntersect, BigDecimal radius) {
        Point circleMiddle = toIntersect.circleMiddlePoint();
        Point ba = Point.vectorOf(lineOne, lineTwo);
        Point ca = Point.vectorOf(lineOne, circleMiddle);

        BigDecimal a = ba.getX().multiply(ba.getX()).add(ba.getY().multiply(ba.getY()));
        BigDecimal b = ca.getX().multiply(ba.getX()).add(ca.getY().multiply(ba.getY()));
        BigDecimal c = ca.getX().multiply(ca.getX()).add(ca.getY().multiply(ca.getY())).subtract(radius.pow(2));
        b = b.divide(a, DOUBLE_SCALE,ROUNDING_MODE);

        BigDecimal discriminant = b.pow(2).subtract(c.divide(a, DOUBLE_SCALE,ROUNDING_MODE));

        if (discriminant.compareTo(BigDecimal.ZERO) < 0) return false;

        else {
            List<Point> points = new ArrayList<>(2);

            //first get points and check if they are part of arc

            discriminant = BigDecimal.valueOf(Math.sqrt(discriminant.doubleValue()));

            BigDecimal scalingFactor1 = b.negate().add(discriminant);
            BigDecimal scalingFactor2 = b.negate().subtract(discriminant);

            points.add(
                    Point.getPointOf(lineOne.getX().subtract(ba.getX().multiply(scalingFactor1)),
                            lineOne.getY().subtract(ba.getY().multiply(scalingFactor1)))
            );

            if (discriminant.compareTo(BigDecimal.ZERO) != 0) {
                points.add(
                        Point.getPointOf(lineOne.getX().subtract(ba.getX().multiply(scalingFactor2)),
                                lineOne.getY().subtract(ba.getY().multiply(scalingFactor2)))
                );
            }
            System.out.println(points);
            for (Point point : points) {
                if (toIntersect.isPartOfArc(point)) return true;
            }

            return false;
        }

    }

    public Point circleMiddlePoint() {
        if (circleMiddle == null) {
            BigDecimal r = arcSettings.getRadius();
            circleMiddle = point.movePoint(point.getX(), point.getY().subtract(r));
        }
        return circleMiddle;
    }

    public static boolean computeIntersectingPoints(Arc one, Arc two) {
        Preconditions.checkArgument(!one.getArcSettings().equals(two.arcSettings), "Arcs should have the same settings");
        Preconditions.checkArgument(one.equals(two), "Arcs cant be the same");
        ArcSettings arcSettings = one.getArcSettings();
        Point oneMiddle = one.circleMiddlePoint();
        Point twoMiddle = two.circleMiddlePoint();
        List<Point> resultPoints = new ArrayList<>();
        resultPoints.addAll(MathUtils.intersectingPointsOf(oneMiddle, twoMiddle, arcSettings.getRadius().subtract(arcSettings.getD().divide(BigDecimal.valueOf(2),DOUBLE_SCALE,ROUNDING_MODE))));
        resultPoints.addAll(MathUtils.intersectingPointsOf(oneMiddle, twoMiddle, arcSettings.getRadius().add(arcSettings.getD().divide(BigDecimal.valueOf(2),DOUBLE_SCALE,ROUNDING_MODE))));

        //now remove points that arent in arc but before check their soroundings
        ArrayList<Point> clone = (ArrayList<Point>) ((ArrayList<Point>) resultPoints).clone();
        for (Point inter : clone) {
            if (one.isPartOfArc(inter) && two.isPartOfArc(inter)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        else {
            if (object instanceof Arc) {
                Arc arc = (Arc) object;
                return arc.getArcSettings().equals(this.arcSettings)
                        && arc.getFi().equals(this.fi)
                        && arc.getPoint().equals(this.point);

            }
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = arcSettings.hashCode();
        result = 31 * result + point.hashCode();
        result = 31 * result + fi.hashCode();
        return hashCode();
    }


    /**
     * To be part of an arc point p has to be inside arc or lying on one of its edges.
     *
     * @param p point to check
     * @return
     */
    public boolean isPartOfArc(Point p) {
        if (circleMiddle == null) circleMiddle = circleMiddlePoint();
        BigDecimal distance = MathUtils.distance(p, circleMiddle);
        BigDecimal d = arcSettings.getD();
        BigDecimal radius = this.arcSettings.getRadius();

        if (distance.compareTo(radius.add(d)) > 0) return false;
        else if (distance.compareTo(radius.subtract(d)) < 0) return false;
        else {
            Point point = Point.vectorOf(circleMiddle, p);
            BigDecimal degree = findDegree(point.getX(),point.getY());
            System.out.println(point);
            System.out.println(degree);
            BigDecimal rotateByFi = normalizeDegrees(BigDecimal.valueOf(90).add(this.fi));
            BigDecimal alfaInHalf = arcSettings.getAlfa().divide(BigDecimal.valueOf(2), DOUBLE_SCALE,ROUNDING_MODE);
            System.out.println(alfaInHalf);
            boolean betweenDegrees = isBetweenDegrees(degree, rotateByFi.subtract(alfaInHalf), normalizeDegrees(rotateByFi.add(alfaInHalf)));

            return betweenDegrees;
        }
    }

    public boolean isBetweenDegrees(BigDecimal degree, BigDecimal start, BigDecimal end) {
        if (start.compareTo(end) < 0) {
            return degree.compareTo(start) >= 0 && degree.compareTo(end) <= 0;
        } else {
            return degree.compareTo(start) <= 0 && degree.compareTo(end) >= 0;
        }
    }

    public static BigDecimal normalizeDegrees(BigDecimal degrees) {
        if (degrees.compareTo(BigDecimal.valueOf(360)) > 0) {
            return degrees.subtract(BigDecimal.valueOf(360));
        } else if (degrees.compareTo(BigDecimal.valueOf(0)) < 0) {
            return degrees.add(BigDecimal.valueOf(360));

        } else {
            return degrees;
        }
    }

    public BigDecimal findDegree(BigDecimal x, BigDecimal y) {
        return BigDecimal.valueOf(Math.toDegrees(Math.atan2(y.doubleValue(), x.doubleValue())));
    }

    @Override
    public String toString() {
        return "Arc{" +
                "fi=" + fi +
                ", point=" + point +
                ", arcSettings=" + arcSettings +
                '}';
    }
}
