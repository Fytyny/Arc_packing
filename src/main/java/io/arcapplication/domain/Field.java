package io.arcapplication.domain;

import com.google.common.base.Preconditions;
import io.arcapplication.AppConfig;
import io.arcapplication.api.FieldInterface;
import io.arcapplication.exception.ArcDoesNotFitInFieldException;
import io.arcapplication.exception.ArcSettingsException;
import org.checkerframework.checker.units.qual.A;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class is representing field of rectangular shape
 */
public class Field implements FieldInterface{
    private List<Arc> arcs;
    private ArcSettings arcSettings;
    private BigDecimal x,y;

    public Field (BigDecimal x, BigDecimal y, ArcSettings arcSettings){
        this.x = x;
        this.y = y;
        arcs = new ArrayList<>();
        this.arcSettings = arcSettings;
    }
    public Field (int x, int y, ArcSettings arcSettings){
        this(BigDecimal.valueOf(x),BigDecimal.valueOf(y), arcSettings);
    }
    public boolean addArc(Arc arc) throws ArcDoesNotFitInFieldException, ArcSettingsException {
        Preconditions.checkArgument(arc.getArcSettings().equals(this.arcSettings), "Arc settings are different");
        for (Arc a : arcs){
            if (a.areIntersecting(a,arc) || isIntersecting(arc)){
                return false;
            }
        }
        arcs.add(arc);
        return true;
    }

    public boolean isIntersecting(Arc arc){
        BigDecimal xDivided = x.divide(BigDecimal.valueOf(2), AppConfig.DOUBLE_SCALE,AppConfig.ROUNDING_MODE);
        BigDecimal yDivided = y.divide(BigDecimal.valueOf(2), AppConfig.DOUBLE_SCALE,AppConfig.ROUNDING_MODE);

        Point lOne = Point.getPointOf(xDivided.negate(),yDivided);
        Point lTwo = Point.getPointOf(xDivided.negate(),yDivided.negate());
        Point rOne = Point.getPointOf(xDivided,yDivided);
        Point rTwo = Point.getPointOf(xDivided,yDivided.negate());

        // edges: lOne - lTwo, rOne - rTwo, lOne - rOne, lTwo - rTwo

        Point cPoint = arc.circleMiddlePoint();
        BigDecimal radius = arc.getArcSettings().getRadius();
        if (cPoint.getX().abs().compareTo(rOne.getX().subtract(radius)) < 0 &&
                cPoint.getY().abs().compareTo(rOne.getY().subtract(radius)) < 0) return false;
        else{
            BigDecimal divide = arc.getArcSettings().getD().divide(BigDecimal.valueOf(2), AppConfig.DOUBLE_SCALE, AppConfig.ROUNDING_MODE);

            if (cPoint.getY().compareTo(BigDecimal.ZERO) <= 0){
                return Arc.lineCircleCheck(lOne,lTwo,arc,radius) || Arc.lineCircleCheck(lTwo,rTwo,arc,radius) || Arc.lineCircleCheck(rOne,rTwo,arc,radius);
            } else {
                return Arc.lineCircleCheck(lOne,lTwo,arc,radius) || Arc.lineCircleCheck(lOne,rOne,arc,radius) || Arc.lineCircleCheck(rOne,rTwo,arc,radius);
            }
        }

    }

    public List<Arc> getArcs() {
        return arcs;
    }

    public void setArcs(List<Arc> arcs) {
        this.arcs = arcs;
    }

    public BigDecimal getX() {
        return x;
    }

    public void setX(BigDecimal x) {
        this.x = x;
    }

    public BigDecimal getY() {
        return y;
    }

    public void setY(BigDecimal y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Field)) return false;
        Field field = (Field) o;
        return Objects.equals(arcs, field.arcs) &&
                x.compareTo(field.getX()) == 0 &&
                y.compareTo(field.getY()) == 0;
    }

    @Override
    public int hashCode() {
        int result = 31 * arcs.hashCode() + x.hashCode();
        return 31 * result + y.hashCode();
    }

    @Override
    public String toString() {
        return "Field{" +
                "arcs=" + arcs +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public static void main(String[] args) {
        ArcSettings settings = new ArcSettings(5,50,4);
        Field field = new Field(1000, 1000,settings);

        Arc arc = new Arc(0,0,0,settings);



    }
}
