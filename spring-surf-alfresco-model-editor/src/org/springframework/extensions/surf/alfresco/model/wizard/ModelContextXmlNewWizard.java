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
package org.springframework.extensions.surf.alfresco.model.wizard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.springframework.extensions.surf.alfresco.model.wizard.page.ModelContextXmlNewWizardPage;
import org.springframework.extensions.surf.commons.ui.EditorUtils;

/**
 * New wizard for description XMLs
 * 
 * @author drq
 *
 */
public class ModelContextXmlNewWizard extends Wizard implements INewWizard 
{
    private WizardNewFileCreationPage firstPage;
    private ModelContextXmlNewWizardPage secondPage;
    private IStructuredSelection selection;
    private String bootstrapXmlPath;
    
    /**
     * 
     */
    public ModelContextXmlNewWizard()
    {
        super();
        setNeedsProgressMonitor(true);
    }

    /**
     * @return the firstPage
     */
    public WizardNewFileCreationPage getFirstPage()
    {
        return firstPage;
    }

    /**
     * @param firstPage the firstPage to set
     */
    public void setFirstPage(WizardNewFileCreationPage firstPage)
    {
        this.firstPage = firstPage;
    }

    /**
     * @return the secondPage
     */
    public ModelContextXmlNewWizardPage getSecondPage()
    {
        return secondPage;
    }

    /**
     * @param secondPage the secondPage to set
     */
    public void setSecondPage(ModelContextXmlNewWizardPage secondPage)
    {
        this.secondPage = secondPage;
    }

    /**
     * @return the selection
     */
    public IStructuredSelection getSelection()
    {
        return selection;
    }

    /**
     * @param selection the selection to set
     */
    public void setSelection(IStructuredSelection selection)
    {
        this.selection = selection;
    }

    /**
	 * @return the bootstrapXmlPath
	 */
	public String getBootstrapXmlPath() {
		return bootstrapXmlPath;
	}

	/**
	 * @param bootstrapXmlPath the bootstrapXmlPath to set
	 */
	public void setBootstrapXmlPath(String bootstrapXmlPath) {
		this.bootstrapXmlPath = bootstrapXmlPath;
	}

	/**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() 
    {
        final IPath containerName = firstPage.getContainerFullPath();
        final String fileName = firstPage.getFileName();
        IRunnableWithProgress op = new IRunnableWithProgress() 
        {
            public void run(IProgressMonitor monitor) throws InvocationTargetException 
            {
                try 
                {
                    doFinish(containerName, fileName, monitor);
                } 
                catch (CoreException e) 
                {
                    throw new InvocationTargetException(e);
                } 
                finally 
                {
                    monitor.done();
                }
            }
        };

        try 
        {
            getContainer().run(true, false, op);
        } 
        catch (InterruptedException e) 
        {
            return false;
        } 
        catch (InvocationTargetException e) 
        {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        return true;
    }

    /**
     * The worker method. It will find the container, create the
     * file if missing or just replace its contents, and open
     * the editor on the newly created file.
     */
    private void doFinish(IPath containerName, String fileName, final IProgressMonitor monitor) throws CoreException 
    {
        // create a sample file
        monitor.beginTask("Creating " + fileName, 2);

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(containerName);
        if (!resource.exists() || !(resource instanceof IContainer)) 
        {
            throwCoreException("Container \"" + containerName + "\" does not exist.");
        }
        IContainer container = (IContainer) resource;
        final IFile file = container.getFile(new Path(fileName));
        getShell().getDisplay().syncExec
        (
                new Runnable() 
                {
                    public void run() 
                    {
                        try 
                        {
                            InputStream stream = openContentStream(secondPage);
                            if (file.exists()) 
                            {
                                file.setContents(stream, true, true, monitor);
                            } 
                            else 
                            {
                                file.create(stream, true, monitor);
                            }
                            stream.close();
                        } 
                        catch (IOException e) 
                        {
                        } 
                        catch (CoreException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
        );

        monitor.worked(1);
        monitor.setTaskName("Opening file for editing...");
        
        bootstrapXmlPath = file.getFullPath().toString();
        
        EditorUtils.openEditor(file, getShell(), null);
        
        monitor.worked(1);
    }

    /**
     * @param message
     * @throws CoreException
     */
    protected void throwCoreException(String message) throws CoreException 
    {
        IStatus status = new Status(IStatus.ERROR, "spring_surf_webscript_editor", IStatus.OK, message, null);
        throw new CoreException(status);
    }

    /**
     * We will accept the selection in the workbench to see if
     * we can initialize from it.
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) 
    {
        this.selection = selection;
    }

    /**
     * @param shortName
     * @param description
     * @return
     */
    protected InputStream openContentStream(ModelContextXmlNewWizardPage secondPage)
    {
        String contents= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        contents += "<!DOCTYPE beans PUBLIC '-//SPRING//DTD BEAN//EN' 'http://www.springframework.org/dtd/spring-beans.dtd'>\n";        
        contents += "<beans>\n";
        contents += "    <bean id=\""+secondPage.getBeanIdText().getText()+"\" parent=\"dictionaryModelBootstrap\" depends-on=\"dictionaryBootstrap\">\n";
        contents += "        <property name=\"models\">\n";
        contents += "           <list>\n";
        contents += "                <value>"+secondPage.getModelText().getText()+"</value>\n";
        contents += "            </list>\n";
        contents += "        </property>\n";
        contents += "    </bean>\n";                  
        contents += "</beans>\n";        
        return new ByteArrayInputStream(contents.getBytes());
    }

    /**
     * Adding the page to the wizard.
     */
    public void addPages() 
    {
        //Add first page for file selection
        if (this.getFirstPage() == null)
        {
            WizardNewFileCreationPage firstPage = new WizardNewFileCreationPage("New Model Bootstrap Context Xml", this.getSelection());
            firstPage.setTitle("New Model Bootstrap Context Xml");
            firstPage.setDescription("Creates a new New Model Bootstrap Context Xml.");
            firstPage.setFileName("new.model.context.xml");
            this.setFirstPage(firstPage);

            addPage(firstPage);
        }

        if (this.getSecondPage() == null)
        {
            //Add second page for basic package information
            ModelContextXmlNewWizardPage secondPage = new ModelContextXmlNewWizardPage("Model Bootstrap Context Xml Options");
            secondPage.setTitle("New Model Bootstrap Context Xml");
            secondPage.setDescription("Enter options for the new New Model Bootstrap Context Xml.");
            this.setSecondPage(secondPage);
            addPage(secondPage);
        }
    }

}
