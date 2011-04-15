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

import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.dictionary.M2Class;
import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2Type;
import org.alfresco.repo.dictionary.M2Property;
import org.alfresco.repo.dictionary.M2Constraint;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.ModelUtils;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.config.WebConfigRuntime;
import org.alfresco.web.config.AdvancedSearchConfigElement.CustomProperty;
import org.alfresco.web.config.PropertySheetConfigElement.ItemConfig;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.springframework.extensions.surf.commons.ui.FormSectionUtils;
import org.springframework.extensions.surf.commons.ui.TableUtils;

/**
 * Schema Property Editor Dialog
 * 
 * @author drq
 *
 */
public class PropertyEditDialog extends AbstractModelDialog
{
    // Data members
    private M2Property property;
    private M2Property updatedProperty;
    private M2Class clazz;

    // UI members
    protected TableEditor comboEditor;
    protected Table constraintTable;
    protected Text nameText;
    protected Text titleText;
    protected Text descriptionText;
    protected CCombo propertyTypeCombo;
    protected Button overrideButton;
    protected Button protectedButton;
    protected Button mandatoryButton;
    protected Button mandatoryEnforcedButton;
    protected Button multiValuedButton;
    protected Text defaultValueText;
    protected Button indexedButton;
    protected Button indexedAtomicallyButton;
    protected Button storedInIndexButton;
    protected CCombo indexTokenisationCombo;

    protected Button enableConfigButton;
    protected Text displayLabelText;
    protected Text displayLabelIdText;
    protected Text converterText;
    protected Button readOnlyButton;
    protected Button viewModeButton;
    protected Button editModeButton;
    protected Button ignoreButton;
    protected Text generatorText;
    protected Button advancedSearchButton;
    protected Text advancedSearchLabelText;
    /**
     * Constructor with parameters shell and Property
     * 
     * @param parent
     * @param property
     * @param service
     * @param model
     */
    public PropertyEditDialog(Shell parent, M2Property property, DictionaryService service,M2Model model, WebConfigRuntime webConfigRuntime,M2Class clazz,IDocument webclientConfigIDocument)
    {
        this(parent,SWT.PRIMARY_MODAL,property,service,model,webConfigRuntime,clazz,webclientConfigIDocument);
    }

    /**
     * Constructor with parameters shell, style and Property
     * 
     * @param parent
     * @param style
     * @param property
     * @param service
     * @param model
     */
    public PropertyEditDialog(Shell parent, int style, M2Property property,DictionaryService service,M2Model model, WebConfigRuntime webConfigRuntime,M2Class clazz,IDocument webclientConfigIDocument)
    {
        super(parent, style,service,model,webConfigRuntime,webclientConfigIDocument);
        this.property = property;
        this.updatedProperty = ModelUtils.newProperty(property.getName());
        this.clazz = clazz;
        ModelUtils.copyProperty(updatedProperty, property);
    }

    /**
     * @return the webscriptResource
     */
    public M2Property getProperty()
    {
        return property;
    }

    /**
     * @param webscriptResource the webscriptResource to set
     */
    public void setProperty(M2Property property)
    {
        this.property = property;
    }

    /**
     * @return
     */
    public M2Property open () 
    {
        initUI("Property Editor");
        buildHeadSection();
        buildLeftSection();
        buildRightSection();
        buildBottomSection();
        addProcessingListeners() ;
        openUI(1200,800);		
        return updatedProperty;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.dialog.AbstractModelDialog#addProcessingListeners()
     */
    @Override
    void addProcessingListeners() 
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
                    ignoreButton.setEnabled(true);
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
                    ignoreButton.setEnabled(false);
                    generatorText.setEnabled(false);
                }
            }
        });

        ok.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                updatedProperty.setName(nameText.getText());
                updatedProperty.setTitle(titleText.getText());
                updatedProperty.setDescription(descriptionText.getText());
                updatedProperty.setType(propertyTypeCombo.getText());
                //options
                updatedProperty.setOverride(overrideButton.getSelection());
                updatedProperty.setProtected(protectedButton.getSelection());
                updatedProperty.setMandatory(mandatoryButton.getSelection());
                updatedProperty.setMandatoryEnforced(mandatoryEnforcedButton.getSelection());
                updatedProperty.setMultiValued(multiValuedButton.getSelection());
                if (defaultValueText.getText() != null)
                {
                    updatedProperty.setDefaultValue(defaultValueText.getText());
                }
                //indexed options
                updatedProperty.setIndexed(indexedButton.getSelection());
                updatedProperty.setIndexedAtomically(indexedAtomicallyButton.getSelection());
                updatedProperty.setStoredInIndex(storedInIndexButton.getSelection());
                if (indexTokenisationCombo.getText() != null && !indexTokenisationCombo.getText().equals(""))
                {
                    updatedProperty.setIndexTokenisationMode(IndexTokenisationMode.valueOf(indexTokenisationCombo.getText()));
                }
                //constrains
                for (TableItem constraintItem : constraintTable.getItems())
                {
                    if (updatedProperty.hasConstraints())
                    {
                        boolean foundConstraint = false;
                        for (M2Constraint constraint : property.getConstraints())
                        {
                            if (constraint.getRef().equals(constraintItem.getText()))
                            {
                                foundConstraint = true;
                            }
                        }
                        if (!foundConstraint)
                        {
                            updatedProperty.addConstraintRef(constraintItem.getText());
                        }
                    }
                    else
                    {
                        updatedProperty.addConstraintRef(constraintItem.getText());
                    }
                }

                //web client configurations
                if (getWebConfigRuntime() != null)
                {
                    HashMap<String,String> propertySheet = new HashMap<String,String>();
                    HashMap<String,String> properties = new HashMap<String,String>();

                    if ( clazz instanceof M2Type)
                    {
                        propertySheet.put("config","node-type");
                        properties.put("config", "type-property");
                        properties.put("type", clazz.getName());
                    } 
                    else if (clazz instanceof M2Aspect)
                    {
                        propertySheet.put("config","aspect-name");
                        properties.put("config", "aspect-property");
                        properties.put("aspect", clazz.getName());
                    }
                    propertySheet.put("type","property");
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
                    propertySheet.put("ignore-if-missing",""+ignoreButton.getSelection());

                    properties.put("property", nameText.getText());
                    properties.put("display-label-id", advancedSearchLabelText.getText());
                    properties.put("show", ""+advancedSearchButton.getSelection());

                    boolean isPropertySheetDirty = getWebConfigRuntime().syncPropertySheet(propertySheet);
                    boolean isAdvancedSearchDirty = getWebConfigRuntime().syncAdvancedSearchOptions(properties);
                    if ( isPropertySheetDirty || isAdvancedSearchDirty)
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
                updatedProperty  = null;
                shell.close();
            }
        });      
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.dialog.AbstractModelDialog#buildLeftSection()
     */
    @Override
    protected void buildLeftSection() {
        super.buildLeftSection();

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        Section basicInfoSection = FormSectionUtils.createStaticSection(toolkit,left,"General Information","Property General Information");        
        Composite  basicInfoSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, basicInfoSection);

        // build basic information section
        Composite basicInfo = toolkit.createComposite(basicInfoSectionContainer);        
        basicInfo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        basicInfo.setLayout(new GridLayout(2, false));

        // build short name text field
        Label label = new Label(basicInfo, SWT.NULL);
        label.setText("&Name:");

        String name = property.getName();
        nameText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        nameText.setLayoutData(gridData);
        nameText.setText(name);

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Title:");

        String title = property.getTitle();
        titleText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
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
        
        String description = property.getDescription();
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

        // build property field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Property Type:");

        propertyTypeCombo = new CCombo(basicInfo, SWT.READ_ONLY | SWT.FLAT | SWT.BORDER);
        propertyTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        for (QName m2t : getService().getAllDataTypes())
        {
            propertyTypeCombo.add(m2t.getPrefixString());
        }
        propertyTypeCombo.setText(property.getType());

        basicInfoSection.setClient(basicInfoSectionContainer);

        // setup option section
        Section optionSection = FormSectionUtils.createStaticSection(toolkit,left,"Advanced Options","View or Update Advanced Options.");

        final Composite  optionSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, optionSection);

        // build basic information section
        Composite options = toolkit.createComposite(optionSectionContainer);        
        options.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        options.setLayout(new GridLayout(2, false));

        // build short name text field
        label = new Label(options, SWT.NULL);
        label.setText("&Override:");

        overrideButton = new Button (options, SWT.CHECK);
        overrideButton.setText ("Check if overrided");
        overrideButton.setSelection (property.isOverride());

        label = new Label(options, SWT.NULL);
        label.setText("&Protected:");

        protectedButton = new Button (options, SWT.CHECK);
        protectedButton.setText ("Check if protected");
        protectedButton.setSelection (property.isProtected());

        label = new Label(options, SWT.NULL);
        label.setText("&Mandatory:");

        mandatoryButton = new Button (options, SWT.CHECK);
        mandatoryButton.setText ("Check if mandatory");
        mandatoryButton.setSelection (property.isMandatory());

        label = new Label(options, SWT.NULL);
        label.setText("&Mandatory Enforced:");

        mandatoryEnforcedButton = new Button (options, SWT.CHECK);
        mandatoryEnforcedButton.setText ("Check if mandatory is enforced");
        mandatoryEnforcedButton.setSelection (property.isMandatoryEnforced());

        label = new Label(options, SWT.NULL);
        label.setText("&Multi Valued:");

        multiValuedButton = new Button (options, SWT.CHECK);
        multiValuedButton.setText ("Check if multi valued");
        multiValuedButton.setSelection (property.isMultiValued());

        label = new Label(options, SWT.NULL);
        label.setText("&Default Value");

        String defaultValue = property.getDefaultValue();
        defaultValueText = new Text(options, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        defaultValueText.setLayoutData(gridData);
        if (defaultValue != null)
        {
            defaultValueText.setText(defaultValue);
        }
        optionSection.setClient(optionSectionContainer);
        
        // setup indexOption section
        Section indexOptionSection = FormSectionUtils.createStaticSection(toolkit,left,"Index Options","View or Update Advanced Options.");

        final Composite  indexOptionSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, indexOptionSection);

        // build basic information section
        Composite indexOptions = toolkit.createComposite(indexOptionSectionContainer);        
        indexOptions.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        indexOptions.setLayout(new GridLayout(2, false));

        // build short name text field
        label = new Label(indexOptions, SWT.NULL);
        label.setText("&Indexed:");

        indexedButton = new Button (indexOptions, SWT.CHECK);
        indexedButton.setText ("Check if indexed");
        if (property.isIndexed() != null)
        {
            indexedButton.setSelection (property.isIndexed());
        }

        label = new Label(indexOptions, SWT.NULL);
        label.setText("&Stored In Index:");

        storedInIndexButton = new Button (indexOptions, SWT.CHECK);
        storedInIndexButton.setText ("Check if stored in index");
        if ( property.isStoredInIndex() != null)
        {
            storedInIndexButton.setSelection (property.isStoredInIndex());
        }

        label = new Label(indexOptions, SWT.NULL);
        label.setText("&Indexed Atomically:");

        indexedAtomicallyButton = new Button (indexOptions, SWT.CHECK);
        indexedAtomicallyButton.setText ("Check if Indexed Atomically");
        if (property.isIndexedAtomically() != null)
        {
            indexedAtomicallyButton.setSelection (property.isMandatory());
        }

        label = new Label(indexOptions, SWT.NULL);
        label.setText("&Index Tokenisation Mode:");

        indexTokenisationCombo = new CCombo(indexOptions, SWT.READ_ONLY | SWT.FLAT | SWT.BORDER);
        indexTokenisationCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        for (IndexTokenisationMode itm : IndexTokenisationMode.values())
        {
            indexTokenisationCombo.add(itm.toString());
        }

        if (property.getIndexTokenisationMode() != null)
        {
            indexTokenisationCombo.setText(property.getIndexTokenisationMode().toString());
        }

        indexOptionSection.setClient(indexOptionSectionContainer);
        
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.dialog.AbstractModelDialog#buildRightSection()
     */
    @Override
    protected void buildRightSection() 
    {
        super.buildRightSection();

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;


        // setup constrain section
        Section constrainSection = FormSectionUtils.createStaticSection(toolkit,right,"Constrains","View or Update Property Constrains.");

        final Composite  constrainSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, constrainSection);

        MouseListener addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                if (getModel().getConstraints() != null && getModel().getConstraints().size() > 0)
                {
                    TableItem item = new TableItem (constraintTable, SWT.NONE);
                    item.setText(0, getModel().getConstraints().get(0).getName());
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };

        MouseListener deleteMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableUtils.deleteCheckedItems(constraintTable);
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };

        MouseListener doubleClickListener = new MouseAdapter () 
        {
            public void mouseDown(MouseEvent e) 
            {
                Point pt = new Point (e.x, e.y);
                final TableItem newItem = constraintTable.getItem (pt);
                if (newItem == null) 
                {
                    if (comboEditor != null)
                    {
                        comboEditor.getEditor().dispose();
                    }
                    return;
                }
                constraintTable.showSelection ();
                // make sure the double click case?
                if (comboEditor != null)
                {
                    comboEditor.getEditor().dispose();
                }

                comboEditor= new TableEditor (constraintTable);

                CCombo combo = new CCombo(constraintTable, SWT.READ_ONLY | SWT.FLAT | SWT.BORDER);
                combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                for (M2Constraint m2c : getModel().getConstraints())
                {
                    combo.add(m2c.getName());
                }
                combo.setText(newItem.getText(0));

                comboEditor.grabHorizontal = true;
                comboEditor.setEditor(combo, newItem, 0);
                GridData gridData = new GridData();
                gridData.horizontalAlignment = SWT.FILL;
                gridData.grabExcessHorizontalSpace = true;
                combo.setLayoutData(gridData);

                combo.addModifyListener
                (
                        new ModifyListener() 
                        {
                            public void modifyText(ModifyEvent e) 
                            {
                            }
                        }
                );
                combo.addFocusListener
                (
                        new FocusListener()
                        {
                            public void focusGained(FocusEvent arg0)
                            {
                            }
                            public void focusLost(FocusEvent arg0)
                            {
                                if (comboEditor != null)
                                {
                                    if (comboEditor.getEditor() != null)
                                        comboEditor.getEditor().dispose();
                                }
                                return;                                
                            }
                        }
                );
                combo.setFocus();
            }
        };

        String [] columnTitles = {"Constrain Name"};
        int [] columnWidths = {2};
        constraintTable = TableUtils.createTable(constrainSectionContainer, "Constrains", true, "Add Constraint", addMouseListener, "Delete Constrain(s)", deleteMouseListener,
                doubleClickListener, null, columnTitles, columnWidths, 150, false,0);

        // populate table items
        if (property.getConstraints() != null)
        {
            for (M2Constraint m2c : property.getConstraints()) 
            {
                TableItem item = new TableItem (constraintTable, SWT.NONE);
                item.setText(0, m2c.getRef());
            }
        }
        constrainSection.setClient(constrainSectionContainer);
        

        // setup webclient configuration section
        Section webclientSection = FormSectionUtils.createStaticSection(toolkit,right,"Web Client Configurations","View or Update Web Client Configurations.");

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
        label.setText("&Ignore:");

        ignoreButton = new Button (webclientConfigs, SWT.CHECK);
        ignoreButton.setText ("Ignore if missing");

        label = new Label(webclientConfigs, SWT.NULL);
        label.setText("&Component Generator:");

        generatorText = new Text(webclientConfigs, SWT.BORDER | SWT.SINGLE);
        generatorText.setLayoutData(gridData);

        label = new Label(webclientConfigs, SWT.NULL);
        label.setText("&Advanced Search:");

        advancedSearchButton = new Button (webclientConfigs, SWT.CHECK);
        advancedSearchButton.setText ("Check if available for advanced search");

        label = new Label(webclientConfigs, SWT.NULL);
        label.setText("&Search Label Id:");

        advancedSearchLabelText = new Text(webclientConfigs, SWT.BORDER | SWT.SINGLE);
        advancedSearchLabelText.setLayoutData(gridData);

        if (getWebConfigRuntime() != null)
        {
            ItemConfig itemConfig = getWebConfigRuntime().findTypeOrAspectPropertySheet (clazz.getName(), property.getName());

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
                ignoreButton.setSelection(itemConfig.getIgnoreIfMissing());
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
                ignoreButton.setEnabled(false);
                generatorText.setEnabled(false);                
            }
        }
        else
        {
            enableConfigButton.setEnabled(false);
            displayLabelText.setEnabled(false);
            displayLabelIdText.setEnabled(false);
            converterText.setEnabled(false);
            readOnlyButton.setEnabled(false);
            editModeButton.setEnabled(false);
            viewModeButton.setEnabled(false);
            ignoreButton.setEnabled(false);
            generatorText.setEnabled(false);
        }

        if ( getWebConfigRuntime() != null)
        {
            CustomProperty customProperty = getWebConfigRuntime().findPropertyAdvancedSearchOption(clazz.getName(), property.getName());
            if (customProperty != null)
            {           
                advancedSearchButton.setSelection(true);
                if (customProperty.LabelId != null)
                {
                    advancedSearchLabelText.setText(customProperty.LabelId);
                }
            }
        }
        else
        {
            advancedSearchButton.setEnabled(false);
            advancedSearchLabelText.setEnabled(false);
        }

        webclientSection.setClient(webclientSectionContainer);
    }
}
