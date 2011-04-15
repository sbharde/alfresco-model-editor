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
package org.springframework.extensions.config.source;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.springframework.extensions.config.source.BaseConfigSource;

/**
 * @author drq
 *
 */
public class StringConfigSource extends BaseConfigSource
{
    /**
     * @param configStr
     */
    public StringConfigSource(String configStr)
    {
        this(Collections.singletonList(configStr));
    }
 
    /**
     * @param arg0
     */
    public StringConfigSource(List<String> arg0)
    {
        super(arg0);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.config.source.BaseConfigSource#getInputStream(java.lang.String)
     */
    @Override
    protected InputStream getInputStream(String configStr)
    {
        InputStream is = null;

        is = new BufferedInputStream(new ByteArrayInputStream(configStr.getBytes()));

        return is;
    }
}
