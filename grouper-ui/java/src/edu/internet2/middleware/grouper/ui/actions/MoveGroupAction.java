package edu.internet2.middleware.grouper.ui.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GrouperHelper;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.StemFinder;
import edu.internet2.middleware.grouper.ui.Message;

/**
 * @author shilen
 * @version $Id: MoveGroupAction.java,v 1.2 2009-05-08 12:03:37 shilen Exp $
 */
public class MoveGroupAction extends GrouperCapableAction {

  static final private String FORWARD_GroupSummary = "GroupSummary";
  static final private String FORWARD_MoveGroup = "MoveGroup";

  public ActionForward grouperExecute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response,
      HttpSession session, GrouperSession grouperSession)
      throws Exception {
    
    DynaActionForm groupForm = (DynaActionForm) form;
    
    String curNode = (String)groupForm.get("groupId");
    Group group = GroupFinder.findByUuid(grouperSession, curNode, true);

    // get the options selected by the user for the group move
    String[] selections = request.getParameterValues("selections");
    
    // find the destination stem
    String stemSelection = request.getParameter("stemSelection");
    if (stemSelection.equals("other")) {
      stemSelection = request.getParameter("otherStemSelection");
    }
    
    if (stemSelection == null || stemSelection.equals("")) {
      request.setAttribute("message", new Message(
          "stems.message.error.invalid-stem", true));
      return mapping.findForward(FORWARD_MoveGroup);
    }

    Stem destinationStem = StemFinder.findByName(grouperSession, stemSelection, false);
    
    if (destinationStem == null) {
      request.setAttribute("message", new Message(
          "stems.message.error.invalid-stem", true));
      return mapping.findForward(FORWARD_MoveGroup);
    }

    GrouperHelper.moveGroup(group, destinationStem, selections);

    request.setAttribute("message", new Message(
        "groups.message.group-moved", group.getName()));
    
    return mapping.findForward(FORWARD_GroupSummary);

  }

}