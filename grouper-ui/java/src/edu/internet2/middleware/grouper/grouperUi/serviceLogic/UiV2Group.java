package edu.internet2.middleware.grouper.grouperUi.serviceLogic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.grouperUi.beans.api.GuiGroup;
import edu.internet2.middleware.grouper.grouperUi.beans.json.GuiResponseJs;
import edu.internet2.middleware.grouper.grouperUi.beans.json.GuiScreenAction;
import edu.internet2.middleware.grouper.grouperUi.beans.json.GuiScreenAction.GuiMessageType;
import edu.internet2.middleware.grouper.grouperUi.beans.ui.GrouperRequestContainer;
import edu.internet2.middleware.grouper.grouperUi.beans.ui.TextContainer;
import edu.internet2.middleware.grouper.ui.GrouperUiFilter;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.subject.Subject;

/**
 * operations in the group screen
 * @author mchyzer
 *
 */
public class UiV2Group {

  /**
   * view group
   * @param request
   * @param response
   */
  public void viewGroup(HttpServletRequest request, HttpServletResponse response) {
    
    final Subject loggedInSubject = GrouperUiFilter.retrieveSubjectLoggedIn();
    
    //initialize the bean
    GrouperRequestContainer grouperRequestContainer = GrouperRequestContainer.retrieveFromRequestOrCreate();
    
    GrouperSession grouperSession = null;

    try {
      grouperSession = GrouperSession.start(loggedInSubject);

      Group group = null;

      GuiResponseJs guiResponseJs = GuiResponseJs.retrieveGuiResponseJs();

      String groupId = request.getParameter("groupId");
      String groupIndex = request.getParameter("groupIndex");
      String groupName = request.getParameter("groupName");
      if (!StringUtils.isBlank(groupId)) {
        group = GroupFinder.findByUuid(grouperSession, groupId, false);
      } else if (!StringUtils.isBlank(groupName)) {
        group = GroupFinder.findByName(grouperSession, groupName, false);
      } else if (!StringUtils.isBlank(groupIndex)) {
        long idIndex = GrouperUtil.longValue(groupIndex);
        group = GroupFinder.findByIdIndexSecure(idIndex, false, null);
      } else {
        guiResponseJs.addAction(GuiScreenAction.newMessage(GuiMessageType.error, 
            TextContainer.retrieveFromRequest().getText().get("groupCantFindGroupId")));
      }

      if (group != null) {
        grouperRequestContainer.getGroupContainer().setGuiGroup(new GuiGroup(group));      
        guiResponseJs.addAction(GuiScreenAction.newInnerHtmlFromJsp("#grouperMainContentDivId", 
            "/WEB-INF/grouperUi2/group/viewGroup.jsp"));
      } else {
        guiResponseJs.addAction(GuiScreenAction.newInnerHtmlFromJsp("#grouperMainContentDivId", 
            "/WEB-INF/grouperUi2/index/indexMain.jsp"));
      }
    } finally {
      GrouperSession.stopQuietly(grouperSession);
    }
  }


}