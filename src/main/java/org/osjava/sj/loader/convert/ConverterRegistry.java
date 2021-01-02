/*
 * Copyright (c) 2005, Henri Yandell
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

package org.osjava.sj.loader.convert;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ConverterRegistry {

    private Map converters = new HashMap();

    // IMPROVE: Support inheritence; ie) key on Class not String?
    //       Use gj-core ClassMap code?
    public ConverterRegistry() {
        this.converters.put( "javax.sql.DataSource", new SJDataSourceConverter());
        this.converters.put( "java.util.Date", new DateConverter());
        this.converters.put( "java.lang.Boolean", new ConstructorConverter());
        this.converters.put( "java.lang.Byte", new ConstructorConverter());
        this.converters.put( "java.lang.Short", new ConstructorConverter());
        this.converters.put( "java.lang.Integer", new ConstructorConverter());
        this.converters.put( "java.lang.Long", new ConstructorConverter());
        this.converters.put( "java.lang.Float", new ConstructorConverter());
        this.converters.put( "java.lang.Double", new ConstructorConverter());
        this.converters.put( "java.lang.Character", new CharacterConverter());
        this.converters.put( "java.util.Map", new MapConverter());
    }

    @Nullable
    public ConverterIF getConverter(String type) {
        if(this.converters.containsKey(type)) {
            return (ConverterIF) converters.get(type);
        }
        return null;
    }

}
