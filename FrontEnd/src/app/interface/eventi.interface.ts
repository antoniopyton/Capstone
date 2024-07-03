import { Utente } from "./utente.interface";

export interface Eventi {
    id: number;
    nome: string;
    descrizione: string;
    data: string; 
    dataInserimento: string;
    luogo: string;
    immagine: string;
    capienzaMax: number;
    postiDisponibili: number;
    utentiPrenotati: Utente[];
    organizzatore: Utente;
    artistiCandidati: Utente[];
    statoBiglietti: string;
}
