import mysql.connector as db
from flask import Flask, request, jsonify
import random, time, hashlib

class Database:

    def __init__(self, username, password):
        self.username = username
        self.password = password
        self.database = None
        self.conn = None

    def connect(self, database):
        self.conn = db.connect(
            host='localhost',
            user=self.username,
            password=self.password,
            database=database
        )
        self.database = database

    def execute(self, command):
        if self.database != None:
            cursor = self.conn.cursor()
            cursor.execute(command)

            # Check if the command is a SELECT query
            if command.strip().lower().startswith("select"):
                rows = cursor.fetchall()
                return rows
            else:
                # Commit non-SELECT changes
                self.conn.commit()
                return cursor.rowcount
        return None

    def close(self):
        if self.conn:
            self.conn.close()
            self.database = None
            self.conn = None

class Message:
    def __init__(self, type, auth, payload):
        self.type = type
        self.auth = auth
        self.payload = payload

    def parse(string):
        data = string.replace('\n','').split('</>')

        if len(data) != 3: return None

        return Message(data[0], data[1], data[2])

    def getString(self):
        return f'{self.type}</>{self.auth}</>{self.payload}'

class Server:
    def __init__(self, database: Database):
        self.app = Flask(__name__)
        self.REMOTE_PASSWORD = 'database'
        self.sessions = {}
        self.database = database

        ## Authentification ##

        def vallidate_user(data, sessions) -> bool:
            username = data.get('username')
            auth = data.get('auth')

            session = sessions[username]
            return auth == session['auth']

        @self.app.route('/auth/start', methods=['POST'])
        def auth_start():
            data = request.get_json()
            username = data.get('username')

            if username in self.sessions:
                return jsonify({"status": "error", "reason": "user already connected"}), 403

            # Generate random challenge for the user
            challenge = random.randint(100000, 100000000)
            self.sessions[username] = {
                "challenge": challenge,
                "timestamp": time.time(),
                "auth": hashlib.md5(f'{username}::{self.REMOTE_PASSWORD}::<secret>{challenge}'.encode('utf-8')).hexdigest()
            }

            self.log(f"[Auth Start] User={username}, Challenge={challenge}")
            return jsonify({"challenge": challenge})

        @self.app.route('/auth/verify', methods=['POST'])
        def auth_verify():
            data = request.get_json()
            username = data.get('username')
            response = data.get('response')

            session = self.sessions.get(username)
            if not session:
                return jsonify({"status": "error", "reason": "no active session"}), 400

            expected = session['auth']
            if response == expected:
                self.sessions[username]["authenticated"] = True
                self.log(f"[Auth Success] User={username}")
                return jsonify({"status": "ok"})
            else:
                self.log(f"[Auth Fail] User={username}\n{expected} | expected\n{response} | recieved")
                del self.sessions[username]
                return jsonify({"status": "fail"}), 403

        @self.app.route('/drop', methods=['POST'])
        def drop_connection():
            # Get JSON data sent from the Java client
            data = request.get_json()
            self.log("Received from Java: " + str(data))

            if(not vallidate_user(data, self.sessions)):
                return jsonify({"status": "not permitted"}), 403

            username = data.get('username')

            del self.sessions[username]

            self.log(f"[Auth DROPPED] User={username}")

            # Send response as JSON
            return jsonify({"status": "ok"})

        ## Connection Test ##

        @self.app.route('/test', methods=['POST'])
        def connection_test():

            self.log(f"[CONNECTION TEST]")

            # Send response as JSON
            return jsonify({"status": "ok"})

        ## Data handelers ##

        @self.app.route('/get/data/all', methods=['POST'])
        def get_data():
            # Get JSON data sent from the Java client
            data = request.get_json()
            self.log("Received from Java: " + str(data))

            if(not vallidate_user(data, self.sessions)):
                return jsonify({"status": "not permitted"}), 403

            data = self.database.execute("select * from Medium")
            response = ''

            for e in data:
                s = ''
                for el in e:
                    s += str(el) + '<element>'
                s = s[:-9]
                response += s + '\n'

            response = response[:-1]

            response_data = {
                "payload": response
            }

            # Send response as JSON
            return jsonify(response_data)

        @self.app.route('/get/free-id', methods=['POST'])
        def get_free_id():
            # Get a free id from the server
            data = request.get_json()
            self.log("Received from Java: " + str(data))

            if(not vallidate_user(data, self.sessions)):
                return jsonify({"status": "not permitted"}), 403

            # Prepare data
            id = find_free_id(self.database)

            response_data = {
                "id": str(id)
            }

            # Send response as JSON
            return jsonify(response_data)

        @self.app.route('/add', methods=['POST'])
        def add():
            data = request.get_json()
            self.log("Received from Java: " + str(data))

            if(not vallidate_user(data, self.sessions)):
                return jsonify({"status": "not permitted"}), 403

            data_base_string = data.get('data')

            # Split the string into parameters
            data_base_string = data_base_string.split('<element>')

            if(len(data_base_string) != 21):
                return jsonify({"status": "error", "message": "Data malformated: Incorrect amount of parameters."})

            # Build sql command
            command = f"INSERT INTO Medium VALUES({', '.join(sql_value(v) for v in data_base_string)})"

            # Insert into database
            result = self.database.execute(command)

            response_data = {
                "status": "ok"
            }

            # Send response as JSON
            return jsonify(response_data)

        @self.app.route('/delete', methods=['POST'])
        def delete():
            data = request.get_json()
            self.log("Received from Java: " + str(data))

            if(not vallidate_user(data, self.sessions)):
                return jsonify({"status": "not permitted"}), 403

            id = data.get('id')

            # Build sql command
            command = f"DELETE FROM Medium WHERE id = {id}"

            # Insert into database
            result = self.database.execute(command)

            response_data = {
                "status": "ok"
            }

            # Send response as JSON
            return jsonify(response_data)

    def log(self, message):
        print(message)

    def start(self):
        self.app.run(host='0.0.0.0', port=5000)

## Utills

def sql_value(val):
    if val is None or val.upper() == "NULL" or val == "":
        return "NULL"
    try:
        float(val)
        return val
    except ValueError:
        val_escaped = val.replace("'", "''")
        return f"'{val_escaped}'"

ID_LOCK = [-1]

def find_free_id(database):
    # Get all used IDs:

    ids = database.execute('select id from Medium')
    ids = [x[0] for x in ids]
    biggest = max(max(ids), max(ID_LOCK))
    available = []

    for i in range(1, biggest + 2):
        if i not in ids and i not in ID_LOCK:
            available.append(i)

    available.sort()
    print(available, 'biggest: ', biggest)
    return available[0]

d = Database('library','Bornholm')
d.connect('library')

s = Server(d)
s.start()