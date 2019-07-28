from room import Room, User
from serializer import serialize_room


def main() -> None:
    # breakpoint()
    user = User('gosho')
    rr = Room('title', user)
    ss = serialize_room(rr)
    print(ss)


if __name__ == "__main__":
    main()
