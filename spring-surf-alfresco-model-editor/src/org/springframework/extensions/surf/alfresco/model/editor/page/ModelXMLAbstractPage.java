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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.pde.internal.ui.editor.FormLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import  org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.part.FileEditorInput;
import org.springframework.extensions.surf.alfresco.model.editor.Activator;
import org.springframework.extensions.surf.alfresco.model.editor.ModelEditor;

/**
 * @author drq
 *
 */
@SuppressWarnings("restriction")
public abstract class ModelXMLAbstractPage extends Composite
{
    private Logger logger = Logger.getLogger(ModelXMLAbstractPage.class.getName());

    protected FileEditorInput editorInput;
    protected ModelEditor modelEditor;
    
    protected IMessageManager msgManager;
    protected ScrolledForm form;
    protected FormToolkit toolkit;

    /**
     * @param parent
     * @param style
     */
    public ModelXMLAbstractPage(Composite parent, int style)
    {
        super(parent, style);
    }

    /**
     * @param parent
     * @param style
     * @param editorInput
     * @param modelEditor
     */
    public ModelXMLAbstractPage(Composite parent, int style,ModelEditor modelEditor)
    {
        super(parent, style);
        this.editorInput = (FileEditorInput) modelEditor.getEditorInput();
        this.modelEditor = modelEditor;
    }

    /**
     * @return the editorInput
     */
    public FileEditorInput getEditorInput()
    {
        return editorInput;
    }

    /**
     * @param editorInput the editorInput to set
     */
    public void setEditorInput(FileEditorInput editorInput)
    {
        this.editorInput = editorInput;
    }    
    /**
     * @return the modelEditor
     */
    public ModelEditor getModelEditor()
    {
        return modelEditor;
    }

    /**
     * @param modelEditor the modelEditor to set
     */
    public void setModelEditor(ModelEditor modelEditor)
    {
        this.modelEditor = modelEditor;
    }

    /**
     * 
     */
    protected void buildHeadSection(String title)
    {
        // create top-level page layout
        FillLayout fillLayout = new FillLayout();
        fillLayout.type = SWT.VERTICAL;
        setLayout(fillLayout);

        // create a top-level form for this page    
        toolkit = new FormToolkit(getDisplay());
        form = toolkit.createScrolledForm(this);

        form.setText(title);
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
                    logger.log(Level.WARNING, "Failed to initial an internal browser instance.", e);                    
                } catch (MalformedURLException e)
                {
                    logger.log(Level.WARNING, "Failed to open the url.", e);                    
                }
            }
        });
        form.getToolBarManager().update(true);
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
                shell.setImage(getImage(form.getMessageType()));
                shell.setText(title);
                shell.setLayout(new FillLayout());
                FormText text = toolkit.createFormText(shell, true);
                configureFormText(form.getForm(), text);
                if (href instanceof IMessage[])
                {
                    text.setText(createFormTextContent((IMessage[]) href), true, false);
                }
                shell.setLocation(hl);
                shell.pack();
                shell.open();
            }
        });        
        toolkit.getHyperlinkGroup().setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_HOVER);
        msgManager = form.getMessageManager(); 
        // setup layout for form body
        Composite  body = form.getBody();
        body.setLayout(FormLayoutFactory.createFormTableWrapLayout(true, 2));
        body.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    }
    
    /**
     * @param type
     * @return
     */
    protected Image getImage(int type) 
    {
        switch (type) 
        {
        case IMessageProvider.ERROR:
            return PlatformUI.getWorkbench().getSharedImages().getImage(
                    ISharedImages.IMG_OBJS_ERROR_TSK);
        case IMessageProvider.WARNING:
            return PlatformUI.getWorkbench().getSharedImages().getImage(
                    ISharedImages.IMG_OBJS_WARN_TSK);
        case IMessageProvider.INFORMATION:
            return PlatformUI.getWorkbench().getSharedImages().getImage(
                    ISharedImages.IMG_OBJS_INFO_TSK);
        }
        return null;
    }

    /**
     * @param form
     * @param text
     */
    protected void configureFormText(final Form form, FormText text) 
    {
        text.addHyperlinkListener(new HyperlinkAdapter() 
        {
            public void linkActivated(HyperlinkEvent e) 
            {
                String is = (String)e.getHref();
                try 
                {
                    int index = Integer.parseInt(is);
                    IMessage [] messages = form.getChildrenMessages();
                    IMessage message =messages[index];
                    Control c = message.getControl();
                    ((FormText)e.widget).getShell().dispose();
                    if (c!=null)
                        c.setFocus();
                }
                catch (NumberFormatException ex) 
                {
                }
            }
        });
        text.setImage("error", getImage(IMessageProvider.ERROR));
        text.setImage("warning", getImage(IMessageProvider.WARNING));
        text.setImage("info", getImage(IMessageProvider.INFORMATION));
    }

    /**
     * @param messages
     * @return
     */
    String createFormTextContent(IMessage[] messages) 
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("<form>");
        for (int i = 0; i < messages.length; i++) 
        {
            IMessage message = messages[i];
            pw.print("<li vspace=\"false\" style=\"image\" indent=\"16\" value=\"");
            switch (message.getMessageType()) 
            {
            case IMessageProvider.ERROR:
                pw.print("error");
                break;
            case IMessageProvider.WARNING:
                pw.print("warning");
                break;
            case IMessageProvider.INFORMATION:
                pw.print("info");
                break;
            }
            pw.print("\"> <a href=\"");
            pw.print(i+"");
            pw.print("\">");
            if (message.getPrefix() != null)
                pw.print(message.getPrefix());
            pw.print(message.getMessage());
            pw.println("</a></li>");
        }
        pw.println("</form>");
        pw.flush();
        return sw.toString();
    }
    
    /**
     * Adds error messages about caught exception
     * 
     * @param e caught exception
     */
    protected void addErrorMessage(Exception e)
    {
        msgManager.addMessage(e.hashCode(), e.getMessage(), null , IMessageProvider.ERROR);
        Throwable t = e;
        while (t != null)
        {
            msgManager.addMessage(t.hashCode(), t.getMessage(), null , IMessageProvider.ERROR);
            t = t.getCause();
        }
    }

    /**
     * Removes all error messages 
     */
    protected void removeAllMessages()
    {
        msgManager.removeAllMessages();
    }
}
