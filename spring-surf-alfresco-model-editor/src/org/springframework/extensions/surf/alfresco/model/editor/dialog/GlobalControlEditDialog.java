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
import org.alfresco.web.config.forms.ControlParam;
import org.alfresco.web.config.forms.FormConfigRuntime;
import org.alfresco.web.config.forms.FormConfigUtils;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
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
public class GlobalControlEditDialog extends AbstractModelDialog
{

    protected String controlName;
    protected Control control;
    protected Control updatedControl;

    protected HashMap<String,Object> properties;
    protected Text controlNameText;
    protected Text controlTemplateText;
    protected Table parameterTable;

    /**
     * Constructor with parameters shell, style and Class
     * 
     * @param parent
     * @param style
     * @param clazz
     * @param service
     * @param model
     */
    public GlobalControlEditDialog(Shell parent, int style, DictionaryService service, M2Model model, FormConfigRuntime formConfigRuntime,IDocument formConfigIDocument, String controlName, Control control)
    {
        super(parent, style,service,model,formConfigRuntime,formConfigIDocument);
        this.controlName = controlName;
        this.control = control;
    }

    /**
     * Constructor with parameters shell and Class
     * 
     * @param parent
     * @param clazz
     * @param service
     * @param model
     */
    public GlobalControlEditDialog(Shell parent, DictionaryService service, M2Model model, FormConfigRuntime formConfigRuntime,IDocument formConfigIDocument,String controlName, Control control)
    {
        this(parent, SWT.PRIMARY_MODAL, service, model,formConfigRuntime,formConfigIDocument, controlName, control);
    }

    protected void initProperties ()
    {
        this.properties = new HashMap<String,Object> ();
        this.properties.put("config-type", "default-control");
        this.properties.put("type-name", this.controlName);
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
        label.setText("&Name:");

        controlNameText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        controlNameText.setLayoutData(gridData);
        controlNameText.setText(controlName);

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Template:");

        controlTemplateText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        controlTemplateText.setLayoutData(gridData);
        String controlTemplate = control.getTemplate();
        if (controlTemplate != null)
        {
            controlTemplateText.setText(controlTemplate);
        }

        basicInfoSection.setClient(basicInfoSectionContainer);

        // setup namespace section
        Section propertySection = FormSectionUtils.createStaticSection(toolkit,left,"Parameters","View or Update Control Parameters.");

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
                null, clickListener, columnTitles, columnWidths, 150, false, 0);

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
    }

    protected void buildRightSection ()
    {
        super.buildRightSection();
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
                if (controlNameText.getText() != null && !controlNameText.getText().equals("") && !controlName.equals(controlNameText.getText()))
                {
                    properties.put("new-type-name", controlNameText.getText());
                }
                updatedControl = new Control(controlTemplateText.getText());
                for (TableItem item : parameterTable.getItems())
                {
                    FormConfigUtils.addControlParam(updatedControl, item.getText(0), item.getText(1));
                }
                properties.put("control", updatedControl);
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
        initUI("Control Editor");
        initProperties ();
        buildHeadSection();
        buildLeftSection();
        buildRightSection();
        buildBottomSection();
        addProcessingListeners();
        openUI(1200,700);
        return properties;
    }

}
