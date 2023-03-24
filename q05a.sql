--Can guarantee that these are all matches where Christine Sinclair plays and 
--scores in via transitivity of equality on join and non-nullity of g.matchID and g.uuid

SELECT DISTINCT s.name, s.location, m.date
FROM (TeamMember t, Plays p, Match m, Goal g, Stadium s)
WHERE (t.name LIKE “Christine Sinclair” AND
    t.uuid = p.uuid AND
	    p.matchID = m.matchID AND
    g.matchID = m.matchID AND
    g.uuid = t.uuid AND
    s.name = m.sname)
ORDER BY s.name;
