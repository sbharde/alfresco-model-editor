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
package org.springframework.extensions.surf.alfresco.model.editor.page;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.alfresco.repo.dictionary.ModelUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.pde.internal.ui.editor.FormLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.springframework.extensions.surf.alfresco.model.editor.ModelEditor;
import org.springframework.extensions.surf.alfresco.model.editor.control.ModelListEditor;
import org.springframework.extensions.surf.alfresco.model.editor.properties.ModelProperties;
import org.springframework.extensions.surf.alfresco.model.wizard.FormConfigXmlNewWizard;
import org.springframework.extensions.surf.alfresco.model.wizard.ModelContextXmlNewWizard;
import org.springframework.extensions.surf.alfresco.model.wizard.WebclientConfigXmlNewWizard;
import org.springframework.extensions.surf.commons.ui.FormSectionUtils;

/**
 * @author drq
 *
 */
@SuppressWarnings("restriction")
public class ModelXMLPreferencePage extends ModelXMLAbstractPage
{

    private Logger logger = Logger.getLogger(ModelXMLPreferencePage.class.getName());

    protected Text bootstrapContextText;
    protected Text webclientConfigText;
    protected Text formConfigText;
    protected Label bootstrapContextXmlLabel;
    protected Label webclientConfigXmlLabel;
    protected Label formConfigXmlLabel;

    /**
     * @param parent
     * @param style
     */
    public ModelXMLPreferencePage(Composite parent, int style)
    {
        super(parent, style);
    }

    /**
     * @param parent
     * @param style
     * @param editorInput
     * @param modelEditor
     */
    public ModelXMLPreferencePage(Composite parent, int style, ModelEditor modelEditor)
    {
        super(parent, style, modelEditor);
        init();
    }

    /**
     * 
     */
    private Object openNewBootstrapXMLWizard ()
    {
        String fullPath = getEditorInput().getFile().getFullPath().toString();
        String fileName = getEditorInput().getFile().getName();
        String path = fullPath.substring(0, fullPath.indexOf(fileName));
        String modelId = fileName.substring(0,fileName.indexOf(".xml"));
        int indexOfModel = modelId.indexOf("Model");
        if (indexOfModel  != -1)
        {
            modelId = modelId.substring(0,indexOfModel);
        }

        ModelContextXmlNewWizard wizard = new  ModelContextXmlNewWizard();

        IStructuredSelection selection = new StructuredSelection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection());
        IFile thisFile = getEditorInput().getFile();
        wizard.init(PlatformUI.getWorkbench(), selection);
        wizard.addPages();
        if (wizard.getFirstPage() != null)
        {
            wizard.getFirstPage().setFileName(modelId+"-model-context.xml");
            wizard.getFirstPage().setContainerFullPath(new Path(path));
            wizard.getSecondPage().setBeanId(modelId+".dictionaryBootstrap");
            wizard.getSecondPage().setModel(fileName);
        }
        WizardDialog dlg = new WizardDialog(getShell(),wizard);
        Object obj = dlg.open();
        if (obj != null)
        {  
            ModelProperties.setModelProperty(thisFile, ModelProperties.BOOTSTRAP_CONTEXT_PROPERTY, wizard.getBootstrapXmlPath());
            bootstrapContextText.setText(wizard.getBootstrapXmlPath());
            setBootstrapContextXmlPath(wizard.getBootstrapXmlPath());
            getModelEditor().setupBootstrapContextPage();
        }
        return obj;
    }

    /**
     * 
     */
    private Object openNewWebclientConfigXMLWizard ()
    {
        String fullPath = getEditorInput().getFile().getFullPath().toString();
        String fileName = getEditorInput().getFile().getName();

        String path = fullPath.substring(0, fullPath.indexOf(fileName));

        WebclientConfigXmlNewWizard wizard = new  WebclientConfigXmlNewWizard();

        IStructuredSelection selection = new StructuredSelection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection());
        IFile thisFile = getEditorInput().getFile();
        wizard.init(PlatformUI.getWorkbench(), selection);
        wizard.addPages();
        if (wizard.getFirstPage() != null)
        {
            wizard.getFirstPage().setFileName("web-client-config-custom.xml");
            wizard.getFirstPage().setContainerFullPath(new Path(path));
        }
        WizardDialog dlg = new WizardDialog(getShell(),wizard);
        Object obj = dlg.open();
        if (obj != null)
        {
            ModelProperties.setModelProperty(thisFile, ModelProperties.WEBCLIENT_CONFIG_PROPERTY, wizard.getWebclientConfigXmlPath());
            webclientConfigText.setText(wizard.getWebclientConfigXmlPath());
            setWebclientConfigXmlPath(wizard.getWebclientConfigXmlPath());
            getModelEditor().setupWebclientConfigPage(); 
        }
        return obj;
    }

    /**
     * 
     */
    private Object openNewFormConfigXMLWizard ()
    {
        String fullPath = getEditorInput().getFile().getFullPath().toString();
        String fileName = getEditorInput().getFile().getName();

        String path = fullPath.substring(0, fullPath.indexOf(fileName));

        FormConfigXmlNewWizard wizard = new  FormConfigXmlNewWizard();

        IStructuredSelection selection = new StructuredSelection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection());
        IFile thisFile = getEditorInput().getFile();
        wizard.init(PlatformUI.getWorkbench(), selection);
        wizard.addPages();
        if (wizard.getFirstPage() != null)
        {
            wizard.getFirstPage().setFileName("form-config-custom.xml");
            wizard.getFirstPage().setContainerFullPath(new Path(path));
        }
        WizardDialog dlg = new WizardDialog(getShell(),wizard);
        Object obj = dlg.open();
        if (obj != null)
        {
            ModelProperties.setModelProperty(thisFile, ModelProperties.FORM_CONFIG_PROPERTY, wizard.getFormConfigXmlPath());
            formConfigText.setText(wizard.getFormConfigXmlPath());
            setFormConfigXmlPath(wizard.getFormConfigXmlPath());
            getModelEditor().setupFormConfigPage(); 
        }
        return obj;
    }    
    /**
     * @return
     */
    private String getWebclientConfigXmlPath()
    {
        return ModelProperties.getModelProperty(getEditorInput().getFile(), ModelProperties.WEBCLIENT_CONFIG_PROPERTY);
    }

    /**
     * @param path
     * @return
     */
    private String setWebclientConfigXmlPath(String path)
    {
        return ModelProperties.setModelProperty(getEditorInput().getFile(), ModelProperties.WEBCLIENT_CONFIG_PROPERTY,path);
    }

    /**
     * @return
     */
    private IFile getWebclientConfigXmlFile()
    {
        String webclientConfigXmlPath = getWebclientConfigXmlPath();

        try
        {
            if (webclientConfigXmlPath != null && !webclientConfigXmlPath.equals(""))
            {
                IPath iXmlResourcePath = new Path(webclientConfigXmlPath);
                final IFile xmlFile = IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getFile(iXmlResourcePath);

                if (xmlFile.exists())
                {
                    return xmlFile;
                }
                else
                {
                    return null;       
                }
            }
            else
            {
                return null;            
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Failed to located web config xml file with path"+webclientConfigXmlPath, e);
            return null;
        }
    }

    /**
     * @return
     */
    private String getFormConfigXmlPath()
    {
        return ModelProperties.getModelProperty(getEditorInput().getFile(), ModelProperties.FORM_CONFIG_PROPERTY);
    }
    
    /**
     * @param path
     * @return
     */
    private String setFormConfigXmlPath(String path)
    {
        return ModelProperties.setModelProperty(getEditorInput().getFile(), ModelProperties.FORM_CONFIG_PROPERTY,path);
    }

    /**
     * @return
     */
    private IFile getFormConfigXmlFile()
    {
        String formConfigXmlPath = getFormConfigXmlPath();

        try
        {
            if (formConfigXmlPath != null && !formConfigXmlPath.equals(""))
            {
                IPath iXmlResourcePath = new Path(formConfigXmlPath);
                final IFile xmlFile = IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getFile(iXmlResourcePath);

                if (xmlFile.exists())
                {
                    return xmlFile;
                }
                else
                {
                    return null;       
                }
            }
            else
            {
                return null;            
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Failed to located web config xml file with path "+formConfigXmlPath, e);
            return null;
        }
    }
    /**
     * @return
     */
    private String getBootstrapContextXmlPath()
    {
        return ModelProperties.getModelProperty(getEditorInput().getFile(), ModelProperties.BOOTSTRAP_CONTEXT_PROPERTY);
    }

    /**
     * @param path
     * @return
     */
    private String setBootstrapContextXmlPath(String path)
    {
        return ModelProperties.setModelProperty(getEditorInput().getFile(), ModelProperties.BOOTSTRAP_CONTEXT_PROPERTY,path);
    }

    /**
     * @return
     */
    private IFile getBootstrapContextXmlFile()
    {
        String bootstrapContextXmlPath = getBootstrapContextXmlPath();

        if (bootstrapContextXmlPath != null && !bootstrapContextXmlPath.equals(""))
        {
            IPath iXmlResourcePath = new Path(bootstrapContextXmlPath);
            final IFile xmlFile = IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getFile(iXmlResourcePath);

            if (xmlFile.exists())
            {
                return xmlFile;
            }
            else
            {
                return null;       
            }
        }
        else
        {
            return null;            
        }        
    }

    protected void buildContextSection (Composite parent)
    {
        final Section contextSection = FormSectionUtils.createStaticSection(toolkit,parent,"Model Bootstrap Context Xml","Create, link or remove model boostrap context xml.");        
        final Composite  contextSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, contextSection);

        // build basic information section
        Composite context = toolkit.createComposite(contextSectionContainer);        
        context.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        context.setLayout(new GridLayout(4, false));

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        bootstrapContextText = new Text(context, SWT.SINGLE | SWT.BORDER);
        bootstrapContextText.setLayoutData(gridData);

        final Button newButton = new Button(context, SWT.PUSH);
        newButton.setText("New...");
        newButton.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
                if (getBootstrapContextXmlFile() != null)
                {
                    getModelEditor().activeBootstrapContextXmlPage();
                }
                else
                {
                    if ( openNewBootstrapXMLWizard() != null )
                    {
                        newButton.setText("Edit");
                    }
                }            
            }
        });

        if (getBootstrapContextXmlFile() != null && !getBootstrapContextXmlFile().equals(""))
        {
            newButton.setText("Edit");            
        }

        Button linkButton = new Button(context, SWT.PUSH);
        linkButton.setText("Link");
        linkButton.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
                FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
                dialog.setFilterExtensions(new String[] { new String("*.xml") });
                String projectPath = getEditorInput().getFile().getProject().getLocation().toString();
                dialog.setFilterPath(projectPath);
                String path = dialog.open();
                if (path != null) 
                {
                    File file = new File(path);
                    if (file.isFile())
                    {
                        String filePath = "/"+getEditorInput().getFile().getProject().getName()+path.substring(path.indexOf(projectPath)+projectPath.length());
                        if (ModelUtils.compareString(filePath, bootstrapContextText.getText()))
                        {
                            bootstrapContextText.setText(filePath);
                            setBootstrapContextXmlPath(filePath);
                            getModelEditor().setupBootstrapContextPage();
                            newButton.setText("Edit");  
                        }
                    }
                }
            }
        });

        Button deleteButton = new Button(context, SWT.PUSH);
        deleteButton.setText("Remove");
        deleteButton.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
                bootstrapContextText.setText("");
                setBootstrapContextXmlPath("");
                getModelEditor().setupBootstrapContextPage();
                newButton.setText("New...");                  
            }
        });

        final String bootstrapContext = this.getBootstrapContextXmlPath();
        if (bootstrapContext != null)
        {
            bootstrapContextText.setText(bootstrapContext);
        }
        bootstrapContextText.addModifyListener 
        (
                new ModifyListener() 
                {
                    public void modifyText(ModifyEvent e) 
                    {
                        Text text = (Text) e.widget;
                        if (ModelUtils.compareString(bootstrapContext,text.getText()))
                        {
                            setBootstrapContextXmlPath(text.getText());
                            getModelEditor().setupBootstrapContextPage();
                        }
                    }
                }
        );
        bootstrapContextText.setEnabled(false);
        contextSection.setClient(contextSectionContainer);
    }

    protected void buildWebclientSection (Composite parent)
    {
        final Section webclientSection = FormSectionUtils.createStaticSection(toolkit,parent,"Model Web Client Configuration Xml","Create, link or remove model web client configuration xml.");        
        final Composite  webclientSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, webclientSection);

        // build basic information section
        Composite webclient = toolkit.createComposite(webclientSectionContainer);        
        webclient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        webclient.setLayout(new GridLayout(4, false));

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        webclientConfigText = new Text(webclient, SWT.SINGLE | SWT.BORDER);
        webclientConfigText.setLayoutData(gridData);

        final Button newButton = new Button(webclient, SWT.PUSH);
        newButton.setText("New...");
        newButton.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
                if (getWebclientConfigXmlFile() != null)
                {
                    getModelEditor().setupWebclientConfigPage();
                    getModelEditor().activeWebclientConfigXmlPage();
                }
                else
                {
                    if (openNewWebclientConfigXMLWizard() != null)
                    {
                        newButton.setText("Edit");
                        getModelEditor().getOverviewPage().initWebclientConfig();
                    }
                }           
            }
        });

        if (getWebclientConfigXmlFile() != null && !getWebclientConfigXmlFile().equals(""))
        {
            newButton.setText("Edit");            
        }

        Button linkButton = new Button(webclient, SWT.PUSH);
        linkButton.setText("Link");
        linkButton.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
                FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
                dialog.setFilterExtensions(new String[] { new String("*.xml") });
                String projectPath = getEditorInput().getFile().getProject().getLocation().toString();
                dialog.setFilterPath(projectPath);
                String path = dialog.open();
                if (path != null) 
                {
                    File file = new File(path);
                    if (file.isFile())
                    {
                        String filePath = "/"+getEditorInput().getFile().getProject().getName()+path.substring(path.indexOf(projectPath)+projectPath.length());
                        if (ModelUtils.compareString(filePath,webclientConfigText.getText()))
                        {
                            webclientConfigText.setText(filePath);
                            setWebclientConfigXmlPath(filePath);
                            getModelEditor().setupWebclientConfigPage();
                            getModelEditor().getOverviewPage().initWebclientConfig();
                            newButton.setText("Edit");  
                        }
                    }
                }
            }
        });

        Button deleteButton = new Button(webclient, SWT.PUSH);
        deleteButton.setText("Remove");
        deleteButton.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
                webclientConfigText.setText("");
                setWebclientConfigXmlPath("");
                getModelEditor().setupWebclientConfigPage();
                getModelEditor().getOverviewPage().removeWebclientConfig();
                newButton.setText("New...");                  
            }
        });

        final String webclientConfig = this.getWebclientConfigXmlPath();
        if (webclientConfig != null)
        {
            webclientConfigText.setText(webclientConfig);
        } 
        webclientConfigText.addModifyListener 
        (
                new ModifyListener() 
                {
                    public void modifyText(ModifyEvent e) 
                    {
                        Text text = (Text) e.widget;
                        if (ModelUtils.compareString(webclientConfig,text.getText()))
                        {
                            setWebclientConfigXmlPath(text.getText());
                            getModelEditor().setupWebclientConfigPage();
                        }
                    }
                }
        );
        webclientConfigText.setEnabled(false);        
        webclientSection.setClient(webclientSectionContainer);
    }

    protected void buildFormSection (Composite parent)
    {
        final Section formSection = FormSectionUtils.createStaticSection(toolkit,parent,"Model Form Configuration Xml","Create, link or remove model form configuration xml.");        
        final Composite  formSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, formSection);

        // build basic information section
        Composite form = toolkit.createComposite(formSectionContainer);        
        form.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        form.setLayout(new GridLayout(4, false));

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        formConfigText = new Text(form, SWT.SINGLE | SWT.BORDER);
        formConfigText.setLayoutData(gridData);

        final Button newButton = new Button(form, SWT.PUSH);
        newButton.setText("New...");
        newButton.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
                if (getFormConfigXmlFile() != null)
                {
                    getModelEditor().setupFormConfigPage();
                    getModelEditor().activeFormConfigXmlPage();
                }
                else
                {
                    if ( openNewFormConfigXMLWizard() != null )
                    {
                        newButton.setText("Edit");
                        getModelEditor().setupFormConfigPage();
                        getModelEditor().activeFormConfigXmlPage();
                        getModelEditor().getOverviewPage().initFormSection();
                    }
                }           
            }
        });

        if (getFormConfigXmlFile() != null && !getFormConfigXmlFile().equals(""))
        {
            newButton.setText("Edit");            
        }

        Button linkButton = new Button(form, SWT.PUSH);
        linkButton.setText("Link");
        linkButton.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
                FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
                dialog.setFilterExtensions(new String[] { new String("*.xml") });
                String projectPath = getEditorInput().getFile().getProject().getLocation().toString();
                dialog.setFilterPath(projectPath);
                System.out.println(projectPath);
                String path = dialog.open();
                if (path != null) 
                {
                    File file = new File(path);
                    if (file.isFile())
                    {
                        String filePath = "/"+getEditorInput().getFile().getProject().getName()+path.substring(path.indexOf(projectPath)+projectPath.length());
                        if (ModelUtils.compareString(filePath,formConfigText.getText()))
                        {
                            formConfigText.setText(filePath);
                            setFormConfigXmlPath(filePath);
                            getModelEditor().setupFormConfigPage();
                            getModelEditor().activeFormConfigXmlPage();
                            getModelEditor().getOverviewPage().initFormSection();
                            newButton.setText("Edit");  
                        }
                    }
                }
            }
        });

        Button deleteButton = new Button(form, SWT.PUSH);
        deleteButton.setText("Remove");
        deleteButton.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
                formConfigText.setText("");
                setFormConfigXmlPath("");
                getModelEditor().setupFormConfigPage();
                newButton.setText("New...");
                getModelEditor().getOverviewPage().removeFormSection();
            }
        });

        final String formConfig = this.getFormConfigXmlPath();
        if (formConfig != null)
        {
            formConfigText.setText(formConfig);
        } 
        formConfigText.addModifyListener 
        (
                new ModifyListener() 
                {
                    public void modifyText(ModifyEvent e) 
                    {
                        Text text = (Text) e.widget;
                        if (ModelUtils.compareString(formConfig,text.getText()))
                        {
                            setFormConfigXmlPath(text.getText());
                            getModelEditor().setupFormConfigPage();
                        }
                    }
                }
        );
        formConfigText.setEnabled(false);        
        formSection.setClient(formSectionContainer);
    }
    
    protected void buildBootstrapSection (Composite parent)
    {
        final Section bootstrapSection = FormSectionUtils.createStaticSection(toolkit,parent,"Model Bootstrap Options","Configure options for model bootstrap.");        
        final Composite  bootstrapSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit,bootstrapSection);

        Composite bootstrap = toolkit.createComposite(bootstrapSectionContainer);        
        bootstrap.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        bootstrap.setLayout(new GridLayout(1, false));

        ModelListEditor modelListEditor = new ModelListEditor(ModelProperties.BOOTSTRAP_MODELS_PROPERTY,"Bootstrap Model List",bootstrap,getModelEditor());
        modelListEditor.load();
        bootstrapSection.setClient(bootstrapSectionContainer);

    }
    /**
     * 
     */
    protected void buildLeftSection()
    {
        Composite body = form.getBody();

        // build left column of the form body
        Composite left = toolkit.createComposite(body);
        left.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
        left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        buildContextSection(left);
        buildWebclientSection(left);
        buildFormSection(left);
    }

    /**
     * 
     */
    protected void buildRightSection()
    {
        Composite body = form.getBody();

        // build left column of the form body
        Composite right = toolkit.createComposite(body);
        right.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
        right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        buildBootstrapSection (right);
    }
    /**
     * 
     */
    protected void init()
    {
        buildHeadSection("Preferences");
        buildRightSection();
        buildLeftSection();
    }
}
