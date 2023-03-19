use aes::cipher::generic_array::GenericArray;
use aes_gcm::{Aes256Gcm, aead::{Aead, Payload}, KeyInit, Nonce};
use crypto_common::rand_core::OsRng;
use libc::rand;
use rand::{RngCore, Rng};


pub struct AESCodec{
    gcm_tag_length : i32,
    gcm_iv_length : i32,
}

impl AESCodec {
    pub fn new() -> AESCodec {
        AESCodec {
            gcm_tag_length : 16,
            gcm_iv_length :  12,
        }
    }
    pub fn encrypt(&self, key: &str, message: &str) -> Result<String, aes_gcm::Error> {
        let key = base64::decode(key).unwrap();
        let iv = rand::thread_rng().gen::<[u8; 12]>();

        let cipher = Aes256Gcm::new(GenericArray::from_slice(&key));

        let payload = Payload {
            msg: message.as_bytes(),
            aad: &[],
        };
        let encrypted_message = cipher.encrypt(GenericArray::from_slice(&iv), payload).unwrap();

        let message_string = base64::encode(&encrypted_message);
        let iv_string = base64::encode(&iv);

        let message = format!("{}{}", message_string, iv_string);

        Ok(message)
    }
    
    
    pub fn decrypt(&self, key : &str, message: Vec<u8>) -> Result<String, aes_gcm::Error>{
        let key = base64::decode(key).unwrap();
        let messageParts = message.split_at(message.len() - self.gcm_tag_length as usize);
        let message = base64::decode(messageParts.0);
        let iv = base64::decode(messageParts.1);

        let cipher = Aes256Gcm::new(GenericArray::from_slice(&key));

        let payload = Payload {
            msg: &message.unwrap(),
            aad: &[],
        };

        let iv2 = iv.unwrap();
        let decrypted_message = cipher.decrypt(GenericArray::from_slice(&iv2), payload).unwrap();

        let message_string = String::from_utf8(decrypted_message).unwrap();
        Ok(message_string)
    }
}