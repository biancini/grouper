/*
  Copyright 2004-2006 University Corporation for Advanced Internet Development, Inc.
  Copyright 2004-2006 The University Of Chicago

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

import  junit.framework.*;
import  org.apache.commons.logging.*;

/**
 * Run default tests.
 * @author  blair christensen.
 * @version $Id: SuiteDefault.java,v 1.4 2006-06-18 19:39:00 blair Exp $
 */
public class SuiteDefault extends TestCase {

  private static final Log LOG = LogFactory.getLog(SuiteDefault.class);

  public SuiteDefault(String name) {
    super(name);
  } // public SuiteDefault(name)

  static public Test suite() {
    TestSuite suite = new TestSuite();

    suite.addTest(  SuiteSettings.suite()     );
    suite.addTest(  SuiteSessions.suite()     );
    suite.addTest(  SuiteStems.suite()        );
    suite.addTest(  SuiteGroupTypes.suite()   );
    suite.addTest(  SuiteGroups.suite()       );
    suite.addTest(  SuiteComposites.suite()   );
    suite.addTest(  SuiteSubjects.suite()     );
    suite.addTest(  SuiteMembers.suite()      );
    suite.addTest(  SuiteMemberships.suite()  );
    suite.addTest(  SuiteMemberOf.suite()     );
    suite.addTest(  SuiteQueries.suite()      );

    // TODO Migrate

    suite.addTestSuite(TestBugsClosed.class);
    suite.addTestSuite(TestBugsOpen.class);

    suite.addTestSuite(TestAccessPrivilege.class);
    suite.addTestSuite(TestField.class);
    suite.addTestSuite(TestGrFiFindByName.class);
    suite.addTestSuite(TestGrFiFindByUuid.class);
    suite.addTestSuite(TestMemberFinder.class);
    // TODO suite.addTestSuite(TestMemberOfAccessPrivs.class);
    // TODO suite.addTestSuite(TestMemberOfNamingPrivs.class);
    suite.addTestSuite(TestNamingPrivilege.class);
    suite.addTestSuite(TestPrivADMIN.class);    // TODO group-with-priv checks
    suite.addTestSuite(TestPrivCREATE.class);  
    suite.addTestSuite(TestPrivOPTIN.class);    // TODO group-with-priv checks
    suite.addTestSuite(TestPrivOPTOUT.class);   // TODO group-with-priv checks
    suite.addTestSuite(TestPrivREAD.class);     // TODO group-with-priv checks
    suite.addTestSuite(TestPrivSTEM.class);     // TODO group-with-priv checks
    suite.addTestSuite(TestPrivVIEW.class);     // TODO group-with-priv checks
    suite.addTestSuite(TestPrivUPDATE.class);   // TODO group-with-priv checks
    suite.addTestSuite(TestStemFinder.class);
    suite.addTestSuite(TestWrongFieldType.class); 
    // TODO suite.addTestSuite(TestWheelGroup.class); 

    return suite;
  } // static public Test suite()

}

