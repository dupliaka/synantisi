package org.kie;

import org.kie.persistence.MeetingSchedulingXlsxFileIO;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

import org.kie.domain.MeetingSchedule;

@Path("/synantisi")
//@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class SynantisiResource {

    @GET
    @Path("/solve")
    public MeetingSchedule solve() {

        SolverFactory solverFactory = SolverFactory.createFromXmlResource("meetingSchedulingSolverConfig.xml");
        Solver<MeetingSchedule> constructionSolver = solverFactory.buildSolver();
        MeetingSchedulingXlsxFileIO meetingSchedulingXlsxFileIO = new MeetingSchedulingXlsxFileIO();
        MeetingSchedule unsolved = meetingSchedulingXlsxFileIO.read(new File("/home/adupliak/IdeaProjects/synantisi/50meetings-160timegrains-5rooms.xlsx"));
        MeetingSchedule solution =  constructionSolver.solve(unsolved);

        File solutionFile = new File("solution.xlsx");
        meetingSchedulingXlsxFileIO.write(solution,new File("solution.xlsx"));
        return solution;

    }
}