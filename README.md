# Open Pi Widget 2.0 (incremental update)

Android Java/XML widget and read-only Raspberry Pi status API.

## Changes
- Fixes null CPU temperature handling on Android.
- Adds service status to `/api/status` with a **fixed allowlist** of systemd units.
- Shows service summary in widget and full service list in app.
- Adds manual refresh button and dark dashboard styling.
- Preserves existing Tailscale HTTPS endpoint and API shape.

## Deployment
1. Back up your running `api/server.py` and APK.
2. Copy `api/server.py` to the Raspberry Pi service's configured script path.
3. Restart **only** the service that runs the status API (identify it first with `systemctl`); verify `curl http://127.0.0.1:28766/api/status`.
4. Build Android app using your existing local Android SDK toolchain. This repository has no Gradle build configuration yet.
5. Install the new APK over the existing app if signed with the same certificate.

The API listens only on `127.0.0.1`; keep HTTPS/Tailscale reverse proxy access restricted. `systemctl is-active` only reads status; it never starts or stops services. Unknown/uninstalled services show `unknown`, not `inactive`. Telegram is not listed until its actual unit name is confirmed. Docker is not included in this version.

**Next**: responsive 1×1/2×1/2×2/4×2 widgets and a verified Android build workflow after confirming local SDK build scripts.
