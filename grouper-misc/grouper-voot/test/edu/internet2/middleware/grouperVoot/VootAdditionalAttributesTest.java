/*******************************************************************************
 * Copyright 2012 Internet2
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/**
 * @author mchyzer
 * @author Andrea Biancini <andrea.biancini@gmail.com>
 */
package edu.internet2.middleware.grouperVoot;

import java.util.TreeSet;

import junit.textui.TestRunner;
import edu.emory.mathcs.backport.java.util.Arrays;
import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupSave;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.MembershipFinder;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.StemFinder;
import edu.internet2.middleware.grouper.attr.AttributeDef;
import edu.internet2.middleware.grouper.attr.AttributeDefName;
import edu.internet2.middleware.grouper.attr.AttributeDefType;
import edu.internet2.middleware.grouper.attr.AttributeDefValueType;
import edu.internet2.middleware.grouper.helper.SubjectTestHelper;

/**
 * Class to test the main service logic for the VOOT connector for Grouper.
 */
public class VootAdditionalAttributesTest extends VootTest {
  
  protected final static String[] ATTRIBUTE_DEFS = { "attribute01", "attribute02" };
  protected final static String[] ATTRIBUTE_NAMES = { "attribute_name_1", "attribute_name_2" };

  /**
   * Main method to execute all tests.
   * @param args parameters passed to main (ignored).
   */
  public static void main(String[] args) {
    TestRunner.run(new VootAdditionalAttributesTest("testPeopleMeAttribute1Value"));
  }
  
  /**
   * Default constructor to initialize VOOT test cases. 
   */
  public VootAdditionalAttributesTest() {
    super();
  }

  /**
   * Constructor with a test name as parameter.
   * @param name the name of this test execution on the test runner.
   */
  public VootAdditionalAttributesTest(String name) {
    super(name);
  }
  
  /**
   * Method to create a registry with all the required users and groups
   * to test all VOOT calls with this test suite.
   * 
   * To test all the combinations, the membership and read access to groups
   * will be assigned to two different subjects with the following logic:
   * <ul>
   * <li><b>Group 0</b>: Subject0 is member and has an attribute valued</li>
   * <li><b>Group 1</b>: Subject0 is member and does not have an attribute valued</li>
   * <li><b>Group 2</b>: Subject0 is member and has an attribute with two values</li>
   * <li><b>Group 3</b>: Subject0 is member and has two attributes valued</li>
   * </ul>
   */
  @Override
  @SuppressWarnings("unchecked")
  protected void createRegistryToTestVOOT() {
    // Setup data as root user
    GrouperSession grouperSession = GrouperSession.startRootSession();
    
    // Create all groups for managing the different membership relations
    Group[] groups = new Group[4];
    for (int i = 0 ; i < groups.length; ++i) {
      groups[i] = new GroupSave(grouperSession).assignName(GROUP_NAMES[i])
          .assignDescription(GROUP_DESCRIPTIONS[i]).assignCreateParentStemsIfNotExist(true).save();
      
      groups[i].addMember(SubjectTestHelper.SUBJ0, false);
    }

    Stem folder = StemFinder.findByName(grouperSession, STEM_NAME, true);
    
    AttributeDefName[] attributeNames = new AttributeDefName[2];
    for (int i = 0; i < attributeNames.length ; i++) {
      AttributeDef attributeDef = folder.addChildAttributeDef(ATTRIBUTE_DEFS[i], AttributeDefType.attr);
      attributeDef.setAssignToImmMembership(true);
      attributeDef.setValueType(AttributeDefValueType.string);
      attributeDef.store();
      attributeNames[i] = folder.addChildAttributeDefName(attributeDef, ATTRIBUTE_NAMES[i], ATTRIBUTE_NAMES[i]);
    }
    
    groups[0].getAttributeDelegate().addAttribute(attributeNames[0]);
    MembershipFinder.findImmediateMembership(grouperSession, groups[0], SubjectTestHelper.SUBJ0, Group.getDefaultList(), true)
      .getAttributeValueDelegate().assignValuesString(attributeNames[0].getDisplayName(), new TreeSet<String>(Arrays.asList(new String[]{"value 1"})), true);
    
    groups[2].getAttributeDelegate().addAttribute(attributeNames[0]);
    MembershipFinder.findImmediateMembership(grouperSession, groups[2], SubjectTestHelper.SUBJ0, Group.getDefaultList(), true)
      .getAttributeValueDelegate().assignValuesString(attributeNames[0].getDisplayName(), new TreeSet<String>(Arrays.asList(new String[]{"value 1", "value 2"})), true);
    
    groups[3].getAttributeDelegate().addAttribute(attributeNames[0]);
    groups[3].getAttributeDelegate().addAttribute(attributeNames[1]);
    MembershipFinder.findImmediateMembership(grouperSession, groups[3], SubjectTestHelper.SUBJ0, Group.getDefaultList(), true)
      .getAttributeValueDelegate().assignValuesString(attributeNames[0].getDisplayName(), new TreeSet<String>(Arrays.asList(new String[]{"value 1"})), true);
    MembershipFinder.findImmediateMembership(grouperSession, groups[3], SubjectTestHelper.SUBJ0, Group.getDefaultList(), true)
      .getAttributeValueDelegate().assignValuesString(attributeNames[1].getDisplayName(), new TreeSet<String>(Arrays.asList(new String[]{"value 2"})), true);

    // Stop root session
    GrouperSession.stopQuietly(grouperSession);
  }

  /**
   * Method that logs in with Subject0 and calls the URL:
   * /peope/@me
   * Note: running this will delete all data in the registry!
   */
  public void testPeopleMeAttribute1Value() {
    createRegistryToTestVOOT();
    
    //start session as logged in user to web service
    GrouperSession grouperSession = GrouperSession.start(SubjectTestHelper.SUBJ0);
    Object resultObject = callGroupsAPI("@me");

    //analyze the result
    int groupCount = 5;
    int[] groups = new int[]{ 1, 3, 4, 6, 10 };
    String[] roles = new String[]{ "admin", "member", "admin", "member", "manager" };
    validateGroups(resultObject, groupCount, null, 0, groupCount, groups, roles);

    GrouperSession.stopQuietly(grouperSession);
  }
  
}
