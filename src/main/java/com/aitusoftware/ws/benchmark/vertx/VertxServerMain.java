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

import com.aitusoftware.ws.benchmark.PortArgument;
import com.aitusoftware.ws.benchmark.common.MessageReceiver;

import org.agrona.CloseHelper;
import org.agrona.concurrent.ShutdownSignalBarrier;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;

public final class VertxServerMain implements AutoCloseable
{
    private static final int THREAD_POOL_SIZE = Integer.getInteger("babl.bench.pool.size", 1);
    private final int port;
    private Vertx vertx;
    private MessageReceiver<ServerWebSocket> messageReceiver;

    public static void main(final String[] args) throws Exception
    {
        try (VertxServerMain server = new VertxServerMain(PortArgument.port(args)))
        {
            server.start();
            try
            {
                new ShutdownSignalBarrier().await();
            }
            finally
            {
                server.close();
            }
        }
    }

    private final class EchoVerticle extends AbstractVerticle
    {
        private HttpServer httpServer;

        @Override
        public void start() throws Exception
        {
            final HttpServerOptions options = new HttpServerOptions();
            options.setTcpNoDelay(true).setReusePort(true);

            httpServer = vertx.createHttpServer(options);
            httpServer.websocketHandler(new Handler<ServerWebSocket>()
            {
                @Override
                public void handle(final ServerWebSocket event)
                {
                    event.frameHandler(new EchoWebSocketFrameHandler(event, messageReceiver));
                }
            });
            httpServer.listen(port);
        }
    }

    VertxServerMain(final int port)
    {
        this.port = port;
    }

    void start() throws Exception
    {
        messageReceiver = new MessageReceiver<>(new ServerWebSocketEventHandler());
        messageReceiver.start();
        vertx = Vertx.vertx(new VertxOptions().setEventLoopPoolSize(THREAD_POOL_SIZE));
        for (int i = 0; i < THREAD_POOL_SIZE; i++)
        {
            vertx.deployVerticle(new EchoVerticle());
        }
    }

    @Override
    public void close() throws Exception
    {
        if (vertx != null)
        {
            vertx.close();
        }
        CloseHelper.close(messageReceiver);
    }
}
