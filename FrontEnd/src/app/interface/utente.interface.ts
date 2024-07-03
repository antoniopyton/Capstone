import { Brani } from "./brani.interface";
import { Eventi } from "./eventi.interface";

export interface Utente {
    id: number,
    nome: string,
    cognome: string,
    email: string,
    password: string,
    tipoUtente: string,
    avatar?: string,
    descrizioneArtista?: string,
    sfondoArtista?: string,
    tipoArtista: string,
    nomeArtista: string,
    brani?: Brani[],
    eventi?: Eventi[]
}
