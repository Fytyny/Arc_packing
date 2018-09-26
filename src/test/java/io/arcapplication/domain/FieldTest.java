package io.arcapplication.domain;

import io.arcapplication.exception.ArcDoesNotFitInFieldException;
import io.arcapplication.exception.ArcSettingsException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class FieldTest {
    Field field;
    ArcSettings settings;

    @Before
    public void init(){
        settings = new ArcSettings(5,50,4);
        field = new Field(1000, 1000,settings);
    }

    @Test
    public void addArcWithSameFi() throws ArcDoesNotFitInFieldException, ArcSettingsException {
        Arc arc = new Arc(0,0,0,settings);
        Assert.assertTrue(field.addArc(arc));
        Assert.assertEquals(1,field.getArcs().size());
        Assert.assertFalse(field.addArc(arc));
        System.out.println(" ");
        Assert.assertTrue(field.addArc(new Arc (5,5,0,settings)));
        Assert.assertFalse(field.addArc(new Arc (6,6,0,settings)));
        Assert.assertTrue(field.addArc(new Arc(0,7,0,settings)));
    }

    @Test
    public void addArcWithDifferentFi() throws ArcDoesNotFitInFieldException, ArcSettingsException {
        Arc arc = new Arc(0,0,0,settings);
        Assert.assertTrue(field.addArc(arc));
        Assert.assertEquals(1,field.getArcs().size());
        Assert.assertFalse(field.addArc(new Arc(0,0,180,settings)));
        Assert.assertTrue(field.addArc(new Arc(0,-10,180,settings)));
    }

    @Test
    public void addRandom(){

        for (int i = 0 ; i < 1000; i++){
            try {
                field.addRandomArc();
            } catch (ArcDoesNotFitInFieldException e) {
                e.printStackTrace();
            } catch (ArcSettingsException e) {
                e.printStackTrace();
            }
        }
        System.out.println(field.getArcs().size());
    }

    @Test
    public void getResults(){
        List<Arc> results = Field.findMostEfficentPacking();
        System.out.println(results.size());
    }

}
