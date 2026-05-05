CREATE TABLE IF NOT EXISTS userstable (
    id SERIAL PRIMARY KEY,
    username varchar(255),
    password varchar(255)
);