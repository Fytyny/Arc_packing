package io.arcapplication.domain;

import com.google.common.base.Preconditions;
import io.arcapplication.AppConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MathUtils {

    private MathUtils() {
    }

    public static BigDecimal distance(Point one, Point two) {
        return BigDecimal.valueOf(Math.sqrt(
                (one.getX().subtract(two.getX())).pow(2)
                        .add(one.getY().subtract(two.getY()).pow(2)).doubleValue()
        ));
    }

    public static Point centerOfSection(Point one, Point two) {

        return Point.getPointOf(
                one.getX().add(two.getX()).divide(BigDecimal.valueOf(2)),
                one.getY().add(two.getY()).divide(BigDecimal.valueOf(2))
        );

    }

    public static Point centerOfSection(Point one, Point two, BigDecimal scale) {

        return Point.getPointOf(
                one.getX().add(two.getX()).multiply(scale),
                one.getY().add(two.getY()).multiply(scale)
        );

    }

    /**
     * just split onetwo distance in half and get arccos(half/R) the result will be an angle
     * then get vector two-one and scale it to r
     * thenrotate
     *
     * @param two
     * @param radius
     * @return
     */
    public static List<Point> intersectingPointsOfSameRadiusCircles(Point one, Point two, BigDecimal radius) {
        Preconditions.checkArgument(one.equals(two), "There is infinity number of intersecting points");
        List<Point> points = new ArrayList<>(2);
        BigDecimal distance = MathUtils.distance(one, two);
        int compareTo = distance.compareTo(radius.multiply(BigDecimal.valueOf(2)));

        if (compareTo > 0) { //No mutual points
            return points;
        } else if (compareTo == 0) {  //one mutual point
            points.add(MathUtils.centerOfSection(one, two));
            return points;
        } else { //two points
            BigDecimal divide = distance.divide(BigDecimal.valueOf(2), 5, BigDecimal.ROUND_HALF_EVEN);
            double acos = Math.acos(divide.divide(radius, 5, BigDecimal.ROUND_HALF_EVEN).doubleValue());
            double degrees = Math.toDegrees(acos);
            Vector vector = Vector.vectorOf(one, two);
            vector = vector.scaleVector(MathUtils.distance(one,two), vector, radius);
            Point oneAfterVector= one.addVector(vector);
            Point point1 = Point.rotatePoint(oneAfterVector, one, degrees);
            Point point2 = Point.rotatePoint(oneAfterVector, one, -degrees);
            points.add(point1);
            points.add(point2);
            return points;
        }
    }

    public static List<Point> intersectingPointsOfTwoCircles(Point one, BigDecimal rOne, Point two, BigDecimal rTwo) {
        if(one.equals(two) && rOne.compareTo(rTwo) == 0) return Collections.emptyList();
        if (one.equals(two)) return Collections.emptyList();
        List<Point> points = new ArrayList<>(2);
        BigDecimal distance = MathUtils.distance(one, two);
        BigDecimal scale = rOne.divide(rOne.add(rTwo), AppConfig.DOUBLE_SCALE,AppConfig.ROUNDING_MODE);
        int compareTo = distance.compareTo(rOne.add(rTwo));
        if (compareTo > 0) { //No mutual points
            return points;
        } else if (compareTo == 0) {  //one mutual point

            Vector vector = Vector.vectorOf(one, two);
            vector = Vector.scaleVector(distance,vector, rOne);

            points.add(one.addVector(vector));
            return points;
        } else { //two points
            BigDecimal arcosVal = distance.pow(2).add(rTwo.pow(2)).subtract(rOne.pow(2)).divide(
                    BigDecimal.valueOf(2).multiply(distance).multiply(rTwo), AppConfig.DOUBLE_SCALE,AppConfig.ROUNDING_MODE
            );
            double acos = BigDecimal.valueOf(Math.acos(arcosVal.doubleValue())).setScale(16, RoundingMode.DOWN).doubleValue();
            double degrees = Math.toDegrees(acos);
            Vector vector = Vector.vectorOf(two, one);
            vector = vector.scaleVector(MathUtils.distance(one,two), vector, rOne);
            Point twoAfterVector = two.addVector(vector);
            System.out.println(twoAfterVector);
            Point point1 = Point.rotatePoint(twoAfterVector, two, degrees);
            Point point2 = Point.rotatePoint(twoAfterVector, two, -degrees);
            points.add(point1);
            points.add(point2);
            return points;
        }
    }

}
