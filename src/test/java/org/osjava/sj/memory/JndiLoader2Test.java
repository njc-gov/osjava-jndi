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
package org.osjava.sj.memory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osjava.sj.loader.FileBasedJndiLoader;
import org.osjava.sj.loader.JndiLoader;
import org.osjava.sj.loader.TestBean;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class JndiLoader2Test {

    private Context ctxt;

    @Before
    public void setUp() {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");

        /* For Directory-Naming
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        env.put(Context.URL_PKG_PREFIXES, "org.apache.naming");
        env.put("jndi.syntax.direction", "left_to_right");
        env.put("jndi.syntax.separator", "/");
        */

        try {
            // Creates an empty initial MemoryContext.
            ctxt = new InitialContext(env);
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
    }

    @After
    public void tearDown() throws NamingException {
        ctxt.close();
        this.ctxt = null;
        System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
        System.clearProperty("jndi.syntax.direction");
        System.clearProperty("jndi.syntax.separator");
    }

    @Test
    public void testProperties() {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        try {
            Properties props = new Properties();
            props.put("foo", "13");
            props.put("bar/foo", "42");
            props.put("bar/test/foo", "101");
            JndiLoader loader = new JndiLoader(env);
            loader.load( props, ctxt );
            assertEquals( "13", ctxt.lookup("foo") );
            assertEquals( "42", ctxt.lookup("bar/foo") );
            assertEquals( "101", ctxt.lookup("bar/test/foo") );
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

    @Test
    public void testDirectory() {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        env.put("org.osjava.sj.filenameToContext", "true");
        try {
            File file = new File("src/test/resources/roots/test.properties");
            FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
            loader.load( file, ctxt );
            assertEquals( "13", ctxt.lookup("test/value") );
        } catch(IOException ioe) {
            ioe.printStackTrace();
            fail("IOException: "+ioe.getMessage());
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

    @Test
    public void testDefaultFile() {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        try {
            File file = new File("src/test/resources/roots/default.properties");
            FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
            loader.load( file, ctxt );
            List list = (List) ctxt.lookup("name");
            assertEquals( "Henri", list.get(0) );
            assertEquals( "Fred", list.get(1) );
            assertEquals( "Foo", ctxt.lookup("com.genjava") );
        } catch(IOException ioe) {
            ioe.printStackTrace();
            fail("IOException: "+ioe.getMessage());
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

    @Test
    public void testFileAsRoot() throws Exception {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
        loader.load(loader.toProperties(
                new File("src/test/resources/roots/fileAsRoot.cfg")), ctxt);
        final String infoCmd = (String) ctxt.lookup("IMAGE_INFO_CMD");
        assertEquals(". /.profile_opix;$OCHOME/opt/Python-2.7/bin/python $OCHOME/opt/image_processor/scripts/imageinfo.py", infoCmd);
    }

    @Test
    public void testSubContext() {
        String dsString = "bing::::foofoo::::Boo";
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put("org.osjava.sj.filenameToContext", "true");
        env.put(JndiLoader.DELIMITER, "/");
        try {
            File file = new File("src/test/resources/roots/java.properties");

            FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
            loader.load( file, ctxt );
            Context subctxt = (Context) ctxt.lookup("java");
            assertEquals( dsString, subctxt.lookup("TestDS").toString() );
            DataSource ds = (DataSource) ctxt.lookup("java/TestDS");
            assertEquals( dsString, ds.toString() );
        } catch(IOException ioe) {
            ioe.printStackTrace();
            fail("IOException: "+ioe.getMessage());
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

    @Test
    public void testTopLevelDataSource() {
        String dsString = "org.gjt.mm.mysql.Driver::::jdbc:mysql://127.0.0.1/tmp::::sa";
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        try {
            File file = new File("src/test/resources/roots/TopLevelDS.properties");
            FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
            loader.load( file, ctxt );
            DataSource ds = (DataSource) ctxt.lookup("TopLevelDS");
            assertEquals( dsString, ds.toString() );
        } catch(IOException ioe) {
            ioe.printStackTrace();
            fail("IOException: "+ioe.getMessage());
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

    @Test
    public void testBoolean() {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        try {
            Properties props = new Properties();
            props.put("foo", "true");
            props.put("foo/type", "java.lang.Boolean");
            JndiLoader loader = new JndiLoader(env);
            loader.load( props, ctxt );
            assertEquals( new Boolean(true), ctxt.lookup("foo") );
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

    @Test
    public void testDate() {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        try {
            Properties props = new Properties();
            props.put("birthday", "2004-10-22");
            props.put("birthday/type", "java.util.Date");
            props.put("birthday/format", "yyyy-MM-dd");

            JndiLoader loader = new JndiLoader(env);
            loader.load( props, ctxt );

            Date d = (Date) ctxt.lookup("birthday");
            Calendar c = Calendar.getInstance();
            c.setTime(d);

            assertEquals( 2004, c.get(Calendar.YEAR) );
            assertEquals( 10 - 1, c.get(Calendar.MONTH) );
            assertEquals( 22, c.get(Calendar.DATE) );
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

    @Test
    public void testConverterPlugin() {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        try {
            Properties props = new Properties();
            props.put("math", "Pi");
            // type is needed here as otherwise it does not know to allow subelements
            props.put("math/type", "magic number");
            props.put("math/converter", "org.osjava.sj.loader.convert.PiConverter");

            JndiLoader loader = new JndiLoader(env);
            loader.load( props, ctxt );

            assertEquals( new Double(Math.PI), ctxt.lookup("math") );
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

    @Test
    public void testBeanConverter() {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        try {
            Properties props = new Properties();
            props.put("bean/type", "org.osjava.sj.loader.TestBean");
            props.put("bean/converter", "org.osjava.sj.loader.convert.BeanConverter");
            props.put("bean/text", "Example");

            JndiLoader loader = new JndiLoader(env);
            loader.load( props, ctxt );

            TestBean testBean = new TestBean();
            testBean.setText("Example");

            assertEquals( testBean, ctxt.lookup("bean") );
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

    @Test
    public void testDbcp() throws IOException, NamingException {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        File file = new File("src/test/resources/roots/pooltest");
        FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
        loader.load( file, ctxt );
        DataSource ds = (DataSource) ctxt.lookup("TestDS");
        DataSource ds1 = (DataSource) ctxt.lookup("OneDS");
        DataSource ds2 = (DataSource) ctxt.lookup("TwoDS");
        DataSource ds3 = (DataSource) ctxt.lookup("ThreeDS");

        try {
            Connection conn = ds.getConnection();
            fail("No database is hooked up, so this should have failed");
        } catch (SQLException sqle) {
            // expected
        }
    }

    @Test
    public void testDbcpPooltest() throws IOException, NamingException {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        File file = new File("src/test/resources/roots/pooltest");
        FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
        loader.load( file, ctxt );
        DataSource ds = (DataSource) ctxt.lookup("TestDS");
        DataSource ds1 = (DataSource) ctxt.lookup("OneDS");
        DataSource ds2 = (DataSource) ctxt.lookup("TwoDS");
        DataSource ds3 = (DataSource) ctxt.lookup("ThreeDS");

        try {
            Connection conn = ds.getConnection();
            fail("No database is hooked up, so this should have failed");
        } catch (SQLException sqle) {
            // expected
        }
    }

    @Test
    public void testDbcp1() throws IOException, NamingException {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        File file = new File("src/test/resources/roots/pooltest1");
        FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
        loader.load( file, ctxt );
        DataSource ds = (DataSource) ctxt.lookup("OneDS");
        try {
            Connection conn = ds.getConnection();
            fail("No database is hooked up, so this should have failed");
        } catch (SQLException sqle) {
            // expected
        }
    }

    @Test
    public void testDbcp2() throws IOException, NamingException {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        File file = new File("src/test/resources/roots/pooltest2");
        FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
        loader.load( file, ctxt );
        DataSource ds = (DataSource) ctxt.lookup("TestDS");
        try {
            Connection conn = ds.getConnection();
            fail("No database is hooked up, so this should have failed");
        } catch (SQLException sqle) {
            // expected
        }
    }

    @Test
    public void testDbcp3() throws IOException, NamingException {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        File file = new File("src/test/resources/roots/pooltest3");
        FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
        loader.load( file, ctxt );
        DataSource ds = (DataSource) ctxt.lookup("ThreeDS");
        try {
            Connection conn = ds.getConnection();
            fail("No database is hooked up, so this should have failed");
        } catch (SQLException sqle) {
            // expected
        }
    }

    @Test
    public void testDbcp4() throws IOException, NamingException {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        File file = new File("src/test/resources/roots/pooltest4");
        FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
        loader.load( file, ctxt );
        DataSource ds = (DataSource) ctxt.lookup("TwoDS");
        try {
            Connection conn = ds.getConnection();
            fail("No database is hooked up, so this should have failed");
        } catch (SQLException sqle) {
            // expected
        }
    }

    @Test
    public void testMultiValueAttributeIntegers() throws Exception {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        File file = new File("src/test/resources/roots/multiValueAttributes/integers");
        FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
        loader.load( file, ctxt );
        final LinkedList<Integer> ints = (LinkedList<Integer>) ctxt.lookup("person/age");
        assert ints.size() == 3;
    }

    @Test
    public void testMultiValueAttributeNoType() throws Exception {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        File file = new File("src/test/resources/roots/multiValueAttributes/noType");
        FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
        loader.load( file, ctxt );
        final LinkedList<String> ages = (LinkedList<String>) ctxt.lookup("person/name");
        assert ages.size() == 3;
    }

    /**
     * 0.11.4.1 java.lang.RuntimeException: Missing value
     */
    @Test
    public void testMultiValueAttributeBooleans() throws Exception {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        File file = new File("src/test/resources/roots/multiValueAttributes/booleans");
        FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
        loader.load( file, ctxt );
        final LinkedList<Boolean> booleans = (LinkedList<Boolean>) ctxt.lookup("person/myBooleans");
        assert booleans.size() == 3;
    }

    /**
     * 0.11.4.1 java.lang.ClassCastException: java.lang.String cannot be cast
     * to java.lang.Character
     */
    @Test
    public void testMultiValueAttributeCharacters() throws Exception {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        File file = new File("src/test/resources/roots/multiValueAttributes/characters");
        FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
        loader.load( file, ctxt );
        final LinkedList<Character> characters = (LinkedList<Character>) ctxt.lookup("person/spelledName");
        final StringWriter writer = new StringWriter(characters.size());
        for (Character character : characters) {
            writer.append(character);
        }
        "Holger".equals(writer.toString());
    }

    /**
     * 0.11.4.1 java.lang.ClassCastException: java.lang.String cannot be cast to
     * java.lang.Short
     */
    @Test
    public void testMultiValueAttributeShorts() throws Exception {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        File file = new File("src/test/resources/roots/multiValueAttributes/shorts");
        FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
        loader.load( file, ctxt );
        final LinkedList<Short> shorts = (LinkedList<Short>) ctxt.lookup("person/myShort");
        assert shorts.size() == 2;
        assert shorts.get(0) == (short) -32768;
    }

    @Test
    public void testMultiValueAttributeDoubles() throws Exception {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        File file = new File("src/test/resources/roots/multiValueAttributes/doubles");
        FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
        loader.load( file, ctxt );
        final LinkedList<Double> doubles = (LinkedList<Double>) ctxt.lookup("person/myDouble");
        assert doubles.size() == 3;
    }

    /**
     * 0.11.4.1 java.lang.RuntimeException: Missing value
     */
    @Test
    public void testMultiValueAttributeMultipleContexts() throws Exception {
        Hashtable env = new Hashtable();
        // Werte aus jndi.properties überschreiben
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.MemoryContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what I want. */
        env.put("jndi.syntax.direction", "left_to_right");
        /* Separator is required for non-flat */
        env.put("jndi.syntax.separator", "/");
        env.put(JndiLoader.DELIMITER, "/");
        File file = new File("src/test/resources/roots/multiValueAttributes");
        FileBasedJndiLoader loader = new FileBasedJndiLoader(env);
        loader.load( file, ctxt );
        final LinkedList<Integer> ints = (LinkedList<Integer>) ctxt.lookup("integers/person/age");
        assert ints.size() == 3;
        final LinkedList<String> ages = (LinkedList<String>) ctxt.lookup("noType/person/name");
        assert ages.size() == 3;
        final LinkedList<Boolean> booleans = (LinkedList<Boolean>) ctxt.lookup("booleans/person/myBooleans");
        assert booleans.size() == 3;
        final LinkedList<Character> characters = (LinkedList<Character>) ctxt.lookup("characters/person/spelledName");
        final StringWriter writer = new StringWriter(characters.size());
        for (Character character : characters) {
            writer.append(character);
        }
        "Holger".equals(writer.toString());
        final LinkedList<Short> shorts = (LinkedList<Short>) ctxt.lookup("shorts/person/myShort");
        assert shorts.size() == 2;
        assert shorts.get(0) == (short) -32768;
        final LinkedList<Double> doubles = (LinkedList<Double>) ctxt.lookup("doubles/person/myDouble");
        assert doubles.size() == 3;
    }
}
