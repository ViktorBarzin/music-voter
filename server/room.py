from collections import defaultdict
from dataclasses import dataclass
from typing import Dict, List, Optional
import uuid


class Room:
    id: str
    name: str
    password: Optional[str]
    owner: 'User'
    users: Optional[List['User']]
    votes: Dict[str, List['User']]  # url -> List[User]

    def __init__(self, name: str, owner: 'User', password: Optional[str] = None, joined_users: Optional[List['User']] = None):
        self.id = str(uuid.uuid4())
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
            if user in self.votes[option.url]:
                return
            else:
                self.votes[option.url].append(user)


@dataclass
class User:
    username: str  # add some validation here at some point

    def __init__(self, username: str) -> None:
        self._validate_username(username)
        self.username = username

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

