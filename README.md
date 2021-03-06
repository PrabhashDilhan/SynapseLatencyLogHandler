# SynapseLatencyLogHandler
This synapse log handler can be used to log the inflowLatency, backendLatency, outflowLatency and roundTripLatency in milliseconds.

### How to use.
1. Build the GlobalLogHandler source using ```mvn clean install``` command.
2. Copy the JAR file into __<APIM_HOME>/repository/components/lib__ directory.
3. enable the handler in __<APIM_HOME>/repository/conf/deployment.toml__ file as mentioned in [WSO2 Document](https://apim.docs.wso2.com/en/3.1.0/reference/config-catalog/#synapse-handlers).
4. Add global log handler logger to the __<APIM_HOME>/repository/conf/log4j2.properties__ file.

   ```properties
      loggers = AUDIT_LOG, global_log_handler, trace-messages, ......
   
      logger.global_log_handler.name = com.custom.global.log.handler.GlobalLogHandler
      logger.global_log_handler.level = INFO
   ```
6. Start the server.
7. You will be able to see captured latencies in the wso2carbon.log file as below.

   ```properties
   apiName | apiMethod |  apiFullRequestPath | backend URL | apiResponse Status code | inflowLatency | backendLatency | outflowLatency | roundTripLatency
   INFO - GlobalLogHandler |admin--testing:vv1|GET|/test/v1|https://localhost:7000|200|348|144|23|515
   ```




