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
package org.springframework.extensions.surf.alfresco.model.editor.dialog;

import java.util.HashMap;

import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.web.config.forms.Control;
import org.alfresco.web.config.forms.FormConfigRuntime;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Shell;

/**
 * @author drq
 *
 */
public class FieldControlEditDialog extends GlobalControlEditDialog
{

    /**
     * @param parent
     * @param style
     * @param service
     * @param model
     * @param formConfigRuntime
     * @param formConfigIDocument
     * @param controlName
     * @param control
     */
    public FieldControlEditDialog(Shell parent, int style,
            DictionaryService service, M2Model model,
            FormConfigRuntime formConfigRuntime, IDocument formConfigIDocument,
            String controlName, Control control)
    {
        super(parent, style, service, model, formConfigRuntime,
                formConfigIDocument, controlName, control);
    }

    /**
     * @param parent
     * @param service
     * @param model
     * @param formConfigRuntime
     * @param formConfigIDocument
     * @param controlName
     * @param control
     */
    public FieldControlEditDialog(Shell parent, DictionaryService service,
            M2Model model, FormConfigRuntime formConfigRuntime,
            IDocument formConfigIDocument, String controlName, Control control)
    {
        super(parent, service, model, formConfigRuntime, formConfigIDocument,
                controlName, control);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.dialog.GlobalControlEditDialog#initProperties()
     */
    @Override
    protected void initProperties()
    {
        this.properties = new HashMap<String,Object> ();
        this.properties.put("config-type", "default-control");
        this.properties.put("type-name", this.controlName);
    }

}
