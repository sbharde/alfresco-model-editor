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

import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.web.config.forms.ControlParam;
import org.alfresco.web.config.forms.FormConfigRuntime;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
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
public class ParameterEditDialog extends AbstractModelDialog
{

    private ControlParam controlParam;
    private ControlParam updatedControlParam;

    private Text paramNameText;
    private Text parmValueText;

    /**
     * Constructor with parameters shell, style and Class
     * 
     * @param parent
     * @param style
     * @param clazz
     * @param service
     * @param model
     */
    public ParameterEditDialog(Shell parent, int style, DictionaryService service, M2Model model, FormConfigRuntime formConfigRuntime,IDocument formConfigIDocument, ControlParam controlParam)
    {
        super(parent, style,service,model,formConfigRuntime,formConfigIDocument);
        this.controlParam = controlParam;
        this.updatedControlParam = null;
    }

    /**
     * Constructor with parameters shell and Class
     * 
     * @param parent
     * @param clazz
     * @param service
     * @param model
     */
    public ParameterEditDialog(Shell parent, DictionaryService service, M2Model model, FormConfigRuntime formConfigRuntime,IDocument formConfigIDocument,ControlParam controlParam)
    {
        this(parent, SWT.PRIMARY_MODAL, service, model,formConfigRuntime,formConfigIDocument, controlParam);
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

        Section basicInfoSection = FormSectionUtils.createStaticSection(toolkit,left,"General Information","Control Parameter General Information");        
        Composite  basicInfoSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, basicInfoSection);

        // build basic information section
        Composite basicInfo = toolkit.createComposite(basicInfoSectionContainer);        
        basicInfo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        basicInfo.setLayout(new GridLayout(2, false));

        // build short name text field
        Label label = new Label(basicInfo, SWT.NULL);
        label.setText("&Name:");

        paramNameText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        paramNameText.setLayoutData(gridData);
        paramNameText.setText(controlParam.getName());

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Value:");

        parmValueText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        parmValueText.setLayoutData(gridData);
        String controlTemplate = controlParam.getValue();
        parmValueText.setText(controlTemplate);
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
                if (paramNameText.getText() != null && !paramNameText.getText().equals(""))
                {
                    updatedControlParam = new ControlParam(paramNameText.getText(),parmValueText.getText());
                }            
                shell.close();
            }
        });

        cancel.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                shell.close();
            }
        });                

    }

    public ControlParam open()
    {
        initUI("Control Parameter Editor");
        buildHeadSection();
        buildLeftSection();
        buildRightSection();
        buildBottomSection();
        addProcessingListeners();
        openUI(1200,700);
        return updatedControlParam;
    }

}
