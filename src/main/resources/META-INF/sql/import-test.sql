INSERT INTO meeting (id, attendees, speaker, topic, sessionId, priority)
VALUES (0, 'E.Mask, M.Zuckerberg', 'L.Torvalds', 'Code like a Boss', 'dCWkP1qnh0mR7-SDXT0KLeGWkB6ai0dShg2B_IfI', 10);
INSERT INTO meeting (id, attendees, speaker, topic, sessionId, priority)
VALUES (1, 'J.Bezos', 'R.Stallman', 'Geek out with Open Source', 'dCWkP1qnh0mR7-SDXT0KLeGWkB6ai0dShg2B_IfI', 5);
INSERT INTO meeting (id, attendees, speaker, topic, sessionId, priority)
VALUES (2, 'T.Hicks, P.Cormier', 'S.Hykes', 'The future of Open Source Infrastructure', 'dCWkP1qnh0mR7-SDXT0KLeGWkB6ai0dShg2B_IfI', 7);
INSERT INTO meeting (id, attendees, speaker, topic, sessionId, priority)
VALUES (3, 'T.Hicks, T.Cook', 'R.Stallman', 'DevOps party', 'dCWkP1qnh0mR7-SDXT0KLeGWkB6ai0dShg2B_IfI', 3);

INSERT INTO room (id, name, sessionId)
VALUES (4, 'Room A', 'dCWkP1qnh0mR7-SDXT0KLeGWkB6ai0dShg2B_IfI');
INSERT INTO room (id, name, sessionId)
VALUES (5, 'Room B', 'dCWkP1qnh0mR7-SDXT0KLeGWkB6ai0dShg2B_IfI');

INSERT INTO timeslot (id, dayofweek, endtime, starttime, sessionId)
VALUES (6, '0', '09:30:00', '08:30:00', 'dCWkP1qnh0mR7-SDXT0KLeGWkB6ai0dShg2B_IfI');
INSERT INTO timeslot (id, dayofweek, endtime, starttime, sessionId)
VALUES (7, '0', '10:30:00', '09:30:00', 'dCWkP1qnh0mR7-SDXT0KLeGWkB6ai0dShg2B_IfI');

ALTER SEQUENCE HIBERNATE_SEQUENCE RESTART WITH 3;
