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
package org.springframework.extensions.surf.alfresco.model.editor.properties;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

/**
 * @author drq
 *
 */
public class ModelProperties
{
    private static final Logger logger = Logger.getLogger(ModelProperties.class.getName());
    
    public static final String BOOTSTRAP_CONTEXT_PROPERTY = "BOOTSTRAP_CONTEXT";
    public static final String WEBCLIENT_CONFIG_PROPERTY  = "WEBCLIENT_CONFIG";
    public static final String BOOTSTRAP_MODELS_PROPERTY  = "BOOTSTRAP_MODELS";
    public static final String FORM_CONFIG_PROPERTY  = "FORM_CONFIG";
    
    /**
     * @param modelFile
     * @param name
     * @return
     */
    public static String getModelProperty(IResource modelFile,String name)
    {
       if (modelFile == null)
       {
           return null;
       }
       else
       {
        try
        {
            return modelFile.getPersistentProperty(new QualifiedName("", name));
        }
        catch (CoreException e)
        {
            logger.log(Level.SEVERE, "Failed to retrieve property "+name,e);
            return null;
        }
       }
    }

    /**
     * @param modelFile
     * @param name
     * @param value
     * @return
     */
    public static String setModelProperty(IResource modelFile,String name,String value)
    {
       if (modelFile == null)
       {
           return null;
       }
       else
       {
        try
        {
            modelFile.setPersistentProperty(new QualifiedName("", name),value);
            return value;
        }
        catch (CoreException e)
        {
            logger.log(Level.SEVERE, "Failed to set property "+name+" with value "+value,e);
            return null;
        }
       }
    }
}
