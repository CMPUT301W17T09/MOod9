import requests
import json

def to_json(obj):
    return json.JSONEncoder().encode(obj)

index_url = "http://cmput301.softwareprocess.es:8080/cmput301w17t09/"
mood_url = index_url + "mood9/_search?pretty=true"
user_url = index_url + "user9/_search"
user_search = {
    "query" : {
        "match_all" : {}
    },
}

print(to_json(user_search))

r = requests.get(user_url, data=to_json(user_search))
print(str(r.json()))
