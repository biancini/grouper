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

import edu.internet2.middleware.grouper.exception.InsufficientPrivilegeException;
import edu.internet2.middleware.grouper.misc.CompositeType;
import edu.internet2.middleware.grouper.registry.RegistryReset;
import edu.internet2.middleware.grouper.util.GrouperUtil;

/**
 * @author  blair christensen.
 * @version $Id: TestCompositeI1.java,v 1.11 2008-09-29 03:38:27 mchyzer Exp $
 * @since   1.0
 */
public class TestCompositeI1 extends TestCase {

  private static final Log LOG = GrouperUtil.getLog(TestCompositeI1.class);

  public TestCompositeI1(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testFailNotPrivilegedToDeleteCompositeMember() {
    LOG.info("testFailNotPrivilegedToDeleteCompositeMember");
    try {
      R               r   = R.populateRegistry(1, 3, 1);
      GrouperSession  nrs = GrouperSession.start( r.getSubject("a") );
      Group           a   = r.getGroup("a", "a");
      a.addCompositeMember(
        CompositeType.INTERSECTION, r.getGroup("a", "b"), r.getGroup("a", "c")
      );
      a.deleteCompositeMember();
      r.rs.stop();
      nrs.stop();
      Assert.fail("deleted composite without privilege to delete composite");
    }
    catch (InsufficientPrivilegeException eIP) {
      Assert.assertTrue("OK: cannot del union without privileges", true);
    }
    catch (Exception e) {
      T.e(e);
    }
  } // public void testFailNotPrivilegedToDeleteCompositeMember()

} // public class TestCompositeI1
