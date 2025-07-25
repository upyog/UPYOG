

package org.egov.finance.inbox.controller;
import java.util.List;

import org.egov.finance.inbox.model.InboxModel;
import org.egov.finance.inbox.service.InboxRenderServiceDelegate;
import org.egov.finance.inbox.workflow.entity.StateAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/inbox")
public class InboxController {

    @Autowired
    private InboxRenderServiceDelegate<StateAware> inboxRenderServiceDelegate;


    @PostMapping
    @ResponseBody
    public List<InboxModel> showInbox() {
        return inboxRenderServiceDelegate.getCurrentUserInboxItems();
    }

    @PostMapping(value = "/draft")
    @ResponseBody
    public List<InboxModel> showDraft() {
        return inboxRenderServiceDelegate.getCurrentUserDraftItems();
    }

    @PostMapping(value = "/history")
    @ResponseBody
    public List<InboxModel> showInboxHistory(@RequestParam Long stateId) {
        return inboxRenderServiceDelegate.getWorkflowHistoryItems(stateId);
    }
}
