use std::{path::Path, result};
use grammar::protocol::Protocol;
use infrastructures::json_repositories::JsonRepositories;
use relay::{relay_manager::RelayManager};
use utils::{net_chooser::NetChooser, aes_codec::AESCodec};

mod domains;
mod grammar;
mod infrastructures;
mod relay;
mod utils;
mod servers;

fn main() {
   let i_relay_repositories = JsonRepositories::new(
        Path::new("src/resources/relay.json")
            .to_str()
            .unwrap()
            .to_string(),
    );
    let relay_manager = RelayManager::new(
        i_relay_repositories,
        NetChooser::new(),
        Protocol::new(),
    );
    relay_manager.start();
}