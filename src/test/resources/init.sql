-- Tabeller fra datavarehus
create schema dt_p;

create table dt_p.v_dim_ia_sektor
(
    sektorkode char(1),
    sektornavn varchar(60)
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

create table dt_p.agg_ia_sykefravar_v
(
    arstall      char(4)    not null,
    kvartal      char(1)    not null,
    orgnr        char(9)    not null,
    naering_kode char(5)    not null,
    alder        char(1)    not null,
    kjonn        char(1)    not null,
    fylkbo       varchar(2) not null,
    sftype       char(1)    not null,
    varighet     char(1)    not null,
    sektor       char(1)    not null,
    storrelse    char(1)    not null,
    fylkarb      varchar(2) not null,
    taptedv      decimal(14, 6),
    muligedv     decimal(15, 6),
    antpers      decimal(7, 0),
    rectype      char(1)
);