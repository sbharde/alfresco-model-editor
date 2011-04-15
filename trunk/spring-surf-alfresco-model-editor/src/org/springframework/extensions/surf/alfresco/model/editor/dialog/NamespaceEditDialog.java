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

import java.net.MalformedURLException;
import java.net.URL;

import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Namespace;
import org.alfresco.repo.dictionary.ModelUtils;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.web.config.WebConfigRuntime;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.springframework.extensions.surf.alfresco.model.editor.Activator;

/**
 * Schema Type Editor Dialog
 * 
 * @author drq
 *
 */
public class NamespaceEditDialog extends AbstractModelDialog
{

    private M2Namespace namespace;
    private M2Namespace updatedNamespace;

    /**
     * Constructor with parameters shell and Property
     * 
     * @param parent
     * @param property
     * @param service
     * @param model
     */
    public NamespaceEditDialog(Shell parent, M2Namespace namespace, DictionaryService service,M2Model model, WebConfigRuntime webConfigRuntime,IDocument webclientConfigIDocument)
    {
        this(parent,SWT.PRIMARY_MODAL,namespace,service,model,webConfigRuntime,webclientConfigIDocument);
    }

    /**
     * Constructor with parameters shell, style and Property
     * 
     * @param parent
     * @param style
     * @param property
     * @param service
     * @param model
     */
    public NamespaceEditDialog(Shell parent, int style, M2Namespace namespace,DictionaryService service,M2Model model, WebConfigRuntime webConfigRuntime,IDocument webclientConfigIDocument)
    {
        super(parent, style,service,model,webConfigRuntime,webclientConfigIDocument);
        this.namespace = namespace;
        this.updatedNamespace = ModelUtils.newNameSpace(namespace.getPrefix(), namespace.getUri());
    }

    /**
     * @return the webscriptResource
     */
    public M2Namespace getNamespace()
    {
        return namespace;
    }

    /**
     * @param webscriptResource the webscriptResource to set
     */
    public void setNamespace(M2Namespace namespace)
    {
        this.namespace = namespace;
    }

    /**
     * @return
     */
    public M2Namespace open () 
    {
        Shell parent = getParent();
        final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
        shell.setText(getText());

        // set up shell layout
        FillLayout fillLayout = new FillLayout();
        fillLayout.type = SWT.VERTICAL;
        shell.setLayout(fillLayout);
        shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        FormToolkit toolkit = new FormToolkit(shell.getDisplay());        
        ScrolledForm form = toolkit.createScrolledForm(shell);
        form.setText("Namespace Editor");
        toolkit.decorateFormHeading(form.getForm());
        form.getToolBarManager().add(new Action("Alfresco") 
        { 
            public ImageDescriptor getImageDescriptor() 
            {
                return Activator.getImageDescriptor("icons/AlfrescoLogo200.png");
            }            
            public void run()
            {
                try
                {
                    PlatformUI.getWorkbench().getBrowserSupport().createBrowser("myID").openURL(new URL("http://www.alfresco.com"));
                } catch (PartInitException e)
                {
                } catch (MalformedURLException e)
                {
                }
            }
        });
        form.getToolBarManager().update(true);
        form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter() 
        {
            public void linkActivated(HyperlinkEvent e) 
            {

            }
        });        
        toolkit.getHyperlinkGroup().setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_HOVER);        
        Composite  body = form.getBody();
        body.setLayout(new GridLayout(1, false));
        body.setLayoutData(gridData);

        Group basicInfoGroup = new Group (body, SWT.NONE);
        basicInfoGroup.setText ("Namespace Basic Information");
        basicInfoGroup.setLayout(new GridLayout (1, false));
        basicInfoGroup.setLayoutData(gridData);
        
        // build basic indefaultValueion section
        Composite basicInfo = toolkit.createComposite(basicInfoGroup);        
        basicInfo.setLayoutData(gridData);
        basicInfo.setLayout(new GridLayout(2, false));

        // build short name text field
        Label label = new Label(basicInfo, SWT.NULL);
        label.setText("&Prefix:");

        String prefix = namespace.getPrefix();
        final Text prefixText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        prefixText.setLayoutData(gridData);
        prefixText.setText(prefix);

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Uri:");

        String uri = namespace.getUri();
        final Text uriText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        uriText.setLayoutData(gridData);
        uriText.setText(uri);
                
        //Creates button bar
        Composite buttonBar = toolkit.createComposite(body);        
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        buttonBar.setLayoutData(gridData);
        RowLayout rowLayout = new RowLayout();
        rowLayout.wrap = true;
        rowLayout.pack = true;
        rowLayout.justify = false;
        rowLayout.type = SWT.HORIZONTAL;
        buttonBar.setLayout(rowLayout);

        Button ok = new Button(buttonBar, SWT.PUSH);
        ok.setText("OK");
        ok.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                updatedNamespace.setPrefix(prefixText.getText());
                updatedNamespace.setUri(uriText.getText());
                shell.close();
            }
        });

        // Create the cancel button and add a handler
        // so that pressing it will set input to null
        Button cancel = new Button(buttonBar, SWT.PUSH);
        cancel.setText("Cancel");
        cancel.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                updatedNamespace  = null;
                shell.close();
            }
        });        

        shell.open();
        Display display = parent.getDisplay();
        while (!shell.isDisposed()) 
        {
            if (!display.readAndDispatch()) display.sleep();
        }

        return updatedNamespace;
    }

	@Override
	void addProcessingListeners() {
		// TODO Auto-generated method stub
		
	}
}
