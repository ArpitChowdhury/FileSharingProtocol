# PROTOCOLLO PER CONDIVIZIONE FILE

## Introduzione

Questo protocollo si basa sul modello client server e si basa sul protocollo UDP.
Il protocollo è compatible solo con file di testo in codifica UTF-8.
Il protocollo vengono utilizzati Datagram di 1KiB (1024 bytes).
Il protocollo durante scarico e carico dei file viene segmentato (max segmenti 255)

## Sintassi

data[N] = X; nella posizione N bisogna inserire un dato M \
data[N;M] = X; dalla N-ennesima e gli successivi M ci sono i byte che descrivono il dato X \
data[N;] = X; dalla N-ennesima fino alla fine ci sono i byte che descrivono il dato X \

- Esempi:
    - data[0] = 1; il primo byte deve avere il valore di 1
    - data[1;4] = num; un numero composto da 4 byte
    - data[2;] = name; il resto dei bytes codificano il valore name

## Comandi

### Client &rarr; Server

#### Request Download

Il client chiede di scaricare un file specificato \
data[0] = 1 \
data[1;4] = fileId : un int che indica il fileId

#### Reload Download List

Il client chiede un aggiornamento dei file disponibili al download
data[0] = 2

#### Request Upload

Il client chiede di caricare un file e attende che il server assegni al file un id \
data[0] = 3\
data[1;] = fileName : un byte array in codifica UTF:8 che indica il nome del file\

#### Uploading

Il client invia il server i dati del file. La fine viene segnalato del end byte\
data[0] = 4 \
data[1] = segmentId \
data[2] = end \
data[2;4] = fileId \
data[6;] = fileData

### Server &rarr; Client

#### Response Upload

Il server assegna al file in caricamento un fileID e richiede il primo segmento \
data[0] = 3\
data[1] = 0 - segment iniziale \
data[2;4] = fileId - il server attribuisce al file identificativo unico
data[6;] = fileName - il nome del server

#### Ack Uploading

Il server continua a richiedere i segmenti sucessivi

data[0] = 3\
data[1] = segmentId + 1 : segment successive \
data[2;4] = fileId : il server attribuisce al file identificativo unico
data[6;] = fileName : il nome del server

#### Ack Uploading: Error segmentId byte overflow

Questo errore si accade quanto il byte supera il valore 255
data[0] = 3 \
data[1] = -2 \
data[2;4] = fileId \
data[6;] fileName \

