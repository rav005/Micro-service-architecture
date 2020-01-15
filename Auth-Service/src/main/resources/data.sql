
INSERT INTO AUTH.roles (name) 
SELECT * FROM (SELECT 'ROLE_ADMIN') AS tmp
WHERE NOT EXISTS (
    SELECT name FROM AUTH.roles WHERE name = 'ROLE_ADMIN'
) LIMIT 1;

INSERT INTO AUTH.users (username, password, created_at, updated_at)
SELECT * FROM (SELECT 'user1', 'password', CURRENT_TIMESTAMP() as c, CURRENT_TIMESTAMP() as u) AS tmp
WHERE NOT EXISTS (
    SELECT username FROM AUTH.users WHERE username = 'user1'
) LIMIT 1;

INSERT INTO AUTH.users (username, password, created_at, updated_at)
SELECT * FROM (SELECT 'user2', 'password', CURRENT_TIMESTAMP() as c, CURRENT_TIMESTAMP() as u) AS tmp
WHERE NOT EXISTS (
    SELECT username FROM AUTH.users WHERE username = 'user2'
) LIMIT 1;


INSERT INTO AUTH.user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
from (SELECT ID as role_id FROM AUTH.roles WHERE name = 'ADMIN') r,
	(SELECT ID as user_id FROM AUTH.users) u
WHERE u.user_id not in (SELECT user_id FROM AUTH.user_roles)
;
