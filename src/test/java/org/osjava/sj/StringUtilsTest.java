/*
 * Copyright (c) 2003-2005, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of osjava-jndi nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
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
package org.osjava.sj;

import org.junit.Test;
import org.osjava.StringUtils;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

    @Test
    public void testReplace() {
        assertEquals("one:two:three", StringUtils.replace("one--two--three", "--", ":" ) );
        assertEquals("one:two:", StringUtils.replace("one--two--", "--", ":" ) );
        assertEquals(":two:three", StringUtils.replace("--two--three", "--", ":" ) );
    }

    @Test
    public void testSplit() {
        assertArrayEquals("", new String[] { "config", "one", "two"}, StringUtils.split("config.one.two", ".") );
        assertArrayEquals("", new String[] { "one", "two", "three"}, StringUtils.split("one.two.three", ".") );
        assertArrayEquals("", new String[] { "one"}, StringUtils.split("one", ".") );
        assertArrayEquals("", new String[] { ""}, StringUtils.split("", ".") );
    }

    private void assertArrayEquals(String message, String[] array1, String[] array2) {
        assertEquals(message+" - Length not equal ", array1.length, array2.length);
        for(int i=0; i<array1.length; i++) {
            assertEquals(message+" - array[" + i + "] element not equal ", array1[i], array2[i]);
        }
    }

}
