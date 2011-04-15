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

import org.alfresco.repo.dictionary.M2Class;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Type;
import org.alfresco.repo.dictionary.ModelUtils;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.web.config.WebConfigRuntime;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.springframework.extensions.surf.commons.ui.FormSectionUtils;

/**
 * @author drq
 *
 */
public class TypeEditDialog extends ClassEditDialog
{

    /**
     * @param parent
     * @param style
     * @param clazz
     * @param service
     * @param model
     */
    public TypeEditDialog(Shell parent, int style, M2Type clazz,
            DictionaryService service, M2Model model, WebConfigRuntime webConfigRuntime,IDocument webclientConfigIDocument)
    {
        super(parent, style, clazz, service, model, webConfigRuntime,webclientConfigIDocument);
        //populate updatedClass
        this.updatedClass = ModelUtils.newType(clazz.getName());
        ModelUtils.copyType((M2Type) updatedClass, clazz);
    }

    /**
     * @param parent
     * @param clazz
     * @param service
     * @param model
     */
    public TypeEditDialog(Shell parent, M2Type clazz,
            DictionaryService service, M2Model model, WebConfigRuntime webConfigRuntime,IDocument webclientConfigIDocument)
    {
        this(parent, SWT.PRIMARY_MODAL,clazz, service, model, webConfigRuntime,webclientConfigIDocument);
    }

	/* (non-Javadoc)
	 * @see org.springframework.extensions.surf.alfresco.model.editor.dialog.ClassEditDialog#buildWebclientSection(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	void buildWebclientSection(Composite parentElem)
	{
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
	    
	    Section webclientConfigSection = FormSectionUtils.createStaticSection(toolkit,parentElem,"Web Client Configuration","View or Update Web Client Configurations");        
		Composite  webclientConfigSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, webclientConfigSection);

		// build basic information section
		Composite webclientConfig = toolkit.createComposite(webclientConfigSectionContainer);        
		webclientConfig.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		webclientConfig.setLayout(new GridLayout(2, false));

		// build short name text field
		Label label = new Label(webclientConfig, SWT.NULL);
		label.setText("&Advanced Search:");

		advancedSearchButton = new Button (webclientConfig, SWT.CHECK);
		advancedSearchButton.setText ("Check if configured for advanced search");
		advancedSearchButton.setLayoutData(gridData);

		if (getWebConfigRuntime() != null)
		{
			advancedSearchButton.setSelection (getWebConfigRuntime().findTypeAdvancedSearchOption(clazz.getName()));
		}
		label = new Label(webclientConfig, SWT.NULL);
		label.setText("&Content Wizards:");

		contentWizardsButton = new Button (webclientConfig, SWT.CHECK);
		contentWizardsButton.setText ("Check if configured for content wizards");
		contentWizardsButton.setLayoutData(gridData);

		if (getWebConfigRuntime() != null)
		{
			contentWizardsButton.setSelection (getWebConfigRuntime().findTypeContentWizardsOption(clazz.getName()));
		}
		else
		{
		    advancedSearchButton.setEnabled(false);
		    contentWizardsButton.setEnabled(false);
		}
		webclientConfigSection.setClient(webclientConfigSectionContainer);		
	}

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.ClassEditDialog#open()
     */
    public M2Class open() 
    {
        initUI("Type Editor");
        buildHeadSection();
        buildLeftSection();
        buildRightSection();
        buildWebclientSection(right);
        buildBottomSection();
        addProcessingListeners();
        openUI(1300,800);
        return updatedClass;
    }
}
