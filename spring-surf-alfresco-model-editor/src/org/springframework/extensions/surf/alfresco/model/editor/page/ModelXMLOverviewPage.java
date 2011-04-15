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
package org.springframework.extensions.surf.alfresco.model.editor.page;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2Constraint;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Namespace;
import org.alfresco.repo.dictionary.M2Type;
import org.alfresco.repo.dictionary.ModelRuntime;
import org.alfresco.repo.dictionary.ModelUtils;
import org.alfresco.repo.dictionary.ModelValidator;
import org.alfresco.web.config.WebConfigRuntime;
import org.alfresco.web.config.forms.ConstraintHandlerDefinition;
import org.alfresco.web.config.forms.Control;
import org.alfresco.web.config.forms.FormConfigElement;
import org.alfresco.web.config.forms.FormConfigRuntime;
import org.eclipse.jface.text.IDocument;
import org.eclipse.pde.internal.ui.editor.FormLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.springframework.extensions.surf.alfresco.model.editor.Activator;
import org.springframework.extensions.surf.alfresco.model.editor.ModelEditor;
import org.springframework.extensions.surf.alfresco.model.editor.dialog.AspectEditDialog;
import org.springframework.extensions.surf.alfresco.model.editor.dialog.ConstraintEditDialog;
import org.springframework.extensions.surf.alfresco.model.editor.dialog.FormEditDialog;
import org.springframework.extensions.surf.alfresco.model.editor.dialog.GlobalConstraintHandlerEditDialog;
import org.springframework.extensions.surf.alfresco.model.editor.dialog.GlobalControlEditDialog;
import org.springframework.extensions.surf.alfresco.model.editor.dialog.NamespaceEditDialog;
import org.springframework.extensions.surf.alfresco.model.editor.dialog.TypeEditDialog;
import org.springframework.extensions.surf.alfresco.model.editor.properties.ModelProperties;
import org.springframework.extensions.surf.commons.ui.FormSectionUtils;
import org.springframework.extensions.surf.commons.ui.FormUtils;
import org.springframework.extensions.surf.commons.ui.TableUtils;

/**
 * Abstract class that provides common members and methods for all overview pages
 * 
 * @author drq
 *
 */
@SuppressWarnings("restriction")
public class ModelXMLOverviewPage extends ModelXMLAbstractPage
{
    private Logger logger = Logger.getLogger(ModelXMLOverviewPage.class.getName());

    private IDocument document;
    private M2Model model;
    private ModelRuntime modelRuntime; 

    private IDocument webclientConfigXmlDoc;
    private WebConfigRuntime webConfigRuntime;
    private IDocument formConfigXmlDoc;
    private FormConfigRuntime formConfigRuntime;

    private Text descriptionText;
    private Text modelNameText;
    private Text authorText;
    private Table importTable;
    private Table namespaceTable;
    private Table typeTable;
    private Table aspectTable;
    private Table constraintTable;

    private Table globalControlTable;
    private Table globalConstraintHandlerTable;
    private Table globalJsDependencyTable;
    private Table globalCssDependencyTable;
    private Table nodeTypeFormTable;
    private Table modelTypeFormTable;
    private Table aspectFormTable;

    private TableEditor dependencyEditor;
    
    private Composite right;
    private Section formSection;


    /**
     * Constructor that uses parent, style and document
     * 
     * @param parent Page parent
     * @param style Page style
     * @param document Page associated document
     */
    public ModelXMLOverviewPage(Composite parent, int style, ModelEditor modelEditor)
    {
        super(parent, style , modelEditor);
        this.document = modelEditor.getDocument();
        this.webclientConfigXmlDoc = modelEditor.getWebclientConfigDocument();
        this.formConfigXmlDoc = modelEditor.getFormConfigDocument();
        init();
    }

    /**
     * @return the document
     */
    public IDocument getDocument()
    {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(IDocument document)
    {
        this.document = document;
    }

    /**
     * @return
     */
    public String getFileModelListProperty()
    {
        return ModelProperties.getModelProperty(editorInput.getFile(), ModelProperties.BOOTSTRAP_MODELS_PROPERTY);
    }

    /**
     * Reloads the overview page
     */
    public void reload()
    {
        try
        {
            modelRuntime = new ModelRuntime(document.get());

            if (webclientConfigXmlDoc != null)
            {
                webConfigRuntime = new WebConfigRuntime(webclientConfigXmlDoc.get());
                webConfigRuntime.initWebConfigRuntime();
            }

            if (formConfigXmlDoc != null)
            {
                formConfigRuntime = new FormConfigRuntime(formConfigXmlDoc.get());
                formConfigRuntime.initConfigRuntime();
            }

            if (modelRuntime.initModelRuntime(getFileModelListProperty()))
            {
                msgManager.removeAllMessages();

                model = modelRuntime.getModel();

                if (modelNameText == null || modelNameText.getText() == null)
                {
                    buildLeftSection();
                    buildRightSection();
                    this.layout(true);
                }
                else
                {
                    modelNameText.setText(model.getName());
                    if (model.getAuthor() != null)
                    {
                        authorText.setText(model.getAuthor());
                    }
                    if (model.getDescription() != null)
                    {
                        descriptionText.setText(model.getDescription());
                    }
                    int counter = 0;
                    for (M2Namespace m2n : model.getImports())
                    {
                        TableItem item = null;
                        if (counter < importTable.getItemCount())	
                        {	
                            item = importTable.getItem(counter); 
                        }
                        else
                        {
                            item = new TableItem (importTable, SWT.NONE);
                        }
                        item.setText(0, m2n.getPrefix());
                        item.setText(1, m2n.getUri());                
                        counter++;
                    }

                    counter = 0;
                    for (M2Namespace m2n : model.getNamespaces())
                    {
                        TableItem item = null;
                        if (counter < namespaceTable.getItemCount())	
                        {	
                            item = namespaceTable.getItem(counter); 
                        }
                        else
                        {
                            item = new TableItem (namespaceTable, SWT.NONE);
                        }
                        item.setText(0, m2n.getPrefix());
                        item.setText(1, m2n.getUri());                
                        counter++;
                    }

                    counter=0;
                    for (M2Type m2t : model.getTypes())
                    {
                        TableItem item = null;
                        if (counter < typeTable.getItemCount())	
                        {	
                            item = typeTable.getItem(counter); 
                        }
                        else
                        {
                            item = new TableItem (typeTable, SWT.NONE);
                        }
                        counter++;
                        item.setText(0, m2t.getName());
                        if (m2t.getTitle() != null)
                        {
                            item.setText(1, m2t.getTitle());
                        }
                        if (m2t.getParentName() != null)
                        {
                            item.setText(2, m2t.getParentName());
                        }
                    }

                    counter = 0;
                    for (M2Aspect m2a : model.getAspects())
                    {
                        TableItem item = null;
                        if (counter < aspectTable.getItemCount())	
                        {	
                            item = aspectTable.getItem(counter); 
                        }
                        else
                        {
                            item = new TableItem (aspectTable, SWT.NONE);
                        }
                        item.setText(0, m2a.getName());
                        if (m2a.getTitle() != null)
                        {
                            item.setText(1, m2a.getTitle());
                        }
                        if (m2a.getParentName() != null)
                        {
                            item.setText(2, m2a.getParentName());
                        }
                        counter ++;
                    }

                    counter =0;
                    for (M2Constraint m2c : model.getConstraints())
                    {
                        TableItem item = null;
                        if (counter < constraintTable.getItemCount())	
                        {	
                            item = constraintTable.getItem(counter); 
                        }
                        else
                        {
                            item = new TableItem (constraintTable, SWT.NONE);
                        }
                        item.setText(0, m2c.getName());
                        item.setText(1, m2c.getType());
                        counter ++;
                    }
                }
            }
        }
        catch(Exception e)
        {
            logger.log(Level.SEVERE, "Failed to reload overview page", e);
            FormUtils.addErrorMessage(msgManager, e);
        }        
    }

    public void initFormSection ()
    {
        this.formConfigXmlDoc = modelEditor.getFormConfigDocument();
        
        if (formConfigXmlDoc != null)
        {
            formConfigRuntime = new FormConfigRuntime(formConfigXmlDoc.get());
            formConfigRuntime.initConfigRuntime();
        }
       
        if (formConfigXmlDoc != null)
        {
            buildFormSection(right);
        }
    }
    
    public void initWebclientConfig ()
    {
        this.webclientConfigXmlDoc = modelEditor.getWebclientConfigDocument();

        if (webclientConfigXmlDoc != null)
        {
            webConfigRuntime = new WebConfigRuntime(webclientConfigXmlDoc.get());
            webConfigRuntime.initWebConfigRuntime();
        }
    }
    
    public void removeWebclientConfig ()
    {
        if (webclientConfigXmlDoc != null)
        {
            webclientConfigXmlDoc = null;
            webConfigRuntime = null;
        }
    }
    
    public void removeFormSection ()
    {
        if (formConfigXmlDoc != null)
        {
            formConfigXmlDoc = null;
        }
       
        if (formSection != null)
        {
            formSection.dispose();
        }
    }
    /**
     * 
     */
    protected void buildLeftSection()
    {
        Composite body = form.getBody();

        // build left column of the form body
        Composite left = toolkit.createComposite(body);
        left.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
        left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        final Section actionsSection = FormSectionUtils.createStaticSection(toolkit,left,"Model Design","Links for Model Design.");        
        final Composite  actionsSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, actionsSection);

        // build basic information section
        Composite actions = toolkit.createComposite(actionsSectionContainer);        
        actions.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        actions.setLayout(new GridLayout(6, false));

        // build short name text field
        Label label = new Label(actions, SWT.NULL);
        label.setImage(Activator.getImageDescriptor("icons/js.gif").createImage());

        Link modelValidation = new Link (actions,SWT.NULL);
        modelValidation.setText("<a>Validate Model</a>");
        modelValidation.addListener (SWT.Selection, new Listener () 
        {
            public void handleEvent(Event event) 
            {
                if ( ModelValidator.validate(new ByteArrayInputStream(document.get().getBytes())))
                {
                    MessageBox mb = new MessageBox(getShell(), SWT.ICON_WORKING | SWT.OK);
                    mb.setText("Model Validation Result");
                    mb.setMessage("This model is valid.");
                    mb.open();
                }
                else
                {
                    MessageBox mb = new MessageBox(getShell(), SWT.ICON_WORKING | SWT.OK);
                    mb.setText("Model Validation Result");
                    mb.setMessage("This model is invalid.");
                    mb.open();                    
                }
            }
        });
        
        label = new Label(actions, SWT.NULL);
        label.setImage(Activator.getImageDescriptor("icons/configure.gif").createImage());

        Link editorPreference = new Link (actions,SWT.NULL);
        editorPreference.setText("<a>Editor Preferences</a>");
        editorPreference.addListener (SWT.Selection, new Listener () 
        {
            public void handleEvent(Event event) 
            {
                getModelEditor().activePreferencePage();
            }
        });

        actionsSection.setClient(actionsSectionContainer);

        Section basicInfoSection = FormSectionUtils.createStaticSection(toolkit,left,"General Information","Model General Information");        
        Composite  basicInfoSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, basicInfoSection);

        // build basic information section
        Composite basicInfo = toolkit.createComposite(basicInfoSectionContainer);        
        basicInfo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        basicInfo.setLayout(new GridLayout(2, false));

        model = modelRuntime.getModel();
        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Name:");

        String modelName = model.getName();
        modelNameText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        modelNameText.setLayoutData(gridData);
        modelNameText.setText(modelName);
        modelNameText.addModifyListener 
        (
                new ModifyListener() 
                {
                    public void modifyText(ModifyEvent e) 
                    {
                        Text text = (Text) e.widget;
                        if (!model.getName().equals(text.getText()))
                        {
                            model.setName(text.getText());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            model.toXML(baos);
                            resetDocument(baos.toString());
                        }
                    }
                }
        );

        // build description textarea field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Description:");
        gridData = new GridData();
        gridData.verticalAlignment = SWT.TOP;
        label.setLayoutData(gridData);

        String description = model.getDescription();
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
        descriptionText.setText(description);        
        descriptionText.addModifyListener
        (
                new ModifyListener() 
                {
                    public void modifyText(ModifyEvent e) 
                    {
                        Text text = (Text) e.widget;
                        if (!model.getDescription().equals(text.getText()))
                        {
                            model.setDescription(text.getText());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            model.toXML(baos);
                            resetDocument(baos.toString());
                        }               
                    }
                }
        );
        gc.dispose();

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Author:");

        String author = model.getAuthor();
        authorText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        authorText.setLayoutData(gridData);
        if (author != null)
        {
            authorText.setText(author);
        }
        authorText.addModifyListener 
        (
                new ModifyListener() 
                {
                    public void modifyText(ModifyEvent e) 
                    {
                        Text text = (Text) e.widget;
                        if (ModelUtils.compareString(model.getAuthor(),text.getText()))
                        {
                            model.setAuthor(text.getText());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            model.toXML(baos);
                            resetDocument(baos.toString());
                        }
                    }
                }
        );

        basicInfoSection.setClient(basicInfoSectionContainer);

        // setup namespace section
        Section namespaceSection = FormSectionUtils.createStaticSection(toolkit,left,"Model Namespaces","View or Update Model Imported and Defined Namespaces.");

        final Composite  namespaceSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, namespaceSection);
        
        Composite namespaces = toolkit.createComposite(namespaceSectionContainer);        
        namespaces.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        namespaces.setLayout(new GridLayout(1, false));

        GridData tabGridData = new GridData();
        tabGridData.horizontalAlignment = SWT.FILL;
        tabGridData.grabExcessHorizontalSpace = true;

        TabFolder tabFolder = new TabFolder (namespaces, SWT.BORDER);
        tabFolder.setLayoutData(tabGridData);

        TabItem importedTab = new TabItem (tabFolder, SWT.NONE);        
        importedTab.setText ("Imported Namespaces"); 
        Composite importedTabContainer = FormSectionUtils.createStaticSectionClient(toolkit, tabFolder);
        
        MouseListener addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem item = new TableItem (importTable, SWT.NONE);
                int nextId = TableUtils.getNextId(importTable, "iuri");
                M2Namespace m2ns = model.createImport("iuri"+nextId, "iprefix"+nextId);
                item.setText(0, m2ns.getPrefix());
                item.setText(1, m2ns.getUri());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                model.toXML(baos);
                resetDocument(baos.toString());
                
                NamespaceEditDialog namespaceSelector = new NamespaceEditDialog(importTable.getShell(),model.getImport(item.getText(1)),modelRuntime.getService(),model,webConfigRuntime,webclientConfigXmlDoc);
                M2Namespace result = namespaceSelector.open();
                if (result != null)
                {
                    if (ModelUtils.compareNameSpace(result,model.getImport(item.getText(1))))
                    {
                        item.setText(0, result.getPrefix());
                        item.setText(1, result.getUri());
                        model.getImport(item.getText(1)).setPrefix(result.getPrefix());
                        model.getImport(item.getText(1)).setUri(result.getUri());
                        ByteArrayOutputStream resultBaos = new ByteArrayOutputStream();
                        model.toXML(resultBaos);
                        resetDocument(resultBaos.toString());
                    }
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
                TableItem[] items = importTable.getItems();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        model.removeImport(importTable.getItem(i).getText(1));
                    }
                }

                int [] selected = TableUtils.deleteCheckedItems(importTable);
                if (selected.length >0)
                {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    model.toXML(baos);
                    resetDocument(baos.toString());
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };

        Listener clickListener = new Listener() {
            public void handleEvent(Event event) {
                Point pt = new Point(event.x, event.y);
                TableItem item = importTable.getItem(pt);
                if (item == null)
                    return;
                for (int i = 0; i < 2; i++) {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) {
                        NamespaceEditDialog namespaceSelector = new NamespaceEditDialog(importTable.getShell(),model.getImport(item.getText(1)),modelRuntime.getService(),model,webConfigRuntime,webclientConfigXmlDoc);
                        M2Namespace result = namespaceSelector.open();
                        if (result != null)
                        {
                            if (ModelUtils.compareNameSpace(result,model.getImport(item.getText(1))))
                            {
                                item.setText(0, result.getPrefix());
                                item.setText(1, result.getUri());
                                model.getImport(item.getText(1)).setPrefix(result.getPrefix());
                                model.getImport(item.getText(1)).setUri(result.getUri());
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                model.toXML(baos);
                                resetDocument(baos.toString());
                            }
                        }

                    }
                }
            }
        };
        String[] columnTitles = {"Prefix", "Uri"};
        int [] columnWidths = {75};

        importTable = TableUtils.createTable(importedTabContainer, "Imported Namespace Editor", true, "Add Import", addMouseListener, "Delete Import(s)", deleteMouseListener, 
                null, clickListener, columnTitles, columnWidths, 150, false, 0);
        // populate table items
        if (model.getImports() != null)
        {
            for (M2Namespace m2ns : model.getImports()) 
            {
                TableItem item = new TableItem (importTable, SWT.NONE);

                // setup table editor for prefix field
                item.setText(0, m2ns.getPrefix());
                // setup table editor for uri field
                item.setText(1, m2ns.getUri());
            }
        }
        
        importedTab.setControl(importedTabContainer);
        
        TabItem definedTab = new TabItem (tabFolder, SWT.NONE);        
        definedTab.setText ("Defined Namespaces"); 
        Composite definedTabContainer = FormSectionUtils.createStaticSectionClient(toolkit, tabFolder);

        
        addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem item = new TableItem (namespaceTable, SWT.NONE);
                int nextId = TableUtils.getNextId(namespaceTable,"nsuri");
                M2Namespace m2ns = model.createNamespace("nsuri"+nextId, "nsprefix"+nextId);
                item.setText(0, m2ns.getPrefix());
                item.setText(1, m2ns.getUri());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                model.toXML(baos);
                resetDocument(baos.toString());
                
                int index = namespaceTable.indexOf(item);
                NamespaceEditDialog namespaceSelector = new NamespaceEditDialog(namespaceTable.getShell(),model.getNamespace(item.getText(1)),modelRuntime.getService(),model,webConfigRuntime,webclientConfigXmlDoc);
                M2Namespace result = namespaceSelector.open();
                if (result != null)
                {
                    if (ModelUtils.compareNameSpace(result,model.getNamespaces().get(index)))
                    {
                        item.setText(0, result.getPrefix());
                        item.setText(1, result.getUri());
                        model.getNamespace(item.getText(1)).setPrefix(result.getPrefix());
                        model.getNamespace(item.getText(1)).setUri(result.getUri());
                        ByteArrayOutputStream resultBaos = new ByteArrayOutputStream();
                        model.toXML(resultBaos);
                        resetDocument(resultBaos.toString());
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
                TableItem[] items = namespaceTable.getItems();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        model.removeNamespace(namespaceTable.getItem(i).getText(1));
                    }
                }

                int [] selected = TableUtils.deleteCheckedItems(namespaceTable);
                if (selected.length >0)
                {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    model.toXML(baos);
                    resetDocument(baos.toString());
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };

        clickListener = new Listener() {
            public void handleEvent(Event event) {
                Point pt = new Point(event.x, event.y);
                TableItem item = namespaceTable.getItem(pt);
                if (item == null)
                    return;
                for (int i = 0; i < 2; i++) {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) {
                        int index = namespaceTable.indexOf(item);
                        NamespaceEditDialog namespaceSelector = new NamespaceEditDialog(namespaceTable.getShell(),model.getNamespace(item.getText(1)),modelRuntime.getService(),model,webConfigRuntime,webclientConfigXmlDoc);
                        M2Namespace result = namespaceSelector.open();
                        if (result != null)
                        {
                            if (ModelUtils.compareNameSpace(result,model.getNamespaces().get(index)))
                            {
                                item.setText(0, result.getPrefix());
                                item.setText(1, result.getUri());
                                model.getNamespace(item.getText(1)).setPrefix(result.getPrefix());
                                model.getNamespace(item.getText(1)).setUri(result.getUri());
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                model.toXML(baos);
                                resetDocument(baos.toString());
                            }
                        }

                    }
                }
            }
        };

        namespaceTable = TableUtils.createTable(definedTabContainer, "Defined Namespace Editor", true, "Add Namespace", 
                addMouseListener, "Delete Namespace(s)", deleteMouseListener, 
                null, clickListener, columnTitles, columnWidths, 150, false, 0);

        // populate table items
        if (model.getImports() != null)
        {
            for (M2Namespace m2ns : model.getNamespaces()) 
            {
                TableItem item = new TableItem (namespaceTable, SWT.NONE);

                // setup table editor for prefix field
                item.setText(0, m2ns.getPrefix());
                // setup table editor for uri field
                item.setText(1, m2ns.getUri());
            }
        }

        definedTab.setControl(definedTabContainer);
        
        tabFolder.pack();
        
        namespaceSection.setClient(namespaceSectionContainer);
    }

    protected void buildRightSection()
    {
        Composite body = form.getBody();

        right = toolkit.createComposite(body);
        right.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
        right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        // setup type section
        Section typeSection = FormSectionUtils.createStaticSection(toolkit,right,"Model Types, Aspectes and Constraints","View or Update Model Types, Aspects and Constraints.");
        final Composite  typeSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, typeSection);
        
        Composite tacs = toolkit.createComposite(typeSectionContainer);        
        tacs.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        tacs.setLayout(new GridLayout(1, false));

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        TabFolder tabFolder = new TabFolder (tacs, SWT.BORDER);
        tabFolder.setLayoutData(gridData);

        TabItem typeTab = new TabItem (tabFolder, SWT.NONE);        
        typeTab.setText ("Types"); 
        Composite typeTabContainer = FormSectionUtils.createStaticSectionClient(toolkit, tabFolder);

        MouseListener addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                String prefix = ModelUtils.getDefaultNameSpace(model);
                int nextId = TableUtils.getNextId(typeTable, prefix+"type");
                String newTypeName = prefix + "type" + nextId;
                M2Type newType = model.createType(newTypeName);
                newType.setTitle("New Type");
                newType.setParentName("cm:content");
                TableItem item = new TableItem (typeTable, SWT.NONE);
                item.setText(0, newType.getName());
                item.setText(1, newType.getTitle());
                item.setText(2, newType.getParentName());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                model.toXML(baos);
                resetDocument(baos.toString());
                
                TypeEditDialog typeSelector = new TypeEditDialog(typeTable.getShell(),
                        model.getType(item.getText(0)), modelRuntime.getService(),model,webConfigRuntime,webclientConfigXmlDoc);
                M2Type result = (M2Type)typeSelector.open();
                if (result != null && ModelUtils.compareType(model.getType(item.getText(0)), result))
                {
                    // Property has been changed
                    ModelUtils.copyType(model.getType(item.getText(0)), result);
                    item.setText(0,result.getName());
                    item.setText(1,result.getTitle());
                    item.setText(2,result.getParentName());
                    ByteArrayOutputStream resultBaos = new ByteArrayOutputStream();
                    model.toXML(resultBaos);
                    resetDocument(resultBaos.toString());
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
                TableItem[] items = typeTable.getItems();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        model.removeType(typeTable.getItem(i).getText(0));
                    }
                }

                int counter = 0;

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        counter ++;
                    }
                }

                int [] selected = new int[counter];
                counter = 0;
                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        selected[counter] = i;
                        counter ++;
                    }
                }
                if (counter >0)
                {
                    typeTable.remove (selected);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    model.toXML(baos);
                    resetDocument(baos.toString());
                }
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
                TableItem item = typeTable.getItem(pt);
                if (item == null)
                    return;
                for (int i = 0; i < 3; i++) {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        TypeEditDialog typeSelector = new TypeEditDialog(typeTable.getShell(),
                                model.getType(item.getText(0)), modelRuntime.getService(),model,webConfigRuntime,webclientConfigXmlDoc);
                        M2Type result = (M2Type)typeSelector.open();
                        if (result != null && ModelUtils.compareType(model.getType(item.getText(0)), result))
                        {
                            // Property has been changed
                            ModelUtils.copyType(model.getType(item.getText(0)), result);
                            item.setText(0,result.getName());
                            item.setText(1,result.getTitle());
                            item.setText(2,result.getParentName());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            model.toXML(baos);
                            resetDocument(baos.toString());
                        }
                    }
                }
            }
        };
        String [] columnTitles = {"Name", "Title" , "Parent Name"};
        int [] columnWidths = {120,2};
        typeTable = TableUtils.createTable(typeTabContainer, "Type Editor", true, "Add Type", addMouseListener, "Delete Type(s)", deleteMouseListener, 
                null, clickListener, columnTitles, columnWidths, 150, false, 0);

        // populate table items
        if (model.getTypes() != null)
        {
            for (M2Type m2t : model.getTypes()) 
            {
                TableItem item = new TableItem (typeTable, SWT.NONE);

                // setup table editor for prefix field
                item.setText(0, m2t.getName());
                // setup table editor for uri field
                item.setText(1, m2t.getTitle());
                item.setText(2,m2t.getParentName());
            }
        }

        typeTab.setControl(typeTabContainer);
        
        TabItem aspectTab = new TabItem (tabFolder, SWT.NONE);        
        aspectTab.setText ("Aspects"); 
        Composite aspectTabContainer = FormSectionUtils.createStaticSectionClient(toolkit, tabFolder);

        addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                String prefix = ModelUtils.getDefaultNameSpace(model);
                int nextId = TableUtils.getNextId(aspectTable,prefix + "aspect");
                String newAspectName = prefix + "aspect" + nextId;
                M2Aspect newAspect = model.createAspect(newAspectName);
                newAspect.setTitle("New Property");
                newAspect.setParentName("cm:content");
                TableItem item = new TableItem (aspectTable, SWT.NONE);
                item.setText(0, newAspect.getName());
                item.setText(1, newAspect.getTitle());
                item.setText(2, newAspect.getParentName());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                model.toXML(baos);
                resetDocument(baos.toString());
                
                AspectEditDialog aspectSelector = new AspectEditDialog(aspectTable.getShell(),
                        model.getAspect(item.getText(0)), modelRuntime.getService(),model,webConfigRuntime,webclientConfigXmlDoc);
                M2Aspect result = (M2Aspect)aspectSelector.open();
                if (result != null && ModelUtils.compareAspect(model.getAspect(item.getText(0)), result))
                {
                    // Property has been changed
                    ModelUtils.copyAspect(model.getAspect(item.getText(0)), result);
                    item.setText(0,result.getName());
                    item.setText(1,result.getTitle());
                    item.setText(2,result.getParentName());
                    ByteArrayOutputStream resultBaos = new ByteArrayOutputStream();
                    model.toXML(resultBaos);
                    resetDocument(resultBaos.toString());
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
                TableItem[] items = aspectTable.getItems();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        model.removeAspect(aspectTable.getItem(i).getText(0));
                    }
                }

                int counter = 0;

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        counter ++;
                    }
                }

                int [] selected = new int[counter];
                counter = 0;
                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        selected[counter] = i;
                        counter ++;
                    }
                }
                if (counter >0)
                {
                    aspectTable.remove (selected);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    model.toXML(baos);
                    resetDocument(baos.toString());
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };

        clickListener = new Listener() {
            public void handleEvent(Event event) {
                Point pt = new Point(event.x, event.y);
                TableItem item = aspectTable.getItem(pt);
                if (item == null)
                    return;
                for (int i = 0; i < 3; i++) {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) {
                        AspectEditDialog aspectSelector = new AspectEditDialog(aspectTable.getShell(),
                                model.getAspect(item.getText(0)), modelRuntime.getService(),model,webConfigRuntime,webclientConfigXmlDoc);
                        M2Aspect result = (M2Aspect)aspectSelector.open();
                        if (result != null && ModelUtils.compareAspect(model.getAspect(item.getText(0)), result))
                        {
                            // Property has been changed
                            ModelUtils.copyAspect(model.getAspect(item.getText(0)), result);
                            item.setText(0,result.getName());
                            item.setText(1,result.getTitle());
                            item.setText(2,result.getParentName());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            model.toXML(baos);
                            resetDocument(baos.toString());
                        }
                    }
                }
            }
        };

        aspectTable = TableUtils.createTable(aspectTabContainer, "Aspect Editor", true, "Add Aspect", addMouseListener, "Delete Aspect(s)", deleteMouseListener,
                null, clickListener, columnTitles, columnWidths, 150, false, 0);

        // populate table items
        if (model.getAspects() != null)
        {
            for (M2Aspect m2t : model.getAspects()) 
            {
                TableItem item = new TableItem (aspectTable, SWT.NONE);

                // setup table editor for prefix field
                item.setText(0, m2t.getName());
                // setup table editor for uri field
                item.setText(1, m2t.getTitle());
                if (m2t.getParentName() != null)
                {
                    item.setText(2,m2t.getParentName());
                }
            }
        }

        aspectTab.setControl(aspectTabContainer);
        
        TabItem constraintTab = new TabItem (tabFolder, SWT.NONE);        
        constraintTab.setText ("Constraints"); 
        Composite constraintTabContainer = FormSectionUtils.createStaticSectionClient(toolkit, tabFolder);

        addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                String prefix = ModelUtils.getDefaultNameSpace(model);
                int nextId = TableUtils.getNextId(constraintTable,  prefix +"constraint");
                String newConstraintName = prefix + "constraint" + nextId;
                M2Constraint newConstraint = model.createConstraint(newConstraintName,"REGEX");
                newConstraint.setTitle("New Constraint");
                newConstraint.setType("REGEX");
                newConstraint.createParameter("expression", "[A-Z]*");
                newConstraint.createParameter("requiresMatch", "false");
                TableItem item = new TableItem (constraintTable, SWT.NONE);
                item.setText(0, newConstraint.getName());
                item.setText(1, newConstraint.getType());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                model.toXML(baos);
                resetDocument(baos.toString());
                
                ConstraintEditDialog constraintSelector = new ConstraintEditDialog(constraintTable.getShell(),
                        model.getConstraint(item.getText(0)), modelRuntime.getService(),model,webConfigRuntime,webclientConfigXmlDoc);
                M2Constraint result = constraintSelector.open();
                if (result != null && ModelUtils.compareConstraint(model.getConstraint(item.getText(0)), result))
                {
                    // Property has been changed
                    ModelUtils.copyConstraint(model.getConstraint(item.getText(0)), result);
                    item.setText(0,result.getName());
                    item.setText(1,result.getType());
                    ByteArrayOutputStream resultBaos = new ByteArrayOutputStream();
                    model.toXML(resultBaos);
                    resetDocument(resultBaos.toString());
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
                TableItem[] items = constraintTable.getItems();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        M2Constraint m2c = model.getConstraint(constraintTable.getItem(i).getText(0));
                        if (m2c != null)
                        {
                            model.removeConstraint(m2c.getName());
                        }
                    }
                }

                int counter = 0;

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        counter ++;
                    }
                }

                int [] selected = new int[counter];
                counter = 0;
                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        selected[counter] = i;
                        counter ++;
                    }
                }
                if (counter >0)
                {
                    constraintTable.remove (selected);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    model.toXML(baos);
                    resetDocument(baos.toString());
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };
        clickListener = new Listener() {
            public void handleEvent(Event event) {
                Point pt = new Point(event.x, event.y);
                TableItem item = constraintTable.getItem(pt);
                if (item == null)
                    return;
                for (int i = 0; i < 2; i++) {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) {

                        ConstraintEditDialog constraintSelector = new ConstraintEditDialog(constraintTable.getShell(),
                                model.getConstraint(item.getText(0)), modelRuntime.getService(),model,webConfigRuntime,webclientConfigXmlDoc);
                        M2Constraint result = constraintSelector.open();
                        if (result != null && ModelUtils.compareConstraint(model.getConstraint(item.getText(0)), result))
                        {
                            // Property has been changed
                            ModelUtils.copyConstraint(model.getConstraint(item.getText(0)), result);
                            item.setText(0,result.getName());
                            item.setText(1,result.getType());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            model.toXML(baos);
                            resetDocument(baos.toString());
                        }

                    }
                }
            }
        };
        String[] constraintTitles = {"Name", "Type"};
        int [] constraintColumnWidths = {2};
        constraintTable = TableUtils.createTable(constraintTabContainer, "Constraint Editor", true, "Add Constraint", addMouseListener, "Delete Constraint(s)", deleteMouseListener, 
                null, clickListener, constraintTitles, constraintColumnWidths, 150, false, 0);

        // populate table items
        if (model.getConstraints() != null)
        {
            for (M2Constraint m2t : model.getConstraints()) 
            {
                TableItem item = new TableItem (constraintTable, SWT.NONE);

                // setup table editor for prefix field
                item.setText(0, m2t.getName());
                // setup table editor for uri field
                item.setText(1, m2t.getType());
            }
        }
        constraintTab.setControl(constraintTabContainer);        
        tabFolder.pack();
        
        typeSection.setClient(typeSectionContainer);

        if (formConfigXmlDoc != null)
        {
            buildFormSection(right);
        }

    }

    /**
     * @param parent
     */
    protected void buildFormSection(Composite parent)
    {
        formSection = FormSectionUtils.createStaticSection(toolkit,parent,"Form Design","View, Create or Edit Form Configurations.");        
        final Composite  formSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, formSection);
        
        // build forms section
        Composite forms = toolkit.createComposite(formSectionContainer);        
        forms.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        forms.setLayout(new GridLayout(1, false));

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        TabFolder tabFolder = new TabFolder (forms, SWT.BORDER);
        tabFolder.setLayoutData(gridData);

        TabItem generalTab = new TabItem (tabFolder, SWT.NONE);        
        generalTab.setText ("Global"); 
        Composite generalTabContainer = FormSectionUtils.createStaticSectionClient(toolkit, tabFolder);
        Map<String, Control> globalDefaultControls = formConfigRuntime.getGlobalDefaultControls();
        MouseListener addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem item = new TableItem (globalControlTable, SWT.NONE);
                int nextId = TableUtils.getNextId(globalControlTable, "type");
                item.setText(0, "type"+nextId);
                item.setText(1, "/form-controls/type"+nextId+".ftl");
                // reset the form configuration document
                HashMap<String,Object> properties = new HashMap<String,Object>();
                properties.put("config-type", "default-control");
                properties.put("type-name", item.getText(0));
                properties.put("control", new Control(item.getText(1)));
                if ( formConfigRuntime.syncGlobalConfigs(properties) )
                {
                    formConfigRuntime.toXml();
                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                    
                    GlobalControlEditDialog globalControlEditDialog = new GlobalControlEditDialog(globalControlTable.getShell(),
                            modelRuntime.getService(),model,formConfigRuntime,formConfigXmlDoc,item.getText(0), formConfigRuntime.getGlobalDefaultControls().get(item.getText(0)));
                    HashMap<String, Object> resultProperties = globalControlEditDialog.open();
                    if (resultProperties != null)
                    {
                        if (resultProperties.containsKey("new-type-name"))
                        {
                            item.setText(0, (String)resultProperties.get("new-type-name"));
                        }
                        item.setText(1, ((Control)resultProperties.get("control")).getTemplate());
                        if (formConfigRuntime.syncGlobalConfigs(resultProperties))
                        {
                            formConfigRuntime.toXml();
                            formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                        }
                    }
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
                TableItem[] items = globalControlTable.getItems();
                HashMap<String,Object> properties = new HashMap<String,Object>();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        // remove global control
                        properties.put(items[i].getText(0), items[i].getText(1));
                    }
                }

                TableUtils.deleteCheckedItems(globalControlTable);

                if (formConfigRuntime.removeDefaultControls(properties))
                {
                    // reset the form configuration document
                    formConfigRuntime.toXml();
                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                }
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
                TableItem item = globalControlTable.getItem(pt);
                if (item == null)
                    return;
                for (int i = 0; i < 2; i++) 
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        GlobalControlEditDialog globalControlEditDialog = new GlobalControlEditDialog(globalControlTable.getShell(),
                                modelRuntime.getService(),model,formConfigRuntime,formConfigXmlDoc,item.getText(0), formConfigRuntime.getGlobalDefaultControls().get(item.getText(0)));
                        HashMap<String, Object> properties = globalControlEditDialog.open();
                        if (properties != null)
                        {
                            if (properties.containsKey("new-type-name"))
                            {
                                item.setText(0, (String)properties.get("new-type-name"));
                            }
                            item.setText(1, ((Control)properties.get("control")).getTemplate());
                            if (formConfigRuntime.syncGlobalConfigs(properties))
                            {
                                formConfigRuntime.toXml();
                                formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                            }
                        }
                    }
                }
            }
        };
        String[] columnTitles = {"Name", "Template"};
        int [] columnWidths = {75};

        globalControlTable = TableUtils.createTable(generalTabContainer, "Global Control", true, "Add Control", addMouseListener, "Delete Control(s)", deleteMouseListener, 
                null, clickListener, columnTitles, columnWidths, 150, false, 0);
        if (globalDefaultControls!=null)
        {
            // populate table items
            for (String typeName : globalDefaultControls.keySet())
            {
                TableItem item = new TableItem (globalControlTable, SWT.NONE);
                item.setText(0, typeName);
                item.setText(1, globalDefaultControls.get(typeName).getTemplate());
            }
        }

        Map<String, ConstraintHandlerDefinition>  globalConstraintHandlers = formConfigRuntime.getGlobalConstraintHandlers();
        MouseListener addMouseListener2 = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem item = new TableItem (globalConstraintHandlerTable, SWT.NONE);
                int nextId = TableUtils.getNextId(globalConstraintHandlerTable, "constraint");
                item.setText(0, "constraint"+nextId);
                item.setText(1, "handler"+nextId);
                // reset the form configuration document
                HashMap<String,Object> properties = new HashMap<String,Object>();
                properties.put("config-type", "constraint-handler");
                properties.put("constraint-type-name", item.getText(0));
                properties.put("constraint", new ConstraintHandlerDefinition(item.getText(0),item.getText(1),"","",""));
                if ( formConfigRuntime.syncGlobalConfigs(properties) )
                {
                    formConfigRuntime.toXml();
                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                    
                    GlobalConstraintHandlerEditDialog globalConstraintHandlerEditDialog = new GlobalConstraintHandlerEditDialog(globalConstraintHandlerTable.getShell(),
                            modelRuntime.getService(),model,formConfigRuntime,formConfigXmlDoc,item.getText(0), formConfigRuntime.getGlobalConstraintHandlers().get(item.getText(0)));
                    HashMap<String, Object> resultProperties = globalConstraintHandlerEditDialog.open();
                    if (resultProperties != null)
                    {
                        if (resultProperties.containsKey("new-constraint-type-name"))
                        {
                            item.setText(0, (String)resultProperties.get("new-constraint-type-name"));
                        }
                        item.setText(1, ((ConstraintHandlerDefinition)resultProperties.get("constraint")).getValidationHandler());
                        if (formConfigRuntime.syncGlobalConfigs(resultProperties))
                        {
                            formConfigRuntime.toXml();
                            formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                        }
                    }
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };
        MouseListener deleteMouseListener2 = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem[] items = globalConstraintHandlerTable.getItems();
                HashMap<String,Object> properties = new HashMap<String,Object>();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        // remove global control
                        properties.put(items[i].getText(0), items[i].getText(1));
                    }
                }

                TableUtils.deleteCheckedItems(globalConstraintHandlerTable);

                if (formConfigRuntime.removeConstraintHandlers(properties))
                {
                    // reset the form configuration document
                    formConfigRuntime.toXml();
                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };

        Listener clickListener2 = new Listener() 
        {
            public void handleEvent(Event event) 
            {
                Point pt = new Point(event.x, event.y);
                TableItem item = globalConstraintHandlerTable.getItem(pt);
                if (item == null)
                    return;
                for (int i = 0; i < 2; i++) 
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        GlobalConstraintHandlerEditDialog globalConstraintHandlerEditDialog = new GlobalConstraintHandlerEditDialog(globalConstraintHandlerTable.getShell(),
                                modelRuntime.getService(),model,formConfigRuntime,formConfigXmlDoc,item.getText(0), formConfigRuntime.getGlobalConstraintHandlers().get(item.getText(0)));
                        HashMap<String, Object> properties = globalConstraintHandlerEditDialog.open();
                        if (properties != null)
                        {
                            if (properties.containsKey("new-constraint-type-name"))
                            {
                                item.setText(0, (String)properties.get("new-constraint-type-name"));
                            }
                            item.setText(1, ((ConstraintHandlerDefinition)properties.get("constraint")).getValidationHandler());
                            if (formConfigRuntime.syncGlobalConfigs(properties))
                            {
                                formConfigRuntime.toXml();
                                formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                            }
                        }
                    }
                }
            }
        };
        String[] columnTitles2 = {"Type", "Validation Handler"};
        int [] columnWidths2 = {125};

        globalConstraintHandlerTable = TableUtils.createTable(generalTabContainer, "Global Constraints Handlers", true, "Add Handler", addMouseListener2, "Delete Handlers(s)", deleteMouseListener2, 
                null, clickListener2, columnTitles2, columnWidths2, 150, false, 0);
        if (globalConstraintHandlers!=null)
        {
            // populate table items
            for (String typeName : globalConstraintHandlers.keySet())
            {
                TableItem item = new TableItem (globalConstraintHandlerTable, SWT.NONE);
                item.setText(0, typeName);
                item.setText(1, globalConstraintHandlers.get(typeName).getValidationHandler());
            }
        }
        
        generalTab.setControl(generalTabContainer);
        
        TabItem dependencyTab = new TabItem (tabFolder, SWT.NONE);        
        dependencyTab.setText ("Dependencies"); 
        Composite dependencyTabContainer = FormSectionUtils.createStaticSectionClient(toolkit, tabFolder);

        final String[]  globalJsDependencies = formConfigRuntime.getJsDependencies();
        MouseListener addMouseListener3 = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem item = new TableItem (globalJsDependencyTable, SWT.NONE);
                int nextId = TableUtils.getNextId(globalJsDependencyTable, "/js", ".js");
                item.setText(0, "/js"+nextId+".js");
                // reset the form configuration document
                HashMap<String,Object> properties = new HashMap<String,Object>();
                properties.put("config-type", "dependencies");
                properties.put("js", item.getText(0));
                if ( formConfigRuntime.syncGlobalConfigs(properties) )
                {
                    formConfigRuntime.toXml();
                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };
        MouseListener deleteMouseListener3 = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem[] items = globalJsDependencyTable.getItems();
                ArrayList<String> jsList = new ArrayList<String>();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        // remove global control
                        jsList.add(items[i].getText(0));
                    }
                }

                TableUtils.deleteCheckedItems(globalJsDependencyTable);

                if (formConfigRuntime.removeJsDependencies(jsList))
                {
                    // reset the form configuration document
                    formConfigRuntime.toXml();
                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                }
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
                int index = globalJsDependencyTable.getSelectionIndex ();
                Point pt = new Point (e.x, e.y);
                final TableItem newItem = globalJsDependencyTable.getItem (pt);
                if (newItem == null) 
                {
                    if (dependencyEditor != null)
                    {
                        dependencyEditor.getEditor().dispose();
                    }
                    return;
                }
                globalJsDependencyTable.showSelection ();
                // make sure the double click case?
                if (dependencyEditor != null)
                {
                    dependencyEditor.getEditor().dispose();
                }
                dependencyEditor = new TableEditor (globalJsDependencyTable);
                Text urlText = new Text (globalJsDependencyTable, SWT.NONE);
                urlText.setText(newItem.getText(0));
                dependencyEditor.grabHorizontal = true;
                dependencyEditor.setEditor(urlText, newItem, 0);
                final int currentIndex = index;
                urlText.addModifyListener
                (
                        new ModifyListener() 
                        {
                            public void modifyText(ModifyEvent e) 
                            {
                                Text text = (Text) e.widget;
                                HashMap<String,Object> properties = new HashMap<String,Object>();
                                properties.put("config-type", "dependencies");
                                properties.put("js", formConfigRuntime.getJsDependencies()[currentIndex]);
                                properties.put("new-js", text.getText());
                                newItem.setText(text.getText());
                                if ( formConfigRuntime.syncGlobalConfigs(properties) )
                                {
                                    formConfigRuntime.toXml();
                                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                                }          
                            }
                        }
                );

                urlText.addFocusListener
                (
                        new FocusListener()
                        {
                            public void focusGained(FocusEvent arg0)
                            {
                            }
                            public void focusLost(FocusEvent arg0)
                            {
                                if (dependencyEditor != null)
                                {
                                    dependencyEditor.getEditor().dispose();
                                }
                                return;                                
                            }

                        }
                );

                urlText.setFocus();

            }
        };

        String[] columnTitles3 = {"Dependency"};
        int [] columnWidths3 = {300};

        globalJsDependencyTable = TableUtils.createTable(dependencyTabContainer, "Global Js Dependency", true, "Add Dependency", addMouseListener3, "Delete Dependency(ies)", deleteMouseListener3, 
                doubleClickListener, null, columnTitles3, columnWidths3, 150, false, 0);
        // populate table items
        if (globalJsDependencies != null)
        {
            for (String dependency : globalJsDependencies)
            {
                TableItem item = new TableItem (globalJsDependencyTable, SWT.NONE);
                item.setText(0, dependency);
            }
        }

        final String[]  globalCssDependencies = formConfigRuntime.getCssDependencies();
        addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem item = new TableItem (globalCssDependencyTable, SWT.NONE);
                int nextId = TableUtils.getNextId(globalCssDependencyTable, "/css", ".css");
                item.setText(0, "/css"+nextId+".css");
                // reset the form configuration document
                HashMap<String,Object> properties = new HashMap<String,Object>();
                properties.put("config-type", "dependencies");
                properties.put("css", item.getText(0));
                if ( formConfigRuntime.syncGlobalConfigs(properties) )
                {
                    formConfigRuntime.toXml();
                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
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
                TableItem[] items = globalCssDependencyTable.getItems();
                ArrayList<String> jsList = new ArrayList<String>();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        // remove global control
                        jsList.add(items[i].getText(0));
                    }
                }

                TableUtils.deleteCheckedItems(globalCssDependencyTable);

                if (formConfigRuntime.removeCssDependencies(jsList))
                {
                    // reset the form configuration document
                    formConfigRuntime.toXml();
                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };

        doubleClickListener = new MouseAdapter () 
        {
            public void mouseDown(MouseEvent e) 
            {                
                int index = globalCssDependencyTable.getSelectionIndex ();
                Point pt = new Point (e.x, e.y);
                final TableItem newItem = globalCssDependencyTable.getItem (pt);
                if (newItem == null) 
                {
                    if (dependencyEditor != null)
                    {
                        dependencyEditor.getEditor().dispose();
                    }
                    return;
                }
                globalCssDependencyTable.showSelection ();
                // make sure the double click case?
                if (dependencyEditor != null)
                {
                    dependencyEditor.getEditor().dispose();
                }
                dependencyEditor = new TableEditor (globalCssDependencyTable);
                Text urlText = new Text (globalCssDependencyTable, SWT.NONE);
                urlText.setText(newItem.getText(0));
                dependencyEditor.grabHorizontal = true;
                dependencyEditor.setEditor(urlText, newItem, 0);
                final int currentIndex = index;
                urlText.addModifyListener
                (
                        new ModifyListener() 
                        {
                            public void modifyText(ModifyEvent e) 
                            {
                                Text text = (Text) e.widget;
                                HashMap<String,Object> properties = new HashMap<String,Object>();
                                properties.put("config-type", "dependencies");
                                properties.put("css", formConfigRuntime.getCssDependencies()[currentIndex]);
                                properties.put("new-css", text.getText());
                                newItem.setText(text.getText());
                                if ( formConfigRuntime.syncGlobalConfigs(properties) )
                                {
                                    formConfigRuntime.toXml();
                                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                                }          
                            }
                        }
                );

                urlText.addFocusListener
                (
                        new FocusListener()
                        {
                            public void focusGained(FocusEvent arg0)
                            {
                            }
                            public void focusLost(FocusEvent arg0)
                            {
                                if (dependencyEditor != null)
                                {
                                    dependencyEditor.getEditor().dispose();
                                }
                                return;                                
                            }

                        }
                );

                urlText.setFocus();

            }
        };

        globalCssDependencyTable = TableUtils.createTable(dependencyTabContainer, "Global Css Dependency", true, "Add Dependency", addMouseListener, "Delete Dependency(ies)", deleteMouseListener, 
                doubleClickListener, null, columnTitles3, columnWidths3, 150, false, 0);
        // populate table items
        if (globalCssDependencies != null)
        {
            for (String dependency : globalCssDependencies)
            {
                TableItem item = new TableItem (globalCssDependencyTable, SWT.NONE);
                item.setText(0, dependency);
            }
        }

        dependencyTab.setControl(dependencyTabContainer);

        TabItem nodeTypeTab = new TabItem (tabFolder, SWT.NONE);
        nodeTypeTab.setText ("Node Type Forms");
        Composite controlTabContainer2 = FormSectionUtils.createStaticSectionClient(toolkit, tabFolder);
        HashMap<String, HashMap<String, FormConfigElement>>  nodeTypeForms = formConfigRuntime.getNodeTypeConfigForms();
        MouseListener addMouseListener4 = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem item = new TableItem (nodeTypeFormTable, SWT.NONE);
                int nextId = TableUtils.getNextId(nodeTypeFormTable, "type");
                item.setText(0, "type"+nextId);
                item.setText(1, "form"+nextId);
                // reset the form configuration document
                HashMap<String,Object> properties = new HashMap<String,Object>();
                properties.put("config-type", "node-type");
                properties.put("type-name", item.getText(0));
                properties.put("form-id", item.getText(1));                
                if ( formConfigRuntime.syncForm(properties) )
                {
                    formConfigRuntime.toXml();
                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                    
                    FormConfigElement nodeTypeConfig = formConfigRuntime.getNodeTypeConfigForm(item.getText(0), item.getText(1));
                    FormEditDialog nodeTypeFormEditDialog = new FormEditDialog(nodeTypeFormTable.getShell(),
                            modelRuntime.getService(),model,formConfigRuntime,formConfigXmlDoc,"node-type",
                            item.getText(0),nodeTypeConfig);
                    HashMap<String, Object> resultProperties = nodeTypeFormEditDialog.open();
                    if (resultProperties != null)
                    {
                        if (formConfigRuntime.syncForm(resultProperties))
                        {
                            formConfigRuntime.toXml();
                            formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                            
                            if (resultProperties.containsKey("new-type-name"))
                            {
                                item.setText(0, (String)resultProperties.get("new-type-name"));
                            }
                            if (resultProperties.containsKey("new-form-id"))
                            {
                                item.setText(1, (String)resultProperties.get("new-form-id"));
                            }
                        }
                    }
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };
        MouseListener deleteMouseListener4 = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem[] items = nodeTypeFormTable.getItems();
                HashMap<String,Object> properties = new HashMap<String,Object>();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        // remove global control
                        properties.put(items[i].getText(0), items[i].getText(1));
                    }
                }

                TableUtils.deleteCheckedItems(nodeTypeFormTable);

                if (formConfigRuntime.removeConstraintHandlers(properties))
                {
                    // reset the form configuration document
                    formConfigRuntime.toXml();
                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };

        Listener clickListener4 = new Listener() 
        {
            public void handleEvent(Event event) 
            {
                Point pt = new Point(event.x, event.y);
                TableItem item = nodeTypeFormTable.getItem(pt);
                if (item == null)
                    return;
                for (int i = 0; i < 2; i++) 
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        FormConfigElement nodeTypeConfig = formConfigRuntime.getNodeTypeConfigForm(item.getText(0), item.getText(1));
                        FormEditDialog nodeTypeFormEditDialog = new FormEditDialog(nodeTypeFormTable.getShell(),
                                modelRuntime.getService(),model,formConfigRuntime,formConfigXmlDoc,"node-type",
                                item.getText(0),nodeTypeConfig);
                        HashMap<String, Object> properties = nodeTypeFormEditDialog.open();
                        if (properties != null)
                        {
                            if (formConfigRuntime.syncForm(properties))
                            {
                                formConfigRuntime.toXml();
                                formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                                
                                if (properties.containsKey("new-type-name"))
                                {
                                    item.setText(0, (String)properties.get("new-type-name"));
                                }
                                if (properties.containsKey("new-form-id"))
                                {
                                    item.setText(1, (String)properties.get("new-form-id"));
                                }
                            }
                        }
                    }
                }
            }
        };
        String[] columnTitles4 = {"Content Type", "Form Id"};
        int [] columnWidths4 = {125};

        nodeTypeFormTable = TableUtils.createTable(controlTabContainer2, "Node Type Forms", true, "Add Form", addMouseListener4, "Delete Form(s)", deleteMouseListener4, 
                null, clickListener4, columnTitles4, columnWidths4, 150, false, 0);
        // populate table items
        if (nodeTypeForms!=null)
        {
            for (String typeName : nodeTypeForms.keySet())
            {
                for (String formId : nodeTypeForms.get(typeName).keySet())
                {
                    TableItem item = new TableItem (nodeTypeFormTable, SWT.NONE);
                    item.setText(0, typeName);
                    item.setText(1, formId);
                }
            }
        }
        nodeTypeTab.setControl(controlTabContainer2);

        TabItem modelTypeTab = new TabItem (tabFolder, SWT.NONE);
        modelTypeTab.setText ("Model Type Forms");

        Composite controlTabContainer3 = FormSectionUtils.createStaticSectionClient(toolkit, tabFolder);
        HashMap<String, HashMap<String, FormConfigElement>>  modelTypeForms = formConfigRuntime.getModelTypeConfigForms();
        MouseListener addMouseListener5 = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem item = new TableItem (modelTypeFormTable, SWT.NONE);
                int nextId = TableUtils.getNextId(modelTypeFormTable, "type");
                item.setText(0, "type"+nextId);
                item.setText(1, "form"+nextId);
                // reset the form configuration document
                HashMap<String,Object> properties = new HashMap<String,Object>();
                properties.put("config-type", "model-type");
                properties.put("type-name", item.getText(0));
                properties.put("form-id", item.getText(1));                
                if ( formConfigRuntime.syncForm(properties) )
                {
                    formConfigRuntime.toXml();
                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                    
                    FormConfigElement modelTypeConfig = formConfigRuntime.getModelTypeConfigForm(item.getText(0), item.getText(1));
                    FormEditDialog modelTypeFormEditDialog = new FormEditDialog(modelTypeFormTable.getShell(),
                            modelRuntime.getService(),model,formConfigRuntime,formConfigXmlDoc,"model-type",
                            item.getText(0),modelTypeConfig);
                    HashMap<String, Object> resultProperties = modelTypeFormEditDialog.open();
                    if (resultProperties != null)
                    {
                        if (formConfigRuntime.syncForm(resultProperties))
                        {
                            formConfigRuntime.toXml();
                            formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                            
                            if (resultProperties.containsKey("new-type-name"))
                            {
                                item.setText(0, (String)resultProperties.get("new-type-name"));
                            }
                            if (resultProperties.containsKey("new-form-id"))
                            {
                                item.setText(1, (String)resultProperties.get("new-form-id"));
                            }
                        }
                    }
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };
        MouseListener deleteMouseListener5 = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem[] items = modelTypeFormTable.getItems();
                HashMap<String,Object> properties = new HashMap<String,Object>();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        // remove global control
                        properties.put(items[i].getText(0), items[i].getText(1));
                    }
                }

                TableUtils.deleteCheckedItems(modelTypeFormTable);

                if (formConfigRuntime.removeConstraintHandlers(properties))
                {
                    // reset the form configuration document
                    formConfigRuntime.toXml();
                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };

        Listener clickListener5 = new Listener() 
        {
            public void handleEvent(Event event) 
            {
                Point pt = new Point(event.x, event.y);
                TableItem item = modelTypeFormTable.getItem(pt);
                if (item == null)
                    return;
                for (int i = 0; i < 2; i++) 
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        FormConfigElement modelTypeConfig = formConfigRuntime.getModelTypeConfigForm(item.getText(0), item.getText(1));
                        FormEditDialog modelTypeFormEditDialog = new FormEditDialog(modelTypeFormTable.getShell(),
                                modelRuntime.getService(),model,formConfigRuntime,formConfigXmlDoc,"model-type",
                                item.getText(0),modelTypeConfig);
                        HashMap<String, Object> properties = modelTypeFormEditDialog.open();
                        if (properties != null)
                        {
                            if (formConfigRuntime.syncForm(properties))
                            {
                                formConfigRuntime.toXml();
                                formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                                
                                if (properties.containsKey("new-type-name"))
                                {
                                    item.setText(0, (String)properties.get("new-type-name"));
                                }
                                if (properties.containsKey("new-form-id"))
                                {
                                    item.setText(1, (String)properties.get("new-form-id"));
                                }
                            }
                        }
                    }
                }
            }
        };

        modelTypeFormTable = TableUtils.createTable(controlTabContainer3, "Node Type Forms", true, "Add Form", addMouseListener5, "Delete Form(s)", deleteMouseListener5, 
                null, clickListener5, columnTitles4, columnWidths4, 150, false, 0);
        // populate table items
        if (modelTypeForms!=null)
        {
            for (String typeName : modelTypeForms.keySet())
            {
                for (String formId : modelTypeForms.get(typeName).keySet())
                {
                    TableItem item = new TableItem (modelTypeFormTable, SWT.NONE);
                    item.setText(0, typeName);
                    item.setText(1, formId);
                }
            }
        }
        modelTypeTab.setControl(controlTabContainer3);
        
        TabItem aspectTab = new TabItem (tabFolder, SWT.NONE);
        aspectTab.setText ("Aspect Forms");
        
        Composite controlTabContainer4 = FormSectionUtils.createStaticSectionClient(toolkit, tabFolder);
        HashMap<String, HashMap<String, FormConfigElement>>  aspectForms = formConfigRuntime.getAspectConfigForms();
        MouseListener addMouseListener6 = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem item = new TableItem (aspectFormTable, SWT.NONE);
                int nextId = TableUtils.getNextId(aspectFormTable, "type");
                item.setText(0, "type"+nextId);
                item.setText(1, "form"+nextId);
                // reset the form configuration document
                HashMap<String,Object> properties = new HashMap<String,Object>();
                properties.put("config-type", "aspect");
                properties.put("type-name", item.getText(0));
                properties.put("form-id", item.getText(1));                
                if ( formConfigRuntime.syncForm(properties) )
                {
                    formConfigRuntime.toXml();
                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                    
                    FormConfigElement aspectConfig = formConfigRuntime.getAspectConfigForm(item.getText(0), item.getText(1));
                    FormEditDialog aspectFormEditDialog = new FormEditDialog(aspectFormTable.getShell(),
                            modelRuntime.getService(),model,formConfigRuntime,formConfigXmlDoc,"aspect",
                            item.getText(0),aspectConfig);
                    HashMap<String, Object> resultProperties = aspectFormEditDialog.open();
                    if (resultProperties != null)
                    {
                        if (formConfigRuntime.syncForm(resultProperties))
                        {
                            formConfigRuntime.toXml();
                            formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                            
                            if (resultProperties.containsKey("new-type-name"))
                            {
                                item.setText(0, (String)resultProperties.get("new-type-name"));
                            }
                            if (resultProperties.containsKey("new-form-id"))
                            {
                                item.setText(1, (String)resultProperties.get("new-form-id"));
                            }
                        }
                    }
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };
        MouseListener deleteMouseListener6 = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                TableItem[] items = aspectFormTable.getItems();
                HashMap<String,Object> properties = new HashMap<String,Object>();

                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        // remove global control
                        properties.put(items[i].getText(0), items[i].getText(1));
                    }
                }

                TableUtils.deleteCheckedItems(aspectFormTable);

                if (formConfigRuntime.removeConstraintHandlers(properties))
                {
                    // reset the form configuration document
                    formConfigRuntime.toXml();
                    formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                }
            }
            @Override
            public void mouseUp(MouseEvent arg0)
            {
            }            
        };

        Listener clickListener6 = new Listener() 
        {
            public void handleEvent(Event event) 
            {
                Point pt = new Point(event.x, event.y);
                TableItem item = aspectFormTable.getItem(pt);
                if (item == null)
                    return;
                for (int i = 0; i < 2; i++) 
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        FormConfigElement aspectConfig = formConfigRuntime.getAspectConfigForm(item.getText(0), item.getText(1));
                        FormEditDialog aspectFormEditDialog = new FormEditDialog(aspectFormTable.getShell(),
                                modelRuntime.getService(),model,formConfigRuntime,formConfigXmlDoc,"aspect",
                                item.getText(0),aspectConfig);
                        HashMap<String, Object> properties = aspectFormEditDialog.open();
                        if (properties != null)
                        {
                            if (formConfigRuntime.syncForm(properties))
                            {
                                formConfigRuntime.toXml();
                                formConfigXmlDoc.set(formConfigRuntime.getConfigXml());
                                
                                if (properties.containsKey("new-type-name"))
                                {
                                    item.setText(0, (String)properties.get("new-type-name"));
                                }
                                if (properties.containsKey("new-form-id"))
                                {
                                    item.setText(1, (String)properties.get("new-form-id"));
                                }
                            }
                        }
                    }
                }
            }
        };

        aspectFormTable = TableUtils.createTable(controlTabContainer4, "Aspect Forms", true, "Add Form", addMouseListener6, "Delete Form(s)", deleteMouseListener6, 
                null, clickListener6, columnTitles4, columnWidths4, 150, false, 0);
        // populate table items
        if (aspectForms!=null)
        {
            for (String typeName : aspectForms.keySet())
            {
                for (String formId : aspectForms.get(typeName).keySet())
                {
                    TableItem item = new TableItem (aspectFormTable, SWT.NONE);
                    item.setText(0, typeName);
                    item.setText(1, formId);
                }
            }
        }
        aspectTab.setControl(controlTabContainer4);
        

        tabFolder.pack();
        formSection.setClient(formSectionContainer);
        
        Composite  body = form.getBody();
        
        body.layout(true);
        body.redraw();
        body.pack();
        form.reflow(true);
    }

    /**
     * Initializes the overview page
     */
    protected void init()
    {
        modelRuntime = new ModelRuntime(document.get());

        if (webclientConfigXmlDoc != null)
        {
            webConfigRuntime = new WebConfigRuntime(webclientConfigXmlDoc.get());
            webConfigRuntime.initWebConfigRuntime();
        }

        if (formConfigXmlDoc != null)
        {
            formConfigRuntime = new FormConfigRuntime(formConfigXmlDoc.get());
            formConfigRuntime.initConfigRuntime();
        }

        buildHeadSection("Model Design");

        Composite  body = form.getBody();

        try
        {
            if (modelRuntime.initModelRuntime(getFileModelListProperty()))
            {
                buildLeftSection();
                buildRightSection();
            }
        }
        catch(Exception e)
        {
            logger.log(Level.SEVERE, "Failed to initialize model runtime.", e);
            addErrorMessage(e);
        }
        body.pack();
        body.layout(true);
        body.redraw();
        form.reflow(true);
    }

    /**
     * Resets the document
     */
    protected void resetDocument(String str)
    {
        document.set(str);
    }
}
