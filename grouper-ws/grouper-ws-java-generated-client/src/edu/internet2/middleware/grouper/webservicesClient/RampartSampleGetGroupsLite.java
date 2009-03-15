package edu.internet2.middleware.grouper.webservicesClient;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;

import edu.internet2.middleware.grouper.webservicesClient.util.GeneratedClientSettings;
import edu.internet2.middleware.grouper.ws.samples.types.WsSampleGenerated;
import edu.internet2.middleware.grouper.ws.samples.types.WsSampleGeneratedType;
import edu.internet2.middleware.grouper.ws.soap.xsd.GetGroupsLite;
import edu.internet2.middleware.grouper.ws.soap.xsd.WsGetGroupsLiteResult;
import edu.internet2.middleware.grouper.ws.soap.xsd.WsGroup;


/**
 * sample rampart call
 */
public class RampartSampleGetGroupsLite implements WsSampleGenerated {
    /**
     * @see edu.internet2.middleware.grouper.ws.samples.types.WsSampleGenerated#executeSample(edu.internet2.middleware.grouper.ws.samples.types.WsSampleGeneratedType)
     */
    public void executeSample(WsSampleGeneratedType wsSampleGeneratedType) {
        getGroupsLite(wsSampleGeneratedType);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        getGroupsLite(WsSampleGeneratedType.soap);
    }

    /**
     * @param wsSampleGeneratedType can run as soap or xml/http
     */
    public static void getGroupsLite(
        WsSampleGeneratedType wsSampleGeneratedType) {
        try {
            //URL, e.g. http://localhost:8093/grouper-ws/servicesWssec/GrouperServiceWssec
            ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromFileSystem(GeneratedClientSettings.fileFromResourceName(
                        "edu/internet2/middleware/grouper/webservicesClient/rampart")
                                                                                                                                   .getAbsolutePath(),
                    null);
            GrouperServiceStub stub = new GrouperServiceStub(ctx,
                    GeneratedClientSettings.URL);

            Options options = stub._getServiceClient().getOptions();

            options.setProperty(HTTPConstants.SO_TIMEOUT, new Integer(3600000));
            options.setProperty(HTTPConstants.CONNECTION_TIMEOUT,
                new Integer(3600000));

            String policyFilePath = GeneratedClientSettings.fileFromResourceName(
                    "edu/internet2/middleware/grouper/webservicesClient/rampart/policy.xml")
                                                           .getAbsolutePath();

            StAXOMBuilder builder = new StAXOMBuilder(policyFilePath);
            Policy policy = PolicyEngine.getPolicy(builder.getDocumentElement());

            options.setProperty(RampartMessageData.KEY_RAMPART_POLICY, policy);

            options.setUserName("GrouperSystem");

            //stub._getServiceClient().engageModule("addressing");
            stub._getServiceClient().engageModule("rampart");

            GetGroupsLite getGroupsLite = GetGroupsLite.class.newInstance();

            //version, e.g. v1_3_000
            getGroupsLite.setClientVersion(GeneratedClientSettings.VERSION);

            getGroupsLite.setActAsSubjectId("GrouperSystem");
            getGroupsLite.setActAsSubjectIdentifier("");
            getGroupsLite.setActAsSubjectSourceId("");

            // check all
            getGroupsLite.setMemberFilter("All");

            getGroupsLite.setSubjectId("GrouperSystem");
            getGroupsLite.setSubjectIdentifier("");
            getGroupsLite.setSubjectSourceId("");

            WsGetGroupsLiteResult wsGetGroupsLiteResult = stub.getGroupsLite(getGroupsLite)
                                                              .get_return();

            System.out.println(ToStringBuilder.reflectionToString(
                    wsGetGroupsLiteResult));

            WsGroup[] results = wsGetGroupsLiteResult.getWsGroups();

            if (results != null) {
                for (WsGroup wsGroup : results) {
                    System.out.println(ToStringBuilder.reflectionToString(
                            wsGroup));
                }
            }
            
            if (!StringUtils.equals("T", 
                wsGetGroupsLiteResult.getResultMetadata().getSuccess())) {
              throw new RuntimeException("didnt get success! ");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}