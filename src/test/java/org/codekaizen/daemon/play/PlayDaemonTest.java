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

import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for <code>PlayDaemon</code>.
 *
 * @author <a href="mailto:kbrockhoff@codekaizen.org">Kevin Brockhoff</a>
 */
public class PlayDaemonTest {

    @Test
    public void shouldInitializeWithCommandLineArguments() throws DaemonInitException {
        final String[] args = new String[4];
        args[0] = System.getProperty("java.io.tmpdir");
        args[1] = "disabled";
        args[2] = "9092";
        args[3] = "0.0.0.0";
        final DaemonContext context = mock(DaemonContext.class);
        when(context.getArguments()).thenReturn(args);
        final PlayDaemon daemon = new PlayDaemon();
        daemon.init(context);
        assertTrue(daemon.httpPort.isEmpty());
        assertFalse(daemon.httpsPort.isEmpty());
        assertEquals(args[3], daemon.address);
    }

}
