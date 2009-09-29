/*--
$Id: BaseSourceAdapter.java,v 1.7 2009-03-22 02:49:26 mchyzer Exp $
$Date: 2009-03-22 02:49:26 $
 
Copyright 2005 Internet2 and Stanford University.  All Rights Reserved.
See doc/license.txt in this distribution.
 */
package edu.internet2.middleware.subject.provider;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.internet2.middleware.subject.Source;
import edu.internet2.middleware.subject.SourceUnavailableException;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectNotFoundException; //esluss - added SubjectNotUniqueException
import edu.internet2.middleware.subject.SubjectNotUniqueException;
import edu.internet2.middleware.subject.SubjectType;

/**
 * <pre>
 * Base Source adapter.
 * 
 * Developers note: you should implement the getSubject and getSubjectByIdentifier
 * methods (that take boolean) since the base class method will soon become abstract, and the
 * method overloads which are deprecated and dont take booleans will go away.
 * 
 * </pre>
 */
public abstract class BaseSourceAdapter implements Source {

  /**
   * 
   */
  private static Log log = LogFactory.getLog(BaseSourceAdapter.class);

  /** */
  protected String id = null;

  /** */
  protected String name = null;

  /** */
  protected Set<SubjectType> types = new HashSet<SubjectType>();

  /** */
  protected SubjectType type = null;

  /** */
  protected Properties params = new Properties();

  /** The three different kinds of searches:  */
  protected HashMap<String, Search> searches = new HashMap<String, Search>();

  /** */
  protected Set<String> attributes = new HashSet<String>();

  /**
   * Default constructor.
   */
  public BaseSourceAdapter() {
  }

  /**
   * Allocates adapter with ID and name.
   * @param id1
   * @param name1
   */
  public BaseSourceAdapter(String id1, String name1) {
    this.id = id1;
    this.name = name1;
  }

  /**
   * {@inheritDoc}
   */
  public String getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  public void setId(String id1) {
    this.id = id1;
  }

  /**
   * {@inheritDoc}
   */
  public String getName() {
    return this.name;
  }

  /**
   * {@inheritDoc}
   */
  public void setName(String name1) {
    this.name = name1;
  }

  /**
   * {@inheritDoc}
   */
  public Set<SubjectType> getSubjectTypes() {
    return this.types;
  }

  /**
   * 
   * @return subject type
   */
  public SubjectType getSubjectType() {
    return this.type;
  }

  /**
   * 
   * @see edu.internet2.middleware.subject.Source#getSubject(java.lang.String)
   * @deprecated use the overload instead
   */
  @Deprecated
  public abstract Subject getSubject(String id1) throws SubjectNotFoundException,
      SubjectNotUniqueException;

  /**
   * 
   * @see edu.internet2.middleware.subject.Source#getSubject(java.lang.String, boolean)
   */
  public Subject getSubject(String id1, boolean exceptionIfNull)
      throws SubjectNotFoundException, SubjectNotUniqueException {
    //NOTE this implementation is here temporarily for backwards compatibility... it will go away soon
    //and this method will become abstract
    try {
      return this.getSubject(id1);
    } catch (SubjectNotFoundException snfe) {
      if (exceptionIfNull) {
        throw snfe;
      }
      return null;
    }

  }

  /**
   * 
   * @see edu.internet2.middleware.subject.Source#getSubjectByIdentifier(java.lang.String)
   * @deprecated use the overload instead
   */
  @Deprecated
  public abstract Subject getSubjectByIdentifier(String id1)
      throws SubjectNotFoundException, SubjectNotUniqueException;

  /**
   * note, you should implement this method since this implementation will become abstract at some point
   * @see edu.internet2.middleware.subject.Source#getSubjectByIdentifier(java.lang.String, boolean)
   */
  public Subject getSubjectByIdentifier(String id1, boolean exceptionIfNull)
      throws SubjectNotFoundException, SubjectNotUniqueException {
    //NOTE this implementation is here temporarily for backwards compatibility... it will go away soon
    //and this method will become abstract
    try {
      return this.getSubjectByIdentifier(id1);
    } catch (SubjectNotFoundException snfe) {
      if (exceptionIfNull) {
        throw snfe;
      }
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public abstract Set<Subject> search(String searchValue);

  /**
   * {@inheritDoc}
   */
  public abstract void init() throws SourceUnavailableException;

  /**
   * Compares this source against the specified source.
   * Returns true if the IDs of both sources are equal.
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof BaseSourceAdapter) {
      return this.getId().equals(((BaseSourceAdapter) other).getId());
    }
    return false;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return "BaseSourceAdapter".hashCode() + this.getId().hashCode();
  }

  /**
   * (non-javadoc)
   * @param type1
   */
  public void addSubjectType(String type1) {
    this.types.add(SubjectTypeEnum.valueOf(type1));
    this.type = SubjectTypeEnum.valueOf(type1);
  }

  /**
   * (non-javadoc)
   * @param name1
   * @param value
   */
  public void addInitParam(String name1, String value) {
    this.params.setProperty(name1, value);
  }

  /**
   * (non-javadoc)
   * @param name1
   * @return param
   */
  protected String getInitParam(String name1) {
    return this.params.getProperty(name1);
  }

  /**
   * (non-javadoc)
   * @return params
   */
  protected Properties getInitParams() {
    return this.params;
  }

  /**
   * 
   * @param attributeName
   */
  public void addAttribute(String attributeName) {
    this.attributes.add(attributeName);
  }

  /**
   * 
   * @return set
   */
  protected Set getAttributes() {
    return this.attributes;
  }

  /**
   * 
   * @param searches1
   */
  protected void setSearches(HashMap<String, Search> searches1) {
    this.searches = searches1;
  }

  /**
   * 
   * @return map
   */
  protected HashMap<String, Search> getSearches() {
    return this.searches;
  }

  /**
   * 
   * @param searchType
   * @return search
   */
  protected Search getSearch(String searchType) {
    HashMap searches1 = getSearches();
    return (Search) searches1.get(searchType);
  }

  /**
   * 
   * @param search
   */
  public void loadSearch(Search search) {
    log.debug("Loading search: " + search.getSearchType());
    this.searches.put(search.getSearchType(), search);
  }
}