from collections import defaultdict
from dataclasses import dataclass
from typing import Dict, List, Optional
import uuid


class Room:
    id: int
    name: str
    password: Optional[str]
    owner: 'User'
    users: List['User']  # List becuase sets are hard ot serialize
    votes: Dict[str, List['User']]  # url -> List[User]

    def __init__(self, name: str, owner: 'User', room_id: int = 0, password: Optional[str] = None, joined_users: Optional[List['User']] = None):
        self.id = room_id or int(uuid.uuid4())
        self.name = name
        self.owner = owner
        self.password = password

        if not joined_users:
            joined_users = [owner]
        self.users = joined_users
        self.votes = defaultdict(list)

    def vote(self, user: 'User', option: 'VoteOption') -> None:
        # if options is new, put it in votes
        if option not in self.votes:
            self.votes[option.url].append(user)
        else:
            # If user has already voted, skip
            if user.username in self.votes[option.url]:
                return
            else:
                self.votes[option.url].append(user)

    def join_user(self, user: 'User') -> None:
        if user not in self.users:
            self.users.append(user)


@dataclass
class User:
    id: int
    username: str  # add some validation here at some point

    def __init__(self, username: str, uid: int = 0) -> None:
        self._validate_username(username)
        self.username = username
        self.id = uid or int(uuid.uuid4())

    def __hash__(self) -> int:
        return hash(self.username)

    def _validate_username(self, username: str) -> str:
        if not username:
            raise ValueError('Invalid username')
        return username


@dataclass(eq=True)
class VoteOption:
    title: str
    url: str

    def __hash__(self) -> int:
        return hash(self.title) * hash(self.url)

