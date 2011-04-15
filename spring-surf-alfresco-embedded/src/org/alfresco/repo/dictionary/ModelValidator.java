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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.service.namespace.QName;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.alfresco.repo.cache.EhCacheAdapter;
import org.alfresco.repo.dictionary.DictionaryDAOImpl.DictionaryRegistry;
import org.alfresco.repo.dictionary.NamespaceDAOImpl.NamespaceRegistry;
import org.alfresco.repo.tenant.SingleTServiceImpl;
import org.alfresco.repo.tenant.TenantService;

/**
 * @author drq
 *
 */
public class ModelValidator
{
    public static boolean validate (InputStream modelStream)
    {
        // construct list of models to test
        // include alfresco defaults
        List<String> bootstrapModels = new ArrayList<String>();
        bootstrapModels.add("alfresco/model/dictionaryModel.xml");
        bootstrapModels.add("alfresco/model/systemModel.xml");
        bootstrapModels.add("org/alfresco/repo/security/authentication/userModel.xml");
        bootstrapModels.add("alfresco/model/contentModel.xml");
        bootstrapModels.add("alfresco/model/wcmModel.xml");
        bootstrapModels.add("alfresco/model/applicationModel.xml");
        bootstrapModels.add("alfresco/model/bpmModel.xml");

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
        try
        {
            DictionaryBootstrap bootstrap = new DictionaryBootstrap();
            bootstrap.setModels(bootstrapModels);
            bootstrap.setDictionaryDAO(dictionaryDAO);
            bootstrap.bootstrap();
            
            M2Model model = M2Model.createModel(modelStream);
            
            System.out.println("Model Name is "+model.getName());
            
            dictionaryDAO.putModel(model);
            
            DictionaryComponent component = new DictionaryComponent();
            component.setDictionaryDAO(dictionaryDAO);
            
            //List all models by name
            for ( QName cModel : component.getAllModels() )
            {
                System.out.println(cModel.toString());
            }
            
            //Get aspects of our current model
            for ( M2Aspect aspect:model.getAspects())
            {
                System.out.println(aspect.getName());
            }
            
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Found an invalid model...");
            Throwable t = e;
            while (t != null)
            {
                System.out.println(t.getMessage());
                t = t.getCause();
            }
            return false;
        }
    }

    private static void initDictionaryCaches(DictionaryDAOImpl dictionaryDAO)
    {
        CacheManager cacheManager = new CacheManager();

        Cache dictionaryEhCache = new Cache("dictionaryCache", 50, false, true, 0L, 0L);
        cacheManager.addCache(dictionaryEhCache);
        EhCacheAdapter<String, DictionaryRegistry> dictionaryCache = new EhCacheAdapter<String, DictionaryRegistry>();
        dictionaryCache.setCache(dictionaryEhCache);

        dictionaryDAO.setDictionaryRegistryCache(dictionaryCache);
    }

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
