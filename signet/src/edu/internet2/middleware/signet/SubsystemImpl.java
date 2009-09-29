/*--
$Id: SubsystemImpl.java,v 1.24 2008-05-17 20:54:09 ddonn Exp $
$Date: 2008-05-17 20:54:09 $
 
Copyright 2006 Internet2, Stanford University

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
package edu.internet2.middleware.signet;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import edu.internet2.middleware.signet.choice.ChoiceSet;
import edu.internet2.middleware.signet.tree.Tree;

/* Hibernate requires this class to be non-final. */

public class SubsystemImpl extends EntityImpl implements Subsystem
{
  private String  helpText;
  
  private Set     categories;
  private Set     functions;
  private Set     choiceSets;

  private Map     limits;
  private Map     permissions;
  
  private TreeImpl	tree;
//  private Tree    tree;

// not used
//  private boolean choiceSetsNotYetFetched  = true;
  private boolean limitsNotYetFetched      = true;
  private boolean permissionsNotYetFetched = true;

  /**
   * Hibernate requires that each persistable entity have a default
   * constructor.
   */
  public SubsystemImpl()
  {
    super();
    this.categories = new HashSet();
    this.functions = new HashSet();
    this.choiceSets = new HashSet();

    this.limits = new HashMap();
    this.permissions = new HashMap();
  }

  /**
   * @param signet A Signet instance
   * @param id
   *            A short mnemonic id which will appear in XML documents and
   *            other documents used by analysts.
   * @param name
   *            A descriptive name which will appear in UIs and documents
   *            exposed to users.
   * @param helpText
   *            A prose description which will appear in help-text and other
   *            explanatory materials.
   * @param status
   * 			The {@link Status} of this Proxy.
   */
  SubsystemImpl(Signet signet, String id, String name, String helpText,
      Status status)
  {
    super(signet, id, name, status);
    this.helpText = helpText;
    this.categories = new HashSet();
    this.functions = new HashSet();
    this.choiceSets = new HashSet();
    
    this.limits = new HashMap();
    this.permissions = new HashMap();
  }

  /* This method exists only for use by Hibernate. */
  public void setCategories(Set categories)
  {
    this.categories = categories;
  }

  /* This method exists only for use by Hibernate. */
  public void setChoiceSets(Set choiceSets)
  {
    this.choiceSets = choiceSets;
  }

  /* This method exists only for use by Hibernate. */
  public void setFunctions(Set functions)
  {
    this.functions = functions;
  }

  /**
   * @param helpText A prose description which will appear in help-text and
   * 		other explanatory materials.
   */
  public void setHelpText(String helpText)
  {
    this.helpText = helpText;
  }

  /* (non-Javadoc)
   * @see edu.internet2.middleware.signet.Entity#getHelpText()
   */
  public String getHelpText()
  {
    return this.helpText;
  }

  /**
   * @return Returns the {@link Tree} associated
   * 		with this Subsystem.
   */
  public Tree getTree()
  {
    if (null != tree)
      tree.setSignet(signet);

    return (tree);
  }

  public void setTree(Tree tree)
  {
    if (tree instanceof TreeImpl)
    {
      ((TreeImpl) tree).setSignet(signet);
    }

    this.tree = (TreeImpl)tree;
  }

  /**
   * TODO - Hibernate requires that getters and setters for collections
   * return the EXACT SAME collection, not just an identical one. Failure
   * to do this makes Hibernate think that the collection has been modified,
   * and causes the entire collection to be re-persisted in the database.
   * 
   * I need to find some way to tell Hibernate to use a specific non-public
   * getter, so that the public getter can resume returning a non-modifiable
   * copy of the collection. 
   */
  public Set getCategories()
  {
    return this.categories;
    // return UnmodifiableSet.decorate(this.categories);
  }
  
  public Category getCategory(String categoryId)
  throws ObjectNotFoundException
  {
    Category requestedCategory = null;
    
    Iterator categoriesIterator = this.categories.iterator();
    while (categoriesIterator.hasNext())
    {
      Category candidate = (Category)(categoriesIterator.next());
      if (candidate.getId().equals(categoryId))
      {
        requestedCategory = candidate;
      }
    }
    
    if (requestedCategory == null)
    {
      throw new ObjectNotFoundException
      	("The Subsystem with ID '" 
      	 + this.getId()
      	 + "' does not contain a Category with ID '"
      	 + categoryId
      	 + "'.");
    }
    
    return requestedCategory;
  }

  /**
   * @return Returns the Functions associated with this Subsystem.
   * @throws ObjectNotFoundException
   */
  public Set getFunctions()
  {
    // Let's make sure all of these Functions have a live Signet reference.
    Iterator functionsIterator = this.functions.iterator();
    while (functionsIterator.hasNext())
    {
      FunctionImpl function = (FunctionImpl)(functionsIterator.next());
      function.setSignet(signet);
    }
    
    return this.functions;
  }
  
  public ChoiceSet getChoiceSet(String id)
  throws ObjectNotFoundException
  {
    // First, make sure that the ChoiceSets have been fetched from
    // the database.
    Set choiceSets = this.getChoiceSets();
    ChoiceSet choiceSet = null;
    
    Iterator choiceSetsIterator = choiceSets.iterator();
    while (choiceSetsIterator.hasNext())
    {
      ChoiceSet candidate = (ChoiceSet)(choiceSetsIterator.next());
      if (candidate.getId().equals(id))
      {
        choiceSet = candidate;
      }
    }
    
    if (choiceSet == null)
    {
      throw new ObjectNotFoundException
      	("The Subsystem with ID '"
      	 + this.getId()
      	 + "' does not contain a ChoiceSet with ID '"
      	 + id
      	 + "'.");
    }
    
    return choiceSet;
  }

  /**
   * @return Returns the ChoiceSets associated with this Subsystem.
   * @throws ObjectNotFoundException
   */
  public Set getChoiceSets()
  {
//    // I really want to handle this purely through Hibernate mappings, but
//    // I haven't figured out how yet.
//
//    if (this.choiceSetsNotYetFetched == true)
//    {
//      // We have not yet fetched the ChoiceSets associated with this
//      // Subsystem from the database. Let's make a copy of
//      // whatever in-memory ChoiceSets we DO have, because they
//      // represent defined-but-not-necessarily-yet-persisted
//      // ChoiceSets.
//      Set unsavedChoiceSets = this.choiceSets;
//
//      this.choiceSets
//      	= this.getSignet().getChoiceSetsBySubsystem(this);
//
//      this.choiceSets.addAll(unsavedChoiceSets);
//
//      this.choiceSetsNotYetFetched = false;
//    }
    
    if (null != signet)
    {
      Iterator choiceSetsIterator = this.choiceSets.iterator();
      while (choiceSetsIterator.hasNext())
      {
        ChoiceSet choiceSet = (ChoiceSet)(choiceSetsIterator.next());
        ((ChoiceSetImpl)choiceSet).setSignet(signet);
      }
    }
    
    return this.choiceSets;
  }

  protected Map buildMap(Set set)
  {
    Map map = new HashMap(set.size());
    Iterator iterator = set.iterator();
    String id;

    while (iterator.hasNext())
    {
      Object obj = iterator.next();

      try
      {
        Method getIdMethod = obj.getClass().getMethod("getId", (Class[])null);
        id = (String) (getIdMethod.invoke(obj, (Object[])null));
      }
      catch (Exception e)
      {
        throw new SignetRuntimeException(
            "Failed to execute 'getID()' method of object '" + obj
                + "' in class '" + obj.getClass() + "'.", e);
      }

      map.put(id, obj);
    }

    return map;
  }

  public void add(Function function)
  {
    this.functions.add(function);
  }
  
  public void add(ChoiceSet choiceSet)
  {
    this.choiceSets.add(choiceSet);
  }
  
  public void add(Limit limit)
  {
    this.limits.put(limit.getId(), limit);
  }
  
  public void add(Permission permission)
  {
    this.permissions.put(permission.getId(), permission);
  }

  /* (non-Javadoc)
   * @see edu.internet2.middleware.signet.Subsystem#add(edu.internet2.middleware.signet.Category)
   */
  public void add(Category category)
  {
    this.categories.add(category);
  }

  /* (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object o)
  {
    if (o == null)
    {
      return 1;
    }
    
    String thisName = null;
    String otherName = null;

    thisName = this.getName();
    otherName = ((Subsystem) o).getName();

    return thisName.compareToIgnoreCase(otherName);
  }
  
  /**
   * @return Returns the Limits associated with this Subsystem.
   * @throws ObjectNotFoundException
   */
  public Map getLimits()
  {
    // I really want to handle this purely through Hibernate mappings, but
    // I haven't figured out how yet.

    if (this.limitsNotYetFetched == true)
    {
      // We have not yet fetched the Limits associated with this
      // Subsystem from the database. Let's make a copy of
      // whatever in-memory Limits we DO have, because they
      // represent defined-but-not-necessarily-yet-persisted
      // Limits.
      Map unsavedLimits = limits;

      limits = signet.getPersistentDB().getLimitsBySubsystem(this);

      limits.putAll(unsavedLimits);

      limitsNotYetFetched = false;
    }
    
    return UnmodifiableMap.decorate(this.limits);
  }
  
  public Limit getLimit(String id)
  throws ObjectNotFoundException
  {
    // First, make sure that the Limits have been fetched from
    // the database.
    Map limits = this.getLimits();
    
    Limit limit = (Limit)(limits.get(id));
    
    if (limit == null)
    {
      throw new ObjectNotFoundException
      	("The Subsystem with ID '"
      	 + this.getId()
      	 + "' does not contain a Limit with ID '"
      	 + id
      	 + "'.");
    }
    
    return limit;
  }
  
  /**
   * @return Returns the Permissions associated with this Subsystem.
   */
  public Map getPermissions()
  {
    // I really want to handle this purely through Hibernate mappings, but
    // I haven't figured out how yet.

    if (this.permissionsNotYetFetched == true)
    {
      // We have not yet fetched the Permissions associated with this
      // Subsystem from the database. Let's make a copy of
      // whatever in-memory Permissions we DO have, because they
      // represent defined-but-not-necessarily-yet-persisted
      // Permissions.
      Map unsavedPermissions = this.permissions;

      permissions = signet.getPersistentDB().getPermissionsBySubsystem(this);

      this.permissions.putAll(unsavedPermissions);

      this.permissionsNotYetFetched = false;
    }
    
    return UnmodifiableMap.decorate(this.permissions);
  }
  
  public Permission getPermission(String id)
  throws ObjectNotFoundException
  {
    // First, make sure that the Permissions have been fetched from
    // the database.
    Map permissions = this.getPermissions();
    
    Permission permission = (Permission)(permissions.get(id));
    
    if (permission == null)
    {
      throw new ObjectNotFoundException
      	("The Subsystem with ID '"
      	 + this.getId()
      	 + "' does not contain a Permission with ID '"
      	 + id
      	 + "'.");
    }
    
    return permission;
  }
  
  public String getId()
  {
    return super.getStringId();
  }
  
  // This method is only for use by Hibernate.
  protected void setId(String id)
  {
    super.setStringId(id);
  }

  /* (non-Javadoc)
   * @see edu.internet2.middleware.signet.Entity#inactivate()
   */
  public void inactivate()
  {
    throw new UnsupportedOperationException
      ("This method is not yet implemented");
  }

	/**
	 * @return true if granting can occur, otherwise false
	 */
	public boolean isPopulatedForGranting()
	{
		boolean retval = false; // assume failure

		Tree _tree = getTree();
		// if this Subsystem has no Tree, none of its Functions can be granted.
		if (null != _tree)
		{
			Set roots = _tree.getRoots();
			// if this Tree contains no TreeNodes, none of the Functions in this Subsystem can be granted.
			if ((null != roots) || (0 < roots.size()))
			{
				// if this Subsystem contains no Functions, there's no granting to be done nohow.
				if (0 < getFunctions().size())
				{
					retval = true;
				}
			}
		}
		return (retval);
	}


	/////////////////////////////////////
	// overrides Object
	/////////////////////////////////////

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if ( !(o instanceof SubsystemImpl))
		{
			return false;
		}
		SubsystemImpl rhs = (SubsystemImpl)o;
		return new EqualsBuilder().append(this.getId(), rhs.getId()).isEquals();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		// you pick a hard-coded, randomly chosen, non-zero, odd number
		// ideally different for each class
		return new HashCodeBuilder(17, 37).append(this.getId()).toHashCode();
	}

}