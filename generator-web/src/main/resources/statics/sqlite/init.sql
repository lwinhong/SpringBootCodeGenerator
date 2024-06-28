CREATE TABLE "db_files"
(
    id           integer not null
        constraint db_files_pk
            primary key autoincrement,
    fileName     TEXT,
    fileMD5      TEXT,
    fileId       TEXT,
    fileSize     TEXT,
    createTime   text,
    deleted      integer,
    user         TEXT,
    updateTime   TEXT,
    filePath     TEXT,
    fileOldName  TEXT,
    relateFileId TEXT
);