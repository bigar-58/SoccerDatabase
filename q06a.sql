CREATE VIEW playerinfo(name, shirtNumber, DOB, country, associationName, group) AS
SELECT DISTINCT t.name, t.shirtNumber, t.DOB, T.country, T.associationName, t.group
FROM Team T, TeamMember t
WHERE T.associationURL = t.associationURL AND t.shirtNumber IS NOT NULL
ORDER BY t.name;
