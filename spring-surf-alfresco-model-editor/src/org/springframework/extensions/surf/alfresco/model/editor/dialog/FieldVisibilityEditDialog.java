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

import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.config.forms.FieldVisibilityInstructionCustom;
import org.alfresco.web.config.forms.FormConfigRuntime;
import org.alfresco.web.config.forms.Mode;
import org.alfresco.web.config.forms.Visibility;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.springframework.extensions.surf.commons.ui.FormSectionUtils;

/**
 * Model Class Editor Dialog
 * 
 * @author drq
 *
 */
public class FieldVisibilityEditDialog extends AbstractModelDialog
{
    private FieldVisibilityInstructionCustom fvInstruction;
    private FieldVisibilityInstructionCustom updatedFVInstruction;

    private CCombo fieldIdCombo;
    private Button editModeButton;
    private Button createModeButton;
    private Button viewModeButton;

    private Button[] displayModeButtons;
    private Button[] forceModeButtons;

    /**
     * Constructor with parameters shell, style and Class
     * 
     * @param parent
     * @param style
     * @param clazz
     * @param service
     * @param model
     */
    public FieldVisibilityEditDialog(Shell parent, int style, DictionaryService service, M2Model model, FormConfigRuntime formConfigRuntime,IDocument formConfigIDocument, FieldVisibilityInstructionCustom fvInstruction)
    {
        super(parent, style,service,model,formConfigRuntime,formConfigIDocument);
        this.fvInstruction = fvInstruction;
    }

    /**
     * Constructor with parameters shell and Class
     * 
     * @param parent
     * @param clazz
     * @param service
     * @param model
     */
    public FieldVisibilityEditDialog(Shell parent, DictionaryService service, M2Model model, FormConfigRuntime formConfigRuntime,IDocument formConfigIDocument,FieldVisibilityInstructionCustom fvInstruction)
    {
        this(parent, SWT.PRIMARY_MODAL, service, model,formConfigRuntime,formConfigIDocument, fvInstruction);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.alfresco.model.editor.dialog.AbstractModelDialog#buildLeftSection()
     */
    protected void buildLeftSection ()
    {
        super.buildLeftSection();

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        Section basicInfoSection = FormSectionUtils.createStaticSection(toolkit,left,"General Information","Field Visibility General Information");        
        Composite  basicInfoSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, basicInfoSection);

        // build basic information section
        Composite basicInfo = toolkit.createComposite(basicInfoSectionContainer);        
        basicInfo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        basicInfo.setLayout(new GridLayout(2, false));

        // build short name text field
        Label label = new Label(basicInfo, SWT.NULL);
        label.setText("&Field ID:");

        fieldIdCombo = new CCombo(basicInfo, SWT.FLAT | SWT.BORDER);
        fieldIdCombo.setLayoutData(gridData);
        fieldIdCombo.setText(fvInstruction.getFieldId());
        for (QName aspect : this.getService().getAllAspects())
        {
            for (QName property : this.getService().getAspect(aspect).getProperties().keySet())
            {
                fieldIdCombo.add(property.getPrefixString());
            }
        }
        for (QName type : this.getService().getAllTypes())
        {
            for (QName property : this.getService().getType(type).getProperties().keySet())
            {
                fieldIdCombo.add(property.getPrefixString());
            }
        }

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Display Mode:");

        Composite displayModeComposite = new Composite(basicInfo, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        displayModeComposite.setLayout(layout);

        displayModeButtons = new Button[2];
        displayModeButtons[0] = new Button (displayModeComposite, SWT.RADIO);
        displayModeButtons[0].setText("Show");
        displayModeButtons[1] = new Button (displayModeComposite, SWT.RADIO);
        displayModeButtons[1].setText("Hide");
        Listener buttonListener = new Listener () 
        {
            public void handleEvent (Event event) 
            {                
                for (Button button : displayModeButtons) 
                {
                    button.setSelection(false);
                }
                Widget eventWidget = event.widget;
                if (eventWidget instanceof Button)
                {
                    ((Button) eventWidget).setSelection(true);
                }
            }
        };

        for (Button button : displayModeButtons) 
        {
            button.addListener (SWT.Selection,buttonListener);
        }
        String displayMode = fvInstruction.getShowOrHide().toString().toLowerCase();
        if (Visibility.SHOW.toString().toLowerCase().equals(displayMode))
        {
            displayModeButtons[0].setSelection(true);
        }
        else
        {
            displayModeButtons[1].setSelection(true);
        }

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Modes:");

        Group modeEditorGroup = new Group (basicInfo, SWT.NONE);
        modeEditorGroup.setText ("Modes");
        modeEditorGroup.setLayout(new GridLayout (3, true));
        modeEditorGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        viewModeButton = new Button (modeEditorGroup, SWT.CHECK);
        viewModeButton.setText ("view");
        editModeButton = new Button (modeEditorGroup, SWT.CHECK);
        editModeButton.setText ("edit");
        createModeButton = new Button (modeEditorGroup, SWT.CHECK);
        createModeButton.setText ("create");

        if (fvInstruction.getModes() != null)
        {
            if (fvInstruction.getModes().contains(Mode.VIEW))
            {
                viewModeButton.setSelection(true);
            }
            if (fvInstruction.getModes().contains(Mode.EDIT))
            {
                editModeButton.setSelection(true);
            }
            if (fvInstruction.getModes().contains(Mode.CREATE))
            {
                createModeButton.setSelection(true);
            }
        }

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Force:");

        Composite forceModeComposite = new Composite(basicInfo, SWT.NULL);
        GridLayout layout2 = new GridLayout();
        layout2.numColumns = 3;
        forceModeComposite.setLayout(layout2);

        forceModeButtons = new Button[3];
        forceModeButtons[0] = new Button (forceModeComposite, SWT.RADIO);
        forceModeButtons[0].setText("True");
        forceModeButtons[1] = new Button (forceModeComposite, SWT.RADIO);
        forceModeButtons[1].setText("False");
        forceModeButtons[2] = new Button (forceModeComposite, SWT.RADIO);
        forceModeButtons[2].setText("None");
        Listener buttonListener2 = new Listener () 
        {
            public void handleEvent (Event event) 
            {                
                for (Button button : forceModeButtons) 
                {
                    button.setSelection(false);
                }
                Widget eventWidget = event.widget;
                if (eventWidget instanceof Button)
                {
                    ((Button) eventWidget).setSelection(true);
                }
            }
        };

        for (Button button : forceModeButtons) 
        {
            button.addListener (SWT.Selection,buttonListener2);
        }
        if (fvInstruction.getForce() != null && !fvInstruction.getForce().equals(""))
        {
            if (fvInstruction.getForce().equals("true"))
            {
                forceModeButtons[0].setSelection(true);
            }
            else
            {
                forceModeButtons[1].setSelection(true);
            }
        }
        else
        {
            forceModeButtons[2].setSelection(true);
        }

        basicInfoSection.setClient(basicInfoSectionContainer);
    }

    protected void buildRightSection ()
    {
        super.buildRightSection();
    }
    /**
     * @return
     */
    public void addProcessingListeners() 
    {		
        ok.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                if (fieldIdCombo.getText() != null && !fieldIdCombo.getText().equals(""))
                {
                    String modeStr = "";
                    if (viewModeButton.getSelection())
                    {
                        modeStr += "view,";
                    }
                    if (editModeButton.getSelection())
                    {
                        modeStr += "edit,";
                    }
                    if (createModeButton.getSelection())
                    {
                        modeStr += "create,";
                    }
                    if (modeStr.endsWith(","))
                    {
                        modeStr = modeStr.substring(0,modeStr.length()-1);
                    }
                    String displayMode = Visibility.SHOW.toString().toLowerCase();
                    if (displayModeButtons[1].getSelection())
                    {
                        displayMode = Visibility.HIDE.toString().toLowerCase();
                    }
                    String forceMode = null;
                    if (forceModeButtons[0].getSelection())
                    {
                        forceMode = "true";
                    }
                    if (forceModeButtons[1].getSelection())
                    {
                        forceMode = "false";
                    }
                    updatedFVInstruction = new FieldVisibilityInstructionCustom(displayMode, fieldIdCombo.getText(), modeStr, forceMode);
                }
                else
                {
                    updatedFVInstruction = null;
                }
                shell.close();
            }
        });

        cancel.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                updatedFVInstruction = null;
                shell.close();
            }
        });                

    }

    /**
     * @return
     */
    public FieldVisibilityInstructionCustom open()
    {
        initUI("Field Visibility Editor");
        buildHeadSection();
        buildLeftSection();
        buildRightSection();
        buildBottomSection();
        addProcessingListeners();
        openUI(1200,700);
        return updatedFVInstruction;
    }

}
