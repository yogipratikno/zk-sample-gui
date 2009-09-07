
DROP SEQUENCE IF EXISTS PRG_SEQUENZ;
CREATE SEQUENCE PRG_SEQUENZ  INCREMENT BY 1 START WITH 100000; 


DROP VIEW IF EXISTS nextidView;
/*CREATE VIEW nextidView as select NEXTVAL('PRG_SEQUENZ') as nextval;*/

/*CREATE VIEW nextidView as select 'PRG_SEQUENZ'.NEXTVAL AS NEXTVAL ;*/ 
CREATE VIEW nextidView as select NEXT VALUE FOR PRG_SEQUENZ as nextval;

DROP TABLE IF EXISTS kunde cascade;
DROP TABLE IF EXISTS artikel cascade;
DROP TABLE IF EXISTS auftrag cascade;
DROP TABLE IF EXISTS auftragposition cascade;
DROP TABLE IF EXISTS branche cascade;

DROP TABLE IF EXISTS sec_user cascade;
DROP TABLE IF EXISTS sec_userrole cascade;
DROP TABLE IF EXISTS sec_role cascade;
DROP TABLE IF EXISTS sec_rolegroup cascade;
DROP TABLE IF EXISTS sec_group cascade;
DROP TABLE IF EXISTS sec_groupright cascade;
DROP TABLE IF EXISTS sec_right cascade;
DROP TABLE IF EXISTS sec_loginlog cascade;

DROP TABLE IF EXISTS guestbook cascade;


/*==============================================================*/
/* Table: Branche                                               */
/*==============================================================*/
create table Branche (
   bra_id               INT8                 not null,
   bra_nr               VARCHAR(20)          null,
   bra_bezeichnung      VARCHAR(30)          not null,
   version              INT4                 not null default 0,
   constraint PK_BRANCHE primary key (bra_id)
)
;
/*==============================================================*/
/* Index: idx_bra_id                                            */
/*==============================================================*/
create unique index idx_bra_id on Branche (
bra_id
);

/*==============================================================*/
/* Index: idx_bra_bezeichnung                                   */
/*==============================================================*/
create unique index idx_bra_bezeichnung on Branche (
bra_bezeichnung
);

/*==============================================================*/
/* Table: artikel                                               */
/*==============================================================*/
create table artikel (
   art_id               INT8                 not null,
   art_kurzbezeichnung  VARCHAR(50)          not null,
   art_langbezeichnung  TEXT                 null,
   art_nr               VARCHAR(20)          not null,
   art_preis            NUMERIC(12,2)        not null default 0.00,
   version              INT4                 not null default 0,
   constraint PK_ARTIKEL primary key (art_id)
)
;
/*==============================================================*/
/* Index: idx_art_id                                            */
/*==============================================================*/
create unique index idx_art_id on artikel (
art_id
);

/*==============================================================*/
/* Index: idx_art_nr                                            */
/*==============================================================*/
create unique index idx_art_nr on artikel (
art_nr
);

/*==============================================================*/
/* Index: idx_art_bezeichnung                                   */
/*==============================================================*/
create  index idx_art_bezeichnung on artikel (
art_kurzbezeichnung
);

/*==============================================================*/
/* Table: auftrag                                               */
/*==============================================================*/
create table auftrag (
   auf_id               INT8                 not null,
   auf_kun_id           INT8                 not null,
   auf_nr               varchar(20)          not null,
   auf_bezeichnung      varchar(50)          null,
   version              int4                 not null default 0,
   constraint pk_auftrag primary key (auf_id)
)
;
/*==============================================================*/
/* Index: ix_auf_id                                             */
/*==============================================================*/
create unique index ix_auf_id on auftrag  (
auf_id
);

/*==============================================================*/
/* Index: ix_auf_kun_id                                         */
/*==============================================================*/
create  index ix_auf_kun_id on auftrag  (
auf_kun_id
);

/*==============================================================*/
/* Index: ix_auf_nr                                             */
/*==============================================================*/
create unique index ix_auf_nr on auftrag (
auf_nr
);

/*==============================================================*/
/* Table: auftragposition                                       */
/*==============================================================*/
create table auftragposition (
   aup_id               INT8                 not null,
   aup_auf_id           INT8                 not null,
   art_id               INT8                 null,
   aup_position         int4                 null,
   aup_menge            numeric(12,2)        null,
   aup_einzelwert       numeric(12,2)        null,
   aup_gesamtwert       numeric(12,2)        null,
   version              int4                 not null default 0,
   constraint pk_auftragposition primary key (aup_id)
)
;
/*==============================================================*/
/* Index: ix_aup_auf_id                                         */
/*==============================================================*/
create  index ix_aup_auf_id on auftragposition  (
aup_auf_id
);

/*==============================================================*/
/* Index: ix_aup_id                                             */
/*==============================================================*/
create unique index ix_aup_id on auftragposition (
aup_id
);

/*==============================================================*/
/* Table: kunde                                                 */
/*==============================================================*/
create table kunde (
   kun_id               INT8                 not null,
   kun_bra_id           INT8                 null,
   kun_nr               VARCHAR(20)          not null,
   kun_matchcode        varchar(20)          null,
   kun_name1            varchar(50)          null,
   kun_name2            varchar(50)          null,
   kun_ort              varchar(50)          null,
   kun_mahnsperre       BOOL                 null,
   version              int4                 not null default 0,
   constraint pk_kunde primary key (kun_id)
)
;
/*==============================================================*/
/* Index: ix_kun_id                                             */
/*==============================================================*/
create unique index ix_kun_id on kunde  (
kun_id
);


/*==============================================================*/
/* Index: ix_kun_nr                                             */
/*==============================================================*/
create unique index ix_kun_nr on kunde (
kun_nr
);


/*==============================================================*/
/* Table: guestbook                                             */
/*==============================================================*/
create table guestbook (
   gub_id               INT8                 not null,
   gub_subject          VARCHAR(40)          not null,
   gub_date             TIMESTAMP            not null,
   gub_usr_name         VARCHAR(40)          not null,
   gub_text             TEXT                 null,
   version              INT4                 null,
   constraint PK_GUESTBOOK primary key (gub_id)
)
;


/*==============================================================*/
/* Index: idx_gub_id                                            */
/*==============================================================*/
create unique index idx_gub_id on guestbook (
gub_id
);

/*==============================================================*/
/* Index: idx_gub_subject                                       */
/*==============================================================*/
create  index idx_gub_subject on guestbook (
gub_subject
);

/*==============================================================*/
/* Index: idx_gub_date                                          */
/*==============================================================*/
create  index idx_gub_date on guestbook (
gub_date
);

/*==============================================================*/
/* Index: idx_gub_usr_name                                      */
/*==============================================================*/
create  index idx_gub_usr_name on guestbook (
gub_usr_name
);


/*==============================================================*/
/* Table: sec_group                                             */
/*==============================================================*/
create table sec_group (
   grp_id               INT8                 not null,
   grp_shortdescription VARCHAR(30)          not null,
   grp_longdescription  VARCHAR(1000)        null,
   version              INT4                 not null default 0,
   constraint PK_SEC_GROUP primary key (grp_id)
)
;


/*==============================================================*/
/* Index: idx_grp_id                                            */
/*==============================================================*/
create unique index idx_grp_id on sec_group (
grp_id
);

/*==============================================================*/
/* Index: idx_grp_shortdescription                              */
/*==============================================================*/
create unique index idx_grp_shortdescription on sec_group (
grp_shortdescription
);

/*==============================================================*/
/* Table: sec_groupright                                        */
/*==============================================================*/
create table sec_groupright (
   gri_id               INT8                 not null,
   grp_id               INT8                 not null,
   rig_id               INT8                 not null,
   version              INT4                 not null default 0,
   constraint PK_SEC_GROUPRIGHT primary key (gri_id)
)
;


/*==============================================================*/
/* Index: idx_gri_id                                            */
/*==============================================================*/
create unique index idx_gri_id on sec_groupright (
gri_id
);

/*==============================================================*/
/* Index: idx_gri_grprig                                        */
/*==============================================================*/
create unique index idx_gri_grprig on sec_groupright (
grp_id,
rig_id
);

/*==============================================================*/
/* Table: sec_loginlog                                          */
/*==============================================================*/
create table sec_loginlog (
   lgl_id               INT8                 not null,
   lgl_loginname        VARCHAR(50)          not null,
   lgl_logtime          TIMESTAMP            not null,
   lgl_ip               VARCHAR(19)          null,
   lgl_status_id        INT4                 not null,
   lgl_sessionid        VARCHAR(50)          null,
   constraint PK_SEC_LOGINLOG primary key (lgl_id)
)
;


/*==============================================================*/
/* Index: idx_lgl_id                                            */
/*==============================================================*/
create unique index idx_lgl_id on sec_loginlog (
lgl_id
);

/*==============================================================*/
/* Index: idx_lgl_logtime                                       */
/*==============================================================*/
create  index idx_lgl_logtime on sec_loginlog (
lgl_logtime
);

/*==============================================================*/
/* Table: sec_right                                             */
/*==============================================================*/
create table sec_right (
   rig_id               INT8                 not null,
   rig_type             INT4                 null default 1,
   rig_name             VARCHAR(50)          not null,
   version              INT4                 not null default 0,
   constraint PK_SEC_RIGHT primary key (rig_id)
)
;


/*==============================================================*/
/* Index: idx_rig_id                                            */
/*==============================================================*/
create unique index idx_rig_id on sec_right (
rig_id
);

/*==============================================================*/
/* Index: idx_rig_type                                          */
/*==============================================================*/
create  index idx_rig_type on sec_right (
rig_type
);

/*==============================================================*/
/* Index: idx_rig_name                                          */
/*==============================================================*/
create unique index idx_rig_name on sec_right (
rig_name
);

/*==============================================================*/
/* Table: sec_role                                              */
/*==============================================================*/
create table sec_role (
   rol_id               INT8                 not null,
   rol_shortdescription VARCHAR(30)          not null,
   rol_longdescription  VARCHAR(1000)        null,
   version              INT4                 not null default 0,
   constraint PK_SEC_ROLE primary key (rol_id)
)
;

/*==============================================================*/
/* Index: idx_role_id                                           */
/*==============================================================*/
create unique index idx_role_id on sec_role (
rol_id
);

/*==============================================================*/
/* Index: idx_role_shortdescription                             */
/*==============================================================*/
create unique index idx_role_shortdescription on sec_role (
rol_shortdescription
);

/*==============================================================*/
/* Table: sec_rolegroup                                         */
/*==============================================================*/
create table sec_rolegroup (
   rlg_id               INT8                 not null,
   grp_id               INT8                 not null,
   rol_id               INT8                 not null,
   version              INT4                 not null default 0,
   constraint PK_SEC_ROLEGROUP primary key (rlg_id)
)
;


/*==============================================================*/
/* Index: idx_rlg_id                                            */
/*==============================================================*/
create unique index idx_rlg_id on sec_rolegroup (
rlg_id
);

/*==============================================================*/
/* Index: idx_rlg_grprol                                        */
/*==============================================================*/
create unique index idx_rlg_grprol on sec_rolegroup (
grp_id,
rol_id
);

/*==============================================================*/
/* Table: sec_user                                              */
/*==============================================================*/
create table sec_user (
   usr_id               INT8                 not null,
   usr_loginname        VARCHAR(50)          not null,
   usr_password         VARCHAR(50)          not null,
   usr_lastname         VARCHAR(50)          null,
   usr_firstname        VARCHAR(50)          null,
   usr_email            VARCHAR(200)         null,
   usr_locale           VARCHAR(5)           null,
   usr_enabled          BOOL                 not null default FALSE,
   usr_accountNonExpired BOOL                 not null default TRUE,
   usr_credentialsNonExpired BOOL                 not null default TRUE,
   usr_accountNonLocked BOOL                 not null default TRUE,
   usr_token            VARCHAR(20)          null,
   version              INT4                 not null default 0,
   constraint PK_SEC_USER primary key (usr_id)
)
;


/*==============================================================*/
/* Index: idx_usr_id                                            */
/*==============================================================*/
create unique index idx_usr_id on sec_user (
usr_id
);

/*==============================================================*/
/* Index: idx_usr_loginname                                     */
/*==============================================================*/
create unique index idx_usr_loginname on sec_user (
usr_loginname
);

/*==============================================================*/
/* Table: sec_userrole                                          */
/*==============================================================*/
create table sec_userrole (
   urr_id               INT8                 not null,
   usr_id               INT8                 not null,
   rol_id               INT8                 not null,
   version              INT4                 not null default 0,
   constraint PK_SEC_USERROLE primary key (urr_id)
);


/*==============================================================*/
/* Index: idx_urr_id                                            */
/*==============================================================*/
create unique index idx_urr_id on sec_userrole (
urr_id
);

/*==============================================================*/
/* Index: idx_urr_usrrol                                        */
/*==============================================================*/
create unique index idx_urr_usrrol on sec_userrole (
usr_id,
rol_id
);

alter table auftrag
   add constraint ref_auf_to_kun foreign key (auf_kun_id)
      references kunde (kun_id)
      on delete cascade on update cascade;

alter table auftragposition
   add constraint ref_aup_to_art foreign key (art_id)
      references artikel (art_id)
      on delete restrict on update restrict;

alter table auftragposition
   add constraint ref_aup_to_auf foreign key (aup_auf_id)
      references auftrag (auf_id)
      on delete cascade on update cascade;

alter table kunde
   add constraint ref_kun_to_bra foreign key (kun_bra_id)
      references Branche (bra_id)
      on delete restrict on update restrict;

alter table sec_groupright
   add constraint ref_gri_to_grp foreign key (grp_id)
      references sec_group (grp_id)
      on delete restrict on update restrict;

alter table sec_groupright
   add constraint ref_gri_to_rig foreign key (rig_id)
      references sec_right (rig_id)
      on delete restrict on update restrict;

alter table sec_rolegroup
   add constraint ref_rlg_to_grp foreign key (grp_id)
      references sec_group (grp_id)
      on delete restrict on update restrict;

alter table sec_rolegroup
   add constraint ref_rlg_to_rol foreign key (rol_id)
      references sec_role (rol_id)
      on delete restrict on update restrict;

alter table sec_userrole
   add constraint ref_aut_to_rol foreign key (rol_id)
      references sec_role (rol_id)
      on delete restrict on update restrict;

alter table sec_userrole
   add constraint ref_aut_to_usr foreign key (usr_id)
      references sec_user (usr_id)
      on delete restrict on update restrict;


/******************** TEST DATA ********************/



/******************** Security: USERS ********************/  
INSERT INTO SEC_USER (USR_ID, USR_LOGINNAME, USR_PASSWORD, USR_LASTNAME, USR_FIRSTNAME, USR_EMAIL, USR_LOCALE, USR_ENABLED, USR_ACCOUNTNONEXPIRED, USR_CREDENTIALSNONEXPIRED, USR_ACCOUNTNONLOCKED, USR_TOKEN,  VERSION)
values (10, 'guest', 'guest', 'guestFirstname', 'guestlastname', 'guest@web.de', NULL, true, true, true, true, null, 0);
INSERT INTO SEC_USER (USR_ID, USR_LOGINNAME, USR_PASSWORD, USR_LASTNAME, USR_FIRSTNAME, USR_EMAIL, USR_LOCALE, USR_ENABLED, USR_ACCOUNTNONEXPIRED, USR_CREDENTIALSNONEXPIRED, USR_ACCOUNTNONLOCKED, USR_TOKEN,  VERSION)
values (11, 'admin', 'admin', 'Visor', 'Super', 'admin@web.de', NULL, true, true, true, true, null, 0);
INSERT INTO SEC_USER (USR_ID, USR_LOGINNAME, USR_PASSWORD, USR_LASTNAME, USR_FIRSTNAME, USR_EMAIL, USR_LOCALE, USR_ENABLED, USR_ACCOUNTNONEXPIRED, USR_CREDENTIALSNONEXPIRED, USR_ACCOUNTNONLOCKED, USR_TOKEN,  VERSION)
values (12, 'user1', 'user1', 'Kingsley', 'Ben', 'B.Kingsley@web.de', NULL, true, true, true, true, null, 0);
INSERT INTO SEC_USER (USR_ID, USR_LOGINNAME, USR_PASSWORD, USR_LASTNAME, USR_FIRSTNAME, USR_EMAIL, USR_LOCALE, USR_ENABLED, USR_ACCOUNTNONEXPIRED, USR_CREDENTIALSNONEXPIRED, USR_ACCOUNTNONLOCKED, USR_TOKEN,  VERSION)
values (13, 'headoffice', 'headoffice', 'Willis', 'Bruce', 'B.Willis@web.de', NULL, true, true, true, true, null, 0);
INSERT INTO SEC_USER (USR_ID, USR_LOGINNAME, USR_PASSWORD, USR_LASTNAME, USR_FIRSTNAME, USR_EMAIL, USR_LOCALE, USR_ENABLED, USR_ACCOUNTNONEXPIRED, USR_CREDENTIALSNONEXPIRED, USR_ACCOUNTNONLOCKED, USR_TOKEN,  VERSION)
values (14, 'user2', 'user2', 'Kingdom', 'Marta', 'M.Kingdom@web.de', NULL, true, true, true, true, null, 0);


/******************** Security: ROLES ********************/  
INSERT INTO SEC_ROLE (ROL_ID, ROL_SHORTDESCRIPTION, ROL_LONGDESCRIPTION, VERSION)
values(10000,'ROLE_ADMIN','Administrator Role', 0);
INSERT INTO SEC_ROLE (ROL_ID, ROL_SHORTDESCRIPTION, ROL_LONGDESCRIPTION, VERSION)
values(10002,'ROLE_OFFICE_ALL_RIGHTS','Office User role with all rights granted.', 0);
INSERT INTO SEC_ROLE (ROL_ID, ROL_SHORTDESCRIPTION, ROL_LONGDESCRIPTION, VERSION)
values(10003,'ROLE_GUEST','Guest Role', 0);
INSERT INTO SEC_ROLE (ROL_ID, ROL_SHORTDESCRIPTION, ROL_LONGDESCRIPTION, VERSION)
values(10004,'ROLE_OFFICE_ONLY_VIEW','Office user with rights that granted only view of data.', 0);
INSERT INTO SEC_ROLE (ROL_ID, ROL_SHORTDESCRIPTION, ROL_LONGDESCRIPTION, VERSION)
values(10005,'ROLE_HEADOFFICE_USER','Headoffice User Role', 0);


/******************** Security: USER-ROLES ********************/  
/* Guest account authorities */
INSERT INTO SEC_USERROLE (URR_ID, USR_ID, ROL_ID, VERSION)
values (11000, 10, 10003, 0);
/* User1 Usr-Id: 12 */
INSERT INTO SEC_USERROLE (URR_ID, USR_ID, ROL_ID, VERSION)
values (11001, 12, 10002, 0);
/* Headoffice user account authorities */
INSERT INTO SEC_USERROLE (URR_ID, USR_ID, ROL_ID, VERSION)
values (11010, 13, 10005, 0);

/* Admin Usr-ID: 11 */
INSERT INTO SEC_USERROLE (URR_ID, USR_ID, ROL_ID, VERSION)
values (11003, 11, 10000, 0);
INSERT INTO SEC_USERROLE (URR_ID, USR_ID, ROL_ID, VERSION)
values (11005, 11, 10002, 0);
INSERT INTO SEC_USERROLE (URR_ID, USR_ID, ROL_ID, VERSION)
values (11006, 11, 10003, 0);
INSERT INTO SEC_USERROLE (URR_ID, USR_ID, ROL_ID, VERSION)
values (11008, 11, 10004, 0);
INSERT INTO SEC_USERROLE (URR_ID, USR_ID, ROL_ID, VERSION)
values (11009, 11, 10005, 0);
/* User2 Usr-ID: 14 */
INSERT INTO SEC_USERROLE (URR_ID, USR_ID, ROL_ID, VERSION)
values (11007, 14, 10004, 0);



/******************** Security: GROUPS ********************/  
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13001, 'Headoffice Supervisor Group', 'kjhf ff hgfd', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13002, 'Admin Group - user accounts', 'create/modify user accounts', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13003, 'Guest Group', 'Minimal Rights for the guests', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13004, 'Admin Group - user rights', 'edit/modify user rights', 0);
/* Customers */
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13000, 'Customers_View', 'Allow to  view customers data', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13008, 'Customers_New', 'Allow create new customers', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13006, 'Customers_Edit', 'Allow editing of customers', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13007, 'Customers_Delete', 'Allow deleting of customers', 0);
/* Orders */
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13010, 'Orders_View', 'Allow to view orders data', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13011, 'Orders_New', 'Allow create new orders', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13012, 'Orders_Edit', 'Allow editing of orders', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13013, 'Orders_Delete', 'Allow deleting of orders', 0);
/* Branches */
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13020, 'Branch_View', 'Allow to view branches data', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13021, 'Branch_New', 'Allow create new branches', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13022, 'Branch_Edit', 'Allow editing of branches', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13023, 'Branch_Delete', 'Allow deleting of branches', 0);
/* Articles */
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13030, 'Articles_View', 'Allow to view articles data', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13031, 'Articles_New', 'Allow create new articles', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13032, 'Articles_Edit', 'Allow editing of articles', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13033, 'Articles_Delete', 'Allow deleting of articles', 0);
/* Offices */
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13040, 'Offices_View', 'Allow to view offices data', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13041, 'Offices_New', 'Allow create new offices', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13042, 'Offices_Edit', 'Allow editing of offices', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13043, 'Offices_Delete', 'Allow deleting of offices', 0);

/* Users */
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13060, 'User_View_UsersOnly', 'Allow to view own user data.', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13061, 'User_Edit_UsersOnly', 'Allow to edit own user data.', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13062, 'Users_View', 'Allow to view all users data.', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13063, 'Users_New', 'Allow create new users', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13064, 'Users_Edit', 'Allow editing of users', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13065, 'Users_Delete', 'Allow deleting of users', 0);
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION)
values (13066, 'Users_Search', 'Allow searching of users', 0);




/******************** Security: ROLE-GROUPS ********************/  
/* ROLE_OFFICE_ALL_RIGHTS */
/* Group: Customers_View */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12000, 13000, 10002, 0);
/* Group: Customers_New */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12001, 13008, 10002, 0);
/*  Group: Customers_Edit */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12002, 13006, 10002, 0);
/*  Group: Customers_Delete */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12003, 13007, 10002, 0);
/*  Group: Orders_View */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12004, 13010, 10002, 0);
/*  Group: Orders_New */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12005, 13011, 10002, 0);
/*  Group: Orders_Edit */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12006, 13012, 10002, 0);
/*  Group: Orders_Delete */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12007, 13013, 10002, 0);
/*  Group: User_View_UsersOnly */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12008, 13060, 10002, 0);
/*  Group: User_Edit_UsersOnly */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12009, 13061, 10002, 0);



/* ROLE_OFFICE_ONLY_VIEW */
/* Group: Customers_View */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12010, 13000, 10004, 0);
/*  Group: Orders_View */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12011, 13010, 10004, 0);
/*  Group: User_View_UsersOnly */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12012, 13060, 10004, 0);


/* ROLE_GUEST */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12020, 13003, 10003, 0);



/* ROLE_ADMIN */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12050, 13002, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12051, 13000, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12052, 13001, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12053, 13003, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12054, 13004, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12055, 13006, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12056, 13007, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12057, 13008, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12058, 13010, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12059, 13011, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12060, 13012, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12061, 13013, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12062, 13020, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12063, 13021, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12064, 13022, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12065, 13023, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12066, 13030, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12067, 13031, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12068, 13032, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12069, 13033, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12070, 13040, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12071, 13041, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12072, 13042, 10000, 0);
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12073, 13043, 10000, 0);
/* Group: Users_View */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12074, 13062, 10000, 0);
/* Group: Users_New */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12075, 13063, 10000, 0);
/* Group: Users_Edit */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12076, 13064, 10000, 0);
/* Group: Users_Delete */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12077, 13065, 10000, 0);
/* Group: Users_Search */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12078, 13066, 10000, 0);



/* ROLE_HEADOFFICE_USER */
/* Group: Branch_View */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12100, 13020, 10005, 0);
/* Group: Branch_New */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12101, 13021, 10005, 0);
/* Group: Branch_Edit */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12102, 13022, 10005, 0);
/* Group: Branch_Delete */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12103, 13023, 10005, 0);
/* Group: Articles_View */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12104, 13030, 10005, 0);
/* Group: Articles_New */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12105, 13031, 10005, 0);
/* Group: Articles_Edit */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12106, 13032, 10005, 0);
/* Group: Articles_Delete */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12107, 13033, 10005, 0);
/* Group: Offices_View */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12108, 13040, 10005, 0);
/* Group: Offices_New */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12109, 13041, 10005, 0);
/* Group: Offices_Edit */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12110, 13042, 10005, 0);
/* Group: Offices_Delete */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12111, 13043, 10005, 0);
/*  Group: User_View_UsersOnly */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12115, 13060, 10005, 0);
/*  Group: User_Edit_UsersOnly */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION)
values (12116, 13061, 10005, 0);



/******************** Security: RIGHTS ********************/  
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15000, 1, 'menuCat_OfficeData', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15001, 2, 'menuItem_OfficeData_Customers', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15002, 2, 'menuItem_OfficeData_Orders', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15003, 1, 'menuCat_MainData', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15004, 2, 'menuItem_MainData_ArticleItems', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15005, 2, 'menuItem_MainData_Branch', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15006, 2, 'menuItem_MainData_Office', 0);

INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15007, 1, 'menuCat_Administration', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15008, 2, 'menuItem_Administration_Users', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15009, 2, 'menuItem_Administration_UserRoles', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15010, 2, 'menuItem_Administration_Roles', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15011, 2, 'menuItem_Administration_RoleGroups', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15012, 2, 'menuItem_Administration_Groups', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15013, 2, 'menuItem_Administration_GroupRights', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15014, 2, 'menuItem_Administration_Rights', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15015, 1, 'menuCat_UserRights', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15016, 2, 'menuItem_Administration_LoginsLog', 0);




/* Pages = Type(0) */
/* --> Page Customer */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15100, 0, 'window_customerList', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15101, 0, 'window_customerDialog', 0);
/* --> Page Orders */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15102, 0, 'orderListWindow', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15103, 0, 'orderDialogWindow', 0);
/* --> Page Articles */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15104, 0, 'window_ArticlesList', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15105, 0, 'window_ArticlesDialog', 0);
/* --> Page Branches */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15106, 0, 'window_BranchesList', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15107, 0, 'window_BranchesDialog', 0);
/* --> Page Offices */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15108, 0, 'window_OfficeList', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15109, 0, 'window_OfficeDialog', 0);
/* --> Page Admin - Users */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15110, 0, 'page_Admin_UserList', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15111, 0, 'page_Admin_UserDialog', 0);
/* --> Page Admin - UserRoles */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15112, 0, 'page_Security_UserRolesList', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15113, 0, 'page_Security_RolesList', 0);
/* --> Page Admin - Roles */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15114, 0, 'page_Security_RolesDialog', 0);
/* --> Page Admin - RoleGroups */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15115, 0, 'page_Security_RoleGroupsList', 0);
/* --> Page Admin - Groups */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15116, 0, 'page_Security_GroupsList', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15117, 0, 'page_Security_GroupsDialog', 0);
/* --> Page Admin - GroupRights */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15118, 0, 'page_Security_GroupRightsList', 0);
/* --> Page Admin - Rights */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15119, 0, 'page_Security_RightsList', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15120, 0, 'page_Security_RightsDialog', 0);
/* --> Page Login Log */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15121, 0, 'page_Admin_LoginLogList', 0);
/* --> Nachtrag Page Orders */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15122, 0, 'orderPositionDialogWindow', 0);


/* Tabs = Type(5) */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15200, 5, 'tab_CustomerDialog_Address', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15201, 5, 'tab_CustomerDialog_Chart', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15202, 5, 'tab_CustomerDialog_Orders', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15203, 5, 'tab_CustomerDialog_Memos', 0);


/* Components = Type(6) */
/* --> CustomerList BUTTON */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15300, 6, 'button_CustomerList_btnHelp', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15301, 6, 'button_CustomerList_NewCustomer', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15302, 6, 'button_CustomerList_CustomerFindDialog', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15303, 6, 'button_CustomerList_PrintList', 0);
/* --> CustomerDialog BUTTON */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15305, 6, 'button_CustomerDialog_btnHelp', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15306, 6, 'button_CustomerDialog_btnNew', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15307, 6, 'button_CustomerDialog_btnEdit', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15308, 6, 'button_CustomerDialog_btnDelete', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15309, 6, 'button_CustomerDialog_btnSave', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15310, 6, 'button_CustomerDialog_btnClose', 0);

/* --> OrderList BUTTON */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15400, 6, 'button_OrderList_btnHelp', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15401, 6, 'button_OrderList_NewOrder', 0);
/* --> OrderDialog BUTTON */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15410, 6, 'button_OrderDialog_btnHelp', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15411, 6, 'button_OrderDialog_btnNew', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15412, 6, 'button_OrderDialog_btnEdit', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15413, 6, 'button_OrderDialog_btnDelete', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15414, 6, 'button_OrderDialog_btnSave', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15415, 6, 'button_OrderDialog_btnClose', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15416, 6, 'button_OrderDialog_PrintOrder', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15417, 6, 'button_OrderDialog_NewOrderPosition', 0);

/* --> OrderPositionDialog BUTTON */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15430, 6, 'button_OrderPositionDialog_btnHelp', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15431, 6, 'button_OrderPositionDialog_PrintOrderPositions', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15432, 6, 'button_OrderPositionDialog_btnNew', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15433, 6, 'button_OrderPositionDialog_btnEdit', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15434, 6, 'button_OrderPositionDialog_btnDelete', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15435, 6, 'button_OrderPositionDialog_btnSave', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15436, 6, 'button_OrderPositionDialog_btnClose', 0);

/* USERS */
/* --> userListWindow */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15470, 0, 'userListWindow', 0);
/* --> userListWindow BUTTONS*/
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15471, 6, 'button_UserList_btnHelp', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15472, 6, 'button_UserList_NewUser', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15473, 6, 'button_UserList_PrintUserList', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15474, 6, 'button_UserList_SearchLoginname', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15475, 6, 'button_UserList_SearchLastname', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15476, 6, 'button_UserList_SearchEmail', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15477, 6, 'checkbox_UserList_ShowAll', 0);
/* --> Mehode onDoubleClick Listbox */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15778, 3, 'UserList_listBoxUser.onDoubleClick', 0);

/* --> userDialogWindow */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15480, 0, 'userDialogWindow', 0);
/* --> userDialogWindow BUTTONS*/
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15481, 6, 'button_UserDialog_btnHelp', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15482, 6, 'button_UserDialog_btnNew', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15483, 6, 'button_UserDialog_btnEdit', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15484, 6, 'button_UserDialog_btnDelete', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15485, 6, 'button_UserDialog_btnSave', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15486, 6, 'button_UserDialog_btnClose', 0);
/* --> userDialogWindow Special Admin Panels */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15487, 6, 'panel_UserDialog_Status', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15488, 6, 'panel_UserDialog_SecurityToken', 0);
/* --> userListWindow Search panel */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15489, 6, 'hbox_UserList_SearchUsers', 0);
/* Tab Details */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15490, 6, 'tab_UserDialog_Details', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15491, 3, 'data_SeeAllUserData', 0);



/* BRANCHES */
/* branchListWindow Buttons*/
/* --> button_BranchList_btnHelp */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15500, 0, 'button_BranchList_btnHelp', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15501, 0, 'button_BranchList_NewBranch', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15502, 0, 'button_BranchList_PrintBranches', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15503, 0, 'button_BranchList_Search_BranchName', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15504, 0, 'button_BranchList_Search_BranchNo', 0);

/* branchDialogWindow BUTTONS */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15510, 6, 'button_BranchDialog_btnHelp', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15511, 6, 'button_BranchDialog_btnNew', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15512, 6, 'button_BranchDialog_btnEdit', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15513, 6, 'button_BranchDialog_btnDelete', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15514, 6, 'button_BranchDialog_btnSave', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15515, 6, 'button_BranchDialog_btnClose', 0);

/* ARTICLES */
/* window_ArticlesList Buttons*/
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15530, 6, 'button_ArticlesList_btnHelp', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15531, 6, 'button_ArticleList_NewArticle', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15532, 6, 'button_ArticleList_PrintList', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15533, 6, 'button_ArticleList_SearchArticleID', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15534, 6, 'button_ArticleList_SearchName', 0);

/* window_ArticlesDialog Buttons*/
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15540, 6, 'button_ArticlesDialog_btnHelp', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15541, 6, 'button_ArticlesDialog_btnNew', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15542, 6, 'button_ArticlesDialog_btnEdit', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15543, 6, 'button_ArticlesDialog_btnDelete', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15544, 6, 'button_ArticlesDialog_btnSave', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15545, 6, 'button_ArticlesDialog_btnClose', 0);

/* OFFICES */
/* window_OfficeList Buttons*/
/* --> button_BranchList_btnHelp */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15600, 6, 'button_OfficeList_btnHelp', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15601, 6, 'button_OfficeList_NewOffice', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15602, 6, 'button_OfficeList_PrintList', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15603, 6, 'button_OfficeList_SearchNo', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15604, 6, 'button_OfficeList_SearchName', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15605, 6, 'button_OfficeList_SearchCity', 0);

/* window_OfficeDialog BUTTONS */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15610, 6, 'button_OfficeDialog_PrintOffice', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15611, 6, 'button_OfficeDialog_btnHelp', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15612, 6, 'button_OfficeDialog_btnNew', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15613, 6, 'button_OfficeDialog_btnEdit', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15614, 6, 'button_OfficeDialog_btnDelete', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15615, 6, 'button_OfficeDialog_btnSave', 0);
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15616, 6, 'button_OfficeDialog_btnClose', 0);




/* Method/Event = Type(3) */
/* --> CustomerList BUTTON */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION)
values (15700, 3, 'CustomerList_listBoxCustomer.onDoubleClick', 0);



/******************** Security: GROUP-RIGHTS ********************/  



/* Headoffice Supervisor Group*/
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14003, 13001, 15003, 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14004, 13001, 15004, 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14005, 13001, 15005, 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14006, 13001, 15006, 0);
/* Administration Group*/
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14007, 13002, 15007, 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14008, 13002, 15008, 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14009, 13002, 15009, 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14010, 13002, 15010, 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14011, 13002, 15011, 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14012, 13002, 15012, 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14013, 13002, 15013, 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14014, 13002, 15014, 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14015, 13002, 15015, 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14016, 13002, 15016, 0);



/* Neu */
/* Group: Customers_View */
/* Right: menuCat_OfficeData */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14200, 13000, 15000, 0);
/* Right: menuItem_OfficeData_Customers */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14201, 13000, 15001, 0);
/* Right: customerListWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14202, 13000, 15100, 0);
/* Right: button_CustomerList_Help */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14203, 13000, 15305, 0);
/* Right: CustomerList_listBoxCustomer.onDoubleClick */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14204, 13000, 15700, 0);


/* Right: customerDialogWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14205, 13000, 15101, 0);
/* Right: button_CustomerDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14206, 13000, 15310, 0);
/* Right: button_CustomerList_Help */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14207, 13000, 15300, 0);
/* Right: button_CustomerList_CustomerFindDialog */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14208, 13000, 15302, 0);
/* Right: button_CustomerList_PrintList */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14209, 13000, 15303, 0);

/* Tab tab_CustomerDialog_Address */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14210, 13000, 15200, 0);
/* Tab tab_CustomerDialog_Addition */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14211, 13000, 15201, 0);
/* Tab tab_CustomerDialog_Orders */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14212, 13000, 15202, 0);
/* Tab tab_CustomerDialog_Memos */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14213, 13000, 15203, 0);



/* Group: Customers_New */
/* Right: customerListWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14230, 13008, 15100, 0);
/* Right: button_CustomerList_NewCustomer */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14231, 13008, 15301, 0);
/* Right: customerDialogWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14232, 13008, 15101, 0);
/* Right: button_CustomerDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14233, 13008, 15310, 0);
/* Right: button_CustomerDialog_btnNew */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14234, 13008, 15306, 0);
/* Right: button_CustomerDialog_btnEdit */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14235, 13008, 15307, 0);
/* Right: button_CustomerDialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14236, 13008, 15309, 0);


/* Group: Customers_Edit */
/* Right: customerListWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14240, 13006, 15100, 0);

/* Right: customerDialogWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14241, 13006, 15101, 0);
/* Right: button_CustomerDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14242, 13006, 15310, 0);
/* Right: button_CustomerDialog_btnEdit */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14243, 13006, 15307, 0);
/* Right: button_CustomerDialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14244, 13006, 15309, 0);


/* Group: Customers_Delete */
/* Right: customerListWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14250, 13007, 15100, 0);
/* Right: customerDialogWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14251, 13007, 15101, 0);
/* Right: button_CustomerDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14252, 13007, 15310, 0);
/* Right: button_CustomerDialog_btnDelete */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14253, 13007, 15308, 0);

/*----------------------------------------------------------*/
/* Group: Orders_View */
/* Right: menuCat_OfficeData */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14300, 13010, 15000, 0);
/* Right: menuItem_OfficeData_Orders */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14301, 13010, 15002, 0);
/* Right: orderListWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14302, 13010, 15102, 0);
/* Right: button_OrderList_btnHelp */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14303, 13010, 15400, 0);
/* Right: CustomerList_listBoxCustomer.onDoubleClick */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14304, 13010, 15700, 0);
/* Right: orderDialogWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14305, 13010, 15103, 0);
/* Right: button_OrderDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14306, 13010, 15415, 0);
/* Right: button_OrderDialog_btnHelp */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14307, 13010, 15410, 0);
/* Right: button_OrderDialog_PrintOrder */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14308, 13010, 15416, 0);
/* Right: orderPositionDialogWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14309, 13010, 15122, 0);
/* Right: button_OrderPositionDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14310, 13010, 15436, 0);
/* Right: button_OrderPositionDialog_btnHelp */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14311, 13010, 15430, 0);
/* Right: button_OrderPositionDialog_PrintOrder */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14312, 13010, 15431, 0);




/* Group: Orders_New */
/* Right: button_OrderList_NewOrder */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14320, 13011, 15401, 0);
/* Right: button_OrderDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14321, 13011, 15415, 0);
/* Right: button_OrderDialog_btnNew */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14322, 13011, 15411, 0);
/* Right: button_OrderDialog_btnEdit */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14323, 13011, 15412, 0);
/* Right: button_CustomerDialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14324, 13011, 15414, 0);
/* Right: button_OrderDialog_NewOrderPosition */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14325, 13011, 15417, 0);
/* OrderPositions -------------------*/
/* Right: button_OrderPositionDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14326, 13011, 15436, 0);
/* Right: button_OrderPositionDialog_btnNew */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14327, 13011, 15432, 0);
/* Right: button_OrderPositionDialog_btnEdit */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14328, 13011, 15433, 0);
/* Right: button_OrderPositionDialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14329, 13011, 15435, 0);


/* Group: Orders_Edit */
/* Right: button_OrderDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14330, 13012, 15415, 0);
/* Right: button_OrderDialog_btnEdit */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14331, 13012, 15412, 0);
/* Right: button_CustomerDialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14332, 13012, 15414, 0);
/* Right: button_OrderDialog_NewOrderPosition */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14333, 13012, 15417, 0);
/* OrderPositions -------------------*/
/* Right: button_OrderPositionDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14334, 13012, 15436, 0);
/* Right: button_OrderPositionDialog_btnEdit */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14335, 13012, 15433, 0);
/* Right: button_OrderPositionDialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14336, 13012, 15435, 0);


/* Group: Orders_Delete */
/* Right: button_OrderDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14340, 13013, 15415, 0);
/* Right: button_OrderDialog_btnDelete */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14341, 13013, 15413, 0);
/* Right: button_OrderDialog_NewOrderPosition */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14342, 13013, 15417, 0);
/* OrderPositions -------------------*/
/* Right: button_OrderPositionDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14343, 13013, 15436, 0);
/* Right: button_OrderPositionDialog_btnDelete */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14344, 13013, 15434, 0);



/* USERS ----------------- */
/* Group: User_View_UsersOnly */
/* Right: menuCat_Administration */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14360, 13060, 15007, 0);
/* Right: menuItem_Administration_Users */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14361, 13060, 15008, 0);
/* Right: userListWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14362, 13060, 15470, 0);
/* Right: button_UserList_btnHelp */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14363, 13060, 15471, 0);
/* Right: UserList_listBoxUser.onDoubleClick */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14364, 13060, 15778, 0);
/* Right: userDialogWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14365, 13060, 15480, 0);
/* Right: tab_UserDialog_Details */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14366, 13060, 15490, 0);
/* Right: button_UserDialog_btnHelp */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14367, 13060, 15481, 0);
/* Right: button_Dialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14368, 13060, 15486, 0);

/* Group: User_Edit_UsersOnly */
/* Right: button_UserDialog_btnEdit */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14370, 13061, 15483, 0);
/* Right: button_Dialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14371, 13061, 15485, 0);


/* Group: Users_View */
/* Right: menuCat_Administration */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14380, 13062, 15007, 0);
/* Right: menuItem_Administration_Users */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14381, 13062, 15008, 0);
/* Right: userListWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14382, 13062, 15470, 0);
/* Right: button_UserList_btnHelp */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14383, 13062, 15471, 0);
/* Right: button_UserList_PrintUserList */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14384, 13062, 15473, 0);
/* Right: UserList_listBoxUser.onDoubleClick */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14385, 13062, 15778, 0);
/* Right: userDialogWindow */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14386, 13062, 15480, 0);
/* Right: tab_UserDialog_Details */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14387, 13062, 15490, 0);
/* Right: button_UserDialog_btnHelp */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14388, 13062, 15481, 0);
/* Right: button_UserDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14389, 13062, 15486, 0);
/* Right: panel_UserDialog_Status */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14390, 13062, 15487, 0);
/* Right: panel_UserDialog_SecurityToken */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14391, 13062, 15488, 0);
/* Right: data_SeeAllUserData */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14392, 13062, 15491, 0);


/* Group: Users_New */
/* Right: button_UserList_NewUser */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14395, 13063, 15472, 0);
/* Right: button_UserDialog_btnNew */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14396, 13063, 15482, 0);
/* Right: button_UserDialog_btnEdit */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14397, 13063, 15483, 0);
/* Right: button_UserDialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14398, 13063, 15485, 0);

/* Group: Users_Edit */
/* Right: button_UserDialog_btnEdit */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14400, 13064, 15483, 0);
/* Right: button_UserDialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14401, 13064, 15485, 0);

/* Group: Users_Delete */
/* Right: button_UserDialog_btnDelete */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14410, 13065, 15484, 0);

/* Group: Users_Search */
/* Right: hbox_UserList_SearchUsers */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14420, 13066, 15489, 0);


/* B r a n c h e s */
/* Group: Branch_View */
/* Right: menuCat_MainData */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14500, 13020, 15003, 0);
/* Right: menuItem_MainData_Branch */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14501, 13020, 15005, 0);
/* Right: page_BranchesList */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14502, 13020, 15106, 0);
/* Right: button_BranchList_btnHelp */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14503, 13020, 15500, 0);
/* Right: button_BranchList_PrintBranches */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14504, 13020, 15502, 0);
/* Right: button_BranchList_Search_BranchName */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14505, 13020, 15503, 0);
/* Right: button_BranchList_Search_BranchNo */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14506, 13020, 15504, 0);
/* Right: page_BranchesDialog */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14507, 13020, 15107, 0);
/* Right: button_BranchDialog_btnHelp */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14508, 13020, 15510, 0);
/* Right: button_BranchDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14509, 13020, 15515, 0);

/* Group: Branch_New */
/* Right: button_BranchList_NewBranch */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14510, 13021, 15501, 0);
/* Right: button_BranchDialog_btnNew */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14511, 13021, 15511, 0);
/* Right: button_BranchDialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14512, 13021, 15514, 0);

/* Group: Branch_Edit */
/* Right: button_BranchDialog_btnEdit */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14520, 13022, 15512, 0);
/* Right: button_BranchDialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14521, 13022, 15514, 0);

/* Group: Branch_Delete */
/* Right: button_BranchDialog_btnDelete */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14530, 13023, 15513, 0);


/* A r t i c l e s */
/* Group: Articles_View */
/* Right: menuCat_MainData */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14540, 13030, 15003, 0);
/* Right: menuItem_MainData_ArticleItems */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14541, 13030, 15004, 0);
/* Right: window_ArticlesList */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14542, 13030, 15104, 0);
/* Right: button_ArticlesList_btnHelp */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14543, 13030, 15530, 0);
/* Right: button_ArticleList_PrintList */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14544, 13030, 15532, 0);
/* Right: window_ArticlesDialog */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14545, 13030, 15105, 0);
/* Right: button_ArticlesDialog_btnHelp */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14546, 13030, 15540, 0);
/* Right: button_ArticlesDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14547, 13030, 15545, 0);

/* Group: Articles_New */
/* Right: button_ArticleList_NewArticle */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14550, 13031, 15531, 0);
/* Right: button_ArticlesDialog_btnNew */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14551, 13031, 15541, 0);
/* Right: button_ArticlesDialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14552, 13031, 15544, 0);

/* Group: Articles_Edit */
/* Right: button_ArticlesDialog_btnEdit */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14555, 13032, 15542, 0);
/* Right: button_ArticlesDialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14556, 13032, 15544, 0);

/* Group: Articles_Delete */
/* Right: button_ArticlesDialog_btnDelete */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14560, 13033, 15543, 0);


/* O F F I C E S */
/* Group: Offices_View */
/* Right: menuCat_MainData */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14570, 13040, 15003, 0);
/* Right: menuItem_MainData_Office */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14571, 13040, 15006, 0);
/* Right: window_OfficesList */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14572, 13040, 15108, 0);
/* Right: button_OfficeList_btnHelp */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14573, 13040, 15600, 0);
/* Right: button_OfficeList_PrintList */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14574, 13040, 15602, 0);
/* Right: button_OfficeList_SearchNo */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14575, 13040, 15603, 0);
/* Right: button_OfficeList_SearchName */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14576, 13040, 15604, 0);
/* Right: button_OfficeList_SearchCity */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14577, 13040, 15605, 0);
/* Right: window_OfficesDialog */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14578, 13040, 15109, 0);
/* Right: button_OfficeDialog_btnHelp */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14579, 13040, 15611, 0);
/* Right: button_OfficeDialog_btnClose */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14580, 13040, 15616, 0);
/* Right: button_OfficeDialog_PrintOffice */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14581, 13040, 15610, 0);


/* Group: Offices_New */
/* Right: button_OfficeList_NewOffice */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14585, 13041, 15601, 0);
/* Right: button_OfficeDialog_btnNew */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14586, 13041, 15612, 0);
/* Right: button_OfficeDialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14587, 13041, 15615, 0);

/* Group: Offices_Edit */
/* Right: button_OfficeDialog_btnEdit */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14590, 13042, 15613, 0);
/* Right: button_OfficeDialog_btnSave */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14591, 13042, 15615, 0);

/* Group: Offices_Delete */
/* Right: button_OfficeDialog_btnDelete */
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION)
values (14595, 13043, 15614, 0);


/* GUESTBOOK init */
INSERT INTO GUESTBOOK (GUB_ID, GUB_SUBJECT, GUB_DATE, GUB_USR_NAME, GUB_TEXT, VERSION)
values (30000, 'Guestbook opened.', CURRENT_TIMESTAMP(), 'admin', 'Guestbook is now openend. Please leave a comment', 0);

/* LOGIN-LOG init, for showing the serverPush sample */
INSERT INTO SEC_LOGINLOG (lgl_id, lgl_loginname, lgl_logtime, lgl_ip, lgl_status_id, lgl_sessionid)
values (40000, 'user1', CURRENT_TIMESTAMP(), '78.23.43.55', 1, '5A5F4DF58E11F8F95A6AB6F2183D6F00');
INSERT INTO SEC_LOGINLOG (lgl_id, lgl_loginname, lgl_logtime, lgl_ip, lgl_status_id, lgl_sessionid)
values (40001, 'admin', CURRENT_TIMESTAMP(), '78.23.43.58', 1, '5A5F4DF66E11F8F95A6AB6F2183D6F00');
INSERT INTO SEC_LOGINLOG (lgl_id, lgl_loginname, lgl_logtime, lgl_ip, lgl_status_id, lgl_sessionid)
values (40002, 'user2', CURRENT_TIMESTAMP(), '78.23.43.58', 1, '5A5F4DF66E11F8F95A6SD6F2183D6F00');
INSERT INTO SEC_LOGINLOG (lgl_id, lgl_loginname, lgl_logtime, lgl_ip, lgl_status_id, lgl_sessionid)
values (40003, 'hacker', CURRENT_TIMESTAMP(), '156.23.47.144', 0, '5A5F4DF66E22F8F95A6SD6F2183D6F00');
INSERT INTO SEC_LOGINLOG (lgl_id, lgl_loginname, lgl_logtime, lgl_ip, lgl_status_id, lgl_sessionid)
values (40004, 'user2', CURRENT_TIMESTAMP(), '88.21.143.158', 1, '5A5H7DF66E11F8F95A6SD6F2183D6F00');
INSERT INTO SEC_LOGINLOG (lgl_id, lgl_loginname, lgl_logtime, lgl_ip, lgl_status_id, lgl_sessionid)
values (40005, 'user2', CURRENT_TIMESTAMP(), '144.21.143.158', 1, '5A5H7DF66E11F8F95A6SD6F2183D6F00');
INSERT INTO SEC_LOGINLOG (lgl_id, lgl_loginname, lgl_logtime, lgl_ip, lgl_status_id, lgl_sessionid)
values (40006, 'crack', CURRENT_TIMESTAMP(), '178.21.143.15', 0, '5A5H7DF66E11F8F95A6SD6F2183D6F00');
INSERT INTO SEC_LOGINLOG (lgl_id, lgl_loginname, lgl_logtime, lgl_ip, lgl_status_id, lgl_sessionid)
values (40007, 'admin', CURRENT_TIMESTAMP(), '166.21.143.58', 1, '5A5H7DF66E11F8F95A6SD6F2183D6F00');
INSERT INTO SEC_LOGINLOG (lgl_id, lgl_loginname, lgl_logtime, lgl_ip, lgl_status_id, lgl_sessionid)
values (40008, 'user2', CURRENT_TIMESTAMP(), '221.21.143.18', 0, '5A5H7DF66E11F8F95A6SD6F2183D6F00');
INSERT INTO SEC_LOGINLOG (lgl_id, lgl_loginname, lgl_logtime, lgl_ip, lgl_status_id, lgl_sessionid)
values (40009, 'admin', CURRENT_TIMESTAMP(), '82.41.143.166', 1, '5A5H7DF66E11F8F95A6SD6F2183D6F00');
INSERT INTO SEC_LOGINLOG (lgl_id, lgl_loginname, lgl_logtime, lgl_ip, lgl_status_id, lgl_sessionid)
values (40010, 'user2', CURRENT_TIMESTAMP(), '82.121.143.22', 1, '5A5H7DF66E11F8F95A6SD6F2183D6F00');
INSERT INTO SEC_LOGINLOG (lgl_id, lgl_loginname, lgl_logtime, lgl_ip, lgl_status_id, lgl_sessionid)
values (40011, 'user1', CURRENT_TIMESTAMP(), '81.221.143.72', 1, '5A5H7DF66E11F8F95A6SD6F2183D6F00');


/* BRANCHE */
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1000, '100', 'Elektro',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1001, '101', 'Maler',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1002, '102', 'Holzverabeitung',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1003, '103',  'Kaufmännisch',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1004, '104',  'Versicherung',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1005, '105', 'Mess- und Regeltechnik',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1006, '106', 'Industriemontagen',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1007, '107', 'KFZ',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1008, '108', 'Banken',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1009, '109', 'Grosshandel',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1010, '110', 'Einzelhandel',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1011, '111', 'Werbung',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1012, '112', 'Gastronomie',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1014, '114', 'Pflegedienste',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1015, '115', 'Transportwesen',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1016, '116', 'Metallverarbeitung',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1017, '117', 'Schlosserei',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1018, '118', 'Sanitär',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1019, '119', 'Heizungsbau',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1020, '120', 'Wasserwirtschaft',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1021, '121', 'Schiffsbau',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1022, '122', 'Lärmschutz',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1023, '123', 'Gerüstbau',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1024, '124', 'Fassadenbau',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1025, '125', 'Farbherstellung',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1026, '126', 'Kieswerk',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1027, '127', 'Blechnerei',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1028, '128', 'Gerüstverleih',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1029, '129', 'Pflasterarbeiten',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1030, '130', 'Trockenbau',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1031, '131', 'Trockenbau- und Sanierung',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1032, '132', 'Hühnerfarm',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1033, '000', '',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1034, '134', 'Transportwesen allgemein',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1035, '135', 'Schwertransport',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1036, '136', 'Gefahrgut Transport',0);
INSERT INTO BRANCHE (BRA_ID, BRA_NR, BRA_BEZEICHNUNG, VERSION)
values (1037, '137', 'Spedition',0);

/* CUSTOMER */
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (20, 1000, '20', 'MÜLLER','--> Müller','Elektroinstallationen','Freiburg',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (21, 1000, '21', 'HUBER','--> Huber','Elektroinstallationen','Oberursel',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (22, 1000, '22', 'SIEMENS','Siemens AG','Elektroinstallationen','Braunschweig',false,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (23, 1000, '23', 'AEG','AEG','Elektroinstallationen','Stuttgart',false,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (24, 1019, '24', 'BUDERUS','Buderus Heizungsbau GmbH','Elektroinstallationen','Rastatt',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (25, 1000, '25', 'MEILER','Elektro Meiler','Inhaber W. Erler','Karlsruhe',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (26, 1000, '26', 'BADER','Bader GmbH','Elektroinstallationen','Berlin',false,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (27, 1000, '27', 'HESKENS','Heskens GmbH','Elektroinstallationen','Badenweiler',false,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (28, 1000, '28', 'MAIER','Maier GmbH','Elektroinstallationen','Friedberg',false,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (29, 1000, '29', 'SCHULZE','Schulze GmbH','Elektroinstallationen','Freiburg',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (30, 1004, '30', 'SCHMIERFINK','Schmierfink AG','Versicherungen','Freiburg',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (31, 1005, '31', 'SCHULZE','Schulze Ltd.','Anlagenbau','Buxtehude',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (32, 1005, '32', 'SCHREINER','Schreiner','SPS-Steuerungsbau','Hamburg',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (33, 1004, '33', 'GUTE RUHE','Gute Ruhe AG','Versicherungen','Berlin',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (34, 1003, '34', 'FREIBERGER','Freiberger GmbH','In- und Export','Aachen',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (35, 1002, '35', 'BERGMANN','Sägewerk Bergmann','Holzverarbeitung','Neustadt',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (2000, 1002, '2000', 'SEILER','Sägewerk Seiler','Holzverarbeitung','Freiburg',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (2001, 1002, '2001', 'BAUER','Hermann Bauer','Sägerwerk','Titisee-Neustadt',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (2002, 1000, '2002', 'BEINHARD','Gebrüder Beinhard GbR','Elektroinstallationen','München',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (2003, 1000, '2003', 'ADLER','Reiner Adler','Elektro Montagen','Dreieich',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (2004, 1000, '2004', 'FINK','Hartmut Fink GmbH','Elektro- und Industriemontagen','Stuttgart',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (2005, 1000, '2005', 'GERHARD','Huber u. Gerhard GbR','Elektroinstallationen','Stuttgart',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (2006, 1004, '2006', 'BELIANZ','BELIANZ','Versicherungs AG','Berlin',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (2007, 1004, '2007', 'KINTERTHUR','Kinterthur','Versicherungs AG','Rastatt',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (2008, 1004, '2008', 'WOLFRAM','Peter Wolfram','Freier Versicherungsvertreter','Norderstedt',true,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (2009, 1000, '2009', 'HESSING','Mario Hessing GmbH','Elektroinstallationen','Rheinweiler',false,0);
INSERT INTO KUNDE (KUN_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION)
VALUES (2010, 1000, '2010', 'FREIBERG','Werner Freiberg GmbH','Elektroinstallationen','Rheinstetten',false,0);

/* ORDER */
INSERT INTO AUFTRAG (AUF_ID,AUF_KUN_ID, AUF_NR, AUF_BEZEICHNUNG, VERSION)
VALUES (40, 20, 'AUF-40', 'EGRH Modul1',0);
INSERT INTO AUFTRAG (AUF_ID,AUF_KUN_ID, AUF_NR, AUF_BEZEICHNUNG, VERSION)
VALUES (41, 20, 'AUF-41', 'OMEGA Modul2',0);
INSERT INTO AUFTRAG (AUF_ID,AUF_KUN_ID, AUF_NR, AUF_BEZEICHNUNG, VERSION)
VALUES (42, 20, 'AUF-42', 'Keller',0);
INSERT INTO AUFTRAG (AUF_ID,AUF_KUN_ID, AUF_NR, AUF_BEZEICHNUNG, VERSION)
VALUES (43, 20, 'AUF-43', 'Schlossallee 23',0);
INSERT INTO AUFTRAG (AUF_ID,AUF_KUN_ID, AUF_NR, AUF_BEZEICHNUNG, VERSION)
VALUES (44, 21, 'AUF-44', 'Renovierung Keller',0);
INSERT INTO AUFTRAG (AUF_ID,AUF_KUN_ID, AUF_NR, AUF_BEZEICHNUNG, VERSION)
VALUES (45, 21, 'AUF-45', 'Renovierung Hochhaus Ilsestrasse 5',0);
INSERT INTO AUFTRAG (AUF_ID,AUF_KUN_ID, AUF_NR, AUF_BEZEICHNUNG, VERSION)
VALUES (46, 21, 'AUF-46', 'Verteilerschrank Umbau; Fa. Klöckner EHG',0);

/* ARTICLE */
INSERT INTO ARTIKEL (ART_ID,ART_KURZBEZEICHNUNG,ART_LANGBEZEICHNUNG,ART_NR,ART_PREIS,VERSION)
VALUES (3000,'Kabelverschraubung DN 27','Kabelverschraubung Messing verchromt DN 27','KS3000',4.23,0);
INSERT INTO ARTIKEL (ART_ID,ART_KURZBEZEICHNUNG,ART_LANGBEZEICHNUNG,ART_NR,ART_PREIS,VERSION)
VALUES (3001,'3-adriges Kabel 1,5mm ','mehradriges Kabel, 3 Adern, Farbe=grau, Querschnitt= 1,5 mm','MK3001',0.12,0);
INSERT INTO ARTIKEL (ART_ID,ART_KURZBEZEICHNUNG,ART_LANGBEZEICHNUNG,ART_NR,ART_PREIS,VERSION)
VALUES (3002,'Lüsterklemmen 10-fach, bis 1.5mm','Lüsterklemmen, grau, bis Querschnitt 1.5mm','LUESTER1002',0.78,0);
INSERT INTO ARTIKEL (ART_ID,ART_KURZBEZEICHNUNG,ART_LANGBEZEICHNUNG,ART_NR,ART_PREIS,VERSION)
VALUES (3003,'Euro-Platine','Euro-Platine für Versuchsaufbauten','PLAT3003',2.34,0);
INSERT INTO ARTIKEL (ART_ID,ART_KURZBEZEICHNUNG,ART_LANGBEZEICHNUNG,ART_NR,ART_PREIS,VERSION)
VALUES (3004,'Leuchtmittel 22 Watt','Leuchtmittel 22 Watt Sparlampe weiss, mittlere Lebensdauer ca. 6000 Std.','SPARLA3004',2.84,0);
INSERT INTO ARTIKEL (ART_ID,ART_KURZBEZEICHNUNG,ART_LANGBEZEICHNUNG,ART_NR,ART_PREIS,VERSION)
VALUES (3005,'Leuchte einzel bis 100 Watt','Hängeleuchte einzel, Farbe=grau, bis 100 Watt','LEU3005',32.00,0);
INSERT INTO ARTIKEL (ART_ID,ART_KURZBEZEICHNUNG,ART_LANGBEZEICHNUNG,ART_NR,ART_PREIS,VERSION)
VALUES (3006,'5-adriges Kabel 1,5mm','mehradriges Kabel, 5 Adern, Farbe=grau, Querschnitt= 1,5 mm','MK3006',0.22, 0);
INSERT INTO ARTIKEL (ART_ID,ART_KURZBEZEICHNUNG,ART_LANGBEZEICHNUNG,ART_NR,ART_PREIS,VERSION)
VALUES (3007,'Kabelbinder 12cm','Kabelbinder, Menge: 100 Stk. Länge: 12 cm, Farbe: weiss','KB3007',1.34, 0);
INSERT INTO ARTIKEL (ART_ID,ART_KURZBEZEICHNUNG,ART_LANGBEZEICHNUNG,ART_NR,ART_PREIS,VERSION)
VALUES (3008,'Kabelverschraubung DN 17','Kabelverschraubung Messing verchromt DN 17','KS3008',2.90,0);
INSERT INTO ARTIKEL (ART_ID,ART_KURZBEZEICHNUNG,ART_LANGBEZEICHNUNG,ART_NR,ART_PREIS,VERSION)
VALUES (3009,'Kabelverschraubung DN 18','Kabelverschraubung Messing verchromt DN 18','KS3009',3.20,0);
INSERT INTO ARTIKEL (ART_ID,ART_KURZBEZEICHNUNG,ART_LANGBEZEICHNUNG,ART_NR,ART_PREIS,VERSION)
VALUES (3010,'Kabelverschraubung DN 22','Kabelverschraubung Messing verchromt DN 22','KS3010',3.40,0);
INSERT INTO ARTIKEL (ART_ID,ART_KURZBEZEICHNUNG,ART_LANGBEZEICHNUNG,ART_NR,ART_PREIS,VERSION)
VALUES (3011,'Lüsterklemmen 10-fach, bis 2.5mm','Lüsterklemmen, grau, bis Querschnitt 1.5mm','LUESTER3011',1.68,0);
INSERT INTO ARTIKEL (ART_ID,ART_KURZBEZEICHNUNG,ART_LANGBEZEICHNUNG,ART_NR,ART_PREIS,VERSION)
VALUES (3012,'Lüsterklemmen 10-fach, bis 4.5mm','Lüsterklemmen, grau, bis Querschnitt 1.5mm','LUESTER3012',3.10,0);



/* ORDERR POSITION */
/* Order No.: 40 */
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (60,40, 3000, 1, 240, 1.20, 288.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (61,40, 3001, 2, 1200, 0.45, 540.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (62,40, 3002, 3, 40, 0.20, 8.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (63,40, 3003, 4, 15, 4.20, 63.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (64,40, 3004, 5, 20, 4.30, 86.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (65,40, 3005, 6, 15, 40.00, 600.00, 0);
/* Order No.: 41 */
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (66,41, 3005, 1, 18, 40.00, 720.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (67,41, 3006, 2, 800 , 0.45, 360.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (68,41, 3002, 3, 40, 0.20, 8.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
/* Order No.: 44 */
VALUES (69,44, 3007, 1, 3, 2.10, 6.30, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (70,44, 3001, 2, 1200, 0.45, 540.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (71,44, 3002, 3, 40, 0.20, 8.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (72,44, 3003, 4, 15, 4.20, 63.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (73,44, 3004, 5, 20, 4.30, 86.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (74,44, 3005, 6, 15, 40.00, 600.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
/* Order No.: 45 */
VALUES (75,45, 3008, 0, 240, 3.20, 768.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (76,45, 3009, 1, 444, 3.80, 1687.20, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (77,45, 3010, 2, 240, 4.10, 984.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
/* Order No.: 46 */
VALUES (78,46, 3011, 0, 40, 2.20, 88.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (79,46, 3012, 1, 80, 3.60, 288.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (80,46, 3002, 2, 90, 1.50, 135.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (81,46, 3010, 3, 100, 4.10, 410.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (82,46, 3011, 4, 400, 2.20, 880.00, 0);
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION)
VALUES (83,46, 3006, 5, 60.00, 0.45, 27.00, 0);



