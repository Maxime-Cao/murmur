use serde::{Deserialize, Serialize, de::DeserializeOwned};
use std::marker::PhantomData;

use super::server_dto::ServerDto;

#[derive(Serialize, Deserialize)]
pub struct RelayDto<T = ()>
where
    T: DeserializeOwned,
{
    multicast_address: String,
    multicast_port: u16,
    configured_domains: Vec<ServerDto>,
    #[serde(skip)]
    phantom: PhantomData<T>,
}

impl<T> RelayDto<T>
where
    T: DeserializeOwned,
{
    pub fn new(
        multicast_address: String,
        multicast_port: u16,
        configured_domains: Vec<ServerDto>,
    ) -> Self {
        RelayDto {
            multicast_address,
            multicast_port,
            configured_domains,
            phantom: PhantomData,
        }
    }

    pub fn multicast_address(&self) -> &String {
        &self.multicast_address
    }

    pub fn multicast_port(&self) -> &u16 {
        &self.multicast_port
    }

    pub fn get_server_list(&self) -> &Vec<ServerDto> {
        &self.configured_domains
    }
}