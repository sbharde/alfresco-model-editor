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

import java.util.Map;

/**
 * @author drq
 *
 */
public class FormConfigUtils
{
    /**
     * @param control
     * @param cpName
     * @param cpValue
     */
    public static void addControlParam(Control control, String cpName, String cpValue)
    {
        ControlParam cp = new ControlParam(cpName, cpValue);
        control.addControlParam(cp);
    }

    /**
     * @param formConfigElement
     * @return
     */
    public static FieldVisibilityManager getFieldVisibilityManager(FormConfigElement formConfigElement)
    {
        return formConfigElement.fieldVisibilityManager;
    }

    /**
     * @param control
     * @param template
     */
    public static void setControlTemplate(Control control, String template)
    {
        control.setTemplate(template);
    }

    /**
     * @param field
     * @param type
     * @param message
     * @param messageId
     * @param validationHandler
     * @param event
     */
    public static void addConstraintDefinition(FormField field, String type, String message, String messageId,
            String validationHandler, String event)
    {
        field.addConstraintDefinition(type, message, messageId, validationHandler, event);
    }

    public static void initFormConfigElement(FormConfigElement formConfig, String formId, String submissionURL, 
            String editTemplate, String createTemplate, String viewTemplate,
            Map<String,FormField> fields, Map<String, FormSet> sets)
    {
        formConfig.setFormId(formId);
        formConfig.setSubmissionURL(submissionURL);
        formConfig.setFormTemplate("edit-form", editTemplate);
        formConfig.setFormTemplate("create-form", createTemplate);
        formConfig.setFormTemplate("view-form", viewTemplate);
        formConfig.setFields(fields);
        for (FormSet set : sets.values())
        {
            formConfig.addSet(set.getSetId(), set.getParentId(), set.getAppearance(), set.getLabel(), set.getLabelId(),set.getTemplate());
        }
    }
}
