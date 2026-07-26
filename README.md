# Quantum Messenger ⚛️🧅

> **Post-Quantum P2P & Tor Onion Encrypted Communications Engine**
> *Designed for Investigative Journalists, Legal Professionals, Enterprise Defense, and High-Threat Environments.*

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://img.shields.io/badge/Android-API_26%2B-brightgreen.svg)](https://developer.android.com)
[![PQC Standard](https://img.shields.io/badge/Cryptography-NIST_ML--KEM_1024-blueviolet.svg)](https://csrc.nist.gov/pqc)
[![Anonymity](https://img.shields.io/badge/Network-Tor_v3_Onion_SOCKS5-orange.svg)](https://www.torproject.org/)

---

## 📌 Executive Overview

**Quantum Messenger** is a zero-trust, post-quantum cryptographic (PQC) peer-to-peer (P2P) messaging application integrated with a background **Tor Onion Proxy Routing Layer** and an **Enterprise MDM Policy Engine**. 

It eliminates centralized server vulnerability, metadata exposure, and quantum computing "harvest-now, decrypt-later" threats facing investigative reporters, human rights defenders, corporate legal counsel, and defense field personnel.

---

## 🚀 Key Architectural Pillars

### 1. ⚛️ Post-Quantum Cryptographic Hybrid Engine
* **Key Encapsulation Mechanism (KEM):** NIST FIPS 203 standardized **ML-KEM-1024 (Kyber-1024)** paired with ECDH Curve25519 for post-quantum defense.
* **Digital Signatures:** NIST FIPS 204 **ML-DSA-87 (Dilithium-5)** for tamper-proof peer identity verification.

### 2. 🧅 Tor v3 Hidden Service Onion Routing
* All P2P traffic routes through an embedded SOCKS5 Tor daemon (`127.0.0.1:9050`) using 3-hop onion encryption.
* Hides user IP addresses, physical geolocation, and cellular metadata from ISP/state surveillance.

### 3. 🛡️ Hardware-Backed Keystore Attestation
* Binds cryptographic key pairs directly to hardware security modules like **Google Titan M2**, **ARM TrustZone**, or **Android StrongBox**. Keys remain non-exportable even under root privilege escalation.

### 4. 🏢 Zero-Trust Enterprise MDM Policy Engine
* **Screen & Screenshot Prevention:** Enforces Android `FLAG_SECURE` across all UI surfaces.
* **Clipboard Isolation:** Restricts cross-app copy-pasting of decrypted payload text.
* **Dead Man's Inactivity Switch:** Configurable zeroize wipe timer (1 to 30 days) that purges Room database tables and private keys if device is untouched.
* **Emergency Remote Wipe:** Zero-knowledge wipe triggered via encrypted administrator control channel.

---

## 📊 Feature Comparison Matrix

| Feature | Quantum Messenger | Signal | Telegram | WhatsApp | Threema | Session |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **Post-Quantum Cryptography (PQC)** | **Kyber-1024 / Dilithium-5** | PQXDH (Kyber-768) | ❌ None | ❌ None | ❌ None | ❌ None |
| **Network Anonymity** | **Native Tor v3 Onion** | ❌ Clearnet IP | ❌ Clearnet IP | ❌ Clearnet IP | ❌ Clearnet IP | Oxen Lokinet |
| **Server Architecture** | **Serverless P2P / Mesh** | Central Server | Cloud Server | Meta Cloud | Central Server | Service Nodes |
| **Hardware Keystore Binding** | **Titan M2 / StrongBox** | OS Keystore | ❌ None | OS Keystore | OS Keystore | ❌ None |
| **Enterprise MDM & DLP** | **Full (Wipe, Policy, DeadMan)** | ❌ None | ❌ None | WhatsApp Biz | Enterprise MDM | ❌ None |
| **Phone Number Required?** | **NO (Quantum ID)** | YES | YES | YES | NO | NO |

---

## 📦 Software Tiering & Licensing

* **Open Source Community Tier (MIT License):** Full P2P PQC engine, Tor SOCKS5 proxy, local encrypted Room database, and basic ephemeral messaging — 100% free for Android.
* **Enterprise Defender Tier:** Adds centralized MDM management, compliance policy enforcement, hardware-backed attestation, and containerized DLP controls.
* **Government & NATO Military Tier:** Custom tactical firmware builds, air-gapped mesh deployments, hardware token (YubiKey/FIDO2) integration, and BSI / ANSSI / NIST compliance audit packages.

---

## 📜 License

This open-source release is provided under the **MIT License**. See [LICENSE](LICENSE) for full details.
