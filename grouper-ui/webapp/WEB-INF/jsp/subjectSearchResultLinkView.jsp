<%-- @annotation@
			Link to subject summary page
--%><%--
  @author Gary Brown.
  @version $Id: subjectSearchResultLinkView.jsp,v 1.3 2008-09-09 20:03:40 mchyzer Exp $
--%>
<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:importAttribute name="viewObject"/>
<c:set var="linkTitle"><fmt:message bundle="${nav}" key="browse.to.subject.summary">
		 		<fmt:param value="${viewObject.description}"/>
		</fmt:message></c:set>
<html:link page="/populateSubjectSummary.do" name="viewObject" title="${linkTitle}">
  <tiles:insert definition="dynamicTileDef" flush="false">
	  <tiles:put name="viewObject" beanName="viewObject"/>
	  <tiles:put name="view" value="subjectSearchResult"/>
  </tiles:insert>
</html:link>