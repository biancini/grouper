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
import edu.internet2.middleware.grouper.exception.GrouperException;
import edu.internet2.middleware.grouper.privs.NamingWrapper;


/**
 * Test {@link NamingWrapper}.
 * <p/>
 * @author  blair christensen.
 * @version $Id: Test_privs_NamingWrapper.java,v 1.5 2009-03-15 06:37:22 mchyzer Exp $
 * @since   1.2.1
 */
public class Test_privs_NamingWrapper extends GrouperTest {


  private GrouperSession s;



  public void setUp() {
    super.setUp();
    try {
      this.s = GrouperSession.start( SubjectFinder.findAllSubject() );
    }
    catch (Exception e) {
      throw new GrouperException( "test setUp() error: " + e.getMessage(), e );
    }
  }

  public void tearDown() {
    super.tearDown();
  }



  /**
   * @since   1.2.1
   */
  public void test_constructor_nullSession() {
    try {
      new NamingWrapper(null, null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }
  /**
   * @since   1.2.1
   */
  public void test_constructor_nullAdapter() {
    try {
      new NamingWrapper(this.s, null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }

}
