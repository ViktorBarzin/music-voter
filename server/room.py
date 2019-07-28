from typing import Dict, List, Optional
import uuid


class Room:
    id: str
    name: str
    password: Optional[str]
    owner: 'User'
    users: List['User']
    votes: Dict['VoteOption', 'User']

    def __init__(self, name: str, owner: 'User', password: str = None, joined_users: List['User'] = None):
        self.id = str(uuid.uuid4())
        self.name = name
        self.owner = owner
        self.password = password

        if not joined_users:
            joined_users = [owner]
        self.users = joined_users


class User:
    uid: str
    username: str

    def __init__(self, username):
        self.username = username


class VoteOption(dict):
    title: str
    url: str


