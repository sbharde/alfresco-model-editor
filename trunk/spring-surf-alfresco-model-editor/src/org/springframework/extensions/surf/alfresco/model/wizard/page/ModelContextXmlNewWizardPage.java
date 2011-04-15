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
package org.springframework.extensions.surf.alfresco.model.wizard.page;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author drq
 *
 */
public class ModelContextXmlNewWizardPage extends WizardPage 
{
    private String beanId;

    private String model;

    private Text beanIdText;

    private Text modelText;

    private Composite pageContainer;

    /**
     * @param pageName
     */
    public ModelContextXmlNewWizardPage(String pageName)
    {
        super(pageName);
    }

    /**
     * @return the beanId
     */
    public String getBeanId()
    {
        return beanId;
    }

    /**
     * @param beanId the beanId to set
     */
    public void setBeanId(String beanId)
    {
        this.beanId = beanId;
    }

    /**
     * @return the model
     */
    public String getModel()
    {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(String model)
    {
        this.model = model;
    }

    /**
     * @return the shortNameText
     */
    public Text getBeanIdText()
    {
        return beanIdText;
    }

    /**
     * @param shortNameText the shortNameText to set
     */
    public void setBeanIdText(Text beanIdText)
    {
        this.beanIdText = beanIdText;
    }

    /**
     * @return the descriptionText
     */
    public Text getModelText()
    {
        return modelText;
    }

    /**
     * @param descriptionText the descriptionText to set
     */
    public void setModelText(Text modelText)
    {
        this.modelText = modelText;
    }

    /**
     * @return the pageContainer
     */
    public Composite getPageContainer()
    {
        return pageContainer;
    }

    /**
     * @param pageContainer the pageContainer to set
     */
    public void setPageContainer(Composite pageContainer)
    {
        this.pageContainer = pageContainer;
    }

    /**
     * Ensures that both text fields are set.
     */
    protected void dialogChanged() 
    {
        updateStatus(null);
    }

    /**
     * @param message
     */
    protected void updateStatus(String message) 
    {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    /**
     * 
     */
    protected void initialize()
    {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) 
    {
        pageContainer = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        pageContainer.setLayout(layout);
        layout.numColumns = 2;
        layout.verticalSpacing = 9;
        Label label = new Label(pageContainer, SWT.NULL);
        label.setText("&Id:");

        beanIdText = new Text(pageContainer, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        beanIdText.setLayoutData(gd);
        beanIdText.addModifyListener
        (
                new ModifyListener() 
                {
                    public void modifyText(ModifyEvent e) 
                    {
                        dialogChanged();
                    }
                }
        );
        if (beanId != null)
        {
            beanIdText.setText(beanId);
        }

        label = new Label(pageContainer, SWT.NULL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = SWT.TOP;
        label.setText("&Model:");

        modelText = new Text(pageContainer, SWT.BORDER | SWT.SINGLE);        
        modelText.setLayoutData(gd);
        modelText.addModifyListener
        (
                new ModifyListener() 
                {
                    public void modifyText(ModifyEvent e) 
                    {
                        dialogChanged();
                    }
                }
        );
        if (model != null)
        {
            modelText.setText(model);
        }
        initialize();
        dialogChanged();
        setControl(pageContainer);
    }
}
