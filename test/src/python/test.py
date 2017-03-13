import requests
import json

def to_json(obj):
    return json.JSONEncoder().encode(obj)

index_url = "http://cmput301.softwareprocess.es:8080/cmput301w17t09/"
mood_url = index_url + "mood9/_search"
user_url = index_url + "user9/_search"
search = {
    "query" : {
        "match_all" : {}
    },
}

r = requests.get(mood_url, data=to_json(search))
print(str(json.dumps(r.json(), indent=2)))
r = requests.get(user_url, data=to_json(search))
print(str(json.dumps(r.json(), indent=2)))
