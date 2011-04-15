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

import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2Association;
import org.alfresco.repo.dictionary.M2ChildAssociation;
import org.alfresco.repo.dictionary.M2Class;
import org.alfresco.repo.dictionary.M2ClassAssociation;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Type;
import org.alfresco.repo.dictionary.ModelUtils;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.config.WebConfigRuntime;
import org.alfresco.web.config.PropertySheetConfigElement.ItemConfig;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
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
 * Class Association Editor Dialog
 * 
 * @author drq
 *
 */
public abstract class ClassAssociationEditDialog extends AbstractModelDialog
{
    // Data members
    protected M2ClassAssociation association;
    protected M2ClassAssociation updatedAssociation;
    protected M2Class clazz;

    // UI members
    protected Text nameText;
    protected Text titleText;
    protected Text descriptionText;
    protected Button protectedButton;
    protected CCombo targetClassNameCombo;
    protected Button sourceMandatoryButton;
    protected Button sourceManyButton;
    protected Text sourceRoleNameText;
    protected Button targetMandatoryButton;
    protected Button targetMandatoryEnforcedButton;
    protected Button targetManyButton;
    protected Text targetRoleNameText;

    protected Button enableConfigButton;
    protected Text displayLabelText;
    protected Text displayLabelIdText;
    protected Text converterText;
    protected Button readOnlyButton;
    protected Button viewModeButton;
    protected Button editModeButton;
    protected Text generatorText;

    /**
     * Constructor with parameters shell and Association
     * 
     * @param parent
     * @param association
     * @param service
     * @param model
     */
    public ClassAssociationEditDialog(Shell parent, M2ClassAssociation association, DictionaryService service,M2Model model, WebConfigRuntime webConfigRuntime,M2Class clazz, IDocument webclientConfigIDocument)
    {
        this(parent,SWT.PRIMARY_MODAL,association,service,model,webConfigRuntime,clazz,webclientConfigIDocument);
    }

    /**
     * Constructor with parameters shell, style and Association
     * 
     * @param parent
     * @param style
     * @param association
     * @param service
     * @param model
     */
    public ClassAssociationEditDialog(Shell parent, int style, M2ClassAssociation association, DictionaryService service,M2Model model, WebConfigRuntime webConfigRuntime, M2Class clazz, IDocument webclientConfigIDocument)
    {
        super(parent, style,service,model,webConfigRuntime,webclientConfigIDocument);
        this.association = association;
        this.updatedAssociation = ModelUtils.newAssociation(association.getName());
        this.clazz = clazz;
        ModelUtils.copyClassAssociation(this.updatedAssociation, association);
    }

    /**
     * @return the webscriptResource
     */
    public M2ClassAssociation getAssociation()
    {
        return association;
    }

    /**
     * @param webscriptResource the webscriptResource to set
     */
    public void setAssociation(M2ClassAssociation association)
    {
        this.association = association;
    }

    /**
     * @param itemConfig
     * @param parentElem
     */
    protected void buildWebclientSection (Composite parentElem)
    {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        ItemConfig itemConfig = null;

        if (association instanceof M2Association)
        {
            itemConfig = this.getWebConfigRuntime().findAssociationPropertySheet(clazz.getName(), association.getName());
        }
        else if (association instanceof M2ChildAssociation)
        {
            itemConfig = this.getWebConfigRuntime().findChildAssociationPropertySheet(clazz.getName(), association.getName());
        }

        // setup webclient configuration section
        Section webclientSection = FormSectionUtils.createStaticSection(toolkit,parentElem,"Web Client Configurations","View or Update Web Client Configurations.");

        final Composite  webclientSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, webclientSection);

        // build basic information section
        Composite webclientConfigs = toolkit.createComposite(webclientSectionContainer);        
        webclientConfigs.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        webclientConfigs.setLayout(new GridLayout(2, false));

        Label label = new Label(webclientConfigs, SWT.NULL);
        label.setText("&Enable Configuration:");

        enableConfigButton = new Button (webclientConfigs, SWT.CHECK);
        enableConfigButton.setText ("Check if enabled for web client");

        label = new Label(webclientConfigs, SWT.NULL);
        label.setText("&Display Label:");

        displayLabelText = new Text(webclientConfigs, SWT.BORDER | SWT.SINGLE);
        displayLabelText.setLayoutData(gridData);

        label = new Label(webclientConfigs, SWT.NULL);
        label.setText("&Display Label ID:");

        displayLabelIdText = new Text(webclientConfigs, SWT.BORDER | SWT.SINGLE);
        displayLabelIdText.setLayoutData(gridData);

        label = new Label(webclientConfigs, SWT.NULL);
        label.setText("&Converter:");

        converterText = new Text(webclientConfigs, SWT.BORDER | SWT.SINGLE);
        converterText.setLayoutData(gridData);

        label = new Label(webclientConfigs, SWT.NULL);
        label.setText("&Read Only:");

        readOnlyButton = new Button (webclientConfigs, SWT.CHECK);
        readOnlyButton.setText ("Check if read only");

        label = new Label(webclientConfigs, SWT.NULL);
        label.setText("&View Mode:");

        viewModeButton = new Button (webclientConfigs, SWT.CHECK);
        viewModeButton.setText ("Check if shown in view mode");

        label = new Label(webclientConfigs, SWT.NULL);
        label.setText("&Edit Mode:");

        editModeButton = new Button (webclientConfigs, SWT.CHECK);
        editModeButton.setText ("Check if shown in edit mode");

        label = new Label(webclientConfigs, SWT.NULL);
        label.setText("&Component Generator:");

        generatorText = new Text(webclientConfigs, SWT.BORDER | SWT.SINGLE);
        generatorText.setLayoutData(gridData);

        if (itemConfig != null)
        {
            enableConfigButton.setSelection(true);
            if (itemConfig.getDisplayLabel() != null)
            {
                displayLabelText.setText(itemConfig.getDisplayLabel());
            }
            if (itemConfig.getDisplayLabelId() != null)
            {
                displayLabelIdText.setText(itemConfig.getDisplayLabelId());
            }
            if (itemConfig.getConverter() != null)
            {
                converterText.setText(itemConfig.getConverter());
            }
            readOnlyButton.setSelection(itemConfig.isReadOnly());
            editModeButton.setSelection(itemConfig.isShownInEditMode());
            viewModeButton.setSelection(itemConfig.isShownInViewMode());
            if (itemConfig.getComponentGenerator() != null)
            {
                generatorText.setText(itemConfig.getComponentGenerator());
            }
        }
        else
        {
            enableConfigButton.setSelection(false);
            displayLabelText.setEnabled(false);
            displayLabelIdText.setEnabled(false);
            converterText.setEnabled(false);
            readOnlyButton.setEnabled(false);
            editModeButton.setEnabled(false);
            viewModeButton.setEnabled(false);
            generatorText.setEnabled(false);

            if (getWebConfigRuntime() == null)
            {
                enableConfigButton.setEnabled(false);
            }
        }
        webclientSection.setClient(webclientSectionContainer);       
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.dialog.AbstractModelDialog#buildLeftSection()
     */
    protected void buildLeftSection()
    {
        super.buildLeftSection();

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        Section basicInfoSection = FormSectionUtils.createStaticSection(toolkit,left,"General Information","Association General Information");        
        Composite  basicInfoSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, basicInfoSection);

        // build basic information section
        Composite basicInfo = toolkit.createComposite(basicInfoSectionContainer);        
        basicInfo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        basicInfo.setLayout(new GridLayout(2, false));

        // build short name text field
        Label label = new Label(basicInfo, SWT.NULL);
        label.setText("&Name:");

        String name = association.getName();
        nameText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        nameText.setLayoutData(gridData);
        nameText.setText(name);

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Title:");

        String title = association.getTitle();
        titleText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        titleText.setLayoutData(gridData);
        if (title != null)
        {
            titleText.setText(title);
        }

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Description:");
        gridData = new GridData();
        gridData.verticalAlignment = SWT.TOP;
        label.setLayoutData(gridData);

        String description = association.getDescription();
        descriptionText = new Text(basicInfo, SWT.BORDER | SWT.WRAP | SWT.MULTI);
        GC gc = new GC(descriptionText);
        FontMetrics fm = gc.getFontMetrics ();
        int height = 5 * fm.getHeight();
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = SWT.FILL;
        gridData.grabExcessVerticalSpace = true;
        gridData.heightHint = height;
        descriptionText.setLayoutData(gridData);
        if (description!=null)
        {
            descriptionText.setText(description);
        }
        gc.dispose();

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Protected:");

        protectedButton = new Button (basicInfo, SWT.CHECK);
        protectedButton.setText ("Check if protected");
        protectedButton.setSelection (association.isProtected());

        // build parent name field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Target Class:");

        targetClassNameCombo = new CCombo(basicInfo, SWT.READ_ONLY | SWT.FLAT | SWT.BORDER);
        targetClassNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        for (QName m2t : getService().getAllTypes())
        {
            targetClassNameCombo.add(m2t.getPrefixString());
        }

        if (association.getTargetClassName() != null)
        {
            targetClassNameCombo.setText(association.getTargetClassName());
        }

        basicInfoSection.setClient(basicInfoSectionContainer);

        // setup option section
        Section sourceSection = FormSectionUtils.createStaticSection(toolkit,left,"Source Configurations","View or Update Source Configurations.");

        final Composite  sourceSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, sourceSection);

        // build basic information section
        Composite source = toolkit.createComposite(sourceSectionContainer);        
        source.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        source.setLayout(new GridLayout(2, false));

        label = new Label(source, SWT.NULL);
        label.setText("&Mandatory:");

        sourceMandatoryButton = new Button (source, SWT.CHECK);
        sourceMandatoryButton.setText ("Check if mandatory");
        sourceMandatoryButton.setSelection (association.isSourceMandatory());

        label = new Label(source, SWT.NULL);
        label.setText("&Many:");

        sourceManyButton = new Button (source, SWT.CHECK);
        sourceManyButton.setText ("Check if many");
        sourceManyButton.setSelection (association.isSourceMandatory());

        label = new Label(source, SWT.NULL);
        label.setText("&Role Name");

        String sourceRoleName = association.getSourceRoleName();
        sourceRoleNameText = new Text(source, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        sourceRoleNameText.setLayoutData(gridData);
        if (sourceRoleName != null)
        {
            sourceRoleNameText.setText(sourceRoleName);
        }
        sourceSection.setClient(sourceSectionContainer);

    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.dialog.AbstractModelDialog#buildRightSection()
     */
    protected void buildRightSection()
    {
        super.buildRightSection();

        // setup option section
        Section targetSection = FormSectionUtils.createStaticSection(toolkit,right,"Target Configurations","View or Update Target Configurations.");

        final Composite  targetSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, targetSection);

        // build basic information section
        Composite target = toolkit.createComposite(targetSectionContainer);        
        target.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        target.setLayout(new GridLayout(2, false));

        Label label = new Label(target, SWT.NULL);
        label.setText("&Mandatory:");

        targetMandatoryButton = new Button (target, SWT.CHECK);
        targetMandatoryButton.setText ("Check if mandatory");
        targetMandatoryButton.setSelection (association.isTargetMandatory());

        label = new Label(target, SWT.NULL);
        label.setText("&Mandatory Enforced:");

        targetMandatoryEnforcedButton = new Button (target, SWT.CHECK);
        targetMandatoryEnforcedButton.setText ("Check if mandatory enforced");
        targetMandatoryEnforcedButton.setSelection (association.isTargetMandatoryEnforced());

        label = new Label(target, SWT.NULL);
        label.setText("&Many:");

        targetManyButton = new Button (target, SWT.CHECK);
        targetManyButton.setText ("Check if many");
        targetManyButton.setSelection (association.isTargetMandatory());

        label = new Label(target, SWT.NULL);
        label.setText("&Role Name");

        String targetRoleName = association.getTargetRoleName();
        targetRoleNameText = new Text(target, SWT.BORDER | SWT.SINGLE);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        targetRoleNameText.setLayoutData(gridData);
        if (targetRoleName != null)
        {
            targetRoleNameText.setText(targetRoleName);
        }
        targetSection.setClient(targetSectionContainer);        
    }

    /**
     * 
     */
    protected void prepareUpdatedAssociation()
    {
        updatedAssociation.setName(nameText.getText());
        updatedAssociation.setTitle(titleText.getText());
        updatedAssociation.setDescription(descriptionText.getText());
        updatedAssociation.setProtected(protectedButton.getSelection());
        if (targetClassNameCombo.getText() != null)
        {
            updatedAssociation.setTargetClassName(targetClassNameCombo.getText());
        }
        //source options
        updatedAssociation.setSourceMany(sourceManyButton.getSelection());
        updatedAssociation.setSourceMandatory(sourceMandatoryButton.getSelection());
        if (sourceRoleNameText.getText() != null&& !sourceRoleNameText.getText().equals(""))
        {
            updatedAssociation.setSourceRoleName(sourceRoleNameText.getText());
        }
        //target options
        updatedAssociation.setTargetMany(targetManyButton.getSelection());
        updatedAssociation.setTargetMandatory(targetMandatoryButton.getSelection());
        updatedAssociation.setTargetMandatoryEnforced(targetMandatoryEnforcedButton.getSelection());
        if (targetRoleNameText.getText() != null&& !targetRoleNameText.getText().equals(""))
        {
            updatedAssociation.setTargetRoleName(targetRoleNameText.getText());
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.dialog.AbstractModelDialog#addProcessingListeners()
     */
    protected void addProcessingListeners()
    {
        enableConfigButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent event) 
            {
                if (enableConfigButton.getSelection())
                {
                    displayLabelText.setEnabled(true);
                    displayLabelIdText.setEnabled(true);
                    converterText.setEnabled(true);
                    readOnlyButton.setEnabled(true);
                    editModeButton.setEnabled(true);
                    viewModeButton.setEnabled(true);
                    generatorText.setEnabled(true); 
                }
                else
                {
                    displayLabelText.setEnabled(false);
                    displayLabelIdText.setEnabled(false);
                    converterText.setEnabled(false);
                    readOnlyButton.setEnabled(false);
                    editModeButton.setEnabled(false);
                    viewModeButton.setEnabled(false);
                    generatorText.setEnabled(false);
                }
            }
        });

        ok.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                prepareUpdatedAssociation();

                if (getWebConfigRuntime() != null)
                {
                    //web client configurations
                    HashMap<String,String> propertySheet = new HashMap<String,String>();

                    if ( clazz instanceof M2Type)
                    {
                        propertySheet.put("config","node-type");
                    } 
                    else if (clazz instanceof M2Aspect)
                    {
                        propertySheet.put("config","aspect-name");
                    }
                    if (association instanceof M2Association)
                    {
                        propertySheet.put("type","association");
                    }
                    if (association instanceof M2ChildAssociation)
                    {
                        propertySheet.put("type","child-association");
                    }
                    propertySheet.put("type-aspect-name",clazz.getName());
                    propertySheet.put("enable", ""+enableConfigButton.getSelection());
                    propertySheet.put("name",nameText.getText());
                    propertySheet.put("display-label",displayLabelText.getText());
                    propertySheet.put("display-label-id",displayLabelIdText.getText());
                    propertySheet.put("converter",converterText.getText());
                    propertySheet.put("read-only",""+readOnlyButton.getSelection());
                    propertySheet.put("show-in-view-mode",""+viewModeButton.getSelection());
                    propertySheet.put("show-in-edit-mode",""+editModeButton.getSelection());
                    propertySheet.put("component-generator",generatorText.getText());

                    boolean isPropertySheetDirty = getWebConfigRuntime().syncPropertySheet(propertySheet);
                    if ( isPropertySheetDirty)
                    {
                        getWebConfigRuntime().toXml();
                        getWebclientConfigIDocument().set(getWebConfigRuntime().getWebConfigXml());
                    }
                }
                shell.close();				
            }
        });

        cancel.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                updatedAssociation  = null;
                shell.close();
            }
        });                        
    }

    /**
     * @return
     */
    public M2ClassAssociation open () 
    {
        initUI("Association Editor");
        buildHeadSection();
        buildLeftSection();
        buildRightSection();
        buildBottomSection();
        addProcessingListeners();
        openUI();
        return updatedAssociation;
    }
}
