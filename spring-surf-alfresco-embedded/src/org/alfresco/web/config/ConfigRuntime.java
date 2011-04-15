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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.extensions.config.source.StringConfigSource;
import org.springframework.extensions.config.xml.XMLConfigService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author drq
 *
 */
abstract public class ConfigRuntime
{
    private Logger logger = Logger.getLogger( ConfigRuntime.class.getName());

    protected XMLConfigService xmlConfigService;
    protected String configXml;
    protected Document configDocument;

    /**
     * @param configXml
     */
    public ConfigRuntime(String configXml)
    {
        super();
        this.configXml = configXml;
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
     * @return the configXml
     */
    public String getConfigXml()
    {
        return configXml;
    }

    /**
     * @param configXml the configXml to set
     */
    public void setConfigXml(String configXml)
    {
        this.configXml = configXml;
    }

    /**
     * @return the configDocument
     */
    public Document getConfigDocument()
    {
        return configDocument;
    }

    /**
     * @param configDocument the configDocument to set
     */
    public void setConfigDocument(Document configDocument)
    {
        this.configDocument = configDocument;
    }

    /**
     * @return
     */
    abstract public String getBaseConfigXml();

    /**
     * @return
     */
    public boolean initConfigRuntime()
    {
        // Add minimal base configurations?
        String baseConfigXml = getBaseConfigXml();
        List<String> xmlConfigFiles = new ArrayList<String>();
        xmlConfigFiles.add(baseConfigXml);
        xmlConfigFiles.add(configXml);
        initXMLConfigService(xmlConfigFiles);
        return true;
    }

    /**
     * @param xmlConfigFiles
     * @return
     */
    protected void initXMLConfigService(List<String> xmlConfigFiles)
    {		
        xmlConfigService = new XMLConfigService(new StringConfigSource(xmlConfigFiles));
        xmlConfigService.initConfig();
        toXmlDocument();
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
            configDocument =  DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(configXml.getBytes()));
        } 
        catch (Exception ex) 
        {
            logger.log(Level.SEVERE, "Failed to parse XML document",ex);
            throw new IllegalStateException("Failed to parse XML document",ex);
        }
        return configDocument;
    }

    /**
     * @return
     */
    public String toXml()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        XmlUtils.writeXml(baos, configDocument);
        configXml = baos.toString();
        return configXml;
    }

    /**
     * @param configElem
     * @param attrName
     * @param properties
     * @return
     */
    protected boolean manageAttribute (Element configElem, String attrName, HashMap<String,Object> properties)
    {
        return manageAttribute (configElem, attrName, (String)properties.get(attrName));
    }

    /**
     * @param configElem
     * @param attrName
     * @param attrValue
     * @return
     */
    protected boolean manageAttribute (Element configElem, String attrName, String attrValue)
    {
        boolean status = false;
        if ( attrValue != null && !attrValue.equals(""))
        {
            if (!configElem.hasAttribute(attrName) || !configElem.getAttribute(attrName).equals(attrValue))
            {
                status = true;
            }
            configElem.setAttribute(attrName,attrValue);
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
    protected  void appendChild (Node parent, Node child)
    {
        parent.appendChild(child);
        Node previousNode = child.getPreviousSibling();
        if (previousNode != null && previousNode instanceof org.w3c.dom.Text)
        {
            previousNode.getParentNode().removeChild(previousNode);
        }
    }

    /**
     * @param parent
     * @param child
     */
    protected  void removeChild (Node child)
    {
        Node previousNode = child.getPreviousSibling();
        if (previousNode != null && previousNode instanceof org.w3c.dom.Text)
        {
            previousNode.getParentNode().removeChild(previousNode);
        }
        child.getParentNode().removeChild(child);
    }
    /**
     * @param parent
     * @param child
     * @param sibling
     */
    protected  void insertChildBefore (Node parent, Node child, Node sibling)
    {
        if ( sibling == null )
        {
            if ( parent.getFirstChild() == null )
            {
                appendChild(parent,child);
            }
            else
            {
                parent.getFirstChild().insertBefore(child, parent.getFirstChild());
            }
        }
        else
        {
            sibling.insertBefore(child, sibling);
        }
    }

    protected  void insertChildAfter (Node parent, Node child, Node sibling)
    {
        if ( sibling == null )
        {
            if (parent.getFirstChild() != null)
            {
                parent.insertBefore(child,parent.getFirstChild());
            }
            else
            {
                appendChild(parent,child);
            }
        }
        else
        {
            if ( sibling.getNextSibling() == null)
            {
                appendChild(parent,child);
            }
            else
            {
                parent.insertBefore(child,sibling.getNextSibling());
            }
        }
    }
    
    /**
     * @author drq
     *
     */
    protected class SyncStatus
    {
        private boolean status;
        private Element element;
        /**
         * @param status
         * @param element
         */
        public SyncStatus(boolean status, Element element)
        {
            super();
            this.status = status;
            this.element = element;
        }
        /**
         * @return the status
         */
        public boolean isStatus()
        {
            return status;
        }
        /**
         * @param status the status to set
         */
        public void setStatus(boolean status)
        {
            this.status = status;
        }
        /**
         * @return the element
         */
        public Element getElement()
        {
            return element;
        }
        /**
         * @param element the element to set
         */
        public void setElement(Element element)
        {
            this.element = element;
        }
        
    }
}
