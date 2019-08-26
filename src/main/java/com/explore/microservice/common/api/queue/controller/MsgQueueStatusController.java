package com.explore.microservice.common.api.queue.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekinsure.collaborus.queue.service.QueueManagementService;
import com.tekinsure.queue.filter.QueueSearchCriteria;
import com.tekinsure.queue.filter.QueueSearchFilter;
import com.tekinsure.tapas.common.api.APIResponse;
import com.tekinsure.tapas.common.ui.TAPASAppContext;

@RestController
@RequestMapping("/msgQueueStatus/api/v1")
public class MsgQueueStatusController {

    // TODO Apply jobqueue comments here
    protected static final Logger LOG = Logger.getLogger(MsgQueueStatusController.class);
    public static final SimpleDateFormat sfd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/getStatusById/{queueId}")
    public Object getStatusById(@PathVariable(value = "queueId") long queueId) {

        try {
            QueueSearchCriteria queueSC = new QueueSearchCriteria();
            queueSC.add(QueueSearchFilter.QueueId.eq(queueId));
            return getQueueManagementService().search(queueSC);
        } catch (Exception e) {
            LOG.error("Error while fetching job queue status for queue id [" + queueId + "]: ", e);
            APIResponse response = new APIResponse(101, APIResponse.STAT.ERROR, e.getMessage());
            return response;
        }
    }

    @GetMapping("/getStatusByRef/{service}/{ref}")
    public Object getStatusByRef(@PathVariable(value = "service") String service, @PathVariable(value = "ref") String ref) {

        try {
            QueueSearchCriteria queueSC = new QueueSearchCriteria();
            queueSC.add(QueueSearchFilter.ServiceName.eq(service));
            queueSC.add(QueueSearchFilter.Reference.eq(ref));
            return getQueueManagementService().search(queueSC);
        } catch (Exception e) {
            LOG.error("Error while fetching job queue status for service [" + service + "] and ref [" + ref + "] : ", e);
            APIResponse response = new APIResponse(301, APIResponse.STAT.ERROR, e.getMessage());
            return response;
        }
    }

    @GetMapping("/getStatusByDate/{service}")
    public Object getStatusByDate(@RequestParam String startDate, @RequestParam String endDate, @PathVariable(value = "service") String service) {

        Date start, end;
        try {
            start = sfd1.parse(startDate);
            end = sfd1.parse(endDate);
        } catch (Exception e1) {
            APIResponse response = new APIResponse(401, APIResponse.STAT.ERROR, "Dates are expected in [yyyy-MM-dd HH:mm:ss.SSS] format.");
            return response;
        }

        try {
            if (start != null && end != null) {
                QueueSearchCriteria queueSC = new QueueSearchCriteria();
                queueSC.add(QueueSearchFilter.ServiceName.eq(service));
                queueSC.add((QueueSearchFilter.CreatedAt.gteq(start).AND(QueueSearchFilter.CreatedAt.lteq(end)))
                        .OR(QueueSearchFilter.StartedAt.gteq(start).AND(QueueSearchFilter.StartedAt.lteq(end)))
                        .OR(QueueSearchFilter.UpdatedAt.gteq(start).AND(QueueSearchFilter.UpdatedAt.lteq(end))));
                return getQueueManagementService().search(queueSC);
            } else {
                LOG.error("Either start date or end date was computed null.");
                APIResponse response = new APIResponse(403, APIResponse.STAT.ERROR, "Either start date or end date was computed null.");
                return response;
            }
        } catch (Exception e) {
            LOG.error("Error while fetching job queue status by date for service [" + service + "] startdate [" + startDate + "] enddate [" + endDate + "]: ", e);
            APIResponse response = new APIResponse(402, APIResponse.STAT.ERROR, e.getMessage());
            return response;
        }

    }

    private QueueManagementService getQueueManagementService() {
        return TAPASAppContext.getBean(QueueManagementService.class);
    }

}
