from sentence_transformers import SentenceTransformer
import os
import untangle
import json

model = SentenceTransformer('sentence-transformers/all-MiniLM-L6-v2')

results = dict()

files = os.listdir('../ft/all')

for name in files:
    with open(f'../ft/all/{name}') as f:
        data = f.read()

        obj = untangle.parse('<root>' + data + '</root>')

        for item in obj.children[0].children:
            doc_id = None
            text = ""
            for prop in item.children:
                if prop._name == 'DOCNO':
                    doc_id = prop.cdata
                if prop._name == 'TEXT':
                    text = prop.cdata

            embeddings = model.encode(text)
            results[doc_id] = embeddings.tolist()
            print(doc_id)

with open('embeddings.json', 'w') as f:
    f.write(json.dumps(results))
