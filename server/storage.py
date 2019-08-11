import sqlite3
import threading
from collections import defaultdict
from typing import Dict, List, Optional, Set
from room import Room, User


ROOMS: Set[Room] = set()  # this should be loaded from persistent storage at some point
DB_SINGLETON = None
DB_NAME = 'databse.db'
LOCK = threading.Lock()


def get_db(db_name: str = DB_NAME) -> 'Database':
    global DB_SINGLETON
    if not DB_SINGLETON:
        DB_SINGLETON = Database(db_name)
    return DB_SINGLETON


def get_rooms() -> List[Room]:
    """
    Get all rooms, maybe read from some persistent storage.
    For now just server current rooms
    """
    rooms = get_db().get_rooms()
    for room in rooms:
        room_votes = get_db().get_votes(room.id)
        room.votes = room_votes
    return rooms


def add_room(room: Room) -> Room:
    get_db().create_room(room)
    return room


def get_user_from_username(username: str) -> Optional[User]:
    """
    Login, for now just find user by id
    """
    try:
        match = next(filter(lambda user: user.username == username, get_db().get_users()))
    except StopIteration:
        return None
    return match


def add_user(user: User) -> User:
    added_user = get_db().create_user(user)
    return added_user


def get_room_by_name(room_name: str) -> Optional[Room]:
    try:
        return next(filter(lambda r: r.name == room_name, get_db().get_rooms()))
    except StopIteration:
        return None


def get_votes(room_name: str) -> Dict[str, List[User]]:
    room = get_room_by_name(room_name)
    if not room:
        raise ValueError(f'Room with name {room_name} not found')
    votes_dict = get_db().get_votes(room.id)
    return votes_dict


def vote(voter: User, url: str, room_name: str, is_voting: bool) -> str:
    room = get_room_by_name(room_name)
    if not room:
        raise ValueError(f'Room with name {room_name} not found')

    # if voting
    if is_voting:
        try:
            get_db().create_vote(voter.id, url, room.id)
        except sqlite3.IntegrityError:
            # If user has already voted for this, it's fine
            pass
    # if "unvoting"
    else:
        get_db().remove_vote(voter.id, url, room.id)
    return url


def lock(func):
    def inner(*args, **kwargs):
        LOCK.acquire()
        res = func(*args, **kwargs)
        LOCK.release()
        return res
    return inner


class Database:
    db_path: str

    def __init__(self, db_name):
        self.db_path = db_name
        self.conn = sqlite3.connect(self.db_path)
        self.cursor = self.conn.cursor()
        self._init_db_schema()

    def _init_db_schema(self):
        # Create user table
        self.cursor.execute('''CREATE TABLE IF NOT EXISTS user
                            (
                            [generated_id] INTEGER PRIMARY KEY,
                            [username] text NOT NULL UNIQUE

                            )''')
        # Create room table
        self.cursor.execute(''' CREATE TABLE IF NOT EXISTS room
                            (
                            [generated_id] INTEGER PRIMARY KEY,
                            [name] text NOT NULL UNIQUE,
                            [password] text,
                            [owner] INTEGER NOT NULL,
                            FOREIGN KEY (owner) REFERENCES user(generated_id)
                            ) ''')

        # Create votes table
        self.cursor.execute('''CREATE TABLE IF NOT EXISTS vote
                            (
                            [generated_id] INTEGER PRIMARY KEY,
                            [url] TEXT NOT NULL,
                            [voter_id] INTEGER NOT NULL,
                            [room_id] INTEGER NOT NULL,
                            FOREIGN KEY (voter_id) REFERENCES user(generated_id),
                            FOREIGN KEY (room_id) REFERENCES room(generated_id),
                            UNIQUE (voter_id, url)
                            )''')
        self.conn.commit()

    # @lock
    def create_user(self, user: User) -> User:
        '''
        Try to save user to db. Return
        '''
        self.conn = sqlite3.connect(self.db_path)
        self.cursor = self.conn.cursor()
        try:
            self.cursor.execute('''INSERT INTO user(username) VALUES (?)''', [user.username])
            self.conn.commit()
            return user
        except sqlite3.IntegrityError:
            # User exists
            raise ValueError(f'User with username {user.username} exists')

    def get_users(self) -> List[User]:
        self.conn = sqlite3.connect(self.db_path)
        self.cursor = self.conn.cursor()

        rows = self.cursor.execute('SELECT * FROM user').fetchall()
        self.conn.commit()
        users_list: List[User] = []
        for row in rows:
            user = User(uid=int(row[0]), username=row[1])
            users_list.append(user)
        return users_list

    def get_user_by_id(self, user_id: int) -> Optional[User]:
        # May be optimized in sql, for now don't
        try:
            user = next(
                (user for user in self.get_users() if user.id == user_id)
            )
            return user
        except StopIteration:
            return None

    # @lock
    def create_room(self, room: Room):
        self.conn = sqlite3.connect(self.db_path)
        self.cursor = self.conn.cursor()

        try:
            self.cursor.execute('''INSERT INTO room(name, password, owner) VALUES (?,?,?)''', [room.name, room.password, room.owner.id])
            self.conn.commit()
        except sqlite3.IntegrityError:
            raise ValueError(f'Room with name {room.name} exists')

    # @lock
    def get_rooms(self) -> List[Room]:
        self.conn = sqlite3.connect(self.db_path)
        self.cursor = self.conn.cursor()

        query = self.cursor.execute('SELECT * FROM room')
        rows = query.fetchall()
        self.conn.commit()
        rooms_list: List[Room] = []
        for row in rows:
            room_id = int(row[0])
            room_name = row[1]
            room_pass = row[2]
            owner_id = int(row[3])
            owner = self.get_user_by_id(owner_id)
            if not owner:
                raise ValueError(f'No user with user id {owner_id}')
            if not room_pass:
                room_pass = None

            room = Room(name=room_name, room_id=room_id, owner=owner, password=room_pass)
            rooms_list.append(room)
        return rooms_list

    # @lock
    def create_vote(self, voter_uid: int, url: str, room_id: int) -> None:
        self.conn = sqlite3.connect(self.db_path)
        self.cursor = self.conn.cursor()

        self.cursor.execute('''INSERT INTO vote(url, voter_id, room_id) VALUES (?,?,?)''', [url, voter_uid, room_id])
        self.conn.commit()

    # @lock
    def remove_vote(self, voter_uid: int, url: str, room_id: int) -> None:
        self.conn = sqlite3.connect(self.db_path)
        self.cursor = self.conn.cursor()

        self.cursor.execute('''DELETE FROM vote WHERE url = (?) AND voter_id = (?) AND room_id = (?)''', [url, voter_uid, room_id])
        self.conn.commit()

    # @lock
    def get_votes(self, room_id: int) -> Dict[str, List[User]]:
        self.conn = sqlite3.connect(self.db_path)
        self.cursor = self.conn.cursor()

        rows = self.cursor.execute('''SELECT url, voter_id FROM vote WHERE room_id = (?)''', [room_id]).fetchall()
        self.conn.commit()
        votes: Dict[str, List[User]] = defaultdict(list)
        for row in rows:
            url = row[0]
            voter_id = int(row[1])
            user = self.get_user_by_id(voter_id)
            if user:
                votes[url].append(user)

        return votes
