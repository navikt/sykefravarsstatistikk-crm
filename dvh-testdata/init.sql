-- Tabeller fra datavarehus
create schema dt_p;

create table dt_p.v_dim_ia_sektor
(
    sektorkode char(1),
    sektornavn varchar(60)
);

create table dt_p.v_dim_ia_naring_sn2007
(
    naringkode char(2)      not null,
    nargrpkode char(2)      not null,
    naringnavn varchar(100) not null,
    constraint r1_naring_sn2007_pk primary key (naringkode)
);

create table dt_p.v_agg_ia_sykefravar_land
(
    arstall      char(4) not null,
    kvartal      char(1) not null,
    naring       char(2) not null,
    naringnavn   varchar(60),
    alder        char(1) not null,
    kjonn        char(1) not null,
    fylkbo       char(2) not null,
    fylknavn     varchar(35),
    varighet     char(1) not null,
    sektor       char(1) not null,
    sektornavn   varchar(60),
    taptedv      decimal(14, 6),
    muligedv     decimal(15, 6),
    antpers      decimal(7, 0),
    ia1_taptedv  decimal(14, 6),
    ia1_muligedv decimal(15, 6),
    ia1_antpers  decimal(7, 0),
    ia2_taptedv  decimal(14, 6),
    ia2_muligedv decimal(15, 6),
    ia2_antpers  decimal(7, 0),
    ia3_taptedv  decimal(14, 6),
    ia3_muligedv decimal(15, 6),
    ia3_antpers  decimal(7, 0)
);

create table dt_p.v_agg_ia_sykefravar_naring
(
    arstall  char(4) not null,
    kvartal  char(1) not null,
    naring   char(2) not null,
    alder    char(1) not null,
    kjonn    char(1) not null,
    taptedv  decimal(14, 6),
    muligedv decimal(15, 6),
    antpers  decimal(7, 0)
);

create table dt_p.v_agg_ia_sykefravar
(
    arstall   char(4)    not null,
    kvartal   char(1)    not null,
    orgnr     char(9)    not null,
    naring    char(5)    not null,
    alder     char(1)    not null,
    kjonn     char(1)    not null,
    fylkbo    varchar(2) not null,
    sftype    char(1)    not null,
    varighet  char(1)    not null,
    sektor    char(1)    not null,
    storrelse char(1)    not null,
    fylkarb   varchar(2) not null,
    taptedv   decimal(14, 6),
    muligedv  decimal(15, 6),
    antpers   decimal(7, 0)
);

create table dt_p.agg_ia_sykefravar_naring_kode
(
    arstall      char(4) not null,
    kvartal      char(1) not null,
    naering_kode char(5) not null,
    alder        char(1) not null,
    kjonn        char(1) not null,
    taptedv      decimal(14, 6),
    muligedv     decimal(15, 6),
    antpers      decimal(7, 0)
);