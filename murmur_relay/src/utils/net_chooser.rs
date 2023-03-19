use network_interface::{NetworkInterface, NetworkInterfaceConfig};
use std::io::{self, Write};
use std::vec::Vec;

#[derive(Clone)]
pub struct NetChooser {
    interfaces: Vec<NetworkInterface>,
    selected_interface: Option<NetworkInterface>,
}

impl NetChooser {
    pub fn new() -> Self {
        let interfaces = NetworkInterface::show().unwrap().into_iter().collect();
        Self { interfaces, selected_interface: None }
    }

    pub fn get_interface_by_index(&self, i: usize) -> Option<&NetworkInterface> {
        self.interfaces.get(i)
    }

    pub fn get_interfaces(&self) -> Vec<String> {
        self.interfaces.iter().map(|iface| iface.name.clone()).collect()
    }

    pub fn select_interface(&self) -> NetworkInterface {
        let mut stdout = io::stdout();
        let stdin = io::stdin();

        let all_interface_names = self.get_interfaces();
        for (index, name) in all_interface_names.iter().enumerate() {
            writeln!(stdout, "{}. {}", index, name).unwrap();
        }
        write!(stdout, "Select your interface: ").unwrap();
        stdout.flush().unwrap();

        let mut input = String::new();
        stdin.read_line(&mut input).unwrap();
        let selected_index = input.trim().parse::<usize>().unwrap();
        let selected_interface = self.get_interface_by_index(selected_index).unwrap();
        selected_interface.clone()
    }
}