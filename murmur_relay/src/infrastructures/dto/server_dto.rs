use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize)]
pub struct ServerDto{
    domain : String,
    base_64_aes: String,
}

impl ServerDto {
    pub fn new(domain : String, base_64_aes : String) -> Self {
        ServerDto{
            domain,
            base_64_aes,
        }
    }
    
    pub fn domain(&self) -> &String {
        &self.domain
    }
    
    pub fn base_64_aes(&self) -> &String {
        &self.base_64_aes
    }
}