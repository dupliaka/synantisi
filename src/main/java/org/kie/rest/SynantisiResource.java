package org.kie.rest;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.kie.domain.MeetingSchedule;
import org.kie.persistence.MeetingSchedulingXlsxFileIO;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("synantisi")
public class SynantisiResource {

    @POST
    @Produces("application/vnd.ms-excel")
    public Response getSynantisi(@RequestBody byte[] body) {

        SolverFactory solverFactory = SolverFactory.createFromXmlResource("meetingSchedulingSolverConfig.xml");
        Solver<MeetingSchedule> constructionSolver = solverFactory.buildSolver();

        MeetingSchedulingXlsxFileIO meetingSchedulingXlsxFileIO = new MeetingSchedulingXlsxFileIO();
        MeetingSchedule unsolved = meetingSchedulingXlsxFileIO.readFromByteArray(body);

        unsolved.setId(0L);
        MeetingSchedule solution = constructionSolver.solve(unsolved);

        byte[] xlsx = meetingSchedulingXlsxFileIO.writeToByteArray(solution);
        Response.ResponseBuilder response = Response.ok(xlsx);
        response.header("Content-Disposition",
                "attachment; filename=new-excel-file.xls");
        return response.build();
    }

    
}