
insert into USERS (LOGIN, PASSWORD, IS_ENCODED) values ('root', 'XohImNooBHFR0OVvjcYpJ3NgPQ1qq73WKhHvch0VQtg=', 1);

insert into USER_ROLES(ROLE, USER_ID) values('ADMIN', (select ID from USERS where LOGIN='root'));
