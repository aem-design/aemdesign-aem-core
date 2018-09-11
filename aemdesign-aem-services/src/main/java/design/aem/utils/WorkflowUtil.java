package design.aem.utils;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Map;

public final class WorkflowUtil {

    public enum PayloadType {
        JCR_PATH,
        JCR_UUID,
        URL,
        JAVA_OBJECT
    }

    public static final PayloadType NO_PAYLOAD_TYPE = PayloadType.URL;
    public static final String NO_PAYLOAD_VALUE = "payload://empty";

    private WorkflowUtil() {
    }

    public static Workflow start(Resource resource, String name, Map<String, Object> metaData) throws WorkflowException {
        return start(resource.getResourceResolver(), name, PayloadType.JCR_PATH, resource.getPath(), metaData);
    }

    public static Workflow start(ResourceResolver resourceResolver, String workflowModelPath, Map<String, Object> metaData) throws WorkflowException {
        return start(resourceResolver, workflowModelPath, NO_PAYLOAD_TYPE, NO_PAYLOAD_VALUE, metaData);
    }

    public static Workflow start(ResourceResolver resourceResolver, String workflowModelPath, PayloadType payloadType, Object payload, Map<String, Object> metaData) throws WorkflowException {
        WorkflowSession wfSession = resourceResolver.adaptTo(WorkflowSession.class);
        WorkflowModel wfModel = wfSession.getModel(workflowModelPath);
        WorkflowData wfData = wfSession.newWorkflowData(payloadType.toString(), payload);
        if (metaData != null) {
            wfData.getMetaDataMap().putAll(metaData);
        }

        return wfSession.startWorkflow(wfModel, wfData);
    }

    public static Workflow.State getState(Workflow workflow) {
        return Workflow.State.valueOf(workflow.getState());
    }

}
