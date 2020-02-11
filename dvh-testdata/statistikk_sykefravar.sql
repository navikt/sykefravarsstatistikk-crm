-- aggregates statistikk tabeller fra datavarehus

-- sykefravær
-- obs: det er to rader per linje med stat (= arstall/kvartal/orgnr -sammen med sektor og næring- /alder/kjønn/fylkbo)
--  rad #1 har taptedv, mens muligedev og antpers er null
--  rad #2 har muligedev og antpers, mens taptedv er null (sftype er 'x' og varighet er 'x')


insert into dt_p.v_agg_ia_sykefravar
(arstall, kvartal,
 orgnr, naring, sektor, storrelse, fylkarb,
 alder, kjonn, fylkbo,
 sftype, varighet,
 taptedv, muligedv, antpers)
values ('2019', '1',
        '987654321', '88911', '3', 'g', '03',
        'b', 'k', '02',
        'x', 'x',
        13, 386.123, 6),
       ('2019', '2',
        '987654321', '88911', '3', 'g', '03',
        'b', 'k', '02',
        'l', 'a',
        10, 123.123, 0);


insert into dt_p.v_agg_ia_sykefravar
(arstall, kvartal,
 orgnr, naring, sektor, storrelse, fylkarb,
 alder, kjonn, fylkbo,
 sftype, varighet,
 taptedv, muligedv, antpers)
values ('2019', '1',
        '987654320', '88911', '3', 'g', '03',
        'b', 'k', '02',
        'x', 'x',
        13, 23.123, 6),
       ('2019', '2',
        '987654320', '88911', '3', 'g', '03',
        'b', 'k', '02',
        'l', 'a',
        10, 56.123, 0);


insert into dt_p.agg_ia_sykefravar_naring_kode
(arstall, kvartal, naering_kode, alder, kjonn, taptedv, muligedv, antpers)
values (2019, 1, 88911, 'x', 'x', 4.426667, 87.3135, 234),
       (2019, 2, 88911, 'x', 'x', 5.426667, 90.3135, 223),
       (2018, 4, 88911, 'x', 'x', 6.426667, 60.3135, 1),
       (2019, 1, 88911, 'x', 'x', 7.426667, 23.3135, 31123),
       (2018, 4, 88911, 'x', 'x', 8.426667, 3.3135, 123),
       (2019, 1, 88911, 'x', 'x', 9.426667, 82.3135, 3),
       (2019, 2, 88911, 'x', 'x', 10.426667, 84.3135, 342),
       (2018, 4, 88911, 'x', 'x', 12.426667, 87.3135, 34),
       (2019, 1, 88911, 'x', 'x', 12.426667, 85.3135, 4)
