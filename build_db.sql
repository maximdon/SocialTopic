CREATE TABLE "INTERNAL_STATE" ("last_index_run" NUMERIC, "server_id" INTEGER PRIMARY KEY  NOT NULL  DEFAULT 1, "last_extract_run" NUMERIC DEFAULT -1, "index_initial_position" NUMERIC DEFAULT -1);
CREATE TABLE "KNOWN_SYNONYMS" ("synonym" VARCHAR(200)  NOT NULL,"abbreviation" VARCHAR(100)  NOT NULL);
CREATE TABLE "SUGGESTED_TERMS" ("ticket_id" VARCHAR, "term" VARCHAR);
CREATE TABLE "review_tracking" ("id" INTEGER PRIMARY KEY  NOT NULL ,"ticket_id" VARCHAR NOT NULL ,"tracking_date" DATETIME NOT NULL , "important_terms" VARCHAR, "irrelevant_terms" VARCHAR);