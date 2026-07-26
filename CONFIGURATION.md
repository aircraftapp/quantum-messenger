# Configuration Guide - Quantum Messenger ⚙️

This document details environment settings, Tor configuration, and MDM policy options.

---

## ⚙️ Environment Variables & BuildConfig

System settings are defined in `BuildConfig` or runtime `.env` files:

| Property | Default Value | Description |
| :--- | :--- | :--- |
| `TOR_SOCKS_PORT` | `9050` | SOCKS5 proxy port for Tor onion daemon |
| `PQC_ALGORITHM_KEM` | `Kyber1024` | Post-quantum KEM key algorithm |
| `PQC_ALGORITHM_DSA` | `Dilithium5` | Post-quantum DSA signature algorithm |
| `MDM_ENFORCE_FLAG_SECURE` | `true` | Prevents screenshots & video capture |
| `MDM_DEAD_MAN_DAYS_DEFAULT` | `7` | Days before inactive zeroize wipe |

---

## 🧅 Tor Onion Proxy Settings

Tor onion routing is configured automatically on startup:
* **Local Proxy Loopback:** `socks5://127.0.0.1:9050`
* **Onion Service Version:** v3 (56-character base32 address)
* **Circuit Hops:** 3-hop entry, middle, and exit onion relays

---

## 🏢 MDM & Device Compliance Configuration

To configure policy defaults programmatically, modify `QuantumViewModel` or inject enterprise app restrictions via Android EMM managed configuration keys:

```xml
<appRestrictions>
  <restriction
    key="enterprise_mode_enabled"
    title="Enable Enterprise Mode"
    type="bool"
    defaultValue="true" />
  <restriction
    key="dead_man_switch_days"
    title="Dead Man Switch Inactivity Days"
    type="integer"
    defaultValue="7" />
</appRestrictions>
```
