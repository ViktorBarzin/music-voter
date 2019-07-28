from room import Room, User
from serializer import serialize_room


def main() -> None:
    # breakpoint()
    user = User(username='gosho')
    rr = Room('title', user)
    breakpoint()
    ss = serialize_room(rr)
    print(ss)


if __name__ == "__main__":
    main()
