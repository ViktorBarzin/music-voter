from typing import List, Optional
from room import Room, User


ROOMS = []  # this may be loaded from db at some point
USERS = []  # ^


def get_rooms() -> List[Room]:
    """
    Get all rooms, maybe read from some persistent storage.
    For now just server current rooms
    """
    global ROOMS
    return ROOMS


def add_room(room: Room):
    global ROOMS
    if room:
        ROOMS.append(room)


def get_user_from_id(uid: str) -> Optional[User]:
    """
    Login, for now just find user by id
    """
    global USERS
    try:
        match = next(filter(lambda user: user.uid == uid, USERS))
    except StopIteration:
        return None
    return match

