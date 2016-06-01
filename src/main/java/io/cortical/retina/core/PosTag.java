/*******************************************************************************
 * Copyright (c) cortical.io GmbH. All rights reserved.
 *  
 * This software is confidential and proprietary information.
 * You shall use it only in accordance with the terms of the
 * license agreement you entered into with cortical.io GmbH.
 ******************************************************************************/
package io.cortical.retina.core;

/**
 * 
 * Available Part of Speech tags.
 */
public enum PosTag {
    CD, CW, FW, JJ, LRB, MD, NN, NNP, NNPS, NNS, P, PUNCT, RB, SYM, VB;
    
    /**
     * Gets the label
     *
     * @return the label
     */
    public String getLabel() {
        return name();
    }
    
    /**
     * Returns a default array of all types
     * @return
     */
    public static PosTag[] any() {
        return values();
    }
}
