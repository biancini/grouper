/*
 * Copyright (C) 2006-2007 blair christensen.
 * All Rights Reserved.
 *
 * You may use and distribute under the same terms as Grouper itself.
 */

package edu.internet2.middleware.grouper.app.gsh;
import java.util.Set;

import bsh.CallStack;
import bsh.Interpreter;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.StemFinder;
import edu.internet2.middleware.grouper.exception.QueryException;
import edu.internet2.middleware.grouper.filter.GrouperQuery;
import edu.internet2.middleware.grouper.filter.StemNameAnyFilter;

/**
 * Query for stems by name.
 * <p/>
 * @author  blair christensen.
 * @version $Id: getStems.java,v 1.2 2008-09-29 03:38:28 mchyzer Exp $
 * @since   0.0.1
 */
public class getStems {

  // PUBLIC CLASS METHODS //

  /**
   * Query for stems by name.
   * <p/>
   * @param   i           BeanShell interpreter.
   * @param   stack       BeanShell call stack.
   * @param   name        Find stems with <i>name</i> as part of their name.
   * @return  {@link Set} of {@link Stem}s.
   * @throws  GrouperShellException
   * @since   0.0.1
   */
  public static Set invoke(Interpreter i, CallStack stack, String name) 
    throws  GrouperShellException
  {
    GrouperShell.setOurCommand(i, true);
    try {
      GrouperSession  s     = GrouperShell.getSession(i);
      Stem            root  = StemFinder.findRootStem(s);
      GrouperQuery    gq    = GrouperQuery.createQuery(
        s, 
        new StemNameAnyFilter(name, root)
      );
      return gq.getStems();
    }
    catch (QueryException eQ) {
      GrouperShell.error(i, eQ);
    }
    return null;
  } // protected static Set getStems(i, name)

} // public class getStems
