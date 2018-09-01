package io.arcapplication.domain;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.arcapplication.AppConfig;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import static io.arcapplication.AppConfig.DOUBLE_SCALE;
import static io.arcapplication.AppConfig.ROUNDING_MODE;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

final public class Point {
    private final BigDecimal x;
    private final BigDecimal y;


    private Point(BigDecimal x, BigDecimal y) {
        this.x = x.setScale(5,BigDecimal.ROUND_FLOOR);;
        this.y = y.setScale(5,BigDecimal.ROUND_FLOOR);;
    }

    public static Point getPointOf(int x, int y) {
        return new Point(BigDecimal.valueOf(x),BigDecimal.valueOf(y));
    }

    public static Point getPointOf(double x, double y) {
        return new Point(BigDecimal.valueOf(x),BigDecimal.valueOf(y));
    }

    public static Point getPointOf(BigDecimal x, BigDecimal y) {
        Preconditions.checkArgument(x != null, "X cannot be null");
        Preconditions.checkArgument(y != null, "Y cannot be null");
        return new Point(x,y);
    }

    public static Point normalizeVector(Point p){
        BigDecimal len = BigDecimal.valueOf(Math.sqrt(p.getX().pow(2).add(p.getY().pow(2)).doubleValue()));

        return Point.getPointOf(p.getX().divide(len,DOUBLE_SCALE,ROUNDING_MODE),p.getY().divide(len, DOUBLE_SCALE, ROUNDING_MODE));

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof  Point){
            Point point = (Point) obj;
            return point.getX().compareTo(this.x) == 0 && point.getY().compareTo(this.y) == 0;
        } else return false;
    }

    @Override
    public int hashCode(){
        return 31 * x.hashCode() + y.hashCode();
    }

    public static Point rotatePoint(Point point, Point around, double degr){
        Preconditions.checkArgument(point != null, "Point cannot be null");
        Preconditions.checkArgument(point != null, "Around cannot be null");
      //  Preconditions.checkArgument(degr >=0 && degr <= 360, "Degrees value has to be between 0 and 360");
        double degrees = Math.toRadians(degr);
        BigDecimal x = point.getX();
        BigDecimal y = point.getY();
        BigDecimal arX = around.getX();
        BigDecimal arY = around.getY();
        return new Point(
                new BigDecimal(cos(degrees) * x.subtract(arX).doubleValue() - sin(degrees) * y.subtract(arY).doubleValue()),
                new BigDecimal(sin(degrees) * x.subtract(arX).doubleValue()).subtract(new BigDecimal(cos(degrees) * y.subtract(arY).doubleValue()))
        );

    }

    public Point movePoint(BigDecimal x, BigDecimal y){
        Preconditions.checkArgument(x != null, "X cannot be null");
        Preconditions.checkArgument(y != null, "Y cannot be null");
        return getPointOf(this.x.add(x),this.y.add(y));
    }

    public static Point vectorOf(Point a, Point b){
        return Point.getPointOf(b.getX().subtract(a.getX()),b.getY().subtract(a.getY()));
    }

    /**
     * Method scale vector from argument to radius and returns a point r away from one of the points in the direction to another point.
     * @param a point a - middle of circle a
     * @param b point b - middle of circle b
     * @param vector vector b-a
     * @param radius
     * @return
     */
    public static Point scaleDownVector(Point a,Point b, Point vector, BigDecimal radius){
        BigDecimal distance = MathUtils.distance(a, b);
        BigDecimal scale = radius.divide(distance, DOUBLE_SCALE, ROUNDING_MODE);
        return Point.getPointOf(vector.getX().multiply(scale),vector.getY().multiply(scale));
    }

    public Point addVector(Point vector){
        return Point.getPointOf(this.getX().add(vector.getX()),this.getY().add(vector.getY()));
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
