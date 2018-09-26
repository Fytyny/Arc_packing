package io.arcapplication.domain;

import java.math.BigDecimal;

import static io.arcapplication.AppConfig.DOUBLE_SCALE;
import static io.arcapplication.AppConfig.ROUNDING_MODE;

public final class Vector {

    private final Point v;

    private Vector(Point v){
        this.v = v;
    }

    public static Vector vectorOf(Point a, Point b){
        return new Vector(Point.getPointOf(b.getX().subtract(a.getX()),b.getY().subtract(a.getY())));
    }
    public static Vector vectorOf(BigDecimal a, BigDecimal b){
        return new Vector(Point.getPointOf(a,b));
    }

    public Point getV() {
        return v;
    }

    public BigDecimal getX(){
        return v.getX();
    }

    public  BigDecimal getY(){
        return v.getY();
    }

    /**
     * Method scale vector from argument to radius and returns a point r away from one of the points in the direction to another point.
     * @param vector vector b-a
     * @param targetDistance
     * @return
     */
    public static Vector scaleVector(BigDecimal distance, Vector vector, BigDecimal targetDistance){
        BigDecimal scale = targetDistance.divide(distance, DOUBLE_SCALE, ROUNDING_MODE);
        return new Vector(Point.getPointOf(vector.getV().getX().multiply(scale),vector.getV().getY().multiply(scale)));
    }

    public static Vector normalizeVector(Vector p){
        BigDecimal len = BigDecimal.valueOf(Math.sqrt(p.getX().pow(2).add(p.getY().pow(2)).doubleValue()));
        return new Vector(Point.getPointOf(p.getX().divide(len,DOUBLE_SCALE,ROUNDING_MODE),p.getY().divide(len, DOUBLE_SCALE, ROUNDING_MODE)));

    }
}
