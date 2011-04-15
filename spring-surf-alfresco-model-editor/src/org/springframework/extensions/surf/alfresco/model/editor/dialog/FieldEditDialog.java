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
import java.util.LinkedHashMap;
import java.util.Map;

import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.config.forms.ConstraintHandlerDefinition;
import org.alfresco.web.config.forms.Control;
import org.alfresco.web.config.forms.ControlParam;
import org.alfresco.web.config.forms.FormConfigElement;
import org.alfresco.web.config.forms.FormConfigRuntime;
import org.alfresco.web.config.forms.FormConfigUtils;
import org.alfresco.web.config.forms.FormField;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
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
public class FieldEditDialog extends AbstractModelDialog
{
    protected FormConfigElement formConfig;
    protected FormField field;
    protected FormField updatedField;
    protected Control control;
    protected Map<String, ConstraintHandlerDefinition> constraintHandlers;

    protected CCombo fieldIdCombo;
    protected CCombo fieldSetCombo;
    protected Text controlTemplateText;
    protected Table parameterTable;
    protected Table constraintHandlerTable;

    protected Text labelText;
    protected Text labelIdText;
    protected Text descriptionText;
    protected Text descriptionIdText;
    protected Text helpText;
    protected Text helpIdText;
    //protected CCombo readOnlyCombo;
    //protected CCombo mandatoryCombo;
    
    protected Button[] readOnlyButtons;
    protected Button[] mandatoryButtons;
    

    /**
     * Constructor with parameters shell, style and Class
     * 
     * @param parent
     * @param style
     * @param clazz
     * @param service
     * @param model
     */
    public FieldEditDialog(Shell parent, int style, DictionaryService service, M2Model model, FormConfigRuntime formConfigRuntime,IDocument formConfigIDocument, FormConfigElement formConfig, FormField field)
    {
        super(parent, style,service,model,formConfigRuntime,formConfigIDocument);
        this.formConfig = formConfig;
        this.field = field;
        this.control = field.getControl();
        this.constraintHandlers = new LinkedHashMap<String, ConstraintHandlerDefinition>(4);

        if (field.getConstraintDefinitionMap()!=null)
        {
            for (ConstraintHandlerDefinition defn : field.getConstraintDefinitionMap().values())
            {
                this.constraintHandlers.put(defn.getType(), defn);
            }
        }
    }

    /**
     * Constructor with parameters shell and Class
     * 
     * @param parent
     * @param clazz
     * @param service
     * @param model
     */
    public FieldEditDialog(Shell parent, DictionaryService service, M2Model model, FormConfigRuntime formConfigRuntime,IDocument formConfigIDocument,FormConfigElement formConfig, FormField field)
    {
        this(parent, SWT.PRIMARY_MODAL, service, model,formConfigRuntime,formConfigIDocument,formConfig, field);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.dialog.AbstractModelDialog#buildLeftSection()
     */
    protected void buildLeftSection ()
    {
        super.buildLeftSection();

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;       

        GridData labelGridData = new GridData();
        labelGridData.verticalAlignment = SWT.TOP;
        labelGridData.horizontalAlignment = SWT.FILL;

        Section basicInfoSection = FormSectionUtils.createStaticSection(toolkit,left,"General Information","Form Field General Information");        
        Composite  basicInfoSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, basicInfoSection);

        // build basic information section
        Composite basicInfo = toolkit.createComposite(basicInfoSectionContainer);        
        basicInfo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        basicInfo.setLayout(new GridLayout(2, false));

        // build short name text field
        Label label = new Label(basicInfo, SWT.NULL);
        label.setText("&Field ID:");

        fieldIdCombo = new CCombo(basicInfo, SWT.FLAT | SWT.BORDER);
        fieldIdCombo.setLayoutData(gridData);
        fieldIdCombo.setText(field.getId());
        for (QName aspect : this.getService().getAllAspects())
        {
            for (QName property : this.getService().getAspect(aspect).getProperties().keySet())
            {
                fieldIdCombo.add(property.getPrefixString());
            }
        }
        for (QName type : this.getService().getAllTypes())
        {
            for (QName property : this.getService().getType(type).getProperties().keySet())
            {
                fieldIdCombo.add(property.getPrefixString());
            }
        }        

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Set:");

        fieldSetCombo = new CCombo(basicInfo, SWT.FLAT | SWT.BORDER);
        fieldSetCombo.setLayoutData(gridData);
        if (field.getSet() != null)
        {
            fieldSetCombo.setText(field.getSet());
        }
        for (String setId : formConfig.getSetIDs())
        {
            fieldSetCombo.add(setId);
        }

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Template:");
        controlTemplateText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        controlTemplateText.setLayoutData(gridData);
        if (control.getTemplate() != null)
        {
            controlTemplateText.setText(control.getTemplate());
        }

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Description:");
        label.setLayoutData(labelGridData);
        descriptionText = new Text(basicInfo, SWT.BORDER | SWT.WRAP | SWT.MULTI);
        GC gc = new GC(descriptionText);
        FontMetrics fm = gc.getFontMetrics ();
        int height = 10 * fm.getHeight();
        GridData textAreaGridData = new GridData();
        textAreaGridData.horizontalAlignment = SWT.FILL;
        textAreaGridData.grabExcessHorizontalSpace = true;
        textAreaGridData.verticalAlignment = SWT.FILL;
        textAreaGridData.grabExcessVerticalSpace = true;
        textAreaGridData.heightHint = height;
        gc.dispose();
        descriptionText.setLayoutData(textAreaGridData);
        if (field.getAttributes().containsKey("description") && field.getAttributes().get("description")!=null)
        {
            descriptionText.setText(field.getAttributes().get("description"));
        }

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Description ID:");
        descriptionIdText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        descriptionIdText.setLayoutData(gridData);
        if (field.getAttributes().containsKey("description-id") && field.getAttributes().get("description-id")!=null)
        {
            descriptionIdText.setText(field.getAttributes().get("description-id"));
        }

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Help:");
        label.setLayoutData(labelGridData);
        helpText = new Text(basicInfo, SWT.BORDER | SWT.WRAP | SWT.MULTI);
        helpText.setLayoutData(textAreaGridData);
        if (field.getAttributes().containsKey("help") && field.getAttributes().get("help")!=null)
        {
            helpText.setText(field.getAttributes().get("help"));
        }

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Help ID:");
        helpIdText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        helpIdText.setLayoutData(gridData);
        if (field.getAttributes().containsKey("help-id") && field.getAttributes().get("help-id")!=null)
        {
            helpIdText.setText(field.getAttributes().get("help-id"));
        }

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Readonly:");

        /*
        readOnlyCombo = new CCombo(basicInfo, SWT.READ_ONLY | SWT.FLAT | SWT.BORDER);
        readOnlyCombo.setLayoutData(gridData);
        if (field.getAttributes().containsKey("read-only") && field.getAttributes().get("read-only")!=null)
        {
            readOnlyCombo.setText(field.getAttributes().get("read-only"));
        }
        readOnlyCombo.add("true");
        readOnlyCombo.add("false");
        */
        Composite readOnlyComposite = new Composite(basicInfo, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        readOnlyComposite.setLayout(layout);

        readOnlyButtons = new Button[3];
        readOnlyButtons[0] = new Button (readOnlyComposite, SWT.RADIO);
        readOnlyButtons[0].setText("True");
        readOnlyButtons[1] = new Button (readOnlyComposite, SWT.RADIO);
        readOnlyButtons[1].setText("False");
        readOnlyButtons[2] = new Button (readOnlyComposite, SWT.RADIO);
        readOnlyButtons[2].setText("None");
        Listener buttonListener2 = new Listener () 
        {
            public void handleEvent (Event event) 
            {                
                for (Button button : readOnlyButtons) 
                {
                    button.setSelection(false);
                }
                Widget eventWidget = event.widget;
                if (eventWidget instanceof Button)
                {
                    ((Button) eventWidget).setSelection(true);
                }
            }
        };

        for (Button button : readOnlyButtons) 
        {
            button.addListener (SWT.Selection,buttonListener2);
        }
        
        if (field.getAttributes().containsKey("read-only") && field.getAttributes().get("read-only")!=null)
        {           
            if (field.getAttributes().get("read-only").equals("true"))
            {
                readOnlyButtons[0].setSelection(true);
            }
            else
            {
                readOnlyButtons[1].setSelection(true);
            }
        }
        else
        {
            readOnlyButtons[2].setSelection(true);
        }
        
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Mandatory:");

        /*
        mandatoryCombo = new CCombo(basicInfo, SWT.READ_ONLY | SWT.FLAT | SWT.BORDER);
        mandatoryCombo.setLayoutData(gridData);
        if (field.getAttributes().containsKey("mandatory") && field.getAttributes().get("mandatory")!=null)
        {
            mandatoryCombo.setText(field.getAttributes().get("mandatory"));
        }
        mandatoryCombo.add("true");
        mandatoryCombo.add("false");
        */
        Composite mandatoryComposite = new Composite(basicInfo, SWT.NULL);
        mandatoryComposite.setLayout(layout);

        mandatoryButtons = new Button[3];
        mandatoryButtons[0] = new Button (mandatoryComposite, SWT.RADIO);
        mandatoryButtons[0].setText("True");
        mandatoryButtons[1] = new Button (mandatoryComposite, SWT.RADIO);
        mandatoryButtons[1].setText("False");
        mandatoryButtons[2] = new Button (mandatoryComposite, SWT.RADIO);
        mandatoryButtons[2].setText("None");
        Listener buttonListener3 = new Listener () 
        {
            public void handleEvent (Event event) 
            {                
                for (Button button : mandatoryButtons) 
                {
                    button.setSelection(false);
                }
                Widget eventWidget = event.widget;
                if (eventWidget instanceof Button)
                {
                    ((Button) eventWidget).setSelection(true);
                }
            }
        };

        for (Button button : mandatoryButtons) 
        {
            button.addListener (SWT.Selection,buttonListener3);
        }
        
        if (field.getAttributes().containsKey("mandatory") && field.getAttributes().get("mandatory")!=null)
        {           
            if (field.getAttributes().get("mandatory").equals("true"))
            {
                mandatoryButtons[0].setSelection(true);
            }
            else
            {
                mandatoryButtons[1].setSelection(true);
            }
        }
        else
        {
            mandatoryButtons[2].setSelection(true);
        }

        basicInfoSection.setClient(basicInfoSectionContainer);

    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.dialog.AbstractModelDialog#buildRightSection()
     */
    protected void buildRightSection ()
    {
        super.buildRightSection();

        // setup namespace section
        Section propertySection = FormSectionUtils.createStaticSection(toolkit,right,"Control Parameters","View or Update Control Parameters.");

        final Composite  propertySectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, propertySection);

        // build basic information section
        Composite config = toolkit.createComposite(propertySectionContainer);        
        config.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        config.setLayout(new GridLayout(2, false));

        MouseListener addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                int counter = parameterTable.getItemCount();
                boolean isNewKeyUsed = false;
                do
                {
                    for (TableItem ti : parameterTable.getItems())
                    {
                        if (ti.getText(0).equals("name"+counter))
                        {
                            isNewKeyUsed = true;
                            counter++;
                        }
                    } 
                } 
                while (isNewKeyUsed);

                TableItem item = new TableItem (parameterTable, SWT.NONE);
                item.setText(0, "name"+counter);
                item.setText(1, "value"+counter);
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
                TableUtils.deleteCheckedItems(parameterTable);              
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
                TableItem item = parameterTable.getItem(pt);
                if (item == null)
                {
                    return;
                }
                for (int i = 0; i < 2; i++) 
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        ControlParam param = new ControlParam(item.getText(0),item.getText(1));
                        ParameterEditDialog paramEditDialog = new ParameterEditDialog(parameterTable.getShell(),
                                getService(),getModel(),getFormConfigRuntime(),getFormConfigIDocument(), new ControlParam(item.getText(0),item.getText(1)));
                        ControlParam updatedParam = paramEditDialog.open();
                        if (updatedParam != null && !param.equals(updatedParam))
                        {
                            item.setText(0, updatedParam.getName());
                            item.setText(1, updatedParam.getValue());
                        }
                    }
                }
            }
        };

        String [] columnTitles = {"Name","Value"};
        int []columnWidths = {3};

        parameterTable = TableUtils.createTable(propertySectionContainer, "Parameters", true, "Add Parameter", addMouseListener, "Delete Parameter(s)", deleteMouseListener,
                null, clickListener, columnTitles, columnWidths, 100, false, 0);

        // populate table items
        if (control.getParams() != null)
        {
            for (ControlParam cp : control.getParams()) 
            {
                TableItem item = new TableItem (parameterTable, SWT.NONE);
                item.setText(0, cp.getName());
                item.setText(1, cp.getValue());
            }
        }

        propertySection.setClient(propertySectionContainer);

        // setup namespace section
        Section constraintHandlerSection = FormSectionUtils.createStaticSection(toolkit,right,"Constraint Handlers","View or Update Constraint Handlers.");

        final Composite  constraintHandlerSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, constraintHandlerSection);

        MouseListener addMouseListener2 = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                int counter = constraintHandlerTable.getItemCount();
                boolean isNewKeyUsed = false;

                do
                {
                    for (TableItem ti : constraintHandlerTable.getItems())
                    {
                        if (ti.getText(0).equals("constraint"+counter))
                        {
                            isNewKeyUsed = true;
                            counter++;
                        }
                    } 
                } 
                while (isNewKeyUsed);

                TableItem item = new TableItem (constraintHandlerTable, SWT.NONE);
                item.setText(0, "constraint"+counter);
                item.setText(1, "handler"+counter);
                constraintHandlers.put("constraint"+counter, new ConstraintHandlerDefinition("constraint"+counter, "handler"+counter, null,null,null));
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
                TableItem[] items = constraintHandlerTable.getItems();
                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        constraintHandlers.remove(items[i].getText(0));
                    }
                }

                TableUtils.deleteCheckedItems(constraintHandlerTable);
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
                TableItem item = constraintHandlerTable.getItem(pt);
                if (item == null)
                    return;
                for (int i = 0; i < 2; i++) 
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        GlobalConstraintHandlerEditDialog globalConstraintHandlerEditDialog = new GlobalConstraintHandlerEditDialog(constraintHandlerTable.getShell(),
                                getService(),getModel(),getFormConfigRuntime(),getFormConfigIDocument(),item.getText(0), constraintHandlers.get(item.getText(0)));
                        HashMap<String, Object> properties = globalConstraintHandlerEditDialog.open();
                        if (properties != null)
                        {
                            if (properties.get("constraint") != null && properties.get("constraint") instanceof ConstraintHandlerDefinition)
                            {
                                constraintHandlers.remove(item.getText(0));
                                if (properties.containsKey("new-constraint-type-name"))
                                {
                                    item.setText(0, (String)properties.get("new-constraint-type-name"));
                                }
                                item.setText(1, ((ConstraintHandlerDefinition)properties.get("constraint")).getValidationHandler());                            
                                constraintHandlers.put(item.getText(0),(ConstraintHandlerDefinition)properties.get("constraint"));
                            }
                        }
                    }
                }
            }
        };
        String[] columnTitles2 = {"Type", "Validation Handler"};
        int [] columnWidths2 = {125};

        constraintHandlerTable = TableUtils.createTable(constraintHandlerSectionContainer, "Constraint Handlers", true, "Add Handler", addMouseListener2, "Delete Handlers(s)", deleteMouseListener2, 
                null, clickListener2, columnTitles2, columnWidths2, 100, false, 0);
        if (constraintHandlers!=null)
        {
            // populate table items
            for (String typeName : constraintHandlers.keySet())
            {
                TableItem item = new TableItem (constraintHandlerTable, SWT.NONE);
                item.setText(0, typeName);
                if (constraintHandlers.get(typeName).getValidationHandler() != null)
                {
                    item.setText(1,constraintHandlers.get(typeName).getValidationHandler());
                }
            }
        } 
        constraintHandlerSection.setClient(constraintHandlerSectionContainer);
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
                if (fieldIdCombo.getText() != null)
                {
                    HashMap<String, String> attributes = new HashMap<String, String>();
                    if (descriptionText.getText() != null)
                    {
                        attributes.put("description", descriptionText.getText());
                    }
                    if (descriptionIdText.getText() != null)
                    {
                        attributes.put("description-id", descriptionIdText.getText());
                    }
                    if (helpText.getText() != null)
                    {
                        attributes.put("help", helpText.getText());
                    }
                    if (helpIdText.getText() != null)
                    {
                        attributes.put("help-id", helpIdText.getText());
                    }
                    /*
                    if (readOnlyCombo.getText() != null)
                    {
                        attributes.put("read-only", readOnlyCombo.getText());
                    }
                    */
                    if (readOnlyButtons[0].getSelection())
                    {
                        attributes.put("read-only", "true");
                    }
                    if (readOnlyButtons[1].getSelection())
                    {
                        attributes.put("read-only", "false");
                    }
                    /*
                    if (mandatoryCombo.getText() != null)
                    {
                        attributes.put("mandatory", mandatoryCombo.getText());
                    }
                    */
                    if (mandatoryButtons[0].getSelection())
                    {
                        attributes.put("mandatory", "true");
                    }
                    if (mandatoryButtons[1].getSelection())
                    {
                        attributes.put("mandatory", "false");
                    }
                    if (fieldSetCombo.getText() != null && !fieldSetCombo.getText().endsWith(""))
                    {
                        attributes.put("set", fieldSetCombo.getText());
                    }
                    updatedField = new FormField(fieldIdCombo.getText(),attributes);
                    if (controlTemplateText.getText() != null)
                    {
                        FormConfigUtils.setControlTemplate(updatedField.getControl(),controlTemplateText.getText());
                    }
                    Control control = updatedField.getControl();
                    for ( TableItem tableItem : parameterTable.getItems())
                    {
                        FormConfigUtils.addControlParam(control, tableItem.getText(0), tableItem.getText(1));
                    }
                    for (ConstraintHandlerDefinition value : constraintHandlers.values())
                    {
                        FormConfigUtils.addConstraintDefinition(updatedField, value.getType(), value.getMessage(), value.getMessageId(), value.getValidationHandler(), value.getEvent());
                    }
                }
                shell.close();
            }
        });

        cancel.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                updatedField = null;
                shell.close();
            }
        });                

    }

    /**
     * @return
     */
    public FormField open()
    {
        initUI("Form Field Editor");
        buildHeadSection();
        buildLeftSection();
        buildRightSection();
        buildBottomSection();
        addProcessingListeners();
        openUI(1200,700);
        return updatedField;
    }

}
