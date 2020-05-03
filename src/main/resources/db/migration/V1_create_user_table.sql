 create table USER
    (
      ID           INTEGER default NEXT VALUE FOR "PUBLIC"."SYSTEM_SEQUENCE_7E96F4B6_5A61_411E_80A2_B1BCEBB73D5D"
        primary key,
      ACCOUNT_ID   VARCHAR(100),
      NAME         VARCHAR(50),
      TOKEN        CHAR(36),
      GMT_CREATE   BIGINT,
      GMT_MODIFIED BIGINT
    );