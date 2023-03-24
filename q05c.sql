

SELECT DISTINCT associationURL, country, numMatches, numGoals
FROM 
(SELECT par.associationURL, tm.country, SUM(CASE WHEN par.associationURL IS NOT NULL THEN 1 ELSE 0 END) AS numMatches
FROM Team tm LEFT OUTER JOIN Participates par
ON tm.associationURL = par.associationURL)team_games,
(SELECT t.associationURL, SUM(CASE WHEN g.gid IS NOT NULL THEN 1 ELSE 0 END) AS numGoals
FROM Team t LEFT OUTER JOIN Goal g
ON t.associationURL = g.team)team_goals
WHERE team_goals.associationURL = team_games.associationURL
ORDER BY associationURL;
