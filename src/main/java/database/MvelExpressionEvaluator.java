package database;

import java.util.HashMap;
import java.util.Map;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.kie.api.event.process.ProcessNodeEvent;
import org.mvel2.MVEL;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.vertx.core.json.jackson.DatabindCodec;

public class MvelExpressionEvaluator {

    public static Object ExececuteSimpleMVELexpression(String expressionString, ProcessNodeEvent event) {
        try {
            // Sicheren Parser-Kontext erstellen
            RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance) event.getNodeInstance().getProcessInstance();
            
            expressionString = expressionString.replace("#{", "");
            expressionString = expressionString.replace("}", "");

            int iend = expressionString.indexOf(".");
            String VariableName = new String();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

             if (iend != -1) {

                VariableName = expressionString.substring(0, iend);
                Map<String, Object> map = objectMapper.convertValue(processInstance.getVariable(VariableName), new TypeReference<Map<String, Object>>() {});
                Map<String, Object> finalmap = new HashMap();
                finalmap.put(VariableName, map);
                return MVEL.eval(expressionString, finalmap);

            } else {
                VariableName = expressionString;
                Map<String, Object> map = objectMapper.convertValue(processInstance.getVariable(VariableName), new TypeReference<Map<String, Object>>() {});
                Map<String, Object> finalmap = new HashMap();
                finalmap.put(VariableName, map);
                return MVEL.eval(expressionString, finalmap);              
            }
        } catch (Exception e) {
            // Fehlerbehandlung (z.B. Logging, benutzerdefinierte Exceptions)
            throw new RuntimeException("Fehler bei der Auswertung des MVEL-Ausdrucks: " + e.getMessage(), e);
        }
    }
}