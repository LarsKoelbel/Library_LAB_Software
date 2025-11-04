import mysql.connector as db
from flask import Flask, request, jsonify
import random, time, hashlib, datetime

## GLOBAL COUNTERS ##

TRAFIC_TOTAL = 0
START_TIME = datetime.datetime.now()


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
    def __init__(self, database: Database, remotePassword):
        self.app = Flask(__name__)
        self.REMOTE_PASSWORD = remotePassword
        self.sessions = {}
        self.database = database

        ## Testing ##

        @self.app.route('/', methods=['GET'])
        def test_get():
            global TRAFIC_TOTAL
            TRAFIC_TOTAL += 1

            return jsonify({"server_up": "true",
                            "trafic_total": TRAFIC_TOTAL,
                            "started_at": START_TIME,
                            "uptime": str(datetime.datetime.now() - START_TIME)})

        ## Authentification ##

        def vallidate_user(data, sessions) -> bool:
            username = data.get('username')
            auth = data.get('auth')

            session = sessions[username]
            return auth == session['auth']

        @self.app.route('/auth/start', methods=['POST'])
        def auth_start():
            global TRAFIC_TOTAL
            TRAFIC_TOTAL += 1
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
            global TRAFIC_TOTAL
            TRAFIC_TOTAL += 1
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
            global TRAFIC_TOTAL
            TRAFIC_TOTAL += 1
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
            global TRAFIC_TOTAL
            TRAFIC_TOTAL += 1

            self.log(f"[CONNECTION TEST]")

            # Send response as JSON
            return jsonify({"status": "ok"})

        ## Data handelers ##

        @self.app.route('/get/data/all', methods=['POST'])
        def get_data():
            global TRAFIC_TOTAL
            TRAFIC_TOTAL += 1
            # Get JSON data sent from the Java client
            data = request.get_json()
            self.log("Received from Java: " + str(data))

            if(not vallidate_user(data, self.sessions)):
                return jsonify({"status": "not permitted"}), 403

            data = self.database.execute("select * from Media")
            print('\n\n', data, '\n\n')
            response = ''

            for e in data:
                # Get media type
                type = e[1]
                # Prepare variables for all data

                id = e[0]
                title = e[2]
                status = e[3]
                dateOfReturn = e[4]
                type = type
                yearOfPublishing = "None"
                publisher = "None"
                isbn = "None"
                author = "None"
                label = "None"
                artist = "None"
                durationInMinutes = "None"
                agePolicy = "None"
                url = "None"
                dataFormat = "None"
                sizeInBytes = "None"
                issn = "None"
                volume = "None"
                number = "None"
                edition = "None"
                pages = "None"

                if type == 'BOOK':
                    # Get book sepecific data
                    itemData = self.database.execute(f'select * from BookData where id = {e[5]}')
                    itemData = itemData[0]

                    yearOfPublishing = str(itemData[1])
                    publisher = str(itemData[2])
                    isbn = str(itemData[3])
                    author = str(itemData[4])
                    edition = str(itemData[5])
                    pages = str(itemData[6])
                elif type == 'EL_MED':
                    # Get el_med sepecific data
                    itemData = self.database.execute(f'select * from ElMedData where id = {e[7]}')
                    itemData = itemData[0]

                    url = str(itemData[1])
                    dataFormat = str(itemData[2])
                    sizeInBytes = str(itemData[3])
                elif type == 'JOURNAL':
                    # Get journal sepecific data
                    itemData = self.database.execute(f'select * from PaperData where id = {e[8]}')
                    itemData = itemData[0]

                    issn = str(itemData[1])
                    volume = str(itemData[2])
                    number = str(itemData[3])
                    edition = str(itemData[4])
                    pages = str(itemData[5])

                elif type == 'CD':

                    # Get CD specific data
                    itemData = self.database.execute(f'select * from CDData where id = {e[6]}')
                    itemData = itemData[0]

                    label = str(itemData[1])
                    artist = str(itemData[2])
                    durationInMinutes = str(itemData[3])
                    agePolicy = str(itemData[4])
                else:
                    self.log(f'Unknown media type found: "{type}". Skipping media...')
                    continue

                response += '<element>'.join([str(x) for x in [
                id, title, status, dateOfReturn, type, yearOfPublishing, publisher, isbn,
                author, label, artist, durationInMinutes, agePolicy,
                url, dataFormat, sizeInBytes, issn, volume, number,
                edition, pages
            ]]) + '\n'


            response = response[:-1]

            print(response)

            response_data = {
                "payload": response
            }

            # Send response as JSON
            return jsonify(response_data)

        @self.app.route('/get/free-id', methods=['POST'])
        def get_free_id():
            global TRAFIC_TOTAL
            TRAFIC_TOTAL += 1
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
            global TRAFIC_TOTAL
            TRAFIC_TOTAL += 1
            data = request.get_json()
            self.log("Received from Java: " + str(data))

            if(not vallidate_user(data, self.sessions)):
                return jsonify({"status": "not permitted"}), 403

            data_base_string = data.get('data')

            # Split the string into parameters
            data_base_string = data_base_string.split('<element>')

            if(len(data_base_string) != 21):
                return jsonify({"status": "error", "message": "Data malformated: Incorrect amount of parameters."})

            # Get type
            type = data_base_string[4]

            if type == 'BOOK':
                command = f"INSERT INTO BookData (id, yearOfPublishing, publisher, isbn, author, edition, pages) VALUES({data_base_string[0]}, {data_base_string[5]}, '{data_base_string[6]}', '{data_base_string[7]}', '{data_base_string[8]}', '{data_base_string[19]}', {data_base_string[20]})"
                self.database.execute(command)

                # Insert refferance into Media reference table
                command = f"INSERT INTO Media (id, type, title, status, dateOfReturn, data_book, data_cd, data_el_med, data_paper) VALUES({data_base_string[0]}, 'BOOK', '{data_base_string[1]}', {data_base_string[2]}, {data_base_string[3]}, {data_base_string[0]}, NULL, NULL, NULL)"
                self.database.execute(command)
            elif type == 'EL_MED':
                command = f"INSERT INTO ElMedData (id, url, dataFormat, sizeInBytes) VALUES({data_base_string[0]}, '{data_base_string[13]}', '{data_base_string[14]}', {data_base_string[15]})"
                self.database.execute(command)

                # Insert reference into Media reference table
                command = f"INSERT INTO Media (id, type, title, status, dateOfReturn, data_book, data_cd, data_el_med, data_paper) VALUES({data_base_string[0]}, 'EL_MED', '{data_base_string[1]}', {data_base_string[2]},{data_base_string[3]}, NULL, NULL, {data_base_string[0]}, NULL)"
                self.database.execute(command)
            elif type == 'JOURNAL':
                command = f"INSERT INTO PaperData (id, issn, volume, number, edition, pages) VALUES({data_base_string[0]}, '{data_base_string[16]}', {data_base_string[17]}, {data_base_string[18]}, '{data_base_string[19]}', {data_base_string[20]})"
                self.database.execute(command)

                # Insert reference into Media reference table
                command = f"INSERT INTO Media (id, type, title, status, dateOfReturn, data_book, data_cd, data_el_med, data_paper) VALUES({data_base_string[0]}, 'JOURNAL', '{data_base_string[1]}', {data_base_string[2]}, {data_base_string[3]}, NULL, NULL, NULL, {data_base_string[0]})"
                self.database.execute(command)
            elif type == 'CD':
                command = f"INSERT INTO CDData (id, label, artist, durationInMinutes, agePolicy) VALUES({data_base_string[0]}, '{data_base_string[9]}', '{data_base_string[10]}', {data_base_string[11]}, '{data_base_string[12]}')"
                self.database.execute(command)

                # Insert reference into Media reference table
                command = f"INSERT INTO Media (id, type, title, status, dateOfReturn, data_book, data_cd, data_el_med, data_paper) VALUES({data_base_string[0]}, 'CD', '{data_base_string[1]}', {data_base_string[2]}, {data_base_string[3]}, NULL, {data_base_string[0]}, NULL,NULL)"
                self.database.execute(command)
            else:
                return jsonify({"status": "error", "message": f"Data malformated: Unknown media type: {type}"})

            response_data = {
                "status": "ok"
            }

            # Send response as JSON
            return jsonify(response_data)

        @self.app.route('/delete', methods=['POST'])
        def delete():
            global TRAFIC_TOTAL
            TRAFIC_TOTAL += 1
            data = request.get_json()
            self.log("Received from Java: " + str(data))

            if(not vallidate_user(data, self.sessions)):
                return jsonify({"status": "not permitted"}), 403

            id = data.get('id')

            # Get type of media
            type = self.database.execute(f"SELECT type FROM Media WHERE id = {id}")
            type = type[0][0]

            # Delete from data-table based on type
            if type == 'BOOK':
                command = f"DELETE FROM BookData WHERE id = {id}"
                self.database.execute(command)
            elif type == 'EL_MED':
                command = f"DELETE FROM ElMedData WHERE id = {id}"
                self.database.execute(command)
            elif type == 'JOURNAL':
                command = f"DELETE FROM PaperData WHERE id = {id}"
                self.database.execute(command)
            elif type == 'CD':
                command = f"DELETE FROM CDData WHERE id = {id}"
                self.database.execute(command)
            else:
                return jsonify({"status": "error", "message": f"Data malformated: Unknown media type: {type}"})

            # Build sql command
            command = f"DELETE FROM Medium WHERE id = {id}"

            # Insert into database
            result = self.database.execute(command)

            response_data = {
                "status": "ok"
            }

            # Send response as JSON
            return jsonify(response_data)

        @self.app.route('/mod-status', methods=['POST'])
        def mod_status():
            global TRAFIC_TOTAL
            TRAFIC_TOTAL += 1
            data = request.get_json()
            self.log("Received from Java: " + str(data))

            if(not vallidate_user(data, self.sessions)):
                return jsonify({"status": "not permitted"}), 403

            id = data.get('id')
            status = data.get('status')
            if status == '0':
                date = data.get('date')
            else:
                date = None


            print('\n\n\n', data, '\n\n\n')

            # Set the status in the database
            self.database.execute(f"update Media set status = {status} where id = {id}")
            if date:
                self.database.execute(f"update Media set dateOfReturn = '{date}' where id = {id}")
            else:
                self.database.execute(f"update Media set dateOfReturn = NULL where id = {id}")


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

    ids = database.execute('select id from Media')
    ids = [x[0] for x in ids]
    biggest = max(max(ids), max(ID_LOCK))
    available = []

    for i in range(1, biggest + 2):
        if i not in ids and i not in ID_LOCK:
            available.append(i)

    available.sort()
    print(available, 'biggest: ', biggest)
    return available[0]

with open('database.passwd', 'r') as file:
    password = file.read().strip()

d = Database('library','12345')
d.connect('library')

s = Server(d, password)
while True:
    try:
        s.start()
        break
    except KeyboardInterrupt:
        break
    except Exception:
        time.sleep(10)
        break