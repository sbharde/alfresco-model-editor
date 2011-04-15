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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * @author drq
 *
 */
public class WizardNewModelCreationPage extends WizardNewFileCreationPage
{

    private Text serviceIdText;

    /**
     * @param pageName
     * @param selection
     */
    public WizardNewModelCreationPage(String pageName,
            IStructuredSelection selection)
    {
        super(pageName, selection);
    }

    /**
     * @return the serviceIdText
     */
    public Text getServiceIdText()
    {
        return serviceIdText;
    }

    /**
     * @param serviceIdText the serviceIdText to set
     */
    public void setServiceIdText(Text serviceIdText)
    {
        this.serviceIdText = serviceIdText;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void handleEvent(Event event)
    {        
        super.handleEvent(event);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#validatePage()
     */
    @Override
    protected boolean validatePage()
    {
        return super.validatePage();
    }

    public void createControl(Composite parent) 
    {
        // inherit default container and name specification widgets
        super.createControl(parent);
        this.setFileName("newModel.xml");

        //Composite composite = (Composite) getControl();        
        setPageComplete(validatePage());
    }
}
