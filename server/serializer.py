import json
from typing import Any, Dict, List
from room import Room, VoteOption

hashable = {str, int, float, bool, None, set}


def serialize_rooms(rooms: List[Room]) -> str:
    serialized = [serialize_room(x) for x in rooms]
    result = {"rooms": serialized}
    return json.dumps(result, default=lambda o: o.__dict__)


def serialize_room(room: Room) -> Dict[str, Any]:
    """
    Main logic to serialize rooms to json.
    This is where the json format is specified
    """
    result = {}
    for key, val in room.__dict__.items():
        if type(val) in hashable:
            result[key] = val
        elif isinstance(val, VoteOption):
            result[key] = serialize_vote_option(val)

    return room.__dict__


def serialize_vote_option(vote_option: VoteOption) -> Dict[str, Any]:
    return vote_option.__dict__
