from sentence_transformers import LoggingHandler, SentenceTransformer
from itertools import chain
import logging
import os
import untangle
import json
import re


logging.basicConfig(
    format="%(asctime)s - %(message)s", datefmt="%Y-%m-%d %H:%M:%S", level=logging.INFO, handlers=[LoggingHandler()]
)

model_name = "all-MiniLM-L6-v2"
# model_name = "all-mpnet-base-v2"

model = SentenceTransformer('sentence-transformers/' + model_name)

def create_doc_embeddings():
  results = dict()
  files = os.listdir('../ft/all')
  doc_ids = []
  texts = []

  for name in files:
      with open(f'../ft/all/{name}') as f:
          data = f.read()

          if name == "stopword.lst":
            print("Skipping stopword.lst")
            continue

          obj = untangle.parse('<root>' + data + '</root>')

          for item in obj.children[0].children:
              doc_id = None
              text = ""
              for prop in item.children:
                  if prop._name == 'DOCNO':
                      doc_id = prop.cdata
                  if prop._name == 'TEXT':
                      text += " " + prop.cdata
                  if prop._name == "HEADLINE":
                      text = prop.cdata + text + " "

              texts.append(text)
              doc_ids.append(doc_id)

  embeddings = model.encode(texts)
  print(embeddings)

  for i, doc_id in enumerate(doc_ids):
      results[doc_id] = embeddings[i].tolist()

  with open(model_name + '-docs.json', 'w') as f:
      f.write(json.dumps(results))

def flatten(l):
    return list(chain.from_iterable(l))

def create_query_embeddings():
  QUERY_FILES = [
      "query-relJudgments/q-topics-org-SET1.txt",
      "query-relJudgments/q-topics-org-SET2.txt",
      "query-relJudgments/q-topics-org-SET3.txt"
  ]

  FIX_TAG_LIST = ["<num>", "<title>", "<desc>", "<narr>"]
  results = dict()
  q_ids = []
  texts = []

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

              texts.append(text)
              q_ids.append(q_id)

  embeddings = model.encode(texts)

  for i, q_id in enumerate(q_ids):
      results[q_id] = embeddings[i].tolist()

  with open(model_name + '-queries.json', 'w') as f:
      f.write(json.dumps(results))


create_doc_embeddings()
create_query_embeddings()
