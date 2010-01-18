/**
 * @author mchyzer
 * $Id: XmlExportGroup.java 6216 2010-01-10 04:52:30Z mchyzer $
 */
package edu.internet2.middleware.grouper.xml.export;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.CompactWriter;

import edu.internet2.middleware.grouper.attr.AttributeDefScope;
import edu.internet2.middleware.grouper.hibernate.AuditControl;
import edu.internet2.middleware.grouper.hibernate.GrouperTransactionType;
import edu.internet2.middleware.grouper.hibernate.HibUtils;
import edu.internet2.middleware.grouper.hibernate.HibernateHandler;
import edu.internet2.middleware.grouper.hibernate.HibernateHandlerBean;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouper.internal.dao.GrouperDAOException;
import edu.internet2.middleware.grouper.misc.GrouperVersion;
import edu.internet2.middleware.grouper.util.GrouperUtil;


/**
 *
 */
public class XmlExportAttributeDefScope {

  /** uuid */
  private String uuid;
  
  /** createTime */
  private String createTime;

  /** modifierTime */
  private String modifierTime;

  /** hibernateVersionNumber */
  private long hibernateVersionNumber;

  /** contextId */
  private String contextId;

  /** attributeDefId */
  private String attributeDefId;

  /** attributeDefScopeType */
  private String attributeDefScopeType;

  
  
  /**
   * attributeDefScopeType
   * @return attributeDefScopeType
   */
  public String getAttributeDefScopeType() {
    return this.attributeDefScopeType;
  }

  /**
   * attributeDefScopeType
   * @param attributeDefScopeType1
   */
  public void setAttributeDefScopeType(String attributeDefScopeType1) {
    this.attributeDefScopeType = attributeDefScopeType1;
  }

  /** scopeString */
  private String scopeString;
  
  /**
   * scopeString
   * @return scopeString
   */
  public String getScopeString() {
    return this.scopeString;
  }

  /**
   * scopeString
   * @param scopeString1
   */
  public void setScopeString(String scopeString1) {
    this.scopeString = scopeString1;
  }

  /** scopeString2 */
  private String scopeString2;
  
  /**
   * scopeString2
   * @return scopeString2
   */
  public String getScopeString2() {
    return this.scopeString2;
  }

  /**
   * scopeString2
   * @param _scopeString2
   */
  public void setScopeString2(String _scopeString2) {
    this.scopeString2 = _scopeString2;
  }

  /**
   * attributeDefId
   * @return attributeDefId
   */
  public String getAttributeDefId() {
    return this.attributeDefId;
  }

  /**
   * attributeDefId
   * @param attributeDefId1
   */
  public void setAttributeDefId(String attributeDefId1) {
    this.attributeDefId = attributeDefId1;
  }

  /**
   * 
   */
  public XmlExportAttributeDefScope() {
    
  }

  /**
   * @param attributeDefScope
   * @param grouperVersion
   */
  public XmlExportAttributeDefScope(GrouperVersion grouperVersion, AttributeDefScope attributeDefScope) {
    
    if (attributeDefScope == null) {
      throw new RuntimeException();
    }
    
    if (grouperVersion == null) {
      throw new RuntimeException();
    }
    
    this.attributeDefId = attributeDefScope.getAttributeDefId();
    this.attributeDefScopeType = attributeDefScope.getAttributeDefScopeTypeDb();
    this.contextId = attributeDefScope.getContextId();
    this.createTime = GrouperUtil.dateStringValue(attributeDefScope.getCreatedOnDb());
    this.hibernateVersionNumber = attributeDefScope.getHibernateVersionNumber();
    this.modifierTime = GrouperUtil.dateStringValue(attributeDefScope.getLastUpdatedDb());
    this.scopeString = attributeDefScope.getScopeString();
    this.scopeString2 = attributeDefScope.getScopeString2();
    this.uuid = attributeDefScope.getId();
    
  }

  /**
   * uuid
   * @return uuid
   */
  public String getUuid() {
    return this.uuid;
  }

  /**
   * uuid
   * @param uuid1
   */
  public void setUuid(String uuid1) {
    this.uuid = uuid1;
  }

  /**
   * createTime
   * @return createTime
   */
  public String getCreateTime() {
    return this.createTime;
  }

  /**
   * createTime
   * @param createTime1
   */
  public void setCreateTime(String createTime1) {
    this.createTime = createTime1;
  }

  /**
   * modifierTime
   * @return modifierTime
   */
  public String getModifierTime() {
    return this.modifierTime;
  }

  /**
   * modifierTime
   * @param modifierTime1
   */
  public void setModifierTime(String modifierTime1) {
    this.modifierTime = modifierTime1;
  }

  /**
   * hibernateVersionNumber
   * @return hibernateVersionNumber
   */
  public long getHibernateVersionNumber() {
    return this.hibernateVersionNumber;
  }

  /**
   * hibernateVersionNumber
   * @param hibernateVersionNumber1
   */
  public void setHibernateVersionNumber(long hibernateVersionNumber1) {
    this.hibernateVersionNumber = hibernateVersionNumber1;
  }

  /**
   * contextId
   * @return contextId
   */
  public String getContextId() {
    return this.contextId;
  }

  /**
   * contextId
   * @param contextId1
   */
  public void setContextId(String contextId1) {
    this.contextId = contextId1;
  }
  
  /**
   * convert to attributeDefNameSet
   * @return the attributeDefNameSet
   */
  public AttributeDefScope toAttributeDefScope() {
    AttributeDefScope attributeDefScope = new AttributeDefScope();
    
    attributeDefScope.setAttributeDefId(this.attributeDefId);
    attributeDefScope.setAttributeDefScopeTypeDb(this.attributeDefScopeType);
    attributeDefScope.setContextId(this.contextId);
    attributeDefScope.setCreatedOnDb(GrouperUtil.dateLongValue(this.createTime));
    attributeDefScope.setHibernateVersionNumber(this.hibernateVersionNumber);
    attributeDefScope.setLastUpdatedDb(GrouperUtil.dateLongValue(this.modifierTime));
    attributeDefScope.setId(this.uuid);
    attributeDefScope.setScopeString(this.scopeString);
    attributeDefScope.setScopeString2(this.scopeString2);
    
    return attributeDefScope;
  }

  /**
   * @param exportVersion
   * @return the xml string
   */
  public String toXml(GrouperVersion exportVersion) {
    StringWriter stringWriter = new StringWriter();
    this.toXml(exportVersion, stringWriter);
    return stringWriter.toString();
  }

  /**
   * @param exportVersion 
   * @param writer
   */
  public void toXml(
      @SuppressWarnings("unused") GrouperVersion exportVersion, Writer writer) {
    XStream xStream = XmlExportUtils.xstream();
  
    CompactWriter compactWriter = new CompactWriter(writer);
    
    xStream.marshal(this, compactWriter);
  
  }

  /**
   * 
   * @param writer
   */
  public static void exportAttributeDefScopes(final Writer writer) {
    //get the members
    HibernateSession.callbackHibernateSession(GrouperTransactionType.READONLY_OR_USE_EXISTING, AuditControl.WILL_NOT_AUDIT, new HibernateHandler() {
      
      public Object callback(HibernateHandlerBean hibernateHandlerBean)
          throws GrouperDAOException {
  
        Session session = hibernateHandlerBean.getHibernateSession().getSession();
  
        //select all action sets (immediate is depth = 1)
        Query query = session.createQuery(
            "select theAttributeDefScope from AttributeDefScope as theAttributeDefScope order by theAttributeDefScope.attributeDefId, theAttributeDefScope.id");
  
        GrouperVersion grouperVersion = new GrouperVersion(GrouperVersion.GROUPER_VERSION);
        try {
          writer.write("  <attributeDefScopes>\n");
  
          //this is an efficient low-memory way to iterate through a resultset
          ScrollableResults results = null;
          try {
            results = query.scroll();
            while(results.next()) {
              Object object = results.get(0);
              AttributeDefScope attributeDefScope = (AttributeDefScope)object;
              XmlExportAttributeDefScope xmlExportAttributeDefScope = new XmlExportAttributeDefScope(grouperVersion, attributeDefScope);
              writer.write("    ");
              xmlExportAttributeDefScope.toXml(grouperVersion, writer);
              writer.write("\n");
            }
          } finally {
            HibUtils.closeQuietly(results);
          }
          
          //end the members element 
          writer.write("  </attributeDefScopes>\n");
        } catch (IOException ioe) {
          throw new RuntimeException("Problem with streaming attributeDefScopes", ioe);
        }
        return null;
      }
    });
  }

  /**
   * take a reader (e.g. dom reader) and convert to an xml export group
   * @param exportVersion
   * @param hierarchicalStreamReader
   * @return the bean
   */
  public static XmlExportAttributeDefScope fromXml(@SuppressWarnings("unused") GrouperVersion exportVersion, 
      HierarchicalStreamReader hierarchicalStreamReader) {
    XStream xStream = XmlExportUtils.xstream();
    
    XmlExportAttributeDefScope xmlExportAttributeDefScope = (XmlExportAttributeDefScope)xStream.unmarshal(hierarchicalStreamReader);
  
    return xmlExportAttributeDefScope;
  }

  /**
   * 
   * @param exportVersion
   * @param xml
   * @return the object from xml
   */
  public static XmlExportAttributeDefScope fromXml(
      @SuppressWarnings("unused") GrouperVersion exportVersion, String xml) {
    XStream xStream = XmlExportUtils.xstream();
    
    XmlExportAttributeDefScope xmlExportAttributeDefScope = (XmlExportAttributeDefScope)xStream.fromXML(xml);
  
    return xmlExportAttributeDefScope;
  }

}