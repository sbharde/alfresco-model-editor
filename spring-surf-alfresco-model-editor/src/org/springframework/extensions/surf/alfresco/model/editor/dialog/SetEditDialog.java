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
import org.alfresco.web.config.forms.FormConfigElement;
import org.alfresco.web.config.forms.FormConfigRuntime;
import org.alfresco.web.config.forms.FormSet;
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
public class SetEditDialog extends AbstractModelDialog
{
    protected FormConfigElement formConfig;
    private FormSet formSet;
    private FormSet updatedFormSet;

    private Text setIdText;
    private CCombo parentIdCombo;
    private Text labelText;
    private Text labelIdText;
    private Text templateText;
    private Text appearanceText;

    /**
     * Constructor with parameters shell, style and Class
     * 
     * @param parent
     * @param style
     * @param clazz
     * @param service
     * @param model
     */
    public SetEditDialog(Shell parent, int style, DictionaryService service, M2Model model, FormConfigRuntime formConfigRuntime,IDocument formConfigIDocument, FormConfigElement formConfig,  FormSet formSet)
    {
        super(parent, style,service,model,formConfigRuntime,formConfigIDocument);
        this.formConfig = formConfig;
        this.formSet = formSet;
    }

    /**
     * Constructor with parameters shell and Class
     * 
     * @param parent
     * @param clazz
     * @param service
     * @param model
     */
    public SetEditDialog(Shell parent, DictionaryService service, M2Model model, FormConfigRuntime formConfigRuntime,IDocument formConfigIDocument,FormConfigElement formConfig,FormSet formSet)
    {
        this(parent, SWT.PRIMARY_MODAL, service, model,formConfigRuntime,formConfigIDocument, formConfig, formSet);
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

        Section basicInfoSection = FormSectionUtils.createStaticSection(toolkit,left,"General Information","Field Visibility General Information");        
        Composite  basicInfoSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, basicInfoSection);

        // build basic information section
        Composite basicInfo = toolkit.createComposite(basicInfoSectionContainer);        
        basicInfo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        basicInfo.setLayout(new GridLayout(2, false));

        // build short name text field
        Label label = new Label(basicInfo, SWT.NULL);
        label.setText("&Field ID:");

        setIdText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        setIdText.setLayoutData(gridData);
        if (formSet.getSetId() == null || formSet.getSetId().equals(""))
        {
            setIdText.setText("root");
        }
        else
        {
            setIdText.setText(formSet.getSetId());
        }

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Parent Set ID:");

        parentIdCombo =new CCombo(basicInfo, SWT.FLAT | SWT.BORDER);
        parentIdCombo.setLayoutData(gridData);
        if (formSet.getParentId() != null)
        {
            parentIdCombo.setText(formSet.getSetId());
        }
        for (String setId : formConfig.getSetIDs())
        {
            parentIdCombo.add(setId);
        }
        
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Label:");

        labelText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        labelText.setLayoutData(gridData);
        if (formSet.getLabel() != null)
        {
            labelText.setText(formSet.getLabel());
        }
        
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Label ID:");

        labelIdText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        labelIdText.setLayoutData(gridData);
        if (formSet.getLabelId() != null)
        {
            labelIdText.setText(formSet.getLabelId());
        }
        
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Appearance:");

        appearanceText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        appearanceText.setLayoutData(gridData);
        if (formSet.getAppearance() != null)
        {
            appearanceText.setText(formSet.getAppearance());
        }
        
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Template:");

        templateText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        templateText.setLayoutData(gridData);
        if (formSet.getTemplate() != null)
        {
            templateText.setText(formSet.getTemplate());
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
                if (setIdText.getText() != null && setIdText.getText().equals("root"))
                {
                    updatedFormSet = new FormSet("", parentIdCombo.getText(),appearanceText.getText(),labelText.getText(),labelIdText.getText(),templateText.getText());
                }
                else
                {
                    updatedFormSet = new FormSet(setIdText.getText(),parentIdCombo.getText(),appearanceText.getText(),labelText.getText(),labelIdText.getText(),templateText.getText());
                }
                shell.close();
            }
        });

        cancel.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                updatedFormSet = null;
                shell.close();
            }
        });                

    }

    /**
     * @return
     */
    public FormSet open()
    {
        initUI("Set Editor");
        buildHeadSection();
        buildLeftSection();
        buildRightSection();
        buildBottomSection();
        addProcessingListeners();
        openUI(1200,700);
        return updatedFormSet;
    }

}
