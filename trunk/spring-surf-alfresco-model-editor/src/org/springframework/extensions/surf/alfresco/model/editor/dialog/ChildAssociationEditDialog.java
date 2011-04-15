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

import org.alfresco.repo.dictionary.M2ChildAssociation;
import org.alfresco.repo.dictionary.M2Class;
import org.alfresco.repo.dictionary.M2ClassAssociation;
import org.alfresco.repo.dictionary.M2Model;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.springframework.extensions.surf.commons.ui.FormSectionUtils;

/**
 * @author drq
 *
 */
public class ChildAssociationEditDialog extends ClassAssociationEditDialog
{
    // UI components
    protected Button allowDuplicateChildNameButton;
    protected Button propagateTimestampsButton;
    protected Text requiredChildNameText;

    /**
     * @param parent
     * @param style
     * @param association
     * @param service
     * @param model
     */
    public ChildAssociationEditDialog(Shell parent, int style,
            M2ChildAssociation association, DictionaryService service,
            M2Model model, WebConfigRuntime webConfigRuntime, M2Class clazz, IDocument webclientConfigIDocument)
    {
        super(parent, style, association,service, model, webConfigRuntime,clazz,webclientConfigIDocument);

        this.association = association;
        this.updatedAssociation = ModelUtils.newChildAssociation(association.getName());
        ModelUtils.copyChildAssociation((M2ChildAssociation)this.updatedAssociation, association);        
    }

    /**
     * @param parent
     * @param association
     * @param service
     * @param model
     */
    public ChildAssociationEditDialog(Shell parent,
            M2ChildAssociation association, DictionaryService service,
            M2Model model, WebConfigRuntime webConfigRuntime, M2Class clazz,IDocument webclientConfigIDocument)
    {
        this(parent, SWT.PRIMARY_MODAL, association,service, model, webConfigRuntime,clazz,webclientConfigIDocument);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.ClassAssociationEditDialog#buildRightSection()
     */
    protected void buildRightSection()
    {
        super.buildRightSection();

        // setup option section
        Section childSection = FormSectionUtils.createStaticSection(toolkit,right,"Child Association Configurations","View or Update Child Association Configurations.");

        final Composite  childSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, childSection);

        // build basic information section
        Composite target = toolkit.createComposite(childSectionContainer);        
        target.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        target.setLayout(new GridLayout(2, false));

        Label label = new Label(target, SWT.NULL);
        label.setText("&Allow Duplicate Child Name:");

        allowDuplicateChildNameButton = new Button (target, SWT.CHECK);
        allowDuplicateChildNameButton.setText ("Check if allow duplicate child name");
        allowDuplicateChildNameButton.setSelection (((M2ChildAssociation)association).allowDuplicateChildName());

        label = new Label(target, SWT.NULL);
        label.setText("&Propagate Timestamps:");

        propagateTimestampsButton = new Button (target, SWT.CHECK);
        propagateTimestampsButton.setText ("Check if mandatory enforced");
        propagateTimestampsButton.setSelection (((M2ChildAssociation)association).isPropagateTimestamps());

        label = new Label(target, SWT.NULL);
        label.setText("&Required Child Name");

        String requiredChildName = ((M2ChildAssociation)association).getRequiredChildName();
        requiredChildNameText = new Text(target, SWT.BORDER | SWT.SINGLE);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        requiredChildNameText.setLayoutData(gridData);
        if (requiredChildName != null)
        {
            requiredChildNameText.setText(requiredChildName);
        }
        childSection.setClient(childSectionContainer); 
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.ClassAssociationEditDialog#prepareUpdatedAssociation()
     */
    protected void prepareUpdatedAssociation()
    {
        super.prepareUpdatedAssociation();
        ((M2ChildAssociation)updatedAssociation).setAllowDuplicateChildName(allowDuplicateChildNameButton.getSelection());
        ((M2ChildAssociation)updatedAssociation).setPropagateTimestamps(propagateTimestampsButton.getSelection());
        if (requiredChildNameText.getText() != null)
        {
            ((M2ChildAssociation)updatedAssociation).setRequiredChildName(requiredChildNameText.getText());
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.ClassAssociationEditDialog#open()
     */
    public M2ClassAssociation open () 
    {
        initUI("Child Association Editor");
        buildHeadSection();
        buildLeftSection();
        buildRightSection();
        buildWebclientSection (right);
        buildBottomSection();
        addProcessingListeners();        
        openUI();
        return updatedAssociation;
    }    
}
