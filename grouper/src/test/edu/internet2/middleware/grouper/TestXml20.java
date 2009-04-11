/*
  Copyright (C) 2004-2007 University Corporation for Advanced Internet Development, Inc.
  Copyright (C) 2004-2007 The University Of Chicago

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package edu.internet2.middleware.grouper;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.commons.logging.Log;

import edu.internet2.middleware.grouper.registry.RegistryReset;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouper.xml.XmlExporter;
import edu.internet2.middleware.grouper.xml.XmlImporter;
import edu.internet2.middleware.grouper.xml.XmlReader;

/**
 * @author  blair christensen.
 * @version $Id: TestXml20.java,v 1.8 2008-09-29 03:38:27 mchyzer Exp $
 * @since   1.1.0
 */
public class TestXml20 extends GrouperTest {

  private static final Log LOG = GrouperUtil.getLog(TestXml20.class);

  public TestXml20(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testUpdateOkDoNotUpdateGroupAttrs() {
    LOG.info("testUpdateOkDoNotUpdateGroupAttrs");
    try {
      // Export - Setup
      R       r     = R.populateRegistry(1, 2, 0);
      Group   gA    = r.getGroup("a", "a");
      String  nameA = gA.getName();
      String  orig  = gA.getDescription();
      gA.setDescription(nameA);
      gA.store();
      assertGroupDescription(gA, nameA);
      r.rs.stop();

      // Export
      GrouperSession  s         = GrouperSession.start( SubjectFinder.findRootSubject() );
      Writer          w         = new StringWriter();
      XmlExporter     exporter  = new XmlExporter(s, new Properties());
      exporter.export(w);
      String          xml       = w.toString();
      s.stop();

      // Reset
      RegistryReset.reset();

      // Install Subjects and partial registry
      r = R.populateRegistry(1, 1, 0);
      assertFindGroupByName(r.rs, nameA, "recreate");
      r.rs.stop();

      // Update
      s = GrouperSession.start( SubjectFinder.findRootSubject() );
      XmlImporter importer  = new XmlImporter(s, new Properties());
      importer.update( XmlReader.getDocumentFromString(xml) );
      s.stop();

      // Import - Verify
      s   = GrouperSession.start( SubjectFinder.findRootSubject() );
      gA = assertFindGroupByName(s, nameA, "update");
      assertGroupDescription(gA, orig);
      s.stop();
    }
    catch (Exception e) {
      unexpectedException(e);
    }
  } // public void testUpdateOkDoNotUpdateGroupAttrs()

} // public class TestXml20
