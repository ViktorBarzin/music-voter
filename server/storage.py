from typing import Optional, Set
from room import Room, User


ROOMS: Set[Room] = set()  # this should be loaded from persistent storage at some point
USERS: Set[User] = set()  # ^


def get_rooms() -> Set[Room]:
    """
    Get all rooms, maybe read from some persistent storage.
    For now just server current rooms
    """
    global ROOMS
    return ROOMS


def add_room(room: Room) -> Room:
    global ROOMS
    if room:
        ROOMS.add(room)
    return room


def get_user_from_username(username: str) -> Optional[User]:
    """
    Login, for now just find user by id
    """
    global USERS
    try:
        match = next(filter(lambda user: user.username == username, USERS))
    except StopIteration:
        return None
    return match


def add_user(user: User) -> User:
    global USERS
    USERS.add(user)
    return user


def get_room_by_name(room_name: str) -> Optional[Room]:
    global ROOMS
    try:
        return next(filter(lambda r: r.name == room_name, ROOMS))
    except StopIteration:
        return None


