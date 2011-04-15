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
import java.util.List;
import java.util.Map;

import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.config.forms.FieldVisibilityInstructionCustom;
import org.alfresco.web.config.forms.FormConfigElement;
import org.alfresco.web.config.forms.FormConfigRuntime;
import org.alfresco.web.config.forms.FormConfigUtils;
import org.alfresco.web.config.forms.FormField;
import org.alfresco.web.config.forms.FormSet;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
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
public class FormEditDialog extends AbstractModelDialog
{

    private String configType;
    private String typeName;

    private FormConfigElement formConfig;    
    private FormConfigElement updatedFormConfig;

    private Map<String, FormSet> sets = new LinkedHashMap<String, FormSet>(4);
    private Map<String, FormField> fields = new LinkedHashMap<String, FormField>(8);
    private Map<String, FieldVisibilityInstructionCustom> fvInstructions = new LinkedHashMap<String, FieldVisibilityInstructionCustom>(8);

    private HashMap<String,Object> properties;
    private Text configTypeText;
    private CCombo typeNameCombo;
    private Text formIdText;

    private Text submissionURLText;    
    private Text createTemplateText;
    private Text editTemplateText;
    private Text viewTemplateText;

    private Table fieldVisibilityTable;

    private Table setTable;
    private Table fieldTable;

    /**
     * Constructor with parameters shell, style and Class
     * 
     * @param parent
     * @param style
     * @param clazz
     * @param service
     * @param model
     */
    public FormEditDialog(Shell parent, int style, DictionaryService service, M2Model model, FormConfigRuntime formConfigRuntime,IDocument formConfigIDocument, 
            String configType, String typeName,FormConfigElement formConfig)
    {
        super(parent, style,service,model,formConfigRuntime,formConfigIDocument);
        this.configType = configType;
        this.typeName = typeName;
        this.formConfig = formConfig;
        this.properties = new HashMap<String,Object> ();
        this.properties.put("config-type", configType);
        this.properties.put("type-name", typeName);
        this.properties.put("form-id", formConfig.getId()==null?"default":formConfig.getId());

        for (FormField formField : formConfig.getFields().values())
        {
            fields.put(formField.getId(), formField);
        }

        for (FormSet formSet : formConfig.getSets().values())
        {
            sets.put(formSet.getSetId(), formSet);
        }

        List<FieldVisibilityInstructionCustom> elemList= getFormConfigRuntime().getFieldVisibilityInstructions(configType, typeName, (String)properties.get("form-id"));
        if (elemList != null)
        {
            for (FieldVisibilityInstructionCustom fieldVisibilityElem : elemList)
            {
                FieldVisibilityInstructionCustom fvInstruction = new FieldVisibilityInstructionCustom(
                        fieldVisibilityElem.getShowOrHide() == null?null:fieldVisibilityElem.getShowOrHide().toString().toLowerCase(),
                                fieldVisibilityElem.getFieldId(),
                                fieldVisibilityElem.getModesString(),fieldVisibilityElem.getForce());
                fvInstructions.put(fieldVisibilityElem.getFieldId(), fvInstruction);                
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
    public FormEditDialog(Shell parent, DictionaryService service, M2Model model, FormConfigRuntime formConfigRuntime,IDocument formConfigIDocument,
            String configType, String typeName,FormConfigElement formConfig)
    {
        this(parent, SWT.PRIMARY_MODAL, service, model,formConfigRuntime,formConfigIDocument, configType, typeName, formConfig);
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

        Section basicInfoSection = FormSectionUtils.createStaticSection(toolkit,left,"General Information","Control General Information");        
        Composite  basicInfoSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, basicInfoSection);

        // build basic information section
        Composite basicInfo = toolkit.createComposite(basicInfoSectionContainer);        
        basicInfo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        basicInfo.setLayout(new GridLayout(2, false));

        // build short name text field
        Label label = new Label(basicInfo, SWT.NULL);
        label.setText("&Config Type:");

        configTypeText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        configTypeText.setLayoutData(gridData);
        configTypeText.setText(configType);
        configTypeText.setEnabled(false);

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Type Name:");

        typeNameCombo = new CCombo(basicInfo, SWT.FLAT | SWT.BORDER);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        typeNameCombo.setLayoutData(gridData);
        typeNameCombo.setText(typeName);
        for (QName m2t : this.getService().getAllTypes())
        {
            typeNameCombo.add(m2t.getPrefixString());
        }
        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Form ID:");

        formIdText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        formIdText.setLayoutData(gridData);

        formIdText.setText((String)properties.get("form-id"));

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Submission URL:");

        submissionURLText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        submissionURLText.setLayoutData(gridData);
        if (formConfig.getSubmissionURL() != null)
        {
            submissionURLText.setText(formConfig.getSubmissionURL());
        }

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Edit Template:");

        editTemplateText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        editTemplateText.setLayoutData(gridData);
        if (formConfig.getEditTemplate() != null)
        {
            editTemplateText.setText(formConfig.getEditTemplate());
        }

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Create Template:");

        createTemplateText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        createTemplateText.setLayoutData(gridData);
        if (formConfig.getCreateTemplate() != null)
        {
            createTemplateText.setText(formConfig.getCreateTemplate());
        }

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&View Template:");

        viewTemplateText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        viewTemplateText.setLayoutData(gridData);
        if (formConfig.getViewTemplate() != null)
        {
            viewTemplateText.setText(formConfig.getViewTemplate());
        }

        basicInfoSection.setClient(basicInfoSectionContainer);

        // setup namespace section
        Section fieldVisibilitySection = FormSectionUtils.createStaticSection(toolkit,left,"Field Visibility","View or Update Field Visibility Configurations.");

        final Composite  fieldVisibilityContainer = FormSectionUtils.createStaticSectionClient(toolkit, fieldVisibilitySection);
        MouseListener addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                int counter = fieldVisibilityTable.getItemCount();
                boolean isNewKeyUsed = false;
                do
                {
                    for (TableItem ti : fieldTable.getItems())
                    {
                        if (ti.getText(0).equals("field"+counter))
                        {
                            isNewKeyUsed = true;
                        }
                    } 
                    for (TableItem ti : fieldVisibilityTable.getItems())
                    {
                        if (ti.getText(0).equals("field"+counter))
                        {
                            isNewKeyUsed = true;
                        }
                    } 
                    if (isNewKeyUsed)
                    {
                        counter++;
                        isNewKeyUsed = false;
                    }
                } 
                while (isNewKeyUsed);

                FieldVisibilityInstructionCustom fvInstruction = new FieldVisibilityInstructionCustom("show","field"+counter,"","false");
                TableItem item = new TableItem (fieldVisibilityTable, SWT.NONE);
                item.setText(0, fvInstruction.getFieldId());
                item.setText(1, fvInstruction.getShowOrHide().toString().toLowerCase());
                item.setText(2, fvInstruction.getModesString());
                item.setText(3, fvInstruction.getForce());
                fvInstructions.put(fvInstruction.getFieldId(), fvInstruction);  
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
                TableItem[] items = fieldVisibilityTable.getItems();
                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        fvInstructions.remove(items[i].getText(0));
                    }
                }
                TableUtils.deleteCheckedItems(fieldVisibilityTable);				
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
                TableItem item = fieldVisibilityTable.getItem(pt);
                if (item == null)
                {
                    return;
                }
                for (int i = 0; i < 4; i++) 
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        FieldVisibilityInstructionCustom fvInstruction = fvInstructions.get(item.getText(0));
                        FieldVisibilityEditDialog fvEditorDialog = new FieldVisibilityEditDialog(fieldVisibilityTable.getShell(),
                                getService(),getModel(),getFormConfigRuntime(),getFormConfigIDocument(),fvInstruction);
                        FieldVisibilityInstructionCustom  updatedFVInstruction = fvEditorDialog.open();
                        if (updatedFVInstruction != null)
                        {
                            fvInstructions.remove(item.getText(0));
                            fvInstructions.put(updatedFVInstruction.getFieldId(), updatedFVInstruction);
                            item.setText(0, updatedFVInstruction.getFieldId());
                            item.setText(1, updatedFVInstruction.getShowOrHide()==null?"":updatedFVInstruction.getShowOrHide().toString().toLowerCase());
                            item.setText(2, updatedFVInstruction.getModesString()==null?"":updatedFVInstruction.getModesString());
                            item.setText(3, updatedFVInstruction.getForce()==null?"":updatedFVInstruction.getForce());
                        }
                    }
                }
            }
        };

        String [] columnTitles = {"Field Name","Display Mode","Mode(s)","Force"};
        int []columnWidths = {150,150,150};

        fieldVisibilityTable = TableUtils.createTable(fieldVisibilityContainer, "Field Visibility Configurations", true, "Add Configuration", addMouseListener, "Delete Configuration(s)", deleteMouseListener,
                null, clickListener, columnTitles, columnWidths, 150, false, 0);

        // populate table items
        List<FieldVisibilityInstructionCustom> elemList= getFormConfigRuntime().getFieldVisibilityInstructions(configType, typeName, (String)properties.get("form-id"));
        if (elemList != null)
        {
            for (FieldVisibilityInstructionCustom fieldVisibilityElem : elemList)
            {
                TableItem tableItem = new TableItem (fieldVisibilityTable, SWT.NONE);                   
                if (fieldVisibilityElem.getFieldId() != null)
                {
                    tableItem.setText(0, fieldVisibilityElem.getFieldId());
                }
                if (fieldVisibilityElem.getShowOrHide() != null)
                {
                    tableItem.setText(1, fieldVisibilityElem.getShowOrHide().toString().toLowerCase());
                }
                if (fieldVisibilityElem.getModesString() != null)
                {
                    tableItem.setText(2, fieldVisibilityElem.getModesString());
                }
                if (fieldVisibilityElem.getForce() != null)
                {
                    tableItem.setText(3, fieldVisibilityElem.getForce());
                }
            }
        }
        fieldVisibilitySection.setClient(fieldVisibilityContainer);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.dialog.AbstractModelDialog#buildRightSection()
     */
    protected void buildRightSection ()
    {
        super.buildRightSection();

        Section appearanceSection = FormSectionUtils.createStaticSection(toolkit,right,"Appearance","Appearance Configurations");        
        Composite  appearanceSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, appearanceSection);

        MouseListener addMouseListener = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                int counter = setTable.getItemCount();
                boolean isNewKeyUsed = false;
                do
                {
                    for (TableItem ti : setTable.getItems())
                    {
                        if (ti.getText(0).equals("set"+counter))
                        {
                            isNewKeyUsed = true;
                            counter++;
                        }
                    } 
                } 
                while (isNewKeyUsed);

                TableItem item = new TableItem (setTable, SWT.NONE);
                item.setText(0, "set"+counter);
                FormSet formSet = new FormSet("set"+counter);
                sets.put(formSet.getSetId(), formSet);
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
                TableItem[] items = setTable.getItems();
                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        sets.remove(items[i].getText(0));
                    }
                }
                TableUtils.deleteCheckedItems(setTable);                
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
                TableItem item = setTable.getItem(pt);
                if (item == null)
                {
                    return;
                }
                for (int i = 0; i < 1; i++) 
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        SetEditDialog setEditorDialog = new SetEditDialog(setTable.getShell(),
                                getService(),getModel(),getFormConfigRuntime(),getFormConfigIDocument(),formConfig,sets.get(item.getText(0)));
                        FormSet  updatedFormSet = setEditorDialog.open();
                        if (updatedFormSet != null)
                        {
                            sets.remove(item.getText(0));
                            sets.put(updatedFormSet.getSetId(), updatedFormSet);
                            item.setText(0, updatedFormSet.getSetId());
                        }
                    }
                }
            }
        };

        String [] columnTitles = {"Set ID"};
        int []columnWidths = {150};

        setTable = TableUtils.createTable(appearanceSectionContainer, "Set Configurations", true, "Add Set", addMouseListener, "Delete Set(s)", deleteMouseListener,
                null, clickListener, columnTitles, columnWidths, 150, false, 0);

        if ( formConfig.getSetIDs() != null )
        {
            for (String setId : formConfig.getSetIDs())
            {
                if (setId != null && !setId.equals(""))
                {
                    TableItem tableItem = new TableItem (setTable, SWT.NONE);
                    tableItem.setText(0, setId);
                }
            }
        }


        MouseListener addMouseListener2 = new MouseListener() 
        {
            @Override
            public void mouseDoubleClick(MouseEvent arg0)
            {
            }
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                int counter = fieldTable.getItemCount();
                boolean isNewKeyUsed = false;
                do
                {
                    for (TableItem ti : fieldTable.getItems())
                    {
                        if (ti.getText(0).equals("field"+counter))
                        {
                            isNewKeyUsed = true;
                        }
                    } 
                    for (TableItem ti : fieldVisibilityTable.getItems())
                    {
                        if (ti.getText(0).equals("field"+counter))
                        {
                            isNewKeyUsed = true;
                        }
                    } 
                    if (isNewKeyUsed)
                    {
                        counter++;
                        isNewKeyUsed = false;
                    }
                } 
                while (isNewKeyUsed);

                TableItem item = new TableItem (fieldTable, SWT.NONE);
                item.setText(0, "field"+counter);
                Map<String,String> attributes = new LinkedHashMap<String,String>();
                attributes.put("description", "description");
                FormField formField = new FormField("field"+counter, attributes);
                fields.put(formField.getId(), formField);
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
                TableItem[] items = fieldTable.getItems();
                for (int i=0; i<items.length; i++) 
                {
                    if (items[i].getChecked())
                    {
                        fields.remove(items[i].getText(0));
                    }
                }
                TableUtils.deleteCheckedItems(fieldTable);                
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
                TableItem item = fieldTable.getItem(pt);
                if (item == null)
                {
                    return;
                }
                for (int i = 0; i < 1; i++) 
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) 
                    {
                        FieldEditDialog fieldEditorDialog = new FieldEditDialog(fieldTable.getShell(),
                                getService(),getModel(),getFormConfigRuntime(),getFormConfigIDocument(),formConfig,fields.get(item.getText(0)));
                        FormField  updatedField =fieldEditorDialog.open();
                        if (updatedField != null)
                        {
                            fields.remove(item.getText(0));
                            fields.put(updatedField.getId(), updatedField);
                            item.setText(0, updatedField.getId());
                        }
                    }
                }
            }
        };

        String [] columnTitles2 = {"Field ID"};
        int []columnWidths2 = {150};

        fieldTable = TableUtils.createTable(appearanceSectionContainer, "Form Fields", true, "Add Field", addMouseListener2, "Delete Field(s)", deleteMouseListener2,
                null, clickListener2, columnTitles2, columnWidths2, 150, false, 0);

        if ( formConfig.getFields() != null )
        {
            for (String fieldId : formConfig.getFields().keySet())
            {
                if (!formConfig.getFields().get(fieldId).getAttributes().isEmpty())
                {
                    TableItem tableItem = new TableItem (fieldTable, SWT.NONE);
                    tableItem.setText(0, fieldId);
                }
            }
        }

        appearanceSection.setClient(appearanceSectionContainer);
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
                if ( getFormConfigRuntime().compareString(formConfig.getId(), formIdText.getText()) )
                {
                    properties.put("new-form-id", formIdText.getText()==null?"":formIdText.getText());
                }
                if ( getFormConfigRuntime().compareString(typeName, typeNameCombo.getText()) )
                {
                    properties.put("new-type-name", typeNameCombo.getText());
                }
                //FormConfigElement formConfig = (FormConfigElement) properties.get("form-config");
                updatedFormConfig = new FormConfigElement();
                FormConfigUtils.initFormConfigElement(updatedFormConfig, formIdText.getText().equals("default")?"":formIdText.getText(), 
                        submissionURLText.getText(), 
                        editTemplateText.getText(),
                        createTemplateText.getText(),
                        viewTemplateText.getText(),
                        fields, sets);
                properties.put("form-config",updatedFormConfig);
                properties.put("visibility-instructions",fvInstructions.values().toArray(new FieldVisibilityInstructionCustom[fvInstructions.values().size()]));
                shell.close();
            }
        });

        cancel.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                properties = null;
                shell.close();
            }
        });                

    }

    /**
     * @return
     */
    public HashMap<String, Object> open()
    {
        initUI("Form Editor");
        buildHeadSection();
        buildLeftSection();
        buildRightSection();
        buildBottomSection();
        addProcessingListeners();
        openUI(1200,700);
        return properties;
    }

}
