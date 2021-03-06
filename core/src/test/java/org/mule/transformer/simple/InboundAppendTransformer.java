/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transformer.simple;

import org.mule.transformer.simple.StringAppendTransformer;

public class InboundAppendTransformer  extends StringAppendTransformer
{
    public static String APPEND_STRING = " inbound";

    public InboundAppendTransformer()
    {
        super(APPEND_STRING);
    }

}


