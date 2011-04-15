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
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */
public class ModelNewWizardPage extends WizardPage
{
    private Text nameText;
    private Text descriptionText;
    private Text namespacePrefixText;
    private Text namespaceUriText;
    
    private Composite pageContainer;

    /**
     * @param pageName
     */
    protected ModelNewWizardPage(String pageName)
    {
        super(pageName);
    }

    /**
     * @return the nameText
     */
    public Text getNameText()
    {
        return nameText;
    }

    /**
     * @param nameText the nameText to set
     */
    public void setNameText(Text nameText)
    {
        this.nameText = nameText;
    }

    /**
     * @return the namespacePrefixText
     */
    public Text getNamespacePrefixText()
    {
        return namespacePrefixText;
    }

    /**
     * @param namespacePrefixText the namespacePrefixText to set
     */
    public void setNamespacePrefixText(Text namespacePrefixText)
    {
        this.namespacePrefixText = namespacePrefixText;
    }

    /**
     * @return the namespaceUriText
     */
    public Text getNamespaceUriText()
    {
        return namespaceUriText;
    }

    /**
     * @param namespaceUriText the namespaceUriText to set
     */
    public void setNamespaceUriText(Text namespaceUriText)
    {
        this.namespaceUriText = namespaceUriText;
    }    /**
     * @return the descriptionText
     */
    public Text getDescriptionText()
    {
        return descriptionText;
    }

    /**
     * @param descriptionText the descriptionText to set
     */
    public void setDescriptionText(Text descriptionText)
    {
        this.descriptionText = descriptionText;
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
        label.setText("&Name:");

        nameText = new Text(pageContainer, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        nameText.setLayoutData(gd);
        nameText.addModifyListener
        (
                new ModifyListener() 
                {
                    public void modifyText(ModifyEvent e) 
                    {
                        dialogChanged();
                    }
                }
        );

        label = new Label(pageContainer, SWT.NULL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = SWT.TOP;
        label.setText("&Description:");
        label.setLayoutData(gd);

        descriptionText = new Text(pageContainer, SWT.BORDER | SWT.MULTI);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        GC gc = new GC(descriptionText);
        FontMetrics fm = gc.getFontMetrics ();
        int height = 5 * fm.getHeight();
        gd.heightHint = height;
        descriptionText.setLayoutData(gd);
        descriptionText.addModifyListener
        (
                new ModifyListener() 
                {
                    public void modifyText(ModifyEvent e) 
                    {
                        dialogChanged();
                    }
                }
        );
        gc.dispose();

        label = new Label(pageContainer, SWT.NULL);
        label.setText("&Namespace Prefix:");

        namespacePrefixText = new Text(pageContainer, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        namespacePrefixText.setLayoutData(gd);
        namespacePrefixText.addModifyListener
        (
                new ModifyListener() 
                {
                    public void modifyText(ModifyEvent e) 
                    {
                        dialogChanged();
                    }
                }
        );

        label = new Label(pageContainer, SWT.NULL);
        label.setText("&Namespace Uri:");

        namespaceUriText = new Text(pageContainer, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        namespaceUriText.setLayoutData(gd);
        namespaceUriText.addModifyListener
        (
                new ModifyListener() 
                {
                    public void modifyText(ModifyEvent e) 
                    {
                        dialogChanged();
                    }
                }
        );
                
        initialize();
        dialogChanged();
        setControl(pageContainer);
    }

    /**
     * Constructor for SampleNewWizardPage.
     * 
     * @param pageName
     */
    public ModelNewWizardPage() 
    {
        super("basicModelInfoPage");
        setTitle("Model Basic Information");
        setDescription("Enters basic information about this model.");

    }

    /**
     * Tests if the current workbench selection is a suitable container to use.
     */
    protected void initialize() 
    {
        this.getNameText().setText("my:newmodel");
        this.getDescriptionText().setText("Model description");
        this.getNamespacePrefixText().setText("my");
        this.getNamespaceUriText().setText("http://www.alfresco.com/model/1.0/my");
    }

}