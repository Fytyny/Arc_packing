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
import java.math.RoundingMode;
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
        this.x = x.setScale(AppConfig.DOUBLE_SCALE,AppConfig.ROUNDING_MODE);;
        this.y = y.setScale(AppConfig.DOUBLE_SCALE, AppConfig.ROUNDING_MODE);
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
        double degrees = Math.toRadians(degr);
        BigDecimal x = around.getX();
        BigDecimal y = around.getY();
        BigDecimal arX = point.getX();
        BigDecimal arY = point.getY();
        BigDecimal cosVal = BigDecimal.valueOf(cos(degrees)).setScale(AppConfig.DOUBLE_SCALE, RoundingMode.DOWN);
        BigDecimal sinVal = BigDecimal.valueOf(sin(degrees)).setScale(AppConfig.DOUBLE_SCALE, RoundingMode.DOWN);
        BigDecimal newX = cosVal.multiply(arX.subtract(x)).subtract(sinVal.multiply(arY.subtract(y)));
        BigDecimal newY = sinVal.multiply(arX.subtract(x)).add(cosVal.multiply(arY.subtract(y)));
        Vector vector = Vector.vectorOf(newX, newY);
        return around.addVector(vector);
    }


    public Point addVector(Vector vector){
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
