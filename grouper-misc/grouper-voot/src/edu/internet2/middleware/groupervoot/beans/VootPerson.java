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

package edu.internet2.middleware.groupervoot.beans;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.internet2.middleware.grouper.util.GrouperEmailUtils;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.subject.Subject;

/**
 * VOOT person bean that gets transformed to json.
 * 
 * @author mchyzer
 * @author <andrea.biancini@gmail.com>
 */
public class VootPerson {
  /** The voot membership role (being "manager", "admin" or "member"). */
  private String voot_membership_role;
  
  /** Person id, e.g. jsmith */
  private String id;
  
  /** Display name, e.g. John Smith */
  private String displayName;
  
  /** Email addresses e.g. jsmith@school.edu, johns@company.com */
  private VootEmail[] emails;
  
  /** Additional parameters */
  private VootAttribute[] attributes;
  /**
   * Default constructor. 
   */
  public VootPerson() {
    // Do nothing
  }

  /**
   * Contructor that builds a VOOT person from a Grouper subject. 
   * @param subject the groupser subject.
   */
  public VootPerson(Subject subject) {
    this.id = subject.getId();
    this.displayName = subject.getName();

    String emailAttributeName = GrouperEmailUtils.emailAttributeNameForSource(subject.getSourceId());
    if (!StringUtils.isBlank(emailAttributeName)) {
      Set<String> emails = subject.getAttributeValues(emailAttributeName);

      if (GrouperUtil.length(emails) > 0) {
        // maybe first is blank
        if (GrouperUtil.length(emails) != 1 || !StringUtils.isBlank(emails.iterator().next())) {
          int i = 0;
          this.emails = new VootEmail[emails.size()];
          for (String email : emails) {
            this.emails[i] = new VootEmail();
            this.emails[i].setType(VootEmail.MailTypes.OTHER.toString());
            this.emails[i].setValue(email);
          }
        }
      }
    }
  }
  
  /**
   * Method to check if an object is equal to the current object.
   * @param otherVootPerson the other object to check
   */
  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object otherVootPerson) {
    if (otherVootPerson instanceof VootPerson) {
      VootPerson other = (VootPerson) otherVootPerson;
      if (!other.getVoot_membership_role().equals(voot_membership_role)) {
        return false;
      }
      if (!other.getId().equals(id)) {
        return false;
      }
      if (!other.getDisplayName().equals(displayName)) {
        return false;
      }
      
      List<VootEmail> mailList1 = Arrays.asList(emails);
      List<VootEmail> mailList2 = Arrays.asList(other.getEmails());
      if (!mailList1.containsAll(mailList2) || !mailList2.containsAll(mailList1)) {
        return false;
      }

      //List<VootAttribute> attributeList1 = Arrays.asList(attributes);
      //List<VootAttribute> attributeList2 = Arrays.asList(other.getAttributes());
      //if (!attributeList1.containsAll(attributeList2) || !attributeList2.containsAll(attributeList1)) {
      //  return false;
      //}
      
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Method to generate an hash code for the current object.
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.displayName, this.emails, this.voot_membership_role);
  }

  /**
   * Get the voot membership role (being "manager", "admin" or "member").
   * 
   * @return the voot membership role
   */
  public String getVoot_membership_role() {
    return this.voot_membership_role;
  }

  /**
   * Set the voot membership role (being "manager", "admin" or "member").
   * 
   * @param voot_membership_role1 the voot membership role
   */
  public void setVoot_membership_role(String voot_membership_role1) {
    this.voot_membership_role = voot_membership_role1;
  }
  
  /**
   * Get the person id, e.g. jsmith
   * 
   * @return the person id, e.g. jsmith
   */
  public String getId() {
    return this.id;
  }

  /**
   * Set the person id, e.g. jsmith
   * 
   * @param id1 the the person id, e.g. jsmith
   */
  public void setId(String id1) {
    this.id = id1;
  }

  /**
   * Get the display name, e.g. John Smith
   * 
   * @return the display name, e.g. John Smith
   */
  public String getDisplayName() {
    return this.displayName;
  }

  /**
   * Set the display name, e.g. John Smith
   * 
   * @param displayName1 the display name, e.g. John Smith
   */
  public void setDisplayName(String displayName1) {
    this.displayName = displayName1;
  }

  /**
   * Get email addresses e.g. jsmith@school.edu, johns@company.com
   * 
   * @return the email addresses.
   */
  public VootEmail[] getEmails() {
    return this.emails;
  }

  /**
   * Set email addresses e.g. jsmith@school.edu, johns@company.com
   * 
   * @param emails1 the email addresses.
   */
  public void setEmails(VootEmail[] emails1) {
    this.emails = emails1;
  }
  
  /**
   * Get additional attributes.
   * 
   * @return the attributes.
   */
  public VootAttribute[] getAttributes() {
    return attributes;
  }

  /**
   * Set additional attributes (if any).
   * 
   * @param attributes1 the additional attributes map.
   */
  public void setAttributes(VootAttribute[] attributes1) {
    this.attributes = attributes1;
  }
  
}