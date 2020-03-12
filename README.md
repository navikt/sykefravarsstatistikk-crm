# Sykefravarsstatistikk for CRM
Script for å importere sykefraværsstatistikk fra datavarehus. Ref: https://github.com/navikt/sykefravarsstatistikk-api

## Datavarehus
Vi baserer oss på følgende spørring fra Datavarehuset for å skape en form for "Lead Scoring".

```
select orgnr                                    as orgnr,
       naering_kode                             as naring,
       count(distinct concat(arstall, kvartal)) as antall_kvartaler,
       sum(taptedv)                             as sum_tapte_dagsverk,
       sum(muligedv)                            as sum_mulige_dagsverk
from dt_p.agg_ia_sykefravar_v
where concat(arstall, kvartal) >
      ((select max(concat(arstall, kvartal)) from dt_p.agg_ia_sykefravar_v) - 30)
group by orgnr, naring
```

## Daglig jobb
1. Sjekker om det finnes en fil fra forrige kjøring på S3, hvis ikke oppretter en tom fil.
2. Leser fil fra S3 inn i minnet for sammenligning med dagens data.
3. Kjøre spørring mot DVH for alt og sammenligner rad for rad mot versjonen fra S3 som ligger i minnet
5. Kjøre spørring mot SF etter utdaterte records. Nullstille de utdaterte radene.
6. Skriver endringer til fil i S3 slik at denne er klar for morgendagen

## Gradle
Kjøre `formatKotlin` før commit for å slippe at lintern klager. Andre gradle kommandoer:
```
./gradlew formatKotlin
./gradlew clean build
./gradlew test --info
```

## Mottak i Salesforce
https://github.com/navikt/crm-platform-integration/blob/master/force-app/main/default/classes/KafkaMessageHandler.cls

## Todo

