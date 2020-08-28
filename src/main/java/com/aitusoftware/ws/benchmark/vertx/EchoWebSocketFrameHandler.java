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
package com.aitusoftware.ws.benchmark.vertx;

import com.aitusoftware.ws.benchmark.common.MessageReceiver;

import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

final class EchoWebSocketFrameHandler implements Handler<WebSocketFrame>
{
    private final ServerWebSocket webSocket;
    private final MessageReceiver<ServerWebSocket> messageReceiver;

    EchoWebSocketFrameHandler(
        final ServerWebSocket webSocket,
        final MessageReceiver<ServerWebSocket> messageReceiver)
    {
        this.webSocket = webSocket;
        this.messageReceiver = messageReceiver;
    }

    @Override
    public void handle(final WebSocketFrame frame)
    {
        if (frame.isFinal())
        {
            if (frame.isText())
            {
                throw new UnsupportedOperationException();
            }
            else
            {
                messageReceiver.publish(frame.binaryData(), webSocket);
            }
        }
    }
}
