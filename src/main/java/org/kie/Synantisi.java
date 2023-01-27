package org.kie;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.kie.domain.MeetingSchedule;
import org.kie.persistence.MeetingSchedulingXlsxFileIO;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import java.io.File;

@QuarkusMain
public class Synantisi implements QuarkusApplication {
    @Override
    public int run(String... args)  {
        SolverFactory solverFactory = SolverFactory.createFromXmlResource("meetingSchedulingSolverConfig.xml");
        Solver<MeetingSchedule> constructionSolver = solverFactory.buildSolver();
        MeetingSchedulingXlsxFileIO meetingSchedulingXlsxFileIO = new MeetingSchedulingXlsxFileIO();
        MeetingSchedule unsolved = meetingSchedulingXlsxFileIO.read(new File("/home/adupliak/IdeaProjects/synantisi/50meetings-160timegrains-5rooms.xlsx"));
        MeetingSchedule solution =  constructionSolver.solve(unsolved);
        meetingSchedulingXlsxFileIO.write(solution,new File("solution.xlsx"));
        return 0;
    }
}
