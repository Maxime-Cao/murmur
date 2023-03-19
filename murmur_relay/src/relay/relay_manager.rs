use crate::domains::relay::Relay;
use crate::grammar::protocol::Protocol;
use crate::servers::server_runnable::ServerRunnable;
use crate::{NetChooser, infrastructures::json_repositories::JsonRepositories};
use std::collections::HashMap;
use std::net::{UdpSocket, SocketAddr, Ipv4Addr};
use std::sync::{Mutex, Arc};

#[derive(Clone)]
pub struct RelayManager{
    repositories : JsonRepositories,
    net_chooser : NetChooser,
    protocol : Protocol,
    server_list : Arc<Mutex<HashMap<String, ServerRunnable>>>
}
impl RelayManager {
    pub fn new(repositories : JsonRepositories, net_chooser : NetChooser, protocol : Protocol) -> Self {
        RelayManager {
            repositories,
            net_chooser,
            protocol,
            server_list : Arc::new(Mutex::new(HashMap::new())),
        }
    }

    pub fn start(&self) {
        let relay = self.repositories.get_relay();
        let selected_interface = self.net_chooser.select_interface();
        println!("[*] Selected interface: {}", selected_interface.name);
        let ipv4_addr = selected_interface.addr.iter().find(|addr| addr.ip().is_ipv4());
        if ipv4_addr.is_none() {
            println!("[!] No ipv4 address found for selected interface");
            return;
        }else{
            println!("[+] Selected interface ipv4 address: {}", ipv4_addr.unwrap().ip());
            let ipv4adrr : Ipv4Addr = ipv4_addr.unwrap().ip().to_string().parse().unwrap();
            std::thread::sleep(std::time::Duration::from_secs(1));
            match Self::multicast_function(self,relay, ipv4adrr) {
                Ok(_) => print!("Ok"),
                Err(err) => println!("Error: {}", err)
            }
        }
    }

    fn multicast_function(&self,relay: Relay, ipv4 : Ipv4Addr) -> Result<(), std::io::Error> {
        let multicast_socket = UdpSocket::bind(format!("0.0.0.0:{}", relay.get_multicast_port()))?;
        let multicast_addr: std::net::SocketAddrV4 = format!("{}:{}", relay.get_multicast_address(), 0).parse().unwrap();

        multicast_socket.join_multicast_v4(&multicast_addr.ip(), &ipv4)?;
        multicast_socket.set_nonblocking(true)?;
        let mut buf = [0; 1024];
        loop {
            match multicast_socket.recv_from(&mut buf) {
                Ok((amt, src)) => {
                    let message_received = String::from_utf8_lossy(&buf[..amt]);
                    println!("[+] Received {} bytes from {}", amt, src);
                    println!("[+] Message received: {}", message_received);
                    RelayManager::handle_message(self,&message_received, src);
                }
                Err(ref e) if e.kind() == std::io::ErrorKind::WouldBlock => {
                    //println!("Waiting for multicast message");
                }
                Err(e) => {
                    println!("[!] Error receiving multicast message: {}", e);
                }
            }
            drop(&multicast_socket);
        }
    }

    fn handle_message(&self, message_received: &str, ip_address: SocketAddr) {
        let check_message = self.protocol.verify_message(message_received);
        if check_message[0] == "ECHO" {
            let port = &check_message[1];
            let domain = &check_message[2];
            let relay = self.repositories.get_relay();
            let server = relay.get_server(domain.as_str());
            let socket = SocketAddr::new(ip_address.ip(), port.parse::<u16>().unwrap());
            if (server.get_domain() != "null" && server.get_base64_aes() != "null") && (self.server_list.lock().unwrap().contains_key(domain) == false) {
               let server_runnable = ServerRunnable::new(self.clone(),server.get_domain().to_string(), server.get_base64_aes().to_string(), self.protocol.clone(),socket);

                let mut server_list = self.server_list.lock().unwrap();
                server_list.insert(domain.to_string(), server_runnable);
                let server_runnable = server_list.get(domain).unwrap();
                let runnable = server_runnable.clone();
                std::thread::spawn(move || {
                    runnable.start();
                });
            }
        }
    }
    
    pub fn remove_server(&self, domain: String) {
        let mut server_list = self.server_list.lock().unwrap();
        server_list.remove(&domain);
    }
    pub fn send_message(&self, domain: String, message: String){
        println!("[*] Sending message to server: {}", domain);
        let server_list = self.server_list.lock().unwrap();
       match server_list.get(&domain) {
           Some(server) => {
               server.add_message(message);
           },
           None => {}

       }
    }
}