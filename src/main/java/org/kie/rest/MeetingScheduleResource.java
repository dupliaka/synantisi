package org.kie.rest;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.kie.domain.MeetingSchedule;
import org.kie.persistence.MeetingSchedulingXlsxFileIO;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("meetingSchedule")
public class MeetingScheduleResource {
    public static final Long SINGLETON_TIME_TABLE_ID = 1L;

    @POST
    public void upload(@RequestBody byte[] body) {
        MeetingSchedulingXlsxFileIO meetingSchedulingXlsxFileIO = new MeetingSchedulingXlsxFileIO();
        MeetingSchedule unsolved = meetingSchedulingXlsxFileIO.readFromByteArray(body);
        unsolved.setId(SINGLETON_TIME_TABLE_ID);

    }


}
