/**
 * @author mchyzer
 * $Id: SimpleMembershipUpdateMenu.java,v 1.2 2009-09-08 18:53:31 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.grouperUi.serviceLogic;

import java.util.LinkedHashSet;
import java.util.MissingResourceException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Member;
import edu.internet2.middleware.grouper.MemberFinder;
import edu.internet2.middleware.grouper.grouperUi.beans.api.GuiSubject;
import edu.internet2.middleware.grouper.grouperUi.beans.json.GuiHideShow;
import edu.internet2.middleware.grouper.grouperUi.beans.json.GuiResponseJs;
import edu.internet2.middleware.grouper.grouperUi.beans.json.GuiScreenAction;
import edu.internet2.middleware.grouper.grouperUi.beans.simpleMembershipUpdate.SimpleMembershipUpdateContainer;
import edu.internet2.middleware.grouper.grouperUi.exceptions.ControllerDone;
import edu.internet2.middleware.grouper.grouperUi.exceptions.NoSessionException;
import edu.internet2.middleware.grouper.grouperUi.j2ee.GrouperUiJ2ee;
import edu.internet2.middleware.grouper.grouperUi.tags.TagUtils;
import edu.internet2.middleware.grouper.grouperUi.util.GuiUtils;
import edu.internet2.middleware.grouper.grouperUi.util.HttpContentType;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.subject.Subject;


/**
 * ajax methods for the menus in the simple membership update module
 */
public class SimpleMembershipUpdateMenu {

  /**
     * handle a click or select from the advanced menu
     * @param httpServletRequest
     * @param httpServletResponse
     */
    public void advancedMenu(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
      GuiResponseJs guiResponseJs = GuiResponseJs.retrieveGuiResponseJs();
      String menuItemId = httpServletRequest.getParameter("menuItemId");
      //String menuHtmlId = httpServletRequest.getParameter("menuHtmlId");
      //String menuRadioGroup = httpServletRequest.getParameter("menuRadioGroup");
      String menuCheckboxChecked  = httpServletRequest.getParameter("menuCheckboxChecked");
  
  //    guiResponseJs.addAction(GuiScreenAction.newAlert("Menu action: menuItemId: " + menuItemId
  //        + ", menuHtmlId: " + menuHtmlId 
  //        + ", menuRadioGroup: " 
  //        + menuRadioGroup + ", menuCheckboxChecked: " + menuCheckboxChecked));
      
      if (StringUtils.equals(menuItemId, "showGroupDetails")) {
        if (GrouperUtil.booleanValue(menuCheckboxChecked)) {
          guiResponseJs.addAction(GuiScreenAction.newHideShowNameToShow("simpleMembershipUpdateGroupDetails"));
        } else {
          guiResponseJs.addAction(GuiScreenAction.newHideShowNameToHide("simpleMembershipUpdateGroupDetails"));
        }
      } else if (StringUtils.equals(menuItemId, "multiDelete")) {
        if (GrouperUtil.booleanValue(menuCheckboxChecked)) {
          guiResponseJs.addAction(GuiScreenAction.newHideShowNameToShow("simpleMembershipUpdateDeleteMultiple"));
        } else {
          guiResponseJs.addAction(GuiScreenAction.newHideShowNameToHide("simpleMembershipUpdateDeleteMultiple"));
        }
      } else if (StringUtils.equals(menuItemId, "showMemberFilter")) {
        if (GrouperUtil.booleanValue(menuCheckboxChecked)) {
          guiResponseJs.addAction(GuiScreenAction.newHideShowNameToShow("simpleMembershipUpdateMemberFilter"));
        } else {
          guiResponseJs.addAction(GuiScreenAction.newHideShowNameToHide("simpleMembershipUpdateMemberFilter"));
        }
      } else if (StringUtils.equals(menuItemId, "exportSubjectIds")) {
        guiResponseJs.addAction(GuiScreenAction.newAlertFromJsp(
            "/WEB-INF/grouperUi/templates/simpleMembershipUpdate/simpleMembershipUpdateExportSubjectIds.jsp"));
      } else if (StringUtils.equals(menuItemId, "exportAll")) {
        guiResponseJs.addAction(GuiScreenAction.newAlertFromJsp(
            "/WEB-INF/grouperUi/templates/simpleMembershipUpdate/simpleMembershipUpdateExportAll.jsp"));
        
      } else if (StringUtils.equals(menuItemId, "import")) {
        guiResponseJs.addAction(GuiScreenAction.newDialogFromJsp(
            "/WEB-INF/grouperUi/templates/simpleMembershipUpdate/simpleMembershipUpdateImport.jsp"));
      } else {
        throw new RuntimeException("Unexpected menu id: '" + menuItemId + "'");
      }
      
    }

  /**
   * make the structure of the advanced menu
   * @param httpServletRequest
   * @param httpServletResponse
   */
  public void advancedMenuStructure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
    
    //get the text to add to html if showing details
    GuiHideShow showGroupDetails = GuiHideShow.retrieveHideShow("simpleMembershipUpdateGroupDetails", true);
    String showGroupDetailsChecked = showGroupDetails.isShowing() ? " checked=\"true\"" : "";
    
    //get the text to add to html if showing multi delete
    GuiHideShow showMultiDelete = GuiHideShow.retrieveHideShow("simpleMembershipUpdateDeleteMultiple", true);
    String showMultiDeleteChecked = showMultiDelete.isShowing() ? " checked=\"true\"" : "";
  
    //get the text to add to html if showing member filter
    GuiHideShow showMemberFilter = GuiHideShow.retrieveHideShow("simpleMembershipUpdateMemberFilter", true);
    String showMemberFilterChecked = showMemberFilter.isShowing() ? " checked=\"true\"" : "";
  
    GuiUtils.printToScreen(
        "<?xml version=\"1.0\"?>\n"
        + "<menu>\n"
        + "  <item id=\"multiDelete\" text=\"" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuDeleteMultiple"), true) 
        + "\" type=\"checkbox\" " + showMultiDeleteChecked + "><tooltip>" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuDeleteMultipleTooltip"), true) + "</tooltip></item>\n"
        + "  <item id=\"showGroupDetails\" text=\"" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuShowGroupDetails"), true) 
        + "\" type=\"checkbox\" " + showGroupDetailsChecked + "><tooltip>" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuShowGroupDetailsTooltip"), true) + "</tooltip></item>\n"
        
        + "  <item id=\"showMemberFilter\" text=\"" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuShowMemberFilter"), true) 
        + "\" type=\"checkbox\" " + showMemberFilterChecked + "><tooltip>" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuShowMemberFilterTooltip"), true) + "</tooltip></item>\n"
  
        + "  <item id=\"importExport\" text=\"" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuImportExport"), true) 
        + "\" ><tooltip>" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuImportExportTooltip"), true) + "</tooltip>\n"
        + "    <item id=\"export\" text=\"" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuExport"), true) 
        + "\" ><tooltip>" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuExportTooltip"), true) + "</tooltip>\n"
        + "      <item id=\"exportSubjectIds\" text=\"" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuExportSubjectIds"), true) 
        + "\" ><tooltip>" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuExportSubjectIdsTooltip"), true) + "</tooltip></item>\n"
        + "      <item id=\"exportAll\" text=\"" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuExportAll"), true) 
        + "\" ><tooltip>" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuExportAllTooltip"), true) + "</tooltip></item>\n"
        //close the export
        + "   </item>\n"
        + "   <item id=\"import\" text=\"" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuImport"), true) 
        + "\" ><tooltip>" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.advancedMenuImportTooltip"), true) + "</tooltip></item>\n"
        //close the import/export
        + "  </item>\n"
        //+ "  <item id=\"m3\" text=\"Help\" type=\"checkbox\" checked=\"true\"/>\n"
        //+ "  <item id=\"radio1\" text=\"Radio1\" type=\"radio\" group=\"hlm\"/>\n"
        //+ "  <item id=\"radio2\" text=\"Radio2\" type=\"radio\" group=\"hlm\"/>\n"
        + "</menu>", HttpContentType.TEXT_XML, false, false);
    throw new ControllerDone();
  }

  /**
     * handle a click or select from the member menu
     * @param httpServletRequest
     * @param httpServletResponse
     */
    @SuppressWarnings("unused")
    public void memberMenu(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
      GuiResponseJs guiResponseJs = GuiResponseJs.retrieveGuiResponseJs();
      String menuItemId = httpServletRequest.getParameter("menuItemId");
      String menuHtmlId = httpServletRequest.getParameter("menuHtmlId");
      //String menuRadioGroup = httpServletRequest.getParameter("menuRadioGroup");
      //String menuCheckboxChecked  = httpServletRequest.getParameter("menuCheckboxChecked");
  
      menuHtmlId = httpServletRequest.getParameter("menuIdOfMenuTarget");
      if (StringUtils.equals(menuItemId, "memberDetails")) {
  //        guiResponseJs.addAction(GuiScreenAction.newAlert("Menu action: menuItemId: " + menuItemId
  //            + ", menuHtmlId: " + menuHtmlId));
        this.memberMenuSubjectDetails();
      } else {
        throw new RuntimeException("Unexpected menu id: '" + menuItemId + "'");
      }
      
    }

  /**
   * make the structure of the advanced menu
   * @param httpServletRequest
   * @param httpServletResponse
   */
  public void memberMenuStructure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
    
    GuiUtils.printToScreen(
        "<?xml version=\"1.0\"?>\n"
        + "<menu>\n"
        + "  <item id=\"memberDetails\" text=\"" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.memberMenuDetailsLabel"), true) 
        + "\"><tooltip>" 
        + GuiUtils.escapeHtml(GuiUtils.message("simpleMembershipUpdate.memberMenuDetailsTooltip"), true) + "</tooltip></item>\n"
        + "</menu>", HttpContentType.TEXT_XML, false, false);
    throw new ControllerDone();
  }

  /**
   * show subject details
   */
  @SuppressWarnings("unchecked")
  public void memberMenuSubjectDetails() {
    
    GuiResponseJs guiResponseJs = GuiResponseJs.retrieveGuiResponseJs();
  
    //lets see which subject we are dealing with:
    HttpServletRequest httpServletRequest = GrouperUiJ2ee.retrieveHttpServletRequest();
    String menuIdOfMenuTarget = httpServletRequest.getParameter("menuIdOfMenuTarget");
    if (StringUtils.isBlank(menuIdOfMenuTarget)) {
      throw new RuntimeException("Missing id of menu target");
    }
    if (!menuIdOfMenuTarget.startsWith("memberMenuButton_")) {
      throw new RuntimeException("Invalid id of menu target: '" + menuIdOfMenuTarget + "'");
    }
    String memberId = GrouperUtil.prefixOrSuffix(menuIdOfMenuTarget, "memberMenuButton_", false);
    
    final Subject loggedInSubject = GrouperUiJ2ee.retrieveSubjectLoggedIn();
    
    GrouperSession grouperSession = null;
    
    try {
  
      grouperSession = GrouperSession.start(loggedInSubject);
  
      Member member = MemberFinder.findByUuid(grouperSession, memberId);
      Subject subject = member.getSubject();
      String order = null;
      
      try {
        order = TagUtils.mediaResourceString( 
            "subject.attributes.order." + subject.getSource().getId());
      } catch (MissingResourceException mre) {
        //thats ok, go with default
      }
      
      if (StringUtils.isBlank(order)) {
        Set<String> attributeNames = new LinkedHashSet<String>();
        attributeNames.add("screenLabel");
        attributeNames.addAll(subject.getAttributes().keySet());
        
        //lets add subjectId, typeName, sourceId, sourceName, memberId
        attributeNames.add("subjectId");
        attributeNames.add("name");
        attributeNames.add("description");
        attributeNames.add("typeName");
        attributeNames.add("sourceId");
        attributeNames.add("sourceName");
        
        order = GrouperUtil.join(attributeNames.iterator(), ',');
      }
  
      String[] attrNames = GrouperUtil.splitTrim(order, ",");
      
      SimpleMembershipUpdateContainer simpleMembershipUpdateContainer = SimpleMembershipUpdateContainer.retrieveFromSession();
      simpleMembershipUpdateContainer.setSubjectForDetails(subject);
      simpleMembershipUpdateContainer.getSubjectDetails().clear();
  
      //lookup each attribute
      for (String attrName: attrNames) {
        
        //sometimes group have blank attributes???
        if (StringUtils.isBlank(attrName)) {
          continue;
        }
        String attributeValue = GuiSubject.attributeValue(subject, attrName);
        simpleMembershipUpdateContainer.getSubjectDetails().put(attrName, attributeValue);
      }
      guiResponseJs.addAction(GuiScreenAction.newAlertFromJsp(
        "/WEB-INF/grouperUi/templates/simpleMembershipUpdate/simpleMembershipUpdateSubjectDetails.jsp"));
  
    } catch (ControllerDone cd) {
      throw cd;
    } catch (NoSessionException nse) {
      throw nse;
    } catch (Exception se) {
      throw new RuntimeException("Error listing member details: " + menuIdOfMenuTarget 
          + ", " + se.getMessage(), se);
    } finally {
      GrouperSession.stopQuietly(grouperSession); 
    }
  
  }

}