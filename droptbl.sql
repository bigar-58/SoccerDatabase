-- Include your drop table DDL statements in this file.
-- Make sure to terminate each statement with a semicolon (;)

-- LEAVE this statement on. It is required to connect to your database.
CONNECT TO cs421;

-- Remember to put the drop table ddls for the tables with foreign key references
--    ONLY AFTER the parent tables has already been dropped (reverse of the creation order).


DROP TABLE Goal;
DROP TABLE Participate;
DROP TABLE Plays;
DROP TABLE Assigned;
DROP TABLE TeamMember;
DROP TABLE Team;
DROP TABLE Referee;
DROP TABLE Ticket;
DROP TABLE Order;
DROP TABLE Client;
DROP TABLE Seat;
DROP TABLE Section;
DROP TABLE Match;
DROP TABLE Stadium;