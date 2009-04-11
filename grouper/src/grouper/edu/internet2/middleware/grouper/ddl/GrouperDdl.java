/*
 * @author mchyzer
 * $Id: GrouperDdl.java,v 1.24.2.2 2009-01-31 14:35:16 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.ddl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import org.apache.commons.lang.StringUtils;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;

import edu.internet2.middleware.grouper.Attribute;
import edu.internet2.middleware.grouper.Composite;
import edu.internet2.middleware.grouper.Field;
import edu.internet2.middleware.grouper.FieldType;
import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupType;
import edu.internet2.middleware.grouper.GroupTypeTuple;
import edu.internet2.middleware.grouper.Member;
import edu.internet2.middleware.grouper.Membership;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.hibernate.GrouperTransactionType;
import edu.internet2.middleware.grouper.hibernate.HibernateHandler;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouper.internal.dao.GrouperDAOException;
import edu.internet2.middleware.grouper.util.GrouperUtil;



/**
 * ddl versions and stuff for grouper.  All ddl classes must have a currentVersion method that
 * returns the current version
 */
public enum GrouperDdl implements DdlVersionable {

  /**
   * delete create source and modify source cols if they exist
   */
  V13 {
    
    /**
     * 
     * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#updateVersionFromPrevious(org.apache.ddlutils.model.Database, DdlVersionBean)
     */
    @Override
    public void updateVersionFromPrevious(Database database, 
        DdlVersionBean ddlVersionBean) {

      Table stemsTable = GrouperDdlUtils.ddlutilsFindTable(database, 
          Stem.TABLE_GROUPER_STEMS);

      GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, stemsTable.getName(), 
          "stem_name_idx", true, "name");

    }
  },

  /**
   * delete create source and modify source cols if they exist
   */
  V12 {
    
    /**
     * 
     * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#updateVersionFromPrevious(org.apache.ddlutils.model.Database, DdlVersionBean)
     */
    @Override
    public void updateVersionFromPrevious(Database database, 
        DdlVersionBean ddlVersionBean) {

      //only drop cols if there are there, and all of them (which means the conversion probably happened, and they
      //havent been dropped yet)
      if (GrouperDdlUtils.ddlutilsFindColumn(database, Group.TABLE_GROUPER_GROUPS, "CREATE_SOURCE", false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Group.TABLE_GROUPER_GROUPS, "CREATE_SOURCE", ddlVersionBean);
      }
      if (GrouperDdlUtils.ddlutilsFindColumn(database, Group.TABLE_GROUPER_GROUPS, "MODIFY_SOURCE", false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Group.TABLE_GROUPER_GROUPS, "MODIFY_SOURCE", ddlVersionBean);
      }
      if (GrouperDdlUtils.ddlutilsFindColumn(database, Stem.TABLE_GROUPER_STEMS, "CREATE_SOURCE", false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Stem.TABLE_GROUPER_STEMS, "CREATE_SOURCE", ddlVersionBean);
      }
      if (GrouperDdlUtils.ddlutilsFindColumn(database, Stem.TABLE_GROUPER_STEMS, "MODIFY_SOURCE", false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Stem.TABLE_GROUPER_STEMS, "MODIFY_SOURCE", ddlVersionBean);
      }
    }
  },

  /**
   * delete backup cols if configured to and if exist
   */
  V11 {
    
    /**
     * 
     * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#updateVersionFromPrevious(org.apache.ddlutils.model.Database, DdlVersionBean)
     */
    @Override
    public void updateVersionFromPrevious(Database database, 
        DdlVersionBean ddlVersionBean) {

      //if not configured to drop, then leave alone
      if (!GrouperConfig.getPropertyBoolean("ddlutils.dropBackupFieldNameTypeCols", false)) {
        return;
      }

      //only drop cols if there are there, and all of them (which means the conversion probably happened, and they
      //havent been dropped yet)
      if (GrouperDdlUtils.ddlutilsFindColumn(database, Attribute.TABLE_GROUPER_ATTRIBUTES, Attribute.COLUMN_OLD_FIELD_NAME, false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Attribute.TABLE_GROUPER_ATTRIBUTES, Attribute.COLUMN_OLD_FIELD_NAME, ddlVersionBean);
      }
      if (GrouperDdlUtils.ddlutilsFindColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_LIST_NAME, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_LIST_TYPE, false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_LIST_NAME, ddlVersionBean);
        GrouperDdlUtils.ddlutilsDropColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_LIST_TYPE, ddlVersionBean);
      }
    }
  },

  /**
   * delete field name/type if in the right situation
   */
  V10 {
    
    /**
     * 
     * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#updateVersionFromPrevious(org.apache.ddlutils.model.Database, DdlVersionBean)
     */
    @Override
    public void updateVersionFromPrevious(Database database, 
        DdlVersionBean ddlVersionBean) {

      Table attributesTable = GrouperDdlUtils.ddlutilsFindTable(database, 
          Attribute.TABLE_GROUPER_ATTRIBUTES);

      addAttributeFieldIndexes(database, ddlVersionBean, attributesTable);

      Table membershipsTable = GrouperDdlUtils.ddlutilsFindTable(database, 
          Membership.TABLE_GROUPER_MEMBERSHIPS);

      addMembershipFieldIndexes(database, membershipsTable);

      //only drop cols if there are there, and all of them (which means the conversion probably happened, and they
      //havent been dropped yet)
      if (GrouperDdlUtils.ddlutilsFindColumn(database, Attribute.TABLE_GROUPER_ATTRIBUTES, Attribute.COLUMN_FIELD_NAME, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Attribute.TABLE_GROUPER_ATTRIBUTES, Attribute.COLUMN_OLD_FIELD_NAME, false) != null) {
        
        GrouperDdlUtils.ddlutilsDropColumn(database, Attribute.TABLE_GROUPER_ATTRIBUTES, Attribute.COLUMN_FIELD_NAME, ddlVersionBean);
      }
      if (GrouperDdlUtils.ddlutilsFindColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_LIST_NAME, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_LIST_TYPE, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_LIST_NAME, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_LIST_TYPE, false) != null) {
        
        GrouperDdlUtils.ddlutilsDropColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_LIST_NAME, ddlVersionBean);
        GrouperDdlUtils.ddlutilsDropColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_LIST_TYPE, ddlVersionBean);
      }
    }
  },
  
  /**
   * update the fields id as a foreign key, keep backups of old data
   */
  V9 {
    
    /**
     * 
     * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#updateVersionFromPrevious(org.apache.ddlutils.model.Database, DdlVersionBean)
     */
    @Override
    public void updateVersionFromPrevious(Database database, 
        DdlVersionBean ddlVersionBean) {

      boolean isDestinationVersion = ddlVersionBean.isDestinationVersion();

      final StringBuilder additionalScripts = ddlVersionBean.getAdditionalScripts();

      boolean needsAttributeFieldIdConversion = needsAttributeFieldIdConversion(database);
      boolean needsMembershipFieldIdConversion = needsMembershipFieldIdConversion(database);
      
      if (needsAttributeFieldIdConversion) {
        
        Table attributesTable = GrouperDdlUtils.ddlutilsFindTable(database, 
            Attribute.TABLE_GROUPER_ATTRIBUTES);
        
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(attributesTable, Attribute.COLUMN_OLD_FIELD_NAME,  
            Types.VARCHAR, "32", false, false);
        
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(attributesTable, Attribute.COLUMN_FIELD_ID, 
            Types.VARCHAR, "128", false, false);
      }

      if (needsMembershipFieldIdConversion) {
        
        Table membershipsTable = GrouperDdlUtils.ddlutilsFindTable(database, 
            Membership.TABLE_GROUPER_MEMBERSHIPS);

        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, Membership.COLUMN_OLD_LIST_NAME,  
            Types.VARCHAR, "32", false, false);
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, Membership.COLUMN_OLD_LIST_TYPE,  
            Types.VARCHAR, "32", false, false);

        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, Membership.COLUMN_FIELD_ID, 
            Types.VARCHAR, "128", false, false);
        
      }
      
      //dont put scripts here if this isnt the right time (at this stage, not building toward a different one)
      //also we need to need the conversion in attributes or memberships
      if (isDestinationVersion && (needsAttributeFieldIdConversion || needsMembershipFieldIdConversion)) {
        
        HibernateSession.callbackHibernateSession(GrouperTransactionType.READONLY_OR_USE_EXISTING, new HibernateHandler() {

          @SuppressWarnings("deprecation")
          public Object callback(HibernateSession hibernateSession)
              throws GrouperDAOException {
            
            Connection connection = hibernateSession.getSession().connection();
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            
            //we dont know where the user is in the upgrade steps, so see if the uuid is still there...
            boolean uuidStillExists = true;
            try {
              statement = connection.prepareStatement("select field_uuid from grouper_fields");
              resultSet = statement.executeQuery();
              while (resultSet.next()) {
                //just testing
              }
             
            } catch (Exception e) {
              uuidStillExists = false;
            } finally {
              GrouperUtil.closeQuietly(resultSet);
              GrouperUtil.closeQuietly(statement);
            }
            String idCol = uuidStillExists ? "field_uuid" : "id";
            String query = "select " + idCol + ", name, type, id from grouper_fields";
            try {
              statement = connection.prepareStatement(query);
              resultSet = statement.executeQuery();
              while (resultSet.next()) {
                
                String uuid = resultSet.getString(1);
                String name = resultSet.getString(2);
                String type = resultSet.getString(3);
                String id = resultSet.getString(4);
                
                //use id if uuid is blank
                uuid = GrouperUtil.defaultIfBlank(uuid, id);
                
                if (StringUtils.isBlank(uuid)) {
                  throw new RuntimeException("Something is wrong, why is uuid blank??? '" + name + "', '" + type + "', '" + query + "'");
                }
                
                //attributes work on the attributes table, and non-attributes work on the memberships table
                if (FieldType.ATTRIBUTE.getType().equals(type)) {
                  
                  //update records, move the name to the id, commit inline so that the db undo required is not too huge
                  additionalScripts.append("update grouper_attributes set old_field_name = field_name, " +
                      "field_id = '" + uuid + "' where field_name = '" + name + "';\ncommit;\n");

                } else {
                  
                  //update records, move the name to the id, commit inline so that the db undo required is not too huge
                  additionalScripts.append("update grouper_memberships set old_list_name = list_name, old_list_type = list_type, " +
                      "field_id = '" + uuid + "' " +
                          "where list_name = '" + name + "' and list_type = '" + type + "';\ncommit;\n");
                  
                }
                
              }
            } catch (Exception e) {
              throw new RuntimeException("Problem with running query: " + query, e);
            } finally {
              GrouperUtil.closeQuietly(resultSet);
              GrouperUtil.closeQuietly(statement);
            }
            
            return null;
          }
          
        });
        
        //CH 20080823 THIS DIDNT WORK SINCE THE MAPPING DOESNT EXIST ANYMORE!!!!
        //loop through all fields:
        //List<Field> fields = HibernateSession.byCriteriaStatic().list(Field.class, null);
        //
        //for (Field field : fields) {
        //  
        //  
        //}
      }

      
    }
  },
  
  /**
   * drop grouper_sessions table if exists
   */
  V8 {
    
    /**
     * 
     * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#updateVersionFromPrevious(org.apache.ddlutils.model.Database, DdlVersionBean)
     */
    @Override
    public void updateVersionFromPrevious(Database database, 
        DdlVersionBean ddlVersionBean) {

      Table grouperSessionsTable = database.findTable("grouper_sessions");
      if (grouperSessionsTable != null) {
        database.removeTable(grouperSessionsTable);
      }
      
    }
  },
  
  /**
   * delete backup cols if configured to and is exist
   */
  V7 {
    
    /**
     * 
     * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#updateVersionFromPrevious(org.apache.ddlutils.model.Database, DdlVersionBean)
     */
    @Override
    public void updateVersionFromPrevious(Database database, 
        DdlVersionBean ddlVersionBean) {

      //if not configured to drop, then leave alone
      if (!GrouperConfig.getPropertyBoolean("ddlutils.dropBackupUuidCols", false)) {
        return;
      }

      //only drop cols if there are there, and all of them (which means the conversion probably happened, and they
      //havent been dropped yet)
      if (GrouperDdlUtils.ddlutilsFindColumn(database, GroupType.TABLE_GROUPER_TYPES, GroupType.COLUMN_OLD_TYPE_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, GroupType.TABLE_GROUPER_TYPES, GroupType.COLUMN_OLD_ID, false) != null) {
        
        GrouperDdlUtils.ddlutilsDropColumn(database, GroupType.TABLE_GROUPER_TYPES, GroupType.COLUMN_OLD_TYPE_UUID, ddlVersionBean);
        GrouperDdlUtils.ddlutilsDropColumn(database, GroupType.TABLE_GROUPER_TYPES, GroupType.COLUMN_OLD_ID, ddlVersionBean);
      }
      
      if (GrouperDdlUtils.ddlutilsFindColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_MEMBERSHIP_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_ID, false) != null) {
        
        GrouperDdlUtils.ddlutilsDropColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_MEMBERSHIP_UUID, ddlVersionBean);
        GrouperDdlUtils.ddlutilsDropColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_ID, ddlVersionBean);
      }
      
      if (GrouperDdlUtils.ddlutilsFindColumn(database, Member.TABLE_GROUPER_MEMBERS, Member.COLUMN_OLD_MEMBER_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Member.TABLE_GROUPER_MEMBERS, Member.COLUMN_OLD_ID, false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Member.TABLE_GROUPER_MEMBERS, Member.COLUMN_OLD_MEMBER_UUID, ddlVersionBean);
        GrouperDdlUtils.ddlutilsDropColumn(database, Member.TABLE_GROUPER_MEMBERS, Member.COLUMN_OLD_ID, ddlVersionBean);
      }

      if (GrouperDdlUtils.ddlutilsFindColumn(database, Group.TABLE_GROUPER_GROUPS, Group.COLUMN_OLD_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Group.TABLE_GROUPER_GROUPS, Group.COLUMN_OLD_ID, false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Group.TABLE_GROUPER_GROUPS, Group.COLUMN_OLD_UUID, ddlVersionBean);
        GrouperDdlUtils.ddlutilsDropColumn(database, Group.TABLE_GROUPER_GROUPS, Group.COLUMN_OLD_ID, ddlVersionBean);
      }

      if (GrouperDdlUtils.ddlutilsFindColumn(database, Field.TABLE_GROUPER_FIELDS, Field.COLUMN_OLD_FIELD_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Field.TABLE_GROUPER_FIELDS, Field.COLUMN_OLD_ID, false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Field.TABLE_GROUPER_FIELDS, Field.COLUMN_OLD_FIELD_UUID, ddlVersionBean);
        GrouperDdlUtils.ddlutilsDropColumn(database, Field.TABLE_GROUPER_FIELDS, Field.COLUMN_OLD_ID, ddlVersionBean);
        
      }

      if (GrouperDdlUtils.ddlutilsFindColumn(database, Composite.TABLE_GROUPER_COMPOSITES, Composite.COLUMN_OLD_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Composite.TABLE_GROUPER_COMPOSITES, Composite.COLUMN_OLD_ID, false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Composite.TABLE_GROUPER_COMPOSITES, Composite.COLUMN_OLD_UUID, ddlVersionBean);
        GrouperDdlUtils.ddlutilsDropColumn(database, Composite.TABLE_GROUPER_COMPOSITES, Composite.COLUMN_OLD_ID, ddlVersionBean);
        
      }

      if (GrouperDdlUtils.ddlutilsFindColumn(database, Stem.TABLE_GROUPER_STEMS, Stem.COLUMN_OLD_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Stem.TABLE_GROUPER_STEMS, Stem.COLUMN_OLD_ID, false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Stem.TABLE_GROUPER_STEMS, Stem.COLUMN_OLD_UUID, ddlVersionBean);
        GrouperDdlUtils.ddlutilsDropColumn(database, Stem.TABLE_GROUPER_STEMS, Stem.COLUMN_OLD_ID, ddlVersionBean);
          
      }

      
    }
  },
  
  /**
   * delete uuid if in right situation
   */
  V6 {
    
    /**
     * 
     * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#updateVersionFromPrevious(org.apache.ddlutils.model.Database, DdlVersionBean)
     */
    @Override
    public void updateVersionFromPrevious(Database database, 
        DdlVersionBean ddlVersionBean) {

      //only drop cols if there are there, and all of them (which means the conversion probably happened, and they
      //havent been dropped yet)
      if (GrouperDdlUtils.ddlutilsFindColumn(database, GroupType.TABLE_GROUPER_TYPES, GroupType.COLUMN_TYPE_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, GroupType.TABLE_GROUPER_TYPES, GroupType.COLUMN_OLD_TYPE_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, GroupType.TABLE_GROUPER_TYPES, GroupType.COLUMN_OLD_ID, false) != null) {
        
        GrouperDdlUtils.ddlutilsDropColumn(database, GroupType.TABLE_GROUPER_TYPES, GroupType.COLUMN_TYPE_UUID, ddlVersionBean);
      }
      
      if (GrouperDdlUtils.ddlutilsFindColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_MEMBERSHIP_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_MEMBERSHIP_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_ID, false) != null) {
        
        GrouperDdlUtils.ddlutilsDropColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_MEMBERSHIP_UUID, ddlVersionBean);
      }
      
      if (GrouperDdlUtils.ddlutilsFindColumn(database, Member.TABLE_GROUPER_MEMBERS, Member.COLUMN_MEMBER_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Member.TABLE_GROUPER_MEMBERS, Member.COLUMN_OLD_MEMBER_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Member.TABLE_GROUPER_MEMBERS, Member.COLUMN_OLD_ID, false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Member.TABLE_GROUPER_MEMBERS, Member.COLUMN_MEMBER_UUID, ddlVersionBean);
      }

      if (GrouperDdlUtils.ddlutilsFindColumn(database, Group.TABLE_GROUPER_GROUPS, Group.COLUMN_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Group.TABLE_GROUPER_GROUPS, Group.COLUMN_OLD_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Group.TABLE_GROUPER_GROUPS, Group.COLUMN_OLD_ID, false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Group.TABLE_GROUPER_GROUPS, Group.COLUMN_UUID, ddlVersionBean);
      }

      if (GrouperDdlUtils.ddlutilsFindColumn(database, Field.TABLE_GROUPER_FIELDS, Field.COLUMN_FIELD_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Field.TABLE_GROUPER_FIELDS, Field.COLUMN_OLD_FIELD_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Field.TABLE_GROUPER_FIELDS, Field.COLUMN_OLD_ID, false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Field.TABLE_GROUPER_FIELDS, Field.COLUMN_FIELD_UUID, ddlVersionBean);
        
      }

      if (GrouperDdlUtils.ddlutilsFindColumn(database, Composite.TABLE_GROUPER_COMPOSITES, Composite.COLUMN_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Composite.TABLE_GROUPER_COMPOSITES, Composite.COLUMN_OLD_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Composite.TABLE_GROUPER_COMPOSITES, Composite.COLUMN_OLD_ID, false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Composite.TABLE_GROUPER_COMPOSITES, Composite.COLUMN_UUID, ddlVersionBean);
        
      }

      if (GrouperDdlUtils.ddlutilsFindColumn(database, Stem.TABLE_GROUPER_STEMS, Stem.COLUMN_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Stem.TABLE_GROUPER_STEMS, Stem.COLUMN_OLD_UUID, false) != null
          && GrouperDdlUtils.ddlutilsFindColumn(database, Stem.TABLE_GROUPER_STEMS, Stem.COLUMN_OLD_ID, false) != null) {
        GrouperDdlUtils.ddlutilsDropColumn(database, Stem.TABLE_GROUPER_STEMS, Stem.COLUMN_UUID, ddlVersionBean);
          
      }

      
    }
  },
  
  /**
   * convert uuid/id to just id
   */
  V5 {
    
    /**
     * 
     * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#updateVersionFromPrevious(org.apache.ddlutils.model.Database, DdlVersionBean)
     */
    @Override
    public void updateVersionFromPrevious(Database database, 
        DdlVersionBean ddlVersionBean) {
      
      boolean isDestinationVersion = ddlVersionBean.isDestinationVersion();
      
      StringBuilder additionalScripts = ddlVersionBean.getAdditionalScripts();
      
      GrouperDdlUtils.ddlutilsDropIndexes(GrouperDdlUtils.ddlutilsFindTable(database, 
          Composite.TABLE_GROUPER_COMPOSITES), Composite.COLUMN_UUID);
      //we need conversion if there is a uuid col, and not an old_uuid col or old_id col
      if (needsCompositeIdConversion(database)) {

        Table compositesTable = GrouperDdlUtils.ddlutilsFindTable(database, 
            Composite.TABLE_GROUPER_COMPOSITES);

        GrouperDdlUtils.ddlutilsFindOrCreateColumn(compositesTable, Composite.COLUMN_OLD_ID, Types.VARCHAR, "128", false, false);
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(compositesTable, Composite.COLUMN_OLD_UUID, Types.VARCHAR, "128", false, false);
        if (isDestinationVersion) {

          //update records, move the uuid to the id
          additionalScripts.append("update grouper_composites set old_id = id, id = uuid, old_uuid = uuid, uuid = ' ' where uuid != ' ' and uuid is not null;\ncommit;\n");
        }          
      }
      
      GrouperDdlUtils.ddlutilsDropIndexes(GrouperDdlUtils.ddlutilsFindTable(database, 
          Membership.TABLE_GROUPER_MEMBERSHIPS), Membership.COLUMN_MEMBERSHIP_UUID);
      //we need conversion if there is a uuid col, and not an old_uuid col or old_id col
      if (needsMembershipIdConversion(database)) {
        
        Table membershipsTable = GrouperDdlUtils.ddlutilsFindTable(database, 
            Membership.TABLE_GROUPER_MEMBERSHIPS);
        
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, Membership.COLUMN_OLD_ID, Types.VARCHAR, "128", false, false);
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, Membership.COLUMN_OLD_MEMBERSHIP_UUID, Types.VARCHAR, "128", false, false);

        if (isDestinationVersion) {

          //update records, move the uuid to the id
          additionalScripts.append("update grouper_memberships set old_id = id, id = membership_uuid, old_membership_uuid = membership_uuid, membership_uuid = ' ' where membership_uuid != ' ' and membership_uuid is not null;\ncommit;\n");
        }          
      }
      
      GrouperDdlUtils.ddlutilsDropIndexes(GrouperDdlUtils.ddlutilsFindTable(database, 
          Field.TABLE_GROUPER_FIELDS), Field.COLUMN_FIELD_UUID);
      //we need conversion if there is a uuid col, and not an old_uuid col or old_id col
      if (needsFieldsIdConversion(database)) {
        
        Table fieldsTable = GrouperDdlUtils.ddlutilsFindTable(database, 
            Field.TABLE_GROUPER_FIELDS);
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(fieldsTable, Field.COLUMN_OLD_ID, Types.VARCHAR, "128", false, false);
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(fieldsTable, Field.COLUMN_OLD_FIELD_UUID, Types.VARCHAR, "128", false, false);
        
        if (isDestinationVersion) {
          //update records, move the uuid to the id
          additionalScripts.append("update grouper_fields set old_id = id, id = field_uuid, old_field_uuid = field_uuid, field_uuid = ' ' where field_uuid != ' ' and field_uuid is not null;\ncommit;\n");
        }          
      }
      
      GrouperDdlUtils.ddlutilsDropIndexes(GrouperDdlUtils.ddlutilsFindTable(database, 
          Group.TABLE_GROUPER_GROUPS), Group.COLUMN_UUID);
      //we need conversion if there is a uuid col, and not an old_uuid col or old_id col
      if (needsGroupsIdConversion(database)) {
        
        Table groupsTable = GrouperDdlUtils.ddlutilsFindTable(database, 
            Group.TABLE_GROUPER_GROUPS);
        
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(groupsTable, Group.COLUMN_OLD_ID, Types.VARCHAR, "128", false, false);
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(groupsTable, Group.COLUMN_OLD_UUID, Types.VARCHAR, "128", false, false);
        
        if (isDestinationVersion) {
          //update records, move the uuid to the id
          additionalScripts.append("update grouper_groups set old_id = id, id = uuid, old_uuid = uuid, uuid = ' ' where uuid != ' ' and uuid is not null;\ncommit;\n");
        }          
      }

      GrouperDdlUtils.ddlutilsDropIndexes(GrouperDdlUtils.ddlutilsFindTable(database, 
          Member.TABLE_GROUPER_MEMBERS), Member.COLUMN_MEMBER_UUID);
      //we need conversion if there is a uuid col, and not an old_uuid col or old_id col
      if (needsMembersIdConversion(database)) {
        
        Table membersTable = GrouperDdlUtils.ddlutilsFindTable(database, 
            Member.TABLE_GROUPER_MEMBERS);
        
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membersTable, Member.COLUMN_OLD_ID, Types.VARCHAR, "128", false, false);
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membersTable, Member.COLUMN_OLD_MEMBER_UUID, Types.VARCHAR, "128", false, false);
        
        if (isDestinationVersion) {
          //update records, move the uuid to the id
          additionalScripts.append("update grouper_members set old_id = id, id = member_uuid, old_member_uuid = member_uuid, member_uuid = ' ' where member_uuid != ' ' and member_uuid is not null;\ncommit;\n");
        }          
      }

      GrouperDdlUtils.ddlutilsDropIndexes(GrouperDdlUtils.ddlutilsFindTable(database, 
          Stem.TABLE_GROUPER_STEMS), Stem.COLUMN_UUID);
      //we need conversion if there is a uuid col, and not an old_uuid col or old_id col
      if (needsStemIdConversion(database)) {
        
        Table stemsTable = GrouperDdlUtils.ddlutilsFindTable(database, 
            Stem.TABLE_GROUPER_STEMS);

        GrouperDdlUtils.ddlutilsFindOrCreateColumn(stemsTable, Stem.COLUMN_OLD_ID, Types.VARCHAR, "128", false, false);
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(stemsTable, Stem.COLUMN_OLD_UUID, Types.VARCHAR, "128", false, false);
        
        if (isDestinationVersion) {
          //update records, move the uuid to the id
          additionalScripts.append("update grouper_stems set old_id = id, id = uuid, old_uuid = uuid, uuid = ' ' where uuid != ' ' and uuid is not null;\ncommit;\n");
        }          
      }

      GrouperDdlUtils.ddlutilsDropIndexes(GrouperDdlUtils.ddlutilsFindTable(database, 
          GroupType.TABLE_GROUPER_TYPES), GroupType.COLUMN_TYPE_UUID);
      //we need conversion if there is a uuid col, and not an old_uuid col or old_id col
      if (needsTypesIdConversion(database)) {
        
        Table typesTable = GrouperDdlUtils.ddlutilsFindTable(database, 
            GroupType.TABLE_GROUPER_TYPES);
              
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(typesTable, GroupType.COLUMN_OLD_ID, Types.VARCHAR, "128", false, false);
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(typesTable, GroupType.COLUMN_OLD_TYPE_UUID, Types.VARCHAR, "128", false, false);
        
        if (isDestinationVersion) {
          //update records, move the uuid to the id
          additionalScripts.append("update grouper_types set old_id = id, id = type_uuid, old_type_uuid = type_uuid, type_uuid = ' ' where type_uuid != ' ' and type_uuid is not null;\ncommit;\n");
        }          
      }
    }

  },

  /** add in the hibernate_version_number cols */
  V4 {
    
    /**
     * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#updateVersionFromPrevious(org.apache.ddlutils.model.Database, DdlVersionBean)
     */
    public void updateVersionFromPrevious(Database database, 
        DdlVersionBean ddlVersionBean) {
      
      versionNumberColumnAdd(ddlVersionBean, Composite.TABLE_GROUPER_COMPOSITES);
      versionNumberColumnAdd(ddlVersionBean, Attribute.TABLE_GROUPER_ATTRIBUTES);
      versionNumberColumnAdd(ddlVersionBean, GroupTypeTuple.TABLE_GROUPER_GROUPS_TYPES);
      versionNumberColumnAdd(ddlVersionBean, Field.TABLE_GROUPER_FIELDS);
      versionNumberColumnAdd(ddlVersionBean, Membership.TABLE_GROUPER_MEMBERSHIPS);
      versionNumberColumnAdd(ddlVersionBean, Group.TABLE_GROUPER_GROUPS);
      versionNumberColumnAdd(ddlVersionBean, Member.TABLE_GROUPER_MEMBERS);
      versionNumberColumnAdd(ddlVersionBean, Stem.TABLE_GROUPER_STEMS);
      versionNumberColumnAdd(ddlVersionBean, GroupType.TABLE_GROUPER_TYPES);
      
    }
  },
  
  /**
   * add grouper loader
   */
  V3 {
    
    /**
     * 
     * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#updateVersionFromPrevious(org.apache.ddlutils.model.Database, DdlVersionBean)
     */
    @Override
    public void updateVersionFromPrevious(Database database, 
        DdlVersionBean ddlVersionBean) {

      //see if the grouper_ext_loader_log table is there
      Table grouploaderLogTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database,"grouper_loader_log");
      
      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "id", 
          Types.VARCHAR, "128", true, true);
      
      
      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "job_name", 
          Types.VARCHAR, "512", false, false);
      
      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "status", 
          Types.VARCHAR, "20", false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "started_time", 
          Types.TIMESTAMP, null, false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "ended_time", 
          Types.TIMESTAMP, null, false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "millis", 
          Types.INTEGER, null, false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "millis_get_data", 
          Types.INTEGER, null, false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "millis_load_data", 
          Types.INTEGER, null, false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "job_type", 
          Types.VARCHAR, "128", false, false);
      
      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "job_schedule_type", 
          Types.VARCHAR, "128", false, false);
      
      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "job_description", 
          Types.VARCHAR, "4000", false, false);
      
      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "job_message", 
          Types.VARCHAR, "4000", false, false);
      
      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "host", 
          Types.VARCHAR, "128", false, false);
      
      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "group_uuid", 
          Types.VARCHAR, "128", false, false);
      
      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "job_schedule_quartz_cron", 
          Types.VARCHAR, "128", false, false);
      
      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "job_schedule_interval_seconds", 
          Types.INTEGER, null, false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "last_updated", 
          Types.TIMESTAMP, null, false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "unresolvable_subject_count", 
          Types.INTEGER, null, false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "insert_count", 
          Types.INTEGER, null, false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "update_count", 
          Types.INTEGER, null, false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "delete_count", 
          Types.INTEGER, null, false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "total_count", 
          Types.INTEGER, null, false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "parent_job_name", 
          Types.VARCHAR, "512", false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "parent_job_id", 
          Types.VARCHAR, "128", false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "and_group_names", 
          Types.VARCHAR, "512", false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouploaderLogTable, "job_schedule_priority", 
          Types.INTEGER, null, false, false);

      //see if the grouper_ext_loader_log table is there
      GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, "grouper_loader_log",
          "grouper_loader_job_name_idx", false, "job_name");

    }
    
    
  },
    
  /** all tables and indexes from grouper v1.3 */
  V2 {
    
    /**
     * 
     * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#updateVersionFromPrevious(org.apache.ddlutils.model.Database, DdlVersionBean)
     */
    @Override
    public void updateVersionFromPrevious(Database database, 
        DdlVersionBean ddlVersionBean) {
      
      int buildingToVersion = ddlVersionBean.getBuildingToVersion();
      
      boolean buildingToThisVersion = V4.getVersion() >= buildingToVersion;
      
      {
        
        boolean attributesTableNew = database.findTable(Attribute.TABLE_GROUPER_ATTRIBUTES) == null;
        
        Table attributeTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database,
            Attribute.TABLE_GROUPER_ATTRIBUTES);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(attributeTable, "id", 
            Types.VARCHAR, "128", true, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(attributeTable, "group_id", 
            Types.VARCHAR, "128", false, true);
  
        if (buildingToThisVersion) {
          
          GrouperDdlUtils.ddlutilsFindOrCreateColumn(attributeTable, "field_name", 
              Types.VARCHAR, "32", false, false);
        
        } 
        
        //this is needed for hibernate, so always add it if the table is being created
        if (attributesTableNew || attributeTable.findColumn(Attribute.COLUMN_FIELD_ID) != null) {
          GrouperDdlUtils.ddlutilsFindOrCreateColumn(attributeTable, Attribute.COLUMN_FIELD_ID, 
              Types.VARCHAR, "128", false, true);
        }
        
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(attributeTable, "value", 
            Types.VARCHAR, "1024", false, true);
  
        //dont add foreign keys if col not there
        if (attributeTable.findColumn(Attribute.COLUMN_FIELD_ID) != null) {
          addAttributeFieldIndexes(database, ddlVersionBean, attributeTable);
        }
        
        //see if we have a custom script here, do this since some versions of mysql cant handle indexes on columns that large
        String scriptOverride = ddlVersionBean.isMysql() ? "\nCREATE INDEX attribute_value_idx " +
            "ON grouper_attributes (value(333));\n" : null;
        
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, ddlVersionBean, attributeTable.getName(), 
            "attribute_value_idx", scriptOverride, false, "value");
  
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, attributeTable.getName(), "attribute_group_idx", false, "group_id");
        
        versionNumberColumnFindOrCreate(attributeTable);
        
      }
      
      {
        Table compositeTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database,
            Composite.TABLE_GROUPER_COMPOSITES);
  
        boolean needsConversion = needsCompositeIdConversion(database);
      
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(compositeTable, "id", 
            Types.VARCHAR, "128", true, true);

        if (needsConversion || buildingToThisVersion) {

          GrouperDdlUtils.ddlutilsFindOrCreateColumn(compositeTable, "uuid", 
              Types.VARCHAR, "128", false, false);

          GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, compositeTable.getName(), 
              "composite_uuid_idx", true, "uuid");

        }
        
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(compositeTable, "owner", 
            Types.VARCHAR, "128", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(compositeTable, "left_factor", 
            Types.VARCHAR, "128", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(compositeTable, "right_factor", 
            Types.VARCHAR, "128", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(compositeTable, "type", 
            Types.VARCHAR, "32", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(compositeTable, "creator_id", 
            Types.VARCHAR, "128", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(compositeTable, "create_time", 
            Types.BIGINT, "20", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, compositeTable.getName(), 
            "composite_composite_idx", false, "owner");
  
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, compositeTable.getName(), 
            "composite_createtime_idx", false, "create_time");
        
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, compositeTable.getName(), 
            "composite_creator_idx", false, "creator_id");
  
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, compositeTable.getName(), 
            "composite_factor_idx", false, "left_factor", "right_factor");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, compositeTable.getName(), 
            "composite_left_factor_idx", false, "left_factor");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, compositeTable.getName(), 
            "composite_right_factor_idx", false, "right_factor");

        versionNumberColumnFindOrCreate(compositeTable);

      }

      {

        Table fieldsTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database,
            Field.TABLE_GROUPER_FIELDS);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(fieldsTable, "id", 
            Types.VARCHAR, "128", true, true);

        boolean needsConversion = needsFieldsIdConversion(database);
    
        if (needsConversion || buildingToThisVersion) {

          GrouperDdlUtils.ddlutilsFindOrCreateColumn(fieldsTable, "field_uuid", 
              Types.VARCHAR, "128", false, false);

          GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, fieldsTable.getName(), 
              "field_uuid_idx", true, "field_uuid");

        }
        
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(fieldsTable, "grouptype_uuid", 
            Types.VARCHAR, "128", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(fieldsTable, "is_nullable", 
            Types.BIT, "1", false, false);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(fieldsTable, "name", 
            Types.VARCHAR, "32", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(fieldsTable, "read_privilege", 
            Types.VARCHAR, "32", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(fieldsTable, "type", 
            Types.VARCHAR, "32", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(fieldsTable, "write_privilege", 
            Types.VARCHAR, "32", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, fieldsTable.getName(), 
            "name_and_type", true, "name", "type");
        
        versionNumberColumnFindOrCreate(fieldsTable);

      }
    
      {
        Table groupsTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database,
            Group.TABLE_GROUPER_GROUPS);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(groupsTable, "id", 
            Types.VARCHAR, "128", true, true);
  
        boolean needsConversion = needsGroupsIdConversion(database);
        
        if (needsConversion || buildingToThisVersion) {

          GrouperDdlUtils.ddlutilsFindOrCreateColumn(groupsTable, "uuid", 
              Types.VARCHAR, "128", false, false);

          GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, groupsTable.getName(), 
              "group_uuid_idx", true, "uuid");

        }
        
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(groupsTable, "parent_stem", 
            Types.VARCHAR, "128", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(groupsTable, "creator_id", 
            Types.VARCHAR, "128", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(groupsTable, "create_time", 
            Types.BIGINT, "20", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(groupsTable, "modifier_id", 
            Types.VARCHAR, "128", false, false);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(groupsTable, "modify_time", 
            Types.BIGINT, "20", false, false);
  
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, groupsTable.getName(), 
            "group_parent_idx", false, "parent_stem");
        
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, groupsTable.getName(), 
            "group_creator_idx", false, "creator_id");
        
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, groupsTable.getName(), 
            "group_createtime_idx", false, "create_time");
        
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, groupsTable.getName(), 
            "group_modifier_idx", false, "modifier_id");
        
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, groupsTable.getName(), 
            "group_modifytime_idx", false, "modify_time");
        
        versionNumberColumnFindOrCreate(groupsTable);
        
      }
    
      {
        Table groupsTypesTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database,
            "grouper_groups_types");
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(groupsTypesTable, "id", 
            Types.VARCHAR, "128", true, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(groupsTypesTable, "group_uuid", 
            Types.VARCHAR, "128", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(groupsTypesTable, "type_uuid", 
            Types.VARCHAR, "128", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, groupsTypesTable.getName(), 
            "grouptypetyple_grouptype_idx", true, "group_uuid", "type_uuid");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, groupsTypesTable.getName(), 
            "grouptypetuple_type_idx", false, "type_uuid");
        
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, groupsTypesTable.getName(), 
            "grouptypetuple_group_idx", false, "group_uuid");
        
        versionNumberColumnFindOrCreate(groupsTypesTable);
      }
    
      {
        Table membersTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database,
            Member.TABLE_GROUPER_MEMBERS);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membersTable, "id", 
            Types.VARCHAR, "128", true, true);
  
        boolean needsConversion = needsMembersIdConversion(database);
        
        if (needsConversion || buildingToThisVersion ) {
          
          GrouperDdlUtils.ddlutilsFindOrCreateColumn(membersTable, "member_uuid", 
              Types.VARCHAR, "128", false, false);
    
          GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membersTable.getName(), 
              "member_uuid_idx", true, "member_uuid");

        }
        
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membersTable, "subject_id", 
            Types.VARCHAR, "255", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membersTable, "subject_source", 
            Types.VARCHAR, "255", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membersTable, "subject_type", 
            Types.VARCHAR, "255", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membersTable.getName(), 
            "member_subjectsourcetype_idx", true, "subject_id", "subject_source", 
            "subject_type");
        
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membersTable.getName(), 
            "member_subjectsource_idx", false, "subject_source");
        
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membersTable.getName(), 
            "member_subjectid_idx", false, "subject_id");
        
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membersTable.getName(), 
            "member_subjecttype_idx", false, "subject_type");
        
        versionNumberColumnFindOrCreate(membersTable);
      }
    
      {
        boolean membershipsTableExists = database.findTable(Membership.TABLE_GROUPER_MEMBERSHIPS) != null;
        Table membershipsTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database,
            Membership.TABLE_GROUPER_MEMBERSHIPS);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, "id", 
            Types.VARCHAR, "128", true, true);
  
        boolean needsConversion = needsMembersIdConversion(database);
        
        if (needsConversion || buildingToThisVersion ) {
          
          GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, "membership_uuid", 
              Types.VARCHAR, "128", false, false);
    
          GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membershipsTable.getName(), 
              "membership_uuid_idx", true, "membership_uuid");

        }

        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, "owner_id", 
            Types.VARCHAR, "128", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, "member_id", 
            Types.VARCHAR, "128", false, true);
  
        //if not testing, dont worry about these columns
        if (buildingToThisVersion) {
          GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, "list_name", 
              Types.VARCHAR, "32", false, false);
    
          GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, "list_type", 
              Types.VARCHAR, "32", false, false);
        }
        
        //only add the col if a new table, else it is added in a subsequent version
        if (!membershipsTableExists || membershipsTable.findColumn(Membership.COLUMN_FIELD_ID) != null) {
          GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, Membership.COLUMN_FIELD_ID, 
              Types.VARCHAR, "128", false, true);
        }
        
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, "mship_type", 
            Types.VARCHAR, "32", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, "via_id", 
            Types.VARCHAR, "128", false, false);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, "depth", 
            Types.INTEGER, "11", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, "parent_membership", 
            Types.VARCHAR, "128", false, false);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, "creator_id", 
            Types.VARCHAR, "128", false, false);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(membershipsTable, "create_time", 
            Types.BIGINT, "20", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membershipsTable.getName(), 
            "membership_createtime_idx", false, "create_time");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membershipsTable.getName(), 
            "membership_creator_idx", false, "creator_id");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membershipsTable.getName(), 
            "membership_depth_idx", false, "depth");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membershipsTable.getName(), 
            "membership_member_idx", false, "member_id");

        //dont add foreign keys if col not there
        if (membershipsTable.findColumn(Membership.COLUMN_FIELD_ID) != null) {

          addMembershipFieldIndexes(database, membershipsTable);

        }

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membershipsTable.getName(), 
            "membership_member_via_idx", false, "member_id", "via_id");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membershipsTable.getName(), 
            "membership_owner_idx", false, "owner_id");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membershipsTable.getName(), 
            "membership_parent_idx", false, "parent_membership");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membershipsTable.getName(), 
            "membership_type_idx", false, "mship_type");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membershipsTable.getName(), 
            "membership_via_idx", false, "via_id");

        versionNumberColumnFindOrCreate(membershipsTable);
       
      }
      {
        if (buildingToThisVersion) {
          Table sessionsTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database,
              "grouper_sessions");
    
          GrouperDdlUtils.ddlutilsFindOrCreateColumn(sessionsTable, "id", 
              Types.VARCHAR, "128", true, true);
          
          //note, this code is only here for unit testing... no need to do id conversion
          
          GrouperDdlUtils.ddlutilsFindOrCreateColumn(sessionsTable, "member_id", 
              Types.VARCHAR, "128", false, true);
    
          GrouperDdlUtils.ddlutilsFindOrCreateColumn(sessionsTable, "starttime", 
              Types.BIGINT, "20", false, true);
    
          GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, sessionsTable.getName(), 
              "session_member_idx", false, "member_id");
  
          versionNumberColumnFindOrCreate(sessionsTable);
        }
      }
      {
        Table stemsTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database,
            Stem.TABLE_GROUPER_STEMS);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(stemsTable, "id", 
            Types.VARCHAR, "128", true, true);
  
        boolean needsConversion = needsStemIdConversion(database);
        
        if (needsConversion || buildingToThisVersion) {
          
          GrouperDdlUtils.ddlutilsFindOrCreateColumn(stemsTable, "uuid", 
              Types.VARCHAR, "128", false, false);

          GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, stemsTable.getName(), 
              "stem_uuid_idx", true, "uuid");

        }
        
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(stemsTable, "parent_stem", 
            Types.VARCHAR, "128", false, false);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(stemsTable, "name", 
            Types.VARCHAR, "255", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(stemsTable, "display_name", 
            Types.VARCHAR, "255", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(stemsTable, "creator_id", 
            Types.VARCHAR, "128", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(stemsTable, "create_time", 
            Types.BIGINT, "20", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(stemsTable, "modifier_id", 
            Types.VARCHAR, "128", false, false);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(stemsTable, "modify_time", 
            Types.BIGINT, "20", false, false);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(stemsTable, "display_extension", 
            Types.VARCHAR, "255", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(stemsTable, "extension", 
            Types.VARCHAR, "255", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(stemsTable, "description", 
            Types.VARCHAR, "1024", false, false);
  
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, stemsTable.getName(), 
            "stem_createtime_idx", false, "create_time");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, stemsTable.getName(), 
            "stem_creator_idx", false, "creator_id");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, stemsTable.getName(), 
            "stem_dislpayextn_idx", false, "display_extension");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, stemsTable.getName(), 
            "stem_displayname_idx", false, "display_name");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, stemsTable.getName(), 
            "stem_extn_idx", false, "extension");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, stemsTable.getName(), 
            "stem_modifier_idx", false, "modifier_id");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, stemsTable.getName(), 
            "stem_modifytime_idx", false, "modify_time");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, stemsTable.getName(), 
            "stem_name_idx", true, "name");

        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, stemsTable.getName(), 
            "stem_parent_idx", false, "parent_stem");

        versionNumberColumnFindOrCreate(stemsTable);
      }
      {
        Table typesTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database,
            "grouper_types");
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(typesTable, "id", 
            Types.VARCHAR, "128", true, true);
  
        boolean needsConversion = needsTypesIdConversion(database);
        
        if (needsConversion || buildingToThisVersion) {
          
          GrouperDdlUtils.ddlutilsFindOrCreateColumn(typesTable, "type_uuid", 
              Types.VARCHAR, "128", false, false);

          GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, typesTable.getName(), 
              "type_uuid_idx", true, "type_uuid");

        }
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(typesTable, "name", 
            Types.VARCHAR, "255", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(typesTable, "creator_uuid", 
            Types.VARCHAR, "128", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(typesTable, "create_time", 
            Types.BIGINT, "20", false, true);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(typesTable, "is_assignable", 
            Types.BIT, "1", false, false);
  
        GrouperDdlUtils.ddlutilsFindOrCreateColumn(typesTable, "is_internal", 
            Types.BIT, "1", false, false);
  
        GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, typesTable.getName(), 
            "type_name_idx", true, "name");

        versionNumberColumnFindOrCreate(typesTable);
      }
    }

  }, 
  
  /** first version of grouper, make sure the ddl table is there */
  V1 {
    /**
     * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#updateVersionFromPrevious(org.apache.ddlutils.model.Database, DdlVersionBean)
     */
    @Override
    public void updateVersionFromPrevious(Database database, 
        DdlVersionBean ddlVersionBean) {

      //see if the grouper_ext_loader_log table is there
      Table grouperDdlTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database,"grouper_ddl");

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouperDdlTable, "id",
          Types.VARCHAR, "128", true, true);
      
      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouperDdlTable, "object_name", 
          Types.VARCHAR, "128", false, false);
      
      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouperDdlTable, "db_version", 
          Types.INTEGER, null, false, false);
      
      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouperDdlTable, "last_updated", 
          Types.VARCHAR, "50", false, false);

      GrouperDdlUtils.ddlutilsFindOrCreateColumn(grouperDdlTable, "history", 
          Types.VARCHAR, "4000", false, false);

      //object name is unique
      GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, "grouper_ddl", "grouper_ddl_object_name_idx", 
          true, "object_name");
      
    }
  };

  /**
   * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#getVersion()
   */
  public int getVersion() {
    return GrouperDdlUtils.versionIntFromEnum(this);
  }

  /**
   * cache this
   */
  private static int currentVersion = -1;
  
  /**
   * add version number col if not there
   * @param ddlVersionBean 
   * @param tableName
   */
  private static void versionNumberColumnAdd(DdlVersionBean ddlVersionBean, String tableName) {
    Database database = ddlVersionBean.getDatabase();

    //if there is no uuid col, then forget it, or if there is a old_uuid col forget it
    Table table = GrouperDdlUtils.ddlutilsFindTable(database, tableName);
    
    if (GrouperDdlUtils.ddlutilsFindColumn(database, tableName, COLUMN_HIBERNATE_VERSION_NUMBER, false) == null) {
      
      boolean destinationVersion = ddlVersionBean.isDestinationVersion();

      if (destinationVersion && ddlVersionBean.getPlatform().getName().toLowerCase().contains("postgres")) {
        ddlVersionBean.appendAdditionalScriptUnique("ALTER TABLE " + tableName + " ADD COLUMN hibernate_version_number bigint DEFAULT 0;\n");
      } else {
        versionNumberColumnFindOrCreate(table);
      }
      if (destinationVersion) {
        ddlVersionBean.getAdditionalScripts().append(
            "update " + tableName + " set hibernate_version_number = 0 where hibernate_version_number is null;\ncommit;\n");
      }
    }

  }

  /**
   * @param table
   */
  private static void versionNumberColumnFindOrCreate(Table table) {
    GrouperDdlUtils.ddlutilsFindOrCreateColumn(table, COLUMN_HIBERNATE_VERSION_NUMBER, 
        Types.BIGINT, "12", false, false);
  }
  
  /**
   * keep the current version here, increment as things change
   * @return the current version
   */
  public static int currentVersion() {
    if (currentVersion == -1) {
      int max = -1;
      for (GrouperDdl grouperDdl : GrouperDdl.values()) {
        String number = grouperDdl.name().substring(1);
        int theInt = Integer.parseInt(number);
        max = Math.max(max, theInt);
      }
      currentVersion = max;
    }
    return currentVersion;
  }

  /**
   * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#getObjectName()
   */
  public String getObjectName() {
    return GrouperDdlUtils.objectName(this);
  }

  /**
   * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#getDefaultTablePattern()
   */
  public String getDefaultTablePattern() {
    return "GROUPER%";
  }
  
  /** column for hibernate version number */
  private static final String COLUMN_HIBERNATE_VERSION_NUMBER = "hibernate_version_number";
  
  /**
   * @see edu.internet2.middleware.grouper.ddl.DdlVersionable#updateVersionFromPrevious(org.apache.ddlutils.model.Database, DdlVersionBean)
   */
  public abstract void updateVersionFromPrevious(Database database, 
      DdlVersionBean ddlVersionBean);

  /**
   * @param database
   * @return true if needs
   */
  private static boolean needsTypesIdConversion(Database database) {
    return GrouperDdlUtils.ddlutilsFindColumn(database, GroupType.TABLE_GROUPER_TYPES, GroupType.COLUMN_TYPE_UUID, false) != null
    && GrouperDdlUtils.ddlutilsFindColumn(database, GroupType.TABLE_GROUPER_TYPES, GroupType.COLUMN_OLD_TYPE_UUID, false) == null
    && GrouperDdlUtils.ddlutilsFindColumn(database, GroupType.TABLE_GROUPER_TYPES, GroupType.COLUMN_OLD_ID, false) == null;
  }

  /**
   * @param database
   * @return true if needs
   */
  private static boolean needsMembersIdConversion(Database database) {
    return GrouperDdlUtils.ddlutilsFindColumn(database, Member.TABLE_GROUPER_MEMBERS, Member.COLUMN_MEMBER_UUID, false) != null
    && GrouperDdlUtils.ddlutilsFindColumn(database, Member.TABLE_GROUPER_MEMBERS, Member.COLUMN_OLD_MEMBER_UUID, false) == null
    && GrouperDdlUtils.ddlutilsFindColumn(database, Member.TABLE_GROUPER_MEMBERS, Member.COLUMN_OLD_ID, false) == null;
  }

  /**
   * @param database
   * @return true if needs
   */
  private static boolean needsGroupsIdConversion(Database database) {
    return GrouperDdlUtils.ddlutilsFindColumn(database, Group.TABLE_GROUPER_GROUPS, Group.COLUMN_UUID, false) != null
      && GrouperDdlUtils.ddlutilsFindColumn(database, Group.TABLE_GROUPER_GROUPS, Group.COLUMN_OLD_UUID, false) == null
      && GrouperDdlUtils.ddlutilsFindColumn(database, Group.TABLE_GROUPER_GROUPS, Group.COLUMN_OLD_ID, false) == null;
  }

  /**
   * @param database
   * @return true if needs it
   */
  private static boolean needsFieldsIdConversion(Database database) {
    return GrouperDdlUtils.ddlutilsFindColumn(database, Field.TABLE_GROUPER_FIELDS, Field.COLUMN_FIELD_UUID, false) != null
      && GrouperDdlUtils.ddlutilsFindColumn(database, Field.TABLE_GROUPER_FIELDS, Field.COLUMN_OLD_FIELD_UUID, false) == null
      && GrouperDdlUtils.ddlutilsFindColumn(database, Field.TABLE_GROUPER_FIELDS, Field.COLUMN_OLD_ID, false) == null;
  }

  /**
   * @param database
   * @return true if needs composite conversion
   */
  private static boolean needsCompositeIdConversion(Database database) {
    return GrouperDdlUtils.ddlutilsFindColumn(database, Composite.TABLE_GROUPER_COMPOSITES, Composite.COLUMN_UUID, false) != null
      && GrouperDdlUtils.ddlutilsFindColumn(database, Composite.TABLE_GROUPER_COMPOSITES, Composite.COLUMN_OLD_UUID, false) == null
      && GrouperDdlUtils.ddlutilsFindColumn(database, Composite.TABLE_GROUPER_COMPOSITES, Composite.COLUMN_OLD_ID, false) == null;
  }  

  /**
   * @param database
   * @return true if needs composite conversion
   */
  private static boolean needsMembershipIdConversion(Database database) {
    return GrouperDdlUtils.ddlutilsFindColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_MEMBERSHIP_UUID, false) != null
      && GrouperDdlUtils.ddlutilsFindColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_MEMBERSHIP_UUID, false) == null
      && GrouperDdlUtils.ddlutilsFindColumn(database, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_ID, false) == null;
  }  

  /**
   * @param database
   * @return true if needs composite conversion
   */
  private static boolean needsMembershipFieldIdConversion(Database database) {
    //no field_id
    return GrouperDdlUtils.ddlutilsFindColumn(database, 
        Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_FIELD_ID, false) == null
        //has list name and type
      && GrouperDdlUtils.ddlutilsFindColumn(database, 
          Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_LIST_NAME, false) != null
      && GrouperDdlUtils.ddlutilsFindColumn(database, 
          Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_LIST_TYPE, false) != null
        //no old list name or type
      && GrouperDdlUtils.ddlutilsFindColumn(database, 
          Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_LIST_NAME, false) == null
      && GrouperDdlUtils.ddlutilsFindColumn(database, 
          Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_OLD_LIST_TYPE, false) == null;
  }  

  /**
   * @param database
   * @return true if needs composite conversion
   */
  private static boolean needsAttributeFieldIdConversion(Database database) {
    //no field_id
    return GrouperDdlUtils.ddlutilsFindColumn(database, 
        Attribute.TABLE_GROUPER_ATTRIBUTES, Attribute.COLUMN_FIELD_ID, false) == null
      //has field name
      && GrouperDdlUtils.ddlutilsFindColumn(database, 
          Attribute.TABLE_GROUPER_ATTRIBUTES, Attribute.COLUMN_FIELD_NAME, false) != null
      //no old field name
      && GrouperDdlUtils.ddlutilsFindColumn(database, 
          Attribute.TABLE_GROUPER_ATTRIBUTES, Attribute.COLUMN_OLD_FIELD_NAME, false) == null;
  }  

  /**
   * @param database
   * @return true if needs
   */
  private static boolean needsStemIdConversion(Database database) {
    return GrouperDdlUtils.ddlutilsFindColumn(database, Stem.TABLE_GROUPER_STEMS, Stem.COLUMN_UUID, false) != null
      && GrouperDdlUtils.ddlutilsFindColumn(database, Stem.TABLE_GROUPER_STEMS, Stem.COLUMN_OLD_UUID, false) == null
      && GrouperDdlUtils.ddlutilsFindColumn(database, Stem.TABLE_GROUPER_STEMS, Stem.COLUMN_OLD_ID, false) == null;
  }
  
  /**
   * drop all views
   * @param ddlVersionBean 
   */
  public void dropAllViews(DdlVersionBean ddlVersionBean) {
    GrouperDdlUtils.ddlutilsDropViewIfExists(ddlVersionBean, "grouper_attributes_v");
    GrouperDdlUtils.ddlutilsDropViewIfExists(ddlVersionBean, "grouper_composites_v");
    GrouperDdlUtils.ddlutilsDropViewIfExists(ddlVersionBean, "grouper_groups_types_v");
    GrouperDdlUtils.ddlutilsDropViewIfExists(ddlVersionBean, "grouper_groups_v");
    GrouperDdlUtils.ddlutilsDropViewIfExists(ddlVersionBean, "grouper_memberships_v");
    GrouperDdlUtils.ddlutilsDropViewIfExists(ddlVersionBean, "grouper_rpt_attributes_v");
    GrouperDdlUtils.ddlutilsDropViewIfExists(ddlVersionBean, "grouper_rpt_composites_v");
    GrouperDdlUtils.ddlutilsDropViewIfExists(ddlVersionBean, "grouper_rpt_group_field_v");
    GrouperDdlUtils.ddlutilsDropViewIfExists(ddlVersionBean, "grouper_rpt_groups_v");
    GrouperDdlUtils.ddlutilsDropViewIfExists(ddlVersionBean, "grouper_rpt_members_v");
    GrouperDdlUtils.ddlutilsDropViewIfExists(ddlVersionBean, "grouper_rpt_stems_v");
    GrouperDdlUtils.ddlutilsDropViewIfExists(ddlVersionBean, "grouper_rpt_types_v");
    GrouperDdlUtils.ddlutilsDropViewIfExists(ddlVersionBean, "grouper_stems_v");
  }
  
  /**
   * add all foreign keys
   * @param ddlVersionBean 
   */
  public void addAllForeignKeysViewsEtc(DdlVersionBean ddlVersionBean) {

    Database database = ddlVersionBean.getDatabase();
    
    int buildingToVersion = ddlVersionBean.getBuildingToVersion();
    
    //dont do anything if version is less than 2
    if (buildingToVersion < V2.getVersion()) {
      return;
    }
    
    //add comments on tables, comments
    //see if the grouper_ext_loader_log table is there
    GrouperDdlUtils.ddlutilsTableComment(ddlVersionBean,"grouper_ddl", 
        "holds a record for each database object name, and db version, and java version");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_ddl", "id", "uuid of this ddl record");
    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_ddl", "object_name", 
        "Corresponds to an enum in grouper.ddl package (with Ddl on end), represents one module, " +
        "so grouper itself is one object");
    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_ddl", "db_version", 
        "Version of this object as far as DB knows about");
    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_ddl", "last_updated", 
        "last update timestamp, string so it can easily be used from update statement");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_ddl", "history", 
        "history of this object name, with most recent first (truncated after 4k)");

    
    
    GrouperDdlUtils.ddlutilsTableComment(ddlVersionBean,
        Attribute.TABLE_GROUPER_ATTRIBUTES, "attributes for groups, including name, extension, etc");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Attribute.TABLE_GROUPER_ATTRIBUTES,  "id", 
        "db id of this attribute record");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Attribute.TABLE_GROUPER_ATTRIBUTES,  "group_id", 
        "group_uuid foreign key");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Attribute.TABLE_GROUPER_ATTRIBUTES,  Attribute.COLUMN_FIELD_ID, 
          "foreign key to field by id");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Attribute.TABLE_GROUPER_ATTRIBUTES,   "value", 
        "value this attribute record");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Attribute.TABLE_GROUPER_ATTRIBUTES, 
        COLUMN_HIBERNATE_VERSION_NUMBER, "hibernate uses this to version rows");

    
    GrouperDdlUtils.ddlutilsTableComment(ddlVersionBean,
          Composite.TABLE_GROUPER_COMPOSITES, "records the composite group, and its factors");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Composite.TABLE_GROUPER_COMPOSITES, "id", 
          "db id of this composite record");

    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Composite.TABLE_GROUPER_COMPOSITES,  "owner", 
          "group uuid of the composite group");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Composite.TABLE_GROUPER_COMPOSITES,  "left_factor", 
          "left factor of the composite group");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Composite.TABLE_GROUPER_COMPOSITES,  "right_factor", 
          "right factor of the composite group");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Composite.TABLE_GROUPER_COMPOSITES,  "type", 
          "e.g. union, complement, intersection");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Composite.TABLE_GROUPER_COMPOSITES,  "creator_id", 
          "member uuid of who created this");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Composite.TABLE_GROUPER_COMPOSITES,  "create_time", 
          "number of millis since 1970 until when created");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Composite.TABLE_GROUPER_COMPOSITES, 
        COLUMN_HIBERNATE_VERSION_NUMBER, "hibernate uses this to version rows");

    
    GrouperDdlUtils.ddlutilsTableComment(ddlVersionBean, Field.TABLE_GROUPER_FIELDS, "describes fields related to types");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Field.TABLE_GROUPER_FIELDS,  "id", 
          "db id of this field record");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Field.TABLE_GROUPER_FIELDS,  "grouptype_uuid", 
          "foreign key to group type");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Field.TABLE_GROUPER_FIELDS, "is_nullable", 
          "if this is nullable");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Field.TABLE_GROUPER_FIELDS, "name", 
          "name of the field");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Field.TABLE_GROUPER_FIELDS,  "read_privilege", 
          "which privilege is required to read this field");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Field.TABLE_GROUPER_FIELDS,  "type", 
          "type of field (e.g. attribute, list, access, naming)");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Field.TABLE_GROUPER_FIELDS, "write_privilege", 
          "which privilege is required to write this attribute");

    GrouperDdlUtils.ddlutilsTableComment(ddlVersionBean, 
          Group.TABLE_GROUPER_GROUPS, "holds the groups in the grouper system");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Group.TABLE_GROUPER_GROUPS,  "id", 
          "db id of this group record");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Group.TABLE_GROUPER_GROUPS,   "parent_stem", 
          "uuid of the stem that this group refers to");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Group.TABLE_GROUPER_GROUPS,   "creator_id", 
          "member uuid of the creator of this group");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Group.TABLE_GROUPER_GROUPS,   "create_time", 
          "number of millis since 1970 that this group was created");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Group.TABLE_GROUPER_GROUPS,   "modifier_id", 
          "member uuid of the last modifier of this group");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Group.TABLE_GROUPER_GROUPS,   "modify_time", 
          "number of millis since 1970 that this group was modified");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Group.TABLE_GROUPER_GROUPS, 
        COLUMN_HIBERNATE_VERSION_NUMBER, "hibernate uses this to version rows");

    
    GrouperDdlUtils.ddlutilsTableComment(ddlVersionBean, 
        "grouper_groups_types", "holds the association between group and type");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_groups_types", "id", 
          "id of this group/type record");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_groups_types",  "group_uuid", 
          "group uuid foreign key");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_groups_types",  "type_uuid", 
          "type uuid foreign key");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_groups_types", 
        COLUMN_HIBERNATE_VERSION_NUMBER, "hibernate uses this to version rows");

    
    GrouperDdlUtils.ddlutilsTableComment(ddlVersionBean, 
          Member.TABLE_GROUPER_MEMBERS, "keeps track of subjects used in grouper.  Records are never deleted from this table");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Member.TABLE_GROUPER_MEMBERS,  "id", 
          "db id of this row");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Member.TABLE_GROUPER_MEMBERS, "subject_id", 
          "subject id is the id from the subject source");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Member.TABLE_GROUPER_MEMBERS,  "subject_source", 
          "id of the source from sources.xml");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Member.TABLE_GROUPER_MEMBERS,  "subject_type", 
          "type of subject, e.g. person");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Member.TABLE_GROUPER_MEMBERS, 
        COLUMN_HIBERNATE_VERSION_NUMBER, "hibernate uses this to version rows");

    
    GrouperDdlUtils.ddlutilsTableComment(ddlVersionBean, 
          Membership.TABLE_GROUPER_MEMBERSHIPS, "keeps track of memberships and permissions");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Membership.TABLE_GROUPER_MEMBERSHIPS,  "id", 
          "db id of this row");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Membership.TABLE_GROUPER_MEMBERSHIPS, "owner_id", 
          "group of the membership");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Membership.TABLE_GROUPER_MEMBERSHIPS, "member_id", 
          "member of the memership");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Membership.TABLE_GROUPER_MEMBERSHIPS, Membership.COLUMN_FIELD_ID, 
            "foreign key to field by id");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Membership.TABLE_GROUPER_MEMBERSHIPS, "mship_type", 
          "type of membership, one of the three: immediate, effective, composite");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Membership.TABLE_GROUPER_MEMBERSHIPS, "via_id", 
          "if effective, this is the group above the member in the chain, " +
          "for composite, this is the composite uuid");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Membership.TABLE_GROUPER_MEMBERSHIPS, "depth", 
          "for effective membership, the number of hops (excluding composites) " +
          "along the membership graph");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Membership.TABLE_GROUPER_MEMBERSHIPS, "parent_membership", 
          "for effective membership, uuid of membership record from the group in " +
          "question to the parent group of the member");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Membership.TABLE_GROUPER_MEMBERSHIPS, "creator_id", 
          "member uuid of the creator of this record");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Membership.TABLE_GROUPER_MEMBERSHIPS, "create_time", 
          "number of millis since 1970 that this record was created");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Membership.TABLE_GROUPER_MEMBERSHIPS, 
        COLUMN_HIBERNATE_VERSION_NUMBER, "hibernate uses this to version rows");

    
    GrouperDdlUtils.ddlutilsTableComment(ddlVersionBean,
        Stem.TABLE_GROUPER_STEMS, "entries for stems and their attributes");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Stem.TABLE_GROUPER_STEMS, "id", 
          "db id of this row");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Stem.TABLE_GROUPER_STEMS,  "parent_stem", 
          "stem uuid of parent stem or empty if under root");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Stem.TABLE_GROUPER_STEMS,  "name", 
          "full name (id) path of stem");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Stem.TABLE_GROUPER_STEMS,  "display_name", 
          "full dislpay name path of stem");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Stem.TABLE_GROUPER_STEMS, "creator_id", 
          "member_id of who created this stem");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Stem.TABLE_GROUPER_STEMS,  "create_time", 
          "number of millis since 1970 since this was created");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Stem.TABLE_GROUPER_STEMS,  "modifier_id", 
          "member_id of modifier who last edited");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Stem.TABLE_GROUPER_STEMS,  "modify_time", 
          "number of millis since 1970 since this was edited");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Stem.TABLE_GROUPER_STEMS,  "display_extension", 
          "display extension (not full path) of stem");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Stem.TABLE_GROUPER_STEMS, "extension", 
          "extension (id) (not full path) of this stem");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Stem.TABLE_GROUPER_STEMS,  "description", 
          "description of stem");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, Stem.TABLE_GROUPER_STEMS, 
        COLUMN_HIBERNATE_VERSION_NUMBER, "hibernate uses this to version rows");

    
    GrouperDdlUtils.ddlutilsTableComment(ddlVersionBean,
          "grouper_types", "the various types which can be assigned to groups");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_types",  "id", 
          "db id of this row");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_types",  "name", 
          "name of this type");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_types",  "creator_uuid", 
          "member_id of the creator");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_types",  "create_time", 
          "number of millis since 1970 since this was created");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_types",  "is_assignable", 
          "if this type is assignable (not internal)");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_types",  "is_internal", 
          "if this type if internal (not assignable)");
    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_types", 
        COLUMN_HIBERNATE_VERSION_NUMBER, "hibernate uses this to version rows");

    
    //see if the grouper_ext_loader_log table is there
    GrouperDdlUtils.ddlutilsTableComment(ddlVersionBean, "grouper_loader_log", 
        "log table with a row for each grouper loader job run");
    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "id", "uuid of this log record");
    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "job_name", 
        "Could be group name (friendly) or just config name");
    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "status", 
        "STARTED, RUNNING, SUCCESS, ERROR, WARNING, CONFIG_ERROR");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "started_time", 
        "When the job was started");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "ended_time", 
        "When the job ended (might be blank if daemon died)");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "millis", 
        "Milliseconds this process took");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "millis_get_data", 
        "Milliseconds this process took to get the data from the source");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "millis_load_data", 
        "Milliseconds this process took to load the data to grouper");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "job_type", 
        "GrouperLoaderJobType enum value");
    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "job_schedule_type", 
        "GrouperLoaderJobscheduleType enum value");
    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "job_description", 
        "More information about the job");
    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "job_message", 
        "Could be a status or error message or stack");
    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "host", 
        "Host that this job ran on");
    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "group_uuid", 
        "If this job involves one group, this is uuid");
    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "job_schedule_quartz_cron", 
        "Quartz cron string for this col");
    
    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "job_schedule_interval_seconds", 
        "How many seconds this is supposed to wait between runs");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "last_updated", 
        "When this record was last updated");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "unresolvable_subject_count", 
        "The number of records which were not subject resolvable");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "insert_count", 
        "The number of records inserted");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "update_count", 
        "The number of records updated");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "delete_count", 
        "The number of records deleted");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "total_count", 
        "The total number of records (e.g. total number of members)");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "parent_job_name", 
        "If this job is a subjob of another job, then put the parent job name here");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "parent_job_id", 
        "If this job is a subjob of another job, then put the parent job id here");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "and_group_names", 
        "If this group query is anded with another group or groups, they are listed here comma separated");

    GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, "grouper_loader_log",  "job_schedule_priority", 
        "Priority of this job (5 is unprioritized, higher the better)");


    
    String groupIdCol = "id";
    
    String stemIdCol = "id";
    
    String memberIdCol = "id";
    
    String typeIdCol = "id";
    
    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Attribute.TABLE_GROUPER_ATTRIBUTES, 
        "fk_attributes_group_id", Group.TABLE_GROUPER_GROUPS, "group_id", groupIdCol);
    
    //dont add if just testing
    boolean buildingFieldIds = buildingToVersion > V4.getVersion();
    if (buildingFieldIds) {
      GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Attribute.TABLE_GROUPER_ATTRIBUTES, 
          "fk_attributes_field_id", Field.TABLE_GROUPER_FIELDS, "field_id", "id");
    }

    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Composite.TABLE_GROUPER_COMPOSITES, 
        "fk_composites_owner", Group.TABLE_GROUPER_GROUPS, "owner", groupIdCol);
    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Composite.TABLE_GROUPER_COMPOSITES, 
        "fk_composites_left_factor", Group.TABLE_GROUPER_GROUPS, "left_factor", groupIdCol);
    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Composite.TABLE_GROUPER_COMPOSITES, 
        "fk_composites_right_factor", Group.TABLE_GROUPER_GROUPS, "right_factor", groupIdCol);
    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Composite.TABLE_GROUPER_COMPOSITES, 
        "fk_composites_creator_id", Member.TABLE_GROUPER_MEMBERS, "creator_id", memberIdCol);
    
    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Field.TABLE_GROUPER_FIELDS, 
        "fk_fields_grouptype_uuid", "grouper_types", "grouptype_uuid", typeIdCol);

    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Group.TABLE_GROUPER_GROUPS, 
        "fk_groups_parent_stem", Stem.TABLE_GROUPER_STEMS, "parent_stem", stemIdCol);
    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Group.TABLE_GROUPER_GROUPS, 
        "fk_groups_creator_id", Member.TABLE_GROUPER_MEMBERS, "creator_id", memberIdCol);
    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Group.TABLE_GROUPER_GROUPS, 
        "fk_groups_modifier_id", Member.TABLE_GROUPER_MEMBERS, "modifier_id", memberIdCol);
    
    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, "grouper_groups_types", 
        "fk_groups_types_group_uuid", Group.TABLE_GROUPER_GROUPS, "group_uuid", groupIdCol);
    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, "grouper_groups_types", 
        "fk_groups_types_type_uuid", "grouper_types", "type_uuid", typeIdCol);

    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Membership.TABLE_GROUPER_MEMBERSHIPS, 
        "fk_memberships_member_id", Member.TABLE_GROUPER_MEMBERS, "member_id", memberIdCol);
    if (buildingFieldIds) {

      GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Membership.TABLE_GROUPER_MEMBERSHIPS, 
          "fk_membership_field_id", Field.TABLE_GROUPER_FIELDS, "field_id", "id");
    }
    
    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Membership.TABLE_GROUPER_MEMBERSHIPS, 
        "fk_memberships_parent", Membership.TABLE_GROUPER_MEMBERSHIPS, "parent_membership", "id");
    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Membership.TABLE_GROUPER_MEMBERSHIPS, 
        "fk_memberships_creator_id", Member.TABLE_GROUPER_MEMBERS, "creator_id", memberIdCol);

    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Stem.TABLE_GROUPER_STEMS, 
        "fk_stems_parent_stem", Stem.TABLE_GROUPER_STEMS, "parent_stem", stemIdCol);
    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Stem.TABLE_GROUPER_STEMS, 
        "fk_stems_creator_id", Member.TABLE_GROUPER_MEMBERS, "creator_id", memberIdCol);
    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, Stem.TABLE_GROUPER_STEMS, 
        "fk_stems_modifier_id", Member.TABLE_GROUPER_MEMBERS, "modifier_id", memberIdCol);

    GrouperDdlUtils.ddlutilsFindOrCreateForeignKey(database, "grouper_types", 
        "fk_types_creator_uuid", Member.TABLE_GROUPER_MEMBERS, "creator_uuid", memberIdCol);
  
    //now lets add views
    GrouperDdlUtils.ddlutilsCreateOrReplaceView(ddlVersionBean, "grouper_attributes_v",
        "Join of groups and attributes with friendly names.  Attributes are name/value pairs for groups.  " +
        "Each group type is related to a set of 0 to many attributes, each attribute is related to one group type." +
        "grouper_fields holds each attribute name under the field type of ^attribute^",
        GrouperUtil.toSet("GROUP_NAME", 
            "ATTRIBUTE_NAME", 
            "ATTRIBUTE_VALUE", 
            "GROUP_TYPE_NAME", 
            "FIELD_ID", 
            "ATTRIBUTE_ID", 
            "GROUP_ID", 
            "GROUPTYPE_UUID"),
         GrouperUtil.toSet("Group name is full ip path, e.g. school:stem1:groupId",
             "Attribute name is the name of the name/value pair",
             "Attribute value is the value of the name/value pair",
             "Group_type_name is the name of the group type this attribute is related to",
             "Field_id is the uuid that uniquely identifies a the field",
             "Attribute_id is the uuid that uniquely identifies the pairing of group and attribute",
             "Group_id is the uuid that uniquely identifies a group",
             "GroupType_uuid is the uuid that uniquely identifies a group type"),
            "select  "
            + "(select ga2.value from grouper_attributes ga2 , grouper_fields gf "
            + "where ga2.group_id = gg.id and gf.name = 'name' and gf.id = ga2.field_id) as group_name, "
            + "gf.NAME as attribute_name, "
            + "ga.VALUE as attribute_value, "
            + "gt.NAME as group_type_name, "
            + "ga.FIELD_ID, "
            + "ga.ID as attribute_id, "
            + "gg.ID as group_id, "
            + "gf.grouptype_uuid "
            + "from grouper_attributes ga, grouper_groups gg, grouper_fields gf, grouper_types gt "
            + "where ga.FIELD_ID = gf.ID "
            + "and ga.GROUP_ID = gg.ID and gf.GROUPTYPE_UUID = gt.ID ");

    GrouperDdlUtils.ddlutilsCreateOrReplaceView(ddlVersionBean, "grouper_composites_v", 
        "Grouper_composites_v is a view of composite relationships with friendly names.  A composite" +
        " is a joining of two groups with a group math operator of: union, intersection, or complement.",
        GrouperUtil.toSet("OWNER_GROUP_NAME", 
            "COMPOSITE_TYPE", 
            "LEFT_FACTOR_GROUP_NAME", 
            "RIGHT_FACTOR_GROUP_NAME", 
            "OWNER_GROUP_DISPLAYNAME",
            "LEFT_FACTOR_GROUP_DISPLAYNAME", 
            "RIGHT_FACTOR_GROUP_DISPLAYNAME", 
            "OWNER_GROUP_ID", 
            "LEFT_FACTOR_GROUP_ID", 
            "RIGHT_FACTOR_GROUP_ID",
            "COMPOSITE_ID", 
            "CREATE_TIME", 
            "CREATOR_ID", 
            "HIBERNATE_VERSION_NUMBER"),
        GrouperUtil.toSet("OWNER_GROUP_NAME: Name of the group which is the result of the composite operation, e.g. school:stem1:allPeople",
            "COMPOSITE_TYPE: union (all members), intersection (only members in both), or complement (in first, not in second)", 
            "LEFT_FACTOR_GROUP_NAME: Name of group which is the first of two groups in the composite operation, e.g. school:stem1:part1", 
            "RIGHT_FACTOR_GROUP_NAME: Name of group which is the second of two groups in the composite operation, e.g. school:stem1:part2", 
            "OWNER_GROUP_DISPLAYNAME: Display name of result group of composite operation, e.g. My school:The stem1:All people",
            "LEFT_FACTOR_GROUP_DISPLAYNAME: Display name of group which is the first of two groups in the composite operation, e.g. My school:The stem1:Part 1", 
            "RIGHT_FACTOR_GROUP_DISPLAYNAME: Display name of group which is the second of two groups in the composite operation, e.g. My school:The stem1:Part 1", 
            "OWNER_GROUP_ID: UUID of the result group", 
            "LEFT_FACTOR_GROUP_ID: UUID of the first group of the composite operation", 
            "RIGHT_FACTOR_GROUP_ID: UUID of the second group of the composite operation",
            "COMPOSITE_ID: UUID of the composite relationship among the three groups", 
            "CREATE_TIME: number of millis since 1970 that the composite was created", 
            "CREATOR_ID: member id of the subject that created the composite relationship", 
            "HIBERNATE_VERSION_NUMBER: increments with each update, starts at 0"
        ),
        "select "
        + "(select ga.value from grouper_attributes ga , grouper_fields gf "
        + "where ga.group_id = gc.owner and gf.name = 'name' and gf.id = ga.field_id) as owner_group_name, "
        + "gc.TYPE as composite_type, "
        + "(select ga.value from grouper_attributes ga , grouper_fields gf "
        + "where ga.group_id = gc.left_factor and gf.name = 'name' and gf.id = ga.field_id) as left_factor_group_name, "
        + "(select ga.value from grouper_attributes ga , grouper_fields gf "
        + "where ga.group_id = gc.right_factor and gf.name = 'name' and gf.id = ga.field_id) as right_factor_group_name, "
        + "(select ga.value from grouper_attributes ga , grouper_fields gf "
        + "where ga.group_id = gc.owner and gf.name = 'displayName' and gf.id = ga.field_id) as owner_group_displayname, "
        + "(select ga.value from grouper_attributes ga , grouper_fields gf "
        + "where ga.group_id = gc.left_factor and gf.name = 'displayName' and gf.id = ga.field_id) as left_factor_group_displayname, "
        + "(select ga.value from grouper_attributes ga , grouper_fields gf "
        + "where ga.group_id = gc.right_factor and gf.name = 'displayName' and gf.id = ga.field_id) as right_factor_group_displayname, "
        + "gc.OWNER as owner_group_id, "
        + "gc.LEFT_FACTOR as left_factor_group_id, "
        + "gc.RIGHT_FACTOR as right_factor_group_id, "
        + "gc.ID as composite_id, "
        + "gc.CREATE_TIME, "
        + "gc.CREATOR_ID, "
        + "gc.HIBERNATE_VERSION_NUMBER "
        + "from grouper_composites gc ");
    GrouperDdlUtils.ddlutilsCreateOrReplaceView(ddlVersionBean, "grouper_groups_types_v", 
        "A group can have one or many types associated.  This is a view of those relationships with friendly names",
        GrouperUtil.toSet("GROUP_NAME", 
            "GROUP_DISPLAYNAME", 
            "GROUP_TYPE_NAME", 
            "GROUP_ID", 
            "GROUP_TYPE_UUID", 
            "GROUPER_GROUPS_TYPES_ID", 
            "HIBERNATE_VERSION_NUMBER"),
        GrouperUtil.toSet("GROUP_NAME: name of group which has the type, e.g. school:stem1:theGroup", 
            "GROUP_DISPLAYNAME: display name of the group which has the type, e.g. My school, the stem 1, The group", 
            "GROUP_TYPE_NAME: friendly name of the type, e.g. grouperLoader", 
            "GROUP_ID: uuid unique id of the group which has the type", 
            "GROUP_TYPE_UUID: uuid unique id of the type related to the group", 
            "GROUPER_GROUPS_TYPES_ID: uuid unique id of the relationship between the group and type", 
            "HIBERNATE_VERSION_NUMBER: increments by one with each update, starts at 0"),
            "select  "
            + "(select ga.value from grouper_attributes ga, grouper_fields gf  "
            + "where ga.group_id = ggt.GROUP_UUID and gf.name = 'name' and ga.field_id = gf.id) as group_name, "
            + "(select ga.value from grouper_attributes ga, grouper_fields gf  "
            + "where ga.group_id = ggt.GROUP_UUID and gf.name = 'displayName' and ga.field_id = gf.id) as group_displayname, "
            + "gt.NAME as group_type_name, "
            + "ggt.GROUP_UUID as group_id, "
            + "ggt.TYPE_UUID as group_type_uuid, "
            + "ggt.ID as grouper_groups_types_id, "
            + "ggt.HIBERNATE_VERSION_NUMBER "
            + "from grouper_groups_types ggt, grouper_types gt "
            + "where ggt.TYPE_UUID = gt.ID ");
    GrouperDdlUtils.ddlutilsCreateOrReplaceView(ddlVersionBean, "grouper_groups_v", 
        "Contains one record for each group, with friendly names for some attributes and some more information",
        GrouperUtil.toSet("EXTENSION", 
            "NAME", 
            "DISPLAY_EXTENSION", 
            "DISPLAY_NAME", 
            "DESCRIPTION", 
            "PARENT_STEM_NAME", 
            "GROUP_ID", 
            "PARENT_STEM_ID", 
            "MODIFIER_SOURCE", 
            "MODIFIER_SUBJECT_ID", 
            "CREATOR_SOURCE", 
            "CREATOR_SUBJECT_ID", 
            "IS_COMPOSITE_OWNER", 
            "IS_COMPOSITE_FACTOR", 
            "CREATOR_ID", 
            "CREATE_TIME", 
            "MODIFIER_ID", 
            "MODIFY_TIME", 
            "HIBERNATE_VERSION_NUMBER"),
        GrouperUtil.toSet("EXTENSION: part of group name not including path information, e.g. theGroup", 
            "NAME: name of the group, e.g. school:stem1:theGroup", 
            "DISPLAY_EXTENSION: name for display of the group, e.g. My school:The stem 1:The group", 
            "DISPLAY_NAME: name for display of the group without any path information, e.g. The group", 
            "DESCRIPTION: contains user entered information about the group e.g. why it exists", 
            "PARENT_STEM_NAME: name of the stem this group is in, e.g. school:stem1", 
            "GROUP_ID: uuid unique id of the group", 
            "PARENT_STEM_ID: uuid unique id of the stem this group is in", 
            "MODIFIER_SOURCE: source name of the subject who last modified this group, e.g. schoolPersonSource", 
            "MODIFIER_SUBJECT_ID: subject id of the subject who last modified this group, e.g. 12345", 
            "CREATOR_SOURCE: source name of the subject who created this group, e.g. schoolPersonSource", 
            "CREATOR_SUBJECT_ID: subject id of the subject who created this group, e.g. 12345", 
            "IS_COMPOSITE_OWNER: T if this is a result of a composite operation (union, intersection, complement), or blank if not", 
            "IS_COMPOSITE_FACTOR: T if this is a member of a composite operation, e.g. one of the grouper being unioned, intersected, or complemeneted", 
            "CREATOR_ID: member id of the subject who created this group, foreign key to grouper_members", 
            "CREATE_TIME: number of millis since 1970 since this group was created", 
            "MODIFIER_ID: member id of the subject who last modified this group, foreign key to grouper_members", 
            "MODIFY_TIME: number of millis since 1970 since this group was last changed", 
            "HIBERNATE_VERSION_NUMBER: increments by 1 for each update"),
            "select  "
            + "(select ga.value from grouper_attributes ga, grouper_fields gf "
            + "where ga.group_id = gg.id and gf.name = 'extension' and gf.id = ga.field_id) as extension, "
            + "(select ga.value from grouper_attributes ga , grouper_fields gf "
            + "where ga.group_id = gg.id and gf.name = 'name' and gf.id = ga.field_id) as name, "
            + "(select ga.value from grouper_attributes ga , grouper_fields gf "
            + "where ga.group_id = gg.id and gf.name = 'displayExtension' and gf.id = ga.field_id) as display_extension, "
            + "    (select ga.value from grouper_attributes ga , grouper_fields gf "
            + "where ga.group_id = gg.id and gf.name = 'displayName' and gf.id = ga.field_id) as display_name, "
            + "(select ga.value from grouper_attributes ga, grouper_fields gf "
            + "where ga.group_id = gg.id and gf.name = 'description' and gf.id = ga.field_id) as description, "
            + "gs.NAME as parent_stem_name, "
            + "gg.id as group_id, "
            + "gs.ID as parent_stem_id, "
            + "(select gm.SUBJECT_SOURCE from grouper_members gm where gm.ID = gg.MODIFIER_ID) as modifier_source, "
            + "(select gm.SUBJECT_ID from grouper_members gm where gm.ID = gg.MODIFIER_ID) as modifier_subject_id, "
            + "(select gm.SUBJECT_SOURCE from grouper_members gm where gm.ID = gg.CREATOR_ID) as creator_source, "
            + "(select gm.SUBJECT_ID from grouper_members gm where gm.ID = gg.CREATOR_ID) as creator_subject_id, "
            + "(select distinct 'T' from grouper_composites gc where gc.OWNER = gg.ID) as is_composite_owner, "
            + "(select distinct 'T' from grouper_composites gc where gc.LEFT_FACTOR = gg.ID or gc.right_factor = gg.id) as is_composite_factor, "
            + "gg.CREATOR_ID, "
            + "gg.CREATE_TIME, "
            + "gg.MODIFIER_ID, "
            + "gg.MODIFY_TIME, "
            + "gg.HIBERNATE_VERSION_NUMBER  "
            + " from grouper_groups gg, grouper_stems gs where gg.PARENT_STEM = gs.ID ");
    GrouperDdlUtils.ddlutilsCreateOrReplaceView(ddlVersionBean, "grouper_memberships_v", 
        "Grouper_memberships_v holds one record for each membership or privilege in the system for members to groups or stems (for privileges).  This is denormalized so there are records for the actual immediate relationships, and the cascaded effective relationships.  This has friendly names.",
        GrouperUtil.toSet("GROUP_NAME", 
            "GROUP_DISPLAYNAME", 
            "STEM_NAME", 
            "STEM_DISPLAYNAME", 
            "SUBJECT_ID", 
            "SUBJECT_SOURCE", 
            "LIST_TYPE", 
            "LIST_NAME", 
            "MEMBERSHIP_TYPE", 
            "COMPOSITE_PARENT_GROUP_NAME", 
            "DEPTH", 
            "CREATOR_SOURCE", 
            "CREATOR_SUBJECT_ID", 
            "MEMBERSHIP_ID", 
            "PARENT_MEMBERSHIP_ID", 
            "STEM_ID", 
            "GROUP_ID", 
            "GROUP_OR_STEM_ID", 
            "CREATE_TIME", 
            "CREATOR_ID", 
            "FIELD_ID"),
        GrouperUtil.toSet("GROUP_NAME: name of the group if this is a group membership, e.g. school:stem1:theGroup", 
            "GROUP_DISPLAYNAME: display name of the group if this is a group membership, e.g. My school:The stem1:The group", 
            "STEM_NAME: name of the stem if this is a stem privilege, e.g. school:stem1", 
            "STEM_DISPLAYNAME: display name of the stems if this is a stem privilege, e.g. My school:The stem1", 
            "SUBJECT_ID: e.g. a school id of a person in the membership e.g. 12345", 
            "SUBJECT_SOURCE: source where the subject in the membership is from e.g. mySchoolPeople", 
            "LIST_TYPE: list: members of a group, access: privilege of a group, naming: privilege of a stem", 
            "LIST_NAME: subset of list type.  which list if a list membership.  which privilege if a privilege.  e.g. members", 
            "MEMBERSHIP_TYPE: either immediate (direct membership or privilege), of effective (membership due to a composite or a group being a member of another group)", 
            "COMPOSITE_PARENT_GROUP_NAME: name of group if this membership relates to a composite relationship, e.g. school:stem:allStudents", 
            "DEPTH: 0 for composite, if not then it is the 0 indexed count of number of group hops between member and group", 
            "CREATOR_SOURCE: subject source where the creator of the group is from", 
            "CREATOR_SUBJECT_ID: subject id of the creator of the group, e.g. 12345", 
            "MEMBERSHIP_ID: uuid unique id of this membership", 
            "PARENT_MEMBERSHIP_ID: if this is an effective membership, then this is the membership_uuid of the cause of this membership", 
            "STEM_ID: if this is a stem privilege, this is the stem uuid unique id", 
            "GROUP_ID: if this is a group list or privilege, this is the group uuid unique id", 
            "GROUP_OR_STEM_ID: stem_id if stem, group_id if group", 
            "CREATE_TIME: number of millis since 1970 since this membership was created", 
            "CREATOR_ID: member_id of the creator, foreign key into grouper_members", 
            "FIELD_ID: uuid unique id of the field.  foreign key to grouper_fields.  This represents the list_type and list_name"),
            "select "
            + "(select ga.value from grouper_attributes ga, grouper_fields gf  "
            + "where ga.group_id = gms.owner_id and gf.name = 'name' and ga.field_id = gf.id) as group_name, "
            + "(select ga.value from grouper_attributes ga, grouper_fields gf  "
            + "where ga.group_id = gms.owner_id and gf.name = 'displayName' and ga.field_id = gf.id) as group_displayname, "
            + "(select gs.NAME from grouper_stems gs "
            + "where gs.ID = gms.owner_id) as stem_name, "
            + "(select gs.display_NAME from grouper_stems gs "
            + "where gs.ID = gms.owner_id) as stem_displayname, "
            + "gm.SUBJECT_ID, gm.subject_source, "
            + "gf.TYPE as list_type, "
            + "gf.NAME as list_name, "
            + "gms.MSHIP_TYPE as membership_type, "
            + "(select ga.value from grouper_attributes ga, grouper_composites gc, grouper_fields gf "
            + "where gc.id = gms.VIA_ID and ga.group_id = gc.OWNER and gf.name = 'name' and ga.field_id = gf.id) as composite_parent_group_name, "
            + "depth,  "
            + "(select gm.SUBJECT_SOURCE from grouper_members gm where gm.ID = gms.creator_ID) as creator_source, "
            + "(select gm.SUBJECT_ID from grouper_members gm where gm.ID = gms.creator_ID) as creator_subject_id, "
            + "gms.id as membership_id,  "
            + "gms.PARENT_MEMBERSHIP as parent_membership_id, "
            + "(select gs.id from grouper_stems gs where gs.ID = gms.owner_id) as stem_id, "
            + "(select gg.id from grouper_groups gg where gg.id = gms.owner_id) as group_id, "
            + "gms.OWNER_ID as group_or_stem_id, "
            + "gms.CREATE_TIME, "
            + "gms.CREATOR_ID, "
            + "gms.FIELD_ID "
            + " from grouper_memberships gms, grouper_members gm, grouper_fields gf "
            + " where gms.MEMBER_ID = gm.ID and gms.field_id = gf.id ");
    GrouperDdlUtils.ddlutilsCreateOrReplaceView(ddlVersionBean, "grouper_stems_v",
        "GROUPER_STEMS_V: holds one record for each stem (folder) in grouper, with friendly names",
        GrouperUtil.toSet("EXTENSION", 
            "NAME", 
            "DISPLAY_EXTENSION", 
            "DISPLAY_NAME", 
            "DESCRIPTION",
            "PARENT_STEM_NAME", 
            "PARENT_STEM_DISPLAYNAME", 
            "CREATOR_SOURCE", 
            "CREATOR_SUBJECT_ID", 
            "MODIFIER_SOURCE",
            "MODIFIER_SUBJECT_ID", 
            "CREATE_TIME", 
            "CREATOR_ID", 
            "STEM_ID", 
            "MODIFIER_ID",
            "MODIFY_TIME", 
            "PARENT_STEM", 
            "HIBERNATE_VERSION_NUMBER"),
        GrouperUtil.toSet("EXTENSION: name of the stem without the parent stem names, e.g. stem1", 
            "NAME: name of the stem including parent stem names, e.g. school:stem1", 
            "DISPLAY_EXTENSION: display name of the stem without parent stem names, e.g. The stem 1", 
            "DISPLAY_NAME: display name of the stem including parent stem names, e.g. My school: The stem 1", 
            "DESCRIPTION: description entered in about the stem, for example including why the stem exists and who has access",
            "PARENT_STEM_NAME: name of the stem (folder) that this stem is in.  e.g. school", 
            "PARENT_STEM_DISPLAYNAME: display name of the stem (folder) that this stem is in.  e.g. My school", 
            "CREATOR_SOURCE: subject source where the subject that created this stem is from, e.g. mySchoolPeople", 
            "CREATOR_SUBJECT_ID: e.g. the school id of the subject that created this stem, e.g. 12345", 
            "MODIFIER_SOURCE: subject source where the subject that last modified this stem is from, e.g. mySchoolPeople",
            "MODIFIER_SUBJECT_ID: e.g. the school id of the subject who last modified this stem, e.g. 12345", 
            "CREATE_TIME: number of millis since 1970 that this stem was created", 
            "CREATOR_ID: member id of the subject who created this stem, foreign key to grouper_members", 
            "STEM_ID: uuid unique id of this stem", 
            "MODIFIER_ID: member id of the subject who last modified this stem, foreign key to grouper_members",
            "MODIFY_TIME: number of millis since 1970 since this stem was last modified", 
            "PARENT_STEM: stem_id uuid unique id of the stem (folder) that this stem is in", 
            "HIBERNATE_VERSION_NUMBER: increments by one for each update from hibernate"),
         "select gs.extension, gs.NAME, "
            + "gs.DISPLAY_EXTENSION, gs.DISPLAY_NAME, gs.DESCRIPTION, "
            + "(select gs_parent.NAME from grouper_stems gs_parent where gs_parent.id = gs.PARENT_STEM) as parent_stem_name, "
            + "(select gs_parent.DISPLAY_NAME from grouper_stems gs_parent where gs_parent.id = gs.PARENT_STEM) as parent_stem_displayname, "
            + "(select gm.SUBJECT_SOURCE from grouper_members gm where gm.ID = gs.creator_ID) as creator_source, "
            + "(select gm.SUBJECT_ID from grouper_members gm where gm.ID = gs.creator_ID) as creator_subject_id, "
            + "(select gm.SUBJECT_SOURCE from grouper_members gm where gm.ID = gs.MODIFIER_ID) as modifier_source, "
            + "(select gm.SUBJECT_ID from grouper_members gm where gm.ID = gs.MODIFIER_ID) as modifier_subject_id, "
            + "gs.CREATE_TIME, gs.CREATOR_ID,  "
            + "gs.ID as stem_id, gs.MODIFIER_ID, gs.MODIFY_TIME, gs.PARENT_STEM, gs.HIBERNATE_VERSION_NUMBER "
            + "from grouper_stems gs ");
    GrouperDdlUtils.ddlutilsCreateOrReplaceView(ddlVersionBean, "grouper_rpt_attributes_v", 
        "GROUPER_RPT_ATTRIBUTES_V: report on attributes, how many groups use each attribute",
        GrouperUtil.toSet("ATTRIBUTE_NAME", 
            "GROUP_COUNT", 
            "GROUP_TYPE_NAME", 
            "FIELD_ID", 
            "GROUP_TYPE_ID"),
        GrouperUtil.toSet("ATTRIBUTE_NAME: friendly name of the attribute which is actually from grouper_fields", 
            "GROUP_COUNT: number of groups which define this attribute", 
            "GROUP_TYPE_NAME: group type which owns this attribute", 
            "FIELD_ID: uuid unique id of this field (attribute), foreign key from grouper_attributes to grouper_fields", 
            "GROUP_TYPE_ID: uuid unique id of the group type.  foreign key from grouper_fields to grouper_types"),
        "select gf.NAME as attribute_name,  "
        + "(select count(*) from grouper_attributes ga where ga.FIELD_ID = gf.id) as group_count,   "
        + "gt.NAME as group_type_name, "
        + "gf.ID as field_id, "
        + "gt.ID as group_type_id "
        + "from grouper_fields gf, grouper_types gt "
        + "where gf.TYPE = 'attribute' and gf.GROUPTYPE_UUID = gt.ID ");
    GrouperDdlUtils.ddlutilsCreateOrReplaceView(ddlVersionBean, "grouper_rpt_composites_v", 
        "GROUPER_RPT_COMPOSITES_V: report on the three composite types: union, intersection, complement and how many of each exist",
        GrouperUtil.toSet("COMPOSITE_TYPE", 
            "THE_COUNT"),
        GrouperUtil.toSet("COMPOSITE_TYPE: either union: all members from both factors, intersection: only members in both factors, complement: members in first but not second factor", 
            "THE_COUNT: nubmer of composites of this type in the system"),
        "select gc.TYPE as composite_type, count(*) as the_count " 
        + "from grouper_composites gc group by gc.type ");
    GrouperDdlUtils.ddlutilsCreateOrReplaceView(ddlVersionBean, "grouper_rpt_group_field_v", 
        "GROUPER_RPT_GROUP_FIELD_V: report on how many unique members are in each group based on field (or list) name and type",
        GrouperUtil.toSet("GROUP_NAME", 
            "GROUP_DISPLAYNAME", 
            "FIELD_TYPE", 
            "FIELD_NAME", 
            "MEMBER_COUNT"),
        GrouperUtil.toSet("GROUP_NAME: name of the group where the list and members are, e.g. school:stem1:myGroup", 
            "GROUP_DISPLAYNAME: display name of the group where the list and members are, e.g. My school:The stem1:My group", 
            "FIELD_TYPE: membership field type, e.g. list or access", 
            "FIELD_NAME: membership field name, e.g. members, admins, readers", 
            "MEMBER_COUNT: number of unique members in the group/field"),
        "select ga.value as group_name, ga2.value as group_displayName, "
        + "gf.type as field_type, gf.name as field_name, count(distinct gms.member_id) as member_count "
        + "from grouper_memberships gms, grouper_fields gf, grouper_attributes ga, grouper_fields gaf,  "
        + "grouper_attributes ga2, grouper_fields gaf2 "
        + "where gms.FIELD_ID = gf.ID "
        + "and ga.FIELD_ID = gaf.id "
        + "and gaf.name = 'name' "
        + "and ga.group_id = gms.OWNER_ID "
        + "and ga2.group_id = gms.owner_id "
        + "and ga2.field_id = gaf2.id "
        + "and gaf2.name = 'displayName' "
        + "group by ga.value, ga2.value, gf.type, gf.name ");
    GrouperDdlUtils.ddlutilsCreateOrReplaceView(ddlVersionBean, "grouper_rpt_groups_v", 
        "GROUPER_RPT_GROUPS_V: report with a line for each group and some counts of immediate and effective members etc",
        GrouperUtil.toSet("GROUP_NAME", 
            "GROUP_DISPLAYNAME", 
            "IMMEDIATE_MEMBERSHIP_COUNT", 
            "MEMBERSHIP_COUNT", 
            "ATTRIBUTE_COUNT", 
            "GROUPS_TYPES_COUNT", 
            "ISA_COMPOSITE_FACTOR_COUNT", 
            "ISA_MEMBER_COUNT", 
            "GROUP_ID"),  
        GrouperUtil.toSet("GROUP_NAME: name of group which has the stats, e.g. school:stem1:theGroup", 
            "GROUP_DISPLAYNAME: display name of the group which has the stats, e.g. My school:The stem1:The group", 
            "IMMEDIATE_MEMBERSHIP_COUNT: number of unique immediate members, directly assigned to this group", 
            "MEMBERSHIP_COUNT: total number of unique members, immediate or effective", 
            "ATTRIBUTE_COUNT: number of attributes defined for this group", 
            "GROUPS_TYPES_COUNT: number of group types associated with this group", 
            "ISA_COMPOSITE_FACTOR_COUNT: number of composites this group is a factor of", 
            "ISA_MEMBER_COUNT: number of groups this group is an immediate or effective member of", 
            "GROUP_ID: uuid unique id of this group"),  
        "select  "
        + "(select ga.value from grouper_attributes ga , grouper_fields gf "
        + "where ga.group_id = gg.id and gf.name = 'name' and gf.id = ga.field_id) as group_name, "
        + "    (select ga.value from grouper_attributes ga , grouper_fields gf "
        + "where ga.group_id = gg.id and gf.name = 'displayName' and gf.id = ga.field_id) as group_displayname, "
        + "(select count(distinct gms.MEMBER_ID) from grouper_memberships gms where gms.OWNER_ID = gg.id and gms.MSHIP_TYPE = 'immediate') as immediate_membership_count, "
        + "(select count(distinct gms.MEMBER_ID) from grouper_memberships gms where gms.OWNER_ID = gg.id) as membership_count, "
        + "(select count(*) from grouper_attributes ga where ga.GROUP_ID = gg.id) as attribute_count, "
        + "(select count(*) from grouper_groups_types ggt where ggt.GROUP_UUID = gg.id) as groups_types_count, "
        + "(select count(*) from grouper_composites gc where gc.LEFT_FACTOR = gg.id or gc.RIGHT_FACTOR = gg.id) as isa_composite_factor_count, "
        + "(select count(distinct gms.OWNER_ID) from grouper_memberships gms, grouper_members gm where gm.SUBJECT_ID = gg.ID and gms.MEMBER_ID = gm.ID ) as isa_member_count, "
        + "gg.ID as group_id "
        + "from grouper_groups gg ");
    GrouperDdlUtils.ddlutilsCreateOrReplaceView(ddlVersionBean, "grouper_rpt_members_v", 
        "GROUPER_RPT_MEMBERS_V: report for each member in grouper_members and some stats like how many groups they are in",
        GrouperUtil.toSet("SUBJECT_ID", 
            "SUBJECT_SOURCE", 
            "MEMBERSHIP_COUNT", 
            "MEMBER_ID"), 
        GrouperUtil.toSet("SUBJECT_ID: e.g. the school person id of the person e.g. 12345", 
            "SUBJECT_SOURCE: subject source where the subject is from, e.g. schoolAllPeople", 
            "MEMBERSHIP_COUNT: number of distinct groups or stems this member has a membership with", 
            "MEMBER_ID: uuid unique id of the member in grouper_members"), 
            "select gm.SUBJECT_ID, gm.SUBJECT_SOURCE, "
            + "(select count(distinct gms.owner_id) from grouper_memberships gms where gms.MEMBER_ID = gm.ID) as membership_count, "
            + "gm.ID as member_id "
            + "from grouper_members gm ");
    GrouperDdlUtils.ddlutilsCreateOrReplaceView(ddlVersionBean, "grouper_rpt_stems_v", 
        "GROUPER_RPT_STEMS_V: report with a row for each stem and stats on many groups or members are inside",
        GrouperUtil.toSet("STEM_NAME", 
            "STEM_DISPLAYNAME", 
            "GROUP_IMMEDIATE_COUNT", 
            "STEM_IMMEDIATE_COUNT", 
            "GROUP_COUNT",
            "STEM_COUNT", 
            "THIS_STEM_MEMBERSHIP_COUNT", 
            "CHILD_GROUP_MEMBERSHIP_COUNT", 
            "GROUP_MEMBERSHIP_COUNT", 
            "STEM_ID"), 
        GrouperUtil.toSet("STEM_NAME: name of the stem in report, e.g. school:stem1", 
            "STEM_DISPLAYNAME: display name of the stem in report, e.g. My school:The stem 1", 
            "GROUP_IMMEDIATE_COUNT: number of groups directly inside this stem", 
            "STEM_IMMEDIATE_COUNT: number of stems directly inside this stem", 
            "GROUP_COUNT: number of groups inside this stem, or in a stem inside this stem etc",
            "STEM_COUNT: number of stems inside this stem or in a stem inside this stem etc", 
            "THIS_STEM_MEMBERSHIP_COUNT: number of access memberships related to this stem (e.g. how many people can create groups/stems inside)", 
            "CHILD_GROUP_MEMBERSHIP_COUNT: number of memberships in groups immediately in this stem", 
            "GROUP_MEMBERSHIP_COUNT: number of memberships in groups in this stem or in stems in this stem etc", 
            "STEM_ID: uuid unique id of this stem"), 
            "select gs.NAME as stem_name, gs.DISPLAY_NAME as stem_displayname, "
            + "(select count(*) from grouper_groups gg where gg.PARENT_STEM = gs.ID) as group_immediate_count, "
            + "(select count(*) from grouper_stems gs2 where gs.id = gs2.PARENT_STEM ) as stem_immediate_count, "
            + "(select count(*) from grouper_attributes ga, grouper_fields gf where ga.FIELD_ID = gf.ID and gf.NAME = 'name' and ga.value like gs.NAME || '%') as group_count, "
            + "(select count(*) from grouper_stems gs2 where gs2.name like gs.NAME || '%') as stem_count, "
            + "(select count(distinct gm.member_id) from grouper_memberships gm where gm.OWNER_ID = gs.id) as this_stem_membership_count,  "
            + "(select count(distinct gm.member_id) from grouper_memberships gm, grouper_groups gg where gg.parent_stem = gs.id and gm.OWNER_ID = gg.id) as child_group_membership_count,  "
            + "(select count(distinct gm.member_id) from grouper_memberships gm, grouper_attributes ga, grouper_fields gf where gm.owner_id = ga.group_id and ga.FIELD_ID = gf.ID and gf.NAME = 'name' and ga.value like gs.NAME || '%') as group_membership_count, "
            + "gs.ID as stem_id "
            + "from grouper_stems gs ");
    GrouperDdlUtils.ddlutilsCreateOrReplaceView(ddlVersionBean, "grouper_rpt_types_v", 
        "GROUPER_RPT_TYPES_V: report on group types and how many groups have that type",
        GrouperUtil.toSet("GROUP_TYPE_NAME", 
            "GROUP_COUNT", 
            "GROUP_TYPE_ID"),
        GrouperUtil.toSet("GROUP_TYPE_NAME: friendly name of this group type", 
            "GROUP_COUNT: number of groups that have this group type", 
            "GROUP_TYPE_ID: uuid unique id of this group type"),
        "select gt.NAME as group_type_name, "
        + "(select count(*) from grouper_groups_types ggt where ggt.TYPE_UUID = gt.ID) as group_count, "
        + "gt.id as group_type_id "
        + "from grouper_types gt ");


    
  }

  /**
   * an example table name so we can hone in on the exact metadata
   * @return the table name
   */
  public String[] getSampleTablenames() {
    return new String[]{"grouper_groups", "grouper_ddl", "grouper_attributes", "grouper_composites",
        "grouper_fields", "grouper_groups_types", "grouper_loader_log", "grouper_members", "grouper_memberships", 
        "grouper_stems", "grouper_types"};
  }
  
  /**
   * @param database
   * @param ddlVersionBean 
   * @param attributeTable
   */
  private static void addAttributeFieldIndexes(Database database, DdlVersionBean ddlVersionBean,  Table attributeTable) {
    GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, attributeTable.getName(), "attribute_uniq_idx", true, "group_id", Attribute.COLUMN_FIELD_ID);
    
    //see if we have a custom script here, do this since some versions of mysql cant handle indexes on columns that large
    String scriptOverride = ddlVersionBean.isMysql() ? "\nCREATE INDEX attribute_field_value_idx " +
        "ON grouper_attributes (field_id, value(333));\n" : null;
    GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, ddlVersionBean, attributeTable.getName(), "attribute_field_value_idx", 
        scriptOverride, false, Attribute.COLUMN_FIELD_ID, "value");
  }

  /**
   * @param database
   * @param membershipsTable
   */
  private static void addMembershipFieldIndexes(Database database, Table membershipsTable) {
    GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membershipsTable.getName(), 
        "membership_member_list_idx", false, "member_id", "field_id");

    GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membershipsTable.getName(), 
        "membership_owner_field_type_idx", false, "owner_id", "field_id", "mship_type");

    GrouperDdlUtils.ddlutilsFindOrCreateIndex(database, membershipsTable.getName(), 
        "membership_owner_member_idx", false, "owner_id", "member_id",
        "field_id", "depth");
  }

}