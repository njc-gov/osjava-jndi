/*
 * Copyright (c) 2005, Henri Yandell
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 *
 * + Neither the name of the osjava-jndi nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without 
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.osjava.sj.memory;

import org.junit.Test;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

import static org.junit.Assert.*;

public class SharedMemoryTest {

    private InitialContext createContext() throws NamingException {
        /* Initial configuration voodoo for the default context. */
        Hashtable contextEnv = new Hashtable();
        contextEnv.put("java.naming.factory.initial", "org.osjava.sj.MemoryContextFactory");

        /* The default is 'flat', which isn't hierarchial and not what I want. */
        contextEnv.put("jndi.syntax.direction", "left_to_right");

        /* Separator is required for non-flat */
        contextEnv.put("jndi.syntax.separator", "/");

        contextEnv.put("org.osjava.sj.jndi.shared", "true");
        
        /* The intial context. */
        return new InitialContext(contextEnv);
    }

    // Bug report submitted by Thomas A. Oehser
    @Test
    public void testSharedMemory() throws NamingException {
        InitialContext ctxA = null;
        try {
            String foo = "bar";
            ctxA = createContext();
            ctxA.bind("fooKey", foo);
            Object oA = ctxA.lookup("fooKey");
            Object oB = createContext().lookup("fooKey");
            assertNotNull("Shared memory failing", oB);
            assertEquals("Shared memory corrupting", oA, oB);
        } catch (NamingException e) {
            fail("NamingException " + e.getMessage());
        }
        finally {
            if (ctxA != null) {
                ctxA.close();
            }
        }
    }

    @Test
    public void testSharedSubContextMemory() throws NamingException {
        InitialContext context = null;
        try {
            context = createContext();
            context.createSubcontext("path");
            context.bind("path/foo", "42");
            context = createContext();
            assertEquals("42", context.lookup("path/foo") );
        } catch (NamingException e) {
            fail("NamingException " + e.getMessage());
        }
        finally {
            if (context != null) {
                context.close();
            }

        }
    }
}
