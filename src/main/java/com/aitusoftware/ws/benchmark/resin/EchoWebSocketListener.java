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
import java.io.InputStream;
import java.io.Reader;

import com.aitusoftware.ws.benchmark.common.MessageReceiver;
import com.caucho.websocket.WebSocketContext;
import com.caucho.websocket.WebSocketListener;

final class EchoWebSocketListener implements WebSocketListener
{
    private final MessageReceiver<WebSocketContext> messageReceiver;

    EchoWebSocketListener(final MessageReceiver<WebSocketContext> messageReceiver)
    {
        this.messageReceiver = messageReceiver;
    }

    @Override
    public void onStart(final WebSocketContext context)
    {
    }

    @Override
    public void onReadBinary(final WebSocketContext context, final InputStream is) throws IOException
    {
        messageReceiver.publish(is, context);
    }

    @Override
    public void onReadText(final WebSocketContext context, final Reader is)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onClose(final WebSocketContext context)
    {
        context.onClose(1001, "OK");
    }

    @Override
    public void onDisconnect(final WebSocketContext context)
    {

    }

    @Override
    public void onTimeout(final WebSocketContext context) throws IOException
    {

    }
}
