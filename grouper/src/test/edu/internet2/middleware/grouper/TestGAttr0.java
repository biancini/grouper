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
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.logging.Log;

import edu.internet2.middleware.grouper.registry.RegistryReset;
import edu.internet2.middleware.grouper.util.GrouperUtil;

/**
 * @author  blair christensen.
 * @version $Id: TestGAttr0.java,v 1.5 2008-09-29 03:38:27 mchyzer Exp $
 * @since   1.1.0
 */
public class TestGAttr0 extends GrouperTest {

  private static final Log LOG = GrouperUtil.getLog(TestGAttr0.class);

  public TestGAttr0(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testGetAttributes() {
    LOG.info("testGetAttributes");
    try {
      R     r   = R.populateRegistry(1, 1, 0);
      Group gA  = r.getGroup("a", "a");

      Map attrs = gA.getAttributes();
      Assert.assertTrue("attrs !null", attrs != null);
      Assert.assertTrue("has extn"        , attrs.containsKey("extension")        );
      Assert.assertTrue("has displayExtn" , attrs.containsKey("displayExtension") );
      Assert.assertTrue("has name"        , attrs.containsKey("name")             );
      Assert.assertTrue("has displayName" , attrs.containsKey("displayName")      );
      T.amount("default attributes", 4, attrs.size()); 

      r.rs.stop();
    }
    catch (Exception e) {
      T.e(e);
    }
  } // public void testGetAttributes()

} // public class TestGAttr0
