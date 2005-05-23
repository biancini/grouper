/*
 * Copyright (C) 2004-2005 University Corporation for Advanced Internet Development, Inc.
 * Copyright (C) 2004-2005 The University Of Chicago
 * All Rights Reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *  * Neither the name of the University of Chicago nor the names
 *    of its contributors nor the University Corporation for Advanced
 *   Internet Development, Inc. may be used to endorse or promote
 *   products derived from this software without explicit prior
 *   written permission.
 *
 * You are under no obligation whatsoever to provide any enhancements
 * to the University of Chicago, its contributors, or the University
 * Corporation for Advanced Internet Development, Inc.  If you choose
 * to provide your enhancements, or if you choose to otherwise publish
 * or distribute your enhancements, in source code form without
 * contemporaneously requiring end users to enter into a separate
 * written license agreement for such enhancements, then you thereby
 * grant the University of Chicago, its contributors, and the University
 * Corporation for Advanced Internet Development, Inc. a non-exclusive,
 * royalty-free, perpetual license to install, use, modify, prepare
 * derivative works, incorporate into the software or other computer
 * software, distribute, and sublicense your enhancements or derivative
 * works thereof, in binary and source code form.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND WITH ALL FAULTS.  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT ARE DISCLAIMED AND the
 * entire risk of satisfactory quality, performance, accuracy, and effort
 * is with LICENSEE. IN NO EVENT SHALL THE COPYRIGHT OWNER, CONTRIBUTORS,
 * OR THE UNIVERSITY CORPORATION FOR ADVANCED INTERNET DEVELOPMENT, INC.
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OR DISTRIBUTION OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package test.edu.internet2.middleware.grouper;

import  edu.internet2.middleware.grouper.*;
import  edu.internet2.middleware.subject.*;

import  java.util.*;
import  junit.framework.*;

public class TestGroupsMoFAdd1 extends TestCase {

  public TestGroupsMoFAdd1(String name) {
    super(name);
  }

  protected void setUp () {
    DB db = new DB();
    db.emptyTables();
    db.stop();
  }

  protected void tearDown () {
    // Nothing -- Yet
  }


  /*
   * TESTS
   */
  

  //
  // Add m0 to g0
  // Add m0 to g1
  //
  // m0 -> g0
  //  \--> g1
  //
  public void testMoF() {
    Subject subj = null;
    try {
      subj = SubjectFactory.getSubject(Constants.rootI, Constants.rootT);
    } catch (SubjectNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    GrouperSession s = GrouperSession.start(subj);

    // Create ns0
    GrouperStem ns0 = GrouperStem.create(
                         s, Constants.ns0s, Constants.ns0e
                       );
    Assert.assertNotNull("ns0 !null", ns0);
    // Create ns1
    GrouperStem ns1 = GrouperStem.create(
                         s, Constants.ns1s, Constants.ns1e
                       );
    Assert.assertNotNull("ns0 !null", ns0);
    // Create g0
    GrouperGroup g0  = GrouperGroup.create(
                         s, Constants.g0s, Constants.g0e
                       );
    Assert.assertNotNull("g0 !null", g0);
    // Create g1
    GrouperGroup g1  = GrouperGroup.create(
                         s, Constants.g1s, Constants.g1e
                       );
    Assert.assertNotNull("g1 !null", g1);
    // Load m0
    GrouperMember m0 = GrouperMember.load(
                         s, Constants.mem0I, Constants.mem0T
                       );
    Assert.assertNotNull("m0 !null", m0);
    // Add m0 to g0's "members"
    try {
      g0.listAddVal(m0);
    } catch (RuntimeException e) {
      Assert.fail("add m0 to g0");
    }
    // Add m0 to g1's "members"
    try {
      g1.listAddVal(m0);
    } catch (RuntimeException e) {
      Assert.fail("add m0 to g1");
    }

    // Now inspect g0's, resulting list values
    Assert.assertTrue(
      "members == 1", g0.listVals("members").size() == 1
    );
    Assert.assertTrue(
      "imm members == 1", g0.listImmVals("members").size() == 1
    );
    Assert.assertTrue(
      "eff members == 0", g0.listEffVals("members").size() == 0
    );
    Iterator iter0I = g0.listImmVals("members").iterator();
    while (iter0I.hasNext()) {
      GrouperList lv = (GrouperList) iter0I.next();
      Assert.assertTrue("g0 empty chain", lv.chain().size() == 0);
      Assert.assertNull("g0 null via", lv.via());
    }

    // Now inspect g1's, resulting list values
    Assert.assertTrue(
      "members == 1", g1.listVals("members").size() == 1
    );
    Assert.assertTrue(
      "imm members == 1", g1.listImmVals("members").size() == 1
    );
    Assert.assertTrue(
      "eff members == 0", g1.listEffVals("members").size() == 0
    );
    Iterator iter1I = g1.listImmVals("members").iterator();
    while (iter1I.hasNext()) {
      GrouperList lv = (GrouperList) iter1I.next();
      Assert.assertTrue("g1 empty chain", lv.chain().size() == 0);
      Assert.assertNull("g1 null via", lv.via());
    }

    s.stop();
  }

}

