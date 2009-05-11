/*--
$Id: PersonViewAction.java,v 1.13 2007-07-06 21:59:20 ddonn Exp $
$Date: 2007-07-06 21:59:20 $
  
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
package edu.internet2.middleware.signet.ui;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import edu.internet2.middleware.signet.Signet;

/**
 * <p>
 * Confirm required resources are available. If a resource is missing,
 * forward to "failure". Otherwise, forward to "success", where
 * success is usually the "welcome" page.
 * </p>
 * <p>
 * Since "required resources" includes the application MessageResources
 * the failure page must not use the standard error or message tags.
 * Instead, it display the Strings stored in an ArrayList stored under
 * the request attribute "ERROR".
 * </p>
 *
 */
public final class PersonViewAction extends BaseAction
{
  // ---------------------------------------------------- Public Methods
  // See superclass for Javadoc
  public ActionForward execute
  	(ActionMapping				mapping,
     ActionForm 					form,
     HttpServletRequest 	request,
     HttpServletResponse response)
  throws Exception
  {
    // Setup message array in case there are errors
    ArrayList messages = new ArrayList();

    // Confirm message resources loaded
    MessageResources resources = getResources(request);
    if (resources==null)
    {
      messages.add(Constants.ERROR_MESSAGES_NOT_LOADED);
    }

    // If there were errors, forward to our failure page
    if (messages.size()>0)
    {
      request.setAttribute(Constants.ERROR_KEY,messages);
      return findFailure(mapping);
    }
    
    HttpSession session = request.getSession(); 
    Signet signet = (Signet)(session.getAttribute(Constants.SIGNET_ATTRNAME));
    
    if (signet == null)
    {
      return (mapping.findForward("notInitialized"));
    }

    // get the selected grantee and put it into the HttpServletRequest for later use
    Common.getGrantee(signet, request);

	Common.getAndSetSubsystem
          (signet,
           request,
           Constants.SUBSYSTEM_HTTPPARAMNAME, // paramName
           Constants.SUBSYSTEM_ATTRNAME,      // attributeName
           Constants.WILDCARD_SUBSYSTEM);     // default value
    
    PrivDisplayType privDisplayType = PrivDisplayType.CURRENT_RECEIVED;
    String privDisplayTypeName
      = request.getParameter(Constants.PRIVDISPLAYTYPE_HTTPPARAMNAME);
    if (privDisplayTypeName != null)
    {
      privDisplayType
        = (PrivDisplayType)
            (TypeSafeEnumeration.getInstanceByName(privDisplayTypeName));
    }
    
    session.setAttribute
      (Constants.PRIVDISPLAYTYPE_ATTRNAME, privDisplayType);
    
    // Forward to our success page
    return findSuccess(mapping);
  }

//  /**
//   * @param currentGrantee
//   * @return
//   * @throws ObjectNotFoundException
//   */
//  private Subsystem getFirstSubsystemOfReceivedAssignments
//    (PrivilegedSubject grantee) throws ObjectNotFoundException
//  {
//    Subsystem firstSubsystem = null;
//    Set assignmentsReceived
//      = grantee.getAssignmentsReceived((Status)null, null, null);
//    Iterator assignmentsReceivedIterator = assignmentsReceived.iterator();
//    if (assignmentsReceivedIterator.hasNext())
//    {
//      firstSubsystem
//      	= ((Assignment)(assignmentsReceivedIterator.next()))
//      			.getFunction().getSubsystem();
//    }
//    
//    return firstSubsystem;
//  }
}