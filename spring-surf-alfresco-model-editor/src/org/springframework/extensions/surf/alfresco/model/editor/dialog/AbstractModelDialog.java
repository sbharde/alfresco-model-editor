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
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.web.config.WebConfigRuntime;
import org.alfresco.web.config.forms.FormConfigRuntime;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.pde.internal.ui.editor.FormLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.springframework.extensions.surf.alfresco.model.editor.Activator;
import org.springframework.extensions.surf.commons.ui.FormUtils;

/**
 * @author drq
 *
 */
@SuppressWarnings("restriction")
abstract public class AbstractModelDialog extends Dialog
{
    private M2Model model;
    private DictionaryService service;
    private WebConfigRuntime webConfigRuntime;
    private IDocument webclientConfigIDocument;
    
    private FormConfigRuntime formConfigRuntime;
    private IDocument formConfigIDocument;
    
    protected FormToolkit toolkit;
    protected ScrolledForm form;
    protected IMessageManager msgManager;
    protected Shell shell;
    protected Composite left;
    protected Composite right;
    protected Composite bottom;
    protected Button ok;
    protected Button cancel;

    /**
     * @param parent
     * @param model
     * @param service
     */
    public AbstractModelDialog(Shell parent, int style,
            DictionaryService service, M2Model model , WebConfigRuntime webConfigRuntime, IDocument webclientConfigIDocument)
    {
        super(parent,style);
        this.model = model;
        this.service = service;
        this.webConfigRuntime = webConfigRuntime;
        this.webclientConfigIDocument = webclientConfigIDocument;
    }

    /**
     * @param parent
     * @param style
     * @param service
     * @param model
     * @param formConfigRuntime
     * @param formConfigIDocument
     */
    public AbstractModelDialog(Shell parent, int style,
            DictionaryService service, M2Model model , FormConfigRuntime formConfigRuntime, IDocument formConfigIDocument)
    {
        super(parent,style);
        this.model = model;
        this.service = service;
        this.formConfigRuntime = formConfigRuntime;
        this.formConfigIDocument = formConfigIDocument;
    }
    
    /**
     * @return the model
     */
    public M2Model getModel()
    {
        return model;
    }
    /**
     * @param model the model to set
     */
    public void setModel(M2Model model)
    {
        this.model = model;
    }
    /**
     * @return the service
     */
    public DictionaryService getService()
    {
        return service;
    }
    /**
     * @param service the service to set
     */
    public void setService(DictionaryService service)
    {
        this.service = service;
    }

	public WebConfigRuntime getWebConfigRuntime() {
		return webConfigRuntime;
	}

	public void setWebConfigRuntime(WebConfigRuntime webConfigRuntime) {
		this.webConfigRuntime = webConfigRuntime;
	}

	public IDocument getWebclientConfigIDocument()
    {
        return webclientConfigIDocument;
    }

    public void setWebclientConfigIDocument(IDocument webclientConfigIDocument)
    {
        this.webclientConfigIDocument = webclientConfigIDocument;
    }

    /**
     * @return the formConfigRuntime
     */
    public FormConfigRuntime getFormConfigRuntime()
    {
        return formConfigRuntime;
    }

    /**
     * @param formConfigRuntime the formConfigRuntime to set
     */
    public void setFormConfigRuntime(FormConfigRuntime formConfigRuntime)
    {
        this.formConfigRuntime = formConfigRuntime;
    }

    /**
     * @return the formConfigIDocument
     */
    public IDocument getFormConfigIDocument()
    {
        return formConfigIDocument;
    }

    /**
     * @param formConfigIDocument the formConfigIDocument to set
     */
    public void setFormConfigIDocument(IDocument formConfigIDocument)
    {
        this.formConfigIDocument = formConfigIDocument;
    }

    /**
	 * @param title
	 */
	protected void initUI (String title)
	{
        Shell parent = getParent();
        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
        shell.setText(getText());

        // set up shell layout
        FillLayout fillLayout = new FillLayout();
        fillLayout.type = SWT.VERTICAL;
        shell.setLayout(fillLayout);
        shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        toolkit = new FormToolkit(shell.getDisplay());        
        form = toolkit.createScrolledForm(shell);
        form.setText(title);		
	}
	
    /**
     * Builds head section of the overview page 
     */
	protected void buildHeadSection()
    {
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
        form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter() 
        {
            public void linkActivated(HyperlinkEvent e) 
            {
                String title = e.getLabel();
                Object href = e.getHref();
                Point hl = ((Control) e.widget).toDisplay(0, 0);
                hl.x += 10;
                hl.y += 10;
                Shell shell = new Shell(form.getShell(), SWT.ON_TOP | SWT.TOOL);
                shell.setImage(FormUtils.getImage(form.getMessageType()));
                shell.setText(title);
                shell.setLayout(new FillLayout());
                FormText text = toolkit.createFormText(shell, true);
                FormUtils.configureFormText(form.getForm(), text);
                if (href instanceof IMessage[])
                {
                    text.setText(FormUtils.createFormTextContent((IMessage[]) href), true, false);
                }
                shell.setLocation(hl);
                shell.pack();
                shell.open();
            }
        });        
        toolkit.getHyperlinkGroup().setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_HOVER);
        msgManager = form.getMessageManager(); 
        Composite  body = form.getBody();
        body.setLayout(FormLayoutFactory.createFormTableWrapLayout(true, 2));
        body.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    }
	
    /**
     * 
     */
    protected void buildBottomSection ()
    {
        Composite body = form.getBody();

        bottom = toolkit.createComposite(body);
        bottom.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
        bottom.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        Composite buttonBar = toolkit.createComposite(bottom);        
        RowLayout rowLayout = new RowLayout();
        rowLayout.wrap = true;
        rowLayout.pack = true;
        rowLayout.justify = false;
        rowLayout.type = SWT.HORIZONTAL;
        buttonBar.setLayout(rowLayout);

        ok = new Button(buttonBar, SWT.PUSH);
        ok.setText("OK");

        cancel = new Button(buttonBar, SWT.PUSH);
        cancel.setText("Cancel");
    }

    /**
     * 
     */
    protected void openUI()
    {
        openUI(1000,700);
    }
    
    /**
     * 
     */
    protected void openUI(int width, int height)
    {
        Shell parent = getParent();
        Composite body = form.getBody();
        body.pack();
        body.layout(true);
        body.redraw();
        form.setFocus();        
        shell.setSize(width, height);
        shell.open();
        Display display = parent.getDisplay();
        while (!shell.isDisposed()) 
        {
            if (!display.readAndDispatch()) display.sleep();
        }
    }

    /**
     * 
     */
    abstract void addProcessingListeners();

    /**
     * 
     */
    protected void buildLeftSection()
    {
        Composite body = form.getBody();
        // build left column of the form body
        left = toolkit.createComposite(body);
        left.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
        left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    }

    /**
     * 
     */
    protected void buildRightSection()    
    {
        Composite body = form.getBody();
        // build left column of the form body
        right = toolkit.createComposite(body);
        right.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
        right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    }

}
