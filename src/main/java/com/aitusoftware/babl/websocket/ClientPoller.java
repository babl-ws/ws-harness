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
package com.aitusoftware.babl.websocket;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

import org.agrona.nio.TransportPoller;

public final class ClientPoller extends TransportPoller
{
    public void execute()
    {
        try
        {
            if (selector.selectNow() != 0)
            {
                final SelectionKey[] keys = selectedKeySet.keys();
                for (int i = 0; i < selectedKeySet.size(); i++)
                {
                    final SelectionKey key = keys[i];
                    final ClientHandler session = (ClientHandler)key.attachment();

                    session.onSelect(key.isReadable(), false);
                }
                selectedKeySet.reset();
            }
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    public interface ClientHandler
    {
        void onSelect(boolean isReadable, boolean isWritable);
    }

    public void register(final SelectableClient client, final ClientHandler clientHandler)
    {
        try
        {
            client.channel.register(selector, SelectionKey.OP_READ).attach(clientHandler);
        }
        catch (final ClosedChannelException e)
        {
            throw new UncheckedIOException(e);
        }
    }
}
