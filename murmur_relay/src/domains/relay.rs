use crate::domains::server::Server;
pub struct Relay {
    multicast_address: String,
    multicast_port: u16,
    server_list: Vec<Server>,
}

impl Relay {
    pub fn new(multicast_address: String, multicast_port: u16, server_list: Vec<Server>) -> Relay {
        Relay {
            multicast_address,
            multicast_port,
            server_list: server_list
        }
    }

    pub fn get_multicast_address(&self) -> &str {
        &self.multicast_address
    }

    pub fn get_multicast_port(&self) -> u16 {
        self.multicast_port
    }

    pub fn get_server_list(&self) -> &Vec<Server> {
        &self.server_list
    }
    
    pub fn get_server(&self, domaine : &str) -> Server {
        for server in &self.server_list {
            if server.get_domain() == domaine {
                let domain = server.get_domain().to_string();
                let base64_aes = server.get_base64_aes().to_string();
                return Server::new(domain, base64_aes)
            }
        }
        panic!("Domaine non trouvé");
    }
}