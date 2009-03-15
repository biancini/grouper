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

import org.apache.commons.logging.Log;

import edu.internet2.middleware.grouper.exception.SchemaException;
import edu.internet2.middleware.grouper.privs.AccessPrivilege;
import edu.internet2.middleware.grouper.registry.RegistryReset;
import edu.internet2.middleware.grouper.util.GrouperUtil;

/**
 * @author  blair christensen.
 * @version $Id: TestGroupType10.java,v 1.7 2009-03-15 06:37:22 mchyzer Exp $
 */
public class TestGroupType10 extends GrouperTest {

  // Private Static Class Constants
  private static final Log LOG = GrouperUtil.getLog(TestGroupType10.class);

  public TestGroupType10(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testDeleteType() {
    LOG.info("testDeleteType");
    try {
      R               r       = R.populateRegistry(0, 0, 0);
      GroupType       custom  = GroupType.createType(r.rs, "custom");
      Field           customA = custom.addAttribute(
        r.rs, "custom a", AccessPrivilege.ADMIN, AccessPrivilege.ADMIN, false
      );
      Field           customL = custom.addList(
        r.rs, "custom l", AccessPrivilege.ADMIN, AccessPrivilege.ADMIN
      );
      custom.delete(r.rs);
      try {
        GroupTypeFinder.find(custom.getName(), true);
        Assert.fail("FAIL: found deleted type");
      }
      catch (SchemaException eS) {
        Assert.assertTrue("OK: deleted type not found", true);
      }
      try {
        FieldFinder.find(customA.getName(), true);
        Assert.fail("FAIL: found deleted attribute");
      }
      catch (SchemaException eS) {
        Assert.assertTrue("OK: deleted attribute not found", true);
      }
      try {
        FieldFinder.find(customL.getName(), true);
        Assert.fail("FAIL: found deleted list");
      }
      catch (SchemaException eS) {
        Assert.assertTrue("OK: deleted list not found", true);
      }
      r.rs.stop();
    }
    catch (Exception e) {
      T.e(e);
    }
  } // public void testDeleteType()

}
