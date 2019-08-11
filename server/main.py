from server_api import app
from storage import Database, User


def main() -> None:
    # init db
    db = Database('databse.db')
    u = User('kekerino')
    db.create_user(u)
    # app.run(host='0.0.0.0', debug=True, port=5000)


if __name__ == "__main__":
    main()
