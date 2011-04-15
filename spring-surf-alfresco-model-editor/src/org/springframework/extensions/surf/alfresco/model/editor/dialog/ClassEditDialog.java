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

import java.util.ArrayList;
import java.util.HashMap;

import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2Association;
import org.alfresco.repo.dictionary.M2ChildAssociation;
import org.alfresco.repo.dictionary.M2Class;
import org.alfresco.repo.dictionary.M2ClassAssociation;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Property;
import org.alfresco.repo.dictionary.M2PropertyOverride;
import org.alfresco.repo.dictionary.M2Type;
import org.alfresco.repo.dictionary.ModelUtils;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.config.WebConfigRuntime;
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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.springframework.extensions.surf.commons.ui.FormSectionUtils;
import org.springframework.extensions.surf.commons.ui.TableUtils;

/**
 * Model Class Editor Dialog
 * 
 * @author drq
 *
 */
public abstract class ClassEditDialog extends AbstractModelDialog
{

    protected M2Class clazz;
    protected M2Class updatedClass;

    private Table propertyTable;
    private Table associationTable;
    private Table childAssociationTable;
    private Table mandatoryAspectstTable;
    private TableEditor mandatoryAspectComboEditor;
    private Table propertyOverrideTable;
    protected CCombo parentNameCombo;
    protected Text nameText;
    protected Text titleText;
    protected Text descriptionText;

    protected Button advancedSearchButton;
    protected Button contentWizardsButton;
    protected Button actionWizardsButton;

    /**
     * Constructor with parameters shell, style and Class
     * 
     * @param parent
     * @param style
     * @param clazz
     * @param service
     * @param model
     */
    public ClassEditDialog(Shell parent, int style, M2Class clazz, DictionaryService service, M2Model model, WebConfigRuntime webConfigRuntime,IDocument webclientConfigIDocument)
    {
        super(parent, style,service,model,webConfigRuntime,webclientConfigIDocument);
        this.clazz = clazz;
    }

    /**
     * Constructor with parameters shell and Class
     * 
     * @param parent
     * @param clazz
     * @param service
     * @param model
     */
    public ClassEditDialog(Shell parent, M2Class clazz, DictionaryService service, M2Model model, WebConfigRuntime webConfigRuntime,IDocument webclientConfigIDocument)
    {
        this(parent, SWT.PRIMARY_MODAL, clazz, service, model,webConfigRuntime,webclientConfigIDocument);
    }

    /**
     * @return the webscriptResource
     */
    public M2Class getClazz()
    {
        return clazz;
    }

    /**
     * @param webscriptResource the webscriptResource to set
     */
    public void setClazz(M2Class clazz)
    {
        this.clazz = clazz;
    }

    abstract M2Class open();

    abstract void buildWebclientSection (Composite parentElem);

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.dialog.AbstractModelDialog#buildLeftSection()
     */
    protected void buildLeftSection ()
    {
        super.buildLeftSection();

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        Section basicInfoSection = FormSectionUtils.createStaticSection(toolkit,left,"General Information","Class General Information");        
        Composite  basicInfoSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, basicInfoSection);

        // build basic information section
        Composite basicInfo = toolkit.createComposite(basicInfoSectionContainer);        
        basicInfo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        basicInfo.setLayout(new GridLayout(2, false));

        // build short name text field
        Label label = new Label(basicInfo, SWT.NULL);
        label.setText("&Name:");

        String name = clazz.getName();
        nameText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        nameText.setLayoutData(gridData);
        nameText.setText(name);

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Title:");

        String title = clazz.getTitle();
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

        String description = clazz.getDescription();
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

        // build parent name field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Parent Name:");

        parentNameCombo = new CCombo(basicInfo, SWT.READ_ONLY | SWT.FLAT | SWT.BORDER);
        parentNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));


        for (QName m2t : this.getService().getAllTypes())
        {
            parentNameCombo.add(m2t.getPrefixString());
        }

        parentNameCombo.setText(clazz.getParentName());


        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Archive:");

        final Button archiveButton = new Button (basicInfo, SWT.CHECK);
        archiveButton.setText ("Check if archive");
        if (clazz.getArchive() != null)
        {
            archiveButton.setSelection (clazz.getArchive());
        }

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Super Type Query:");

        final Button superClassQuery = new Button (basicInfo, SWT.CHECK);
        superClassQuery.setText ("Check if included in super type query");
        if (clazz.getIncludedInSuperTypeQuery() != null)
        {
            superClassQuery.setSelection (clazz.getIncludedInSuperTypeQuery());
        }

        basicInfoSection.setClient(basicInfoSectionContainer);

        // setup namespace section
        Section propertySection = FormSectionUtils.createStaticSection(toolkit,left,"Properties","View or Update Properties.");

        final Composite  propertySectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, propertySection);
        MouseListener addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem item = new TableItem (propertyTable, SWT.NONE);
                String prefix = ModelUtils.getDefaultNameSpace(getModel());
                int nextId = TableUtils.getNextId(propertyTable, prefix+"property");
                String newPropertyName = prefix+"property"+nextId;
                M2Property newProperty = updatedClass.createProperty(newPropertyName);
                newProperty.setType("d:text");
                item.setText(0, newProperty.getName());
                item.setText(1, newProperty.getType());
                
                M2Property property = updatedClass.getProperty(item.getText(0));
                PropertyEditDialog propertySelector = new PropertyEditDialog(propertyTable.getShell(),
                        property,getService(),getModel(),getWebConfigRuntime(),clazz,getWebclientConfigIDocument());
                M2Property result = propertySelector.open();
                if (result != null && ModelUtils.compareProperty(property, result))
                {
                    // Property has been changed
                    ModelUtils.copyProperty(property, result);
                    item.setText(0,result.getName());
                    item.setText(1,result.getType());
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
                TableItem[] items = propertyTable.getItems();
                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        updatedClass.removeProperty(propertyTable.getItem(i).getText(1));
                    }
                }
                TableUtils.deleteCheckedItems(propertyTable);				
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };

        Listener clickListener = new Listener() 
        {
            public void handleEvent(Event event) 
            {
                Point pt = new Point(event.x, event.y);
                TableItem item = propertyTable.getItem(pt);
                if (item == null)
                {
                    return;
                }
                for (int i = 0; i < 2; i++) 
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        M2Property property = updatedClass.getProperty(item.getText(0));
                        PropertyEditDialog propertySelector = new PropertyEditDialog(propertyTable.getShell(),
                                property,getService(),getModel(),getWebConfigRuntime(),clazz,getWebclientConfigIDocument());
                        M2Property result = propertySelector.open();
                        if (result != null && ModelUtils.compareProperty(property, result))
                        {
                            // Property has been changed
                            ModelUtils.copyProperty(property, result);
                            item.setText(0,result.getName());
                            item.setText(1,result.getType());
                        }
                    }
                }
            }
        };

        String [] columnTitles = {"Name","Class"};
        int []columnWidths = {3};

        propertyTable = TableUtils.createTable(propertySectionContainer, "Properties", true, "Add Property", addMouseListener, "Deleted Property(ies)", deleteMouseListener,
                null, clickListener, columnTitles, columnWidths, 150, false, 0);

        // populate table items
        if (clazz.getProperties() != null)
        {
            for (M2Property m2ns : clazz.getProperties()) 
            {
                TableItem item = new TableItem (propertyTable, SWT.NONE);
                // setup table editor for prefix field
                item.setText(0, m2ns.getName());
                // setup table editor for uri field
                item.setText(1, m2ns.getType());
            }
        }

        propertySection.setClient(propertySectionContainer);

        // setup propertyOverride section
        Section propertyOverrideSection = FormSectionUtils.createStaticSection(toolkit,left,"Property Overrides","View or Update Property Overrides.");

        final Composite  propertyOverrideSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, propertyOverrideSection);

        addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem item = new TableItem (propertyOverrideTable, SWT.NONE);
                String prefix = ModelUtils.getDefaultNameSpace(getModel());
                int nextId = TableUtils.getNextId(propertyOverrideTable, prefix+"propertyOverride");
                String newPropertyOverrideName = prefix+"propertyOverride"+nextId;
                M2PropertyOverride newPropertyOverride = updatedClass.createPropertyOverride(newPropertyOverrideName);                
                item.setText(0, newPropertyOverride.getName());
                
                M2PropertyOverride propertyOverride = updatedClass.getPropertyOverride(item.getText(0));
                PropertyOverrideEditDialog propertyOverrideSelector = new PropertyOverrideEditDialog(propertyOverrideTable.getShell(),
                        propertyOverride,getService(),getModel(), getWebConfigRuntime(),getWebclientConfigIDocument());
                M2PropertyOverride result = (M2PropertyOverride) propertyOverrideSelector.open();
                if (result != null && ModelUtils.comparePropertyOverride(propertyOverride, result))
                {
                    // PropertyOverride has been changed
                    ModelUtils.copyPropertyOverride(propertyOverride, result);
                    item.setText(0,result.getName());
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };

        deleteMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem[] items = propertyOverrideTable.getItems();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        updatedClass.removePropertyOverride(propertyOverrideTable.getItem(i).getText(0));
                    }
                }

                TableUtils.deleteCheckedItems(propertyOverrideTable);
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };

        clickListener = new Listener() {
            public void handleEvent(Event event) 
            {
                Point pt = new Point(event.x, event.y);
                TableItem item = propertyOverrideTable.getItem(pt);
                if (item == null)
                {
                    return;
                }
                for (int i = 0; i < 1; i++) 
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        M2PropertyOverride propertyOverride = updatedClass.getPropertyOverride(item.getText(0));
                        PropertyOverrideEditDialog propertyOverrideSelector = new PropertyOverrideEditDialog(propertyOverrideTable.getShell(),
                                propertyOverride,getService(),getModel(), getWebConfigRuntime(),getWebclientConfigIDocument());
                        M2PropertyOverride result = (M2PropertyOverride) propertyOverrideSelector.open();
                        if (result != null && ModelUtils.comparePropertyOverride(propertyOverride, result))
                        {
                            // PropertyOverride has been changed
                            ModelUtils.copyPropertyOverride(propertyOverride, result);
                            item.setText(0,result.getName());
                        }
                    }
                }
            }
        };

        String [] columnTitles2 = {"Name"};
        int []columnWidths2 = {2};

        propertyOverrideTable = TableUtils.createTable(propertyOverrideSectionContainer, "Property Overrides", true, "Add Property Override", addMouseListener, "Delete Property Override(s", deleteMouseListener, 
                null, clickListener, columnTitles2, columnWidths2, 150, false,0);

        // populate table items
        if (clazz.getPropertyOverrides() != null)
        {
            for (M2PropertyOverride m2ns : clazz.getPropertyOverrides()) 
            {
                TableItem item = new TableItem (propertyOverrideTable, SWT.NONE);
                item.setText(0, m2ns.getName());
            }
        }
        propertyOverrideTable.getColumn(0).pack ();

        propertyOverrideSection.setClient(propertyOverrideSectionContainer);        
    }

    protected void buildRightSection ()
    {
        super.buildRightSection();

        // setup association section
        Section associationSection = FormSectionUtils.createStaticSection(toolkit,right,"Associations","View or Update Peer Association and Child Associations.");
        final Composite  associationSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, associationSection);

        Composite forms = toolkit.createComposite(associationSectionContainer);        
        forms.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        forms.setLayout(new GridLayout(1, false));

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        TabFolder tabFolder = new TabFolder (forms, SWT.BORDER);
        tabFolder.setLayoutData(gridData);

        TabItem peerAssociationTab = new TabItem (tabFolder, SWT.NONE);        
        peerAssociationTab.setText ("Peer Associations"); 
        Composite peerAssociationTabContainer = FormSectionUtils.createStaticSectionClient(toolkit, tabFolder);

        MouseListener addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem item = new TableItem (associationTable, SWT.NONE);
                String prefix = ModelUtils.getDefaultNameSpace(getModel());
                int nextId = TableUtils.getNextId(associationTable, prefix+"association");
                String newAssociationName = prefix+"association"+nextId;
                M2Association newAssociation = updatedClass.createAssociation(newAssociationName);                
                item.setText(0, newAssociation.getName());
                
                M2Association association = (M2Association) updatedClass.getAssociation(item.getText(0));
                AssociationEditDialog associationSelector = new AssociationEditDialog(associationTable.getShell(),
                        association,getService(),getModel(),getWebConfigRuntime(), clazz,getWebclientConfigIDocument());
                M2Association result = (M2Association)associationSelector.open();
                if (result != null && ModelUtils.compareAssociation(association, result))
                {
                    // Association has been changed
                    ModelUtils.copyAssociation(association, result);
                    item.setText(0,result.getName());
                    item.setText(1,result.getTargetClassName());
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
                TableItem[] items = associationTable.getItems();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        updatedClass.removeAssociation(associationTable.getItem(i).getText(0));
                    }
                }

                TableUtils.deleteCheckedItems(associationTable);
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };
        Listener clickListener = new Listener() {
            public void handleEvent(Event event) 
            {
                Point pt = new Point(event.x, event.y);
                TableItem item = associationTable.getItem(pt);
                if (item == null)
                {
                    return;
                }
                for (int i = 0; i < 2; i++) 
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        M2Association association = (M2Association) updatedClass.getAssociation(item.getText(0));
                        AssociationEditDialog associationSelector = new AssociationEditDialog(associationTable.getShell(),
                                association,getService(),getModel(),getWebConfigRuntime(), clazz,getWebclientConfigIDocument());
                        M2Association result = (M2Association)associationSelector.open();
                        if (result != null && ModelUtils.compareAssociation(association, result))
                        {
                            // Association has been changed
                            ModelUtils.copyAssociation(association, result);
                            item.setText(0,result.getName());
                            item.setText(1,result.getTargetClassName());
                        }
                    }
                }
            }
        };	
        String[] columnTitles = {"Name", "Target Class"};
        int[] columnWidths = {3};

        associationTable = TableUtils.createTable(peerAssociationTabContainer, "Associations", true, "Add Association", addMouseListener, "Delete Associations", deleteMouseListener,
                null, clickListener, columnTitles, columnWidths, 150, false, 0);

        // populate table items
        if (clazz.getAssociations() != null)
        {
            for (M2ClassAssociation m2ns : clazz.getAssociations()) 
            {
                if (!m2ns.isChild())
                {
                    TableItem item = new TableItem (associationTable, SWT.NONE);
                    // setup table editor for prefix field
                    item.setText(0, m2ns.getName());
                    // setup table editor for uri field
                    if (m2ns.getTargetClassName()!= null)
                    {
                        item.setText(1, m2ns.getTargetClassName());
                    }
                }
            }
        }

        peerAssociationTab.setControl(peerAssociationTabContainer);

        TabItem childAssociationTab = new TabItem (tabFolder, SWT.NONE);        
        childAssociationTab.setText ("Child Associations"); 
        Composite childAssociationTabContainer = FormSectionUtils.createStaticSectionClient(toolkit, tabFolder);

        addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem item = new TableItem (childAssociationTable, SWT.NONE);
                String prefix = ModelUtils.getDefaultNameSpace(getModel());
                int nextId = TableUtils.getNextId(childAssociationTable, prefix+"childAssociation");
                String newAssociationName = prefix+"childAssociation"+nextId;
                M2ChildAssociation newAssociation = updatedClass.createChildAssociation(newAssociationName);                
                item.setText(0, newAssociation.getName());
                
                for (M2ClassAssociation m2ns : updatedClass.getAssociations()) 
                {
                    if (m2ns.isChild() && m2ns.getName().equals(item.getText(0)))
                    {
                        M2ChildAssociation childAssociation = (M2ChildAssociation) m2ns;
                        ChildAssociationEditDialog childAssociationSelector = new ChildAssociationEditDialog(childAssociationTable.getShell(),
                                childAssociation,getService(),getModel(),getWebConfigRuntime(),clazz,getWebclientConfigIDocument());
                        M2ChildAssociation result = (M2ChildAssociation) childAssociationSelector.open();
                        if (result != null && ModelUtils.compareChildAssociation(childAssociation, result))
                        {
                            // Association has been changed
                            ModelUtils.copyChildAssociation(childAssociation, result);
                            item.setText(0,result.getName());
                            item.setText(1,result.getTargetClassName());
                        }
                    }
                }

            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };
        deleteMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem[] items = childAssociationTable.getItems();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        updatedClass.removeAssociation(childAssociationTable.getItem(i).getText(0));
                    }
                }
                TableUtils.deleteCheckedItems(childAssociationTable);
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };
        clickListener = new Listener() {
            public void handleEvent(Event event) 
            {
                Point pt = new Point(event.x, event.y);
                TableItem item = childAssociationTable.getItem(pt);
                if (item == null)
                {
                    return;
                }
                for (int i = 0; i < 2; i++) 
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        for (M2ClassAssociation m2ns : updatedClass.getAssociations()) 
                        {
                            if (m2ns.isChild() && m2ns.getName().equals(item.getText(0)))
                            {
                                M2ChildAssociation childAssociation = (M2ChildAssociation) m2ns;
                                ChildAssociationEditDialog childAssociationSelector = new ChildAssociationEditDialog(childAssociationTable.getShell(),
                                        childAssociation,getService(),getModel(),getWebConfigRuntime(),clazz,getWebclientConfigIDocument());
                                M2ChildAssociation result = (M2ChildAssociation) childAssociationSelector.open();
                                if (result != null && ModelUtils.compareChildAssociation(childAssociation, result))
                                {
                                    // Association has been changed
                                    ModelUtils.copyChildAssociation(childAssociation, result);
                                    item.setText(0,result.getName());
                                    item.setText(1,result.getTargetClassName());
                                }
                            }
                        }
                    }
                }
            }
        };
        childAssociationTable = TableUtils.createTable(childAssociationTabContainer, "Child Associations", true, "Add Child Association", addMouseListener, "Delete Child Association(s)", deleteMouseListener, 
                null, clickListener, columnTitles, columnWidths, 150, false, 0);

        // populate table items
        if (clazz.getAssociations() != null)
        {
            for (M2ClassAssociation m2ns : clazz.getAssociations()) 
            {
                if (m2ns.isChild())
                {
                    TableItem item = new TableItem (childAssociationTable, SWT.NONE);
                    // setup table editor for prefix field
                    item.setText(0, m2ns.getName());
                    // setup table editor for uri field
                    if (m2ns.getTargetClassName()!= null)
                    {
                        item.setText(1, m2ns.getTargetClassName());
                    }
                }
            }
        }

        childAssociationTab.setControl(childAssociationTabContainer);
        tabFolder.pack();
        associationSection.setClient(associationSectionContainer);

        // setup mandatoryAspects section
        Section mandatoryAspectsSection = FormSectionUtils.createStaticSection(toolkit,right,"Mandatory Aspects","View or Update Mandatory Aspects.");
        addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                if (getModel().getAspects() != null && getModel().getAspects().size() > 0)
                {
                    TableItem item = new TableItem (mandatoryAspectstTable, SWT.NONE);
                    item.setText(0, getModel().getAspects().get(0).getName());
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };
        deleteMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableUtils.deleteCheckedItems(mandatoryAspectstTable);
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };
        MouseAdapter doublClickListener =	new MouseAdapter () 
        {
            public void mouseDown(MouseEvent e) 
            {
                Point pt = new Point (e.x, e.y);
                final TableItem newItem = mandatoryAspectstTable.getItem (pt);
                if (newItem == null) 
                {
                    if (mandatoryAspectComboEditor != null)
                    {
                        mandatoryAspectComboEditor.getEditor().dispose();
                    }
                    return;
                }
                mandatoryAspectstTable.showSelection ();
                // make sure the double click case?
                if (mandatoryAspectComboEditor != null)
                {
                    mandatoryAspectComboEditor.getEditor().dispose();
                }

                mandatoryAspectComboEditor= new TableEditor (mandatoryAspectstTable);

                CCombo combo = new CCombo(mandatoryAspectstTable, SWT.READ_ONLY | SWT.FLAT | SWT.BORDER);
                combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                for (M2Aspect m2c : getModel().getAspects())
                {
                    combo.add(m2c.getName());
                }
                combo.setText(newItem.getText(0));

                mandatoryAspectComboEditor.grabHorizontal = true;
                mandatoryAspectComboEditor.setEditor(combo, newItem, 0);
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
                                if (mandatoryAspectComboEditor != null)
                                {
                                    if (mandatoryAspectComboEditor.getEditor() != null)
                                        mandatoryAspectComboEditor.getEditor().dispose();
                                }

                                return;                                
                            }

                        }
                );

                combo.setFocus();

            }
        };
        String [] columnTitles2 = {"Aspect Name"};
        int [] columnWidths2 = {2};
        final Composite  mandatoryAspectsSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, mandatoryAspectsSection);

        mandatoryAspectstTable = TableUtils.createTable(mandatoryAspectsSectionContainer, "Mandatory Aspects", true, "Add Mandatory Aspect", addMouseListener, "Delete Mandatory Aspect(s", deleteMouseListener, 
                doublClickListener, null, columnTitles2, columnWidths2, 150,false,0);

        // populate table items
        if (clazz.getMandatoryAspects() != null)
        {
            for (String ma : clazz.getMandatoryAspects()) 
            {
                TableItem item = new TableItem (mandatoryAspectstTable, SWT.NONE);
                item.setText(0, ma);
            }
        }

        mandatoryAspectsSection.setClient(mandatoryAspectsSectionContainer);        
    }
    /**
     * @return
     */
    public void addProcessingListeners() 
    {		
        ok.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                updatedClass.setName(nameText.getText());
                updatedClass.setTitle(titleText.getText());
                updatedClass.setDescription(descriptionText.getText());
                updatedClass.setParentName(parentNameCombo.getText());

                ArrayList<String> mandatoryAspectNames = new ArrayList<String>();
                for (String mandatoryAspectName:  updatedClass.getMandatoryAspects())
                {
                    mandatoryAspectNames.add(mandatoryAspectName);
                }
                for (String mandatoryAspectName:  mandatoryAspectNames)
                {
                    updatedClass.removeMandatoryAspect(mandatoryAspectName);
                }        
                for (TableItem item : mandatoryAspectstTable.getItems())
                {
                    updatedClass.addMandatoryAspect(item.getText(0));
                }

                if ( getWebConfigRuntime() != null)
                {
                    // web client configuration changes
                    HashMap<String,String> properties = new HashMap<String,String>();
                    HashMap<String,String> wizardProperties = new HashMap<String,String>();

                    boolean isWizardsDirty = false;
                    boolean isAdvancedSearchDirty = false;

                    if ( clazz instanceof M2Type)
                    {
                        properties.put("config", "type");
                        properties.put("type", clazz.getName());
                        properties.put("show", ""+advancedSearchButton.getSelection());

                        wizardProperties.put("type",clazz.getName());
                        if (contentWizardsButton != null)
                        {
                            wizardProperties.put("show",""+contentWizardsButton.getSelection());
                        }
                        isWizardsDirty = getWebConfigRuntime().syncContentWizardOptions(wizardProperties);
                        isAdvancedSearchDirty = getWebConfigRuntime().syncAdvancedSearchOptions(properties);
                    } 
                    else if (clazz instanceof M2Aspect)
                    {
                        wizardProperties.put("aspect", clazz.getName());
                        if (actionWizardsButton != null)
                        {
                            wizardProperties.put("show",""+actionWizardsButton.getSelection());
                        }
                        isWizardsDirty = getWebConfigRuntime().syncActionWizardOptions(wizardProperties);
                    }

                    if (isAdvancedSearchDirty || isWizardsDirty)
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
                updatedClass  = null;
                shell.close();
            }
        });                

    }

}
