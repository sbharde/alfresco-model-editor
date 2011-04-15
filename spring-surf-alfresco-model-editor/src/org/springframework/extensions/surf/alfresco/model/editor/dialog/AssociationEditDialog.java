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

import org.alfresco.repo.dictionary.M2Association;
import org.alfresco.repo.dictionary.M2Class;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.ModelUtils;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.web.config.WebConfigRuntime;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * Schema Association Editor Dialog
 * 
 * @author drq
 *
 */
public class AssociationEditDialog extends ClassAssociationEditDialog
{

    /**
     * 
     * @param parent parent shell
     * @param style dialog style
     * @param association mapped webscript resource
     */
    public AssociationEditDialog(Shell parent, int style, M2Association association, DictionaryService service,M2Model model, WebConfigRuntime webConfigRuntime, M2Class clazz,IDocument webclientConfigIDocument)
    {
        super(parent, style, association,service, model,webConfigRuntime,clazz,webclientConfigIDocument);
        this.association = association;
        this.updatedAssociation = ModelUtils.newAssociation(association.getName());
        ModelUtils.copyAssociation((M2Association) this.updatedAssociation, association);
    }

    /**
     * 
     * @param parent parent shell
     * @param association mapped webscript resource
     */
    public AssociationEditDialog(Shell parent, M2Association association, DictionaryService service,M2Model model, WebConfigRuntime webConfigRuntime, M2Class clazz,IDocument webclientConfigIDocument)
    {
        this(parent,SWT.PRIMARY_MODAL, association,service, model,webConfigRuntime,clazz,webclientConfigIDocument);
    }

	/* (non-Javadoc)
	 * @see org.springframework.extensions.surf.alfresco.model.editor.dialog.ClassAssociationEditDialog#buildRightSection()
	 */
	protected void buildRightSection()
	{
		super.buildRightSection();
		buildWebclientSection (right);
	}
}
