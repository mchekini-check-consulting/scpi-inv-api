DELETE FROM profiles p
WHERE p.id NOT IN (
    SELECT MAX(id)
    FROM profiles
    GROUP BY email
);

ALTER TABLE profiles
    ADD CONSTRAINT unique_email UNIQUE (email);