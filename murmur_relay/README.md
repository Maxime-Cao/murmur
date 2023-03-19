# Murmur Relay

## Informations
Le murmur relay est un projet créé en rust pour partager des messages entre plusieurs serveurs murmur.
```
Version Cargo : 1.68.0 
```
Lien de téléchargement : 
<br>[RUST & CARGO](https://doc.rust-lang.org/cargo/getting-started/installation.html)

## Classes du projet
```
protocol : Grammaire

relay_manager : Gestionnaire de relay & multicast

server_runnable : Thread d'un serveur murmur (unicast)

aes_codec : Codec AES (chiffrement / déchiffrement)
net_chooser : Choix de l'interface réseau
```
## Lancement du projet
```rust
cargo run
````

## Bibliothèques externes
[regex](https://docs.rs/crate/regex/1.7.1)<br>
[serde](https://docs.rs/crate/serde/1.0.156)<br>
[serde_json](https://docs.rs/crate/serde_json/1.0.94)<br>
[network-interface](https://docs.rs/crate/network-interface/1.0.0)<br>
[if-addrs](https://docs.rs/crate/if-addrs/0.10.1)<br>
[multicast-socket](https://docs.rs/crate/multicast-socket/0.2.2)<br>
[multicast-dns](https://docs.rs/crate/multicast_dns/0.5.0)<br>
[libc](https://docs.rs/crate/libc/0.2.140)<br>
[aes](https://docs.rs/crate/aes/0.8.2)<br>
[aes-gcm](https://docs.rs/crate/aes-gcm/0.10.1)<br>
[base64](https://docs.rs/crate/base64/0.21.0)<br>
[crypto-common](https://docs.rs/crate/crypto-common/0.1.6)<br>
[cipher](https://docs.rs/crate/cipher/0.4.4)<br>
[rand](https://docs.rs/crate/rand/0.8.5)<br>
