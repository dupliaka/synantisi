INSERT INTO meeting (id, attendees, speaker, topic)
VALUES (0, 'E.Mask, M.Zuckerberg', 'L.Torvalds', 'Code like a Boss');
INSERT INTO meeting (id, attendees, speaker, topic)
VALUES (1, 'J.Bezos', 'R.Stallman', 'Geek out with Open Source');
INSERT INTO meeting (id, attendees, speaker, topic)
VALUES (2, 'T.Hicks, P.Cormier', 'S.Hykes', 'The future of Open Source Infrastructure');
INSERT INTO meeting (id, attendees, speaker, topic)
VALUES (3, 'T.Hicks, T.Cook', 'R.Stallman', 'DevOps party');

INSERT INTO room (id, name)
VALUES (4, 'Room A');
INSERT INTO room (id, name)
VALUES (5, 'Room B');

INSERT INTO timeslot (id, dayofweek, endtime, starttime)
VALUES (6, '0', '09:30:00', '08:30:00');
INSERT INTO timeslot (id, dayofweek, endtime, starttime)
VALUES (7, '0', '10:30:00', '09:30:00');

ALTER SEQUENCE HIBERNATE_SEQUENCE RESTART WITH 3;
