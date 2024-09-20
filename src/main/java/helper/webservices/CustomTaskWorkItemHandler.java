package helper.webservices;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
 
@ApplicationScoped
public class CustomTaskWorkItemHandler implements KogitoWorkItemHandler {
 
    @Override
    public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
 
        int  port = (int) workItem.getParameters().get("port");
        String host = (String) workItem.getParameters().get("host");
        String path = (String) workItem.getParameters().get("path");
        String method = (String) workItem.getParameters().get("method");
         
        Vertx vertx = Vertx.vertx();
        WebClientOptions options = new WebClientOptions()
                .setDefaultHost(host)
                .setDefaultPort(port)        
                .setConnectTimeout(5000)     
                .setIdleTimeout(10000);      
                
        WebClient client = WebClient.create(vertx, options);
        JsonObject requestBody = new JsonObject();

        // Liste der auszuschließenden Parameter
        Set<String> excludedParameters = new HashSet<>(Arrays.asList("port", "method", "path", "host", "NodeName", "TaskName")); 

        for(String parameter : workItem.getParameters().keySet()) {
            if (!excludedParameters.contains(parameter)) { // Parameter ausschließen
                requestBody.put(parameter, workItem.getParameters().get(parameter));
            }
        }
        
        switch (method) {
            case "POST":
 
                client.post(port, host, path)
                .putHeader("content-type", "application/json")
                .sendJsonObject(requestBody, ar -> {
                    if (ar.succeeded()) {
                        System.out.println("Response: " + ar.result().bodyAsString());
                    } else {
                        System.out.println("Something went wrong " + ar.cause().getMessage());
                    }
                    vertx.close();
                });  
                break;
 
            case "GET":
 
            client.get(port, host, path)
            .putHeader("content-type", "application/json")
            .sendJsonObject(requestBody, ar -> {
                if (ar.succeeded()) {
                    System.out.println("Response: " + ar.result().bodyAsString());
                } else {
                    System.out.println("Something went wrong " + ar.cause().getMessage());
                }
                vertx.close();
               });
                break;
 
            case "PUT":
 
            client.put(port, host, path)
            .putHeader("content-type", "application/json")
            .sendJsonObject(requestBody, ar -> {
                if (ar.succeeded()) {
                    System.out.println("Response: " + ar.result().bodyAsString());
                } else {
                    System.out.println("Something went wrong " + ar.cause().getMessage());
                }
                vertx.close();
            });  
                break;
 
            case "DELETE":
            
            client.delete(port, host, path)
            .putHeader("content-type", "application/json")
            .sendJsonObject(requestBody, ar -> {
                if (ar.succeeded()) {
                    System.out.println("Response: " + ar.result().bodyAsString());
                } else {
                    System.out.println("Something went wrong " + ar.cause().getMessage());
                }
                vertx.close();
            });  
                break;
 
 
            default:
                break;
        }
 
               client.post(port, host, path)
                .putHeader("content-type", "application/json")
                .sendJsonObject(requestBody, ar -> {
                    if (ar.succeeded()) {
                        System.out.println("Response: " + ar.result().bodyAsString());
                    } else {
                        System.out.println("Something went wrong " + ar.cause().getMessage());
                    }
                    vertx.close();
                });  
 
 
 
 
                
                Map<String, Object> results = new HashMap<String, Object>();
                results.put("Result", "Message Returned from Work Item Handler");
                manager.completeWorkItem(workItem.getStringId(), results);
 
    }
 
    @Override
    public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        System.err.println("Error happened in the custom work item definition.");
    }
}