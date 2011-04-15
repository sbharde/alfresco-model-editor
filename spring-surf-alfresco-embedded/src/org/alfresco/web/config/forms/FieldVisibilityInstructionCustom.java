/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.web.config.forms;

import java.util.List;

/**
 * @author drq
 *
 */
public class FieldVisibilityInstructionCustom extends
        FieldVisibilityInstruction
{
    private String force;
    private String modesString;
    
    /**
     * @param showOrHide
     * @param fieldId
     * @param modesString
     */
    public FieldVisibilityInstructionCustom(String showOrHide, String fieldId,
            String modesString, String force)
    {
        super(showOrHide, fieldId, modesString);
        this.force = force;
        this.modesString = modesString;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.config.forms.FieldVisibilityInstruction#getFieldId()
     */
    @Override
    public String getFieldId()
    {
        return super.getFieldId();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.config.forms.FieldVisibilityInstruction#getModes()
     */
    @Override
    public List<Mode> getModes()
    {
        return super.getModes();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.config.forms.FieldVisibilityInstruction#getShowOrHide()
     */
    @Override
    public Visibility getShowOrHide()
    {
        return super.getShowOrHide();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.config.forms.FieldVisibilityInstruction#toString()
     */
    @Override
    public String toString()
    {
        return super.toString();
    }

    /**
     * @return the force
     */
    public String getForce()
    {
        return force;
    }

    /**
     * @param force the force to set
     */
    public void setForce(String force)
    {
        this.force = force;
    }

    /**
     * @return the modesString
     */
    public String getModesString()
    {
        return modesString;
    }

    /**
     * @param modesString the modesString to set
     */
    public void setModesString(String modesString)
    {
        this.modesString = modesString;
    }

}
