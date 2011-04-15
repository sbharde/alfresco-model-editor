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
import org.alfresco.repo.dictionary.M2PropertyOverride;
import org.alfresco.repo.dictionary.ModelUtils;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.web.config.WebConfigRuntime;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.pde.internal.ui.editor.FormLayoutFactory;
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
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.springframework.extensions.surf.alfresco.model.editor.Activator;
import org.springframework.extensions.surf.commons.ui.FormSectionUtils;

/**
 * Schema Property Editor Dialog
 * 
 * @author drq
 *
 */
@SuppressWarnings("restriction")
public class PropertyOverrideEditDialog extends AbstractModelDialog
{

    private M2PropertyOverride propertyOverride;
    private M2PropertyOverride updatedPropertyOverride;

    /**
     * Constructor with parameters shell and Property
     * 
     * @param parent
     * @param propertyOverride
     * @param service
     * @param model
     */
    public PropertyOverrideEditDialog(Shell parent, M2PropertyOverride propertyOverride, DictionaryService service,M2Model model, WebConfigRuntime webConfigRuntime,IDocument webclientConfigIDocument)
    {
        this(parent,SWT.PRIMARY_MODAL,propertyOverride,service,model,webConfigRuntime,webclientConfigIDocument);
    }

    /**
     * Constructor with parameters shell, style and Property
     * 
     * @param parent
     * @param style
     * @param propertyOverride
     * @param service
     * @param model
     */
    public PropertyOverrideEditDialog(Shell parent, int style, M2PropertyOverride propertyOverride, DictionaryService service,M2Model model, WebConfigRuntime webConfigRuntime,IDocument webclientConfigIDocument)
    {
        super(parent, style,service,model,webConfigRuntime,webclientConfigIDocument);
        this.propertyOverride = propertyOverride;
        this.updatedPropertyOverride = ModelUtils.newPropertyOverride(propertyOverride.getName());
        ModelUtils.copyPropertyOverride(updatedPropertyOverride, propertyOverride);
    }

    /**
     * @return the webscriptResource
     */
    public M2PropertyOverride getProperty()
    {
        return propertyOverride;
    }

    /**
     * @param webscriptResource the webscriptResource to set
     */
    public void setProperty(M2PropertyOverride property)
    {
        this.propertyOverride = property;
    }

    /**
     * @return
     */
    public M2PropertyOverride open () 
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
        form.setText("Property Override Editor");
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
        final Composite  body = form.getBody();
        body.setLayout(FormLayoutFactory.createFormTableWrapLayout(true, 1));
        body.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        // build left column of the form body
        Composite left = toolkit.createComposite(body);
        left.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
        left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        Section basicInfoSection = FormSectionUtils.createStaticSection(toolkit,left,"General Information","Property General Information");        
        Composite  basicInfoSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, basicInfoSection);

        // build basic information section
        Composite basicInfo = toolkit.createComposite(basicInfoSectionContainer);        
        basicInfo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        basicInfo.setLayout(new GridLayout(2, false));

        // build short name text field
        Label label = new Label(basicInfo, SWT.NULL);
        label.setText("&Name:");

        String name = propertyOverride.getName();
        final Text nameText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        nameText.setLayoutData(gridData);
        nameText.setText(name);

        basicInfoSection.setClient(basicInfoSectionContainer);

        // setup option section
        Section optionSection = FormSectionUtils.createStaticSection(toolkit,left,"Options","View or Update Advanced Options.");

        final Composite  optionSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, optionSection);

        // build basic information section
        Composite options = toolkit.createComposite(optionSectionContainer);        
        options.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        options.setLayout(new GridLayout(2, false));

        label = new Label(options, SWT.NULL);
        label.setText("&Mandatory:");

        final Button mandatoryButton = new Button (options, SWT.CHECK);
        mandatoryButton.setText ("Check if mandatory");
        if (propertyOverride.isMandatory() != null)
        {
            mandatoryButton.setSelection (propertyOverride.isMandatory());
        }

        label = new Label(options, SWT.NULL);
        label.setText("&Default Value");

        String defaultValue = propertyOverride.getDefaultValue();
        final Text defaultValueText = new Text(options, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        defaultValueText.setLayoutData(gridData);
        if (defaultValue != null)
        {
            defaultValueText.setText(defaultValue);
        }
        optionSection.setClient(optionSectionContainer);

        //Creates button bar
        // build left column of the form body
        Composite bottom = toolkit.createComposite(body);
        bottom.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
        bottom.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        Composite buttonBar = toolkit.createComposite(bottom);        
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
                updatedPropertyOverride.setName(nameText.getText());
                updatedPropertyOverride.setMandatory(mandatoryButton.getSelection());
                if (defaultValueText.getText() != null)
                {
                    updatedPropertyOverride.setDefaultValue(defaultValueText.getText());
                }
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
                updatedPropertyOverride  = null;
                shell.close();
            }
        });                

        shell.open();
        Display display = parent.getDisplay();
        while (!shell.isDisposed()) 
        {
            if (!display.readAndDispatch()) display.sleep();
        }

        return updatedPropertyOverride;
    }

	@Override
	void addProcessingListeners() {
		// TODO Auto-generated method stub
		
	}
}
