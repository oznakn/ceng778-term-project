from sentence_transformers import SentenceTransformer
import os
import re
import untangle
import json

from itertools import chain

def flatten(l):
    return list(chain.from_iterable(l))

QUERY_FILES = [
    "query-relJudgments/q-topics-org-SET1.txt",
    "query-relJudgments/q-topics-org-SET2.txt",
    "query-relJudgments/q-topics-org-SET3.txt"
]

FIX_TAG_LIST = ["<num>", "<title>", "<desc>", "<narr>"]
model = SentenceTransformer('sentence-transformers/all-MiniLM-L6-v2')

results = dict()

for name in QUERY_FILES:
    with open(f'../{name}') as f:
        data = flatten(list(map(lambda x: x.split(" "), f.read().replace("&", "&amp;").split("\n"))))
        data = list(filter(lambda x: x != "", data))

        i = 0
        while i < len(data):
            if data[i] in FIX_TAG_LIST:
                for x in range(i + 1, len(data)):
                    if data[x][0] == "<" and data[x][-1] == ">":
                        data.insert(x, "</" + data[i][1:])
                        break
            i += 1

        data = " ".join(data)

        obj = untangle.parse('<root>' + data + '</root>')

        for item in obj.children[0].children:
            q_id = None
            text = ""
            for prop in item.children:
                if prop._name == 'num':
                    q_id = int(list(filter(lambda x: x != "", prop.cdata.split(" ")))[-1])
                if prop._name == 'title':
                    text = prop.cdata

            embeddings = model.encode(text)
            results[q_id] = embeddings.tolist()

            print(q_id)

with open('qembeddings.json', 'w') as f:
    f.write(json.dumps(results))
