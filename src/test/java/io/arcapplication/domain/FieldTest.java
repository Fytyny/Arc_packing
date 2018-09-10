package io.arcapplication.domain;

import io.arcapplication.exception.ArcDoesNotFitInFieldException;
import io.arcapplication.exception.ArcSettingsException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FieldTest {
    Field field;
    ArcSettings settings;

    @Before
    public void init(){
        settings = new ArcSettings(5,50,4);
        field = new Field(1000, 1000,settings);
    }

    @Test
    public void addArc() throws ArcDoesNotFitInFieldException, ArcSettingsException {
        Arc arc = new Arc(0,0,0,settings);
        Assert.assertTrue(field.addArc(arc));
        Assert.assertEquals(1,field.getArcs().size());

        Assert.assertFalse(field.addArc(arc));

    }
}
