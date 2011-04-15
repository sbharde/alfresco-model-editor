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
package org.alfresco.web.config.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.alfresco.web.config.ConfigRuntime;
import org.alfresco.web.config.XmlUtils;
import org.springframework.extensions.config.Config;
import org.w3c.dom.Element;

/**
 * @author drq
 *
 */
public class FormConfigRuntime extends ConfigRuntime
{

    protected Config globalConfig;
    protected DefaultControlsConfigElement globalDefaultControls;
    protected ConstraintHandlersConfigElement globalConstraintHandlers;
    protected DependenciesConfigElement dependencies;

    /**
     * @param configXml
     */
    public FormConfigRuntime(String configXml)
    {
        super(configXml);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.config.ConfigRuntime#getBaseConfigXml()
     */
    @Override
    public String getBaseConfigXml()
    {
        String baseFormConfigXml = "<alfresco-config>"  
            +"<plug-ins>"
            +" <evaluators>"
            +"  <evaluator id=\"node-type\" class=\"org.alfresco.web.config.forms.NodeTypeEvaluator\" />"
            +"  <evaluator id=\"model-type\" class=\"org.alfresco.web.config.forms.ModelTypeEvaluator\" />"
            +"  <evaluator id=\"aspect\" class=\"org.alfresco.web.config.forms.AspectEvaluator\" />"
            +"</evaluators>"
            +"<element-readers>"
            +" <element-reader element-name=\"forms\" class=\"org.alfresco.web.config.forms.FormsElementReader\"/>"
            +"</element-readers>"
            +" </plug-ins>"
            +"</alfresco-config>";
        return baseFormConfigXml;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.config.ConfigRuntime#initConfigRuntime()
     */
    @Override
    public boolean initConfigRuntime()
    {
        boolean status = super.initConfigRuntime();
        // Get global configuration
        globalConfig = xmlConfigService.getGlobalConfig();

        if (globalConfig != null)
        {
            FormsConfigElement globalForms = (FormsConfigElement)globalConfig.getConfigElement("forms");
            globalDefaultControls = globalForms.getDefaultControls();   
            globalConstraintHandlers = globalForms.getConstraintHandlers();
            dependencies = globalForms.getDependencies();
        }

        return status;
    }    

    /**
     * @return
     */
    public Map<String, ConstraintHandlerDefinition> getGlobalConstraintHandlers()
    {
        return globalConstraintHandlers == null ? null : globalConstraintHandlers.getItems();        
    }

    /**
     * @return
     */
    public Map<String, Control> getGlobalDefaultControls()
    {
        return globalDefaultControls == null ? null : globalDefaultControls.getItems();        
    }

    /**
     * @return
     */
    public String[] getCssDependencies()
    {
        return dependencies == null ? null : dependencies.getCss();
    }

    /**
     * @return
     */
    public String[] getJsDependencies()
    {
        return dependencies == null ? null : dependencies.getJs();
    }

    /**
     * @return
     */
    protected Element setGlobalConfigFormsElement ()
    {
        Element rootElement = (Element) configDocument.getFirstChild();
        Element globalConfigElem = XmlUtils.findFirstElement("config[not(@evaluator) and not(@condition)]", rootElement);
        if (globalConfigElem == null)
        {                       
            globalConfigElem = configDocument.createElement("config");
            appendChild(rootElement,globalConfigElem);
        }            
        Element formsElem = XmlUtils.findFirstElement("forms", globalConfigElem);
        if (formsElem == null)
        {                       
            formsElem = configDocument.createElement("forms");
            appendChild(globalConfigElem,formsElem);
        }
        return formsElem;
    }

    /**
     * @param properties
     * @return
     */
    public boolean syncGlobalConfigs(HashMap<String,Object> properties)
    {
        boolean status = false;

        Element formsElem = XmlUtils.findFirstElement("config[not(@evaluator) and not(@condition)]/forms", (Element) configDocument.getFirstChild());
        if (formsElem == null)
        {
            formsElem = setGlobalConfigFormsElement();
            status = true;
        }

        String configType   = (String)properties.get("config-type");
        if (configType != null)
        {
            if (configType.equals("default-control"))
            {
                String typeName = (String)properties.get("type-name");
                String newTypeName = (properties.containsKey("new-type-name"))? (String)properties.get("new-type-name"):null;

                Element defaultControlsElem = XmlUtils.findFirstElement("default-controls", formsElem);
                if (defaultControlsElem == null)
                {
                    defaultControlsElem = configDocument.createElement("default-controls");
                    appendChild(formsElem,defaultControlsElem);
                    status = true;
                }
                if (typeName != null)
                {
                    Element typeControlElem = XmlUtils.findFirstElement("type[@name='"+typeName+"']", defaultControlsElem);
                    if (newTypeName != null && !typeName.equals(newTypeName))
                    {
                        if (typeControlElem != null)
                        {
                            //typeControlElem.getParentNode().removeChild(typeControlElem);
                            removeChild(typeControlElem);
                            typeControlElem = null;
                            status = true;
                        }
                        typeName = newTypeName;
                    }
                    Control control = (Control) properties.get("control");
                    if (typeControlElem == null)
                    {
                        typeControlElem = configDocument.createElement("type");
                        manageAttribute(typeControlElem,"name", typeName);
                        manageAttribute(typeControlElem,"template", control.getTemplate());
                        appendChild(defaultControlsElem,typeControlElem);
                        status = true;
                    }

                    if (control.getParams() != null)
                    {
                        boolean parametersChanged = false;
                        ControlParam[] controlParameters = control.getParams();
                        if (typeControlElem.getChildNodes().getLength() != controlParameters.length)
                        {
                            parametersChanged = true;
                        }
                        else
                        {
                            for (ControlParam controlParam : controlParameters)
                            {
                                String parameterValue = controlParam.getValue();
                                Element parameterElem = XmlUtils.findFirstElement("control-param[@name='"+controlParam.getName()+"']", defaultControlsElem);
                                if (parameterElem == null)
                                {
                                    parametersChanged = true;
                                }
                                else if (!parameterElem.getTextContent().equals(parameterValue))
                                {
                                    parametersChanged = true;    
                                }
                            }
                        }
                        if (parametersChanged)
                        {                            
                            while (typeControlElem.getChildNodes().getLength() > 0)
                            {
                                removeChild(typeControlElem.getFirstChild());
                            }
                            for (ControlParam controlParam : controlParameters)
                            {
                                String parameterValue = controlParam.getValue();
                                Element parameterElem = XmlUtils.findFirstElement("control-param[@name='"+controlParam.getName()+"']", defaultControlsElem);
                                if (parameterElem == null)
                                {
                                    parameterElem = configDocument.createElement("control-param");
                                    manageAttribute(parameterElem,"name",controlParam.getName());
                                    status = true;
                                }
                                if (!parameterElem.getTextContent().equals(parameterValue))
                                {
                                    parameterElem.setTextContent(parameterValue);
                                    status = true;    
                                }
                                if (parameterElem != null)
                                {
                                    appendChild(typeControlElem,parameterElem);
                                }
                            }
                        }
                    }
                }
            } 
            else if (configType.equals("constraint-handler"))
            {
                String typeName = (String)properties.get("constraint-type-name");
                String newTypeName = (properties.containsKey("new-constraint-type-name"))? (String)properties.get("new-constraint-type-name"):null;

                Element constraintHandlersElem = XmlUtils.findFirstElement("constraint-handlers", formsElem);
                if (constraintHandlersElem == null)
                {
                    constraintHandlersElem = configDocument.createElement("constraint-handlers");
                    appendChild(formsElem,constraintHandlersElem);
                    status = true;
                }
                if (typeName != null)
                {
                    Element constraintHandlerElem = XmlUtils.findFirstElement("constraint[@type='"+typeName+"']", constraintHandlersElem);
                    if (newTypeName != null && !typeName.equals(newTypeName))
                    {
                        if (constraintHandlerElem != null)
                        {
                            //constraintHandlerElem.getParentNode().removeChild(constraintHandlerElem);
                            removeChild(constraintHandlerElem);
                            constraintHandlerElem = null;
                            status = true;
                        }
                        typeName = newTypeName;
                    }
                    ConstraintHandlerDefinition constraintHandler = (ConstraintHandlerDefinition) properties.get("constraint");
                    if (constraintHandlerElem == null)
                    {
                        constraintHandlerElem = configDocument.createElement("constraint");
                        manageAttribute(constraintHandlerElem,"type", typeName);
                        appendChild(constraintHandlersElem,constraintHandlerElem);
                        status = true;
                    }
                    if ( manageAttribute (constraintHandlerElem,"validation-handler", constraintHandler.getValidationHandler()) )
                    {
                        status = true;
                    }
                    if ( manageAttribute (constraintHandlerElem,"event", constraintHandler.getEvent()) )
                    {
                        status = true;
                    }
                    if ( manageAttribute (constraintHandlerElem,"message", constraintHandler.getMessage()) )
                    {
                        status = true;
                    }
                    if ( manageAttribute (constraintHandlerElem,"message-id", constraintHandler.getMessageId()) )
                    {
                        status = true;
                    }                   
                }
            }
            else if (configType.equals("dependencies"))
            {
                Element dependenciesElem = XmlUtils.findFirstElement("dependencies", formsElem);
                if (dependenciesElem == null)
                {
                    dependenciesElem = configDocument.createElement("dependencies");
                    appendChild(formsElem,dependenciesElem);
                    status = true;
                }
                if (properties.containsKey("js"))
                {
                    String dependency = (String)properties.get("js");
                    Element jsElement = XmlUtils.findFirstElement("js[@src='"+dependency+"']", dependenciesElem);
                    if (jsElement == null)
                    {
                        jsElement = configDocument.createElement("js");
                        appendChild(dependenciesElem,jsElement);
                        status = true;
                    }
                    if (properties.containsKey("new-js"))
                    {
                        String newDependency = (String)properties.get("new-js");
                        if (!dependency.equals(newDependency))
                        {
                            dependency = newDependency;
                        }
                    }
                    if (manageAttribute(jsElement,"src",dependency))
                    {
                        status = true;
                    }
                }
                if (properties.containsKey("css"))
                {
                    String dependency = (String)properties.get("css");
                    Element cssElement = XmlUtils.findFirstElement("css[@src='"+dependency+"']", dependenciesElem);
                    if (cssElement == null)
                    {
                        cssElement = configDocument.createElement("css");
                        appendChild(dependenciesElem,cssElement);
                        status = true;
                    }
                    if (properties.containsKey("new-css"))
                    {
                        String newDependency = (String)properties.get("new-css");
                        if (!dependency.equals(newDependency))
                        {
                            dependency = newDependency;
                        }
                    }
                    if (manageAttribute(cssElement,"src",dependency))
                    {
                        status = true;
                    }
                }
            }
        }                       
        return status;
    }

    /**
     * @param properties
     * @return
     */
    public boolean removeDefaultControls(HashMap<String,Object> properties)
    {
        boolean status = false;
        for (String typeName : properties.keySet())
        {
            String typeTemplate = (String)properties.get(typeName);
            Element defaultControlElem = XmlUtils.findFirstElement("config[not(@evaluator) and not(@condition)]/forms/default-controls/type[@name='"+typeName+"' and @template='"+typeTemplate+"']", (Element) configDocument.getFirstChild());
            if (defaultControlElem != null)
            {
                //defaultControlElem.getParentNode().removeChild(defaultControlElem);
                removeChild(defaultControlElem);
                status = true;
            }
        }
        return status;
    }

    /**
     * @param properties
     * @return
     */
    public boolean removeConstraintHandlers(HashMap<String,Object> properties)
    {
        boolean status = false;
        for (String typeName : properties.keySet())
        {
            String validationHandler = (String)properties.get(typeName);
            Element validationHandlerElem = XmlUtils.findFirstElement("config[not(@evaluator) and not(@condition)]/forms/constraint-handlers/constraint[@type='"+typeName+"' and @validation-handler='"+validationHandler+"']", (Element) configDocument.getFirstChild());
            if (validationHandlerElem != null)
            {
                //validationHandlerElem.getParentNode().removeChild(validationHandlerElem);
                removeChild(validationHandlerElem);
                status = true;
            }
        }
        return status;
    }

    /**
     * @param dependencies
     * @return
     */
    public boolean removeJsDependencies(ArrayList<String> dependencies)
    {
        boolean status = false;
        for (String dependency : dependencies)
        {
            Element dependencyElem = XmlUtils.findFirstElement("config[not(@evaluator) and not(@condition)]/forms/dependencies/js[@src='"+dependency+"']", (Element) configDocument.getFirstChild());
            if (dependencyElem != null)
            {
                //dependencyElem.getParentNode().removeChild(dependencyElem);
                removeChild(dependencyElem);
                status = true;
            }
        }
        return status;
    }

    /**
     * @param dependencies
     * @return
     */
    public boolean removeCssDependencies(ArrayList<String> dependencies)
    {
        boolean status = false;
        for (String dependency : dependencies)
        {
            Element dependencyElem = XmlUtils.findFirstElement("config[not(@evaluator) and not(@condition)]/forms/dependencies/css[@src='"+dependency+"']", (Element) configDocument.getFirstChild());
            if (dependencyElem != null)
            {
                //dependencyElem.getParentNode().removeChild(dependencyElem);
                removeChild(dependencyElem);
                status = true;
            }
        }
        return status;
    }

    /**
     * @return
     */
    public HashMap<String, HashMap<String, FormConfigElement>> getModelTypeConfigForms ()
    {
        HashMap<String,HashMap<String, FormConfigElement>> modelTypeConfigForms = new HashMap<String,HashMap<String, FormConfigElement>>();
        for (Element modelTypeElem : XmlUtils.findElements("config[@evaluator='model-type']", (Element) configDocument.getFirstChild()))
        {
            if (modelTypeElem.hasAttribute("condition"))
            {
                String condition = modelTypeElem.getAttribute("condition");
                HashMap<String, FormConfigElement> conditionForms = new HashMap<String, FormConfigElement>();
                for (Element conditionFormElem : XmlUtils.findElements("config[@evaluator='model-type' and @condition='"+condition+"']/forms/form", (Element) configDocument.getFirstChild()))
                {
                    if (conditionFormElem.hasAttribute("id"))
                    {
                        String formId = conditionFormElem.getAttribute("id");
                        conditionForms.put(formId,this.getModelTypeConfigForm(condition, formId));
                    }
                    else
                    {
                        conditionForms.put("default",this.getModelTypeConfigForm(condition, null));
                    }
                }
                modelTypeConfigForms.put(condition, conditionForms);
            }
        }
        return modelTypeConfigForms;
    }

    /**
     * @return
     */
    public HashMap<String, HashMap<String, FormConfigElement>> getNodeTypeConfigForms ()
    {
        HashMap<String,HashMap<String, FormConfigElement>> nodeTypeConfigForms = new HashMap<String,HashMap<String, FormConfigElement>>();
        for (Element modelTypeElem : XmlUtils.findElements("config[@evaluator='node-type']", (Element) configDocument.getFirstChild()))
        {
            if (modelTypeElem.hasAttribute("condition"))
            {
                String condition = modelTypeElem.getAttribute("condition");
                HashMap<String, FormConfigElement> conditionForms = new HashMap<String, FormConfigElement>();
                for (Element conditionFormElem : XmlUtils.findElements("config[@evaluator='node-type' and @condition='"+condition+"']/forms/form", (Element) configDocument.getFirstChild()))
                {
                    if (conditionFormElem.hasAttribute("id"))
                    {
                        String formId = conditionFormElem.getAttribute("id");
                        conditionForms.put(formId,this.getModelTypeConfigForm(condition, formId));
                    }
                    else
                    {
                        conditionForms.put("default",this.getModelTypeConfigForm(condition, null));
                    }
                }
                nodeTypeConfigForms.put(condition, conditionForms);
            }
        }
        return nodeTypeConfigForms;
    }

    /**
     * @param condition
     * @param formId
     * @return
     */
    public FormConfigElement getModelTypeConfigForm (String condition, String formId)
    {
        Config modelTypeConfig = xmlConfigService.getConfig(condition);
        if (modelTypeConfig != null)
        {
            FormsConfigElement formsConfigElement = (FormsConfigElement)modelTypeConfig.getConfigElement("forms");
            if (formId == null)
            {
                return formsConfigElement.getDefaultForm();
            }
            else
            {
                return formsConfigElement.getForm(formId);
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * @return
     */
    public HashMap<String, HashMap<String, FormConfigElement>> getAspectConfigForms ()
    {
        HashMap<String,HashMap<String, FormConfigElement>> aspectConfigForms = new HashMap<String,HashMap<String, FormConfigElement>>();
        for (Element modelTypeElem : XmlUtils.findElements("config[@evaluator='aspect']", (Element) configDocument.getFirstChild()))
        {
            if (modelTypeElem.hasAttribute("condition"))
            {
                String condition = modelTypeElem.getAttribute("condition");
                HashMap<String, FormConfigElement> conditionForms = new HashMap<String, FormConfigElement>();
                for (Element conditionFormElem : XmlUtils.findElements("config[@evaluator='aspect' and @condition='"+condition+"']/forms/form", (Element) configDocument.getFirstChild()))
                {
                    if (conditionFormElem.hasAttribute("id"))
                    {
                        String formId = conditionFormElem.getAttribute("id");
                        conditionForms.put(formId,this.getModelTypeConfigForm(condition, formId));
                    }
                    else
                    {
                        conditionForms.put("default",this.getModelTypeConfigForm(condition, null));
                    }
                }
                aspectConfigForms.put(condition, conditionForms);
            }
        }
        return aspectConfigForms;
    }
    
    public void testConfigRead()
    {
        HashMap<String, HashMap<String, FormConfigElement>> forms = getNodeTypeConfigForms();

        for (String condition : forms.keySet())
        {
            System.out.println("Node Type:"+condition);
            HashMap<String, FormConfigElement> cForms = forms.get(condition);
            for (String formId : cForms.keySet())
            {
                System.out.println("Form Id:"+formId);
            }
        }

        forms = getModelTypeConfigForms();

        for (String condition : forms.keySet())
        {
            System.out.println("Model Type:"+condition);
            HashMap<String, FormConfigElement> cForms = forms.get(condition);
            for (String formId : cForms.keySet())
            {
                System.out.println("Form Id:"+formId);
            }
        }
    }

    /**
     * @param condition
     * @param formId
     * @return
     */
    public FormConfigElement getNodeTypeConfigForm (String condition, String formId)
    {
        String formIdXpath = (formId.equals("default")) ? "not(@id)":"@id='"+formId+"'";
        Element formElem = XmlUtils.findFirstElement("config[@evaluator='node-type' and @condition='"+condition+"']/forms/form["+formIdXpath+"]", (Element) configDocument.getFirstChild());
        if (formElem != null)
        {
            FormElementReader formElementReader = new FormElementReader();
            try
            {
                return (FormConfigElement)formElementReader.parse(convert(formElem));
            } catch (ParserConfigurationException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * @param condition
     * @param formId
     * @return
     */
    public FormConfigElement getAspectConfigForm (String condition, String formId)
    {
        String formIdXpath = (formId.equals("default")) ? "not(@id)":"@id='"+formId+"'";
        Element formElem = XmlUtils.findFirstElement("config[@evaluator='aspect' and @condition='"+condition+"']/forms/form["+formIdXpath+"]", (Element) configDocument.getFirstChild());
        if (formElem != null)
        {
            FormElementReader formElementReader = new FormElementReader();
            try
            {
                return (FormConfigElement)formElementReader.parse(convert(formElem));
            } catch (ParserConfigurationException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            return null;
        }
    }
    
    /**
     * @param condition
     * @param formId
     * @return
     */
    public Element getNodeTypeConfigFormElement (String condition, String formId)
    {
        String formIdXpath = (formId == null) ? "not(@id)":"@id='"+formId+"'";

        return XmlUtils.findFirstElement("config[@evaluator='node-type' and @condition='"+condition+"']/forms/form["+formIdXpath+"]", (Element) configDocument.getFirstChild());

    }

    /**
     * @param condition
     * @param formId
     * @return
     */
    public List<FieldVisibilityInstructionCustom> getFieldVisibilityInstructions (String configType, String contentType, String formId)
    {
        String formIdPath = formId.equals("default")?"[not(@id)]":"[@id='"+formId+"']";
        Element elem = XmlUtils.findFirstElement("config[@evaluator='"+configType+"' and @condition='"+contentType+"']/forms/form"+formIdPath+"/field-visibility", (Element) configDocument.getFirstChild());
        ArrayList <FieldVisibilityInstructionCustom> fieldVisibilityInstructions = new ArrayList <FieldVisibilityInstructionCustom>();
        if (elem != null)
        {
            for (Element fieldVisibilityElem : XmlUtils.findElements("show|hide", elem))
            {
                fieldVisibilityInstructions.add(new FieldVisibilityInstructionCustom(fieldVisibilityElem.getNodeName(),fieldVisibilityElem.getAttribute("id"),fieldVisibilityElem.getAttribute("for-mode"),fieldVisibilityElem.getAttribute("force")));                                 
            }            
            return fieldVisibilityInstructions;
        }
        else
        {
            return null;
        }
    }

    private SyncStatus syncTemplateElement(String template,String templateMode, Element formElem, Element siblingElem)
    {
        boolean status = false;
        Element templateElem = XmlUtils.findFirstElement(templateMode, formElem);
        if (template != null && !template.equals(""))
        {
            if ( templateElem != null )
            {
                if ( !templateElem.getAttribute("template").equals(template) )
                {
                    manageAttribute(templateElem, "template", template);
                    status = true;
                }
            }
            else
            {
                templateElem = configDocument.createElement(templateMode);
                manageAttribute(templateElem, "template", template);
                insertChildAfter(formElem,templateElem,siblingElem);
                status = true;
            }
        }
        else
        {
            if ( templateElem != null )
            {
                //templateElem.getParentNode().removeChild(templateElem);
                removeChild(templateElem);
                status = true;
                templateElem = null;
            }
        }
        return new SyncStatus(status,templateElem);
    }

    public boolean compareObject (Object a, Object b)
    {
        if ( (a == null && b != null) || (a != null && b == null) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean compareString(String sa, String sb)
    {
        if (sa == null)
        {
            if (sb == null)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if (sa.equals(sb))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean compareAttributeList(Element attributeElem, Map<String,String> attributeList)
    {
        if ( compareObject (attributeElem,attributeList))
        {
            return true;
        }
        if (attributeElem != null)
        {
            if (attributeElem.getAttributes().getLength() != attributeList.size())
            {
                return true;
            }
            for (String key : attributeList.keySet())
            {
                if (!attributeElem.hasAttribute(key) || !attributeElem.getAttribute(key).equals(attributeList.get(key)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param appearanceElem
     * @param formField
     * @return
     */
    private boolean isFieldChanged(Element appearanceElem, FormField formField)
    {
        String formFieldId = formField.getId();
        Element fieldElement = XmlUtils.findFirstElement("field[@id='"+formFieldId+"']", appearanceElem);
        if (fieldElement == null)
        {
            return true;
        }
        if ( compareAttributeList(fieldElement, formField.getAttributes()))
        {
            return true;
        }
        Control control = formField.getControl();
        Element controlElement = XmlUtils.findFirstElement("control", fieldElement);
        if (compareObject (control, controlElement))
        {
            return true;
        }

        if (control != null)
        {
            if (compareString (controlElement.getAttribute("template"), control.getTemplate()))
            {
                return true;
            }
            if (compareObject (control.getParams(),controlElement.getChildNodes()))
            {
                return true;
            }            
            if (control.getParams() != null)
            {
                for (ControlParam param : control.getParams())
                {
                    Element paramElement = XmlUtils.findFirstElement("control[@name='"+param.getName()+"']", controlElement);
                    if (paramElement == null || compareString(paramElement.getTextContent(),param.getValue()))
                    {
                        return true;
                    }
                }
            }
        }

        Element constraintsElement = XmlUtils.findFirstElement("constraint-handlers", fieldElement);
        Map<String, ConstraintHandlerDefinition> constraints = formField.getConstraintDefinitionMap();

        if (compareObject (constraintsElement,constraints))
        {
            return true;
        }

        if (constraints!= null)
        {
            if (constraints.size() != constraintsElement.getChildNodes().getLength())
            {
                return true;
            }

            for (String constraintId : constraints.keySet())
            {
                ConstraintHandlerDefinition constraint = constraints.get(constraintId);
                Element constraintElement =XmlUtils.findFirstElement("constraint[@type='"+constraint.getType()+"']", fieldElement);
                if (constraintElement == null)
                {
                    return true;
                }
                if (compareString(constraintElement.getAttribute("event"),constraint.getEvent()) ||
                        compareString(constraintElement.getAttribute("message"),constraint.getMessage()) ||
                        compareString(constraintElement.getAttribute("message-id"),constraint.getMessageId()) ||
                        compareString(constraintElement.getAttribute("validation-handler"),constraint.getValidationHandler() ))
                {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * @param properties
     * @return
     */
    public boolean syncForm(HashMap<String,Object> properties)
    {
        boolean status = false;
        Element currentElem = null;

        String configType   = (String)properties.get("config-type");
        if (configType != null)
        {
            if (configType.equals("node-type") || configType.equals("model-type") || configType.equals("aspect"))
            {
                String typeName = (String)properties.get("type-name");
                String newTypeName = (properties.containsKey("new-type-name"))? (String)properties.get("new-type-name"):null;
                String formId = (String)properties.get("form-id");
                String newFormId = (properties.containsKey("new-form-id"))? (String)properties.get("new-form-id"):null;
                if (typeName != null && formId != null)
                {
                    Element configElem = XmlUtils.findFirstElement("config[@evaluator='"+configType+"' and @condition='"+typeName+"']", (Element) configDocument.getFirstChild());
                    if (newTypeName != null && !newTypeName.equals(typeName) && configElem != null)
                    {
                        //configElem.getParentNode().removeChild(configElem);
                        removeChild(configElem);
                        typeName = newTypeName;
                        configElem = null;
                        status = true;
                    }
                    if (configElem == null)
                    {
                        configElem = configDocument.createElement("config");
                        manageAttribute(configElem,"evaluator",configType);
                        manageAttribute(configElem,"condition", typeName);
                        appendChild((Element) configDocument.getFirstChild(),configElem);
                        status = true;
                    }
                    Element formsElem = XmlUtils.findFirstElement("forms", configElem);
                    if (formsElem == null)
                    {
                        formsElem = configDocument.createElement("forms");
                        appendChild(configElem,formsElem);
                        status = true;
                    }
                    String formXpath = formId.equals("")?"form[not(@condition)]":"form[@id='"+formId+"']";                        
                    Element formElem = XmlUtils.findFirstElement(formXpath, formsElem);
                    if (newFormId != null && !newFormId.equals(formId) && formElem != null)
                    {
                        //formElem.getParentNode().removeChild(formElem);
                        removeChild(formElem);
                        formId = newFormId;
                        formElem = null;
                        status = true;
                    }
                    if (formElem == null)
                    {
                        formElem = configDocument.createElement("form");
                        manageAttribute(formElem,"id", formId);
                        appendChild(formsElem,formElem);
                        status = true;
                    }

                    FormConfigElement formConfig = (FormConfigElement) properties.get("form-config");
                    if (formConfig != null)
                    {                       
                        if( manageAttribute(formElem,"submission-url",formConfig.getSubmissionURL()))
                        {
                            status = true;
                        }

                        SyncStatus syncStatus = syncTemplateElement(formConfig.getViewTemplate(),"view-form", formElem,null);
                        if (syncStatus.getElement() != null)
                        {
                            currentElem = syncStatus.getElement(); 
                        }
                        if (syncStatus.isStatus())
                        {
                            status = true;
                        }
                        syncStatus = syncTemplateElement(formConfig.getEditTemplate(),"edit-form", formElem,currentElem);
                        if (syncStatus.getElement() != null)
                        {
                            currentElem = syncStatus.getElement(); 
                        }
                        if (syncStatus.isStatus())
                        {
                            status = true;
                        }
                        syncStatus = syncTemplateElement(formConfig.getCreateTemplate(),"create-form", formElem,currentElem);
                        if (syncStatus.getElement() != null)
                        {
                            currentElem = syncStatus.getElement(); 
                        }
                        if (syncStatus.isStatus())
                        {
                            status = true;
                        }

                        Element visibilityInstructionElem = XmlUtils.findFirstElement("field-visibility", formElem);
                        if (visibilityInstructionElem == null)
                        {
                            visibilityInstructionElem = configDocument.createElement("field-visibility");
                            insertChildAfter(formElem,visibilityInstructionElem,currentElem);
                            currentElem =  visibilityInstructionElem;
                            status = true;
                        }
                        if (properties.containsKey("visibility-instructions") && properties.get("visibility-instructions") instanceof FieldVisibilityInstructionCustom[])
                        {
                            FieldVisibilityInstructionCustom[] visibilityInstructions = (FieldVisibilityInstructionCustom[])(properties.get("visibility-instructions"));
                            boolean visibilityChanged = false;
                            if (visibilityInstructions.length != visibilityInstructionElem.getChildNodes().getLength())
                            {
                                visibilityChanged = true;
                            }
                            else
                            {
                                for (FieldVisibilityInstruction fieldVisibilityInstruction : visibilityInstructions)
                                {
                                    String fieldVisibilityXpath = fieldVisibilityInstruction.getShowOrHide().equals(Visibility.SHOW)?"show":"hide";
                                    fieldVisibilityXpath += "[@id='"+fieldVisibilityInstruction.getFieldId()+"'";
                                    if (fieldVisibilityInstruction.getModes() != null)
                                    {
                                        if (fieldVisibilityInstruction.getModes().size() != Mode.values().length)
                                        {
                                            fieldVisibilityXpath += " and @for-mode='";
                                            for (int i=0 ; i < fieldVisibilityInstruction.getModes().size() -1 ; i++)
                                            {
                                                fieldVisibilityXpath +=fieldVisibilityInstruction.getModes().get(i).toString()+",";
                                            }
                                            fieldVisibilityXpath +=fieldVisibilityInstruction.getModes().get(fieldVisibilityInstruction.getModes().size() -1).toString()+"'";
                                        }
                                        if (formConfig.getForcedFields() != null)
                                        {
                                            if (formConfig.getForcedFieldsAsList().contains(fieldVisibilityInstruction.getFieldId()))
                                            {
                                                fieldVisibilityXpath += " and @force='true'"; 
                                            }
                                        }
                                    }
                                    fieldVisibilityXpath += "]";
                                    Element fieldVisibilityElem = XmlUtils.findFirstElement(fieldVisibilityXpath, visibilityInstructionElem);
                                    if (fieldVisibilityElem == null)
                                    {
                                        visibilityChanged = true;
                                    }
                                }
                            }
                            if (visibilityChanged)
                            {
                                while (visibilityInstructionElem.getChildNodes().getLength()>0)
                                {
                                    //visibilityInstructionElem.getFirstChild().getParentNode().removeChild(visibilityInstructionElem.getFirstChild());
                                    removeChild(visibilityInstructionElem.getFirstChild());
                                }
                                for (FieldVisibilityInstructionCustom fieldVisibilityInstruction : visibilityInstructions)
                                {
                                    String fieldVisibility = fieldVisibilityInstruction.getShowOrHide().equals(Visibility.SHOW)?"show":"hide";
                                    String fieldId = fieldVisibilityInstruction.getFieldId();
                                    Element fieldVisibilityElem = configDocument.createElement(fieldVisibility);
                                    manageAttribute(fieldVisibilityElem,"id", fieldId);
                                    if (fieldVisibilityInstruction.getModes() != null)
                                    {                                    
                                        if (fieldVisibilityInstruction.getModes().size() != Mode.values().length)
                                        {
                                            String fieldVisibilityMode = "";
                                            for (int i=0 ; i < fieldVisibilityInstruction.getModes().size() -1 ; i++)
                                            {
                                                fieldVisibilityMode +=fieldVisibilityInstruction.getModes().get(i).toString()+",";
                                            }
                                            fieldVisibilityMode +=fieldVisibilityInstruction.getModes().get(fieldVisibilityInstruction.getModes().size() -1).toString();
                                            manageAttribute(fieldVisibilityElem,"for-mode", fieldVisibilityMode);
                                        }
                                        if (formConfig.getForcedFields() != null)
                                        {
                                            if (formConfig.getForcedFieldsAsList().contains(fieldVisibilityInstruction.getFieldId()))
                                            {
                                                manageAttribute(fieldVisibilityElem,"for-mode","true"); 
                                            }
                                        }
                                    }
                                    manageAttribute(fieldVisibilityElem,"force", fieldVisibilityInstruction.getForce());
                                    appendChild(visibilityInstructionElem,fieldVisibilityElem);
                                }
                            }
                        }
                        Element appearanceElem = XmlUtils.findFirstElement("appearance", formElem);

                        if (appearanceElem  == null)
                        {
                            appearanceElem  = configDocument.createElement("appearance");
                            insertChildAfter(formElem,appearanceElem,currentElem);
                            status = true;
                        }

                        Map<String,FormSet> sets = formConfig.getSets();
                        boolean setChanged = false;
                        if (XmlUtils.findElements("set", appearanceElem).size() != sets.size()-1)
                        {
                            setChanged = true; 
                        }
                        else
                        {
                            for (Element fieldElement : XmlUtils.findElements("set", appearanceElem))
                            {
                                if (!sets.keySet().contains(fieldElement.getAttribute("id")))
                                {
                                    setChanged = true;
                                }
                            }
                        }
                        if (setChanged)
                        {
                            for (Element setElement : XmlUtils.findElements("set", appearanceElem))
                            {
                                //setElement.getParentNode().removeChild(setElement);
                                removeChild(setElement);
                            }
                            status = true;
                        }
                        // now sync individual set element
                        Element currentAppearanceElem = null;
                        for (FormSet formSet : sets.values())
                        {
                            String setId = formSet.getSetId();
                            if (!setId.equals(""))
                            {
                                Element setElement = XmlUtils.findFirstElement("set[@id='"+setId+"']", appearanceElem);
                                if (setElement == null)
                                {
                                    setElement = configDocument.createElement("set");
                                    insertChildAfter(appearanceElem,setElement,currentAppearanceElem);
                                }
                                if (this.manageAttribute(setElement, "id", setId))
                                {
                                    status = true;
                                }
                                if (this.manageAttribute(setElement, "parent", formSet.getParentId()))
                                {
                                    status = true;
                                }
                                if (this.manageAttribute(setElement, "label", formSet.getLabel()))
                                {
                                    status = true;
                                }
                                if (this.manageAttribute(setElement, "label-id", formSet.getLabelId()))
                                {
                                    status = true;
                                }
                                if (this.manageAttribute(setElement, "appearance", formSet.getAppearance()))
                                {
                                    status = true;
                                }
                                if (this.manageAttribute(setElement, "template", formSet.getTemplate()))
                                {
                                    status = true;
                                }
                                List<Element> setElems = XmlUtils.findElements("set", appearanceElem);
                                currentAppearanceElem = setElems.get(setElems.size()-1);
                            }
                        }

                        Map<String,FormField> fields = formConfig.getFields();
                        int appearanceCounter = 0;
                        for (FormField formField : fields.values())
                        {
                            if (formField.getAttributes() != null)
                            {
                                appearanceCounter ++;    
                            }
                        }
                        boolean fieldChanged = false;
                        if (XmlUtils.findElements("field", appearanceElem).size() != appearanceCounter)
                        {
                            fieldChanged = true; 
                        }
                        else
                        {
                            for (Element fieldElement : XmlUtils.findElements("field", appearanceElem))
                            {
                                if (!fields.keySet().contains(fieldElement.getAttribute("id")))
                                {
                                    fieldChanged = true;
                                }
                            }
                            if (!fieldChanged)
                            {
                                for (FormField formField : fields.values())
                                {
                                    if (formField.getAttributes() != null && isFieldChanged(appearanceElem, formField))
                                    {
                                        fieldChanged = true;
                                    }
                                }
                            }
                        }
                        if (fieldChanged)
                        {
                            for (Element fieldElement : XmlUtils.findElements("field", appearanceElem))
                            {
                                //fieldElement.getParentNode().removeChild(fieldElement);
                                removeChild(fieldElement);
                            }
                            status = true;

                            // now sync individual field element
                            for (FormField formField : fields.values())
                            {
                                if (formField.getAttributes() != null && !formField.getAttributes().isEmpty() && isFieldChanged(appearanceElem, formField))
                                {
                                    String formFieldId = formField.getId();
                                    Element fieldElement = XmlUtils.findFirstElement("field[@id='"+formFieldId+"']", appearanceElem);
                                    if (fieldElement == null)
                                    {
                                        fieldElement = configDocument.createElement("field");
                                        manageAttribute(fieldElement,"id", formFieldId);
                                        for (String key : formField.getAttributes().keySet())
                                        {
                                            manageAttribute(fieldElement,key, formField.getAttributes().get(key));
                                        }
                                        if (formField.getControl() != null)
                                        {
                                            Control control = formField.getControl();
                                            Element controlElement = configDocument.createElement("control");
                                            manageAttribute (controlElement, "template", control.getTemplate());
                                            if (control.getParams() != null)
                                            {
                                                for (ControlParam param : control.getParams())
                                                {
                                                    Element paramElement = configDocument.createElement("control-param");
                                                    manageAttribute(paramElement,"name", param.getName());
                                                    paramElement.setTextContent(param.getValue());
                                                    appendChild(controlElement,paramElement);
                                                }
                                            }
                                            appendChild(fieldElement,controlElement);
                                        }
                                        if (formField.getConstraintDefinitionMap()!= null)
                                        {
                                            Element constraintsElement = configDocument.createElement("constraint-handlers");
                                            Map<String, ConstraintHandlerDefinition> constraints = formField.getConstraintDefinitionMap();
                                            for (String constraintId : constraints.keySet())
                                            {
                                                ConstraintHandlerDefinition constraint = constraints.get(constraintId);
                                                Element constraintElement = configDocument.createElement("constraint");
                                                manageAttribute(constraintElement,"type",constraint.getType());
                                                manageAttribute(constraintElement,"event",constraint.getEvent());
                                                manageAttribute(constraintElement,"message",constraint.getMessage());
                                                manageAttribute(constraintElement,"message-id",constraint.getMessageId());
                                                manageAttribute(constraintElement,"validation-handler",constraint.getValidationHandler());
                                                appendChild(constraintsElement,constraintElement);
                                            }
                                            appendChild(fieldElement,constraintsElement);
                                        }
                                        insertChildAfter(appearanceElem,fieldElement,currentAppearanceElem);
                                        currentAppearanceElem = fieldElement;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return status;
    }
    /**
     * @param element
     * @return
     * @throws ParserConfigurationException
     */
    public org.dom4j.Element convert( org.w3c.dom.Element element) throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc1 = builder.newDocument();
        doc1.appendChild(doc1.importNode(element, true));
        // Convert w3c document to dom4j document
        org.dom4j.io.DOMReader reader = new org.dom4j.io.DOMReader();
        org.dom4j.Document doc2 = reader.read( doc1);

        return doc2.getRootElement();
    }
}
