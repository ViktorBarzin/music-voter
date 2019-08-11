# API Map

| Endpoint | Methods | Post params
--- | --- | ---
| `/api/rooms` | `GET`, `POST` | `name`, `owner_username`, [`password`]
| `/api/users` | `POST` | `username`
| `/api/vote/<room name>` | `GET`, `POST` | `url`, `username`
| `/api/join/<room name>` | `POST` | `username`
