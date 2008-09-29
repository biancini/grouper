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
import edu.internet2.middleware.grouper.registry.RegistryReset;
import edu.internet2.middleware.subject.Source;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import edu.internet2.middleware.subject.SubjectNotUniqueException;
import edu.internet2.middleware.subject.SubjectType;

/**
 * Test {@link GrouperSourceAdapter} class.
 * <p />
 * @author  blair christensen.
 * @version $Id: TestGrouperSourceAdapter.java,v 1.11 2008-09-29 03:38:27 mchyzer Exp $
 */
public class TestGrouperSourceAdapter extends TestCase {

  // Private Class Constants
  private static final  String ID   = "g:gsa:test";
  private static final  String NAME = "Grouper: Group Source Adapter: Test";


  // Private Instance Variables
  private Source sa;
  
  public TestGrouperSourceAdapter(String name) {
    super(name);
  }

  protected void setUp () {
    RegistryReset.internal_resetRegistryAndAddTestSubjects();
    sa = new GrouperSourceAdapter(ID, NAME);
  }

  protected void tearDown () {
    // Nothing 
  }

  // Tests
 
  public void testAdapter() { 
    Assert.assertNotNull("sa !null", sa);
    Assert.assertTrue("sa.id", sa.getId().equals(ID));
    Assert.assertTrue("sa.name", sa.getName().equals(NAME));
  } // public void testAdapter()

  public void testAdapterTypes() {
    Object[]    types = sa.getSubjectTypes().toArray();
    Assert.assertTrue("1 type", types.length == 1);
    SubjectType type  = (SubjectType) types[0];
    Assert.assertNotNull("type !null", type);
    Assert.assertTrue(
      "type instanceof SubjectType",
      type instanceof SubjectType
    );
    Assert.assertTrue(
      "type == group", type.getName().equals("group")
    );
  } // public void testAdapterTypes()

  public void testAdapterBadSubject() {
    try { 
      Subject subj = sa.getSubject("i do not exist");
      Assert.fail("found bad subject: " + subj);
    } 
    catch (SubjectNotFoundException e) {
      Assert.assertTrue("failed to find bad subject", true);
    }
    catch (SubjectNotUniqueException eSNU) {
      T.e(eSNU);
    }
  } // public void testAdapterBadSubject()

  public void testAdapterBadSubjectByIdentifier() {
    try { 
      Subject subj = sa.getSubjectByIdentifier("i do not exist");
      Assert.fail("found bad subject: " + subj);
    } 
    catch (SubjectNotFoundException e) {
      Assert.assertTrue("failed to find bad subject", true);
    }
    catch (SubjectNotUniqueException eSNU) {
      T.e(eSNU);
    }
  } // public void testAdapterBadSubjectByIdentifer() 

}

