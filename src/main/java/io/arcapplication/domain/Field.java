package io.arcapplication.domain;

import com.google.common.base.Preconditions;
import io.arcapplication.AppConfig;
import io.arcapplication.api.FieldInterface;
import io.arcapplication.exception.ArcDoesNotFitInFieldException;
import io.arcapplication.exception.ArcSettingsException;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

/**
 * Class is representing field of rectangular shape
 */
final public class Field implements FieldInterface {
    private List<Arc> arcs;
    private final ArcSettings arcSettings;
    private final BigDecimal x, y;

    public Field(BigDecimal x, BigDecimal y, ArcSettings arcSettings) {
        this.x = x;
        this.y = y;
        arcs = new ArrayList<>();
        this.arcSettings = arcSettings;
    }

    public Field(int x, int y, ArcSettings arcSettings) {
        this(BigDecimal.valueOf(x), BigDecimal.valueOf(y), arcSettings);
    }

    public void clear() {
        arcs = new ArrayList<>();
    }

    public boolean addArc(Arc arc){
        Preconditions.checkArgument(arc.getArcSettings().equals(this.arcSettings), "Arc settings are different");
        if (!isPartOfField(arc.getPoint()) || isIntersecting(arc)) return false;
        for (Arc a : arcs) {
            if (a.areIntersecting(a, arc)) {
                return false;
            }
        }
        arcs.add(arc);
        return true;
    }
    private boolean isPartOfField(Point p){
        BigDecimal xDivided = x.divide(BigDecimal.valueOf(2), AppConfig.DOUBLE_SCALE, AppConfig.ROUNDING_MODE);
        BigDecimal yDivided = y.divide(BigDecimal.valueOf(2), AppConfig.DOUBLE_SCALE, AppConfig.ROUNDING_MODE);

        return p.getX().abs().compareTo(xDivided) <= 0 && p.getY().abs().compareTo(yDivided) <=0;
    }

    public boolean isIntersecting(Arc arc) {
        BigDecimal xDivided = x.divide(BigDecimal.valueOf(2), AppConfig.DOUBLE_SCALE, AppConfig.ROUNDING_MODE);
        BigDecimal yDivided = y.divide(BigDecimal.valueOf(2), AppConfig.DOUBLE_SCALE, AppConfig.ROUNDING_MODE);

        Point lOne = Point.getPointOf(xDivided.negate(), yDivided);
        Point lTwo = Point.getPointOf(xDivided.negate(), yDivided.negate());
        Point rOne = Point.getPointOf(xDivided, yDivided);
        Point rTwo = Point.getPointOf(xDivided, yDivided.negate());

        // edges: lOne - lTwo, rOne - rTwo, lOne - rOne, lTwo - rTwo

        Point cPoint = arc.circleMiddlePoint();
        BigDecimal radius = arcSettings.getRadius();
        BigDecimal divide = arc.getArcSettings().getD()
                .divide(BigDecimal.valueOf(2), AppConfig.DOUBLE_SCALE, AppConfig.ROUNDING_MODE);
        BigDecimal radiusPlusHalfD = radius.add(divide);
        if (cPoint.getX().abs()
                .compareTo(rOne.getX().subtract(radiusPlusHalfD)) < 0 &&
                cPoint.getY().abs().
                        compareTo(rOne.getY().subtract(radiusPlusHalfD)) < 0) {
            return false;
        } else {
            BigDecimal alfa = arcSettings.getAlfa();
            BigDecimal f = arc.getFi();
            Point aROne = cPoint.addVector(Vector.vectorOf(BigDecimal.ZERO, radius.add(divide)));
            Point aRTwo = cPoint.addVector(Vector.vectorOf(BigDecimal.ZERO, radius.subtract(divide)));

            //vertices of arc
            Point aOneY = Point.rotatePoint(aROne, cPoint, f.add(alfa).doubleValue());
            Point aOneX = Point.rotatePoint(aRTwo, cPoint, f.add(alfa).doubleValue());
            Point aTwoY = Point.rotatePoint(aROne, cPoint, f.subtract(alfa).doubleValue());
            Point aTwoX = Point.rotatePoint(aRTwo, cPoint, f.subtract(alfa).doubleValue());

            if (!isPartOfField(aOneY)
                    || !isPartOfField(aOneX)
                    || !isPartOfField(aTwoY)
                    || !isPartOfField(aTwoX)) {
                return true;
            }

            if (cPoint.getY().compareTo(BigDecimal.ZERO) <= 0) {
                return Arc.lineCircleCheck(lOne, lTwo, arc, radius)
                        || Arc.lineCircleCheck(lTwo, rTwo, arc, radius)
                        || Arc.lineCircleCheck(rOne, rTwo, arc, radius);
            } else {
                return Arc.lineCircleCheck(lOne, lTwo, arc, radius)
                        || Arc.lineCircleCheck(lOne, rOne, arc, radius)
                        || Arc.lineCircleCheck(rOne, rTwo, arc, radius);
            }
        }
    }

    public void addRandomArc() throws ArcDoesNotFitInFieldException, ArcSettingsException {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        double x = threadLocalRandom.nextDouble(this.x.negate().doubleValue(), this.x.doubleValue());
        double y = threadLocalRandom.nextDouble(this.y.negate().doubleValue(), this.y.doubleValue());
        double fi = threadLocalRandom.nextDouble(0, 360);

        addArc(new Arc(BigDecimal.valueOf(x), BigDecimal.valueOf(y), BigDecimal.valueOf(fi), arcSettings));
    }

    public static List<Arc> getRandomPacking(int times, ArcSettings settings, Field field) {
        Field f = new Field(field.getX(), field.getY(), settings);

        for (int i = 0; i < times; i++) {
            try {
                f.addRandomArc();
            } catch (ArcDoesNotFitInFieldException e) {
                e.printStackTrace();
            } catch (ArcSettingsException e) {
                e.printStackTrace();
            }

        }
        return f.getArcs();
    }

    public static List<Arc> findMostEfficentPacking() {
        //   BigDecimal radius = BigDecimal.ONE;
        String pathname = "progress.txt";
        File file = new File(pathname);
        BigDecimal radiusStart = BigDecimal.ONE;
        BigDecimal alfaStart = BigDecimal.ONE;
        List<Arc> packing = null;
        if (file.exists()) {
            try {
                List<String> strings = Files.readAllLines(file.toPath());
                radiusStart = new BigDecimal(strings.get(0));
                alfaStart = new BigDecimal(strings.get(1)).add(BigDecimal.valueOf(0.1d));
                int i = Integer.parseInt(strings.get(2));
                packing = new ArrayList<>();
                for (int z = 0; z < i; z++) {
                    packing.add(new Arc(radiusStart, alfaStart, alfaStart, new ArcSettings(new BigDecimal(strings.get(3)), new BigDecimal(strings.get(4)), new BigDecimal(strings.get(5)))));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println("Strting");
        System.out.println(radiusStart.toString() + " " + alfaStart);
        System.out.println("-------------------");
        int i = 0;
        for (BigDecimal radius = radiusStart; radius.compareTo(BigDecimal.valueOf(4)) < 0; radius = radius.add(BigDecimal.valueOf(0.1d))) {
            for (BigDecimal alfa = alfaStart; alfa.compareTo(BigDecimal.valueOf(20)) < 0; alfa = alfa.add(BigDecimal.valueOf(0.1d))) {

                ExecutorService executorService = Executors.newFixedThreadPool(8);
                ArrayList<List<Arc>> arcsLists = new ArrayList<>();
                int number = 0;
                for (BigDecimal d = BigDecimal.valueOf(0.1); d.compareTo(radius.divide(BigDecimal.valueOf(2), AppConfig.DOUBLE_SCALE, AppConfig.ROUNDING_MODE)) < 0; d = d.add(BigDecimal.valueOf(0.1d))) {

                    final BigDecimal radiusFinal = radius;
                    final BigDecimal alfaFinal = alfa;
                    final BigDecimal dFinal = d;
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("start thread");
                            ArcSettings settings = new ArcSettings(radiusFinal, alfaFinal, dFinal);
                            Field field = new Field(BigDecimal.valueOf(2), BigDecimal.valueOf(2), settings);
                            List<Arc> randomPacking = getRandomPacking(1000, settings, field);
                            arcsLists.add(randomPacking);
                            System.out.println("end thread");

                        }
                    });
                    number++;
                    i++;
                }

                try {
                    System.out.println(number);
                    executorService.shutdown();
                    executorService.awaitTermination(25 * number, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("finished");
                    Optional<List<Arc>> max = arcsLists.stream().max(Comparator.comparing(List::size));
                    if (max.isPresent()) {
                        if (packing != null) {
                            if (max.get().size() > packing.size()) packing = max.get();
                        } else {
                            packing = max.get();
                        }
                        if (max.get().size() < 100) break;
                    }
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    List<String> strings = new ArrayList<>();
                    strings.add(radius.toString());
                    strings.add(alfa.toString());
                    strings.add(String.valueOf(packing.size()));
                    ArcSettings arcSettings = packing.get(0).getArcSettings();
                    strings.add(arcSettings.getRadius().toString());
                    strings.add(arcSettings.getAlfa().toString());
                    strings.add(arcSettings.getD().toString());
                    Path path = Paths.get(pathname);
                    try {
                        Files.write(path, strings);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }

        }
        System.out.println(i);
        return packing;

    }

    public List<Arc> getArcs() {
        return arcs;
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }

    public ArcSettings getArcSettings() {
        return arcSettings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Field)) return false;
        Field field = (Field) o;
        return Objects.equals(arcSettings, field.getArcSettings()) &&
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
        ArcSettings settings = new ArcSettings(5, 50, 4);
        Field field = new Field(1000, 1000, settings);

        Arc arc = new Arc(0, 0, 0, settings);


    }
}
