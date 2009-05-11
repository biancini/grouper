<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<!--
  $Id: conditions.jsp,v 1.7 2007-07-06 21:59:20 ddonn Exp $
  $Date: 2007-07-06 21:59:20 $
  
  Copyright 2004 Internet2 and Stanford University.  All Rights Reserved.
  Licensed under the Signet License, Version 1,
  see doc/license.txt in this distribution.
-->

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <meta name="robots" content="noindex, nofollow" />
    <title>
      <%=ResLoaderUI.getString("signet.title") %>
    </title>
    <link href="styles/signet.css" rel="stylesheet" type="text/css" />
    <script language="JavaScript" type="text/javascript" src="scripts/signet.js">
    </script>
  </head>

  <body onload="javascript:selectLimitCheckbox();">
    <script type="text/javascript">
      function selectLimitCheckbox()
      {
        if (hasUnselectedLimits())
        {
          document.form1.completeAssignmentButton.disabled = true;
        }
        else
        {
          document.form1.completeAssignmentButton.disabled = false;
        }
      }
      
      function hasUnselectedLimits()
      {
        var theForm = document.form1;
        var currentLimitName = null;
        var currentLimitSelected = true;
        
        for (var i = 0; i < theForm.elements.length; i++)
        {
          var currentElement = theForm.elements[i];
             
          if (currentElement.name == null)
          {
            continue;
          }
             
          var nameParts = currentElement.name.split(':');
          
          if (nameParts[0] == 'LIMITVALUE_MULTI')
          {
            if ((currentLimitName != null)
                && (currentLimitName != currentElement.name)
                && (currentLimitSelected == false))
            {
              // We've finished examining a Limit, and it had no
              // selected values.
              return true; // We've found an un-selected Limit.
            }
            else if (currentLimitName == currentElement.name)
            {
              // We're looking at a series of values for a single Limit.
              if (currentElement.checked == true)
              {
                currentLimitSelected = true;
              }
            }
            else
            {
              // We're moving on to a previously unexamined Limit. The previous
              // Limits, if any, all had at least one selected value.
              currentLimitName = currentElement.name;
              currentLimitSelected = currentElement.checked;
            }
          }
        }
        
        return !currentLimitSelected;
      }
  </script>
  
<%@ taglib uri="http://struts.apache.org/tags-bean"  prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-html"  prefix="html" %>

<%@ page import="java.text.DateFormat" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Date" %>

<%@ page import="edu.internet2.middleware.signet.subjsrc.SignetSubject" %>
<%@ page import="edu.internet2.middleware.signet.Subsystem" %>
<%@ page import="edu.internet2.middleware.signet.Category" %>
<%@ page import="edu.internet2.middleware.signet.Assignment" %>
<%@ page import="edu.internet2.middleware.signet.Function" %>
<%@ page import="edu.internet2.middleware.signet.tree.TreeNode" %>
<%@ page import="edu.internet2.middleware.signet.Signet" %>
<%@ page import="edu.internet2.middleware.signet.Limit" %>
<%@ page import="edu.internet2.middleware.signet.Status" %>

<%@ page import="edu.internet2.middleware.signet.resource.ResLoaderUI" %>

<%@ page import="edu.internet2.middleware.signet.ui.LimitRenderer" %>
<%@ page import="edu.internet2.middleware.signet.ui.Common" %>
<%@ page import="edu.internet2.middleware.signet.ui.Constants" %>

<% 
  Signet signet
     = (Signet)
         (request.getSession().getAttribute(Constants.SIGNET_ATTRNAME));
         
  SignetSubject loggedInPrivilegedSubject
     = (SignetSubject)
         (request.getSession().getAttribute(Constants.LOGGEDINUSER_ATTRNAME));
         
  SignetSubject		currentGranteePrivilegedSubject;
  Subsystem			currentSubsystem;
  Category			currentCategory;
  Function			currentFunction;
  TreeNode			currentScope;
         
  // If the session contains a Constants.ASSIGNMENT_ATTRNAME attribute, then we're
  // editing an existing Assignment. Otherwise, we're attempting to create a
  // new one.
  Assignment currentAssignment
    = (Assignment)(request.getSession().getAttribute(Constants.ASSIGNMENT_ATTRNAME));
   
  if (currentAssignment != null)
  {
    currentGranteePrivilegedSubject = currentAssignment.getGrantee();
    currentFunction = currentAssignment.getFunction();
    currentSubsystem = currentFunction.getSubsystem();
    currentCategory = currentFunction.getCategory();
    currentScope = currentAssignment.getScope();
  }
  else
  {
    currentGranteePrivilegedSubject
      = (SignetSubject)
          (request
             .getSession()
               .getAttribute
                 (Constants.CURRENTPSUBJECT_ATTRNAME));
         
    currentSubsystem
      = (Subsystem)
          (request.getSession().getAttribute(Constants.SUBSYSTEM_ATTRNAME));
         
    currentCategory
      = (Category)
          (request.getSession().getAttribute(Constants.CATEGORY_ATTRNAME));
         
    currentFunction
      = (Function)
          (request.getSession().getAttribute(Constants.FUNCTION_ATTRNAME));
         
    currentScope
      = (TreeNode)
          (request.getSession().getAttribute(Constants.SCOPE_ATTRNAME));
  }

  Limit[] currentLimits
  	= Common.getLimitsInDisplayOrder(currentFunction.getLimits());
         
  DateFormat dateFormat = DateFormat.getDateInstance();
   
  String personViewHref
    = "PersonView.do?granteeSubjectTypeId="
      + currentGranteePrivilegedSubject.getSubjectType()
      + "&granteeSubjectId="
      + currentGranteePrivilegedSubject.getId()
      + "&subsystemId="
      + currentSubsystem.getId();

       
  String functionsHref
    = "Functions.do?select="
      + currentSubsystem.getId();
       
  String orgBrowseHref
   	= "OrgBrowse.do?functionSelectList="
      + currentFunction.getId();
%>

    <form name="form1" action="Confirm.do">
      <tiles:insert page="/tiles/header.jsp" flush="true" />
      <div id="Navbar">
        <span class="logout">
            <%=Common.displayLogoutHref(request)%>
        </span> <!-- logout -->
        <span class="select">
          <a href="Start.do?<%=Constants.CURRENTPSUBJECT_HTTPPARAMNAME%>=<%=Common.buildCompoundId(loggedInPrivilegedSubject.getEffectiveEditor())%>">
            <%=Common.homepageName(loggedInPrivilegedSubject)%>
          </a>
          &gt; <!-- displays as text right-angle bracket -->
        <a href="<%=personViewHref%>"><%=ResLoaderUI.getString("conditions.subjectview.txt") %> 
            [<%=currentGranteePrivilegedSubject.getName()%>]
          </a>
<% if (currentAssignment == null)
   {
%>
          &gt;<%=ResLoaderUI.getString("conditions.grantpriv.txt") %>
<%
   }
   else
   {
%>
          &gt;<%=ResLoaderUI.getString("conditions.editpriv.txt") %>
<%
   }
%>
          
        </span> <!-- select -->
      </div>  <!-- Navbar -->
      
      <div id="Layout">
        <div id="Content">
          <div id="ViewHead">
<% if (currentAssignment == null)
   {
%>
            <span class="dropback"><%=ResLoaderUI.getString("conditions.grantto.txt") %></span>
<%
   }
   else
   {
%>
            <span class="dropback"><%=ResLoaderUI.getString("conditions.editfor.txt") %></span>
<%
   }
%>
            <h1>
              <%=currentGranteePrivilegedSubject.getName()%>
       	    </h1>
       	    <span class="ident"><%=currentGranteePrivilegedSubject.getDescription()%></span><!--,	Technology Strategy and Support Operations-->
          </div>  <!-- ViewHead -->

         	<div class="section" id="summary">
<% if (currentAssignment == null)
   {
%>
            <h2><%=ResLoaderUI.getString("conditions.newassigndet.txt") %></h2>
<%
   }
   else
   {
%>
            <h2><%=ResLoaderUI.getString("conditions.currassigndet.txt") %></h2>
<%
   }
%>
							<table>
              	<!-- deleted name row -->
              	<tr>
              		<th class="label" scope="row"><%=ResLoaderUI.getString("conditions.type.lbl") %></th>
              		<td class="data"><%=currentSubsystem.getName()%></td>
<% if (currentAssignment == null)
   {
%>
              		<td class="control">
					<a href="<%=personViewHref%>"><img src="images/arrow_left.gif" alt="" /><%=ResLoaderUI.getString("conditions.change.txt") %></a>
					</td>
<%
	}
%>									
         		</tr>								
              	<tr>
              		<th class="label" scope="row"><%=ResLoaderUI.getString("conditions.privilege.lbl") %></th>
              		<td class="data"><span class="category"><%=currentCategory.getName()%></span> : <span class="function"><%=currentFunction.getName()%></span><br />
              		    <%=currentFunction.getHelpText()%></td>
           		  <% if (currentAssignment == null)
   {
%>
              		<td class="control">
										<a href="<%=functionsHref%>"><img src="images/arrow_left.gif" />change</a>
 				  	</td>
<%
   }
%>				
                </tr>			
			    <tr>
              		<th class="label" scope="row"><%=ResLoaderUI.getString("conditions.scope.lbl") %></th>
              		<td class="data">
											<%=signet.displayAncestry
													(currentScope,
													 " : ",  // childSeparatorPrefix
													 "",                // levelPrefix
													 "",               // levelSuffix
													 "")                 // childSeparatorSuffix
											 %>
				  </td>
<% if (currentAssignment == null)
   {
%>
              		<td class="control">
											<a href="<%=orgBrowseHref%>"><img src="images/arrow_left.gif" /><%=ResLoaderUI.getString("conditions.change.txt") %></a>
				  </td>
<%
   }
%>									
							  </tr>					
			  </table>						
		  </div>

         
            
<%
  if (currentLimits.length > 0)
  {
%>
            <div class="section">
<% if (currentAssignment == null)
   {
%>
              <h2><%=ResLoaderUI.getString("conditions.selectlimits.hdr") %></h2>
<%
   }
   else
   {
%>
              <h2><%=ResLoaderUI.getString("conditions.editlimits.hdr") %></h2>
<%
   }
%>
           
<%
    for (int i = 0; i < currentLimits.length; i++)
    {
      request.setAttribute("limitAttr", currentLimits[i]);
      request.setAttribute
        ("grantableChoiceSubsetAttr",
         loggedInPrivilegedSubject.getGrantableChoices
           (currentFunction, currentScope, currentLimits[i]));
           
      if (currentAssignment != null)
      {
        request.setAttribute
          ("assignmentLimitValuesAttr", currentAssignment.getLimitValues());
      }
      else
      {
        request.setAttribute
          ("assignmentLimitValuesAttr", new HashSet());
      }
%>
              
              <fieldset>
                <legend>
                  <%=currentLimits[i].getName()%>
                </legend>
                <p>
                  <%=currentLimits[i].getHelpText()%>
                </p>
                  <tiles:insert
                     page='<%="/tiles/" + currentLimits[i].getRenderer()%>'
                     flush="true">
                    <tiles:put name="limit" beanName="limitAttr" />
                    <tiles:put name="grantableChoiceSubset" beanName="grantableChoiceSubsetAttr" />
                    <tiles:put name="assignmentLimitValues" beanName="assignmentLimitValuesAttr" />
                  </tiles:insert>
              </fieldset>
<%
    }
%>
            </div> <!-- section -->
<%
  }
%>
		 
            <div class="section">
<% if (currentAssignment == null)
   {
%>
              <h2><%=ResLoaderUI.getString("conditions.setconditions.hdr") %></h2>
<%
   }
   else
   {
%>
              <h2><%=ResLoaderUI.getString("conditions.editconditions.hdr") %></h2>
<%
   }
%>

                <table>
                  <tr>
                    <%=Common.dateSelection
                      (request,
                       Constants.EFFECTIVE_DATE_PREFIX,
                       ResLoaderUI.getString("conditions.effective.txt"),
                       ResLoaderUI.getString("conditions.immediately.txt"),
                       ResLoaderUI.getString("conditions.on.txt"),
                       currentAssignment == null
                         ? null
                         : currentAssignment.getEffectiveDate(),
                       currentAssignment == null
                         ? true
                         : currentAssignment.getStatus().equals(Status.PENDING))%>
                  </tr>
                  <tr>
                    <%=Common.dateSelection
                      (request,
                       Constants.EXPIRATION_DATE_PREFIX,
                       ResLoaderUI.getString("conditions.duration.txt"),
                       ResLoaderUI.getString("conditions.untilrevoked.txt"),
                       ResLoaderUI.getString("conditions.until.txt"),
                       currentAssignment == null
                         ? null
                         : currentAssignment.getExpirationDate())%>
                  </tr>
                  <tr>

                    <td><%=ResLoaderUI.getString("conditions.holdercan.txt") %></td>
                    <td>
					<%=Common.createAssignmentCheckbox(
							Constants.CAN_USE_HTTPPARAMNAME,
							currentAssignment, 
							ResLoaderUI.getString("conditions.canuse.txt")) %>
                      <br />
                     <%=Common.createAssignmentCheckbox(
                     		Constants.CAN_GRANT_HTTPPARAMNAME,
                     		currentAssignment,
                     		ResLoaderUI.getString("conditions.toothers.txt")) %>
                    </td>
                  </tr>
                </table>
		</div>		
        <div class="section">
        
          <h2><%=ResLoaderUI.getString("conditions.completeassign.hdr") %> </h2>	
                <input
                   name="completeAssignmentButton"
                   type="submit"
                   class="button-def"
                   value="<%=(currentAssignment==null ? ResLoaderUI.getString("conditions.completeassign_1.bt") : ResLoaderUI.getString("conditions.completeassign_2.bt"))%>" />

              <p>
                <a href="<%=personViewHref%>">
                  <img src="images/arrow_left.gif" alt="" /><%=ResLoaderUI.getString("conditions.cancelandreturn.txt") %> [<%=currentGranteePrivilegedSubject.getName()%>]
                </a>
          </p>
          </div> <!-- section -->
        </div> <!--Content -->
				      
        <div id="Sidebar">
          <div class="helpbox">
			 	  	<h2><%=ResLoaderUI.getString("conditions.help.hdr") %></h2>
			  		<div class="helpbox">
			  		  <p><%=ResLoaderUI.getString("conditions.steps.txt") %></p>
          	          <ol>
          	            <li class="dropback"><%=ResLoaderUI.getString("conditions.selpriv.txt") %> </li>
          	            <li class="dropback"><%=ResLoaderUI.getString("conditions.selscope.txt") %></li>
          	            <li><b><%=ResLoaderUI.getString("conditions.helptext.txt") %></b></li>
       	              </ol>
					  <h2><%=ResLoaderUI.getString("conditions.definitions.hdr") %></h2>
          	          <dl>
					  <dt><%=ResLoaderUI.getString("conditions.usepriv.hdr") %> </dt>
					  <dd><%=ResLoaderUI.getString("conditions.usepriv.txt") %></dd>
					  <dt><%=ResLoaderUI.getString("conditions.grantpriv.hdr") %> </dt>
					  <dd><%=ResLoaderUI.getString("conditions.grantpriv.txt") %></dd>
					  </dl>
			  		</div>
          	</div> 
        </div> <!-- Sidebar -->
       <tiles:insert page="/tiles/footer.jsp" flush="true" />
      </div> <!-- Layout -->
    </form>
  </body>
</html>