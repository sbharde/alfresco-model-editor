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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alfresco.repo.dictionary.M2Constraint;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2NamedValue;
import org.alfresco.repo.dictionary.ModelUtils;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.web.config.WebConfigRuntime;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.pde.internal.ui.editor.FormLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
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
 *  Constraint Editor Dialog
 * 
 * @author drq
 *
 */
@SuppressWarnings("restriction")
public class ConstraintEditDialog extends AbstractModelDialog
{
    // Data members
    protected M2Constraint constraint;
    protected M2Constraint updatedConstraint;

    // UI members
    protected Shell shell;
    protected FormToolkit toolkit;
    protected ScrolledForm form;
    protected Composite left;
    protected Composite right;
    protected Composite bottom;
    protected Text nameText;
    protected Text titleText;
    protected Text descriptionText;
    protected CCombo typeCombo;
    protected Button ok;
    protected Button cancel;
    protected Text refText;

    protected Composite parameter;
    protected Composite  parameterSectionContainer;
    private TableEditor singleFieldEditor;

    private HashMap<String,Control> parameterControls;
    /**
     * Constructor with parameters shell and Constraint
     * 
     * @param parent
     * @param constraint
     * @param service
     * @param model
     */
    public ConstraintEditDialog(Shell parent, M2Constraint constraint, DictionaryService service,M2Model model, WebConfigRuntime webConfigRuntime,IDocument webclientConfigIDocument)
    {
        this(parent,SWT.PRIMARY_MODAL,constraint,service,model,webConfigRuntime,webclientConfigIDocument);
    }

    /**
     * Constructor with parameters shell, style and Constraint
     * 
     * @param parent
     * @param style
     * @param constraint
     * @param service
     * @param model
     */
    public ConstraintEditDialog(Shell parent, int style, M2Constraint constraint, DictionaryService service,M2Model model, WebConfigRuntime webConfigRuntime,IDocument webclientConfigIDocument)
    {
        super(parent, style,service,model,webConfigRuntime,webclientConfigIDocument);
        this.constraint = constraint;
        this.updatedConstraint = ModelUtils.newConstraint(constraint.getName());
        ModelUtils.copyConstraint(this.updatedConstraint, constraint);
    }

    /**
     * @return the webscriptResource
     */
    public M2Constraint getConstraint()
    {
        return constraint;
    }

    /**
     * @param webscriptResource the webscriptResource to set
     */
    public void setConstraint(M2Constraint constraint)
    {
        this.constraint = constraint;
    }

    protected void buildLeftSection()
    {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        Composite body = form.getBody();

        // build left column of the form body
        left = toolkit.createComposite(body);
        left.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
        left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        Section basicInfoSection = FormSectionUtils.createStaticSection(toolkit,left,"General Information","Constraint General Information");        
        Composite  basicInfoSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, basicInfoSection);

        // build basic information section
        Composite basicInfo = toolkit.createComposite(basicInfoSectionContainer);        
        basicInfo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        basicInfo.setLayout(new GridLayout(2, false));

        // build short name text field
        Label label = new Label(basicInfo, SWT.NULL);
        label.setText("&Name:");

        String name = constraint.getName();
        nameText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        nameText.setLayoutData(gridData);
        nameText.setText(name);

        // build short name text field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Title:");

        String title = constraint.getTitle();
        titleText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        titleText.setLayoutData(gridData);
        if (title != null)
        {
            titleText.setText(title);
        }

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Description:");
        gridData = new GridData();
        gridData.verticalAlignment = SWT.TOP;
        label.setLayoutData(gridData);

        String description = constraint.getDescription();
        descriptionText = new Text(basicInfo, SWT.BORDER | SWT.WRAP | SWT.MULTI);
        GC gc = new GC(descriptionText);
        FontMetrics fm = gc.getFontMetrics ();
        int height = 5 * fm.getHeight();
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = SWT.FILL;
        gridData.grabExcessVerticalSpace = true;
        gridData.heightHint = height;
        descriptionText.setLayoutData(gridData);
        if (description!=null)
        {
            descriptionText.setText(description);
        }
        gc.dispose();

        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Reference:");

        String ref = constraint.getRef();
        refText = new Text(basicInfo, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        refText.setLayoutData(gridData);
        if (ref != null)
        {
            refText.setText(ref);
        }

        // build type field
        label = new Label(basicInfo, SWT.NULL);
        label.setText("&Type :");

        typeCombo = new CCombo(basicInfo, SWT.READ_ONLY | SWT.FLAT | SWT.BORDER);
        typeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        for (String typeName : ModelUtils.getAllConstrainTypes().keySet())
        {
            typeCombo.add(typeName);
        }

        if (constraint.getType() != null)
        {
            typeCombo.setText(constraint.getType());
        }


        basicInfoSection.setClient(basicInfoSectionContainer);

    }

    protected void buildParameterComposite (M2Constraint m2c)
    {

        if (parameter != null )
        {
            parameter.dispose();
            parameter = null;

            shell.redraw();
            shell.layout(true);
            shell.pack(true);
        }

        parameterControls = new HashMap<String,Control>();

        List<M2NamedValue> params = m2c.getParameters();

        parameter = toolkit.createComposite(parameterSectionContainer);        
        parameter.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        parameter.setLayout(new GridLayout(2, false));

        for (M2NamedValue m2nv : params)
        {
            String name = m2nv.getName();

            Label label = new Label(parameter, SWT.NULL);
            label.setText("&"+name+":");

            if (m2nv.hasSimpleValue())
            {
                Text paramText = new Text(parameter, SWT.BORDER | SWT.SINGLE);
                GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
                paramText.setLayoutData(gridData);
                if (m2nv.getSimpleValue() != null)
                {
                    paramText.setText(m2nv.getSimpleValue());
                }
                parameterControls.put(name, paramText);
            }

            if (m2nv.hasListValue())
            {
                Group editorGroup = new Group (parameter, SWT.NONE);
                editorGroup.setText (m2nv.getName()+" Editor");
                editorGroup.setLayout(new GridLayout ());

                GridData gridData = new GridData();
                gridData.horizontalAlignment = SWT.FILL;
                gridData.grabExcessHorizontalSpace = true;
                gridData.verticalAlignment = SWT.FILL;
                gridData.grabExcessVerticalSpace = true;
                gridData.heightHint = 150;
                editorGroup.setLayoutData( gridData);

                final Table table = new Table (editorGroup, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CHECK | SWT.FULL_SELECTION);

                // add the action button bar for the type table
                ToolBar toolBar = new ToolBar(editorGroup, SWT.FLAT);    
                toolBar.setLayoutData( new GridData(SWT.CENTER, SWT.CENTER, false, false));
                ToolItem add = new ToolItem(toolBar, SWT.SEPARATOR);
                CLabel clabel = new CLabel(toolBar, SWT.SHADOW_NONE);
                clabel.setText("Add Value");
                clabel.setImage(Activator.getImageDescriptor("icons/add.gif").createImage());
                clabel.setAlignment(SWT.CENTER);
                add.setControl(clabel);
                add.setWidth(120);        
                clabel.addMouseListener(new MouseListener() 
                {
                    @Override
                    public void mouseDoubleClick(MouseEvent arg0)
                    {
                    }
                    @Override
                    public void mouseDown(MouseEvent arg0)
                    {
                        TableItem item = new TableItem (table, SWT.NONE);
                        item.setText(0,"value");
                    }
                    @Override
                    public void mouseUp(MouseEvent arg0)
                    {
                    }            
                });        

                new ToolItem(toolBar,SWT.SEPARATOR);

                ToolItem delete = new ToolItem(toolBar, SWT.SEPARATOR);
                clabel = new CLabel(toolBar, SWT.SHADOW_NONE);
                clabel.setText("Delete value(s)");
                clabel.setImage(Activator.getImageDescriptor("icons/delete.gif").createImage());
                clabel.setAlignment(SWT.CENTER);
                delete.setControl(clabel);
                delete.setWidth(140);        
                clabel.addMouseListener(new MouseListener() 
                {
                    @Override
                    public void mouseDoubleClick(MouseEvent arg0)
                    {
                    }
                    @Override
                    public void mouseDown(MouseEvent arg0)
                    {                        
                        TableItem[] items = table.getItems();
                        int counter = 0;

                        for (int i=0; i<items.length; i++) 
                        {
                            if (items[i].getChecked())
                            {
                                counter ++;
                            }
                        }

                        int [] selected = new int[counter];
                        counter = 0;
                        for (int i=0; i<items.length; i++) 
                        {
                            if (items[i].getChecked())
                            {
                                selected[counter] = i;
                                counter ++;
                            }
                        }
                        table.remove (selected);
                    }
                    @Override
                    public void mouseUp(MouseEvent arg0)
                    {
                    }            
                });

                toolBar.pack();

                table.setLinesVisible (true);
                table.setHeaderVisible (true);
                GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
                gridData1.heightHint = 300;
                table.setLayoutData (gridData1);

                parameterSectionContainer.addControlListener(new ControlAdapter() 
                {
                    public void controlResized(ControlEvent e) 
                    {
                        if (! table.isDisposed())
                        {
                            Rectangle area = parameterSectionContainer.getClientArea();
                            Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                            ScrollBar vBar = table.getVerticalBar();
                            int width = area.width - table.computeTrim(0,0,0,0).width - vBar.getSize().x;
                            if (size.y > area.height + table.getHeaderHeight()) 
                            {
                                // Subtract the scrollbar width from the total column width
                                // if a vertical scrollbar will be required
                                Point vBarSize = vBar.getSize();
                                width -= vBarSize.x;
                            }
                            Point oldSize = table.getSize();
                            if (oldSize.x > area.width) 
                            {
                                // table is getting smaller so make the columns 
                                // smaller first and then resize the table to
                                // match the client area width
                                table.getColumns()[0].setWidth(width/2);
                                table.setSize(area.width, table.computeTrim(0,0,0,0).height);
                            } 
                            else
                            {
                                // table is getting bigger so make the table 
                                // bigger first and then make the columns wider
                                // to match the client area width
                                table.setSize(area.width, table.computeTrim(0,0,0,0).height);
                                table.getColumns()[0].setWidth(width/2);
                            }
                        }
                    }
                }); 

                // populate table titles
                String[] titles = {"value"};

                for (int i=0; i<titles.length; i++) 
                {
                    TableColumn column = new TableColumn(table, SWT.BORDER);
                    column.setText (titles [i]);
                }


                for (String value : m2nv.getListValue())
                {
                    TableItem item = new TableItem (table, SWT.NONE);
                    item.setText(0, value);
                }

                table.addMouseListener (new MouseAdapter () 
                {
                    public void mouseDown(MouseEvent e) 
                    {                       
                        int index = table.getSelectionIndex ();
                        Point pt = new Point (e.x, e.y);
                        TableItem newItem = table.getItem (pt);

                        if (newItem == null) 
                        {
                            if (singleFieldEditor != null)
                            {
                                singleFieldEditor.getEditor().dispose();
                            }
                            return;
                        }
                        table.showSelection ();
                        // make sure the double click case?
                        if (singleFieldEditor != null)
                        {
                            singleFieldEditor.getEditor().dispose();
                        }
                        singleFieldEditor = new TableEditor (table);
                        Text urlText = new Text (table, SWT.NONE);
                        urlText.setText(newItem.getText(0));
                        singleFieldEditor.grabHorizontal = true;
                        singleFieldEditor.setEditor(urlText, newItem, 0);
                        final int currentIndex = index;


                        urlText.addModifyListener
                        (
                                new ModifyListener() 
                                {
                                    public void modifyText(ModifyEvent e) 
                                    {
                                        //Text text = (Text) e.widget;
                                    }
                                }
                        );

                        urlText.addFocusListener
                        (
                                new FocusListener()
                                {
                                    public void focusGained(FocusEvent arg0)
                                    {
                                    }
                                    public void focusLost(FocusEvent arg0)
                                    {
                                        Text text = (Text) arg0.widget;
                                        table.getItem(currentIndex).setText(text.getText());
                                        if (singleFieldEditor != null)
                                        {                                            
                                            singleFieldEditor.getEditor().dispose();
                                        }
                                        return;                                
                                    }

                                }
                        );

                        urlText.setFocus();

                    }
                });


                for (int i=0; i<titles.length; i++) 
                {
                    table.getColumn (i).pack ();
                }

                parameterControls.put(name, table);

            }
        }

    }

    protected void buildRightSection()
    {
        Composite body = form.getBody();

        right = toolkit.createComposite(body);
        right.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
        right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        // setup option section
        Section parameterSection = FormSectionUtils.createStaticSection(toolkit,right,"Parameter Configurations","View or Update Parameter Configurations.");

        parameterSectionContainer = FormSectionUtils.createStaticSectionClient(toolkit, parameterSection);

        buildParameterComposite (constraint);

        parameterSection.setClient(parameterSectionContainer);    

    }

    protected void buildBottomSection()
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

        // Create the cancel button and add a handler
        cancel = new Button(buttonBar, SWT.PUSH);
        cancel.setText("Cancel");
    }

    protected void prepareUpdatedConstraint()
    {
        updatedConstraint = ModelUtils.newConstraint(nameText.getText());

        updatedConstraint.setTitle(titleText.getText());
        updatedConstraint.setDescription(descriptionText.getText());
        if (refText.getText() != null && !refText.getText().equals(""))
        {
            updatedConstraint.setRef(refText.getText());
        }
        updatedConstraint.setType(typeCombo.getText());

        for (String name : parameterControls.keySet())
        {
            Control control = parameterControls.get(name);
            if (control instanceof Text)
            {
                updatedConstraint.createParameter(name, ((Text)control).getText());
            }
            if (control instanceof Table)
            {
                Table paramTable = (Table) control;
                ArrayList<String> values = new ArrayList<String>();
                for (TableItem item : paramTable.getItems())
                {
                    values.add(item.getText(0));
                }
                updatedConstraint.createParameter(name, values);
            }
        }

    }

    protected void addProcessingListeners()
    {
        typeCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String selectedConstraintType = typeCombo.getText();
                buildParameterComposite (ModelUtils.getAllConstrainTypes().get(selectedConstraintType));

                Composite body = form.getBody();

                body.redraw();
                body.layout(true);
                body.pack(true);

                shell.redraw();
                shell.layout(true);
                shell.pack(true);
                shell.setSize(1000, 600);

            };
        });

        ok.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                prepareUpdatedConstraint();
                shell.close();
            }
        });

        cancel.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                updatedConstraint  = null;
                shell.close();
            }
        });                        
    }

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
    }

    protected void initUI()
    {
        Shell parent = getParent();
        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
        shell.setText(getText());

        // set up shell layout
        FillLayout fillLayout = new FillLayout();
        fillLayout.type = SWT.VERTICAL;
        shell.setLayout(fillLayout);
        shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        toolkit = new FormToolkit(shell.getDisplay());        
        form = toolkit.createScrolledForm(shell);
        form.setText("Constraint Editor");        
    }
    /**
     * @return
     */
    public M2Constraint open () 
    {
        initUI();

        buildHeadSection();

        Composite body = form.getBody();
        body.setLayout(FormLayoutFactory.createFormTableWrapLayout(true, 2));
        body.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        buildLeftSection();
        buildRightSection();
        buildBottomSection();
        addProcessingListeners();

        body.pack();
        body.layout(true);
        body.redraw();

        shell.pack();
        shell.layout(true);
        shell.setSize(1000, 600);
        shell.open();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) 
        {
            if (!display.readAndDispatch()) display.sleep();
        }

        return updatedConstraint;
    }
}
