-- aggregates statistikk tabeller fra datavarehus

-- sykefravær
-- obs: det er to rader per linje med stat (= arstall/kvartal/orgnr -sammen med sektor og næring- /alder/kjønn/fylkbo)
--  rad #1 har taptedv, mens muligedev og antpers er null
--  rad #2 har muligedev og antpers, mens taptedv er null (sftype er 'x' og varighet er 'x')

insert into dt_p.agg_ia_sykefravar_v
(arstall, kvartal,
 orgnr, naering_kode, sektor, storrelse, fylkarb,
 alder, kjonn, fylkbo,
 sftype, varighet,
 taptedv, muligedv, antpers, rectype)
values (2019, 1, 987654321, 88911, 3, 'g', '03', 'b', 'k', '02', 'x', 'x', 300, 1000, 6, 'X'),
       (2019, 2, 987654321, 88911, 3, 'g', '03', 'b', 'k', '02', 'x', 'x', 300, 1000, 6, 'X'),
       (2019, 1, 987654320, 88911, 3, 'g', '03', 'b', 'k', '02', 'x', 'x', 500, 2000, 6, 'X'),
       (2019, 2, 987654320, 88911, 3, 'g', '03', 'b', 'k', '02', 'l', 'a', 500, 2000, 5, 'X');

insert into dt_p.agg_ia_sykefravar_naring_kode
(arstall, kvartal, naering_kode, alder, kjonn, taptedv, muligedv, antpers)
values (2019, 1, 88911, 'x', 'x', 300, 1000, 5),
       (2019, 2, 88911, 'x', 'x', 500, 2000, 5),
       (2018, 4, 88911, 'x', 'x', 2.0, 100.0, 5),
       (2019, 1, 88911, 'x', 'x', 6.0, 100.0, 5),
       (2018, 4, 88911, 'x', 'x', 2.0, 100.0, 5),
       (2019, 1, 88911, 'x', 'x', 6.0, 100.0, 5),
       (2019, 2, 88911, 'x', 'x', 2.0, 100.0, 5),
       (2018, 4, 88911, 'x', 'x', 6.0, 100.0, 5),
       (2019, 1, 88911, 'x', 'x', 4.0, 100.0, 5)
