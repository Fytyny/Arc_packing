package io.arcapplication.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class ArcSettings {
    private BigDecimal radius,alfa,d;

    public ArcSettings(BigDecimal radius, BigDecimal alfa, BigDecimal d) {
        this.radius = radius;
        this.alfa = alfa;
        this.d = d;
    }
    public ArcSettings(int radius, int alfa, int d) {
        this(BigDecimal.valueOf(radius), BigDecimal.valueOf(alfa), BigDecimal.valueOf(d));
    }
    public BigDecimal getRadius() {
        return radius;
    }

    public void setRadius(BigDecimal radius) {
        this.radius = radius;
    }

    public BigDecimal getAlfa() {
        return alfa;
    }

    public void setAlfa(BigDecimal alfa) {
        this.alfa = alfa;
    }

    public BigDecimal getD() {
        return d;
    }

    public void setD(BigDecimal d) {
        this.d = d;
    }

    @Override
    public String toString() {
        return "ArcSettings{" +
                "radius=" + radius +
                ", alfa=" + alfa +
                ", d=" + d +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArcSettings)) return false;
        ArcSettings that = (ArcSettings) o;
        return radius.compareTo(that.getRadius()) == 0 &&
                alfa.compareTo(that.getAlfa())== 0 &&
                d.compareTo(that.getD()) == 0;
    }

    @Override
    public int hashCode() {
        int result = 31 * radius.hashCode()+ alfa.hashCode();
        result = 31 * result + d.hashCode();
        return result;
    }
}
