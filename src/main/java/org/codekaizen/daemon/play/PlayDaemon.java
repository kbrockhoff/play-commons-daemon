/*
 *  Copyright (c) 2014 by Kevin Brockhoff.
 *  Kevin licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.codekaizen.daemon.play;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import play.api.Mode;
import play.core.StaticApplication;
import play.core.server.NettyServer;
import scala.Enumeration;
import scala.Option;

import java.io.File;

/**
 * Provides a wrapper for <code>play.core.server.NettyServer</code>
 * so running of a Play Framework 2.0 app can be managed by Apache Commons Daemon.
 *
 * @author  <a href="mailto:kbrockhoff@codekaizen.org">Kevin Brockhoff</a>
 */
public class PlayDaemon implements Daemon {

    static final String[] EMPTY_ARGS = {};
    static final String DISABLED = "disabled";

    String[] args = EMPTY_ARGS;
    File applicationHome = new File(System.getProperty("user.dir"));
    Option<Object> httpPort = Option.apply((Object) Integer.valueOf(9000));
    Option<Object> httpsPort = Option.apply((Object) Integer.valueOf(9443));
    String address = "0.0.0.0";
    NettyServer nettyServer;

    public PlayDaemon() {
        super();
    }

    @Override
    public void init(final DaemonContext context) throws DaemonInitException {
        if (context.getArguments() != null) {
            args = context.getArguments();
        }
        if (args.length > 0) {
            applicationHome = new File(args[0]);
        }
        if (args.length > 1) {
            final String port = args[1];
            if (DISABLED.equals(port)) {
                httpPort = Option.empty();
            } else {
                try {
                    httpPort = Option.apply((Object) Integer.valueOf(port));
                } catch (final NumberFormatException cause) {
                    throw new DaemonInitException("specified port [" + port + "] is invalid");
                }
            }
        }
        if (args.length > 2) {
            final String port = args[2];
            if (DISABLED.equals(port)) {
                httpsPort = Option.empty();
            } else {
                try {
                    httpsPort = Option.apply((Object) Integer.valueOf(port));
                } catch (final NumberFormatException cause) {
                    throw new DaemonInitException("specified port [" + port + "] is invalid");
                }
            }
        }
        if (args.length > 3) {
            address = args[3];
        }
        if (!applicationHome.isDirectory()) {
            throw new DaemonInitException("specified application home [" + applicationHome + "] is not a directory");
        }
    }

    @Override
    public void start() {
        final StaticApplication appProvider = new StaticApplication(applicationHome);
        final Enumeration.Value mode = Mode.Prod();
        nettyServer = new NettyServer(appProvider, httpPort, httpsPort, address, mode);
    }

    @Override
    public void stop() {
        if (nettyServer != null) {
            nettyServer.stop();
        }
    }

    @Override
    public void destroy() {
        args = EMPTY_ARGS;
    }

}
