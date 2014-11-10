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

import java.util.HashMap;
import java.util.Map;

import junit.textui.TestRunner;
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
import edu.internet2.middleware.grouper.privs.AccessPrivilege;
import edu.internet2.middleware.grouperVoot.beans.VootPerson;
import edu.internet2.middleware.grouperVoot.messages.VootGetGroupsResponse;
import edu.internet2.middleware.grouperVoot.messages.VootGetMembersResponse;
import edu.internet2.middleware.subject.Subject;

/**
 * Class to test the main service logic for the VOOT connector for Grouper.
 */
public class VootAdditionalAttributesTest extends VootTest {
  
  protected final static String[] ATTRIBUTE_DEFS = { "attribute01", "attribute02" };
  protected final static String[] ATTRIBUTE_NAMES = { "attribute_name_1", "attribute_name_2" };
  protected final static String[] ATTRIBUTE_VALUES = { "attribute value 1", "attribute value 2" };

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
   * <li><b>Group 2</b>: Subject0 is member and has two attributes valued</li>
   * <li><b>Group 3</b>: Subject0 is member and group has an attribute valued</li>
   * <li><b>Group 4</b>: Subject0 is member and group has two attributes valued</li>
   * </ul>
   */
  @Override
  protected void createRegistryToTestVOOT() {
    // Setup data as root user
    GrouperSession grouperSession = GrouperSession.startRootSession();
    
    // Create all groups for managing the different membership relations
    Group[] groups = new Group[5];
    for (int i = 0 ; i < groups.length; ++i) {
      groups[i] = new GroupSave(grouperSession).assignName(GROUP_NAMES[i])
          .assignDescription(GROUP_DESCRIPTIONS[i]).assignCreateParentStemsIfNotExist(true).save();
      
      groups[i].addMember(SubjectTestHelper.SUBJ0, false);
      groups[i].grantPriv(SubjectTestHelper.SUBJ0, AccessPrivilege.ADMIN, false);
      groups[i].grantPriv(SubjectTestHelper.SUBJ0, AccessPrivilege.GROUP_ATTR_READ, false);
    }

    Stem folder = StemFinder.findByName(grouperSession, STEM_NAME, true);
    
    AttributeDefName[] attributeNames = new AttributeDefName[2];
    for (int i = 0; i < attributeNames.length ; i++) {
      AttributeDef attributeDef = folder.addChildAttributeDef(ATTRIBUTE_DEFS[i], AttributeDefType.attr);
      attributeDef.setMultiAssignable(true);
      attributeDef.setAssignToImmMembership(true);
      attributeDef.setAssignToGroup(true);
      attributeDef.setValueType(AttributeDefValueType.string);
      attributeDef.store();
      attributeNames[i] = folder.addChildAttributeDefName(attributeDef, ATTRIBUTE_NAMES[i], ATTRIBUTE_NAMES[i]);
    }
    
    MembershipFinder.findImmediateMembership(grouperSession, groups[0], SubjectTestHelper.SUBJ0, Group.getDefaultList(), true)
      .getAttributeDelegate().assignAttribute(attributeNames[0]);
    MembershipFinder.findImmediateMembership(grouperSession, groups[0], SubjectTestHelper.SUBJ0, Group.getDefaultList(), true)
      .getAttributeValueDelegate().assignValue(attributeNames[0].getDisplayName(), ATTRIBUTE_VALUES[0]);
    
    MembershipFinder.findImmediateMembership(grouperSession, groups[2], SubjectTestHelper.SUBJ0, Group.getDefaultList(), true)
      .getAttributeValueDelegate().assignValue(attributeNames[0].getDisplayName(), ATTRIBUTE_VALUES[0]);
    MembershipFinder.findImmediateMembership(grouperSession, groups[2], SubjectTestHelper.SUBJ0, Group.getDefaultList(), true)
      .getAttributeValueDelegate().assignValue(attributeNames[1].getDisplayName(), ATTRIBUTE_VALUES[1]);
    
    groups[3].getAttributeDelegate().assignAttribute(attributeNames[0]);
    groups[3].getAttributeValueDelegate().assignValue(attributeNames[0].getDisplayName(), ATTRIBUTE_VALUES[0]);
    
    groups[4].getAttributeDelegate().assignAttribute(attributeNames[0]);
    groups[4].getAttributeDelegate().assignAttribute(attributeNames[1]);
    groups[4].getAttributeValueDelegate().assignValue(attributeNames[0].getDisplayName(), ATTRIBUTE_VALUES[0]);
    groups[4].getAttributeValueDelegate().assignValue(attributeNames[1].getDisplayName(), ATTRIBUTE_VALUES[1]);

    // Stop root session
    GrouperSession.stopQuietly(grouperSession);
  }

  /**
   * Method that logs in with Subject0 and calls the URL:
   * /people/@me/vootTest:group0
   * Note: running this will delete all data in the registry!
   */
  public void testPeopleMeAttribute1Value() {
    createRegistryToTestVOOT();
    
    //start session as logged in user to web service
    //GrouperSession grouperSession = GrouperSession.start(SubjectTestHelper.SUBJ0, true);
    //FIXME: don't know why but with non root user the group attributes are not retrieved
    GrouperSession grouperSession = GrouperSession.startRootSession();
    Object resultObject = callPeopleAPI("@me", GROUP_NAMES[0]);

    //analyze the result
    Subject[] subjects = new Subject[]{ SubjectTestHelper.SUBJ0 };
    String[] roles = new String[]{ "admin" };
    validateMembers(resultObject, 1, null, 0, 1, subjects, roles);
    
    VootGetMembersResponse vootGetMembersResponse = (VootGetMembersResponse) resultObject;
    VootPerson curPerson = null;
    
    for (int i = 0; i < vootGetMembersResponse.getEntry().length; ++i) {
      if (SubjectTestHelper.SUBJ0_ID.equals(vootGetMembersResponse.getEntry()[i].getId())) {
        curPerson = vootGetMembersResponse.getEntry()[i];
      }
    }
    
    Map<String, String[]> attributeValues = new HashMap<String, String[]>();
    attributeValues.put(STEM_NAME + ":" + ATTRIBUTE_NAMES[0], new String[]{ ATTRIBUTE_VALUES[0] });
    validateMemberAttributes(curPerson, attributeValues);
    
    GrouperSession.stopQuietly(grouperSession);
  }
  
  /**
   * Method that logs in with Subject0 and calls the URL:
   * /people/@me/vootTest:group1
   * Note: running this will delete all data in the registry!
   */
  public void testPeopleMeAttribute0Values() {
    createRegistryToTestVOOT();
    
    //start session as logged in user to web service
    //GrouperSession grouperSession = GrouperSession.start(SubjectTestHelper.SUBJ0, true);
    //FIXME: don't know why but with non root user the group attributes are not retrieved
    GrouperSession grouperSession = GrouperSession.startRootSession();
    Object resultObject = callPeopleAPI("@me", GROUP_NAMES[1]);

    //analyze the result
    Subject[] subjects = new Subject[]{ SubjectTestHelper.SUBJ0 };
    String[] roles = new String[]{ "admin" };
    validateMembers(resultObject, 1, null, 0, 1, subjects, roles);
    
    VootGetMembersResponse vootGetMembersResponse = (VootGetMembersResponse) resultObject;
    VootPerson curPerson = null;
    
    for (int i = 0; i < vootGetMembersResponse.getEntry().length; ++i) {
      if (SubjectTestHelper.SUBJ0_ID.equals(vootGetMembersResponse.getEntry()[i].getId())) {
        curPerson = vootGetMembersResponse.getEntry()[i];
      }
    }
    
    Map<String, String[]> attributeValues = null;
    validateMemberAttributes(curPerson, attributeValues);
    
    GrouperSession.stopQuietly(grouperSession);
  }
  
  /**
   * Method that logs in with Subject0 and calls the URL:
   * /people/@me/vootTest:group2
   * Note: running this will delete all data in the registry!
   */
  public void testPeopleMeAttribute2Values() {
    createRegistryToTestVOOT();
    
    //start session as logged in user to web service
    //GrouperSession grouperSession = GrouperSession.start(SubjectTestHelper.SUBJ0, true);
    //FIXME: don't know why but with non root user the group attributes are not retrieved
    GrouperSession grouperSession = GrouperSession.startRootSession();
    Object resultObject = callPeopleAPI("@me", GROUP_NAMES[2]);

    //analyze the result
    Subject[] subjects = new Subject[]{ SubjectTestHelper.SUBJ0 };
    String[] roles = new String[]{ "admin" };
    validateMembers(resultObject, 1, null, 0, 1, subjects, roles);
    
    VootGetMembersResponse vootGetMembersResponse = (VootGetMembersResponse) resultObject;
    VootPerson curPerson = null;
    
    for (int i = 0; i < vootGetMembersResponse.getEntry().length; ++i) {
      if (SubjectTestHelper.SUBJ0_ID.equals(vootGetMembersResponse.getEntry()[i].getId())) {
        curPerson = vootGetMembersResponse.getEntry()[i];
      }
    }
    
    Map<String, String[]> attributeValues = new HashMap<String, String[]>();
    attributeValues.put(STEM_NAME + ":" + ATTRIBUTE_NAMES[0], new String[]{ ATTRIBUTE_VALUES[0] });
    attributeValues.put(STEM_NAME + ":" + ATTRIBUTE_NAMES[1], new String[]{ ATTRIBUTE_VALUES[1] });
    validateMemberAttributes(curPerson, attributeValues);
    
    GrouperSession.stopQuietly(grouperSession);
  }
  
  /**
   * Method that logs in with Subject0 and calls the URL:
   * /group/@me
   * Note: running this will delete all data in the registry!
   */
  public void testGroupMeAttribute1Value() {
    createRegistryToTestVOOT();
    
    //start session as logged in user to web service
    //GrouperSession grouperSession = GrouperSession.start(SubjectTestHelper.SUBJ0, true);
    //FIXME: don't know why but with non root user the group attributes are not retrieved
    GrouperSession grouperSession = GrouperSession.startRootSession();
    Object resultObject = callGroupsAPI("@me");

    //analyze the result
    int groupCount = 5;
    int[] groups = new int[]{ 0, 1, 2, 3, 4 };
    String[] roles = new String[]{ "admin", "admin", "admin", "admin", "admin" };
    validateGroups(resultObject, groupCount, null, 0, groupCount, groups, roles);
    
    VootGetGroupsResponse vootGetGrousResponse = (VootGetGroupsResponse) resultObject;
    
    Map<String, String[]> attributeValues = new HashMap<String, String[]>();
    attributeValues.put(STEM_NAME + ":" + ATTRIBUTE_NAMES[0], new String[]{ ATTRIBUTE_VALUES[0] });
    
    for (int i = 0; i < vootGetGrousResponse.getEntry().length; ++i) {
      if (GROUP_NAMES[3].equals(vootGetGrousResponse.getEntry()[i].getId())) {
        validateGroupAttributes(vootGetGrousResponse.getEntry()[i], attributeValues);
      }
    }
    
    GrouperSession.stopQuietly(grouperSession);
  }
  
  /**
   * Method that logs in with Subject0 and calls the URL:
   * /group/@me
   * Note: running this will delete all data in the registry!
   */
  public void testGroupMeAttribute2Values() {
    createRegistryToTestVOOT();
    
    //start session as logged in user to web service
    //GrouperSession grouperSession = GrouperSession.start(SubjectTestHelper.SUBJ0, true);
    //FIXME: don't know why but with non root user the group attributes are not retrieved
    GrouperSession grouperSession = GrouperSession.startRootSession();
    Object resultObject = callGroupsAPI("@me");

    //analyze the result
    int groupCount = 5;
    int[] groups = new int[]{ 0, 1, 2, 3, 4 };
    String[] roles = new String[]{ "admin", "admin", "admin", "admin", "admin" };
    validateGroups(resultObject, groupCount, null, 0, groupCount, groups, roles);
    
    VootGetGroupsResponse vootGetGrousResponse = (VootGetGroupsResponse) resultObject;
    
    Map<String, String[]> attributeValues = new HashMap<String, String[]>();
    attributeValues.put(STEM_NAME + ":" + ATTRIBUTE_NAMES[0], new String[]{ ATTRIBUTE_VALUES[0] });
    attributeValues.put(STEM_NAME + ":" + ATTRIBUTE_NAMES[1], new String[]{ ATTRIBUTE_VALUES[1] });
    
    for (int i = 0; i < vootGetGrousResponse.getEntry().length; ++i) {
      if (GROUP_NAMES[4].equals(vootGetGrousResponse.getEntry()[i].getId())) {
        validateGroupAttributes(vootGetGrousResponse.getEntry()[i], attributeValues);
      }
    }
    
    GrouperSession.stopQuietly(grouperSession);
  }
  
}
