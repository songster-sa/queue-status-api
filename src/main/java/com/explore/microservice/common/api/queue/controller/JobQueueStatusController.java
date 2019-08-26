package com.explore.microservice.common.api.queue.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekinsure.collaborus.jqf.JobQueue;
import com.tekinsure.collaborus.jqf.JobQueueService;
import com.tekinsure.collaborus.jqf.pal.JobQueueFilter;
import com.tekinsure.collaborus.jqf.pal.JobQueueSearchCriteria;
import com.tekinsure.tapas.common.api.APIResponse;
import com.tekinsure.tapas.common.ui.TAPASAppContext;

@RestController
@RequestMapping("/jobQueueStatus/api/v1")
public class JobQueueStatusController {

    protected static final Logger LOG = Logger.getLogger(JobQueueStatusController.class);

    // can define multiple date formats
    public static final SimpleDateFormat sfd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    // TODO try with ResponseEntity
    // TODO see if all mappings could be differentiated by params, keeping the endpoint same

    @GetMapping("/getStatusById/{queueId}")
    public Object getStatusById(@PathVariable(value = "queueId") long queueId) {

        try {
            JobQueueSearchCriteria queueSC = new JobQueueSearchCriteria();
            queueSC.add(JobQueueFilter.JobQueueId.eq(queueId));
            return getJobQueuePAL().searchJobQueue(queueSC);
        } catch (Exception e) {
            LOG.error("Error while fetching job queue status for queue id [" + queueId + "]: ", e);
            APIResponse response = new APIResponse(101, APIResponse.STAT.ERROR, e.getMessage());
            return response;
        }
    }

    @GetMapping("/getStatusByRef/{service}/{ref}")
    public Object getStatusByRef(@PathVariable(value = "service") String service, @PathVariable(value = "ref") String ref) {

        try {
            JobQueueSearchCriteria queueSC = new JobQueueSearchCriteria();
            queueSC.add(JobQueueFilter.ServiceName.eq(service));
            queueSC.add(JobQueueFilter.Reference.eq(ref));
            return getJobQueuePAL().searchJobQueue(queueSC);
        } catch (Exception e) {
            LOG.error("Error while fetching job queue status for service [" + service + "] and ref [" + ref + "] : ", e);
            APIResponse response = new APIResponse(301, APIResponse.STAT.ERROR, e.getMessage());
            return response;
        }
    }

    // TODO try spring.jackson.date-format or @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
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
                JobQueueSearchCriteria queueSC = new JobQueueSearchCriteria();
                queueSC.add(JobQueueFilter.ServiceName.eq(service));
                queueSC.add((JobQueueFilter.CreatedAt.gteq(start).AND(JobQueueFilter.CreatedAt.lteq(end)))
                        .OR(JobQueueFilter.StartedAt.gteq(start).AND(JobQueueFilter.StartedAt.lteq(end)))
                        .OR(JobQueueFilter.UpdatedAt.gteq(start).AND(JobQueueFilter.UpdatedAt.lteq(end))));
                return getJobQueuePAL().searchJobQueue(queueSC);
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

    // TODO add more filtering options.
    // TODO check pagination, if there's a lot of entries in a single queue.

    private JobQueueService getJobQueuePAL() {
        return TAPASAppContext.getBean(JobQueueService.class);
    }
}
