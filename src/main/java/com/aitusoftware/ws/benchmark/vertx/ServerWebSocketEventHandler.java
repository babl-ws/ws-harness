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

import com.aitusoftware.ws.benchmark.common.Message;
import com.lmax.disruptor.EventHandler;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;

public class ServerWebSocketEventHandler implements EventHandler<Message<ServerWebSocket>>
{
    private final Buffer buffer = Buffer.buffer();

    @Override
    public void onEvent(final Message<ServerWebSocket> event, final long sequence, final boolean endOfBatch)
    {
        final ServerWebSocket webSocket = event.getContext();
        webSocket.writeFinalBinaryFrame(buffer.setBytes(0, event.getInput(), 0, event.getLength()));
    }
}