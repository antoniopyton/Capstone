import { Utente } from "./utente.interface";

export interface Brani {
    id: number,
    titolo: string,
    fileUrl: string,
    genere: string,
    durata: number,
    copertina?: string,
    artista: Utente,
    ascolti: number
}
