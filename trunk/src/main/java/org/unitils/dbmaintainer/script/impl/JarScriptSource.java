/*
 * Copyright 2006-2008,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id$
 */
package org.unitils.dbmaintainer.script.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptSource;
import org.unitils.dbmaintainer.script.impl.jar.DbScriptJarReader;
import org.unitils.util.PropertyUtils;

/**
 * @author Alexander Snaps <alex.snaps@gmail.com>
 * @author Filip Neven
 */
public class JarScriptSource extends DefaultScriptSource implements ScriptSource {

    public static final String DB_MAINTAINER_SCRIPT_JAR = "dbMaintainer.script.jar";

    @Override
	protected List<Script> loadAllScripts() {
    	String sourceJar = PropertyUtils.getString(DB_MAINTAINER_SCRIPT_JAR, configuration);
    	try {
        	List<Script> allScripts = new ArrayList<Script>();
            DbScriptJarReader jarReader = new DbScriptJarReader(sourceJar);
            for (Script script : jarReader) {
            	allScripts.add(script);
            }
            return allScripts;
        } catch(IOException e) {
            throw new UnitilsException("Error parsing JAR file " + sourceJar + " for scripts", e);
        }
	}
    
    @Test
    public void testJarScriptSource() {
    	JarScriptSource jarScriptSource = new JarScriptSource();
    	Properties conf = new ConfigurationLoader().getDefaultConfiguration();
    	conf.setProperty(DB_MAINTAINER_SCRIPT_JAR, "C:/scriptrunner.jar");
    	jarScriptSource.init(conf);
    	List<Script> scripts = jarScriptSource.getAllUpdateScripts();
    	System.out.println(scripts);
    }

}
