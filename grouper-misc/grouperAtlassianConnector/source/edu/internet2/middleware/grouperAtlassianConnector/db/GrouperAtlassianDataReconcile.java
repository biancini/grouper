/**
 * @author mchyzer
 * $Id$
 */
package edu.internet2.middleware.grouperAtlassianConnector.db;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.StatefulJob;
import org.quartz.impl.StdSchedulerFactory;

import edu.internet2.middleware.grouperAtlassianConnector.GrouperAccessProvider;
import edu.internet2.middleware.grouperAtlassianConnector.GrouperAtlassianUtils;
import edu.internet2.middleware.grouperAtlassianConnector.GrouperProfileProvider;
import edu.internet2.middleware.grouperAwsChangelog.GrouperAwsSqsListener;
import edu.internet2.middleware.grouperAwsChangelog.GrouperSqsMessage;
import edu.internet2.middleware.grouperClient.collections.MultiKey;
import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;
import edu.internet2.middleware.grouperClientExt.org.apache.commons.logging.Log;
import edu.internet2.middleware.grouperClientExt.xmpp.EsbEvents;
import edu.internet2.middleware.grouperClientExt.xmpp.GcDecodeEsbEvents;




/**
 *
 */
public class GrouperAtlassianDataReconcile implements Job, StatefulJob {

  /**
   * @param args
   */
  public static void main(String[] args) {
    
    if (GrouperClientUtils.length(args) == 2
        && GrouperClientUtils.equals(args[0], "full")
        && GrouperClientUtils.equals(args[1], "readonly")) {
      new GrouperAtlassianDataReconcile().reconcileGrouperAndAtlassian(true);
    } else if (GrouperClientUtils.length(args) == 2
          && GrouperClientUtils.equals(args[0], "full")
          && GrouperClientUtils.equals(args[1], "notReadonly")) {
      new GrouperAtlassianDataReconcile().reconcileGrouperAndAtlassian(false);
    } else {
      scheduleAtlassianFullRefreshJob();
      listenForAwsRefreshes();
    }
    
    
    
    //  //lets list users
    //  for (AtlassianCwdUser atlassianCwdUser : AtlassianCwdUser.retrieveUsers().values()) {
    //    System.out.println(atlassianCwdUser.toString());
    //  }
    
//    new GcDbAccess().callbackConnection(new GcConnectionCallback() {
//
//      @Override
//      public Object callback(Connection connection) {
//
//        AtlassianCwdUser atlassianCwdUser = new AtlassianCwdUser();
//        atlassianCwdUser.initNewObject();
//        
//        atlassianCwdUser.setDisplayName("Test User");
//        atlassianCwdUser.setEmailAddress("a@b.d");
//        atlassianCwdUser.setLowerDisplayName("test user");
//        atlassianCwdUser.setLowerEmailAddress("a@b.d");
//        atlassianCwdUser.setLowerUserName("testuser1");
//        atlassianCwdUser.setUserName("testuser1");
//        atlassianCwdUser.store();
//
//        
//        new GcDbAccess().callbackTransaction(new GcTransactionCallback<Void>() {
//
//          /**
//           * 
//           * @see edu.internet2.middleware.grouperClient.jdbc.GcTransactionCallback#callback(edu.internet2.middleware.grouperClient.jdbc.GcDbAccess)
//           */
//          @Override
//          public Void callback(GcDbAccess dbAccess) {
//
//            AtlassianCwdUser atlassianCwdUser2 = new AtlassianCwdUser();
//            atlassianCwdUser2.initNewObject();
//            
//            atlassianCwdUser2.setDisplayName("Test User");
//            atlassianCwdUser2.setEmailAddress("a@b.c");
//            atlassianCwdUser2.setLowerDisplayName("test user");
//            atlassianCwdUser2.setLowerEmailAddress("a@b.c");
//            atlassianCwdUser2.setLowerUserName("testuser");
//            atlassianCwdUser2.setUserName("testuser");
//            atlassianCwdUser2.store();
//            return null;
//          }
//          
//        });
//        
//        new GcDbAccess().callbackTransaction(new GcTransactionCallback<Void>() {
//
//          /**
//           * 
//           * @see edu.internet2.middleware.grouperClient.jdbc.GcTransactionCallback#callback(edu.internet2.middleware.grouperClient.jdbc.GcDbAccess)
//           */
//          @Override
//          public Void callback(GcDbAccess dbAccess) {
//
//            AtlassianCwdUser atlassianCwdUser2 = new AtlassianCwdUser();
//            atlassianCwdUser2.initNewObject();
//            
//            atlassianCwdUser2.setDisplayName("Test User");
//            atlassianCwdUser2.setEmailAddress("a@b.c");
//            atlassianCwdUser2.setLowerDisplayName("test user");
//            atlassianCwdUser2.setLowerEmailAddress("a@b.c");
//            atlassianCwdUser2.setLowerUserName("testuse2");
//            atlassianCwdUser2.setUserName("testuse2");
//            atlassianCwdUser2.store();
//            
//            //2 shouldnt make it, but 3 should
//            GcDbAccess.transactionEnd(GcTransactionEnd.rollback, false);
//            
//            atlassianCwdUser2 = new AtlassianCwdUser();
//            atlassianCwdUser2.initNewObject();
//            
//            atlassianCwdUser2.setDisplayName("Test User");
//            atlassianCwdUser2.setEmailAddress("a@b.c");
//            atlassianCwdUser2.setLowerDisplayName("test user");
//            atlassianCwdUser2.setLowerEmailAddress("a@b.c");
//            atlassianCwdUser2.setLowerUserName("testuser3");
//            atlassianCwdUser2.setUserName("testuser3");
//            atlassianCwdUser2.store();
//            
//            return null;
//          }
//          
//        });
//        
//        new GcDbAccess().callbackTransaction(new GcTransactionCallback<Void>() {
//
//          /**
//           * 
//           * @see edu.internet2.middleware.grouperClient.jdbc.GcTransactionCallback#callback(edu.internet2.middleware.grouperClient.jdbc.GcDbAccess)
//           */
//          @Override
//          public Void callback(GcDbAccess dbAccess) {
//
//            AtlassianCwdUser atlassianCwdUser2 = new AtlassianCwdUser();
//            atlassianCwdUser2.initNewObject();
//            
//            atlassianCwdUser2.setDisplayName("Test User");
//            atlassianCwdUser2.setEmailAddress("a@b.c");
//            atlassianCwdUser2.setLowerDisplayName("test user");
//            atlassianCwdUser2.setLowerEmailAddress("a@b.c");
//            atlassianCwdUser2.setLowerUserName("testuse4");
//            atlassianCwdUser2.setUserName("testuse4");
//            atlassianCwdUser2.store();
//            
//            //4 should make it, but 5 shouldnt
//            GcDbAccess.transactionEnd(GcTransactionEnd.commit, false);
//            
//            atlassianCwdUser2 = new AtlassianCwdUser();
//            atlassianCwdUser2.initNewObject();
//            
//            atlassianCwdUser2.setDisplayName("Test User");
//            atlassianCwdUser2.setEmailAddress("a@b.c");
//            atlassianCwdUser2.setLowerDisplayName("test user");
//            atlassianCwdUser2.setLowerEmailAddress("a@b.c");
//            atlassianCwdUser2.setLowerUserName("testuser5");
//            atlassianCwdUser2.setUserName("testuser5");
//            atlassianCwdUser2.store();
//
//            throw new RuntimeException("What?");
//            
//          }
//          
//        });
//
//
//        return null;
//      }
//    });

    
//    AtlassianCwdUser atlassianCwdUser = new AtlassianCwdUser();
//    atlassianCwdUser.initNewObject();
//    atlassianCwdUser.setUserName("testuser");
//
//    atlassianCwdUser.store();
    
//    AtlassianCwdUser atlassianCwdUser = new DbAccess().primaryKey(10621L).select(AtlassianCwdUser.class);
//    
//    atlassianCwdUser.setDisplayName("Test User3");
//    
//    atlassianCwdUser.store();
//    
//    atlassianCwdUser.delete();
  }

  /**
   * 
   */
  public static void listenForAwsRefreshes() {
    while(true) {
      
      try {
        
        List<GrouperSqsMessage> grouperSqsMessages = GrouperAwsSqsListener.checkMessages(true);
        
        LOG.info("Received " + GrouperClientUtils.length(grouperSqsMessages) + " message(s) from Grouper");
        
        //if we get a message, do a full refresh
        new GrouperAtlassianDataReconcile().reconcileGrouperAndAtlassian(false);
        
        for (GrouperSqsMessage grouperSqsMessage : GrouperClientUtils.nonNull(grouperSqsMessages)) {
          
          EsbEvents esbEvents = GcDecodeEsbEvents.decodeEsbEvents(grouperSqsMessage.getMessageBody());

          esbEvents = GcDecodeEsbEvents.unencryptEsbEvents(esbEvents);
          
          GrouperAwsSqsListener.deleteMessage(grouperSqsMessage.getReceiptHandle());
        }
        
      } catch (Exception e) {
        LOG.error("error", e);
      }

      //wait some time in between runs
      GrouperClientUtils.sleep(30000);
    }
  }
  
  /**
   * schedule full refresh job
   */
  public static void scheduleAtlassianFullRefreshJob() {
    
    String cronString = GrouperClientUtils.propertiesValue("atlassian.fullRefreshAws.quartz.cron", false);
    
    //if not specified dont run
    if (GrouperClientUtils.isBlank(cronString)) {
      LOG.warn("No cron for full refresh in config file, not scheduling it: atlassian.fullRefreshAws.quartz.cron");
      return;
    }

    String jobName = "fullRefresh_atlassianGrouper";

    scheduleJob(jobName, cronString, GrouperAtlassianDataReconcile.class);

    
  }
  
  /**
   * schedule a cron job
   * @param jobName something unique and descriptive
   * @param quartzCronString
   * @param jobClass
   */
  public static void scheduleJob(String jobName, String quartzCronString, Class<? extends Job> jobClass) {

    //no cron string, dont run the cron
    if (GrouperClientUtils.isBlank(quartzCronString)) {
      return;
    }
    
    String jobGroup = Scheduler.DEFAULT_GROUP;
    JobDetail jobDetail = new JobDetail(jobName,
        jobGroup, jobClass);

    Scheduler scheduler = scheduler();

    //atlassian requires durable jobs...
    jobDetail.setDurability(true);

    boolean uniqueTriggerNames = GrouperClientUtils.propertiesValueBoolean("grouperClient.atlassian.uniqueQuartzTriggerNames", false, false);

    //in old versions of quartz, the trigger group cannot be null
    String triggerName = "trigger_" + jobName;

    if (uniqueTriggerNames) {
      triggerName += GrouperClientUtils.uniqueId();
    }

    CronTrigger cronTrigger = null;
    if (!uniqueTriggerNames) {
      try {
        cronTrigger = (CronTrigger) scheduler.getTrigger(triggerName, jobGroup);
      } catch (SchedulerException se) {
        throw new RuntimeException("Problem with trigger: " + jobName, se);
      }
    }
    if (cronTrigger == null) {
      cronTrigger = new CronTrigger(triggerName, jobGroup);
    }

    try {
      cronTrigger.setCronExpression(quartzCronString);
    } catch (ParseException pe) {
      throw new RuntimeException("Problems parsing: '" + quartzCronString + "'", pe);
    }

    try {
      scheduler.scheduleJob(jobDetail, cronTrigger);
    } catch (SchedulerException se) {
      throw new RuntimeException("Problem with job: " + jobName, se);
    }


  }

  /**
   * scheduler
   * @return scheduler
   */
  public static Scheduler scheduler() {
    try {
      return schedulerFactory().getScheduler();
    } catch (SchedulerException se) {
      throw new RuntimeException(se);
    }
  }

  /**
  * scheduler factory singleton
  */
  private static SchedulerFactory schedulerFactory = null;

  /**
  * lazy load (and start the scheduler) the scheduler factory
  * @return the scheduler factory
  */
  public static SchedulerFactory schedulerFactory() {
    if (schedulerFactory == null) {
      schedulerFactory = new StdSchedulerFactory();
      try {
        schedulerFactory.getScheduler().start();
      } catch (SchedulerException se) {
        throw new RuntimeException(se);
      }
    }
    return schedulerFactory;
  }

  
  /**
   * 
   * @param readonly
   */
  public void reconcileGrouperAndAtlassian(boolean readonly) {
    
    //dont run this multiple times at the same time
    synchronized (GrouperAtlassianDataReconcile.class) {
    
      this.retrieveAllFromAtlassian();
      this.retrieveAllFromGrouper();
      
      reconcileGroups(readonly);    
      
      reconcilePeople(readonly);
  
      reconcileMemberships(readonly);
    }
    
  }

  
  /**
   * assume groups are reconciled before this
   * @param readonly
   */
  private void reconcileMemberships(boolean readonly) {
    
    //get all groups
    //if we have reconciled groups, then they should be in here...
    for (String groupName : GrouperClientUtils.nonNull(this.atlassianGroupnameToGroupMap).keySet()) {
      
      Set<String> grouperUsersInGroup = GrouperClientUtils.nonNull(this.grouperGroupNameToUserNames.get(groupName));
      Set<String> atlassianUsersInGroup = GrouperClientUtils.nonNull(this.atlassianGroupnameToUserSetMap.get(groupName));
      
      Set<String> usersToAdd = new HashSet<String>(grouperUsersInGroup);
      usersToAdd.removeAll(atlassianUsersInGroup);

      for (String username : usersToAdd) {

        AtlassianCwdUser atlassianCwdUser = this.atlassianUsernameToUserMap.get(username);
        AtlassianCwdGroup atlassianCwdGroup = this.atlassianGroupnameToGroupMap.get(groupName);
        
        if (atlassianCwdUser == null) {
          //it isnt in the user group forget it
          continue;
        }
        
        AtlassianCwdMembership atlassianCwdMembership = new AtlassianCwdMembership();
        atlassianCwdMembership.initNewObject();
        atlassianCwdMembership.setChildId(atlassianCwdUser.getId());
        atlassianCwdMembership.setChildName(atlassianCwdUser.getUserName());
        atlassianCwdMembership.setLowerChildName(GrouperClientUtils.defaultString(atlassianCwdUser.getUserName()).toLowerCase());
        atlassianCwdMembership.setParentId(atlassianCwdGroup.getId());
        atlassianCwdMembership.setParentName(atlassianCwdGroup.getGroupName());
        atlassianCwdMembership.setLowerParentName(GrouperClientUtils.defaultString(atlassianCwdGroup.getGroupName()).toLowerCase());
        
        //memberships
        if (readonly) {
          
          System.out.println("Adding " + username + " to group " + groupName);
          
        } else {
          
          LOG.info("Adding " + username + " to group " + groupName);
          atlassianCwdMembership.store();
          
        }

      }
      
      Set<String> usersToRemove = new HashSet<String>(atlassianUsersInGroup);
      usersToRemove.removeAll(grouperUsersInGroup);

      for (String username : usersToRemove) {

        AtlassianCwdUser atlassianCwdUser = this.atlassianUsernameToUserMap.get(username);
        AtlassianCwdGroup atlassianCwdGroup = this.atlassianGroupnameToGroupMap.get(groupName);
        
        MultiKey multiKey = new MultiKey(atlassianCwdGroup.getGroupName(), atlassianCwdUser.getUserName());
        
        AtlassianCwdMembership atlassianCwdMembership = this.atlassianGroupAndUserToMembershipMap.get(multiKey);
        
        //memberships
        if (readonly) {
          
          System.out.println("Removing " + username + " from group " + groupName + ", membershipId: " + atlassianCwdMembership.getId());
          
        } else {
          
          LOG.info("Removing " + username + " from group " + groupName + ", membershipId: " + atlassianCwdMembership.getId());
          atlassianCwdMembership.delete();
          
        }

      }
      

    }
    
  }

  /**
   * @param readonly
   */
  private void reconcilePeople(boolean readonly) {
    //reconcile people
    Set<AtlassianCwdUser> usersToAddOrEdit = new HashSet<AtlassianCwdUser>();
    for (String username : this.grouperUserNameToUser.keySet()) {
      
      AtlassianCwdUser grouperUser = this.grouperUserNameToUser.get(username);
      AtlassianCwdUser atlassianUser = this.atlassianUsernameToUserMap.get(username);
      
      if (atlassianUser == null) {
        usersToAddOrEdit.add(grouperUser);
      } else {
        //check the name or email address for changes
        boolean hasChange = false;
        if (!GrouperClientUtils.equals(grouperUser.getDisplayName(), atlassianUser.getDisplayName())) {
          atlassianUser.setDisplayName(grouperUser.getDisplayName());
          atlassianUser.setLowerDisplayName(GrouperClientUtils.defaultString(grouperUser.getDisplayName()).toLowerCase());
          hasChange = true;
        }
        if (!GrouperClientUtils.equalsIgnoreCase(grouperUser.getEmailAddress(), atlassianUser.getEmailAddress())) {
          atlassianUser.setEmailAddress(grouperUser.getEmailAddress());
          atlassianUser.setLowerEmailAddress(GrouperClientUtils.defaultString(grouperUser.getEmailAddress()).toLowerCase());
          hasChange = true;
        }
        //if has change, then store it
        if (hasChange) {
          usersToAddOrEdit.add(atlassianUser);
        }
      }
    }
    
    //store
    for (AtlassianCwdUser atlassianCwdUser : usersToAddOrEdit) {

      this.atlassianUsernameToUserMap.put(atlassianCwdUser.getUserName(), atlassianCwdUser);
      
      if (readonly) {
        System.out.println((atlassianCwdUser.getId() == null ? "Adding" : "Updating") 
            + " user: " + atlassianCwdUser.getUserName());
      } else {
        LOG.info((atlassianCwdUser.getId() == null ? "Adding" : "Updating") 
            + " user: " + atlassianCwdUser.getUserName());
        atlassianCwdUser.store();
      }
    }
    
    //dont delete users, just remove members
  }

  /**
   * @param readonly
   */
  private void reconcileGroups(boolean readonly) {
    //reconcile groups
    //do inserts
    Set<String> groupNamesToAddToAtlassian = new HashSet<String>(GrouperClientUtils.nonNull(this.grouperGroupNames));
    
    groupNamesToAddToAtlassian.removeAll(this.atlassianGroupnameToGroupMap.keySet());
    
    for (String groupNameToAdd : groupNamesToAddToAtlassian) {
      
      AtlassianCwdGroup atlassianCwdGroup = new AtlassianCwdGroup();
      atlassianCwdGroup.initNewObject();
      atlassianCwdGroup.setGroupName(groupNameToAdd);
      atlassianCwdGroup.setLowerGroupName(GrouperClientUtils.defaultString(groupNameToAdd).toLowerCase());

      //add to list of groups
      this.atlassianGroupnameToGroupMap.put(groupNameToAdd, atlassianCwdGroup);

      if (readonly) {
        System.out.println("Adding group: " + groupNameToAdd);
      } else {
        LOG.info("Adding group: " + groupNameToAdd);
        atlassianCwdGroup.store();
        
      }
    }
    
    //dont delete groups, just remove members
  }

  /**
   * group names in grouper
   */
  private Set<String> grouperGroupNames = null;
  
  /**
   * map of user name to user in grouper
   */
  private Map<String, AtlassianCwdUser> grouperUserNameToUser = null;

  /**
   * map of group name to list of user names
   */
  private Map<String, Set<String>> grouperGroupNameToUserNames = null;
  
  /**
   * get all data from grouper
   */
  public void retrieveAllFromGrouper() {
    
    GrouperAccessProvider grouperAccessProvider = new GrouperAccessProvider();
    GrouperProfileProvider grouperProfileProvider = new GrouperProfileProvider();
    
    grouperAccessProvider.flushCaches();
    grouperProfileProvider.flushCaches();
    
    List<String> groupNames = grouperAccessProvider.list();
    this.grouperGroupNames = new HashSet<String>(groupNames);
    
    List<String> userNames = grouperProfileProvider.list();
    
    this.grouperUserNameToUser = new HashMap<String, AtlassianCwdUser>();
    
    for (String username : GrouperClientUtils.nonNull(userNames)) {
      Map<String, String> propertySet = grouperProfileProvider.getPropertySet(username);
      
      String email = propertySet.get(GrouperAtlassianUtils.PROPERTY_SET_EMAIL);
      String name = propertySet.get(GrouperAtlassianUtils.PROPERTY_SET_NAME);
      
      AtlassianCwdUser atlassianCwdUser = new AtlassianCwdUser();
      atlassianCwdUser.initNewObject();
      atlassianCwdUser.setDisplayName(name);
      atlassianCwdUser.setEmailAddress(email);
      atlassianCwdUser.setLowerDisplayName(GrouperClientUtils.defaultString(name).toLowerCase());
      atlassianCwdUser.setLowerEmailAddress(GrouperClientUtils.defaultString(email).toLowerCase());
      atlassianCwdUser.setUserName(username);
      atlassianCwdUser.setLowerUserName(GrouperClientUtils.defaultString(username).toLowerCase());
      this.grouperUserNameToUser.put(username, atlassianCwdUser);
    }

    this.grouperGroupNameToUserNames = new HashMap<String, Set<String>>();
    
    for (String groupName : GrouperClientUtils.nonNull(this.grouperGroupNames)) {

      HashSet<String> users = new HashSet<String>(GrouperClientUtils.nonNull(
          grouperAccessProvider.listUsersInGroup(groupName)));
      this.grouperGroupNameToUserNames.put(groupName, 
          users);
      
    }

  }
  
  /**
   * username to user map
   */
  private Map<String, AtlassianCwdUser> atlassianUsernameToUserMap = null;

  /**
   * groupname to group map
   */
  private Map<String, AtlassianCwdGroup> atlassianGroupnameToGroupMap = null;

  /**
   * groupname to group map
   */
  private Map<String, Set<String>> atlassianGroupnameToUserSetMap = null;

  /**
   * group and user to membership
   */
  private Map<MultiKey, AtlassianCwdMembership> atlassianGroupAndUserToMembershipMap = null;

  /**
   * logger
   */
  private static Log LOG = GrouperClientUtils.retrieveLog(GrouperAtlassianDataReconcile.class);

  /**
   * get all data from atlassian
   */
  public void retrieveAllFromAtlassian() {

    this.atlassianUsernameToUserMap = AtlassianCwdUser.retrieveUsers();
    
    this.atlassianGroupnameToGroupMap = AtlassianCwdGroup.retrieveGroups();

    this.atlassianGroupAndUserToMembershipMap = new HashMap<MultiKey, AtlassianCwdMembership>();
    this.atlassianGroupnameToUserSetMap = new HashMap<String, Set<String>>();
    
    List<AtlassianCwdMembership> memberships = AtlassianCwdMembership.retrieveMemberships();
    
    for (AtlassianCwdMembership atlassianCwdMembership : GrouperClientUtils.nonNull(memberships)) {

      //keep this in a map to lookup the membership
      this.atlassianGroupAndUserToMembershipMap.put(
          new MultiKey(atlassianCwdMembership.getParentName(), atlassianCwdMembership.getChildName()),
          atlassianCwdMembership);
      
      //keep a set of usernames for this group
      Set<String> usernames = this.atlassianGroupnameToUserSetMap.get(atlassianCwdMembership.getParentName());
      if (usernames == null) {
        
        usernames = new HashSet<String>();
        this.atlassianGroupnameToUserSetMap.put(atlassianCwdMembership.getParentName(), usernames);
        
      }
      
      usernames.add(atlassianCwdMembership.getChildName());
    }
  
  }

  /**
   * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
   */
  public void execute(JobExecutionContext context) throws JobExecutionException {
    LOG.debug("Running full refresh from cron");
    reconcileGrouperAndAtlassian(false);
  }
  
}
