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
package org.springframework.extensions.surf.alfresco.model.editor.properties;

import java.io.File;

import org.alfresco.repo.dictionary.ModelRuntime;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * @author drq
 *
 */
public class ModelPropertyPage extends PropertyPage 
{

    public static final String BOOTSTRAP_CONTEXT_TITLE = "&Bootstrap Context Xml:";
    public static final String WEBCLIENT_CONFIG_TITLE = "&Web Client Configuration Xml:";
    public static final String BOOTSTRAP_MODELS_TITLE = "&Bootstrap Model List:";
    public static final String FORM_CONFIG_TITLE = "&Form Configuration Xml:";

    private static final int TEXT_FIELD_WIDTH = 50;

    private Text bootstrapContextText;
    private Text webclientConfigText;
    private Text bootstrapModelsText;
    private Text formConfigText;

    /**
     * Constructor for ModelPropertyPage.
     */
    public ModelPropertyPage() 
    {
        super();
    }

    /**
     * @param parent
     */
    private void addFirstSection(Composite parent) 
    {
        Composite composite = createDefaultComposite(parent);

        Label bootstrapModelsLabel = new Label(composite, SWT.NONE);
        bootstrapModelsLabel.setText(BOOTSTRAP_MODELS_TITLE);

        bootstrapModelsText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData gd = new GridData();
        gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
        bootstrapModelsText.setLayoutData(gd);
        
        String bootstrapModel = ModelProperties.getModelProperty((IResource) getElement(), ModelProperties.BOOTSTRAP_MODELS_PROPERTY);
        bootstrapModelsText.setText(( bootstrapModel != null) ?  bootstrapModel : "");
    }

    /**
     * @param parent
     */
    private void addSeparator(Composite parent) 
    {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        separator.setLayoutData(gridData);
    }

    /**
     * @param parent
     * @param labelText
     * @return
     */
    private Text addFilePicker(Composite parent, String labelText)
    {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText);

        Composite textButtonComposite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        textButtonComposite.setLayout(layout);

        GridData data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        textButtonComposite.setLayoutData(data);

        final Text textField = new Text(textButtonComposite, SWT.SINGLE | SWT.BORDER);
        GridData gd = new GridData();
        gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
        textField.setLayoutData(gd);

        Button button = new Button(textButtonComposite, SWT.PUSH);
        button.setText("Browse");
        button.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
                FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
                dialog.setFilterExtensions(new String[] { new String("*.xml") });
                String projectPath = ((IResource) getElement()).getProject().getLocation().toString();
                dialog.setFilterPath(projectPath);
                String path = dialog.open();
                if (path != null) {

                    File file = new File(path);
                    if (file.isFile())
                    {
                        textField.setText("/"+((IResource) getElement()).getProject().getName()+path.substring(path.indexOf(projectPath)+projectPath.length()));
                    }
                }
            }
        });
        
        return textField;
    }
    
    /**
     * @param parent
     */
    private void addSecondSection(Composite parent) 
    {
        Composite composite = createDefaultComposite(parent);

        bootstrapContextText = addFilePicker(composite, BOOTSTRAP_CONTEXT_TITLE);
        webclientConfigText  = addFilePicker(composite, WEBCLIENT_CONFIG_TITLE);
        formConfigText = addFilePicker(composite,FORM_CONFIG_TITLE);
        
        String bootstrapContext = ModelProperties.getModelProperty((IResource) getElement(), ModelProperties.BOOTSTRAP_CONTEXT_PROPERTY);
        bootstrapContextText.setText((bootstrapContext != null) ? bootstrapContext : "");
        String webclientConfig = ModelProperties.getModelProperty((IResource) getElement(), ModelProperties.WEBCLIENT_CONFIG_PROPERTY);
        webclientConfigText.setText(( webclientConfig != null) ?  webclientConfig : "");
        String formConfig = ModelProperties.getModelProperty((IResource) getElement(), ModelProperties.FORM_CONFIG_PROPERTY);
        formConfigText.setText(( formConfig != null) ?  formConfig : "");
    }

    /**
     * @see PreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) 
    {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        GridData data = new GridData(GridData.FILL);
        data.grabExcessHorizontalSpace = true;
        composite.setLayoutData(data);

        addFirstSection(composite);
        addSeparator(composite);
        addSecondSection(composite);
        return composite;
    }

    /**
     * @param parent
     * @return
     */
    private Composite createDefaultComposite(Composite parent) 
    {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        GridData data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        composite.setLayoutData(data);

        return composite;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() 
    {
        bootstrapModelsText.setText(ModelRuntime.DEFAULT_MODEL_LIST);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    public boolean performOk() 
    {        
        String bootstrapContextVal = ModelProperties.setModelProperty((IResource) getElement(), ModelProperties.BOOTSTRAP_CONTEXT_PROPERTY, bootstrapContextText.getText());
        String webclientConfigVal = ModelProperties.setModelProperty((IResource) getElement(), ModelProperties.WEBCLIENT_CONFIG_PROPERTY,  webclientConfigText.getText());
        String bootstrapModelsVal = ModelProperties.setModelProperty((IResource) getElement(), ModelProperties.BOOTSTRAP_MODELS_PROPERTY, bootstrapModelsText.getText());
        String formConfigVal = ModelProperties.setModelProperty((IResource) getElement(), ModelProperties.FORM_CONFIG_PROPERTY, formConfigText.getText());

        return (bootstrapContextVal != null) && (webclientConfigVal != null) && (bootstrapModelsVal != null) && (formConfigVal != null);

    }

}