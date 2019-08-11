import json
import urllib.parse
from collections import defaultdict
from typing import Any, Dict
from flask import Flask, request, Response
app = Flask(__name__)

from room import Room, User, VoteOption
from storage import get_user_from_username, add_room, get_rooms, add_user, get_room_by_name, vote
from serializer import serialize_rooms
from werkzeug.local import LocalProxy


def get_error_response(msg: str, code: int = 400) -> Response:
    return Response(json.dumps({'message': msg}), code, mimetype='application/json')


def get_ok_response(msg: str = 'OK', code: int = 200) -> Response:
    return Response(json.dumps({'message': msg}), status=code, mimetype='application/json')


@app.route('/api/rooms', methods=['GET', 'POST'])
def rooms() -> Response:
    if request.method == 'GET':
        rooms = get_rooms()
        serialized_rooms = serialize_rooms(rooms)
        return Response(serialized_rooms, status=200, mimetype='application/json')
    elif request.method == 'POST':
        try:
            room_name = request.form['name']
            owner_username = request.form['owner_username']
            password = request.form.get('password')  # Optional

            owner = get_user_from_username(owner_username)
            # TODO: this is for DEBUG reasons only, remove once registration works
            if not owner:
                return get_error_response(f'User {owner_username} not found.')
            room = Room(name=room_name, owner=owner, password=password)
            try:
                add_room(room)
            except ValueError as e:
                return get_error_response(str(e))

            return get_ok_response()
        except KeyError:
            return get_error_response('Invalid form, required params: "name", "owner_username"')
    return get_ok_response()


@app.route('/api/users', methods=['POST'])
def users() -> Response:
    try:
        username = request.form['username']
        user = User(username)
        add_user(user)
    except KeyError:
        return get_error_response('Invalid form, required params: "username"')
    except ValueError as e:
        return get_error_response(str(e))
    return get_ok_response()


@app.route('/api/vote/<room_name>', methods=['GET', 'POST'])
def vote_endpoint(room_name: str = '') -> Response:
    room = get_room_by_name(urllib.parse.unquote(room_name))
    if not room:
        return get_error_response(f'Room with name: {room_name} not found.')

    if request.method == 'POST':
        return _vote_post(request, room)  # type: ignore
    elif request.method == 'GET':
        return _vote_get(request, room)  # type: ignore
    return get_error_response(f'Invalid request method: {request.method}')


def _vote_post(request: LocalProxy, room: Room) -> Response:
    try:
        url = request.form['url']
        voter_username = request.form['username']  # replace with sessions
    except KeyError:
        return get_error_response(f'Required params: "title", "url", "username"')

    voter = get_user_from_username(voter_username)
    if not voter:
        return get_error_response(f'User {voter_username} not found.')
    vote(voter, url, room.name)
    return get_ok_response()


def _vote_get(request: LocalProxy, room: Room) -> Response:
    """
    Get all vote options and number of users that voted for each
    """
    serialized: Dict[str, Dict[str, Any]] = defaultdict(dict)

    for vote_option_url, voters in room.votes.items():
        serialized['vote_options'][vote_option_url] = {'url': vote_option_url, 'voters': voters}
    return Response(json.dumps(serialized, default=lambda x: x.__dict__), status=200, mimetype='application/json')


@app.route('/api/join/<room_name>', methods=['POST'])
def join(room_name: str = '') -> Response:
    room = get_room_by_name(urllib.parse.unquote(room_name))
    if not room:
        return get_error_response(f'Room with name: {room_name} not found.')

    try:
        username = request.form['username']
    except KeyError:
        return get_error_response(f'Invalid form, required params: "username"')

    user = get_user_from_username(username)
    if not user:
        return get_error_response(f'User with username "{username}" not found')

    room.join_user(user)
    return get_ok_response()
