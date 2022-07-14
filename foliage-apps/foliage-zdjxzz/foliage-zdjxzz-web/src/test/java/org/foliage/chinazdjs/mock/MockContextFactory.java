/*******************************************************************************
 * Copyright 2021 Greatwall Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.foliage.chinazdjs.mock;

import org.mockito.Mockito;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

/**
 * 
 * 
 * @author 1111395@greatwall.com
 * @version 1.0.0
 */
public class MockContextFactory implements InitialContextFactory {

    private static transient DirContext context;

    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        if (context == null) {
            synchronized (MockContextFactory.class) {
                if (context == null) {
                    context = (DirContext) Mockito.mock(DirContext.class);
                }
            }
        }
        return context;
    }

}
