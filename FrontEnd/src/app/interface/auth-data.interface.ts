import { Utente } from "./utente.interface";

export interface AuthData {
    accessToken: string;
    user: Utente;
}
