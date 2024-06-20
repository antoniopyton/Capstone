export interface Utente {
    id: number,
    nome: string,
    cognome: string,
    email: string,
    password: string,
    tipoUtente: string,
    username: string,
    avatar?: string,
    descrizioneArtista?: string,
    sfondoArtista?: string
}
