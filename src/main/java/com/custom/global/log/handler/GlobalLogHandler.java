package com.custom.global.log.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.AbstractSynapseHandler;
import org.apache.synapse.MessageContext;


public class GlobalLogHandler extends AbstractSynapseHandler {

    Log log = LogFactory.getLog(GlobalLogHandler.class);

    public GlobalLogHandler() {
    }

    /**
     * @param messageContext synapse messageContext
     * @return requext Inflow time
     */
    public boolean handleRequestInFlow(MessageContext messageContext) {


        messageContext.setProperty("request.execution.start.time", Long.toString(System.currentTimeMillis()));

        String apiMethod = LogUtils.getRestMethod(messageContext);

        messageContext.setProperty("API_METHOD", apiMethod);

        return true;
    }

    /**
     * @param messageContext synapse messageContext
     * @return request outflow time
     */
    public boolean handleRequestOutFlow(MessageContext messageContext) {
        try {

            messageContext.setProperty("api.ut.log.backendRequestTime", Long.toString(System.currentTimeMillis()));

            String apiTo = LogUtils.getTo(messageContext);

            messageContext.setProperty("BACKEND_URL", apiTo);

            return true;
        } catch (Exception e) {
            log.error("Cannot publish request event. " + e.getMessage(), e);
        }

        return true;
    }

    /**
     * @param messageContext synapse messageContext
     * @return response Inflow time
     */
    public boolean handleResponseInFlow(MessageContext messageContext) {
        try {

            messageContext.setProperty("api.ut.log.backendRequestEndTime", Long.toString(System.currentTimeMillis()));

            return true;
        } catch (Exception e) {
            log.error("Cannot publish response event. " + e.getMessage(), e);
        }
        return true;
    }

    /**
     * @param messageContext synapse messageContext
     * @return response outflow time
     */
    public boolean handleResponseOutFlow(MessageContext messageContext) {
        try {
            long requestInTime = 0;
            long requestOutTime = 0;
            long responseInTime = 0;

            String inflowLatency = "-";
            String outflowLatency = "-";
            String backendLatency = "-";
            String roundTripLatency = "-";

            String apiMethod = (String) messageContext.getProperty("API_METHOD");
            String apiName = LogUtils.getAPIName(messageContext);
            String apiTo = (String) messageContext.getProperty("BACKEND_URL");
            String apiFullRequestPath = (String) messageContext.getProperty("REST_FULL_REQUEST_PATH");
            String apiResponseSC = LogUtils.getRestHttpResponseStatusCode(messageContext);

            String requestInTimeValue = (String) messageContext.getProperty("request.execution.start.time");
            String requestOutTimeValue = (String) messageContext.getProperty("api.ut.log.backendRequestTime");
            String responseInTimeValue = (String) messageContext.getProperty("api.ut.log.backendRequestEndTime");
            if (requestInTimeValue != null && !requestInTimeValue.isEmpty()) {
                requestInTime = Long.parseLong((String) requestInTimeValue);
            }
            if (requestOutTimeValue != null && !requestOutTimeValue.isEmpty()) {
                requestOutTime = Long.parseLong((String) requestOutTimeValue);
            }
            if (responseInTimeValue != null && !responseInTimeValue.isEmpty()) {
                responseInTime = Long.parseLong((String) responseInTimeValue);
            }

            if (requestInTime <= requestOutTime && requestInTime != 0) {
                inflowLatency = Long.toString(requestOutTime - requestInTime);
            }
            if (responseInTime <= System.currentTimeMillis() && responseInTime != 0) {
                outflowLatency = Long.toString(System.currentTimeMillis() - responseInTime);
            }
            if (requestOutTime <= responseInTime && requestOutTime != 0) {
                backendLatency = Long.toString(responseInTime - requestOutTime);
            }
            if (requestInTime <= System.currentTimeMillis() && requestInTime != 0) {
                roundTripLatency = Long.toString(System.currentTimeMillis() - requestInTime);
            }


            log.info("|" + apiName + "|" + apiMethod + "|" + apiFullRequestPath + "|"
                    + apiTo + "|" + apiResponseSC + "|" + inflowLatency + "|" + backendLatency
                    + "|" + outflowLatency + "|" + roundTripLatency);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return true;
    }


}

