# Sistema informatico per la gestione dei crediti informativi (ECM)

Il Sistema per l'Educazione Continua in Medicina (ECM) è disciplinato dall'Accordo Stato-Regioni del 19 Aprile 2012, “Nuovo sistema di Formazione Continua in Medicina”, (rep. 101/CSR).
Il suo obiettivo è quello di monitorare e controllare l'aggiornamento professionale degli operatori della sanità; viene diretto e coordinato a livello regionale dai Centri Regionali di Riferimento per l'ECM; integrati a loro volta con il Sistema ECM Nazionale, che si occupa di fornire linee guida e indirizzi comuni.
Il software ideato dalla 3D Informatica è in grado di fornire tutte le funzionalità necessarie per la gestione delle attività complesse svolte dagli attori che interagiscono con il sistema, ovvero la Segreteria regionale ECM, i Provider, i Professionisti, gli Ordini, i Collegi e le Associazioni Professionali.
___

##### Specifiche tecnologiche

La soluzione proposta garantisce flessibilità e rapidità di evoluzione in funzione delle esigenze espresse dalla Regione Veneto per la gestione dei processi di accreditamento ECM, adattandosi facilmente a eventuali cambiamenti tecnologici o di processo.
L’utilizzo di JPA permette al sistema di adattarsi ai cambiamenti tecnologici, disaccoppiando il software dal database scelto, permettendo in futuro di passare senza sviluppi onerosi a un RDBMS differente rispetto a quello attualmente proposto (PostgreSQL).
La scelta di Bonita come BPM conferisce flessibilità alla parte di gestione dei workflow, consentendo tramite il disegno dei flussi di adattare la soluzione applicativa alle modifiche dei processi. La scelta di eXtraWay e dell’XML consente al software invece la massima flessibilità nella gestione delle informazioni e la massima adattabilità al cambiamento di quanto registrato nel sistema.

##### Integrazioni

* Integrazione con il sistema MyPay per la gestione dei pagamenti;
* Protocollo regionale Lapis Web per l’invio e la ricezione di documentazione protocollata
* Integrazione con la Firma Digitale;
* Anagrafe Nazionale del Co.Ge.A.P.S per la trasmissione del report delle partecipazioni tramite tracciato XML;
* Sistema Age.Na.S. per l’importazione dei dati pregressi sui Provider accreditati e gli eventi formativi.
* Integrazione con il Portale SISSR.

##### Vantaggi

Il sistema permette di gestire in maniera integrata tutte le attività svolte dalla Segreteria ECM, offrendo un unico punto di accesso ai procedimenti e ai documenti gestiti. Il documentale, posto alla base del sistema, fornisce un bacino informativo completo e dettagliato su tutte le attività svolte, garantendo un rapido recupero delle informazioni disponibili sui Provider, sugli eventi organizzati e sui percorsi formativi dei singoli Professionisti.
L'applicativo è estremamente flessibile; ogni modifica effettuata sui procedimenti (per esempio a causa di eventuali adeguamenti normativi) potrà essere adattata in maniera estremamente semplice nel software già esistente, ridisegnando velocemente i flussi configurati.
Il sistema offre un valido supporto per valutare la qualità complessiva della formazione offerta, e tiene traccia di ogni comunicazione e attività intercorsa fra i Provider, la Segreteria e i Professionisti Sanitari; mappando in modo certo i procedimenti e rendendo estremamente efficiente ed efficace la loro gestione.

##### Configurazione

Tutte le configurazioni si trovano all'interno del file _application.properties_

```properties_
# JPA
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.default_schema=ecmdb

# gestione ottimizzata delle connessioni al DB con il driver JDBC
spring.datasource.test-on-borrow=true
spring.datasource.validation-query=select 1

#DEFAULT
#spring.jpa.hibernate.naming-strategy=org.springframework.boot.orm.jpa.hibernate.SpringNamingStrategy

#CASE SENSITIVE però la conversione a basso livelo su postgres sbaglia select account."changePassword" -> account.changePassword
#spring.jpa.hibernate.naming-strategy=org.hibernate.cfg.DefaultNamingStrategy

# SPRING INTERNATIONALIZATION
spring.messages.basename=i18n/messages

# SPRING PROFILES
spring.profiles.active=demo

# Liquibase dati usati durante l'avvio della webapp per aggiornare il db
liquibase.change-log=classpath:liquibase/master-changeLog.xml
liquibase.default-schema=ecmdb

multipart.maxFileSize= 10MB
multipart.maxRequestSize = 25MB

# LOGGING
#logging.file=/var/log/ecm/ecm.log

############### ECM PROPERTIES ###############
ecm.version=3.0.7-SNAPSHOT

#validita' in giorni della password
account.expires.days=365

# File size limit (Mb)
file.multipart.maxFileSize=2
file.multipart.maxFileSize4MB=4

#limite in minuti prima dello scadere entro cui � modificabile la seduta
seduta.validation.minutes=30

#email segreteria ECM
email.segreteriaEcm=formazione.sviluppo@azero.veneto.it

#debugTestMode default=false se attivata permette di visualizzare una serie di comandi per agevolare la fase di test (#esempio...mette si a tutte le valutazioni....)
debugTestMode=false
#debugSaltaProtocollo defalut=false se attiva usa un meccanismo interno per generare numero e data di protocollo
debugSaltaProtocollo=false
#debugBrokeProtocollo defalut=false se attiva manda in errore le protocollazioni in uscita...utile per testare il meccanismo di annullamento/riesecuzione del protocollo
debugBrokeProtocollo=false
#debugSaltaScheduledTasks defalut=false se attiva mette in pausa i task
debugSaltaScheduledTasks=false

###################### CONFIGURAZIONE DELLA DATA PER LA QUOTA ANNUALE #####################

#Il giorno in cui inizia il calcolo della Quota Annuale
pagamento.giornoInizioCalcolaQuotaAnnuale=01
#Il messe in cui inizia il calcolo della Quota Annuale
pagamento.messeInizioCalcolaQuotaAnnuale=04
#Il giorno in cui finisce il calcolo della Quota Annuale
pagamento.giornoFinisceCalcolaQuotaAnnuale=31
#Il messe in cui finisce il calcolo della Quota Annuale
pagamento.messeFinisceCalcolaQuotaAnnuale=12

##########################################################################################

#giorni minimi in cui il provider può effettuare l'integrazione (utilizzato per popolare la select)
giorni.integrazione.min=5

#giorni massimi in cui il provider può effettuare l'integrazione (utilizzato per popolare la select)
giorni.integrazione.max=30

#numero referee da selezionare
numero.referee=3

#numero giorni minimi necessari per poter inserire evento (provider A)
giorni.min.evento.provider.A=15

#numero giorni minimi necessari per poter inserire evento (provider B)
giorni.min.evento.provider.B=30

#numero giorni minimi necessari per poter inserire evento riezione
giorni.min.evento.riedizione=10

#numero giorni prima dell'inizio di un evento fino a quando è possibile posticipare l'eento (provider A)
giorni.possibilita.posticipo.da.inizio.evento.provider.A=4

#numero giorni prima dell'inizio di un evento fino a quando è possibile posticipare l'eento (provider b)
giorni.possibilita.posticipo.da.inizio.evento.provider.B=10

#numero responsabili scientifici evento massimi
numero.massimo.responsabili.evento=3

#numero esperti evento massimi
numero.massimo.esperti.evento=3

#numero coordinatori evento massimi
numero.massimo.coordinatori.evento=3

#giorni massimi durata evento FSC
giorni.max.evento.fsc=730

#giorni massimi durata evento FSC con Tipologia evento diversa da Attività di ricerca
#ATTENZIONE quando si cambia questa property occorre cambiare anche il testo dell'errore error.numero_massimo_giorni_evento_fsc_non_attivita_di_ricerca
giorni.max.evento.fsc.versione2=365

#giorni massimi durata evento FSC con Tipologia evento uguale ad Attività di ricerca
#ATTENZIONE quando si cambia questa property occorre cambiare anche il testo dell'errore error.numero_massimo_giorni_evento_fsc_attivita_di_ricerca
giorni.max.evento.fsc.versione2.attivitaDiRicerca=730

#giorni massimi durata evento FAD
giorni.max.evento.fad=365

#data fine massima per evento fad da mettere in formato yyyyMMdd
#ATTENZIONE quando si cambia questa property occorre cambiare anche il testo dell'errore error.fad_data_fine_non_appartenente_triennio_attuale
evento.fad.data.fine.max.triennio=20191231

#data fine massima per evento fad da mettere in formato yyyyMMdd
#ATTENZIONE quando si cambia questa property occorre cambiare anche il testo dell'errore error.fsc_data_fine_non_appartenente_triennio_attuale
evento.fsc.data.fine.max.triennio=20191231

#numero minimo dei partecipanti all'evento convegno congresso RES
numero.minimo.partecipanti.convegno.congresso.res=200

#numero massimo dei partecipanti all'evento workshop seminario RES
numero.massimo.partecipanti.workshop.seminario.res=100

#numero massimo dei partecipanti all'evento corso di aggiornamento RES
numero.massimo.partecipanti.corso.aggiornamento.res=200

#numero massimo dei partecipanti all'evento gruppi di miglioramento FSC
numero.massimo.partecipanti.gruppi.miglioramento.fsc=25

#numero massimo dei partecipanti all'evento audit clinico FSC
numero.massimo.partecipanti.audit.clinico.fsc=25

#durata minima richiesta per evento RES
durata.minima.evento.res=3

#numero di versione degli eventi che viene utilizzata come default nel caso non sia stato possibile valutarla
evento.numeroversione.default=3

#numeri di versioni degli eventi dei quali � possibile effettuare la riedizione, è possibile mettere pi� versioni separandole con la virgola (es. 1,2)
evento.versioni.rieditabili=3

#data inizio dalla quale un evento viene considerato di versione 2 da mettere in formato yyyyMMdd
evento.data.passaggio.versione.due=20180401
evento.data.passaggio.versione.tre=20190321

#la lista eventi di default sia provider che segreteria, viene filtrata per data modifica maggiore della data ottenuta sottraendo alla data attuale i giorni configurati
evento.lista.default.giorni.ultima.modifica=365

#durata minima richiesta per evento FSC di tipo audit clinico
durata.minima.audit.clinico.fsc=10

#durata minima richiesta per evento FSC di tipo gruppi di miglioramento
durata.minima.gruppi.miglioramento.fsc=8

#durata minima richiesta per evento FSC di tipo progetti di miglioramento
durata.minima.progetti.miglioramento.fsc=8

#giorni prima del blocco edit della riedizione Evento
giorni.prima.blocco.edit.riedizione=4

#giorni prima del blocco edit di Eventi del provider di gruppo A
giorni.prima.blocco.edit.gruppoA=4

#giorni prima del blocco edit di Eventi del provider di gruppo B
giorni.prima.blocco.edit.gruppoB=10

#ERM14774
#abilta / disabilita l'invio automatico di email come promemoria delle scadenza
task.sendAlertEmail=false

#limite di valutazioni non date per cui un referee è selezionabile per una valutazione
valutazioni.non.date.limite=3

#giorni che il provider ha per poter modificare i dati e i documenti della domanda di accreditamento
giorni.variazione.dati.accreditamento=15

#numero massimo di bean in sessione contemporaneamente
num.conversation.toKeep=10

#booleans to change behaviour of gestione timer in Accreditamento
conteggioGiorniAvanzatoAbilitato=true
conteggioGiorniAvanzatoBeforeDayMode=false

########################################################

############### DATE E SCADENZE VARIE ###############
#pianoFormativo.dataFineModifica -> 15 Dicembre
pianoFormativo.giornoFineModifica=15
pianoFormativo.meseFineModifica=12

#relazioneAnnuale.dataFineModifica -> 31 Marzo
relazioneAnnuale.giornoFineModifica=31
relazioneAnnuale.meseFineModifica=3
#ERM012514
relazioneAnnuale.giornoPeriodoNuovo=30
relazioneAnnuale.mesePeriodoNuovo=6

#ERM014776
accreditamento.numeroGiorniDopoChiusura=7
###### Workflow ######
bonita.bonitaviewserverurl=http://127.0.0.1:9080/bonitaview
bonita.serverurl=http://127.0.0.1:9080
bonita.applicationname=bonita
bonita.admin.username=admin
bonita.admin.password=bonita_pwd
bonita.system.username=system
bonita.users.password=system_pwd
bonita.createaccountonlogin=true
########################################################


########## Co.Ge.A.P.S. #########
cogeaps.protocol=http
cogeaps.host=application2.cogeaps.it
cogeaps.port=8090
cogeaps.rest_service.carica=/app/api/evento/carica
cogeaps.rest_service.stato_elaborazione=/app/api/evento/statoelaborazione
cogeaps.username=ente
cogeaps.password=ente_pwd
cogeaps.proxy.attivo=false

##############################################

############### THREAD PROPERTIES ############

thread.corePoolSize=6
thread.maxPoolSize=10
thread.queueCapacity=25
thread.threadNamePrefix=ecm-scheduled-tasks-thread-
thread.waitForTasksToCompleteOnShutdown=true

##############################################

############### ENGINEERING PROPERTIES ###############

######### MY PAY #########
#Parametri per connettersi al servizio (diverso tra test e produzione)
ipa=user
password=user_pwd
#Id servizio assegnato da MyPay (IUD)
servizio=009
#Endpoint (diverso tra test e produzione)
endpoint.pagamenti=https://paygov.collaudo.regione.veneto.it/pa/services/PagamentiTelematiciDovutiPagati
#Specifico in base a cosa pago - da concordare con RVE e Mola
datispecifici.riscossione=9/1234567890
#Da aggiornare con il dato definitivo (da concordare con Regione)
tipo.dovuto.evento=ECM_SINGOLO_EVENTO
tipo.dovuto.quotaannua=ECM_QUOTA_ANNUA
proxy.attivo=false
proxy.host
proxy.port
#PER ALCUNI PROXY POTREBBE ANCHE NON ESSERE VALORIZZATO. IN EFFETTO DOVREBBE ESSERE COSI' PER QUELLO DI REGIONE
proxy.username
proxy.password
causale.length=140
versione=6.0

######### FIRMA DIGITALE #########
firma.url=https://servizi.collaudo.regione.veneto.it/FirmaWeb/servlet/AdapterHTTP
firma.idclassificazione=60.00.05.00.00-C.120.21.1.B4
firma.referer=http://ecmtest.demo.3di.it/ecm/*

######### PROTOCOLLO  #########
protocollo.service.versione=rv

######### PROTOCOLLO RV #########
protocollo.codApplicativo=S051.001
protocollo.operatore.entrata=tania-ferrotti
protocollo.codStruttura=69.02.03.00.00
protocollo.idc=C.120.23.1
protocollo.endpoint=http://endpoint.fqdn/LapisWebSOAP/LapisWebSOAP.asp

######### PROTOCOLLO WEB RAINBOW #########
protocollo.webrainbow.endpoint=http://endpoint.fqdn/WebRainbow/Protocol?wsdl
protocollo.webrainbow.ufficioCreatore.entrata=UOC Formazione e Sviluppo delle Professioni Sanitarie
protocollo.webrainbow.ufficioCreatore.uscita=UOC Formazione e Sviluppo delle Professioni Sanitarie

######### CAS #########
app.service.home=https://localhost:8443/
app.service.security=https://localhost:8443/j_spring_cas_security_check
cas.url.prefix=https://cas-test.regione.veneto.it/cas
cas.service.login=https://cas-test.regione.veneto.it/cas/login
#cas.service.login=https://cas.3di.it/cas/login
cas.service.logout=https://cas-test.regione.veneto.it/cas/logout
cas.service.validation=https://cas-test.regione.veneto.it/cas

######### JAVERS #########
javers.packagesToScan=it.tredi.ecm.audit.entity,it.tredi.ecm.dao.entity

######### PROXY #########
http.proxy.protocol=http
http.proxy.host=127.0.0.1
http.proxy.port=3128
http.proxy.authenticated=false
http.proxy.username=
http.proxy.password=

##############################################

# la seguente properties risolverebbe tutti gli errori di lazy initialization exception
# tuttavia ogni volta che deve accedere alla risorsa non caricata a sessione chiusa
# deve aprire una nuova connessione come nel pattern "Open Session in View"
# per approfondire:https://hibernate.atlassian.net/browse/HHH-7457?page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel&showAll=true
#spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true

##############################################e

```
#### Status del progetto

- stabile

#### Limitazioni sull'utilizzo del progetto:

Il presente modulo della piattaforma documentale è stato realizzato facendo uso di PostgreSQL e Bonita BPM 7.x.

___
#### Detentore del copyright:
Regione del Veneto
___
#### Soggetto incaricato del mantenimento del progetto open source:
| 3D Informatica srl |
| :------------------- |
| Via Speranza, 35 - 40068 S. Lazzaro di Savena |
| Tel. 051.450844 - Fax 051.451942 |
| http://www.3di.it |

___
#### Indirizzo e-mail a cui inviare segnalazioni di sicurezza:
tickets@3di.it
