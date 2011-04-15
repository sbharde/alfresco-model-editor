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
package org.springframework.extensions.surf.alfresco.model.editor.control;

import org.alfresco.repo.dictionary.ModelRuntime;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.part.FileEditorInput;
import org.springframework.extensions.surf.alfresco.model.editor.ModelEditor;

/**
 * @author drq
 *
 */
public class ModelListEditor
{
    private IFile modelXmlFile;
    private ModelEditor modelEditor;

    /**
     * The list widget; <code>null if none
     * (before creation or after disposal).
     */
    private List selectedList;

    private Text candidateText;

    /**
     * The button box containing the Add, Remove, Up, and Down buttons;
     * <code>null if none (before creation or after disposal).
     */
    private Composite buttonBox;
    private Composite buttonBox2;

    /**
     * The Add button.
     */
    private Button addButton;

    /**
     * The Add button.
     */
    private Button restoreButton;

    /**
     * The Remove button.
     */
    private Button removeButton;

    /**
     * The Up button.
     */
    private Button upButton;

    /**
     * The Down button.
     */
    private Button downButton;

    /**
     * The selection listener.
     */
    private SelectionListener selectionListener;

    /**
     * Property name constant (value <code>"field_editor_is_valid")
     * to signal a change in the validity of the value of this field editor.
     */
    public static final String IS_VALID = "field_editor_is_valid";//$NON-NLS-1$

    /**
     * Property name constant (value <code>"field_editor_value")
     * to signal a change in the value of this field editor.
     */
    public static final String VALUE = "field_editor_value";//$NON-NLS-1$

    /** 
     * Gap between label and control.
     */
    protected static final int HORIZONTAL_GAP = 8;

    /**
     * The name of the preference displayed in this field editor.
     */
    private String preferenceName;

    /**
     * Indicates whether the default value is currently displayed,
     * initially <code>false.
     */
    private boolean isDefaultPresented = false;

    /**
     * The label's text.
     */
    private String labelText;

    /**
     * The label control.
     */
    private Label label;

    /**
     * Creates a new field editor.
     */
    protected ModelListEditor() 
    {
    }

    /**
     * Creates a new field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    protected ModelListEditor(String name, String labelText, Composite parent) 
    {
        init(name, labelText);
        createControl(parent);
    }

    /**
     * @param modelXmlFile
     */
    public ModelListEditor(String name, String labelText, Composite parent,ModelEditor modelEditor)
    {
        this(name, labelText, parent);
        this.modelEditor = modelEditor;
        this.modelXmlFile = ((FileEditorInput)modelEditor.getEditorInput()).getFile();
    }

    /**
     * @return the modelXmlFile
     */
    public IFile getModelXmlFile()
    {
        return modelXmlFile;
    }

    /**
     * @param modelXmlFile the modelXmlFile to set
     */
    public void setModelXmlFile(IFile modelXmlFile)
    {
        this.modelXmlFile = modelXmlFile;
    }

    /**
     * Applies a font.
     * <p>
     * The default implementation of this framework method
     * does nothing. Subclasses should override this method
     * if they want to change the font of the SWT control to
     * a value different than the standard dialog font.
     * </p>
     */
    protected void applyFont() 
    {
    }

    /**
     * Checks if the given parent is the current parent of the
     * supplied control; throws an (unchecked) exception if they
     * are not correctly related.
     *
     * @param control the control
     * @param parent the parent control
     */
    protected void checkParent(Control control, Composite parent) 
    {
        Assert.isTrue(control.getParent() == parent, "Different parents");
    }

    /**
     * Clears the error message from the message line.
     */
    protected void clearErrorMessage() 
    {
    }

    /**
     * Clears the normal message from the message line.
     */
    protected void clearMessage() 
    {
    }

    /**
     * Returns the number of pixels corresponding to the
     * given number of horizontal dialog units.
     * <p>
     * Clients may call this framework method, but should not override it.
     * </p>
     *
     * @param control the control being sized
     * @param dlus the number of horizontal dialog units
     * @return the number of pixels
     */
    protected int convertHorizontalDLUsToPixels(Control control, int dlus) 
    {
        GC gc = new GC(control);
        gc.setFont(control.getFont());
        int averageWidth = gc.getFontMetrics().getAverageCharWidth();
        gc.dispose();

        double horizontalDialogUnitSize = averageWidth * 0.25;

        return (int) Math.round(dlus * horizontalDialogUnitSize);
    }

    /**
     * Returns the number of pixels corresponding to the
     * given number of vertical dialog units.
     * <p>
     * Clients may call this framework method, but should not override it.
     * </p>
     *
     * @param control the control being sized
     * @param dlus the number of vertical dialog units
     * @return the number of pixels
     */
    protected int convertVerticalDLUsToPixels(Control control, int dlus) 
    {
        GC gc = new GC(control);
        gc.setFont(control.getFont());
        int height = gc.getFontMetrics().getHeight();
        gc.dispose();

        double verticalDialogUnitSize = height * 0.125;

        return (int) Math.round(dlus * verticalDialogUnitSize);
    }

    /**
     * Creates this field editor's main control containing all of its
     * basic controls.
     *
     * @param parent the parent control
     */
    protected void createControl(Composite parent) 
    {
        GridLayout layout = new GridLayout();
        layout.numColumns = getNumberOfControls();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = HORIZONTAL_GAP;
        parent.setLayout(layout);
        doFillIntoGrid(parent, layout.numColumns);
    }

    /**
     * Fills this field editor's basic controls into the given parent. 
     *
     * @param parent the composite used as a parent for the basic controls;
     *  the parent's layout must be a <code>GridLayout
     * @param numColumns the number of columns
     */
    public void fillIntoGrid(Composite parent, int numColumns) 
    {
        Assert.isTrue(numColumns >= getNumberOfControls());
        Assert.isTrue(parent.getLayout() instanceof GridLayout);
        doFillIntoGrid(parent, numColumns);
    }

    /**
     * Returns the label control. 
     *
     * @return the label control, or <code>null
     *  if no label control has been created
     */
    protected Label getLabelControl() 
    {
        return label;
    }

    /**
     * Returns this field editor's label component.
     * <p>
     * The label is created if it does not already exist
     * </p>
     *
     * @param parent the parent
     * @return the label control
     */
    public Label getLabelControl(Composite parent) 
    {
        if (label == null) 
        {
            label = new Label(parent, SWT.LEFT);
            label.setFont(parent.getFont());
            String text = getLabelText();
            if (text != null) 
            {
                label.setText(text);
            }
            label.addDisposeListener(new DisposeListener() 
            {
                public void widgetDisposed(DisposeEvent event) 
                {
                    label = null;
                }
            });
        } 
        else 
        {
            checkParent(label, parent);
        }
        return label;
    }

    /**
     * Returns this field editor's label text.
     *
     * @return the label text
     */
    public String getLabelText() 
    {
        return labelText;
    }


    /**
     * Returns the name of the preference this field editor operates on.
     *
     * @return the name of the preference
     */
    public String getPreferenceName() 
    {
        return preferenceName;
    }

    /**
     * Initialize the field editor with the given preference name and label.
     * 
     * @param name the name of the preference this field editor works on
     * @param text the label text of the field editor
     */
    protected void init(String name, String text) 
    {
        Assert.isNotNull(name);
        Assert.isNotNull(text);
        preferenceName = name;
        this.labelText = text;
    }

    /**
     * Returns whether this field editor contains a valid value.
     * <p>
     * The default implementation of this framework method
     * returns <code>true. Subclasses wishing to perform
     * validation should override both this method and
     * <code>refreshValidState.
     * </p>
     * 
     * @return <code>true if the field value is valid,
     *   and <code>false if invalid
     * @see #refreshValidState()
     */
    public boolean isValid() 
    {
        return true;
    }

    /**
     * Initializes this field editor with the preference value from
     * the preference store.
     */
    public void load() 
    {
        if (hasPropertySet())
        {
            isDefaultPresented = false;
            doLoad();
        }
        else
        {
            isDefaultPresented = true;
            doLoadDefault();
        }
        refreshValidState();
    }

    /**
     * Initializes this field editor with the default preference value
     * from the preference store.
     */
    public void loadDefault() 
    {
        isDefaultPresented = true;
        doLoadDefault();
        refreshValidState();
    }

    /**
     * @return
     */
    public boolean hasPropertySet() 
    {
        try
        {
            String propertyValue = modelXmlFile.getPersistentProperty(new QualifiedName("", preferenceName));
            if (propertyValue != null)
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (CoreException e)
        {
            return false;
        }
    }

    /**
     * Returns whether this field editor currently presents the
     * default value for its preference.
     * 
     * @return <code>true if the default value is presented,
     *   and <code>false otherwise
     */
    public boolean presentsDefaultValue() 
    {
        return isDefaultPresented;
    }

    /**
     * Refreshes this field editor's valid state after a value change
     * and fires an <code>IS_VALID property change event if
     * warranted.
     * <p>
     * The default implementation of this framework method does
     * nothing. Subclasses wishing to perform validation should override
     * both this method and <code>isValid.
     * </p>
     *
     * @see #isValid
     */
    protected void refreshValidState() 
    {
    }

    /**
     * Sets this field editor's label text.
     * The label is typically presented to the left of the entry field.
     *
     * @param text the label text
     */
    public void setLabelText(String text) 
    {
        Assert.isNotNull(text);
        labelText = text;
        if (label != null) 
        {
            label.setText(text);
        }
    }

    /**
     * Stores this field editor's value back into the preference store.
     */
    public void store() 
    {
        doStore();
    }

    /**
     * Set the GridData on button to be one that is spaced for the
     * current font.
     * @param button the button the data is being set on.
     */

    protected void setButtonLayoutData(Button button) 
    {

        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);

        // Compute and store a font metric
        GC gc = new GC(button);
        gc.setFont(button.getFont());
        FontMetrics fontMetrics = gc.getFontMetrics();
        gc.dispose();

        int widthHint = org.eclipse.jface.dialogs.Dialog
        .convertVerticalDLUsToPixels(fontMetrics,
                IDialogConstants.BUTTON_WIDTH);
        data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT,
                SWT.DEFAULT, true).x);
        button.setLayoutData(data);
    }


    /**
     * Notifies that the Add button has been pressed.
     */
    private void addPressed() 
    {
        String input = getNewInputObject();

        if (input != null) 
        {
            int index = selectedList.getSelectionIndex();
            if (index >= 0) 
            {
                selectedList.add(input, index + 1);
            } 
            else 
            {
                selectedList.add(input, 0);
            }
            selectionChanged();
            store();
        }
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void adjustForNumColumns(int numColumns) 
    {
        Control control = getLabelControl();
        ((GridData) control.getLayoutData()).horizontalSpan = numColumns;
        ((GridData) selectedList.getLayoutData()).horizontalSpan = numColumns - 1;
    }

    /**
     * Creates the Add, Remove, Up, and Down button in the given button box.
     *
     * @param box the box for the buttons
     */
    private void createButtons(Composite box) 
    {
        // addButton = createPushButton(box, "ListEditor.add");//$NON-NLS-1$
        restoreButton = createPushButton(box, "Restore Defaults");
        removeButton = createPushButton(box, "ListEditor.remove");//$NON-NLS-1$
        upButton = createPushButton(box, "ListEditor.up");//$NON-NLS-1$
        downButton = createPushButton(box, "ListEditor.down");//$NON-NLS-1$
    }

    private void createButtons2(Composite box) 
    {
        addButton = createPushButton(box, "ListEditor.add");//$NON-NLS-1$
    }

    /**
     * Helper method to create a push button.
     * 
     * @param parent the parent control
     * @param key the resource name used to supply the button's label text
     * @return Button
     */
    private Button createPushButton(Composite parent, String key) 
    {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(JFaceResources.getString(key));
        button.setFont(parent.getFont());
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        int widthHint = convertHorizontalDLUsToPixels(button,
                IDialogConstants.BUTTON_WIDTH);
        data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT,
                SWT.DEFAULT, true).x);
        button.setLayoutData(data);
        button.addSelectionListener(getSelectionListener());
        return button;
    }

    /**
     * Creates a selection listener.
     */
    public void createSelectionListener() 
    {
        selectionListener = new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                Widget widget = event.widget;
                if (widget == addButton) 
                {
                    addPressed();
                } 
                else if (widget == removeButton) 
                {
                    removePressed();
                } 
                else if (widget == upButton) 
                {
                    upPressed();
                } 
                else if (widget == downButton) 
                {
                    downPressed();
                }
                else if (widget == restoreButton) 
                {
                    restorePressed();
                } 
                else if (widget == selectedList) 
                {
                    selectionChanged();
                }
            }
        };
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doFillIntoGrid(Composite parent, int numColumns) 
    {
        Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        control.setLayoutData(gd);

        candidateText = getCandidateTextControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = numColumns - 1;
        gd.grabExcessHorizontalSpace = true;
        candidateText.setLayoutData(gd);

        buttonBox2 = getButtonBoxControl2(parent);
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        buttonBox2.setLayoutData(gd);

        selectedList = getSelectedListControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = numColumns - 1;
        gd.grabExcessHorizontalSpace = true;
        selectedList.setLayoutData(gd);

        buttonBox = getButtonBoxControl(parent);
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        buttonBox.setLayoutData(gd);
    }

    /**
     * Notifies that the Down button has been pressed.
     */
    private void downPressed() 
    {
        swap(false);
    }

    /**
     * Returns this field editor's button box containing the Add, Remove,
     * Up, and Down button.
     *
     * @param parent the parent control
     * @return the button box
     */
    public Composite getButtonBoxControl(Composite parent) 
    {
        if (buttonBox == null) 
        {
            buttonBox = new Composite(parent, SWT.NULL);
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            buttonBox.setLayout(layout);
            createButtons(buttonBox);
            buttonBox.addDisposeListener(new DisposeListener() 
            {
                public void widgetDisposed(DisposeEvent event) 
                {
                    addButton = null;
                    removeButton = null;
                    restoreButton = null;
                    upButton = null;
                    downButton = null;
                    buttonBox = null;
                }
            });
        } 
        else 
        {
            checkParent(buttonBox, parent);
        }

        selectionChanged();
        return buttonBox;
    }

    public Composite getButtonBoxControl2(Composite parent) 
    {
        if (buttonBox2 == null) 
        {
            buttonBox2 = new Composite(parent, SWT.NULL);
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            buttonBox2.setLayout(layout);
            createButtons2(buttonBox2);
            buttonBox2.addDisposeListener(new DisposeListener() 
            {
                public void widgetDisposed(DisposeEvent event) 
                {
                    addButton = null;
                }
            });
        } 
        else 
        {
            checkParent(buttonBox2, parent);
        }

        //selectionChanged();
        return buttonBox2;
    }

    /**
     * Returns this field editor's list control.
     *
     * @param parent the parent control
     * @return the list control
     */
    public List getSelectedListControl(Composite parent)
    {
        if (selectedList == null) 
        {
            selectedList = new List(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL
                    | SWT.H_SCROLL);
            selectedList.setFont(parent.getFont());
            selectedList.addSelectionListener(getSelectionListener());
            selectedList.addDisposeListener(new DisposeListener() 
            {
                public void widgetDisposed(DisposeEvent event) 
                {
                    selectedList = null;
                }
            });
        } 
        else 
        {
            checkParent(selectedList, parent);
        }
        return selectedList;
    }

    public Text getCandidateTextControl(Composite parent)
    {
        if (candidateText == null) 
        {
            candidateText = new Text(parent, SWT.SINGLE | SWT.BORDER);
            candidateText.setFont(parent.getFont());
            candidateText.addSelectionListener(getSelectionListener());
            candidateText.addDisposeListener(new DisposeListener() 
            {
                public void widgetDisposed(DisposeEvent event) 
                {
                    candidateText = null;
                }
            });
        } 
        else 
        {
            checkParent(candidateText, parent);
        }
        return candidateText;
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    public int getNumberOfControls() 
    {
        return 2;
    }

    /**
     * Returns this field editor's selection listener.
     * The listener is created if nessessary.
     *
     * @return the selection listener
     */
    private SelectionListener getSelectionListener() 
    {
        if (selectionListener == null) 
        {
            createSelectionListener();
        }
        return selectionListener;
    }

    /**
     * Returns this field editor's shell.
     * <p>
     * This method is internal to the framework; subclassers should not call
     * this method.
     * </p>
     *
     * @return the shell
     */
    protected Shell getShell() 
    {
        if (addButton == null) 
        {
            return null;
        }
        return addButton.getShell();
    }

    /**
     * Notifies that the Remove button has been pressed.
     */
    private void removePressed() 
    {
        int index = selectedList.getSelectionIndex();
        if (index >= 0) 
        {
            selectedList.remove(index);
            selectionChanged();
            store();
        }
    }

    /**
     * Notifies that the Restore button has been pressed.
     */
    private void restorePressed() 
    {
        selectedList.removeAll();
        loadDefault();
        selectionChanged();
        store();
    }

    /**
     * Notifies that the list selection has changed.
     */
    private void selectionChanged() 
    {
        int index = selectedList.getSelectionIndex();
        int size = selectedList.getItemCount();

        removeButton.setEnabled(index >= 0);
        upButton.setEnabled(size > 1 && index > 0);
        downButton.setEnabled(size > 1 && index >= 0 && index < size - 1);
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    public void setFocus() 
    {
        if (selectedList != null) 
        {
            selectedList.setFocus();
        }
    }

    /**
     * Moves the currently selected item up or down.
     *
     * @param up <code>true if the item should move up,
     *  and <code>false if it should move down
     */
    private void swap(boolean up) 
    {
        int index = selectedList.getSelectionIndex();
        int target = up ? index - 1 : index + 1;

        if (index >= 0) 
        {
            String[] selection = selectedList.getSelection();
            Assert.isTrue(selection.length == 1);
            selectedList.remove(index);
            selectedList.add(selection[0], target);
            selectedList.setSelection(target);
        }
        selectionChanged();
    }

    /**
     * Notifies that the Up button has been pressed.
     */
    private void upPressed() 
    {
        swap(true);
    }

    /*
     * @see FieldEditor.setEnabled(boolean,Composite).
     */
    public void setEnabled(boolean enabled, Composite parent) 
    {
        getSelectedListControl(parent).setEnabled(enabled);
        addButton.setEnabled(enabled);
        restoreButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
        upButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
    }

    protected String createList(String[] arg0)
    {
        StringBuffer sb = new StringBuffer();        
        if(arg0 != null && arg0.length > 0)
        {
            for (int i = 0 ; i < arg0.length -1 ; i++)
            {
                sb.append(arg0[i]).append(";");
            }
            sb.append(arg0[arg0.length-1]);
        }
        return sb.toString();
    }

    protected String getNewInputObject()
    {        
        String candidate = candidateText.getText();

        if (candidate != null)
        {
            for (String selected : selectedList.getItems())
            {
                if (selected.equals(candidate))
                {
                    return null;
                }
            }
        }

        return candidateText.getText();
    }

    protected String[] parseString(String arg0)
    {
        return arg0.split(";");
    }

    /**
     * 
     */
    protected void doLoad() 
    {
        if (selectedList != null && modelXmlFile != null) 
        {
            String s;
            try
            {
                s = modelXmlFile.getPersistentProperty(new QualifiedName("", preferenceName));
                String[] array = parseString(s);
                for (int i = 0; i < array.length; i++) 
                {
                    selectedList.add(array[i]);
                }
            }
            catch (CoreException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     */
    protected void doLoadDefault() 
    {
        if (selectedList != null) 
        {
            selectedList.removeAll();
            String s = ModelRuntime.DEFAULT_MODEL_LIST;
            String[] array = parseString(s);
            for (int i = 0; i < array.length; i++) 
            {
                selectedList.add(array[i]);
            }
        }
    }

    /**
     * 
     */
    protected void doStore() 
    {
        String s = createList(selectedList.getItems());
        if (s != null) 
        {
            try
            {
                if (modelXmlFile != null)
                {
                    modelXmlFile.setPersistentProperty(new QualifiedName("", preferenceName),s);
                    modelEditor.getOverviewPage().reload();
                }
            } 
            catch (CoreException e)
            {
                e.printStackTrace();
            }
        }
    }

}
