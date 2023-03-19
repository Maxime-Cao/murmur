use super::mapper::mapping::Mapping;
use crate::domains::relay::Relay;
use std::io::Read;
use std::fs::File;
use std::io::BufWriter;

#[derive(Clone)]
pub struct JsonRepositories{
    config_file_path: String,
}

impl JsonRepositories {
    pub fn new(config_file_path: String) -> Self {
        JsonRepositories {
            config_file_path,
        }
    }
    
    pub fn get_relay(&self) -> Relay {
        let mut file = match File::open(&self.config_file_path) {
            Ok(file) => file,
            Err(e) => panic!("Error opening config file {}: {}", self.config_file_path, e),
        };

        let mut json = String::new();
        match file.read_to_string(&mut json) {
            Ok(_) => {},
            Err(e) => panic!("Error reading config file {}: {}", self.config_file_path, e),
        }

        match serde_json::from_str(&json) {
            Ok(relay_dto) => {
                let map = Mapping::new();
                map.get_relay(relay_dto)
            },
            Err(e) => panic!("Error parsing config file {}: {}", self.config_file_path, e),
        }
    }

    pub fn write_relay(&self,relay: Relay) {
        let map = Mapping::new();
        let relay_dto = map.get_relay_dto(relay);
        let file = File::create(&self.config_file_path).unwrap();
        let writer = BufWriter::new(file);
        serde_json::to_writer_pretty(writer, &relay_dto).unwrap();
    }
}