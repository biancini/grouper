/*--
$Id: PrivilegeImpl.java,v 1.9 2006-10-25 00:08:28 ddonn Exp $
$Date: 2006-10-25 00:08:28 $

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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.set.UnmodifiableSet;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author Andy Cohen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrivilegeImpl
implements
  Privilege,
  Comparable
{
  Permission                                    permission;
  Set                                           limitValues;
  edu.internet2.middleware.signet.tree.TreeNode scope;
  
  /**
   * Hibernate requires the presence of a default constructor.
   */
  public PrivilegeImpl()
  {
    super();
  }
  
  private PrivilegeImpl
    (Permission                                     permission,
     Set                                            limitValues,
     edu.internet2.middleware.signet.tree.TreeNode  scope)
  {
    this.permission = permission;
    this.limitValues = limitValues;
    this.scope = scope;
  }
  
  public static Set getPrivileges(Assignment assignment)
  {
    Set privileges = new HashSet();
    
    Set assignmentPermissions
      = assignment.getFunction().getPermissions();
    
    Set assignmentLimitValues = assignment.getLimitValues();
    
    Iterator assignmentPermissionsIterator = assignmentPermissions.iterator();
    while (assignmentPermissionsIterator.hasNext())
    {
      Permission permission
        = (Permission)(assignmentPermissionsIterator.next());
      
      Set permissionLimits = permission.getLimits();      
      Set permissionLimitValues
        = filterLimitValues(permissionLimits, assignmentLimitValues);
      
      Privilege privilege
        = new PrivilegeImpl(permission, permissionLimitValues, assignment.getScope());
      
      privileges.add(privilege);
    }
    
    return privileges;
  }
  
  static Set filterLimitValues
    (Limit  limit,
     Set    limitValues)
  {
    Set filteredLimitValues = new HashSet();
    
    Iterator limitValuesIterator = limitValues.iterator();
    while (limitValuesIterator.hasNext())
    {
      LimitValue candidate = (LimitValue)(limitValuesIterator.next());
      
      if (candidate.getLimit().equals(limit))
      {
        filteredLimitValues.add(candidate);
      }
    }
    
    return filteredLimitValues;
  }
  
  static Set filterLimitValues
    (Set limits,
     Set limitValues)
  {
     Set filteredLimitValues = new HashSet();
     Iterator limitsIterator = limits.iterator();
     while (limitsIterator.hasNext())
     {
       Limit limit = (Limit)(limitsIterator.next());
       filteredLimitValues.addAll(filterLimitValues(limit, limitValues));
     }
     
     return filteredLimitValues;
  }

  /* (non-Javadoc)
   * @see edu.internet2.middleware.signet.Privilege#getPermission()
   */
  public Permission getPermission()
  {
    return this.permission;
  }

  /* (non-Javadoc)
   * @see edu.internet2.middleware.signet.Privilege#getLimitValues()
   */
  public Set getLimitValues()
  {
    return  UnmodifiableSet.decorate(this.limitValues);
  }

  public boolean equals(Object obj)
  {
    if ( !(obj instanceof PrivilegeImpl) )
    {
      return false;
    }
    
    PrivilegeImpl rhs = (PrivilegeImpl) obj;
    return new EqualsBuilder()
      .append(this.getPermission(), rhs.getPermission())
      .append(this.getLimitValues(), rhs.getLimitValues())
      .isEquals();
  }
  
  public int hashCode()
  {
    // you pick a hard-coded, randomly chosen, non-zero, odd number
    // ideally different for each class
    return new HashCodeBuilder(17, 37)
      .append(this.permission)
      .append(this.limitValues)
      .toHashCode();
  }
  
  public int compareTo(Object o)
  {
    PrivilegeImpl rhs = (PrivilegeImpl) o;
    return new CompareToBuilder()
      // .appendSuper(super.compareTo(o)
      .append(this.permission, rhs.permission)
      .append(this.limitValues, rhs.limitValues)
      .toComparison();
  }
  
  public edu.internet2.middleware.signet.tree.TreeNode getScope()
  {
    return this.scope;
  }
}