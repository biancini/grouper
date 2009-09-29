/*--
$Id: LimitRenderer.java,v 1.10 2006-06-30 02:04:41 ddonn Exp $
$Date: 2006-06-30 02:04:41 $

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

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import edu.internet2.middleware.signet.Limit;
import edu.internet2.middleware.signet.LimitValue;
import edu.internet2.middleware.signet.ObjectNotFoundException;
import edu.internet2.middleware.signet.Signet;
import edu.internet2.middleware.signet.SignetRuntimeException;
import edu.internet2.middleware.signet.Subsystem;
import edu.internet2.middleware.signet.choice.Choice;
import edu.internet2.middleware.signet.choice.ChoiceSet;

/**
 * @author Andy Cohen
 *
 */
public class LimitRenderer
{
  private static final String MULTISELECT_LIMIT_VALUE_PARAMETER_PREFIX
  	= "LIMITVALUE_MULTI";
  private static final String SINGLESELECT_LIMIT_VALUE_PARAMETER_PREFIX
  	= "LIMITVALUE_SINGLE";
  private static final String DELIMITER = ":";
  
  public static String render(Limit limit)
  {
    StringBuffer outStr = new StringBuffer();
    boolean isMultiSelect
    	= (limit.getRenderer().equals("multipleChoiceCheckboxes.jsp"));
    String limitName = makeLimitValueParamName(limit, isMultiSelect);
    
    ChoiceSet choiceSet = limit.getChoiceSet();

    Set choices = choiceSet.getChoices();
    boolean isFirstChoice = true;
    Iterator choicesIterator = choices.iterator();
    
    if (limit.getRenderer().equals("singleChoicePullDown.jsp"))
    {
      outStr.append
      	("<select class=\"" + limit.getDataType() + "\""
      	 + "name \"" + limitName + "\">\n");
      
      while (choicesIterator.hasNext())
      {
        Choice choice = (Choice)(choicesIterator.next());
        outStr.append("<option" + (isFirstChoice ? " selected" : "") + ">");
        outStr.append(choice.getDisplayValue());
        outStr.append("</option>\n");
        
        isFirstChoice = false;
      }
      
      outStr.append("</select>");
    }
    else if (limit.getRenderer().equals("multipleChoiceCheckboxes.jsp"))
    {
      int choiceNumber = 0;
      while (choicesIterator.hasNext())
      {
        if (choiceNumber > 0)
        {
          outStr.append("<br />\n");
        }
        
        Choice choice = (Choice)(choicesIterator.next());
        String choiceName = limitName + "_" + choiceNumber;
        outStr.append
          (" <input name=\"" + choiceName + "\""
           + " type=\"checkbox\" value=\"" + choice.getValue() + "\" />\n");
        outStr.append
        	("<label for=\"" + choiceName + "\">");
        outStr.append(choice.getDisplayValue());
        outStr.append("</label>\n");
        choiceNumber++;
      }
    }
    else
    {
      outStr.append
      	("ERROR: Unexpected value from limit.getRenderer(): "
      	 + limit.getRenderer());
    }
    
    return outStr.toString();
  }
  
  public static boolean isLimitValueParamName(String paramName)
  {
    return
    	(paramName.startsWith
    	    (MULTISELECT_LIMIT_VALUE_PARAMETER_PREFIX + DELIMITER)
    	 || paramName.startsWith
    	 		(SINGLESELECT_LIMIT_VALUE_PARAMETER_PREFIX + DELIMITER));
  }
  
  public static String makeLimitValueParamName
  	(Limit		limit,
  	 boolean 	isMultiSelect)
  throws SignetRuntimeException
  {
    return
      (isMultiSelect
    	  ? MULTISELECT_LIMIT_VALUE_PARAMETER_PREFIX
      	: SINGLESELECT_LIMIT_VALUE_PARAMETER_PREFIX)
     	 + DELIMITER
       + limit.getSubsystem().getId()
       + DELIMITER
       + limit.getId();
  }
  
  public static Limit getLimitByParamName
  	(Signet signet,
  	 String limitParamName)
  {
    StringTokenizer tokenizer = new StringTokenizer(limitParamName, DELIMITER);
    /* String prefix = */ tokenizer.nextToken();
    String subsystemId = tokenizer.nextToken();
    String limitId = tokenizer.nextToken();
    Limit limit = null;
    
    try
    {
      Subsystem subsystem = signet.getPersistentDB().getSubsystem(subsystemId);
      limit = subsystem.getLimit(limitId);
    }
    catch (ObjectNotFoundException onfe)
    {
      throw new SignetRuntimeException(onfe);
    }

    return limit;
  }
  
  static Set getAllLimitValues
	  (Signet signet, HttpServletRequest request)
  {
    Set limitValues = new HashSet();
    Enumeration paramNames = request.getParameterNames();
    while (paramNames.hasMoreElements())
    {
      String paramName = (String)(paramNames.nextElement());
      
      if (LimitRenderer.isLimitValueParamName(paramName))
      {
        Limit limit = LimitRenderer.getLimitByParamName(signet, paramName);
        String[] paramValues = request.getParameterValues(paramName);
        for (int i = 0; i < paramValues.length; i++)
        {
          String value = paramValues[i];
          LimitValue limitValue = new LimitValue(limit, value);
          limitValues.add(limitValue);
        }
      }
    }
    
    return limitValues;
  }
}