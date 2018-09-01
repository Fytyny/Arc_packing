package io.arcapplication.domain;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MathUtils {

    private MathUtils(){}

    public static BigDecimal distance(Point one, Point two){
        return BigDecimal.valueOf(Math.sqrt(
                (one.getX().subtract(two.getX())).pow(2)
                        .add(one.getY().subtract(two.getY()).pow(2)).doubleValue()
        ));
    }

    public static Point centerOfSection(Point one, Point two){

        return Point.getPointOf(
                one.getX().add(two.getX()).divide(BigDecimal.valueOf(2)),
                one.getY().add(two.getY()).divide(BigDecimal.valueOf(2))
        );

    }

    /**
     *
     * just split onetwo distance in half and get arccos(half/R) the result will be an angle
     * then get vector two-one and scale it to r
     * thenrotate
     * @param two
     * @param radius
     * @return
     */
    public static List<Point> intersectingPointsOf(Point one, Point two, BigDecimal radius){
        Preconditions.checkArgument(one.equals(two),"There is infinity number of intersecting points");
        List<Point> points = new ArrayList<>(2);
        BigDecimal distance = MathUtils.distance(one, two);
        int compareTo = distance.compareTo(radius.multiply(BigDecimal.valueOf(2)));
        if (compareTo > 0) { //No mutual points
            return points;
        } else if (compareTo == 0){  //one mutual point
            points.add(MathUtils.centerOfSection(one,two));
            return points;
        }else { //two points
            BigDecimal divide = distance.divide(BigDecimal.valueOf(2),5,BigDecimal.ROUND_HALF_EVEN);
            double acos = Math.acos(divide.divide(radius, 5, BigDecimal.ROUND_HALF_EVEN).doubleValue());
            double degrees = Math.toDegrees(acos);
            Point vector = Point.vectorOf(one, two);
            vector = Point.scaleDownVector(one,two,vector,radius);
            Point point1 = Point.rotatePoint(vector, one, degrees);
            Point point2 = Point.rotatePoint(vector, one, -degrees);
            points.add(point1);
            points.add(point2);
            return points;
        }
    }

}
