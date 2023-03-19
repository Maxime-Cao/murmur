pub struct Server {
    domain: String,
    base64_aes: String,
}

impl Server {
    pub fn new(domain: String, base64_aes: String) -> Server {
        Server { domain, base64_aes }
    }

    pub fn get_domain(&self) -> &str {
        &self.domain
    }

    pub fn get_base64_aes(&self) -> &str {
        &self.base64_aes
    }
}