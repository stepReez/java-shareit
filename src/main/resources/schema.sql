CREATE TABLE IF NOT EXISTS items (
  id BIGINT PRIMARY KEY,
  name varchar(255) NOT NULL,
  description varchar(512)NOT NULL,
  available boolean,
  owner BIGINT
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY,
  name varchar(255),
  email varchar(512)
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT PRIMARY KEY,
  start DATE,
  end_time DATE,
  item_id BIGINT,
  booker_id BIGINT,
  status varchar(8),
  FOREIGN KEY (item_id) REFERENCES items (id),
  FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT PRIMARY KEY,
  text varchar(512),
  item_id BIGINT,
  author_id BIGINT,
  created DATE,
  FOREIGN KEY (item_id) REFERENCES items (id),
  FOREIGN KEY (author_id) REFERENCES users (id)
);

ALTER TABLE items ADD FOREIGN KEY (owner) REFERENCES users (id);
