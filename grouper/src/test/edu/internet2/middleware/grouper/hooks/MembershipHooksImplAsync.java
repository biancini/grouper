/*
 * @author mchyzer
 * $Id: MembershipHooksImplAsync.java,v 1.1 2008-07-08 20:47:42 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.hooks;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.hooks.beans.HooksBean;
import edu.internet2.middleware.grouper.hooks.beans.HooksContext;
import edu.internet2.middleware.grouper.hooks.beans.HooksMembershipChangeBean;
import edu.internet2.middleware.grouper.hooks.logic.HookAsynchronous;
import edu.internet2.middleware.grouper.hooks.logic.HookAsynchronousHandler;
import edu.internet2.middleware.grouper.util.GrouperUtil;


/**
 * test implementation of group hooks for test
 */
public class MembershipHooksImplAsync extends MembershipHooks {

  /** most recent subject id added to group */
  static String mostRecentInsertMemberSubjectId;

  /** keep track of hook count seconds (2 for each) */
  static int preAddMemberHookCountAyncSeconds = 0;
  
  /** let the outer know when done */
  static boolean done = false;
  
  /** let the outer know problems */
  static Exception problem;
  
  /**
   * 
   * @see edu.internet2.middleware.grouper.hooks.MembershipHooks#membershipPreAddMember(edu.internet2.middleware.grouper.hooks.beans.HooksContext, edu.internet2.middleware.grouper.hooks.beans.HooksMembershipChangeBean)
   */
  @Override
  public void membershipPreAddMember(HooksContext hooksContext, 
      HooksMembershipChangeBean preAddMemberBean) {

    HookAsynchronous.callbackAsynchronous(hooksContext, preAddMemberBean, new HookAsynchronousHandler() {

      public void callback(HooksContext hooksContext, HooksBean hooksBean) {
        
        HooksMembershipChangeBean preAddMemberBeanThread = (HooksMembershipChangeBean)hooksBean;
        
        done = false;
        problem = null;
        
        String subjectId = preAddMemberBeanThread.getMember().getSubjectId();
        mostRecentInsertMemberSubjectId = subjectId;
        preAddMemberHookCountAyncSeconds++;
        GrouperUtil.sleep(1000);
        preAddMemberHookCountAyncSeconds++;
        
        //get a session
        GrouperSession grouperSession = hooksContext.getGrouperSession();
        
        if (grouperSession == MembershipHooksTest.grouperSession) {
          problem = new RuntimeException("GrouperSession is the same instance");
        }

        if (!StringUtils.equals(grouperSession.getSubject().getId(), MembershipHooksTest.grouperSession.getSubject().getId())) {
          problem = new RuntimeException("Grouper session doesnt have same subject id: " + grouperSession.getSubject().getId()
              + ", " + MembershipHooksTest.grouperSession.getSubject().getId());
        }
        
        done = true;
        
        
      }
      
    });
    
  }

}
