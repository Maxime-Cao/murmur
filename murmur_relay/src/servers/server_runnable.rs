use std::{io::{Read, Write}, net::{SocketAddr, TcpStream}, sync::{Arc, Mutex}};
use crate::{relay::relay_manager::RelayManager, grammar::protocol::Protocol, utils::aes_codec::AESCodec};

#[derive(Clone)]
pub struct ServerRunnable{
    relay_manager : RelayManager,
    domain: String,
    base64_aes: String,
    protocol : Protocol,
    socket_addr : SocketAddr,
    message : Arc<Mutex<Vec<String>>>,
}

impl ServerRunnable{
    pub fn new(relay_manager : RelayManager,domain: String, base64_aes: String,protocol : Protocol, socket_addr: SocketAddr) -> ServerRunnable {
        ServerRunnable {
            relay_manager,
            domain,
            base64_aes,
            protocol,
            socket_addr,
            message : Arc::new(Mutex::new(Vec::new())),
        }
    }
    pub fn start(&self){
        let mut buffer = [0; 1024];
        println!("[*] ServerRunnable run : {}", self.socket_addr);
        let mut stream = TcpStream::connect(self.socket_addr).unwrap();
        stream = stream.try_clone().unwrap();
        stream.set_nonblocking(true).unwrap();
        loop {
            match stream.read(&mut buffer) {
                Ok(size) => {
                    let message_received = String::from_utf8_lossy(&buffer[..size]);
                    if size > 0 {
                        let message_received = message_received.trim_end_matches("\r ").trim_end_matches("\n").trim_end_matches("\r\n");
                        let message_handle = message_received.to_string().as_str().to_string();
                        self.handle_message(&message_handle);
                    }
                }
                Err(ref e) if e.kind() == std::io::ErrorKind::WouldBlock => {
                    //println!("Waiting for multicast message: {}", &self.base64_aes);
                }
                Err(e) => {
                    println!("[!] Error receiving multicast message: {}", e);
                    self.relay_manager.remove_server(self.domain.clone());
                }
            }
            
            let mut message = self.message.lock().unwrap();
            if message.len() > 0 {
                let message_to_send = message.remove(0);
                let aes_codec = AESCodec::new();
                let message_encrypt = aes_codec.encrypt(&self.base64_aes, &message_to_send).unwrap();
                let message_encrypt = format!("{}\r\n", message_encrypt);
                let message_ecrypt = message_encrypt.as_bytes();
                println!("[*] Message to send : {}", message_encrypt);
                let writerResult = stream.write(message_ecrypt).unwrap();
                stream.flush().unwrap();
                if writerResult == 0 {
                    println!("[!] Error sending message");
                } else if writerResult != message_encrypt.len() {
                    println!("[!] Error sending message");
                } else {
                    println!("[+] Message sent");
                }
            }
        }
    }
    fn handle_message(&self, message_received: &str) {
        //Le message_received peut surement avoir un caractère de fin de ligne (comme \r ou \n ou les deux). Il faut donc le supprimer
    
        let aes_codec = AESCodec::new();
        let message = aes_codec.decrypt(&self.base64_aes, message_received.as_bytes().to_vec()).unwrap();
        println!("[*] Message received : {}", message);
        let check_message = self.protocol.verify_message(&message);
        if check_message[0] == "SEND" {
            let nom_tag_domaine = &check_message[3];
            let mut nom_tag_domaine_split = nom_tag_domaine.split("@");
            let domaine_to_send = nom_tag_domaine_split.nth(1).unwrap();
            self.relay_manager.send_message(domaine_to_send.to_string(), message.to_string());
        }
    }
    pub fn add_message(&self, new_message : String){
        let mut message = self.message.lock().unwrap();
        message.push(new_message);
    }
}