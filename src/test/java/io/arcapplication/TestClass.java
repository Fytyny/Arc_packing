package io.arcapplication;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.arcapplication.domain.Point;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
public class TestClass {
    @Test
    public void test(){
        String n = null;
        System.out.println(n instanceof String);



        List<Point> pointList = Lists.newArrayList(Point.getPointOf(0,0),Point.getPointOf(1,9),Point.getPointOf(0,0));
        removeDuplicates(pointList);
        System.out.println(pointList.toString());

        Assert.assertEquals(2,pointList.size());

        List<Integer> integers = Lists.newArrayList(1);
        lol(pointList,integers);
        System.out.println(integers.get(1) + 23);

        integers(32.32);
    }


    public void removeDuplicates(List<?> list){
        Set<Object> set = new HashSet<>();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            Object next = iterator.next();
            if (!set.add(next)) iterator.remove();
        }
    }

    public void lol(List<?> wild, List<?> card){

        System.out.println("wildcard " +card.toString());
    }

    @SafeVarargs
    public final <E extends Number> List<E> integers(E... elements){
        return Lists.newArrayList(elements);
    }
}
