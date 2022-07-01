CREATE TABLE local.book (
	id bigint PRIMARY KEY,
	title text,
	price bigint
);

CREATE TABLE local.book_store(
	id bigint PRIMARY KEY,
	book_ids list<bigint>,
	contact text,
);

INSERT INTO local.book JSON '{
  "id" : "1",
  "title" : "computer science",
  "price": 50000
}';

INSERT INTO local.book JSON '{
  "id" : "2",
  "title" : "art",
  "price": 30000
}';

INSERT INTO local.book_store JSON '{
  "id" : "1",
  "book_ids" : [1,2],
  "contact": "010-1111-2222"
}';