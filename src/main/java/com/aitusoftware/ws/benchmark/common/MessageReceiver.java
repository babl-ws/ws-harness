/*
 * Copyright 2019-2020 Aitu Software Limited.
 *
 * https://aitusoftware.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aitusoftware.ws.benchmark.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.ThreadFactory;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import io.vertx.core.buffer.Buffer;

public final class MessageReceiver<T> implements AutoCloseable
{
    private final InputStreamEventTranslator<T> inputStreamEventTranslator = new InputStreamEventTranslator<>();
    private final BufferEventTranslator<T> bufferEventTranslator = new BufferEventTranslator<>();
    private final CopyEventTranslator<T> copyEventTranslator = new CopyEventTranslator<>();
    private final Disruptor<Message<T>> toApplicationDisruptor;
    private final Disruptor<Message<T>> fromApplicationDisruptor;

    public MessageReceiver(
        final EventHandler<Message<T>> eventHandler)
    {
        toApplicationDisruptor = new Disruptor<>(Message::new, 8192, threadFactory(),
            ProducerType.MULTI, new BusySpinWaitStrategy());
        fromApplicationDisruptor = new Disruptor<>(Message::new, 8192, threadFactory(),
            ProducerType.SINGLE, new BusySpinWaitStrategy());
        toApplicationDisruptor.handleEventsWith(new EchoEventHandler());
        fromApplicationDisruptor.handleEventsWith(eventHandler);
    }

    private final class EchoEventHandler implements EventHandler<Message<T>>
    {
        @Override
        public void onEvent(final Message<T> event, final long sequence, final boolean endOfBatch) throws Exception
        {
            fromApplicationDisruptor.publishEvent(copyEventTranslator, event);
        }
    }

    private static final class CopyEventTranslator<T> implements EventTranslatorOneArg<Message<T>, Message<T>>
    {
        @Override
        public void translateTo(final Message<T> event, final long sequence, final Message<T> arg0)
        {
            System.arraycopy(arg0.input, 0, event.input, 0, arg0.length);
            event.context = arg0.context;
            event.length = arg0.length;
        }
    }

    public void start()
    {
        toApplicationDisruptor.start();
        fromApplicationDisruptor.start();
    }

    public void publish(final InputStream input, final T context)
    {
        toApplicationDisruptor.publishEvent(inputStreamEventTranslator, input, context);
    }

    public void publish(final Buffer input, final T context)
    {
        toApplicationDisruptor.publishEvent(bufferEventTranslator, input, context);
    }

    private static ThreadFactory threadFactory()
    {
        return runnable ->
        {
            final Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName("msg-processor");
            return thread;
        };
    }

    @Override
    public void close() throws Exception
    {
        toApplicationDisruptor.shutdown();
    }

    private static class BufferEventTranslator<T> implements EventTranslatorTwoArg<Message<T>, Buffer, T>
    {
        @Override
        public void translateTo(final Message<T> event, final long sequence, final Buffer arg0, final T arg1)
        {
            final int length = arg0.length();
            arg0.getBytes(event.input, 0);
            event.length = length;
            event.context = arg1;
        }
    }

    private static class InputStreamEventTranslator<T> implements EventTranslatorTwoArg<Message<T>, InputStream, T>
    {
        @Override
        public void translateTo(final Message<T> event, final long sequence, final InputStream arg0, final T arg1)
        {
            int c;
            int offset = 0;
            try
            {
                while ((c = arg0.read(event.input, offset, event.input.length - offset)) != -1)
                {
                    offset += c;
                }
                event.context = arg1;
                event.length = offset;
            }
            catch (final IOException e)
            {
                throw new UncheckedIOException(e);
            }
        }
    }
}
