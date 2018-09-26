package io.arcapplication.domain;

import com.google.common.base.Preconditions;
import io.arcapplication.AppConfig;
import io.arcapplication.api.ArcInterface;
import io.arcapplication.exception.ArcSettingsException;

import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static io.arcapplication.AppConfig.DOUBLE_SCALE;
import static io.arcapplication.AppConfig.ROUNDING_MODE;
import static java.lang.Math.max;
import static java.lang.Math.min;

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

    public Arc(int x, int y, int fi, ArcSettings arcSettings) {
        this(BigDecimal.valueOf(x), BigDecimal.valueOf(y), BigDecimal.valueOf(fi), arcSettings);
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

    public static boolean areIntersecting(Arc a, Arc b) {
        if (a.getPoint().equals(b.getPoint())) return true;
        Preconditions.checkArgument(a.getArcSettings().equals(b.arcSettings), "Arcs should have the same settings");
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
        BigDecimal divide = a.getArcSettings().getD().divide(BigDecimal.valueOf(2), DOUBLE_SCALE, ROUNDING_MODE);

        Point aROne = one.addVector(Vector.vectorOf(BigDecimal.ZERO, radius.add(divide)));
        Point aRTwo = one.addVector(Vector.vectorOf(BigDecimal.ZERO, radius.subtract(divide)));
        Point bROne = two.addVector(Vector.vectorOf(BigDecimal.ZERO, radius.add(divide)));
        Point bRTwo = two.addVector(Vector.vectorOf(BigDecimal.ZERO, radius.subtract(divide)));


        // two sections of a (one = middle)

        BigDecimal f = a.getFi();
        BigDecimal alfa = a.getArcSettings().getAlfa().divide(BigDecimal.valueOf(2), DOUBLE_SCALE, ROUNDING_MODE);

        Point aOneY = Point.rotatePoint(aROne, one, f.add(alfa).doubleValue());
        Point aOneX = Point.rotatePoint(aRTwo, one, f.add(alfa).doubleValue());

        Point aTwoY = Point.rotatePoint(aROne, one, f.subtract(alfa).doubleValue());
        Point aTwoX = Point.rotatePoint(aRTwo, one, f.subtract(alfa).doubleValue());

        //two sections of b (two = middle)

        f = b.getFi();

        Point bOneY = Point.rotatePoint(bROne, two, f.add(alfa).doubleValue());
        Point bOneX = Point.rotatePoint(bRTwo, two, f.add(alfa).doubleValue());

        Point bTwoY = Point.rotatePoint(bROne, two, f.subtract(alfa).doubleValue());
        Point bTwoX = Point.rotatePoint(bRTwo, two, f.subtract(alfa).doubleValue());

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
        return doIntersect(aOneX, aOneY, bOneX, bOneY) ||
                doIntersect(aOneX, aOneY, bTwoX, bTwoY) ||
                doIntersect(aTwoX, aTwoY, bOneX, bOneY) ||
                doIntersect(aTwoX, aTwoY, bTwoX, bTwoY);
    }

    @Deprecated
    public static boolean quickLineCheck(Point aOneX, Point aOneY, Point bOneX, Point bOneY) {
        return Line2D.linesIntersect(aOneX.getX().doubleValue(), aOneX.getY().doubleValue(), aOneY.getX().doubleValue(), aOneY.getY().doubleValue(), bOneX.getX().doubleValue(),
                bOneX.getY().doubleValue(), bOneY.getX().doubleValue(), bOneY.getY().doubleValue());

    }

    /**
     * Method is computing intersecting points of two line segments.
     *
     * @param e start point of first segment
     * @param l end point of first segment
     * @param s start point of second segment
     * @param k end point of second segment
     * @return true if two lines are intersecting
     */
    public static boolean cramerLineIntersect(Point e, Point l, Point s, Point k) {
        if (e.equals(s) || e.equals(k) || l.equals(s) || l.equals(k)) return true;
        Vector d = Vector.vectorOf(e, l);
        Vector j = Vector.vectorOf(s, k);

        BigDecimal eq1 = s.getX().subtract(e.getX());
        BigDecimal eq2 = s.getY().subtract(e.getY());

        BigDecimal matrixDet = d.getX().multiply(j.getY().negate()).subtract(j.getX().negate().multiply(d.getY()));
        BigDecimal uDet = eq1.multiply(j.getY().negate()).subtract(j.getX().negate().multiply(eq2));
        BigDecimal iDet = d.getX().multiply(eq2).subtract(eq1.multiply(d.getY()));

        if (matrixDet.compareTo(BigDecimal.ZERO) == 0) {
            if (uDet.compareTo(BigDecimal.ZERO) == 0 && iDet.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal scalar1 = getScalar(s, e, d);
                BigDecimal scalar = getScalar(k, e, d);
                boolean res = ((scalar1.compareTo(BigDecimal.ZERO) >= 0 && scalar1.compareTo(BigDecimal.ONE) <= 0) ||
                        (scalar.compareTo(BigDecimal.ZERO) >= 0 && scalar.compareTo(BigDecimal.ONE) <= 0)) ||
                        ((scalar.compareTo(BigDecimal.ZERO) <= 0 && scalar1.compareTo(BigDecimal.ONE) >= 0)
                                || (scalar1.compareTo(BigDecimal.ZERO) <= 0 && scalar.compareTo(BigDecimal.ONE) >= 0
                        ));
                return res;
            }
            return false;
        } else {
            BigDecimal u = uDet.divide(matrixDet, AppConfig.DOUBLE_SCALE, AppConfig.ROUNDING_MODE);
            BigDecimal i = iDet.divide(matrixDet, AppConfig.DOUBLE_SCALE, AppConfig.ROUNDING_MODE);
            boolean b = u.compareTo(BigDecimal.ZERO) >= 0 && u.compareTo(BigDecimal.ONE) <= 0 &&
                    i.compareTo(BigDecimal.ZERO) >= 0 && i.compareTo(BigDecimal.ONE) <= 0;
            return b;
        }

    }

    // Given three colinear points p, q, r, the function checks if
// point q lies on line segment 'pr'
    private static boolean onSegment(Point p, Point q, Point r)
    {
        if (Double.compare(q.getX().doubleValue(),max(p.getX().doubleValue(), r.getX().doubleValue())) <= 0 && Double.compare(q.getX().doubleValue() , min(p.getX().doubleValue(), r.getX().doubleValue())) >= 0 &&
                Double.compare(q.getY().doubleValue(),max(p.getY().doubleValue(), r.getY().doubleValue())) <= 0 && Double.compare(q.getY().doubleValue(), min(p.getY().doubleValue(), r.getY().doubleValue())) >= 0){
            return true;
        }
        return false;
    }
    private static int orientation(Point p, Point q, Point r)
    {
        // See https://www.geeksforgeeks.org/orientation-3-ordered-points/
        // for details of below formula.
        BigDecimal val = (q.getY().subtract( p.getY())).multiply(r.getX().subtract(q.getX())).subtract(
                q.getX().subtract(p.getX()).multiply(r.getY().subtract(q.getY())));

        if (val.compareTo(BigDecimal.ZERO) == 0) return 0;  // colinear

        return (val.compareTo(BigDecimal.ZERO) > 0)? 1: 2; // clock or counterclock wise
    }

    private static boolean doIntersect(Point p1, Point q1, Point p2, Point q2)
    {
        // Find the four orientations needed for general and
        // special cases
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        // General case
        if (o1 != o2 && o3 != o4)
            return true;

        // Special Cases
        // p1, q1 and p2 are colinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;

        // p1, q1 and q2 are colinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;

        // p2, q2 and p1 are colinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;

        // p2, q2 and q1 are colinear and q1 lies on segment p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;

        return false; // Doesn't fall in any of the above cases
    }
    /**
     * Get u from E + u * D = X when there is a result for sure
     *
     * @param x
     * @param e
     * @param d
     * @return
     */
    public static BigDecimal getScalar(Point x, Point e, Vector d) {
        if (d.getX().compareTo(BigDecimal.ZERO) != 0) {
            return x.getX().subtract(e.getX()).divide(d.getX(), AppConfig.DOUBLE_SCALE, AppConfig.ROUNDING_MODE);
        } else if (d.getY().compareTo(BigDecimal.ZERO) != 0) {
            return x.getY().subtract(e.getY()).divide(d.getY(), AppConfig.DOUBLE_SCALE, AppConfig.ROUNDING_MODE);
        } else {
            return BigDecimal.ZERO;
        }
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
        Vector p12 = Vector.vectorOf(lineOne, lineTwo);
        Vector norm = Vector.normalizeVector(p12);

        Vector p1c = Vector.vectorOf(lineOne, circleMiddle);
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
        Vector d = Vector.vectorOf(lineOne, lineTwo);
        Vector f = Vector.vectorOf(circleMiddle, lineOne);

        BigDecimal a = d.getX().multiply(d.getX()).add(d.getY().multiply(d.getY()));

        BigDecimal b = BigDecimal.valueOf(2).multiply(f.getX().multiply(d.getX()).add(f.getY().multiply(d.getY())));
        BigDecimal c = f.getX().multiply(f.getX()).add(f.getY().multiply(f.getY())).subtract(radius.pow(2));
        if (a.compareTo(BigDecimal.ZERO) != 0) {

            //b = b.divide(a, DOUBLE_SCALE, ROUNDING_MODE);

            BigDecimal discriminant = b.pow(2).subtract(BigDecimal.valueOf(4).multiply(c).multiply(a));

            if (discriminant.compareTo(BigDecimal.ZERO) < 0) return false;

            else {
                List<Point> points = new ArrayList<>(2);

                //first get points and check if they are part of arc

                discriminant = BigDecimal.valueOf(Math.sqrt(discriminant.doubleValue()));

                BigDecimal t1 = b.negate().add(discriminant).divide(BigDecimal.valueOf(2).multiply(a),AppConfig.DOUBLE_SCALE,AppConfig.ROUNDING_MODE);
                BigDecimal t2 = b.negate().subtract(discriminant).divide(BigDecimal.valueOf(2).multiply(a),AppConfig.DOUBLE_SCALE,AppConfig.ROUNDING_MODE);

                if (t1.compareTo(BigDecimal.ZERO) >= 0 && t1.compareTo(BigDecimal.ONE) <= 0){
                    Point p = lineOne.addVector(Vector.vectorOf(d.getX().multiply(t1), d.getY().multiply(t1)));
                    boolean partOfArc = toIntersect.isPartOfArc(p);
                    if (partOfArc) return partOfArc;

                }

                if(t2.compareTo(BigDecimal.ZERO) >= 0 && t2.compareTo(BigDecimal.ONE)<=0){
                    Point p = lineOne.addVector(Vector.vectorOf(d.getX().multiply(t2), d.getY().multiply(t2)));
                    boolean partOfArc = toIntersect.isPartOfArc(p);
                    if (partOfArc) return partOfArc;
                }

                return false;
            }
        } else{
            if (b.compareTo(BigDecimal.ZERO) == 0 && c.compareTo(BigDecimal.ZERO) == 0){
                return true;
            }
            else if (b.compareTo(BigDecimal.ZERO) != 0){
                BigDecimal x = c.negate().divide(b, AppConfig.DOUBLE_SCALE, AppConfig.DOUBLE_SCALE);
                if (x.compareTo(BigDecimal.ZERO) >= 0 && x.compareTo(BigDecimal.ONE) >= 0){
                    return toIntersect.isPartOfArc(lineOne.addVector(Vector.vectorOf(d.getX().multiply(x), d.getY().multiply(x))));

                }
            }
            else return false;
        }
        return false;

    }

    /*
        Get r of standard circle
     */
    public Point circleMiddlePoint() {
        if (circleMiddle == null) {
            BigDecimal r = arcSettings.getRadius();
            Point var = point.addVector(Vector.vectorOf(BigDecimal.ZERO, r.negate()));
            this.circleMiddle = Point.rotatePoint(var, point, this.fi.doubleValue());
        }
        return circleMiddle;
    }

    private static boolean computeIntersectingPoints(Arc one, Arc two) {
        ArcSettings arcSettings = one.getArcSettings();
        Point oneMiddle = one.circleMiddlePoint();
        Point twoMiddle = two.circleMiddlePoint();
        List<Point> resultPoints = new ArrayList<>();
        if (!oneMiddle.equals(twoMiddle)) {
            BigDecimal subtractR = arcSettings.getRadius().subtract(arcSettings.getD().divide(BigDecimal.valueOf(2), DOUBLE_SCALE, ROUNDING_MODE));
            BigDecimal addR = arcSettings.getRadius().add(arcSettings.getD().divide(BigDecimal.valueOf(2), DOUBLE_SCALE, ROUNDING_MODE));

            resultPoints.addAll(MathUtils.intersectingPointsOfTwoCircles(oneMiddle, subtractR, twoMiddle, subtractR));
            resultPoints.addAll(MathUtils.intersectingPointsOfTwoCircles(oneMiddle, addR, twoMiddle, addR));

            resultPoints.addAll(MathUtils.intersectingPointsOfTwoCircles(oneMiddle, addR, twoMiddle, subtractR));
            resultPoints.addAll(MathUtils.intersectingPointsOfTwoCircles(oneMiddle, subtractR, twoMiddle, addR));

        }
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
        BigDecimal d = arcSettings.getD().divide(BigDecimal.valueOf(2),AppConfig.DOUBLE_SCALE,AppConfig.ROUNDING_MODE);
        BigDecimal radius = this.arcSettings.getRadius();

        if (distance.compareTo(radius.add(d)) > 0) return false;
        else if (distance.compareTo(radius.subtract(d)) < 0) return false;
        else {
            Vector vector = Vector.vectorOf(circleMiddle, p);
            BigDecimal degree = normalizeDegrees(findDegree(vector.getX(), vector.getY()));
            BigDecimal rotateByFi = normalizeDegrees(BigDecimal.valueOf(90).add(this.fi));
            BigDecimal alfaInHalf = arcSettings.getAlfa().divide(BigDecimal.valueOf(2), DOUBLE_SCALE, ROUNDING_MODE);
            boolean betweenDegrees = isBetweenDegrees(degree, normalizeDegrees(rotateByFi.subtract(alfaInHalf)), normalizeDegrees(rotateByFi.add(alfaInHalf)));

            return betweenDegrees;
        }
    }

    public boolean isBetweenDegrees(BigDecimal degree, BigDecimal start, BigDecimal end) {
        if (start.compareTo(end) < 0) {
            return degree.compareTo(start) >= 0 && degree.compareTo(end) <= 0;
        } else {
            return degree.compareTo(start) <= 0 && degree.compareTo(end) <= 0 || degree.compareTo(start) >=0 && degree.compareTo(end)>=0;
        }
    }

    public static BigDecimal normalizeDegrees(BigDecimal degrees) {
        if (degrees.compareTo(BigDecimal.valueOf(360)) == 0){
            return BigDecimal.ZERO;
        }
        else if (degrees.compareTo(BigDecimal.valueOf(360)) > 0) {
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
