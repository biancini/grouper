/*
 * Copyright (C) 2006-2007 blair christensen.
 * All Rights Reserved.
 *
 * You may use and distribute under the same terms as Grouper itself.
 */

package edu.internet2.middleware.grouper.app.gsh;
import bsh.CallStack;
import bsh.Interpreter;
import edu.internet2.middleware.grouper.Composite;
import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.exception.GroupNotFoundException;
import edu.internet2.middleware.grouper.exception.InsufficientPrivilegeException;
import edu.internet2.middleware.grouper.exception.MemberAddException;
import edu.internet2.middleware.grouper.misc.CompositeType;

/**
 * Add a composite member.
 * <p/>
 * @author  blair christensen.
 * @version $Id: addComposite.java,v 1.2 2008-09-29 03:38:28 mchyzer Exp $
 * @since   0.0.1
 */
public class addComposite {

  // PUBLIC CLASS METHODS //

  /**
   * Add a composite member.
   * <p/>
   * @param   i           BeanShell interpreter.
   * @param   stack       BeanShell call stack.
   * @param   group       Add {@link Composite} to {@link Group} with this name.
   * @param   type        {@link CompositeType}.
   * @param   left        Left factor {@link Group}.
   * @param   right       Right factor {@link Group}.
   * @return  True if succeeds.
   * @throws  GrouperShellException
   * @since   0.0.1
   */
  public static boolean invoke(
    Interpreter i, CallStack stack, String group, CompositeType type, String left, String right
  ) 
    throws  GrouperShellException
  {
    GrouperShell.setOurCommand(i, true);
    try {
      GrouperSession  s     = GrouperShell.getSession(i);
      Group           g     = GroupFinder.findByName(s, group);
      Group           l     = GroupFinder.findByName(s, left);
      Group           r     = GroupFinder.findByName(s, right);
      g.addCompositeMember(type, l, r);
      return true;
    }
    catch (GroupNotFoundException eGNF)         {
      GrouperShell.error(i, eGNF);
    }
    catch (InsufficientPrivilegeException eIP)  {
      GrouperShell.error(i, eIP);
    }
    catch (MemberAddException eMA)              {
      GrouperShell.error(i, eMA);
    }
    return false;
  } // public static boolean invoke(i, stack, group, type, left, right)

} // public class addComposite
