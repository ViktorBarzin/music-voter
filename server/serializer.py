import json
from typing import List
from room import Room


def serialize_rooms(rooms: List[Room]) -> str:
    serialized = json.dumps([serialize_room(x) for x in rooms])
    return serialized


def serialize_room(room: Room) -> str:
    """
    Main logic to serialize rooms to json.
    This is where the json format is specified
    """
    serialized = json.dumps(room.__dict__, default=lambda o: o.__dict__)
    return serialized
