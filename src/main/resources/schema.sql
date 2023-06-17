CREATE TABLE IF NOT EXISTS items (
  id BIGINT PRIMARY KEY,
  name varchar(255),
  description varchar(512),
  available boolean,
  owner BIGINT
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY,
  name varchar(255),
  email varchar(512)
);

ALTER TABLE items ADD FOREIGN KEY (owner) REFERENCES users (id);
