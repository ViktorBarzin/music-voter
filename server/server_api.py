from flask import Flask, request, abort, Response
app = Flask(__name__)

from room import Room
from storage import get_user_from_id, add_room, get_rooms
from serializer import serialize_rooms


@app.route('/api')
def api():
    return 'kek'


@app.route('/api/rooms', methods=['GET', 'POST'])
def rooms() -> str:
    if request.method == 'GET':
        rooms = get_rooms()
        serialized_rooms = serialize_rooms(rooms)
        return Response(serialized_rooms, status=200, mimetype='application/json')
    elif request.method == 'POST':
        try:
            room_name = request.form['name']
            owner_id = request.form['owner_uid']
            password = request.form.get('password')  # Optional

            owner = get_user_from_id(owner_id)
            room = Room(name=room_name, owner=owner, password=password)
            add_room(room)

            return Response('', status=200, mimetype='application/json')
        except KeyError:
            return abort(401)

