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
package org.springframework.extensions.surf.alfresco.model.editor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;
import org.springframework.extensions.surf.alfresco.model.editor.page.ModelXMLOverviewPage;
import org.springframework.extensions.surf.alfresco.model.editor.page.ModelXMLPreferencePage;
import org.springframework.extensions.surf.alfresco.model.editor.properties.ModelProperties;

/**
 * @author drq
 *
 */
@SuppressWarnings("restriction")
public class ModelEditor extends XMLMultiPageEditorPart
{
    private Logger logger = Logger.getLogger(ModelEditor.class.getName());

    private StructuredTextEditor modelXmlEditor;
    private StructuredTextEditor bootstrapContextXmlEditor;
    private StructuredTextEditor webclientConfigXmlEditor;
    private StructuredTextEditor formConfigXmlEditor;

    private FileEditorInput editorInput;

    private IDocumentListener documentListener = null;

    private ModelXMLOverviewPage overviewPage;
    private ModelXMLPreferencePage preferencePage;


    /**
     * @return the overviewPage
     */
    public ModelXMLOverviewPage getOverviewPage()
    {
        return overviewPage;
    }

    /**
     * @param overviewPage the overviewPage to set
     */
    public void setOverviewPage(ModelXMLOverviewPage overviewPage)
    {
        this.overviewPage = overviewPage;
    }

    /**
     * @return the modelXmlEditor
     */
    public StructuredTextEditor getModelXmlEditor()
    {
        return modelXmlEditor;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.MultiPageEditorPart#addPage(org.eclipse.ui.IEditorPart, org.eclipse.ui.IEditorInput)
     */
    @Override
    public int addPage(IEditorPart editor, IEditorInput input) throws PartInitException 
    {
        if (editor instanceof StructuredTextEditor) 
        {
            modelXmlEditor = (StructuredTextEditor) editor;
        }
        return super.addPage(editor, input);
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart#createPages()
     */
    protected void createPages() 
    {
        super.createPages();

        for (int i = 0 ; i < getPageCount(); i ++)
        {
            if (getPageText(i).equals("Design"))
            {
                setPageText(i, "Model Tree Editor");
            }
            if (getPageText(i).equals("Source"))
            {
                setPageText(i, "Model Xml Source");
            }
        }

        this.documentListener = new DocumentListener();

        editorInput = (FileEditorInput) modelXmlEditor.getEditorInput();

        modelXmlEditor.getDocumentProvider().getDocument(editorInput).addDocumentListener(documentListener);

        // setup bootstrap context xml page
        setupBootstrapContextPage();
        // setup webclient configuration page
        setupWebclientConfigPage();
        // setup form configuration page
        setupFormConfigPage();
        // setup overview page
        setupOverviewPage();
        // setup preference page
        setupPreferencePage();

        // this is for dealing with save event
        addPropertyListener(new IPropertyListener ()
        {
            @Override
            public void propertyChanged(Object source, int property)
            {
                overviewPage.reload();
            }            
        });        
    }

    /**
     * Creates the overview page and makes it the first and active page.
     */
    protected void setupOverviewPage() 
    {
        // create the overview page
        overviewPage = new ModelXMLOverviewPage(getContainer(), SWT.NULL, this);
        // add the overview page
        addPage(0,overviewPage);
        // set the page text
        setPageText(0, "Model Design");
        // make it active
        setActivePage(0);
    }

    /**
     * @param editorInput
     */
    protected void setupPreferencePage() 
    {
        // create the preference page
        preferencePage = new ModelXMLPreferencePage(getContainer(), SWT.NULL, this);
        // add the preference page
        addPage(1,preferencePage);
        // set the page text
        setPageText(1, "Preferences");    
    }    

    /**
     * @return
     */
    public int getWebclientConfigXmlPageIndex()
    {
        for (int i = 0 ; i < this.getPageCount(); i ++)
        {
            if (this.getEditor(i) != null)
            {
                if ( webclientConfigXmlEditor != null && this.getEditor(i).getTitle().equals(webclientConfigXmlEditor.getTitle()))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * @return
     */
    public int getFormConfigXmlPageIndex()
    {
        for (int i = 0 ; i < this.getPageCount(); i ++)
        {
            if (this.getEditor(i) != null)
            {
                if ( formConfigXmlEditor != null && this.getEditor(i).getTitle().equals(formConfigXmlEditor.getTitle()))
                {
                    return i;
                }
            }
        }
        return -1;
    }
    /**
     * @return
     */
    public int getBootstrapContextXmlPageIndex()
    {
        for (int i = 0 ; i < this.getPageCount(); i ++)
        {
            if (this.getEditor(i) != null)
            {
                if ( bootstrapContextXmlEditor != null && this.getEditor(i).getTitle().equals(bootstrapContextXmlEditor.getTitle()))
                {
                    return i;
                }
            }
        }
        return -1;
    }   

    public void activePreferencePage()
    {
        super.setActivePage(1);
    }
    
    /**
     * 
     */
    public void activeWebclientConfigXmlPage()
    {
        super.setActivePage(getWebclientConfigXmlPageIndex());
    }    

    /**
     * 
     */
    public void activeFormConfigXmlPage()
    {
        super.setActivePage(getFormConfigXmlPageIndex());
    }

    /**
     * 
     */
    public void activeBootstrapContextXmlPage()
    {
        super.setActivePage(getBootstrapContextXmlPageIndex());
    }

    /**
     * @param editorInput
     */
    public void setupBootstrapContextPage()
    {
        String bootstrapContextXmlPath = ModelProperties.getModelProperty(editorInput.getFile(), ModelProperties.BOOTSTRAP_CONTEXT_PROPERTY);

        if ( bootstrapContextXmlPath != null && !bootstrapContextXmlPath.equals(""))
        {
            IFile buildFile = getFile(bootstrapContextXmlPath);
            if (buildFile != null)
            {
                FileEditorInput in = new FileEditorInput(buildFile);
                try
                {
                    if (bootstrapContextXmlEditor != null)
                    {
                        super.removePage(getBootstrapContextXmlPageIndex());
                        bootstrapContextXmlEditor.dispose();
                    }
                    bootstrapContextXmlEditor = new StructuredTextEditor();
                    int bootstrapContextXmlPageIndex = super.addPage(bootstrapContextXmlEditor,in);
                    setPageText(bootstrapContextXmlPageIndex,buildFile.getName());
                } 
                catch (PartInitException e)
                {
                    logger.log(Level.SEVERE, "Failed to open new page for model bootstrap context xml", e);
                }
            }
            else
            {
                if (bootstrapContextXmlEditor != null)
                {
                    super.removePage(getBootstrapContextXmlPageIndex());
                    bootstrapContextXmlEditor.dispose();
                    bootstrapContextXmlEditor = null;
                }                
            }
        }
        else
        {
            if (bootstrapContextXmlEditor != null)
            {
                super.removePage(getBootstrapContextXmlPageIndex());
                bootstrapContextXmlEditor.dispose();
                bootstrapContextXmlEditor = null;
            }       
        }
    }

    /**
     * @param editorInput
     */
    public void setupWebclientConfigPage()
    {
        // Check if bootstrap XML exists
        String webclientConfigXmlPath =  ModelProperties.getModelProperty(editorInput.getFile(), ModelProperties.WEBCLIENT_CONFIG_PROPERTY);

        if ( webclientConfigXmlPath != null && !webclientConfigXmlPath.equals(""))
        {
            IFile buildFile = getFile(webclientConfigXmlPath);
            if (buildFile != null)
            {
                FileEditorInput in = new FileEditorInput(buildFile);
                try
                {
                    if (webclientConfigXmlEditor != null)
                    {
                        super.removePage(getWebclientConfigXmlPageIndex());
                        webclientConfigXmlEditor.dispose();
                    }
                    webclientConfigXmlEditor = new StructuredTextEditor();
                    int webclientConfigXmlPageIndex = super.addPage(webclientConfigXmlEditor,in);
                    setPageText(webclientConfigXmlPageIndex,buildFile.getName());
                    webclientConfigXmlEditor.getDocumentProvider().getDocument(webclientConfigXmlEditor.getEditorInput()).addDocumentListener(documentListener);
                } catch (PartInitException e)
                {
                    logger.log(Level.SEVERE, "Failed to open new page for model webclient configuration xml", e);
                }
            }
            else
            {
                if (webclientConfigXmlEditor != null)
                {
                    super.removePage(getWebclientConfigXmlPageIndex());
                    webclientConfigXmlEditor.dispose();
                    webclientConfigXmlEditor = null;
                }                
            }
        }
        else
        {
            if (webclientConfigXmlEditor != null)
            {
                super.removePage(getWebclientConfigXmlPageIndex());
                webclientConfigXmlEditor.dispose();
                webclientConfigXmlEditor = null;
            }            
        }
    }

    /**
     * @param editorInput
     */
    public void setupFormConfigPage()
    {
        // Check if bootstrap XML exists
        String formConfigXmlPath =  ModelProperties.getModelProperty(editorInput.getFile(), ModelProperties.FORM_CONFIG_PROPERTY);

        if ( formConfigXmlPath != null && !formConfigXmlPath.equals(""))
        {
            IFile buildFile = getFile(formConfigXmlPath);
            if (buildFile != null)
            {
                FileEditorInput in = new FileEditorInput(buildFile);
                try
                {
                    if (formConfigXmlEditor != null)
                    {
                        super.removePage(getFormConfigXmlPageIndex());
                        formConfigXmlEditor.dispose();
                    }
                    formConfigXmlEditor = new StructuredTextEditor();
                    int formConfigXmlPageIndex = super.addPage(formConfigXmlEditor,in);
                    setPageText(formConfigXmlPageIndex,buildFile.getName());
                    formConfigXmlEditor.getDocumentProvider().getDocument(formConfigXmlEditor.getEditorInput()).addDocumentListener(documentListener);
                } catch (PartInitException e)
                {
                    logger.log(Level.SEVERE, "Failed to open new page for model form configuration xml", e);
                }
            }
            else
            {
                if (formConfigXmlEditor != null)
                {
                    super.removePage(getFormConfigXmlPageIndex());
                    formConfigXmlEditor.dispose();
                    formConfigXmlEditor = null;
                }                
            }
        }
        else
        {
            if (formConfigXmlEditor != null)
            {
                super.removePage(getFormConfigXmlPageIndex());
                formConfigXmlEditor.dispose();
                formConfigXmlEditor = null;
            }            
        }
    }    
    /**
     * Returns the document that the editor is working on.
     * 
     * @return document
     */
    public IDocument getDocument() 
    {
        IDocument document = null;
        if (modelXmlEditor != null) 
        {
            document = modelXmlEditor.getDocumentProvider().getDocument(modelXmlEditor.getEditorInput());
        }
        return document;
    }

    /**
     * @return
     */
    public IDocument getWebclientConfigDocument() 
    {
        IDocument webclientConfig = null;
        if (webclientConfigXmlEditor != null) 
        {
            webclientConfig = webclientConfigXmlEditor.getDocumentProvider().getDocument(webclientConfigXmlEditor.getEditorInput());
        }
        return webclientConfig;
    }

    /**
     * @return
     */
    public IDocument getFormConfigDocument() 
    {
        IDocument formConfig = null;
        if (formConfigXmlEditor != null) 
        {
            formConfig = formConfigXmlEditor.getDocumentProvider().getDocument(formConfigXmlEditor.getEditorInput());
        }
        return formConfig;
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart#dispose()
     */
    public void dispose() 
    {
        overviewPage.dispose();
        preferencePage.dispose();

        if (bootstrapContextXmlEditor != null)
        {
            bootstrapContextXmlEditor.dispose();
        }
        if (webclientConfigXmlEditor != null)
        {
            webclientConfigXmlEditor.dispose();
        }
        super.dispose();
    }

    /**
     * Internal DocumentLister that will force overview page to reload
     * once the document is changed by other editor pages.
     * 
     * @author drq
     */
    private class DocumentListener implements IDocumentListener 
    {
        /* (non-Javadoc)
         * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
         */
        public void documentAboutToBeChanged(DocumentEvent event) 
        {
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
         */
        public void documentChanged(DocumentEvent event) 
        {
            overviewPage.reload();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave(IProgressMonitor monitor) 
    {
        super.doSave(monitor);
        modelXmlEditor.doSave(monitor);
        if (bootstrapContextXmlEditor != null)
        {
            bootstrapContextXmlEditor.doSave(monitor);
        }
        if (webclientConfigXmlEditor != null)
        {
            webclientConfigXmlEditor.doSave(monitor);
        }
        if (formConfigXmlEditor != null)
        {
            formConfigXmlEditor.doSave(monitor);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    public void doSaveAs() 
    {
        modelXmlEditor.doSaveAs();
    }

    /**
     * @param path
     * @return
     */
    private IFile getFile (String path)
    {
        try
        {
            IFile file = IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getFile(new Path(path));
            if (file.exists())
            {
                return file;
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Failed to locate file with path "+path, e);
            return null;
        }   
    }
}
