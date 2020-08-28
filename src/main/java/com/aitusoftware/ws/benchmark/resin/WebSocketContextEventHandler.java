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
package com.aitusoftware.ws.benchmark.resin;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

import com.aitusoftware.ws.benchmark.common.Message;
import com.caucho.websocket.WebSocketContext;
import com.lmax.disruptor.EventHandler;

public final class WebSocketContextEventHandler implements EventHandler<Message<WebSocketContext>>
{
    @Override
    public void onEvent(final Message<WebSocketContext> event, final long sequence, final boolean endOfBatch)
    {
        final WebSocketContext context = event.getContext();
        try
        {
            final OutputStream outputStream = context.startBinaryMessage();
            outputStream.write(event.getInput(), 0, event.getLength());
            outputStream.close();
            context.flush();
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }
}
