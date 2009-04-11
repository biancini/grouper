/*
  Copyright (C) 2006-2007 University Corporation for Advanced Internet Development, Inc.
  Copyright (C) 2006-2007 The University Of Chicago

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

package edu.internet2.middleware.grouper.bench;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Member;
import edu.internet2.middleware.grouper.MemberFinder;
import edu.internet2.middleware.grouper.RegistrySubject;
import edu.internet2.middleware.grouper.SubjectFinder;
import edu.internet2.middleware.grouper.exception.GrouperRuntimeException;
import edu.internet2.middleware.subject.Subject;

/**
 * Benchmark finding an already existing {@link Member} by {@link Subject}.
 * @author  blair christensen.
 * @version $Id: FindExistingMemberBySubject.java,v 1.6 2008-09-29 03:38:30 mchyzer Exp $
 * @since   1.1.0
 */
public class FindExistingMemberBySubject extends BaseGrouperBenchmark {

  // PRIVATE INSTANCE VARIABLES
  GrouperSession  s;
  Subject         subj;


  // MAIN //
  public static void main(String args[]) {
    BaseGrouperBenchmark gb = new FindExistingMemberBySubject();
    gb.benchmark();
  } // public static void main(args[])


  // CONSTRUCTORS

  /**
   * @since 1.1.0
   */
  protected FindExistingMemberBySubject() {
    super();
  } // protected FindExistingMemberBySubject()

  // PUBLIC INSTANCE METHODS //

  /**
   * @since 1.1.0
   */
  public void init() 
    throws GrouperRuntimeException 
  {
    try {
      this.s = GrouperSession.start( SubjectFinder.findRootSubject());
      RegistrySubject.add(this.s, "subj0", "person", "subject 0");
      this.subj = SubjectFinder.findById("subj0");
      MemberFinder.findBySubject(s, subj);
    }
    catch (Exception e) {
      throw new GrouperRuntimeException(e.getMessage());
    }
  } // public void init()

  /**
   * @since 1.1.0
   */
  public void run() 
    throws GrouperRuntimeException 
  {
    try {
      MemberFinder.findBySubject(this.s, this.subj);
    }
    catch (Exception e) {
      throw new GrouperRuntimeException(e);
    }
  } // public void run()

} // public class FindExistingMemberBySubject
