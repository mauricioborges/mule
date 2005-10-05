/* 
* $Header$
* $Revision$
* $Date$
* ------------------------------------------------------------------------------------------------------
* 
* Copyright (c) SymphonySoft Limited. All rights reserved.
* http://www.symphonysoft.com
* 
* The software in this package is published under the terms of the BSD
* style license a copy of which has been included with this distribution in
* the LICENSE.txt file. 
*
*/
package org.mule.jbi.registry;

import org.mule.registry.Registry;
import org.mule.registry.RegistryFactory;
import org.mule.registry.RegistryStore;
import org.mule.ManagementContext;

/**
 * todo document
 *
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class JbiRegistryFactory implements RegistryFactory
 {
    public Registry create(RegistryStore store, ManagementContext context) {
        return new JbiRegistry(store, context);
    }
}
