import json
import subprocess
import psutil
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

# Fixed allowlist: the API never accepts commands or service names from requests.
SERVICES = {
    "WhatsApp MCP": "whatsapp-mcp.service",
    "WhatsApp tunnel": "openai-whatsapp-tunnel.service",
    "Mail tunnel": "openai-mail-tunnel.service",
    "Terminal tunnel": "openai-terminal-tunnel.service",
    "Instagram MCP": "instagram-mcp.service",
}

def service_state(unit):
    try:
        result = subprocess.run(["systemctl", "is-active", unit], capture_output=True,
                                text=True, timeout=2, check=False)
        state = result.stdout.strip()
        return state if state in ("active", "inactive", "failed", "activating", "deactivating") else "unknown"
    except (OSError, subprocess.TimeoutExpired):
        return "unknown"

class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path != "/api/status":
            self.send_error(404)
            return
        temps = psutil.sensors_temperatures()
        readings = temps.get("cpu_thermal", [])
        temperature = readings[0].current if readings else None
        data = {
            "cpu": psutil.cpu_percent(interval=0.2),
            "ram": psutil.virtual_memory().percent,
            "disk": psutil.disk_usage("/").percent,
            "temperature": temperature,
            "services": {name: service_state(unit) for name, unit in SERVICES.items()}
        }
        body = json.dumps(data, ensure_ascii=False).encode("utf-8")
        self.send_response(200)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Cache-Control", "no-store")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

if __name__ == "__main__":
    ThreadingHTTPServer(("127.0.0.1", 28766), Handler).serve_forever()
