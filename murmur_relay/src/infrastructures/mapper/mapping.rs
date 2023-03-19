use crate::infrastructures::dto::relay_dto::RelayDto;
use crate::domains::relay::Relay;
use crate::domains::server::Server;
use crate::infrastructures::dto::server_dto::ServerDto;

pub struct Mapping{}

impl Mapping{
    pub fn new() -> Self {
        Mapping{}
    }
    pub fn get_relay(&self, relay_dto: RelayDto) -> Relay {
        let server_list_dto = relay_dto.get_server_list();
        let mut server_list = Vec::new();
        for server_dto in server_list_dto {
            let server = Server::new(server_dto.domain().to_owned(), server_dto.base_64_aes().to_owned());
            server_list.push(server);
        }
        Relay::new(relay_dto.multicast_address().to_owned(),relay_dto.multicast_port().to_owned().into(),server_list)
    }
    
    pub fn get_relay_dto(&self, relay: Relay) -> RelayDto {
        let server_list = relay.get_server_list();
        let mut server_list_dto = Vec::new();
        for server in server_list {
            let server_dto = ServerDto::new(server.get_domain().to_owned(), server.get_base64_aes().to_owned());
            server_list_dto.push(server_dto);
        }
        RelayDto::new(relay.get_multicast_address().to_owned(), relay.get_multicast_port().to_owned().try_into().unwrap(), server_list_dto)
    }
}