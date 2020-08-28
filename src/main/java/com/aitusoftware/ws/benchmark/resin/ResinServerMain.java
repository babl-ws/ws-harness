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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aitusoftware.ws.benchmark.PortArgument;
import com.aitusoftware.ws.benchmark.PortProbe;
import com.aitusoftware.ws.benchmark.common.MessageReceiver;
import com.caucho.resin.HttpEmbed;
import com.caucho.resin.ResinEmbed;
import com.caucho.resin.ServletEmbed;
import com.caucho.resin.ServletMappingEmbed;
import com.caucho.resin.WebAppEmbed;
import com.caucho.websocket.WebSocketContext;
import com.caucho.websocket.WebSocketListener;
import com.caucho.websocket.WebSocketServletRequest;

import org.agrona.CloseHelper;
import org.agrona.concurrent.ShutdownSignalBarrier;

public final class ResinServerMain extends HttpServlet implements AutoCloseable
{
    private ResinEmbed server;
    private MessageReceiver<WebSocketContext> messageReceiver;
    private static volatile WebSocketListener listener;

    public static void main(final String[] args)
    {
        try (ResinServerMain server = new ResinServerMain())
        {
            server.start(PortArgument.port(args));
            new ShutdownSignalBarrier().await();
        }
    }

    public void start(final int port)
    {
        messageReceiver = new MessageReceiver<>(new WebSocketContextEventHandler());
        messageReceiver.start();
        listener = new EchoWebSocketListener(messageReceiver);
        server = launch(port);

        PortProbe.ensurePortOpen(port);
    }

    @Override
    public void close()
    {
        if (server != null)
        {
            server.close();
        }
        CloseHelper.close(messageReceiver);
    }

    public static ResinEmbed launch(final int port)
    {
        final ResinEmbed resin = new ResinEmbed();

        final HttpEmbed http = new HttpEmbed(port);
        resin.addPort(http);
        resin.setDevelopmentMode(false);

        final WebAppEmbed webApp = new WebAppEmbed("/", "/tmp/resin");
        final ServletEmbed servlet = new ServletEmbed();
        servlet.setServletClass(ResinServerMain.class.getName());
        servlet.setServletName("ws");
        webApp.addServlet(servlet);
        final ServletMappingEmbed mapping = new ServletMappingEmbed();
        mapping.setUrlPattern("/*");
        mapping.setServletName("ws");
        webApp.addServletMapping(mapping);
        resin.addWebApp(webApp);

        resin.start();

        return resin;
    }

    public void service(final HttpServletRequest req, final HttpServletResponse res) throws IOException
    {
        final WebSocketServletRequest wsReq = (WebSocketServletRequest)req;

        wsReq.startWebSocket(listener);
    }
}