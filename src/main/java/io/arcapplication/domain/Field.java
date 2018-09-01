package io.arcapplication.domain;

import io.arcapplication.api.FieldInterface;
import io.arcapplication.exception.ArcDoesNotFitInFieldException;
import io.arcapplication.exception.ArcSettingsException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Field implements FieldInterface{
    private List<Arc> arcs;

    private BigDecimal x,y;

    public boolean addArc(Arc arc) throws ArcDoesNotFitInFieldException, ArcSettingsException {
        for (Arc a : arcs){
            if (a.areIntersecting(a,arc)){
                return false;
            }
        }
        return true;
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
}
