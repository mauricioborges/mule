/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.streaming.processor;

import org.mule.api.DefaultMuleException;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.devkit.processor.DevkitBasedMessageProcessor;
import org.mule.streaming.Consumer;
import org.mule.streaming.ConsumerIterator;
import org.mule.streaming.ElementBasedPagingConsumer;
import org.mule.streaming.PagedBasedPagingConsumer;
import org.mule.streaming.PagingConfiguration;
import org.mule.streaming.PagingDelegate;
import org.mule.streaming.PagingDelegateProducer;
import org.mule.streaming.PagingDelegateWrapper;
import org.mule.streaming.Producer;
import org.mule.streaming.StreamingOutputUnit;

import java.util.Iterator;

/**
 * Base class for devkit generated pageable message processors. This processor
 * automatically takes care of obtaining a {@link org.mule.streaming.PagingDelegate}
 * and returning a {@link org.mule.streaming.ConsumerIterator.ConsumerIterator}
 * accordingly
 */
public abstract class AbstractDevkitBasedPageableMessageProcessor extends DevkitBasedMessageProcessor
{

    /**
     * This attribute specifies if the returned {@link Iterator} should return
     * individual elements or whole pages
     */
    private StreamingOutputUnit outputUnit = StreamingOutputUnit.ELEMENT;

    /**
     * The number of elements to obtain on each invocation to the data source
     */
    private String fetchSize;

    /**
     * Zero-based index of the first page to return. This value has to be equals or
     * greater than zero
     */
    private String firstPage;

    /**
     * Zero-based index of the last page to return. -1 means no limit. If not -1,
     * then it cannot be lower than {@link firstPage}
     */
    private String lastPage;

    public AbstractDevkitBasedPageableMessageProcessor(String operationName)
    {
        super(operationName);
    }

    /**
     * This method sets the message payload to an instance of
     * {@link org.mule.streaming.ConsumerIterator.ConsumerIterator} configured
     * accordingly to the configured outputUnit and the
     * {@link org.mule.streaming.PagingDelegate} obtained by invoking {@link
     * org.mule.streaming.processor.AbstractDevkitBasedPageableMessageProcessor.
     * getPagingDelegate(MuleEvent, PagingConfiguration)}
     * 
     * @return a {@link MuleEvent}
     * @throws IllegalArgumentException is firstPage is lower than zero or if
     *             lastPage is lower than zero and firstPage or if fetchSize is lower
     *             than zero
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final MuleEvent doProcess(MuleEvent event) throws Exception
    {
        PagingDelegate<?> delegate = this.getPagingDelegate(event, this.makeConfiguration(event));

        if (delegate == null)
        {
            throw new DefaultMuleException("Obtained paging delegate cannot be null");
        }

        delegate = new PagingDelegateWrapper(delegate);

        Producer<?> producer = new PagingDelegateProducer(delegate);
        Consumer<?> consumer = null;

        if (this.outputUnit == StreamingOutputUnit.ELEMENT)
        {
            consumer = new ElementBasedPagingConsumer(producer);
        }
        else if (this.outputUnit == StreamingOutputUnit.PAGE)
        {
            consumer = new PagedBasedPagingConsumer(producer);
        }
        else
        {
            throw new DefaultMuleException("Unsupported outputStrategy " + this.outputUnit);
        }

        event.getMessage().setPayload(new ConsumerIterator(consumer));

        return event;
    }

    /**
     * Implement this method to return the {@link org.mule.streaming.PagingDelegate}
     * to be used when paging. This method should never return <code>null</code>
     * 
     * @param event the current mule event
     * @param pagingConfiguration paging configuration parameters
     * @return a not null {@link org.mule.streaming.PagingDelegate}
     * @throws Exception
     */
    protected abstract PagingDelegate<?> getPagingDelegate(MuleEvent event,
                                                           PagingConfiguration pagingConfiguration)
        throws Exception;

    private PagingConfiguration makeConfiguration(MuleEvent event) throws MuleException
    {
        int transformedFetchSize = this.toInt(event, this.fetchSize);
        int transformedFirstPage = this.toInt(event, this.firstPage);
        int transformedLastPage = this.toInt(event, this.lastPage);

        if (transformedFetchSize <= 0)
        {
            throw new IllegalArgumentException("Fetch size cannot be lower than zero");
        }

        if (transformedFirstPage < 0)
        {
            throw new IllegalArgumentException("First page index cannot be lower than zero");
        }

        if (transformedLastPage > -1 && transformedLastPage < transformedFirstPage)
        {
            throw new IllegalArgumentException("Last page index cannot be lower than first page index");
        }

        return new PagingConfiguration(transformedFetchSize, transformedFirstPage, transformedLastPage,
            this.outputUnit);
    }

    private int toInt(MuleEvent event, Object source) throws MuleException
    {
        try
        {
            return (Integer) this.evaluateAndTransform(muleContext, event, Integer.class, null, source);
        }
        catch (Exception e)
        {
            throw new DefaultMuleException("Error evaluating paging expressions " + source, e);
        }
    }

    public void setOutputUnit(StreamingOutputUnit outputUnit)
    {
        this.outputUnit = outputUnit;
    }

    public void setFetchSize(String fetchSize)
    {
        this.fetchSize = fetchSize;
    }

    public void setFirstPage(String firstPage)
    {
        this.firstPage = firstPage;
    }

    public void setLastPage(String lastPage)
    {
        this.lastPage = lastPage;
    }

}
