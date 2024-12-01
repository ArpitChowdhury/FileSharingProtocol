# PROTOCOLLO PER CONDIVIZIONE FILE

## Introduzione

Questo protocollo si basa sul modello client server e si basa sul protocollo UDP.
Il protocollo è compatible solo con la codifica Big-Endian per valori che occupano più di un byte, quindi trasmissione
inizia dal byte più significativo (
estremità più grande) per finire col meno significativo.
Il protocollo vengono utilizzati Datagram di 2KiB (2048 bytes)

## Sintassi

data[N] = X; nella posizione N bisogna inserire un dato M \
data[N;M] = X; dalla N-ennesima e gli successivi M ci sono i byte che descrivono il dato X \
data[N;] = X; dalla N-ennesima fino alla fine ci sono i byte che descrivono il dato X \

- Esempi:
    - data[0] = 1; il primo byte deve avere il valore di 1
    - data[1;4] = num; un numero composto da 4 byte
    - data[2;] = name; il resto dei bytes codificano il valore

## Comandi

### Client &rarr; Server

#### Request Download

Il client chiede di scaricare un file specificato \
data[0] = 1 \
data[1;4] = fileId - un int che indica il fileId

#### Request Upload

Il client chiede di caricare un file e attende che il server assegni al file un id \
data[0] = 3\
data[1;] = fileName - un byte array in codifica UTF-8 che indica il nome del file\

#### Uploading

Il client invia il server i dati del file \
data[0] = 4 \
data[1] = segmentId \
data[2;4] = fileId \
data[6;] = fileData



