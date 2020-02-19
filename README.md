# sykefravarsstatistikk-crm
Script for å importere sykefraværsstatistikk fra datavarehus. Ref: https://github.com/navikt/sykefravarsstatistikk-api

```
SELECT orgnr,
       max(CONCAT(ARSTALL, KVARTAL)) as siste_kvartal,
       sum(antpers) as sum_antall_personer,
       sum(taptedv)  as sum_tapte_dagsverk,
       sum(muligedv) as sum_mulige_dagsverk
FROM V_AGG_IA_SYKEFRAVAR
WHERE CONCAT(ARSTALL, KVARTAL) > '20181'
group by orgnr;

```

Alternativt legge inn ett tall som presenterer potensielle dagsverk.
Index isteden for sykefravær som tar med seg bransjetall.
Potensielt Sparte Dagsverk?

## Prosess  
1. Leser fil fra s3 inn i minnet
2. Kjøre spørring mot DVH for alt og sammenligner rad for rad mot versjonen i minne.
3. Skriver endringer til fil i s3
5. Kjøre spørring mot SF etter utdaterte records. Nullstille de utdaterte radene.


Kjøre før commit for å slippe at lintern klager.
```
./gradlew formatKotlin
./gradlew clean build
```

### Mottak i Salesforce
https://github.com/navikt/crm-platform-integration/blob/master/force-app/main/default/classes/KafkaMessageHandler.cls

