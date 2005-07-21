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
 */
package org.mule.providers.soap;

import org.mule.impl.endpoint.MuleEndpointURI;
import org.mule.providers.service.ConnectorFactory;
import org.mule.providers.service.ConnectorServiceDescriptor;
import org.mule.tck.NamedTestCase;
import org.mule.umo.endpoint.UMOEndpointURI;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class SoapEndpointTestCase extends NamedTestCase
{
    public void testEndpoint() throws Exception
    {
        String url = "glue:http://www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2";
        UMOEndpointURI endpointUri = new MuleEndpointURI(url);
        assertEquals("glue", endpointUri.getSchemeMetaInfo());
        // it's up to the client to actually strip off the method name if
        // necessary
        assertEquals("http://www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2", endpointUri.getAddress());
        assertEquals("getSomething", endpointUri.getParams().getProperty("method"));
        assertEquals(3, endpointUri.getParams().size());

        url = "glue:http://www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2";
        endpointUri = new MuleEndpointURI(url);
        assertEquals("glue", endpointUri.getSchemeMetaInfo());
        assertEquals("http://www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2", endpointUri.getAddress());
        assertEquals("getSomething", endpointUri.getParams().getProperty("method"));
        assertEquals(3, endpointUri.getParams().size());
    }

    public void testEndpointWithUserInfo() throws Exception
    {
        String url = "glue:http://admin:pwd@www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2";
        UMOEndpointURI endpointUri = new MuleEndpointURI(url);
        assertEquals("glue", endpointUri.getSchemeMetaInfo());
        // it's up to the client to actually strip off the method name if
        // necessary
        assertEquals("http://www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2", endpointUri.getAddress());
        assertEquals("getSomething", endpointUri.getParams().getProperty("method"));
        assertEquals(3, endpointUri.getParams().size());
        assertEquals("admin:pwd", endpointUri.getUserInfo());
        assertEquals("admin", endpointUri.getUsername());
        assertEquals("pwd", endpointUri.getPassword());
    }

    public void testEndpointFinder() throws Exception
    {
        String url = "soap:http://www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2";
        UMOEndpointURI endpointUri = new MuleEndpointURI(url);
        assertEquals("soap", endpointUri.getSchemeMetaInfo());
        // it's up to the client to actually strip off the method name if
        // necessary
        assertEquals("http://www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2", endpointUri.getAddress());
        assertEquals("getSomething", endpointUri.getParams().getProperty("method"));
        assertEquals(3, endpointUri.getParams().size());

        ConnectorServiceDescriptor csd = ConnectorFactory.getServiceDescriptor("soap");
        assertEquals("axis", csd.getProtocol());
        assertEquals("org.mule.providers.soap.axis.AxisConnector", csd.getConnector());

    }
}
