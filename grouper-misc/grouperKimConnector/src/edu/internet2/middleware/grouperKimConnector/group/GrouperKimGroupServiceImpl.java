/**
 * @author mchyzer
 * $Id: GrouperKimGroupServiceImpl.java,v 1.6 2009-12-15 21:15:32 mchyzer Exp $
 */
package edu.internet2.middleware.grouperKimConnector.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.dto.GroupMembershipInfo;
import org.kuali.rice.kim.service.GroupService;

import edu.internet2.middleware.grouperClient.api.GcFindGroups;
import edu.internet2.middleware.grouperClient.api.GcGetGroups;
import edu.internet2.middleware.grouperClient.api.GcGetMembers;
import edu.internet2.middleware.grouperClient.api.GcHasMember;
import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;
import edu.internet2.middleware.grouperClient.ws.StemScope;
import edu.internet2.middleware.grouperClient.ws.WsMemberFilter;
import edu.internet2.middleware.grouperClient.ws.beans.WsFindGroupsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGroupsResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGroupsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembersResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembersResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroup;
import edu.internet2.middleware.grouperClient.ws.beans.WsHasMemberResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsHasMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsStemLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubject;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;
import edu.internet2.middleware.grouperClientExt.org.apache.commons.logging.Log;
import edu.internet2.middleware.grouperKimConnector.util.GrouperKimUtils;


/**
 * Implement the group service to delegate to grouper
 */
public class GrouperKimGroupServiceImpl implements GroupService {

  /**
   * logger
   */
  private static final Log LOG = GrouperClientUtils.retrieveLog(GrouperKimGroupServiceImpl.class);

  /**
   * <pre>
   * java.util.List<java.lang.String> getDirectGroupIdsForPrincipal(java.lang.String principalId)
   *
   * Get the groupIds in which the principal has direct membership only. 
   * @see org.kuali.rice.kim.service.GroupService#getDirectGroupIdsForPrincipal(java.lang.String)
   * </pre>
   */
  public List<String> getDirectGroupIdsForPrincipal(String principalId) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getDirectGroupIdsForPrincipal");
    debugMap.put("principalId", principalId);

    String sourceId = GrouperKimUtils.subjectSourceId();

    String stemName = GrouperKimUtils.kimStem();

    List<GroupInfo> groupInfos = getGroupsHelper(principalId, sourceId, stemName, 
        StemScope.ALL_IN_SUBTREE, WsMemberFilter.Immediate, debugMap);
    
    return GrouperKimUtils.convertGroupInfosToGroupIds(groupInfos);
  }

  /**
   * <pre>
   * getDirectMemberGroupIds
   *
   * java.util.List<java.lang.String> getDirectMemberGroupIds(java.lang.String groupId)
   *
   * Get all the groups which are direct members of the given group. 
   * 
   * @see org.kuali.rice.kim.service.GroupService#getDirectMemberGroupIds(java.lang.String)
   * </pre>
   */
  public List<String> getDirectMemberGroupIds(String groupId) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getDirectMemberGroupIds");
    debugMap.put("groupId", groupId);
    
    return getMemberIdsHelper(groupId, new String[]{"g:gsa"}, 
        WsMemberFilter.Immediate, debugMap);
  }

  /**
   * getDirectMemberPrincipalIds
   *
   * java.util.List<java.lang.String> getDirectMemberPrincipalIds(java.lang.String groupId)
   *
   * Get all the principals directly assigned to the given group.
   * @see org.kuali.rice.kim.service.GroupService#getDirectMemberPrincipalIds(java.lang.String)
   */
  public List<String> getDirectMemberPrincipalIds(String groupId) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getDirectMemberPrincipalIds");
    debugMap.put("groupId", groupId);
    
    return getMemberIdsHelper(groupId, GrouperKimUtils.subjectSourceIds(), 
        WsMemberFilter.Immediate, debugMap);
  }

  /**
   * <pre>
   * java.util.List<java.lang.String> getParentGroupIds(java.lang.String groupId)
   *
   * Get the groups which are parents of the given group.
   *
   * This will recurse into groups above the given group and build a complete list of all groups included above this group. 
   * @see org.kuali.rice.kim.service.GroupService#getDirectParentGroupIds(java.lang.String)
   * </pre>
   */
  public List<String> getDirectParentGroupIds(String groupId) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getDirectParentGroupIds");
    debugMap.put("groupId", groupId);

    String stemName = GrouperKimUtils.kimStem();

    List<GroupInfo> groupInfos = getGroupsHelper(groupId, "g:gsa", stemName, 
        StemScope.ALL_IN_SUBTREE, WsMemberFilter.Immediate, debugMap);
    
    return GrouperKimUtils.convertGroupInfosToGroupIds(groupInfos);
  }

  /**
   * <pre>
   * getGroupAttributes
   *
   * java.util.Map<java.lang.String,java.lang.String> getGroupAttributes(java.lang.String groupId)
   *
   * Get all the attributes of the given group. 
   * 
   * @see org.kuali.rice.kim.service.GroupService#getGroupAttributes(java.lang.String)
   * </pre>
   */
  public Map<String, String> getGroupAttributes(String groupId) {
    
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getGroupAttributes");
    
    Map<String, GroupInfo> resultMap = getGroupInfosHelper(GrouperClientUtils.toList(groupId), debugMap);
    
    if (GrouperClientUtils.length(resultMap) == 0) {
      return null;
    }

    if (resultMap.size() > 1) {
      throw new RuntimeException("Why is there more than one result when searching for " + groupId);
    }
    
    GroupInfo groupInfo = resultMap.get(groupId);
    return groupInfo.getAttributes();
    
  }

  /**
   * <pre>
   * getGroupIdsForPrincipal
   *
   * java.util.List<java.lang.String> getGroupIdsForPrincipal(java.lang.String principalId)
   *
   * Get all the groups for the given principal. Recurses into parent groups to provide a comprehensive list. 
   * @see org.kuali.rice.kim.service.GroupService#getGroupIdsForPrincipal(java.lang.String)
   * </pre>
   */
  public List<String> getGroupIdsForPrincipal(String principalId) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getGroupIdsForPrincipal");
    debugMap.put("principalId", principalId);

    String sourceId = GrouperKimUtils.subjectSourceId();

    String stemName = GrouperKimUtils.kimStem();

    List<GroupInfo> groupInfos = getGroupsHelper(principalId, sourceId, stemName, StemScope.ALL_IN_SUBTREE, null, debugMap);
    
    return GrouperKimUtils.convertGroupInfosToGroupIds(groupInfos);
  }

  /**
   * <pre>
   * getGroupIdsForPrincipalByNamespace
   *
   * java.util.List<java.lang.String> getGroupIdsForPrincipalByNamespace(java.lang.String principalId,
   *                                                                 java.lang.String namespaceCode)
   *
   * Get all the groups for the given principal in the given namespace. Recurses into parent groups to provide a comprehensive list. 
   * @see org.kuali.rice.kim.service.GroupService#getGroupIdsForPrincipalByNamespace(java.lang.String, java.lang.String)
   * </pre>
   */
  public List<String> getGroupIdsForPrincipalByNamespace(String principalId, String namespaceCode) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getGroupIdsForPrincipalByNamespace");
    debugMap.put("principalId", principalId);
    debugMap.put("namespaceCode", namespaceCode);

    String sourceId = GrouperKimUtils.subjectSourceId();

    String stemName = GrouperKimUtils.kimStem() + ":" + namespaceCode;

    List<GroupInfo> groupInfos = getGroupsHelper(principalId, sourceId, stemName, StemScope.ONE_LEVEL, null, debugMap);
    
    return GrouperKimUtils.convertGroupInfosToGroupIds(groupInfos);
  }

  /**
   * getGroupInfo
   *
   * GroupInfo getGroupInfo(java.lang.String groupId)
   *
   * Get the group by the given id. 
   * @see org.kuali.rice.kim.service.GroupService#getGroupInfo(java.lang.String)
   */
  public GroupInfo getGroupInfo(String groupId) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getGroupInfo");
    
    Map<String, GroupInfo> resultMap = getGroupInfosHelper(GrouperClientUtils.toList(groupId), debugMap);
    
    if (GrouperClientUtils.length(resultMap) == 0) {
      return null;
    }
    
    if (resultMap.size() > 1) {
      throw new RuntimeException("Why is there more than one result when searching for " + groupId);
    }

    //return the group;
    return resultMap.values().iterator().next();
  }

  /**
   * getGroupInfoByName
   *
   * GroupInfo getGroupInfoByName(java.lang.String namespaceCode,
   *                          java.lang.String groupName)
   *
   * Get the group by the given namesapce code and name. 
   * @see org.kuali.rice.kim.service.GroupService#getGroupInfoByName(java.lang.String, java.lang.String)
   */
  public GroupInfo getGroupInfoByName(String namespaceCode, String groupName) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getGroupInfoByName");
    debugMap.put("namespaceCode", namespaceCode);
    debugMap.put("groupName", groupName);
    
    boolean hadException = false;
    
    try {
      
      GcFindGroups gcFindGroups = new GcFindGroups();
      gcFindGroups.addGroupName(GrouperKimUtils.kimStem() + ":" + namespaceCode + ":" + groupName);
      
      WsFindGroupsResults wsFindGroupsResults = gcFindGroups.execute();
      
      //we did one assignment, we have one result
      WsGroup[] wsGroups = wsFindGroupsResults.getGroupResults();
      
      int numberOfGroups = GrouperClientUtils.length(wsGroups);
      
      debugMap.put("resultNumberOfGroups", numberOfGroups);
      
      if (numberOfGroups == 0) {
        return null;
      }
      
      if (numberOfGroups > 1) {
        throw new RuntimeException("Why is there more than 1 group returned?");
      }

      return GrouperKimUtils.convertWsGroupToGroupInfo(wsGroups[0]);
      
    } catch (RuntimeException re) {
      String errorPrefix = GrouperKimUtils.mapForLog(debugMap) + ", ";
      LOG.error(errorPrefix, re);
      GrouperClientUtils.injectInException(re, errorPrefix);
      hadException = true;
      throw re;
    } finally {
      if (LOG.isDebugEnabled() && !hadException) {
        LOG.debug(GrouperKimUtils.mapForLog(debugMap));
      }
    }

    
  }

  /**
   * java.util.Map<java.lang.String,GroupInfo> getGroupInfos(java.util.Collection<java.lang.String> groupIds)
   *
   * Gets all groups for the given collection of group ids.
   *
   * The result is a Map containing the group id as the key and the group info as the value. 
   * @see org.kuali.rice.kim.service.GroupService#getGroupInfos(java.util.Collection)
   */
  public Map<String, GroupInfo> getGroupInfos(Collection<String> groupIds) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getGroupInfos");
    
    return getGroupInfosHelper(groupIds, debugMap);
  }

  /**
   * get group info on a bunch of group ids
   * @param groupIds
   * @param debugMap 
   * @return the map of id to group
   */
  private Map<String, GroupInfo> getGroupInfosHelper(Collection<String> groupIds, Map<String, Object> debugMap) {
    int groupIdsSize = GrouperClientUtils.length(groupIds);
    debugMap.put("groupIds.size", groupIdsSize);
    Map<String, GroupInfo> result = new LinkedHashMap<String, GroupInfo>();
    if (groupIdsSize == 0) {
      return result;
    }
    boolean hadException = false;
    
    try {
      
      int index = 0;
      
      //log some of these
      for (String groupId : groupIds) {
        
        //dont log all...
        if (index > 20) {
          break;
        }
        
        debugMap.put("groupIds." + index, groupId);
        
        index++;
      }

      GcFindGroups gcFindGroups = new GcFindGroups();
      
      for (String groupId : groupIds) {
        gcFindGroups.addGroupUuid(groupId);
      }
      
      WsFindGroupsResults wsFindGroupsResults = gcFindGroups.execute();
      
      //we did one assignment, we have one result
      WsGroup[] wsGroups = wsFindGroupsResults.getGroupResults();
      
      debugMap.put("resultNumberOfGroups", GrouperClientUtils.length(wsGroups));
      
      index = 0;
      for (WsGroup wsGroup : GrouperClientUtils.nonNull(wsGroups, WsGroup.class)) {
        
        if (index < 20) {
          debugMap.put("groupResult." + index, wsGroup.getUuid() + ", " + wsGroup.getName());
        }
        
        GroupInfo groupInfo = GrouperKimUtils.convertWsGroupToGroupInfo(wsGroup);
        result.put(groupInfo.getGroupId(), groupInfo);
        index++;
      }
      
      return result;
      
    } catch (RuntimeException re) {
      String errorPrefix = GrouperKimUtils.mapForLog(debugMap) + ", ";
      LOG.error(errorPrefix, re);
      GrouperClientUtils.injectInException(re, errorPrefix);
      hadException = true;
      throw re;
    } finally {
      if (LOG.isDebugEnabled() && !hadException) {
        LOG.debug(GrouperKimUtils.mapForLog(debugMap));
      }
    }

  }
  
  /**
   * @see org.kuali.rice.kim.service.GroupService#getGroupMembers(java.util.List)
   */
  public Collection<GroupMembershipInfo> getGroupMembers(List<String> arg0) {
    return null;
  }

  /**
   * @see org.kuali.rice.kim.service.GroupService#getGroupMembersOfGroup(java.lang.String)
   */
  public Collection<GroupMembershipInfo> getGroupMembersOfGroup(String arg0) {
    return null;
  }

  /**
   * getGroupsForPrincipal
   *
   * java.util.List<GroupInfo> getGroupsForPrincipal(java.lang.String principalId)
   *
   * Get all the groups for a given principal.
   *
   * This will include all groups directly assigned as well as those inferred by the fact that they are members of higher level groups. 
   *    
   * @see org.kuali.rice.kim.service.GroupService#getGroupsForPrincipal(java.lang.String)
   */
  public List<GroupInfo> getGroupsForPrincipal(String principalId) {
    
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getGroupsForPrincipal");
    debugMap.put("principalId", principalId);

    String sourceId = GrouperKimUtils.subjectSourceId();

    String stemName = GrouperKimUtils.kimStem();

    return getGroupsHelper(principalId, sourceId, stemName, StemScope.ALL_IN_SUBTREE, null, debugMap);

  }

  /**
   * get groups for a subject
   * @param subjectId
   * @param sourceId or null to not specify
   * @param stemName to search in (required)
   * @param stemScope scope in stem
   * @param wsMemberFilter is if all, immediate, effective, etc  null means all
   * @param debugMap
   * @return the group infos
   */
  private List<GroupInfo> getGroupsHelper(String subjectId, String sourceId, String stemName, 
      StemScope stemScope, WsMemberFilter wsMemberFilter, Map<String, Object> debugMap) {
    boolean hadException = false;
    
    try {
      
      int index = 0;
      
      GcGetGroups gcGetGroups = new GcGetGroups();
      
      WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
      
      wsSubjectLookup.setSubjectId(subjectId);
      gcGetGroups.addSubjectLookup(wsSubjectLookup);
      
      debugMap.put("subjectId", subjectId);
      debugMap.put("sourceId", sourceId);

      if (!GrouperClientUtils.isBlank(sourceId)) {
        wsSubjectLookup.setSubjectSourceId(sourceId);
      }
      
      debugMap.put("stemName", stemName);
      debugMap.put("stemScope", stemScope == null ? null : stemScope.name());
      
      WsStemLookup wsStemLookup = new WsStemLookup(stemName, null);
      
      gcGetGroups.assignWsStemLookup(wsStemLookup);
      gcGetGroups.assignStemScope(stemScope);
      
      debugMap.put("wsMemberFilter", wsMemberFilter == null ? null : wsMemberFilter.name());
      gcGetGroups.assignMemberFilter(wsMemberFilter);
      
      WsGetGroupsResults wsGetGroupsResults = gcGetGroups.execute();
      WsGetGroupsResult[] wsGetGroupsResultArray = wsGetGroupsResults.getResults();
      
      int resultsSize = GrouperClientUtils.length(wsGetGroupsResultArray);
      
      debugMap.put("resultsArraySize", resultsSize);
      
      //not sure why this would ever be 0...
      if (resultsSize == 0) {
        return null;
      }
      
      if (resultsSize > 1) {
        throw new RuntimeException("Why is result array size more than 1?");
      }
      
      WsGetGroupsResult wsGetGroupsResult = wsGetGroupsResultArray[0];
      
      WsGroup[] wsGroups = wsGetGroupsResult.getWsGroups();
      resultsSize = GrouperClientUtils.length(wsGroups);
      debugMap.put("resultSize", resultsSize);
      List<GroupInfo> results = new ArrayList<GroupInfo>();
      
      index = 0;
      
      for (WsGroup wsGroup : wsGroups) {
        
        if (index < 20) {
          
          debugMap.put("result." + index, wsGroup.getUuid() + ", " + wsGroup.getName());
          
        }
        
        GroupInfo groupInfo = GrouperKimUtils.convertWsGroupToGroupInfo(wsGroup);
        results.add(groupInfo);
        
        
        index++;
      }
      
      return results;
      
    } catch (RuntimeException re) {
      String errorPrefix = GrouperKimUtils.mapForLog(debugMap) + ", ";
      LOG.error(errorPrefix, re);
      GrouperClientUtils.injectInException(re, errorPrefix);
      hadException = true;
      throw re;
    } finally {
      if (LOG.isDebugEnabled() && !hadException) {
        LOG.debug(GrouperKimUtils.mapForLog(debugMap));
      }
    }
  }

  /**
   * <pre>
   * getGroupsForPrincipalByNamespace
   *
   * java.util.List<GroupInfo> getGroupsForPrincipalByNamespace(java.lang.String principalId,
   *                                                       java.lang.String namespaceCode)
   *
   * Get all the groups within a namespace for a given principal.
   *
   * This is the same as the getGroupsForPrincipal(String) method except that the results will be filtered by namespace after retrieval.
   * </pre> 
   * @see org.kuali.rice.kim.service.GroupService#getGroupsForPrincipalByNamespace(java.lang.String, java.lang.String)
   */
  public List<GroupInfo> getGroupsForPrincipalByNamespace(String principalId, String namespaceCode) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getGroupsForPrincipalByNamespace");
    debugMap.put("principalId", principalId);
    debugMap.put("namespaceCode", namespaceCode);

    String sourceId = GrouperKimUtils.subjectSourceId();

    String stemName = GrouperKimUtils.kimStem() + ":" + namespaceCode;

    return getGroupsHelper(principalId, sourceId, stemName, StemScope.ONE_LEVEL, null, debugMap);
  }

  /**
   * <pre>
   * @see org.kuali.rice.kim.service.GroupService#getMemberGroupIds(java.lang.String)
   * </pre>
   */
  public List<String> getMemberGroupIds(String groupId) {
    
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getMemberPrincipalIds");
    debugMap.put("groupId", groupId);
    
    return getMemberIdsHelper(groupId, new String[]{"g:gsa"}, null, debugMap);

    
  }

  /**
   * <pre>
   * getMemberPrincipalIds
   * java.util.List<java.lang.String> getMemberPrincipalIds(java.lang.String groupId)
   *
   * Get all the principals of the given group. Recurses into contained groups to provide a comprehensive list. 
   * @see org.kuali.rice.kim.service.GroupService#getMemberPrincipalIds(java.lang.String)
   * </pre>
   */
  public List<String> getMemberPrincipalIds(String groupId) {
    
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getMemberPrincipalIds");
    debugMap.put("groupId", groupId);
    
    return getMemberIdsHelper(groupId, GrouperKimUtils.subjectSourceIds(), null, debugMap);
    
  }

  /**
   * get member ids from a group
   * @param groupId
   * @param sourceIds 
   * @param wsMemberFilter null for all, or immediate, or nonimmediate
   * @param debugMap 
   * @return the member ids
   */
  private List<String> getMemberIdsHelper(String groupId, String[] sourceIds, WsMemberFilter wsMemberFilter, Map<String, Object> debugMap ) {
    
    boolean hadException = false;
    
    try {
      
      int index = 0;
      
      GcGetMembers gcGetMembers = new GcGetMembers();
            
      debugMap.put("groupId", groupId);

      gcGetMembers.addGroupUuid(groupId);
      
      int sourceIdsLength = GrouperClientUtils.length(sourceIds);
      
      debugMap.put("sourceIds.length", sourceIdsLength);
      
      for (int i=0;i<sourceIdsLength;i++) {
        
        gcGetMembers.addSourceId(sourceIds[i]);
        
      }
      
      debugMap.put("wsMemberFilter", wsMemberFilter == null ? null : wsMemberFilter.name());

      gcGetMembers.assignMemberFilter(wsMemberFilter);
      
      WsGetMembersResults wsGetMembersResults = gcGetMembers.execute();
      WsGetMembersResult[] wsGetMembersResultArray = wsGetMembersResults.getResults();
      
      int resultsSize = GrouperClientUtils.length(wsGetMembersResultArray);
      
      debugMap.put("resultsArraySize", resultsSize);
      
      //not sure why this would ever be 0...
      if (resultsSize == 0) {
        throw new RuntimeException("Why is result size not 1?");
      }
      
      if (resultsSize > 1) {
        throw new RuntimeException("Why is result array size more than 1?");
      }
      
      WsGetMembersResult wsGetMembersResult = wsGetMembersResultArray[0];
      
      WsSubject[] wsSubjects = wsGetMembersResult.getWsSubjects();
      
      int wsSubjectsLength = GrouperClientUtils.length(wsSubjects);
      
      debugMap.put("wsSubjectsLength", wsSubjectsLength);

      if (wsSubjectsLength == 0) {
        return null;
      }

      List<String> results = new ArrayList<String>();
      
      for (int i=0;i<wsSubjectsLength;i++) {
        WsSubject wsSubject = wsSubjects[i];

        if (i < 20) {
          debugMap.put("result." + index, wsSubject.getId());
        }

        results.add(wsSubject.getId());
      }
      
      return results;
    } catch (RuntimeException re) {
      String errorPrefix = GrouperKimUtils.mapForLog(debugMap) + ", ";
      LOG.error(errorPrefix, re);
      GrouperClientUtils.injectInException(re, errorPrefix);
      hadException = true;
      throw re;
    } finally {
      if (LOG.isDebugEnabled() && !hadException) {
        LOG.debug(GrouperKimUtils.mapForLog(debugMap));
      }
    }
  }
  
  
  /**
   * <pre>
   * java.util.List<java.lang.String> getParentGroupIds(java.lang.String groupId)
   *
   * Get the groups which are parents of the given group.
   * 
   * This will recurse into groups above the given group and build a complete list of all groups included above this group. 
   * @param groupId 
   * @return the list of group ids
   * @see org.kuali.rice.kim.service.GroupService#getParentGroupIds(java.lang.String)
   </pre>
   */
  public List<String> getParentGroupIds(String groupId) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "getParentGroupIds");
    debugMap.put("groupId", groupId);

    String stemName = GrouperKimUtils.kimStem();

    List<GroupInfo> groupInfos = getGroupsHelper(groupId, "g:gsa", stemName, 
        StemScope.ALL_IN_SUBTREE, null, debugMap);
    
    return GrouperKimUtils.convertGroupInfosToGroupIds(groupInfos);
  }

  /**
   * <pre>
   * isDirectMemberOfGroup
   *
   * boolean isDirectMemberOfGroup(java.lang.String principalId,
   *                           java.lang.String groupId)
   *
   * Check whether the give principal is a member of the group.
   *
   * This will not recurse into contained groups. 
   * @see org.kuali.rice.kim.service.GroupService#isDirectMemberOfGroup(java.lang.String, java.lang.String)
   * </pre>
   */
  public boolean isDirectMemberOfGroup(String principalId, String groupId) {
    
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "isDirectMemberOfGroup");
    debugMap.put("principalId", principalId);
    debugMap.put("groupId", groupId);

    String sourceId = GrouperKimUtils.subjectSourceId();
    
    return isMemberHelper(principalId, sourceId, groupId, WsMemberFilter.Immediate, debugMap);
    
  }

  /**
   * 
   * @param subjectId
   * @param sourceId or null to search all
   * @param groupId
   * @param wsMemberFilter null for all, or immediate, etc
   * @param debugMap 
   * @return if has member
   */
  private boolean isMemberHelper(String subjectId, String sourceId, String groupId, WsMemberFilter wsMemberFilter, Map<String, Object> debugMap) {
    boolean hadException = false;
    
    debugMap.put("subjectId", subjectId);
    debugMap.put("sourceId", sourceId);
    debugMap.put("groupId", groupId);
    debugMap.put("wsMemberFilter", wsMemberFilter == null ? null : wsMemberFilter.name());
    
    try {
      
      GcHasMember gcHasMember = new GcHasMember();
      
      gcHasMember.assignGroupUuid(groupId);
      
      WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
      wsSubjectLookup.setSubjectId(subjectId);
      
      if (!GrouperClientUtils.isBlank(sourceId)) {
        wsSubjectLookup.setSubjectSourceId(sourceId);
      }
      
      gcHasMember.addSubjectLookup(wsSubjectLookup);
      
      gcHasMember.assignMemberFilter(wsMemberFilter);
      
      WsHasMemberResults wsHasMemberResults = gcHasMember.execute();
      WsHasMemberResult[] wsHasMemberResultArray = wsHasMemberResults.getResults();
      
      int resultsSize = GrouperClientUtils.length(wsHasMemberResultArray);
      
      debugMap.put("resultsArraySize", resultsSize);
      
      //not sure why this would ever be 0...
      if (resultsSize == 0) {
        throw new RuntimeException("Why would this not return an answer???");
      }
      
      if (resultsSize > 1) {
        throw new RuntimeException("Why is result array size more than 1?");
      }
      
      WsHasMemberResult wsHasMemberResult = wsHasMemberResultArray[0];
      
      String resultCode = wsHasMemberResult.getResultMetadata().getResultCode();
      debugMap.put("resultCode", resultCode);

      if (GrouperClientUtils.equals("IS_MEMBER", resultCode)) {
        return true;
      }
      if (GrouperClientUtils.equals("IS_NOT_MEMBER", resultCode)) {
        return false;
      }

      throw new RuntimeException("Not expecting result code: " + resultCode);

    } catch (RuntimeException re) {
      String errorPrefix = GrouperKimUtils.mapForLog(debugMap) + ", ";
      LOG.error(errorPrefix, re);
      GrouperClientUtils.injectInException(re, errorPrefix);
      hadException = true;
      throw re;
    } finally {
      if (LOG.isDebugEnabled() && !hadException) {
        LOG.debug(GrouperKimUtils.mapForLog(debugMap));
      }
    }

  }

  /**
   * boolean isGroupActive(java.lang.String groupId)
   *
   * Checks if the group with the given id is active. Returns true if it is, false otherwise. 
   * @see org.kuali.rice.kim.service.GroupService#isGroupActive(java.lang.String)
   */
  public boolean isGroupActive(String groupId) {
    
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "isGroupActive");
    
    Map<String, GroupInfo> resultMap = getGroupInfosHelper(GrouperClientUtils.toList(groupId), debugMap);
    
    if (GrouperClientUtils.length(resultMap) == 0) {
      return false;
    }

    if (resultMap.size() > 1) {
      throw new RuntimeException("Why is there more than one result when searching for " + groupId);
    }
    
    //if there is one, then we found it, it is active
    return true;
  }

  /**
   * <pre>
   * isGroupMemberOfGroup
   *
   * boolean isGroupMemberOfGroup(java.lang.String groupMemberId,
   *                          java.lang.String groupId)
   *
   * Check whether the group identified by groupMemberId is a member of the group identified by groupId. This will recurse through all groups. 
   * 
   * @see org.kuali.rice.kim.service.GroupService#isGroupMemberOfGroup(java.lang.String, java.lang.String)
   * </pre>
   */
  public boolean isGroupMemberOfGroup(String groupMemberId, String groupId) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "isGroupMemberOfGroup");
    debugMap.put("groupMemberId", groupMemberId);
    debugMap.put("groupId", groupId);

    return isMemberHelper(groupMemberId, "g:gsa", groupId, WsMemberFilter.Immediate, debugMap);
  }

  /**
   * <pre>
   * isMemberOfGroup
   *
   * boolean isMemberOfGroup(java.lang.String principalId,
   *                     java.lang.String groupId)
   *
   * Check whether the give principal is a member of the group.
   *
   * This will also return true if the principal is a member of a groups assigned to this group. 
   * 
   * @see org.kuali.rice.kim.service.GroupService#isMemberOfGroup(java.lang.String, java.lang.String)
   * </pre>
   */
  public boolean isMemberOfGroup(String principalId, String groupId) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    debugMap.put("operation", "isGroupMemberOfGroup");
    debugMap.put("principalId", principalId);
    debugMap.put("groupId", groupId);
    String sourceId = GrouperKimUtils.subjectSourceId();

    return isMemberHelper(principalId, sourceId, groupId, null, debugMap);
  }

  /**
   * @see org.kuali.rice.kim.service.GroupService#lookupGroupIds(java.util.Map)
   */
  public List<String> lookupGroupIds(Map<String, String> arg0) {
    throw new RuntimeException("Not implemented");
  }

  /**
   * @see org.kuali.rice.kim.service.GroupService#lookupGroups(java.util.Map)
   */
  public List<? extends Group> lookupGroups(Map<String, String> arg0) {
    throw new RuntimeException("Not implemented");
  }

}