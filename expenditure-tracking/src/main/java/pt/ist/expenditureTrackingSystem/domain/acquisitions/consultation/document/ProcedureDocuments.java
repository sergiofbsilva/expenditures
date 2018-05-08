package pt.ist.expenditureTrackingSystem.domain.acquisitions.consultation.document;

import module.workflow.util.ClassNameBundle;
import module.workflow.util.FileUploadBeanResolver;
import module.workflow.util.WorkflowFileUploadBean;

@ClassNameBundle(bundle = "ExpenditureResources")
public class ProcedureDocuments extends PurchaseOrder_Base {

    static {
        FileUploadBeanResolver.registerBeanForProcessFile(ProcedureDocuments.class, WorkflowFileUploadBean.class);
    }

    public ProcedureDocuments(final String displayName, final String filename, final byte[] content) {
        super();
        init(displayName, filename, content);
    }

}