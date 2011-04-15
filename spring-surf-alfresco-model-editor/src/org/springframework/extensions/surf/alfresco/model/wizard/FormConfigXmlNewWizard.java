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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

/**
 * New wizard for Custom Web Client Configuration Xml
 * 
 * @author drq
 *
 */
public class FormConfigXmlNewWizard extends Wizard implements INewWizard 
{
    private WizardNewFileCreationPage firstPage;
    private IStructuredSelection selection;
    private String formConfigXmlPath;
    
    /**
     * 
     */
    public FormConfigXmlNewWizard()
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
	 * @return the formConfigXmlPath
	 */
	public String getFormConfigXmlPath() {
		return formConfigXmlPath;
	}

	/**
	 * @param formConfigXmlPath the formConfigXmlPath to set
	 */
	public void setFormConfigXmlPath(String formConfigXmlPath) {
		this.formConfigXmlPath = formConfigXmlPath;
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
                            InputStream stream = openContentStream();
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
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
        );

        monitor.worked(1);
        monitor.setTaskName("Opening file for editing...");
        
        formConfigXmlPath = file.getFullPath().toString();
        
        getShell().getDisplay().asyncExec
        (
                new Runnable() 
                {
                    public void run() 
                    {
                        IWorkbenchPage page =
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                        try 
                        {
                            IDE.openEditor(page, file, true);
                        } 
                        catch (PartInitException e) 
                        {
                        }
                    }
                }
        );
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
    protected InputStream openContentStream()
    {
        String contents= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<alfresco-config>\n    <config>\n        <forms>\n        </forms>\n    </config>\n</alfresco-config>";
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
            WizardNewFileCreationPage firstPage = new WizardNewFileCreationPage("New Custom Form Configuration Xml", this.getSelection());
            firstPage.setTitle("New Custom Form Configuration Xml");
            firstPage.setDescription("Creates A New Custom Form Configuration Xml.");
            firstPage.setFileName("form-config-custom.xml");
            this.setFirstPage(firstPage);

            addPage(firstPage);
        }
    }

}
