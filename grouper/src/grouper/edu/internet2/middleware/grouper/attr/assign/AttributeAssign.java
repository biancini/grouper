/**
 * 
 */
package edu.internet2.middleware.grouper.attr.assign;

import java.sql.Timestamp;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GrouperAPI;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Member;
import edu.internet2.middleware.grouper.Membership;
import edu.internet2.middleware.grouper.MembershipFinder;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.annotations.GrouperIgnoreClone;
import edu.internet2.middleware.grouper.annotations.GrouperIgnoreDbVersion;
import edu.internet2.middleware.grouper.annotations.GrouperIgnoreFieldConstant;
import edu.internet2.middleware.grouper.attr.AttributeDef;
import edu.internet2.middleware.grouper.attr.AttributeDefName;
import edu.internet2.middleware.grouper.attr.finder.AttributeDefFinder;
import edu.internet2.middleware.grouper.attr.finder.AttributeDefNameFinder;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouper.internal.dao.hib3.Hib3GrouperVersioned;
import edu.internet2.middleware.grouper.internal.util.GrouperUuid;
import edu.internet2.middleware.grouper.misc.GrouperDAOFactory;
import edu.internet2.middleware.grouper.misc.GrouperHasContext;
import edu.internet2.middleware.grouper.util.GrouperUtil;


/**
 * definition of an attribute
 * @author mchyzer
 *
 */
@SuppressWarnings("serial")
public class AttributeAssign extends GrouperAPI implements GrouperHasContext, Hib3GrouperVersioned {

  /** logger */
  @SuppressWarnings("unused")
  private static final Log LOG = GrouperUtil.getLog(AttributeAssign.class);

  /** name of the groups attribute def table in the db */
  public static final String TABLE_GROUPER_ATTRIBUTE_ASSIGN = "grouper_attribute_assign";

  /** actions col in db */
  public static final String COLUMN_ACTION = "action";

  /** column */
  public static final String COLUMN_CONTEXT_ID = "context_id";

  /** column */
  public static final String COLUMN_CREATED_ON = "created_on";

  /** column */
  public static final String COLUMN_LAST_UPDATED = "last_updated";

  /** column */
  public static final String COLUMN_NOTES = "notes";

  /** column */
  public static final String COLUMN_ID = "id";

  /** column */
  public static final String COLUMN_ATTRIBUTE_DEF_NAME_ID = "attribute_def_name_id";

  /** column */
  public static final String COLUMN_OWNER_GROUP_ID = "owner_group_id";

  /** column */
  public static final String COLUMN_OWNER_STEM_ID = "owner_stem_id";

  /** column */
  public static final String COLUMN_OWNER_MEMBER_ID = "owner_member_id";

  /** column */
  public static final String COLUMN_OWNER_MEMBERSHIP_ID = "owner_membership_id";

  /** column */
  public static final String COLUMN_OWNER_ATTRIBUTE_ASSIGN_ID = "owner_attribute_assign_id";

  /** column */
  public static final String COLUMN_OWNER_ATTRIBUTE_DEF_ID = "owner_attribute_def_id";

  /** column */
  public static final String COLUMN_ENABLED = "enabled";

  /** column */
  public static final String COLUMN_ENABLED_TIME = "enabled_time";

  /** column */
  public static final String COLUMN_DISABLED_TIME = "disabled_time";

  
  //*****  START GENERATED WITH GenerateFieldConstants.java *****//

  /** constant for field name for: action */
  public static final String FIELD_ACTION = "action";

  /** constant for field name for: attributeNameId */
  public static final String FIELD_ATTRIBUTE_NAME_ID = "attributeNameId";

  /** constant for field name for: contextId */
  public static final String FIELD_CONTEXT_ID = "contextId";

  /** constant for field name for: createdOnDb */
  public static final String FIELD_CREATED_ON_DB = "createdOnDb";

  /** constant for field name for: disabledTimeDb */
  public static final String FIELD_DISABLED_TIME_DB = "disabledTimeDb";

  /** constant for field name for: enabled */
  public static final String FIELD_ENABLED = "enabled";

  /** constant for field name for: enabledTimeDb */
  public static final String FIELD_ENABLED_TIME_DB = "enabledTimeDb";

  /** constant for field name for: id */
  public static final String FIELD_ID = "id";

  /** constant for field name for: lastUpdatedDb */
  public static final String FIELD_LAST_UPDATED_DB = "lastUpdatedDb";

  /** constant for field name for: notes */
  public static final String FIELD_NOTES = "notes";

  /** constant for field name for: ownerAttributeAssignId */
  public static final String FIELD_OWNER_ATTRIBUTE_ASSIGN_ID = "ownerAttributeAssignId";

  /** constant for field name for: ownerAttributeDefId */
  public static final String FIELD_OWNER_ATTRIBUTE_DEF_ID = "ownerAttributeDefId";

  /** constant for field name for: ownerGroupId */
  public static final String FIELD_OWNER_GROUP_ID = "ownerGroupId";

  /** constant for field name for: ownerMemberId */
  public static final String FIELD_OWNER_MEMBER_ID = "ownerMemberId";

  /** constant for field name for: ownerMembershipId */
  public static final String FIELD_OWNER_MEMBERSHIP_ID = "ownerMembershipId";

  /** constant for field name for: ownerStemId */
  public static final String FIELD_OWNER_STEM_ID = "ownerStemId";

  /**
   * fields which are included in db version
   */
  @SuppressWarnings("unused")
  private static final Set<String> DB_VERSION_FIELDS = GrouperUtil.toSet(
      FIELD_ACTION, FIELD_ATTRIBUTE_NAME_ID, FIELD_CONTEXT_ID, FIELD_CREATED_ON_DB, 
      FIELD_DISABLED_TIME_DB, FIELD_ENABLED, FIELD_ENABLED_TIME_DB, FIELD_ID, 
      FIELD_LAST_UPDATED_DB, FIELD_NOTES, FIELD_OWNER_ATTRIBUTE_ASSIGN_ID, FIELD_OWNER_ATTRIBUTE_DEF_ID, 
      FIELD_OWNER_GROUP_ID, FIELD_OWNER_MEMBER_ID, FIELD_OWNER_MEMBERSHIP_ID, FIELD_OWNER_STEM_ID);

  /**
   * fields which are included in clone method
   */
  private static final Set<String> CLONE_FIELDS = GrouperUtil.toSet(
      FIELD_ACTION, FIELD_ATTRIBUTE_NAME_ID, FIELD_CONTEXT_ID, FIELD_CREATED_ON_DB, 
      FIELD_DISABLED_TIME_DB, FIELD_ENABLED, FIELD_ENABLED_TIME_DB, FIELD_HIBERNATE_VERSION_NUMBER, 
      FIELD_ID, FIELD_LAST_UPDATED_DB, FIELD_NOTES, FIELD_OWNER_ATTRIBUTE_ASSIGN_ID, 
      FIELD_OWNER_ATTRIBUTE_DEF_ID, FIELD_OWNER_GROUP_ID, FIELD_OWNER_MEMBER_ID, FIELD_OWNER_MEMBERSHIP_ID, 
      FIELD_OWNER_STEM_ID);

  //*****  END GENERATED WITH GenerateFieldConstants.java *****//

  /**
   * 
   */
  public AttributeAssign() {
    //default
  }
  
  /**
   * create an attribute assign, including a uuid
   * @param ownerStem
   * @param theAction
   * @param attributeDefName
   */
  public AttributeAssign(Stem ownerStem, String theAction, AttributeDefName attributeDefName) {
    
    this.setOwnerStemId(ownerStem.getUuid());
    this.setAction(theAction);
    this.setAttributeDefNameId(attributeDefName.getId());
    this.setId(GrouperUuid.getUuid());

  }

  /**
   * create an attribute assign, including a uuid
   * @param ownerAttributeDef
   * @param theAction
   * @param attributeDefName
   */
  public AttributeAssign(AttributeDef ownerAttributeDef, String theAction, AttributeDefName attributeDefName) {
    
    this.setOwnerAttributeDefId(ownerAttributeDef.getId());
    this.setAction(theAction);
    this.setAttributeDefNameId(attributeDefName.getId());
    this.setId(GrouperUuid.getUuid());

  }

  /**
   * create an attribute assign, including a uuid
   * @param ownerGroup
   * @param theAction
   * @param attributeDefName
   */
  public AttributeAssign(Group ownerGroup, String theAction, AttributeDefName attributeDefName) {
    
    this.setOwnerGroupId(ownerGroup.getUuid());
    this.setAction(theAction);
    this.setAttributeDefNameId(attributeDefName.getId());
    this.setId(GrouperUuid.getUuid());

  }

  /**
   * create an attribute assign, including a uuid.  This is for an effective membership
   * @param ownerGroup
   * @param ownerMember 
   * @param theAction
   * @param attributeDefName
   */
  public AttributeAssign(Group ownerGroup, Member ownerMember, String theAction, AttributeDefName attributeDefName) {
    
    //this must be an immediate, list membership
    Membership membership = MembershipFinder.findImmediateMembership(GrouperSession.staticGrouperSession(), 
        ownerGroup, ownerMember.getSubject(), Group.getDefaultList(), false);

    if (membership == null) {
      throw new RuntimeException("Effective memberships which have attributes must be immediate on the members list: " 
          + ownerGroup + ", " + GrouperUtil.subjectToString(ownerMember.getSubject()));
    }
    
    this.setOwnerGroupId(ownerGroup.getUuid());
    this.setOwnerMemberId(ownerMember.getUuid());
    this.setAction(theAction);
    this.setAttributeDefNameId(attributeDefName.getId());
    this.setId(GrouperUuid.getUuid());

  }

  /**
   * create an attribute assign, including a uuid
   * @param ownerAttributeAssign
   * @param theAction
   * @param attributeDefName
   */
  public AttributeAssign(AttributeAssign ownerAttributeAssign, String theAction, AttributeDefName attributeDefName) {
    
    //cant assign to an assignment of an assignment.
    if (!StringUtils.isBlank(ownerAttributeAssign.getOwnerAttributeAssignId())) {
      throw new RuntimeException("You cants assign an attribute to " +
      		"an assignment of an assignment (only to an assignment of a non-assignment): " 
          + theAction + ", " + attributeDefName.getName());
    }
    
    this.setOwnerAttributeAssignId(ownerAttributeAssign.getId());
    this.setAction(theAction);
    this.setAttributeDefNameId(attributeDefName.getId());
    this.setId(GrouperUuid.getUuid());

  }

  /**
   * create an attribute assign, including a uuid
   * @param ownerMembership
   * @param theAction
   * @param attributeDefName
   */
  public AttributeAssign(Membership ownerMembership, String theAction, AttributeDefName attributeDefName) {
    
    //this must be an immediate, list membership
    if (!ownerMembership.isImmediate()) {
      throw new RuntimeException("Memberships which have attributes must be immediate: " 
          + ownerMembership.getType() + ", " + ownerMembership.getUuid());
    }
    
    if (!Group.getDefaultList().equals(ownerMembership.getList())) {
      throw new RuntimeException("Memberships which have attributes must be list type: " 
          + ownerMembership.getList() + ", " + ownerMembership.getUuid());
      
    }
    
    this.setOwnerMembershipId(ownerMembership.getUuid());
    this.setAction(theAction);
    this.setAttributeDefNameId(attributeDefName.getId());
    this.setId(GrouperUuid.getUuid());

  }

  /**
   * create an attribute assign, including a uuid
   * @param ownerMember
   * @param theAction
   * @param attributeDefName
   */
  public AttributeAssign(Member ownerMember, String theAction, AttributeDefName attributeDefName) {
    
    this.setOwnerMemberId(ownerMember.getUuid());
    this.setAction(theAction);
    this.setAttributeDefNameId(attributeDefName.getId());
    this.setId(GrouperUuid.getUuid());

  }

  /**
   * save or update this object
   */
  public void saveOrUpdate() {
    GrouperDAOFactory.getFactory().getAttributeAssign().saveOrUpdate(this);
  }
  
  /**
   * delete this object
   */
  public void delete() {
    GrouperDAOFactory.getFactory().getAttributeAssign().delete(this);
  }
  
  /**
   * deep clone the fields in this object
   */
  @Override
  public AttributeAssign clone() {
    return GrouperUtil.clone(this, CLONE_FIELDS);
  }

  /** attribute name in this assignment */
  private String attributeDefNameId;

  /** if this is an attribute assign attribute, this is the foreign key */
  private String ownerAttributeAssignId;
  
  /** if this is an attribute def attribute, this is the foreign key */
  private String ownerAttributeDefId;
  
  /** if this is a group attribute, this is the foreign key */
  private String ownerGroupId;
  
  /** if this is a member attribute, this is the foreign key */
  private String ownerMemberId;
  
  /** if this is a membership attribute, this is the foreign key */
  private String ownerMembershipId;
  
  /** if this is a stem attribute, this is the foreign key */
  private String ownerStemId;
  
  /** id of this attribute def */
  private String id;

  /** context id of the transaction */
  private String contextId;

  /**
   * time in millis when this attribute was last modified
   */
  private Long lastUpdatedDb;

  /**
   * time in millis when this attribute was created
   */
  private Long createdOnDb;

  /**
   * notes about this assignment, free-form text
   */
  private String notes;

  /**
   * action for this assignment (e.g. assign).  Generally this will be AttributeDef.ACTION_DEFAULT.
   * action must exist in AttributeDef.actions
   */
  private String action;
  
  /**
   * true or false for if this assignment is enabled (e.g. might have expired) 
   */
  private boolean enabled = true;
  
  /**
   * if there is a date here, and it is in the future, this assignment is disabled
   * until that time
   */
  private Long enabledTimeDb;
  
  /**
   * if there is a date here, and it is in the past, this assignment is disabled
   */
  private Long disabledTimeDb;
  
  /**
   * action for this assignment (e.g. assign).  Generally this will be AttributeDef.ACTION_DEFAULT.
   * action must exist in AttributeDef.actions
   * @return the action
   */
  public String getAction() {
    return this.action;
  }

  /** cache the attribute def name */
  @GrouperIgnoreClone @GrouperIgnoreDbVersion @GrouperIgnoreFieldConstant
  private AttributeDefName attributeDefName;
  
  /**
   * 
   * @return attributeDefName
   */
  public AttributeDefName getAttributeDefName() {
    if (this.attributeDefName == null ) {
      this.attributeDefName = AttributeDefNameFinder.findById(this.attributeDefNameId, true);
    }
    return this.attributeDefName;
  }
  
  /** cache the attribute def of this attribute def name */
  @GrouperIgnoreClone @GrouperIgnoreDbVersion @GrouperIgnoreFieldConstant
  private AttributeDef attributeDef;
  
  /**
   * 
   * @return attributeDef
   */
  public AttributeDef getAttributeDef() {
    if (this.attributeDef == null ) {
      this.attributeDef = AttributeDefFinder.findByAttributeDefNameId(this.attributeDefNameId, true);
    }
    return this.attributeDef;
  }
  
  /** */
  @GrouperIgnoreClone @GrouperIgnoreDbVersion @GrouperIgnoreFieldConstant
  private AttributeAssignAttrAssignDelegate attributeAssignAttrAssignDelegate;
  
  /**
   * 
   * @return the delegate
   */
  public AttributeAssignAttrAssignDelegate getAttributeDelegate() {
    if (this.attributeAssignAttrAssignDelegate == null) {
      this.attributeAssignAttrAssignDelegate = new AttributeAssignAttrAssignDelegate(this);
    }
    return this.attributeAssignAttrAssignDelegate;
  }

  
  /**
   * comma separated list of actions for this attribute.  at least there needs to be one.
   * default is "assign" actions must contain only alphanumeric or underscore, case sensitive
   * e.g. read,write,admin
   * @param theAction
   */
  public void setAction(String theAction) {
    this.action = theAction;
  }

  /**
   * context id of the transaction
   * @return context id
   */
  public String getContextId() {
    return this.contextId;
  }

  /**
   * context id of the transaction
   * @param contextId1
   */
  public void setContextId(String contextId1) {
    this.contextId = contextId1;
  }

  /**
   * id of this attribute def
   * @return id
   */
  public String getId() {
    return id;
  }

  /**
   * id of this attribute def
   * @param id1
   */
  public void setId(String id1) {
    this.id = id1;
  }

  
  /**
   * when last updated
   * @return timestamp
   */
  public Timestamp getLastUpdated() {
    return this.lastUpdatedDb == null ? null : new Timestamp(this.lastUpdatedDb);
  }

  /**
   * when last updated
   * @return timestamp
   */
  public Long getLastUpdatedDb() {
    return this.lastUpdatedDb;
  }

  /**
   * when last updated
   * @param lastUpdated1
   */
  public void setLastUpdated(Timestamp lastUpdated1) {
    this.lastUpdatedDb = lastUpdated1 == null ? null : lastUpdated1.getTime();
  }

  /**
   * when last updated
   * @param lastUpdated1
   */
  public void setLastUpdatedDb(Long lastUpdated1) {
    this.lastUpdatedDb = lastUpdated1;
  }
  
  /**
   * when created
   * @return timestamp
   */
  public Timestamp getCreatedOn() {
    return this.createdOnDb == null ? null : new Timestamp(this.createdOnDb);
  }

  /**
   * when created
   * @return timestamp
   */
  public Long getCreatedOnDb() {
    return this.createdOnDb;
  }

  /**
   * when created
   * @param createdOn1
   */
  public void setCreatedOn(Timestamp createdOn1) {
    this.createdOnDb = createdOn1 == null ? null : createdOn1.getTime();
  }

  /**
   * when created
   * @param createdOn1
   */
  public void setCreatedOnDb(Long createdOn1) {
    this.createdOnDb = createdOn1;
  }

  /**
   * notes about this assignment, free-form text
   * @return the notes
   */
  public String getNotes() {
    return this.notes;
  }

  /**
   * notes about this assignment, free-form text
   * @param notes1
   */
  public void setNotes(String notes1) {
    this.notes = notes1;
  }

  
  /**
   * attribute name in this assignment
   * @return the attributeNameId
   */
  public String getAttributeDefNameId() {
    return this.attributeDefNameId;
  }

  
  /**
   * attribute name in this assignment
   * @param attributeNameId1 the attributeNameId to set
   */
  public void setAttributeDefNameId(String attributeNameId1) {
    this.attributeDefNameId = attributeNameId1;
    //reset cached object
    this.attributeDefName = null;
    this.attributeDef = null;
  }

  
  /**
   * if this is an attribute assign attribute, this is the foreign key
   * @return the ownerAttributeAssignId
   */
  public String getOwnerAttributeAssignId() {
    return this.ownerAttributeAssignId;
  }

  
  /**
   * if this is an attribute assign attribute, this is the foreign key
   * @param ownerAttributeAssignId1 the ownerAttributeAssignId to set
   */
  public void setOwnerAttributeAssignId(String ownerAttributeAssignId1) {
    this.ownerAttributeAssignId = ownerAttributeAssignId1;
  }

  
  /**
   * if this is an attribute def attribute, this is the foreign key
   * @return the ownerAttributeDefId
   */
  public String getOwnerAttributeDefId() {
    return this.ownerAttributeDefId;
  }

  
  /**
   * if this is an attribute def attribute, this is the foreign key
   * @param ownerAttributeDefId1 the ownerAttributeDefId to set
   */
  public void setOwnerAttributeDefId(String ownerAttributeDefId1) {
    this.ownerAttributeDefId = ownerAttributeDefId1;
  }

  
  /**
   * if this is a group attribute, this is the foreign key
   * @return the ownerAttributeGroupId
   */
  public String getOwnerGroupId() {
    return this.ownerGroupId;
  }

  
  /**
   * if this is a group attribute, this is the foreign key
   * @param ownerAttributeGroupId1 the ownerAttributeGroupId to set
   */
  public void setOwnerGroupId(String ownerAttributeGroupId1) {
    this.ownerGroupId = ownerAttributeGroupId1;
  }

  
  /**
   * if this is a member attribute, this is the foreign key
   * @return the ownerAttributeMemberId
   */
  public String getOwnerMemberId() {
    return this.ownerMemberId;
  }

  
  /**
   * if this is a member attribute, this is the foreign key
   * @param ownerAttributeMemberId1 the ownerAttributeMemberId to set
   */
  public void setOwnerMemberId(String ownerAttributeMemberId1) {
    this.ownerMemberId = ownerAttributeMemberId1;
  }

  
  /**
   * if this is a membership attribute, this is the foreign key
   * @return the ownerAttributeMembershipId
   */
  public String getOwnerMembershipId() {
    return this.ownerMembershipId;
  }

  
  /**
   * if this is a membership attribute, this is the foreign key
   * @param ownerAttributeMembershipId1 the ownerAttributeMembershipId to set
   */
  public void setOwnerMembershipId(String ownerAttributeMembershipId1) {
    this.ownerMembershipId = ownerAttributeMembershipId1;
  }

  
  /**
   * if this is a stem attribute, this is the foreign key
   * @return the ownerAttributeStemId
   */
  public String getOwnerStemId() {
    return this.ownerStemId;
  }

  
  /**
   * if this is a stem attribute, this is the foreign key
   * @param ownerAttributeStemId1 the ownerAttributeStemId to set
   */
  public void setOwnerStemId(String ownerAttributeStemId1) {
    this.ownerStemId = ownerAttributeStemId1;
  }

  
  /**
   * true or false for if this assignment is enabled (e.g. might have expired) 
   * @return the enabled
   */
  public boolean isEnabled() {
    //currently this is based on timestamp
    long now = System.currentTimeMillis();
    if (this.enabledTimeDb != null && this.enabledTimeDb > now) {
      return false;
    }
    if (this.disabledTimeDb != null && this.disabledTimeDb < now) {
      return false;
    }
    return true;
  }
  
  /**
   * true or false for if this assignment is enabled (e.g. might have expired) 
   * @param enabled1 the enabled to set
   */
  public void setEnabled(boolean enabled1) {
    this.enabled = enabled1;
  }

  /**
   * true or false for if this assignment is enabled (e.g. might have expired) 
   * @return the enabled
   */
  public String getEnabledDb() {
    return this.enabled ? "T" : "F";
  }

  
  /**
   * true or false for if this assignment is enabled (e.g. might have expired) 
   * @param enabled1 the enabled to set
   */
  public void setEnabledDb(String enabled1) {
    this.enabled = GrouperUtil.booleanValue(enabled1);
  }

  
  /**
   * if there is a date here, and it is in the future, this assignment is disabled
   * until that time
   * @return the enabledTimeDb
   */
  public Long getEnabledTimeDb() {
    return this.enabledTimeDb;
  }

  
  /**
   * if there is a date here, and it is in the future, this assignment is disabled
   * until that time
   * @param enabledTimeDb1 the enabledTimeDb to set
   */
  public void setEnabledTimeDb(Long enabledTimeDb1) {
    this.enabledTimeDb = enabledTimeDb1;
  }

  
  /**
   * if there is a date here, and it is in the past, this assignment is disabled
   * @return the disabledTimeDb
   */
  public Long getDisabledTimeDb() {
    return this.disabledTimeDb;
  }

  
  /**
   * if there is a date here, and it is in the past, this assignment is disabled
   * @param disabledTimeDb1 the disabledTimeDb to set
   */
  public void setDisabledTimeDb(Long disabledTimeDb1) {
    this.disabledTimeDb = disabledTimeDb1;
  }

  /**
   * if there is a date here, and it is in the future, this assignment is disabled
   * until that time
   * @return the enabledTimeDb
   */
  public Timestamp getEnabledTime() {
    return this.enabledTimeDb == null ? null : new Timestamp(this.enabledTimeDb);
  }

  
  /**
   * if there is a date here, and it is in the future, this assignment is disabled
   * until that time
   * @param enabledTimeDb1 the enabledTimeDb to set
   */
  public void setEnabledTime(Timestamp enabledTimeDb1) {
    this.enabledTimeDb = enabledTimeDb1 == null ? null : enabledTimeDb1.getTime();
  }

  
  /**
   * if there is a date here, and it is in the past, this assignment is disabled
   * @return the disabledTimeDb
   */
  public Timestamp getDisabledTime() {
    return this.disabledTimeDb == null ? null : new Timestamp(this.disabledTimeDb);
  }

  
  /**
   * if there is a date here, and it is in the past, this assignment is disabled
   * @param disabledTimeDb1 the disabledTimeDb to set
   */
  public void setDisabledTime(Timestamp disabledTimeDb1) {
    this.disabledTimeDb = disabledTimeDb1 == null ? null : disabledTimeDb1.getTime();
  }

  /**
   * @see edu.internet2.middleware.grouper.GrouperAPI#onPreSave(edu.internet2.middleware.grouper.hibernate.HibernateSession)
   */
  @Override
  public void onPreSave(HibernateSession hibernateSession) {
    super.onPreSave(hibernateSession);
    long now = System.currentTimeMillis();
    this.setCreatedOnDb(now);
    this.setLastUpdatedDb(now);
  }

  /**
   * @see edu.internet2.middleware.grouper.GrouperAPI#onPreUpdate(edu.internet2.middleware.grouper.hibernate.HibernateSession)
   */
  @Override
  public void onPreUpdate(HibernateSession hibernateSession) {
    super.onPreUpdate(hibernateSession);
    this.setLastUpdatedDb(System.currentTimeMillis());
  }
}
