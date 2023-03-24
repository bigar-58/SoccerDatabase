-- Include your create table DDL statements in this file.
-- Make sure to terminate each statement with a semicolon (;)

-- LEAVE this statement on. It is required to connect to your database.
CONNECT TO cs421;

-- Remember to put the create table ddls for the tables with foreign key references
--    ONLY AFTER the parent tables has already been created.

CREATE TABLE Team (
    associationURL VARCHAR(255) PRIMARY KEY,
    associationName VARCHAR(255) NOT NULL,
    country VARCHAR(255),
    group VARCHAR(255)
);

CREATE TABLE TeamMember (
    uuid INT PRIMARY KEY,
    associationURL VARCHAR(255) REFERENCES Team(associationURL) NOT NULL,
    name VARCHAR(255),
    DOB DATE,
    role VARCHAR(255),
    shirtNumber INT,
    genPosition VARCHAR(255)
);

CREATE TABLE Referee (
    rid INT PRIMARY KEY,
    role VARCHAR(255),
    country VARCHAR(255),
    name VARCHAR(255),
    yearsOfExperience INT
);

CREATE TABLE Stadium(
    name VARCHAR(255) PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    capacity INT
);

CREATE TABLE Match (
    matchID INT PRIMARY KEY,
    sname VARCHAR(255) REFERENCES Stadium(sname) NOT NULL,
    round VARCHAR(255),
    date DATE,
    duration INT,
    time TIME
);

CREATE TABLE Goal (
    gid INT PRIMARY KEY,
    team VARCHAR(255) REFERENCES Team(associationURL),
    uuid INT REFERENCES TeamMember(uuid) NOT NULL,
    matchID INT REFERENCES Match(matchID) NOT NULL,
    duringPenaltyKick BOOLEAN,
    minute INT,
    occurrence VARCHAR(255)
);

CREATE TABLE Client (
    email VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(255)
);

CREATE TABLE Order (
    transactionID INT PRIMARY KEY,
    email VARCHAR(255) REFERENCES Client(email),
    totalCost DECIMAL(10, 2),
    paymentMethod VARCHAR(255)
);

CREATE TABLE Ticket (
    tid INT PRIMARY KEY,
    matchID INT REFERENCES Match(matchID),
    transactionID INT REFERENCES Order(transactionID),
    price DECIMAL(10, 2)
);

CREATE TABLE Section (
    name VARCHAR(255) PRIMARY KEY,
    capacity INT
);

CREATE TABLE Seat (
    sId INT PRIMARY KEY,
    sname VARCHAR(255) REFERENCES Stadium(sname),
    section VARCHAR(255) REFERENCES Section(name),
    row INT,
    seatNumber INT
);

CREATE TABLE Participate (
    associationURL VARCHAR(255) REFERENCES Team(associationURL),
    matchID INT REFERENCES Match(matchID) NOT NULL,
    PRIMARY KEY (associationURL, matchID)
);

CREATE TABLE Assigned (
    rid INT REFERENCES Referee(rid),
    matchID INT REFERENCES Match(matchID),
    PRIMARY KEY (rid, matchID)
);

CREATE TABLE Plays (
    uuid INT REFERENCES TeamMember(uuid),
    matchID INT REFERENCES Match(matchID),
    minEntered INT,
    minExited INT,
    detailedPosition VARCHAR(255),
    yellowCards INT,
    receivedRed BOOLEAN,
    PRIMARY KEY (uuid, matchID)
);





