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

import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.privs.AccessPrivilege;
import edu.internet2.middleware.grouper.registry.RegistryReset;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.subject.Subject;

/**
 * @author  blair christensen.
 * @version $Id: TestGroupType11.java,v 1.8 2009-03-15 06:37:22 mchyzer Exp $
 */
public class TestGroupType11 extends GrouperTest {

  // Private Static Class Constants
  private static final Log LOG = GrouperUtil.getLog(TestGroupType11.class);

  public TestGroupType11(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testUseCustomAttributeAsNonRoot() {
    LOG.info("testUseCustomAttributeAsNonRoot");
    try {
      R         r       = R.populateRegistry(1, 1, 1);
      Group     gA      = r.getGroup("a", "a");
      String    name    = gA.getName();
      Subject   subjA   = r.getSubject("a");
      GroupType custom  = GroupType.createType(r.rs, "custom");
      Field     attr    = custom.addAttribute(
        r.rs, "custom a", AccessPrivilege.ADMIN, AccessPrivilege.ADMIN, false
      );
      gA.addType(custom);
      gA.grantPriv(subjA, AccessPrivilege.ADMIN);
      r.rs.stop();

      // Now test-and-set attribute as !root
      GrouperSession  s = GrouperSession.start(subjA);
      Group           g = GroupFinder.findByName(s, name, true);
      Assert.assertTrue(
        "group has custom type", g.hasType(custom)
      );         
      Assert.assertTrue(
        "group does not have attribute set - yet",
        g.getAttribute(attr.getName()).equals(GrouperConfig.EMPTY_STRING)
      );
      try {
        g.setAttribute(attr.getName(), name);
        g.store();
        Assert.assertTrue("set attribute", true);
      }
      catch (Exception e) {
        Assert.fail("exception while setting custom attribute! - " + e.getMessage());
      }
      T.string("now group has attribute set", name, g.getAttribute(attr.getName()));
      s.stop();

      // Now make sure it was properly persisted
      GrouperSession  S = GrouperSession.start(subjA);
      Group           G = GroupFinder.findByName(S, name, true);
      T.string("attribute was persisted", name, G.getAttribute(attr.getName()));
      S.stop();
    }
    catch (Exception e) {
      T.e(e);
    }
  } // public void testUseCustomAttributeAsNonRoot()

} // public class TestGroupType11
