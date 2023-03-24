

SELECT name, shirtNumber, country
FROM 
(SELECT DISTINCT par.associationURL, tm.country, SUM(CASE WHEN par.associationURL IS NOT NULL THEN 1 ELSE 0 END) AS numgames
FROM Team tm LEFT OUTER JOIN Participates par
ON tm.associationURL = par.associationURL
)team_games, 
(SELECT DISTINCT p.uuid, t.associationURL, t.name, t.shirtNumber, SUM(CASE WHEN p.uuid AND IS NOT NULL THEN 1 ELSE 0 END) AS numplayed
FROM TeamMember t LEFT OUTER JOIN Plays p 
ON t.uuid = p.uuid
)player_games
WHERE numgames = numplayed AND 
   team_games.associationURL = player_games.associationURL
ORDER BY name;
