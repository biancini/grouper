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
import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;

import edu.internet2.middleware.grouper.privs.AccessPrivilege;
import edu.internet2.middleware.grouper.registry.RegistryReset;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.subject.Subject;

/**
 * @author  blair christensen.
 * @version $Id: TestGroup27.java,v 1.8 2008-09-29 03:38:27 mchyzer Exp $
 */
public class TestGroup27 extends TestCase {

  // Private Class Constants
  private static final Log LOG = GrouperUtil.getLog(TestGroup27.class);


  public TestGroup27(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testCanWriteFieldValidFieldNotRoot() {
    LOG.info("testCanWriteFieldValidFieldNotRoot");
    try {
      R       r     = R.populateRegistry(1, 1, 1);
      Group   a     = r.getGroup("a", "a");
      Subject subjA = r.getSubject("a");
      a.grantPriv(subjA, AccessPrivilege.UPDATE);

      GrouperSession s = GrouperSession.start(subjA);
      Assert.assertTrue("can write", a.canWriteField(Group.getDefaultList()));
      s.stop();

      r.rs.stop();
    }
    catch (Exception e) {
      T.e(e);
    }
  } // public void testCanWriteFieldValidFieldNotRoot()

}

