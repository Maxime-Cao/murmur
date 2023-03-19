use std::string::String;
use regex::Regex;

#[derive(Clone)]
pub struct Protocol {
    // Définitions standards :
    rx_chiffre: String,
    rx_lettre: String,
    rx_lettre_chiffre: String,
    rx_caractere_imprimable: String,
    rx_crlf: String,
    rx_symbole: String,
    rx_esp: String,
    rx_domaine: String,
    rx_port: String,
    rx_round: String,
    rx_bcrypt_hash: String,
    rx_sha3_hex: String,
    rx_salt_size: String,
    rx_random22: String,
    rx_salt: String,
    rx_message: String,
    rx_message_interne: String,
    rx_nom_utilisateur: String,
    rx_tag: String,
    rx_nom_domaine: String,
    rx_tag_domaine: String,
    rx_id_domaine: String,

    // Echanges entre le client et le Murmur Server :
    rx_hello: String,
    rx_connect: String,
    rx_param: String,
    rx_confirm: String,
    rx_register: String,
    rx_follow: String,
    rx_msg: String,
    rx_msgs: String,
    rx_ok: String,
    rx_error: String,
    rx_disconnect: String,

    // Echanges multicast entre Murmur Server et Murmur Relay :
    rx_echo: String,

    // Echanges unicast entre Murmur Server et Murmur Relay :
    rx_send: String,

    hello: String,
    connect: String,
    param: String,
    confirm: String,
    register: String,
    follow: String,
    msg: String,
    msgs: String,
    ok: String,
    error: String,
    disconnect: String,
    echo: String,
    send: String,
}

impl Protocol {
    pub fn new() -> Self {
        let rx_chiffre = "[0-9]".to_string();
        let rx_lettre = "[a-zA-Z]".to_string();
        let rx_lettre_chiffre = format!("[{}{}]", rx_lettre, rx_chiffre).to_string();
        let rx_caractere_imprimable = "[\\x20-\\xff]".to_string();
        let rx_crlf = "\\x0d\\x0a".to_string();
        let rx_symbole = "[\\x21-\\x2f\\x3a-\\x40\\x5b-\\x60]".to_string();
        let rx_esp = "\\x20".to_string();
        let rx_domaine = format!("[{}{}]{{5,200}}", rx_lettre_chiffre, "\\x2e").to_string();
        let rx_port = format!("{}{{1,5}}", rx_chiffre).to_string();
        let rx_round = format!("{}{{2}}", rx_chiffre).to_string();
        let rx_bcrypt_hash = format!("\\$2b\\${}\\[{}{}\\]{{1,70}}", rx_round, rx_lettre_chiffre, rx_symbole).to_string();
        let rx_sha3_hex = format!("{}{{30,200}}", rx_lettre_chiffre).to_string();
        let rx_salt_size = format!("{}{{2}}", rx_chiffre).to_string();
        let rx_random22 = format!("[{}{}]{{22}}", rx_lettre_chiffre, rx_symbole).to_string();
        let rx_salt = rx_random22.clone();
        let rx_message = format!("{}{{1,200}}", rx_caractere_imprimable).to_string();
        let rx_message_interne = format!("{}{{1,500}}", rx_caractere_imprimable).to_string();
        let rx_nom_utilisateur = format!("{}{{5,20}}", rx_lettre_chiffre).to_string();
        let rx_tag = format!("#{}{{5,20}}", rx_lettre_chiffre).to_string();
        let rx_nom_domaine = format!("{}@{}", rx_nom_utilisateur, rx_domaine).to_string();
        let rx_tag_domaine = format!("{}@{}", rx_tag, rx_domaine).to_string();
        let rx_id_domaine = format!("{}{{1,5}}@{}", rx_chiffre, rx_domaine).to_string();

        let rx_hello = format!(r"(HELLO){}({}){}({})({}){{0,1}}", rx_esp, rx_domaine, rx_esp, rx_random22, rx_crlf).to_string();
        let rx_connect = format!("(CONNECT){}({}){}({})({}){{0,1}}", rx_esp, rx_nom_utilisateur, rx_esp, rx_sha3_hex, rx_crlf).to_string();
        let rx_param = format!(r"(PARAM){}({}){}({})({}){{0,1}}", rx_esp, rx_round, rx_esp, rx_salt, rx_crlf).to_string();
        let rx_confirm = format!(r"(CONFIRM){}({})({}){{0,1}}", rx_esp, rx_sha3_hex, rx_crlf).to_string();
        let rx_register = format!(r"(REGISTER){}({}){}({}){}({})({}){{0,1}}", rx_esp, rx_nom_utilisateur, rx_esp, rx_salt_size, rx_esp, rx_bcrypt_hash, rx_crlf).to_string();
        let rx_follow = format!(r"(FOLLOW){}({}|{})({}){{0,1}}", rx_esp, rx_nom_domaine, rx_tag_domaine, rx_crlf).to_string();
        let rx_msg = format!(r"(MSG){}({})({}){{0,1}}", rx_esp, rx_message, rx_crlf).to_string();
        let rx_msgs = format!(r"(MSGS){}({}){}({})({}){{0,1}}", rx_esp, rx_nom_domaine, rx_esp, rx_message, rx_crlf).to_string();
        let rx_ok = format!(r"(\\+OK){}({})({}){{0,1}}", rx_esp, rx_message, rx_crlf);
        let rx_error = format!(r"(-ERR){}({})({}){{0,1}}", rx_esp, rx_message, rx_crlf);
        let rx_disconnect = format!(r"(DISCONNECT)({}){{0,1}}", rx_crlf);
        let rx_echo= format!(r"(ECHO){}({}){}({})({}){{0,1}}", rx_esp, rx_port, rx_esp, rx_domaine, rx_crlf);
        let rx_send = format!("(SEND){}({}){}({}|{}){}({}|{}){}({})", rx_esp, rx_id_domaine, rx_esp, rx_nom_domaine, rx_tag_domaine, rx_esp, rx_nom_domaine, rx_tag_domaine, rx_esp, rx_message_interne).to_string();

        let hello = format!(r"HELLO <domaine> <random22>{}", rx_crlf).to_string();
        let connect = format!(r"CONNECT <nom-utilisateur>{}", rx_crlf).to_string();
        let param = format!(r"PARAM <round> <salt>{}", rx_crlf).to_string();
        let confirm = format!(r"CONFIRM <sha3>{}", rx_crlf).to_string();
        let register = format!(r"REGISTER <nom-utilisateur> <salt-size> <bcrypt-hash>{}", rx_crlf).to_string();
        let follow = format!(r"FOLLOW <nom-ou-tag-domaine>{}", rx_crlf).to_string();
        let msg = format!(r"MSG <message>{}", rx_crlf).to_string();
        let msgs = format!(r"MSGS <nom-domaine> <message>{}", rx_crlf).to_string();
        let ok = format!(r"+OK <message>{}", rx_crlf).to_string();
        let error = format!(r"-ERR <message>{}", rx_crlf).to_string();
        let disconnect = format!(r"DISCONNECT{}", rx_crlf).to_string();
        let echo = format!(r"ECHO <port> <domaine>{}", rx_crlf).to_string();
        let send = format!(r"SEND <id-domaine> <nom-domaine> <nom-ou-tag-domaine> <message-interne>").to_string();

        Self {
            rx_chiffre,
            rx_lettre,
            rx_lettre_chiffre,
            rx_caractere_imprimable,
            rx_crlf,
            rx_symbole,
            rx_esp,
            rx_domaine,
            rx_port,
            rx_round,
            rx_bcrypt_hash,
            rx_sha3_hex,
            rx_salt,
            rx_salt_size,
            rx_random22,
            rx_message,
            rx_message_interne,
            rx_nom_utilisateur,
            rx_tag,
            rx_nom_domaine,
            rx_tag_domaine,
            rx_id_domaine,
            rx_hello,
            rx_connect,
            rx_param,
            rx_confirm,
            rx_register,
            rx_follow,
            rx_msg,
            rx_msgs,
            rx_ok,
            rx_error,
            rx_disconnect,
            rx_echo,
            rx_send,
            hello,
            connect,
            param,
            confirm,
            register,
            follow,
            msg,
            msgs,
            ok,
            error,
            disconnect,
            echo,
            send,
        }
    }

    pub fn verify_message(&self, message: &str) -> Vec<String> {
        let mut result = Vec::new();
        match message.split(" ").next().unwrap().trim() {
            "HELLO" => result = self.verify_hello(message),
            "CONNECT" => result = self.verify_connect(message),
            "PARAM" => result = self.verify_param(message),
            "CONFIRM" => result = self.verify_confirm(message),
            "REGISTER" => result = self.verify_register(message),
            "FOLLOW" => result = self.verify_follow(message),
            "MSG" => result = self.verify_msg(message),
            "MSGS" => result = self.verify_msgs(message),
            "OK" => result = self.verify_ok(message),
            "ERROR" => result = self.verify_error(message),
            "DISCONNECT" => result = self.verify_disconnect(message),
            "ECHO" => result = self.verify_echo(message),
            "SEND" => result = self.verify_send(message),
            _ => {
                result.push("-ERR".to_string());
                result.push("This message is not correctly formatted".to_string());
            }
        }
        result
    }
    fn verify_hello(&self, message: &str) -> Vec<String> {
        let mut result = Vec::new();
        let regex = Regex::new(&self.rx_hello).unwrap();
        if let Some(captures) = regex.captures(message) {
            result.push(captures[1].to_string());
            result.push(captures[2].to_string());
            result.push(captures[3].to_string());
        } else {
            result.push("-ERR".to_string());
            result.push("This HELLO message is not correctly formatted".to_string());
        }
        result
    }

    fn verify_connect(&self, message: &str) -> Vec<String> {
        let mut result = Vec::new();
        let regex = Regex::new(&self.rx_connect).unwrap();
        if let Some(captures) = regex.captures(message) {
            result.push(captures[1].to_string());
            result.push(captures[2].to_string());
        } else {
            result.push("-ERR".to_string());
            result.push("This CONNECT message is not correctly formatted".to_string());
        }
        result
    }

    fn verify_param(&self, message: &str) -> Vec<String> {
        let mut result = Vec::new();
        let regex = Regex::new(&self.rx_param).unwrap();
        if let Some(captures) = regex.captures(message) {
            result.push(captures[1].to_string());
            result.push(captures[2].to_string());
            result.push(captures[3].to_string());
        } else {
            result.push("-ERR".to_string());
            result.push("This PARAM message is not correctly formatted".to_string());
        }
        result
    }

    fn verify_confirm(&self, message: &str) -> Vec<String> {
        let mut result = Vec::new();
        let regex = Regex::new(&self.rx_confirm).unwrap();
        if let Some(captures) = regex.captures(message) {
            result.push(captures[1].to_string());
            result.push(captures[2].to_string());
        } else {
            result.push("-ERR".to_string());
            result.push("This CONFIRM message is not correctly formatted".to_string());
        }
        result
    }

    fn verify_register(&self, message: &str) -> Vec<String> {
        let mut result = Vec::new();
        let regex = Regex::new(&self.rx_register).unwrap();
        if let Some(captures) = regex.captures(message) {
            result.push(captures[1].to_string());
            result.push(captures[2].to_string());
            result.push(captures[3].to_string());
            result.push(captures[4].to_string());
        } else {
            result.push("-ERR".to_string());
            result.push("This REGISTER message is not correctly formatted".to_string());
        }
        result
    }

    fn verify_follow(&self, message: &str) -> Vec<String> {
        let mut result = Vec::new();
        let regex = Regex::new(&self.rx_follow).unwrap();
        if let Some(captures) = regex.captures(message) {
            result.push(captures[1].to_string());
            result.push(captures[2].to_string());
        } else {
            result.push("-ERR".to_string());
            result.push("This FOLLOW message is not correctly formatted".to_string());
        }
        result
    }

    fn verify_msg(&self, message: &str) -> Vec<String> {
        let mut result = Vec::new();
        let regex = Regex::new(&self.rx_msg).unwrap();
        if let Some(captures) = regex.captures(message) {
            result.push(captures[1].to_string());
            result.push(captures[2].to_string());
        } else {
            result.push("-ERR".to_string());
            result.push("This MSG message is not correctly formatted".to_string());
        }
        result
    }

    fn verify_msgs(&self, message: &str) -> Vec<String> {
        let mut result = Vec::new();
        let regex = Regex::new(&self.rx_msgs).unwrap();
        if let Some(captures) = regex.captures(message) {
            result.push(captures[1].to_string());
            result.push(captures[2].to_string());
            result.push(captures[3].to_string());
        } else {
            result.push("-ERR".to_string());
            result.push("This MSGS message is not correctly formatted".to_string());
        }
        result
    }

    fn verify_ok(&self, message: &str) -> Vec<String> {
        let mut result = Vec::new();
        let regex = Regex::new(&self.rx_ok).unwrap();
        if let Some(captures) = regex.captures(message) {
            result.push(captures[1].to_string());
            result.push(captures[2].to_string());
        } else {
            result.push("-ERR".to_string());
            result.push("This OK message is not correctly formatted".to_string());
        }
        result
    }

    fn verify_error(&self, message: &str) -> Vec<String> {
        let mut result = Vec::new();
        let regex = Regex::new(&self.rx_error).unwrap();
        if let Some(captures) = regex.captures(message) {
            result.push(captures[1].to_string());
            result.push(captures[2].to_string());
        } else {
            result.push("-ERR".to_string());
            result.push("This ERROR message is not correctly formatted".to_string());
        }
        result
    }

    fn verify_disconnect(&self, message: &str) -> Vec<String> {
        let mut result = Vec::new();
        let regex = Regex::new(&self.rx_disconnect).unwrap();
        if let Some(captures) = regex.captures(message) {
            result.push(captures[1].to_string());
        } else {
            result.push("-ERR".to_string());
            result.push("This DISCONNECT message is not correctly formatted".to_string());
        }
        result
    }

    fn verify_echo(&self, message: &str) -> Vec<String> {
        let mut result = Vec::new();
        let regex = Regex::new(&self.rx_echo).unwrap();
        if let Some(captures) = regex.captures(message) {
            result.push(captures[1].to_string());
            result.push(captures[2].to_string());
            result.push(captures[3].to_string());
        } else {
            result.push("-ERR".to_string());
            result.push("This ECHO message is not correctly formatted".to_string());
        }
        result
    }

    fn verify_send(&self, message: &str) -> Vec<String> {
        let mut result = Vec::new();
        let regex = Regex::new(&self.rx_send).unwrap();
        if let Some(captures) = regex.captures(message) {
            result.push(captures[1].to_string());
            result.push(captures[2].to_string());
            result.push(captures[3].to_string());
            result.push(captures[4].to_string());
            result.push(captures[5].to_string());
        } else {
            result.push("-ERR".to_string());
            result.push("This SEND message is not correctly formatted".to_string());
        }
        result
    }
}
