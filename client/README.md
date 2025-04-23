# HackSync Deployment Guide

This guide explains how to deploy the HackSync application on a Linux server.

## Prerequisites

- Java 21
- Node.js and npm
- PostgreSQL
- systemd
- nginx (recommended for reverse proxy)

## Server Setup

1. Create a system user for the application:
   ```bash
   sudo useradd -m -s /bin/bash hacksync
   ```

2. Install Java 21:
   ```bash
   sudo apt update
   sudo apt install openjdk-21-jdk
   ```

3. Install Node.js and npm:
   ```bash
   sudo apt install nodejs npm
   ```

4. Install PostgreSQL:
   ```bash
   sudo apt install postgresql postgresql-contrib
   ```

## Deployment Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/HackSync.git
   ```

2. Set up the server:
   ```bash
   # Copy service file
   sudo cp deploy/server/hacksync-server.service /etc/systemd/system/
   
   # Make start script executable
   chmod +x deploy/server/start.sh
   
   # Enable and start the service
   sudo systemctl enable hacksync-server
   sudo systemctl start hacksync-server
   ```

3. Set up the client:
   ```bash
   # Copy service file
   sudo cp deploy/client/hacksync-client.service /etc/systemd/system/
   
   # Make start script executable
   chmod +x deploy/client/start.sh
   
   # Enable and start the service
   sudo systemctl enable hacksync-client
   sudo systemctl start hacksync-client
   ```

4. Configure nginx (recommended):
   ```nginx
   # Server configuration
   server {
       listen 80;
       server_name api.yourdomain.com;
       
       location / {
           proxy_pass http://localhost:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   
   # Client configuration
   server {
       listen 80;
       server_name yourdomain.com;
       
       location / {
           proxy_pass http://localhost:3000;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```

## Environment Variables

Create a `.env` file in the server directory with the following variables:
```
DB_URL=jdbc:postgresql://localhost:5432/postgres
DB_USER=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
```

## Monitoring

Check service status:
```bash
sudo systemctl status hacksync-server
sudo systemctl status hacksync-client
```

View logs:
```bash
sudo journalctl -u hacksync-server
sudo journalctl -u hacksync-client
```

## Troubleshooting

1. Check service status:
   ```bash
   sudo systemctl status hacksync-server
   ```

2. View logs:
   ```bash
   sudo journalctl -u hacksync-server -f
   ```

3. Restart services:
   ```bash
   sudo systemctl restart hacksync-server
   sudo systemctl restart hacksync-client
   ```