/*
  Copyright (C) 2004-2007 University Corporation for Advanced Internet Development, Inc.
  Copyright (C) 2004-2007 The University Of Chicago

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

package edu.internet2.middleware.grouper.subj;
import edu.internet2.middleware.grouper.Member;
import edu.internet2.middleware.grouper.internal.util.Quote;
import edu.internet2.middleware.subject.Subject;

/**
 * {@link Subject} utility helper class.
 * <p/>
 * @author  blair christensen.
 * @version $Id: SubjectHelper.java,v 1.2 2008-09-10 05:45:59 mchyzer Exp $
 */
public class SubjectHelper {

  private static final String SUBJECT_DELIM = "/";


  /**
   * @return  True if both objects are <code>Subject</code>s and equal.
   * @since   1.2.1
   */
  public static boolean eq(Object a, Object b) {
    // TODO 20070816 add tests
    if ( (a == null) || (b == null) ) {
      return false;
    }
    if ( !(a instanceof Subject) ) {
      return false;
    }
    if ( !(b instanceof Subject) ) {
      return false;
    }
    Subject subjA = (Subject) a;
    Subject subjB = (Subject) b;
    if (
         subjA.getId().equals( subjB.getId() )
      && subjA.getSource().getId().equals( subjB.getSource().getId() )
      && subjA.getType().getName().equals( subjB.getType().getName() )
    )
    {
      return true;
    }
    return false;
  } 
 
  // @since   1.2.0
  public static String getPretty(Member _m) {
    
    return  Quote.single( _m.getSubjectId() ) // don't bother grabbing the name.  names aren't consistent, after all.
            + SUBJECT_DELIM
            + Quote.single( _m.getSubjectTypeId() ) 
            + SUBJECT_DELIM
            + Quote.single( _m.getSubjectSourceId() );
  } // protected static String getPretty(_m)
 
  // @since   1.2.0
  public static String getPretty(Subject subj) {
    if (subj instanceof LazySubject) {
      return subj.toString();
    }
    return  Quote.single( subj.getId() )
            + SUBJECT_DELIM
            + Quote.single( subj.getType().getName() ) 
            + SUBJECT_DELIM
            + Quote.single( subj.getSource().getId() );
  } // protected static String getPretty(subj)

} // class SubjectHelper
 