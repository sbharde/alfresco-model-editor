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
import org.alfresco.repo.dictionary.ModelUtils;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.web.config.forms.ConstraintHandlerDefinition;
import org.alfresco.web.config.forms.FormConfigRuntime;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.springframework.extensions.surf.commons.ui.FormSectionUtils;

/**
 * Model Class Editor Dialog
 * 
 * @author drq
 *
 */
public class GlobalConstraintHandlerEditDialog extends AbstractModelDialog
{

    private String constraintTypeName;

    private ConstraintHandlerDefinition constraintHandler;
    private ConstraintHandlerDefinition updatedConstraintHandler;

    private HashMap<String,Object> properties;

    private CCombo constraintTypeNameCombo;
    private Text constraintHandlerText;
    private Text eventText;
    private Text messageText;
    private Text messageIdText;

    /**
     * Constructor with parameters shell, style and Class
     * 
     * @param parent
     * @param style
     * @param clazz
     * @param service
     * @param model
     */
    public GlobalConstraintHandlerEditDialog(Shell parent, int style, DictionaryService service, M2Model model, FormConfigRuntime formConfigRuntime,IDocument formConfigIDocument, String constraintTypeName, ConstraintHandlerDefinition constraintHandler)
    {
        super(parent, style,service,model,formConfigRuntime,formConfigIDocument);
        this.constraintTypeName = constraintTypeName;
        this.constraintHandler = constraintHandler;
    }

    /**
     * Constructor with parameters shell and Class
     * 
     * @param parent
     * @param clazz
     * @param service
     * @param model
     */
    public GlobalConstraintHandlerEditDialog(Shell parent, DictionaryService service, M2Model model, FormConfigRuntime formConfigRuntime,IDocument formConfigIDocument,String constraintTypeName, ConstraintHandlerDefinition constraintHandler)
    {
        this(parent, SWT.PRIMARY_MODAL, service, model,formConfigRuntime,formConfigIDocument, constraintTypeName, constraintHandler);
    }

    protected void initProperties ()
    {
        this.properties = new HashMap<String,Object> ();
        this.properties.put("config-type", "constraint-handler");
        this.properties.put("constraint-type-name", this.constraintTypeName);
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

        Section basicInfoSection = FormSectionUtils.createStaticSection(toolkit,left,"General Information","Constraint General Information");        
        Composite  basicInfoSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, basicInfoSection);

        // build basic information section
        Composite basicInfo = toolkit.createComposite(basicInfoSectionContainer);        
        basicInfo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        basicInfo.setLayout(new GridLayout(2, false));

        // build short name text field
        Label label = new Label(basicInfo, SWT.NULL);
        label.setText("&Constraint Type Name:");

        constraintTypeNameCombo = new CCombo(basicInfo, SWT.FLAT | SWT.BORDER);
        constraintTypeNameCombo.setLayoutData(gridData);
        constraintTypeNameCombo.setText(constraintTypeName);
        
        for (String typeName : ModelUtils.getAllConstrainTypes().keySet())
        {
            constraintTypeNameCombo.add(typeName);
        }

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Validation Handler:");

        constraintHandlerText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        constraintHandlerText.setLayoutData(gridData);
        String constraintHandlerValue = constraintHandler.getValidationHandler();
        if (constraintHandlerValue != null)
        {
            constraintHandlerText.setText(constraintHandlerValue);
        }

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Message:");

        messageText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        messageText.setLayoutData(gridData);
        String message = constraintHandler.getMessage();
        if (message != null)
        {
            messageText.setText(message);
        }

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Message ID:");

        messageIdText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        messageIdText.setLayoutData(gridData);
        String messageId = constraintHandler.getMessageId();
        if (messageId != null)
        {
            messageIdText.setText(messageId);
        }

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Event:");

        eventText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        eventText.setLayoutData(gridData);
        String event = constraintHandler.getEvent();
        if (event != null)
        {
            eventText.setText(event);
        }
        basicInfoSection.setClient(basicInfoSectionContainer);


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
                if (constraintTypeNameCombo.getText() != null && !constraintTypeNameCombo.getText().equals("") && !constraintTypeName.equals(constraintTypeNameCombo.getText()))
                {
                    properties.put("new-constraint-type-name", constraintTypeNameCombo.getText());
                }
                updatedConstraintHandler = new ConstraintHandlerDefinition(constraintTypeNameCombo.getText(),constraintHandlerText.getText(),messageText.getText(),messageIdText.getText(),eventText.getText());
                properties.put("constraint", updatedConstraintHandler);
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
        initUI("Constraint Editor");
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
