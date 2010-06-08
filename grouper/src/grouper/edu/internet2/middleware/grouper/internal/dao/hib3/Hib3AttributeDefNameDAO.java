package edu.internet2.middleware.grouper.internal.dao.hib3;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.attr.AttributeDef;
import edu.internet2.middleware.grouper.attr.AttributeDefName;
import edu.internet2.middleware.grouper.attr.finder.AttributeDefFinder;
import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.exception.AttributeDefNameNotFoundException;
import edu.internet2.middleware.grouper.exception.AttributeDefNameTooManyResults;
import edu.internet2.middleware.grouper.exception.AttributeDefNotFoundException;
import edu.internet2.middleware.grouper.exception.GroupNotFoundException;
import edu.internet2.middleware.grouper.hibernate.AuditControl;
import edu.internet2.middleware.grouper.hibernate.ByHqlStatic;
import edu.internet2.middleware.grouper.hibernate.GrouperTransactionType;
import edu.internet2.middleware.grouper.hibernate.HibUtils;
import edu.internet2.middleware.grouper.hibernate.HibernateHandler;
import edu.internet2.middleware.grouper.hibernate.HibernateHandlerBean;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouper.internal.dao.AttributeDefNameDAO;
import edu.internet2.middleware.grouper.internal.dao.GrouperDAOException;
import edu.internet2.middleware.grouper.internal.dao.QueryOptions;
import edu.internet2.middleware.grouper.misc.GrouperDAOFactory;
import edu.internet2.middleware.grouper.privs.AttributeDefPrivilege;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.subject.Subject;

/**
 * Data Access Object for attribute def name
 * @author  mchyzer
 * @version $Id: Hib3AttributeDefNameDAO.java,v 1.6 2009-11-17 02:52:29 mchyzer Exp $
 */
public class Hib3AttributeDefNameDAO extends Hib3DAO implements AttributeDefNameDAO {
  
  /**
   * 
   */
  @SuppressWarnings("unused")
  private static final String KLASS = Hib3AttributeDefNameDAO.class.getName();

  /**
   * reset the attribute def names
   * @param hibernateSession
   */
  static void reset(HibernateSession hibernateSession) {
    hibernateSession.byHql().createQuery("delete from AttributeDefName").executeUpdate();
  }

  /**
   * 
   * @see edu.internet2.middleware.grouper.internal.dao.AttributeDefNameDAO#findByIdSecure(java.lang.String, boolean)
   */
  public AttributeDefName findByIdSecure(String id, boolean exceptionIfNotFound) {
    AttributeDefName attributeDefName = HibernateSession.byHqlStatic().createQuery(
        "from AttributeDefName where id = :theId")
      .setString("theId", id).uniqueResult(AttributeDefName.class);
    
    attributeDefName = filterSecurity(attributeDefName);
    
    if (attributeDefName == null && exceptionIfNotFound) {
      throw new AttributeDefNotFoundException("Cant find (or not allowed to find) attribute def name by id: " + id);
    }
    return attributeDefName;
  }

  /**
   * 
   * @see edu.internet2.middleware.grouper.internal.dao.AttributeDefNameDAO#saveOrUpdate(edu.internet2.middleware.grouper.attr.AttributeDefName)
   */
  public void saveOrUpdate(AttributeDefName attributeDefName) {
    HibernateSession.byObjectStatic().saveOrUpdate(attributeDefName);
  }
  /**
   * make sure grouper session can view the attribute def Name
   * @param attributeDefNames
   * @return the set of attribute def Names
   */
  static Set<AttributeDefName> filterSecurity(Set<AttributeDefName> attributeDefNames) {
    Set<AttributeDefName> result = new LinkedHashSet<AttributeDefName>();
    if (attributeDefNames != null) {
      for (AttributeDefName attributeDefName : attributeDefNames) {
        attributeDefName = filterSecurity(attributeDefName);
        if (attributeDefName != null) {
          result.add(attributeDefName);
        }
      }
    }
    return result;
  }
  
  /**
   * make sure grouper session can view the attribute def Name
   * @param attributeDefName
   * @return the attributeDefName or null
   */
  static AttributeDefName filterSecurity(AttributeDefName attributeDefName) {
    if (attributeDefName == null) {
      return null;
    }
    
    AttributeDef attributeDef = AttributeDefFinder.findById(attributeDefName.getAttributeDefId(), false);
    return attributeDef == null ? null : attributeDefName;
  }

  /**
   * 
   * @see edu.internet2.middleware.grouper.internal.dao.AttributeDefNameDAO#findByNameSecure(java.lang.String, boolean)
   */
  public AttributeDefName findByNameSecure(String name, boolean exceptionIfNotFound)
      throws GrouperDAOException, AttributeDefNameNotFoundException {
    AttributeDefName attributeDefName = HibernateSession.byHqlStatic()
      .createQuery("select a from AttributeDefName as a where a.nameDb = :value")
      .setCacheable(false)
      .setCacheRegion(KLASS + ".FindByName")
      .setString("value", name).uniqueResult(AttributeDefName.class);

    attributeDefName = filterSecurity(attributeDefName);

    //handle exceptions out of data access method...
    if (attributeDefName == null && exceptionIfNotFound) {
      throw new AttributeDefNotFoundException("Cannot find (or not allowed to find) attribute def name with name: '" + name + "'");
    }
    return attributeDefName;
  }

  /**
   * @see edu.internet2.middleware.grouper.internal.dao.AttributeDefNameDAO#delete(AttributeDefName)
   */
  public void delete(final AttributeDefName attributeDefName) {

    HibernateSession.callbackHibernateSession(GrouperTransactionType.READ_WRITE_OR_USE_EXISTING, 
        AuditControl.WILL_NOT_AUDIT, new HibernateHandler() {

          public Object callback(HibernateHandlerBean hibernateHandlerBean)
              throws GrouperDAOException {

            hibernateHandlerBean.getHibernateSession().setCachingEnabled(false);

            //set parent to null so mysql doest get mad
            //http://bugs.mysql.com/bug.php?id=15746
            // delete group sets
            GrouperDAOFactory.getFactory().getAttributeDefNameSet().deleteByIfHasAttributeDefName(attributeDefName);
            hibernateHandlerBean.getHibernateSession().byObject().delete(attributeDefName);
            return null;

          }
      
    });

  }
  

  /**
   * @see edu.internet2.middleware.grouper.internal.dao.AttributeDefNameDAO#findByStem(java.lang.String)
   */
  public Set<AttributeDefName> findByStem(String id) {
    Set<AttributeDefName> attributeDefNames = HibernateSession.byHqlStatic()
        .createQuery("from AttributeDefName where stemId = :id")
        .setCacheable(false)
        .setCacheRegion(KLASS + ".FindByStem")
        .setString("id", id)
        .listSet(AttributeDefName.class);
    
    return attributeDefNames;
  }

  /**
   * @see edu.internet2.middleware.grouper.internal.dao.AttributeDefNameDAO#findByUuidOrName(java.lang.String, java.lang.String, boolean)
   */
  public AttributeDefName findByUuidOrName(String id, String name,
      boolean exceptionIfNotFound) {
    try {
      AttributeDefName attributeDefName = HibernateSession.byHqlStatic()
        .createQuery("from AttributeDefName as theAttributeDefName where theAttributeDefName.id = :theId or theAttributeDefName.nameDb = :theName")
        .setCacheable(true)
        .setCacheRegion(KLASS + ".FindByUuidOrName")
        .setString("theId", id)
        .setString("theName", name)
        .uniqueResult(AttributeDefName.class);
      if (attributeDefName == null && exceptionIfNotFound) {
        throw new GroupNotFoundException("Can't find attributeDefName by id: '" + id + "' or name '" + name + "'");
      }
      return attributeDefName;
    }
    catch (GrouperDAOException e) {
      String error = "Problem finding attributeDefName by id: '" 
        + id + "' or name '" + name + "', " + e.getMessage();
      throw new GrouperDAOException( error, e );
    }
  }

  /**
   * 
   * @see edu.internet2.middleware.grouper.internal.dao.AttributeDefNameDAO#saveUpdateProperties(edu.internet2.middleware.grouper.attr.AttributeDefName)
   */
  public void saveUpdateProperties(AttributeDefName attributeDefName) {
    //run an update statement since the business methods affect these properties
    HibernateSession.byHqlStatic().createQuery("update AttributeDefName " +
        "set hibernateVersionNumber = :theHibernateVersionNumber, " +
        "contextId = :theContextId, " +
        "createdOnDb = :theCreatedOnDb " +
        "where id = :theId")
        .setLong("theHibernateVersionNumber", attributeDefName.getHibernateVersionNumber())
        .setLong("theCreatedOnDb", attributeDefName.getCreatedOnDb())
        .setString("theContextId", attributeDefName.getContextId())
        .setString("theId", attributeDefName.getId()).executeUpdate();
  }

  /**
   * @see edu.internet2.middleware.grouper.internal.dao.AttributeDefNameDAO#findByAttributeDef(java.lang.String)
   */
  public Set<AttributeDefName> findByAttributeDef(String id) {
    Set<AttributeDefName> attributeDefNames = HibernateSession.byHqlStatic()
      .createQuery("from AttributeDefName where attributeDefId = :id order by name")
      .setCacheable(false)
      .setCacheRegion(KLASS + ".FindByAttributeDef")
      .setString("id", id)
      .listSet(AttributeDefName.class);
  
    return attributeDefNames;

  }

  /**
   * @see edu.internet2.middleware.grouper.internal.dao.AttributeDefNameDAO#findAllSecure(java.lang.String, java.util.Set, QueryOptions)
   */
  public Set<AttributeDefName> findAllSecure(String searchField,
      Set<String> searchInAttributeDefIds, QueryOptions queryOptions) {

    {
      String searchFieldNoPercents = StringUtils.replace(StringUtils.defaultString(searchField), "%", "");
      
      if (StringUtils.isBlank(searchFieldNoPercents) || searchFieldNoPercents.length() < 2) {
        throw new RuntimeException("Need to pass in a searchField of at least 2 chars");
      }
    }
    
    String searchFieldLower = StringUtils.defaultString(searchField).toLowerCase();

    GrouperSession grouperSession = GrouperSession.staticGrouperSession();
    
    Subject grouperSessionSubject = grouperSession.getSubject();
    
    StringBuilder sqlTables = new StringBuilder("from AttributeDefName as attributeDefName ");
    ByHqlStatic byHqlStatic = HibernateSession.byHqlStatic();
    
    StringBuilder sqlWhereClause =  new StringBuilder(" (lower(attributeDefName.extensionDb) like :searchField " +
      "or lower(attributeDefName.displayExtensionDb) like :searchField " +
      "or lower(attributeDefName.description) like :searchField) ");

    grouperSession.getAttributeDefResolver().hqlFilterAttrDefsWhereClause(
        grouperSessionSubject, byHqlStatic, 
        sqlTables, sqlWhereClause, "attributeDefName.attributeDefId", AttributeDefPrivilege.READ_PRIVILEGES);

    StringBuilder sql;
    sql = sqlTables.append(" where ").append(sqlWhereClause);
    
    if (GrouperUtil.length(searchInAttributeDefIds) > 0) {
      sql.append(" and attributeDefName.attributeDefId in (");
      sql.append(HibUtils.convertToInClause(searchInAttributeDefIds, byHqlStatic));
      sql.append(") ");
    }
    
    Set<AttributeDefName> attributeDefNames = byHqlStatic
      .createQuery(sql.toString()).options(queryOptions)
      .setCacheable(true)
      .setCacheRegion(KLASS + ".FindAll")
      .setString("searchField", searchFieldLower)
      .listSet(AttributeDefName.class);

    int maxSize = GrouperConfig.getPropertyInt("findAllAttributeDefNames.maxResultSize", 30000);
    if (maxSize > -1) {
      if (maxSize < attributeDefNames.size()) {
        throw new AttributeDefNameTooManyResults("Too many results: " 
            + attributeDefNames.size() + ", '" + searchField + "'");
      }
    }
    
    return attributeDefNames;

  }

  /**
   * @see AttributeDefNameDAO#findByAttributeDefLike(String, String)
   */
  public Set<AttributeDefName> findByAttributeDefLike(String attributeDefId,
      String likeString) {
    
    //if all
    if (StringUtils.equals(likeString, "%")) {
      return findByAttributeDef(attributeDefId);
    }

    //if some
    Set<AttributeDefName> attributeDefNames = HibernateSession.byHqlStatic()
      .createQuery("from AttributeDefName where attributeDefId = :id and name like :likeString")
      .setCacheable(false)
      .setCacheRegion(KLASS + ".FindByAttributeDefLike")
      .setString("id", attributeDefId)
      .setString("likeString", likeString)
      .listSet(AttributeDefName.class);
  
    return attributeDefNames;
  }

} 

