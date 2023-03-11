package org.kie.solver;

import org.apache.commons.collections4.CollectionUtils;
import org.kie.domain.Meeting;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class MeetingScheduleConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                roomConflict(constraintFactory),
                speakerConflict(constraintFactory),
                attendanceConflict(constraintFactory)
        };
    }

    Constraint roomConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Meeting.class,
                        Joiners.equal(Meeting::getTimeslot),
                        Joiners.equal(Meeting::getRoom))
                .penalize(HardMediumSoftScore.ONE_HARD)
                .asConstraint("Room Conflict");
    }

    Constraint speakerConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Meeting.class,
                        Joiners.equal(Meeting::getTimeslot),
                        Joiners.equal(Meeting::getSpeaker))
                .penalize(HardMediumSoftScore.ONE_HARD)
                .asConstraint("Speaker Conflict");
    }

    Constraint attendanceConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Meeting.class,
                        Joiners.equal(Meeting::getTimeslot))
                .filter((meeting1,
                        meeting2) -> CollectionUtils.intersection(meeting1.getAttendeesList(), meeting2.getAttendeesList())
                                .size() > 0)
                .penalize(HardMediumSoftScore.ONE_HARD)
                .asConstraint("Attendance Conflict");
    }

}
