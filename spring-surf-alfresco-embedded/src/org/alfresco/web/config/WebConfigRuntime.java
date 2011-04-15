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
package org.alfresco.web.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.alfresco.web.config.AdvancedSearchConfigElement.CustomProperty;
import org.alfresco.web.config.PropertySheetConfigElement.AssociationConfig;
import org.alfresco.web.config.PropertySheetConfigElement.ChildAssociationConfig;
import org.alfresco.web.config.PropertySheetConfigElement.ItemConfig;
import org.alfresco.web.config.PropertySheetConfigElement.PropertyConfig;
import org.alfresco.web.config.PropertySheetConfigElement.SeparatorConfig;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.source.StringConfigSource;
import org.springframework.extensions.config.xml.XMLConfigService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author drq
 *
 */
public class WebConfigRuntime
{
    private XMLConfigService xmlConfigService;
    private String webConfigXml;
    private Document webConfigDocument;

    /**
     * @param webConfigXml
     */
    public WebConfigRuntime(String webConfigXml)
    {
        this.webConfigXml = webConfigXml;
    }

    /**
     * @return the xmlConfigService
     */
    public XMLConfigService getXmlConfigService()
    {
        return xmlConfigService;
    }

    /**
     * @param xmlConfigService the xmlConfigService to set
     */
    public void setXmlConfigService(XMLConfigService xmlConfigService)
    {
        this.xmlConfigService = xmlConfigService;
    }

    /**
     * @return the webConfigXml
     */
    public String getWebConfigXml()
    {
        return webConfigXml;
    }

    /**
     * @param webConfigXml the webConfigXml to set
     */
    public void setWebConfigXml(String webConfigXml)
    {
        this.webConfigXml = webConfigXml;
    }

    /**
     * @return
     */
    public Document getWebConfigDocument() 
    {       
        return webConfigDocument;
    }

    /**
     * @param webConfigDocument
     */
    public void setWebConfigDocument(Document webConfigDocument) 
    {
        this.webConfigDocument = webConfigDocument;
    }

    /**
     * @return
     */
    public boolean initWebConfigRuntime()
    {
        // Add minimal base configurations?
        String baseWebConfigXml = "<alfresco-config>"  
            +"<plug-ins>"
            +" <evaluators>"
            +" <evaluator id=\"node-type\" class=\"org.springframework.extensions.config.evaluator.StringEvaluator\" />"
            +"  <evaluator id=\"aspect-name\" class=\"org.springframework.extensions.config.evaluator.StringEvaluator\" />"
            +"</evaluators>"
            +"<element-readers>"
            +"<element-reader element-name=\"property-sheet\" class=\"org.alfresco.web.config.PropertySheetElementReader\"/>"
            +"<element-reader element-name=\"client\" class=\"org.alfresco.web.config.ClientElementReader\"/>"
            +"<element-reader element-name=\"navigation\" class=\"org.alfresco.web.config.NavigationElementReader\" />"
            +"<element-reader element-name=\"languages\" class=\"org.alfresco.web.config.LanguagesElementReader\" />"
            +"<element-reader element-name=\"advanced-search\" class=\"org.alfresco.web.config.AdvancedSearchElementReader\" />"
            +"<element-reader element-name=\"views\" class=\"org.alfresco.web.config.ViewsElementReader\" />"
            +"<element-reader element-name=\"actions\" class=\"org.alfresco.web.config.ActionsElementReader\" />"
            +"<element-reader element-name=\"wcm\" class=\"org.alfresco.web.config.WCMElementReader\" />"
            +"</element-readers>"
            +"</plug-ins>"
            +"</alfresco-config>";
        List<String> xmlConfigFiles = new ArrayList<String>();
        xmlConfigFiles.add(baseWebConfigXml);
        xmlConfigFiles.add(webConfigXml);
        initXMLConfigService(xmlConfigFiles);
        return true;
    }

    /**
     * @param xmlConfigFiles
     * @return
     */
    private void initXMLConfigService(List<String> xmlConfigFiles)
    {		
        xmlConfigService = new XMLConfigService(new StringConfigSource(xmlConfigFiles));
        xmlConfigService.initConfig();
        toXmlDocument();
    }

    /**
     * @param typeName
     * @return
     */
    public boolean findTypeAdvancedSearchOption(String typeName)
    {
        try
        {
            if ( xmlConfigService!= null && xmlConfigService.getConfig("Advanced Search") != null)
            {
                AdvancedSearchConfigElement config = (AdvancedSearchConfigElement)xmlConfigService.getConfig("Advanced Search").
                getConfigElement(AdvancedSearchConfigElement.CONFIG_ELEMENT_ID);
                if (config != null && config.getContentTypes() != null)
                {
                    for (String contentType : config.getContentTypes())
                    {
                        if (contentType.equals(typeName))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param typeName
     * @param propertyName
     * @return
     */
    public CustomProperty findPropertyAdvancedSearchOption(String typeName,String propertyName)
    {
        if ( xmlConfigService!= null && xmlConfigService.getConfig("Advanced Search") != null)
        {
            AdvancedSearchConfigElement config = (AdvancedSearchConfigElement)xmlConfigService.getConfig("Advanced Search").

            getConfigElement(AdvancedSearchConfigElement.CONFIG_ELEMENT_ID);

            if ( config != null && config.getCustomProperties() != null )
            {
                for (CustomProperty cp : config.getCustomProperties())
                {
                    if ( cp.Type.equals(typeName) && cp.Property.equals(propertyName) )
                    {
                        return cp;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param typeName
     * @return
     */
    public boolean findTypeContentWizardsOption(String typeName)
    {
        if (xmlConfigService != null)
        {
            Config wizardCfg = xmlConfigService.getConfig("Content Wizards");
            if (wizardCfg == null)
            {
                return false;
            }
            else
            {
                if (wizardCfg.hasConfigElement("content-types"))
                {
                    ConfigElement contentTypes = wizardCfg.getConfigElement("content-types");
                    for (ConfigElement configElement : contentTypes.getChildren())
                    {
                        if (configElement.getName().equals("type") && configElement.getAttribute("name").equals(typeName))
                        {
                            return true;
                        }
                    }
                    return false;
                }
                else
                {
                    return false;
                }
            }	
        }
        else
        {
            return false;
        }
    }

    /**
     * @param aspectName
     * @return
     */
    public boolean findAspectActionWizardsOption(String aspectName)
    {
        if (xmlConfigService != null)
        {
            Config wizardCfg = xmlConfigService.getConfig("Action Wizards");
            if (wizardCfg == null)
            {
                return false;
            }
            else
            {
                if (wizardCfg.hasConfigElement("aspects"))
                {
                    ConfigElement contentTypes = wizardCfg.getConfigElement("aspects");
                    for (ConfigElement configElement : contentTypes.getChildren())
                    {
                        if (configElement.getName().equals("aspect") && configElement.getAttribute("name").equals(aspectName))
                        {
                            return true;
                        }
                    }
                    return false;
                }
                else
                {
                    return false;
                }
            }	
        }
        else
        {
            return false;
        }
    }

    /**
     * @param typeName
     * @return
     */
    public PropertySheetConfigElement findTypeOrAspectPropertySheet (String typeName)
    {
        if (xmlConfigService != null && xmlConfigService.getConfig(typeName) != null)
        {
            return (PropertySheetConfigElement) xmlConfigService.getConfig(typeName).
            getConfigElement(PropertySheetConfigElement.CONFIG_ELEMENT_ID);			
        }
        else
        {
            return null;
        }
    }

    /**
     * @param typeName
     * @param propertyName
     * @return
     */
    public ItemConfig findTypeOrAspectPropertySheet (String typeName, String propertyName)
    {
        PropertySheetConfigElement typePropertySheet = findTypeOrAspectPropertySheet (typeName);
        if (typePropertySheet != null)
        {
            if (typePropertySheet.getItems().containsKey(propertyName) && typePropertySheet.getItems().get(propertyName) instanceof PropertyConfig)
            {
                return typePropertySheet.getItems().get(propertyName);
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

    /**
     * @param typeName
     * @param associationName
     * @return
     */
    public ItemConfig findAssociationPropertySheet (String typeName, String associationName)
    {
        PropertySheetConfigElement typePropertySheet = findTypeOrAspectPropertySheet (typeName);
        if (typePropertySheet != null)
        {
            if (typePropertySheet.getItems().containsKey(associationName) && typePropertySheet.getItems().get(associationName) instanceof AssociationConfig)
            {
                return typePropertySheet.getItems().get(associationName);
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

    /**
     * @param typeName
     * @param childAssociationName
     * @return
     */
    public ItemConfig findChildAssociationPropertySheet (String typeName, String childAssociationName)
    {
        PropertySheetConfigElement typePropertySheet = findTypeOrAspectPropertySheet (typeName);
        if (typePropertySheet != null)
        {
            if (typePropertySheet.getItems().containsKey(childAssociationName) && typePropertySheet.getItems().get(childAssociationName) instanceof ChildAssociationConfig)
            {
                return typePropertySheet.getItems().get(childAssociationName);
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

    /**
     * @param propertySheet
     * @return
     */
    public String toXML(PropertySheetConfigElement propertySheet)
    {
        StringBuffer sb = new StringBuffer ();
        sb.append("<").append(PropertySheetConfigElement.CONFIG_ELEMENT_ID).append(">\n");
        for ( ItemConfig itemConfig : propertySheet.getItems().values())
        {
            if (itemConfig instanceof PropertyConfig)
            {
                sb.append("<show-property");
            } 
            else if (itemConfig instanceof AssociationConfig)
            {
                sb.append("<show-association");
            }
            else if (itemConfig instanceof ChildAssociationConfig)
            {
                sb.append("<show-child-association");
            }
            if (itemConfig instanceof SeparatorConfig)
            {
                sb.append("<separator");
            }

            sb.append(" name=\"").append(itemConfig.getName()).append("\"");

            if ( itemConfig.getDisplayLabel() != null)
            {
                sb.append(" display-label=\"").append(itemConfig.getDisplayLabel()).append("\"");
            }
            if ( itemConfig.getDisplayLabelId() != null)
            {
                sb.append(" display-label-id=\"").append(itemConfig.getDisplayLabelId()).append("\"");
            }
            if ( itemConfig.getConverter() != null)
            {
                sb.append(" converter=\"").append(itemConfig.getConverter()).append("\"");
            }
            sb.append(" read-only=\"").append(itemConfig.isReadOnly()).append("\"");
            sb.append(" show-in-view-mode=\"").append(itemConfig.isShownInViewMode()).append("\"");
            sb.append(" show-in-edit-mode=\"").append(itemConfig.isShownInEditMode()).append("\"");
            if (itemConfig instanceof PropertyConfig)
            {
                sb.append(" ignore-if-missing=\"").append(itemConfig.getIgnoreIfMissing()).append("\"");
            }
            if ( itemConfig.getComponentGenerator() != null)
            {
                sb.append(" component-generator=\"").append(itemConfig.getComponentGenerator()).append("\"");
            }
            sb.append(" />\n");	
        }
        sb.append("</").append(PropertySheetConfigElement.CONFIG_ELEMENT_ID).append("/>\n");
        return sb.toString();
    }

    /**
     * Utility method for returning Document object of an XML file.
     * 
     * @return XML document object.
     */
    public Document toXmlDocument() 
    {
        try 
        {
            webConfigDocument =  DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(webConfigXml.getBytes()));
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
            throw new IllegalStateException("Failed to parse XML document ",ex);
        }
        return webConfigDocument;
    }

    /**
     * @return
     */
    public String toXml()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        XmlUtils.writeXml(baos, webConfigDocument);
        webConfigXml = baos.toString();
        return webConfigXml;
    }

    /**
     * @param properties
     * @return
     */
    public boolean syncActionWizardOptions(HashMap<String,String> properties)
    {
        boolean status = false;
        Element rootElement = (Element) webConfigDocument.getFirstChild();
        Element actionWizardsConfigElem = XmlUtils.findFirstElement("config[@evaluator='string-compare' and @condition='Action Wizards']", rootElement);
        if (actionWizardsConfigElem == null)
        {                       
            actionWizardsConfigElem = webConfigDocument.createElement("config");
            actionWizardsConfigElem.setAttribute("evaluator", "string-compare");
            actionWizardsConfigElem.setAttribute("condition", "Action Wizards");
            appendChild(rootElement,actionWizardsConfigElem);
            status = true;
        }            
        Element aspectsElem = XmlUtils.findFirstElement("aspects", actionWizardsConfigElem);
        if (aspectsElem == null)
        {                       
            aspectsElem = webConfigDocument.createElement("aspects");
            appendChild(actionWizardsConfigElem,aspectsElem);
            status = true;
        }          

        String aspectName     = properties.get("aspect");
        String showOption   = properties.get("show");

        if (aspectName != null && showOption != null)
        {
            Element typeElem = XmlUtils.findFirstElement("aspect[@name='"+aspectName+"']", aspectsElem);
            if (typeElem == null && showOption.equals("true"))
            {                       
                typeElem = webConfigDocument.createElement("aspect");
                typeElem.setAttribute("name", aspectName);
                appendChild(aspectsElem,typeElem);
                status = true;
            }    
            if (typeElem != null && showOption.equals("false"))
            {   
                typeElem.getParentNode().removeChild(typeElem);
                status = true;
            }
        }                        
        return status;
    }

    /**
     * @param properties
     * @return
     */
    public boolean syncContentWizardOptions(HashMap<String,String> properties)
    {
        boolean status = false;
        Element rootElement = (Element) webConfigDocument.getFirstChild();
        Element contentWizardsConfigElem = XmlUtils.findFirstElement("config[@evaluator='string-compare' and @condition='Content Wizards']", rootElement);
        if (contentWizardsConfigElem == null)
        {                       
            contentWizardsConfigElem = webConfigDocument.createElement("config");
            contentWizardsConfigElem.setAttribute("evaluator", "string-compare");
            contentWizardsConfigElem.setAttribute("condition", "Content Wizards");
            appendChild(rootElement,contentWizardsConfigElem);                        
            status = true;
        }            
        Element contentTypesElem = XmlUtils.findFirstElement("content-types", contentWizardsConfigElem);
        if (contentTypesElem == null)
        {                       
            contentTypesElem = webConfigDocument.createElement("content-types");
            appendChild(contentWizardsConfigElem,contentTypesElem);                        
            status = true;
        }          

        String typeName     = properties.get("type");
        String showOption   = properties.get("show");

        if (typeName != null && showOption != null)
        {
            Element typeElem = XmlUtils.findFirstElement("type[@name='"+typeName+"']", contentTypesElem);
            if (typeElem == null && showOption.equals("true"))
            {                       
                typeElem = webConfigDocument.createElement("type");
                typeElem.setAttribute("name", typeName);
                appendChild(contentTypesElem,typeElem);                        
                status = true;
            }    
            if (typeElem != null && showOption.equals("false"))
            {   
                typeElem.getParentNode().removeChild(typeElem);
                status = true;
            }
        }                        
        return status;
    }

    /**
     * @param properties
     * @return
     */
    public boolean syncAdvancedSearchOptions(HashMap<String,String> properties)
    {
        boolean status = false;

        String configType = properties.get("config");

        if (configType != null)
        {
            // check if we have the config node for Advanced Search
            Element rootElement = (Element) webConfigDocument.getFirstChild();
            Element advancedSearchConfigElem = XmlUtils.findFirstElement("config[@evaluator='string-compare' and @condition='Advanced Search']", rootElement);
            if (advancedSearchConfigElem == null)
            {                       
                advancedSearchConfigElem = webConfigDocument.createElement("config");
                advancedSearchConfigElem.setAttribute("evaluator", "string-compare");
                advancedSearchConfigElem.setAttribute("condition", "Advanced Search");
                appendChild(rootElement,advancedSearchConfigElem);                        
                status = true;
            }    
            Element advancedSearchElem = XmlUtils.findFirstElement("advanced-search", advancedSearchConfigElem);
            if (advancedSearchElem == null)
            {                       
                advancedSearchElem = webConfigDocument.createElement("advanced-search");
                appendChild(advancedSearchConfigElem,advancedSearchElem);                        
                status = true;
            }    
            Element contentTypesElem = XmlUtils.findFirstElement("content-types", advancedSearchElem);
            if (contentTypesElem == null)
            {                       
                contentTypesElem = webConfigDocument.createElement("content-types");
                appendChild(advancedSearchElem,contentTypesElem);                        
                status = true;
            }    
            Element customPropertiesElem = XmlUtils.findFirstElement("custom-properties", advancedSearchElem);
            if (customPropertiesElem == null)
            {                       
                customPropertiesElem = webConfigDocument.createElement("custom-properties");
                appendChild(advancedSearchElem,customPropertiesElem);                        
                status = true;
            }    

            String typeName     = properties.get("type");
            String aspectName   = properties.get("aspect");
            String propertyName = properties.get("property");
            String showOption   = properties.get("show");
            String displayLableId = properties.get("display-label-id");

            if (configType.equals("type-property"))
            {
                if (typeName != null && propertyName != null && showOption != null)
                {
                    Element typePropertyElem = XmlUtils.findFirstElement("meta-data[type='"+typeName+"' and property='"+propertyName+"']", customPropertiesElem);
                    if (typePropertyElem == null && showOption.equals("true"))
                    {                       
                        typePropertyElem = webConfigDocument.createElement("meta-data");
                        typePropertyElem.setAttribute("type", typeName);
                        typePropertyElem.setAttribute("property", propertyName);
                        if (displayLableId != null)
                        {
                            typePropertyElem.setAttribute("display-label-id", displayLableId);
                        }
                        appendChild(customPropertiesElem,typePropertyElem);                        
                        status = true;
                    }    
                    if (typePropertyElem != null && showOption.equals("false"))
                    {   
                        typePropertyElem.getParentNode().removeChild(typePropertyElem);
                        status = true;
                    }
                }                
            }
            if (configType.equals("aspect-property"))
            {
                if (aspectName != null && propertyName != null && showOption != null)
                {                    
                    Element aspectPropertyElem = XmlUtils.findFirstElement("meta-data[aspect='"+aspectName+"' and property='"+propertyName+"']", customPropertiesElem);
                    if (aspectPropertyElem == null && showOption.equals("true"))
                    {                       
                        aspectPropertyElem = webConfigDocument.createElement("meta-data");
                        aspectPropertyElem.setAttribute("aspect", aspectName);
                        aspectPropertyElem.setAttribute("property", propertyName);
                        if (displayLableId != null)
                        {
                            aspectPropertyElem.setAttribute("display-label-id", displayLableId);
                        }
                        appendChild(customPropertiesElem,aspectPropertyElem);                        
                        status = true;
                    }    
                    if (aspectPropertyElem != null && showOption.equals("false"))
                    {   
                        aspectPropertyElem.getParentNode().removeChild(aspectPropertyElem);
                        status = true;
                    }
                }                
            }        
            if (configType.equals("type"))
            {
                if (typeName != null && showOption != null)
                {
                    Element typeElem = XmlUtils.findFirstElement("type[@name='"+typeName+"']", contentTypesElem);
                    if (typeElem == null && showOption.equals("true"))
                    {                       
                        typeElem = webConfigDocument.createElement("type");
                        typeElem.setAttribute("name", typeName);
                        if (displayLableId != null)
                        {
                            typeElem.setAttribute("display-label-id", displayLableId);
                        }
                        appendChild(contentTypesElem,typeElem);                        
                        status = true;
                    }    
                    if (typeElem != null && showOption.equals("false"))
                    {   
                        typeElem.getParentNode().removeChild(typeElem);
                        status = true;
                    }
                }                
            }
            if (configType.equals("aspect"))
            {
                if (aspectName != null && showOption != null)
                {
                    Element aspectElem = XmlUtils.findFirstElement("aspect[@name='"+aspectName+"']", contentTypesElem);
                    if (aspectElem == null && showOption.equals("true"))
                    {                       
                        aspectElem = webConfigDocument.createElement("aspect");
                        aspectElem.setAttribute("name", aspectName);
                        if (displayLableId != null)
                        {
                            aspectElem.setAttribute("display-label-id", displayLableId);
                        }
                        appendChild(contentTypesElem,aspectElem);                        
                        status = true;
                    }    
                    if (aspectElem != null && showOption.equals("false"))
                    {   
                        aspectElem.getParentNode().removeChild(aspectElem);
                        status = true;
                    }
                }                
            }
        }

        return status;
    }

    /**
     * @param typeName
     * @param propertySheet
     * @return
     */
    public boolean syncPropertySheet(HashMap<String,String> propertySheet)
    {
        boolean status = false;

        Element rootElement = (Element) webConfigDocument.getFirstChild();
        String enableConfig = propertySheet.get("enable");
        String config = propertySheet.get("config");
        String typeOrAspectName = propertySheet.get("type-aspect-name");
        Element typeElem = XmlUtils.findFirstElement("config[@evaluator='"+config+"' and @condition='"+typeOrAspectName+"']", rootElement);
        if (typeElem == null)
        {
            if (enableConfig.equals("false"))
            {
                return status;
            }

            typeElem = webConfigDocument.createElement("config");
            typeElem.setAttribute("evaluator", config);
            typeElem.setAttribute("condition", typeOrAspectName);
            appendChild(rootElement,typeElem);
            status = true;
        }    

        Element propertySheetElem = XmlUtils.findFirstElement("property-sheet",typeElem);

        if (propertySheetElem == null)
        {
            if (enableConfig.equals("false"))
            {
                return status;
            }
            propertySheetElem = webConfigDocument.createElement("property-sheet");
            appendChild(typeElem,propertySheetElem);
            status = true;
        }   

        Element configElem = null;
        if (propertySheet.get("type").equals("property"))
        {
            configElem = XmlUtils.findFirstElement("show-property[@name='"+propertySheet.get("name")+"']",propertySheetElem);
            if (configElem == null)
            {
                if (enableConfig.equals("false"))
                {
                    return status;
                }
                configElem = webConfigDocument.createElement("show-property");
                appendChild(propertySheetElem,configElem);
                status = true;
            }
        } 
        else if (propertySheet.get("type").equals("association"))
        {
            configElem = XmlUtils.findFirstElement("show-association[@name='"+propertySheet.get("name")+"']",propertySheetElem);
            if (configElem == null)
            {
                if (enableConfig.equals("false"))
                {
                    return status;
                }
                configElem = webConfigDocument.createElement("show-association");
                appendChild(propertySheetElem,configElem);
                status = true;
            }
        }
        else if (propertySheet.get("type").equals("child-association"))
        {
            if (enableConfig.equals("false"))
            {
                return status;
            }
            configElem = XmlUtils.findFirstElement("show-child-association[@name='"+propertySheet.get("name")+"']",propertySheetElem);
            if (configElem == null)
            {
                configElem = webConfigDocument.createElement("show-child-association");
                appendChild(propertySheetElem,configElem);
                status = true;
            }
        }
        else if (propertySheet.get("type").equals("separator"))
        {
            if (enableConfig.equals("false"))
            {
                return status;
            }
            configElem = XmlUtils.findFirstElement("separator[@name='"+propertySheet.get("name")+"']",propertySheetElem);
            if (configElem == null)
            {
                configElem = webConfigDocument.createElement("separator");
                appendChild(propertySheetElem,configElem);
                status = true;
            }
        }

        if (enableConfig.equals("false"))
        {
            configElem.getParentNode().removeChild(configElem);
            status = true;
            return status;
        }

        if (configElem != null)
        {
            status = manageAttribute (configElem, "name", propertySheet) || status;
            status = manageAttribute (configElem, "display-label", propertySheet) || status;
            status = manageAttribute (configElem, "display-label-id", propertySheet) || status;
            status = manageAttribute (configElem, "converter", propertySheet) || status;
            status = manageAttribute (configElem, "read-only", propertySheet) || status;
            status = manageAttribute (configElem, "show-in-view-mode", propertySheet) || status;
            status = manageAttribute (configElem, "show-in-edit-mode", propertySheet) || status;
            status = manageAttribute (configElem, "component-generator",propertySheet) || status;
            if (propertySheet.get("type").equals("property"))
            {
                status = manageAttribute (configElem, "ignore-if-missing",propertySheet) || status;                    
            }
        }

        return status;
    }

    /**
     * @param configElem
     * @param attrName
     * @param properties
     * @return
     */
    private boolean manageAttribute (Element configElem, String attrName, HashMap<String,String> properties)
    {
        boolean status = false;
        String newValue = properties.get(attrName);
        if ( newValue != null && !newValue.equals(""))
        {
            if (!configElem.hasAttribute(attrName) || !configElem.getAttribute(attrName).equals(newValue))
            {
                status = true;
            }
            configElem.setAttribute(attrName,newValue);
        }
        else
        {
            if (configElem.hasAttribute(attrName))
            {
                configElem.removeAttribute(attrName);
                status = true;
            }
        }
        return status;
    }

    /**
     * @param parent
     * @param child
     */
    private  void appendChild (Node parent, Node child)
    {
        parent.appendChild(child);
        Node previousNode = child.getPreviousSibling();
        if (previousNode != null && previousNode instanceof org.w3c.dom.Text)
        {
            previousNode.getParentNode().removeChild(previousNode);
        }
    }
}
