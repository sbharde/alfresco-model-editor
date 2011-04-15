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
package org.alfresco.repo.dictionary;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.alfresco.repo.cache.EhCacheAdapter;
import org.alfresco.repo.dictionary.DictionaryDAOImpl.DictionaryRegistry;
import org.alfresco.repo.dictionary.NamespaceDAOImpl.NamespaceRegistry;
import org.alfresco.repo.tenant.SingleTServiceImpl;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.DictionaryService;


/**
 * @author drq
 *
 */
public class ModelRuntime
{
    private String modelXml;
    private M2Model model;
    private DictionaryService service;

    final public static String DEFAULT_MODEL_LIST = "alfresco/model/dictionaryModel.xml;alfresco/model/systemModel.xml;org/alfresco/repo/security/authentication/userModel.xml;alfresco/model/contentModel.xml;alfresco/model/wcmModel.xml;alfresco/model/applicationModel.xml;alfresco/model/bpmModel.xml";
    final public static String DEFAULT_MODEL_LIST_SEPARATOR = ";";

    /**
     * @param modelXml
     */
    public ModelRuntime(String modelXml)
    {
        this.modelXml = modelXml;
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

    /**
     * @return
     */
    public boolean initModelRuntime(String modelList)
    {
        InputStream modelStream = null;
        try
        {
            modelStream = new ByteArrayInputStream(modelXml.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e1)
        {
            e1.printStackTrace();
        }

        // include alfresco defaults
        List<String> bootstrapModels = new ArrayList<String>();

        if (modelList == null || modelList.equals(""))
        {
            bootstrapModels.addAll(Arrays.asList(DEFAULT_MODEL_LIST.split(DEFAULT_MODEL_LIST_SEPARATOR)));    
        }
        else
        {
            bootstrapModels.addAll(Arrays.asList(modelList.split(DEFAULT_MODEL_LIST_SEPARATOR)));  
        }
        // include models specified on command line
        //bootstrapModels.add(modelXmlPath);

        // construct dictionary dao        
        TenantService tenantService = new SingleTServiceImpl();

        NamespaceDAOImpl namespaceDAO = new NamespaceDAOImpl();
        namespaceDAO.setTenantService(tenantService);

        initNamespaceCaches(namespaceDAO);

        DictionaryDAOImpl dictionaryDAO = new DictionaryDAOImpl(namespaceDAO);
        dictionaryDAO.setTenantService(tenantService);

        initDictionaryCaches(dictionaryDAO);

        // bootstrap dao
        DictionaryBootstrap bootstrap = new DictionaryBootstrap();
        bootstrap.setModels(bootstrapModels);
        bootstrap.setDictionaryDAO(dictionaryDAO);
        bootstrap.bootstrap();

        model = M2Model.createModel(modelStream);
        dictionaryDAO.putModel(model);

        DictionaryComponent component = new DictionaryComponent();
        component.setDictionaryDAO(dictionaryDAO);

        service = component;    

        return true;


    }

    /**
     * @param dictionaryDAO
     */
    private static void initDictionaryCaches(DictionaryDAOImpl dictionaryDAO)
    {
        CacheManager cacheManager = new CacheManager();

        Cache dictionaryEhCache = new Cache("dictionaryCache", 50, false, true, 0L, 0L);
        cacheManager.addCache(dictionaryEhCache);
        EhCacheAdapter<String, DictionaryRegistry> dictionaryCache = new EhCacheAdapter<String, DictionaryRegistry>();
        dictionaryCache.setCache(dictionaryEhCache);

        dictionaryDAO.setDictionaryRegistryCache(dictionaryCache);
    }

    /**
     * @param namespaceDAO
     */
    private static void initNamespaceCaches(NamespaceDAOImpl namespaceDAO)
    {
        CacheManager cacheManager = new CacheManager();

        Cache namespaceEhCache = new Cache("namespaceCache", 50, false, true, 0L, 0L);
        cacheManager.addCache(namespaceEhCache);
        EhCacheAdapter<String, NamespaceRegistry> namespaceCache = new EhCacheAdapter<String, NamespaceRegistry>();
        namespaceCache.setCache(namespaceEhCache);

        namespaceDAO.setNamespaceRegistryCache(namespaceCache);
    }
}
