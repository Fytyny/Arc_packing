package io.arcapplication.api;

import io.arcapplication.domain.Arc;
import io.arcapplication.exception.ArcDoesNotFitInFieldException;
import io.arcapplication.exception.ArcSettingsException;

public interface FieldInterface {
    public boolean addArc(Arc arc) throws ArcDoesNotFitInFieldException, ArcSettingsException;
}
