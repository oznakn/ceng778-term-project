import logging
from sentence_transformers import LoggingHandler, SentenceTransformer
import os
import untangle
import json
import time


logging.basicConfig(
    format="%(asctime)s - %(message)s", datefmt="%Y-%m-%d %H:%M:%S", level=logging.INFO, handlers=[LoggingHandler()]
)

# Important, you need to shield your code with if __name__. Otherwise, CUDA runs into issues when spawning new processes.
if __name__ == "__main__":
  model = SentenceTransformer('sentence-transformers/all-MiniLM-L6-v2')

  # Start the multi-process pool on all available CUDA devices
  pool = model.start_multi_process_pool([
    "mps:0",
    "mps:1",
    "mps:2",
    "mps:3",
    "mps:4",
    "mps:5",
    "mps:6",
    "mps:7"
  ])

  results = dict()

  files = os.listdir('../ft/all')
  doc_ids = []

  time1 = time.time()

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
                      text += " " + prop.cdata
                  if prop._name == "HEADLINE":
                      text = prop.cdata + text + " "
              doc_ids.append(doc_id)

  time2 = time.time()
  embeddings = model.encode_multi_process(text, pool)
  time3 = time.time()
  print(embeddings)

  for i, doc_id in enumerate(doc_ids):
      results[doc_id] = embeddings[i].tolist()

  with open('embeddings2.json', 'w') as f:
      f.write(json.dumps(results))

  # Optional: Stop the processes in the pool
  model.stop_multi_process_pool(pool)

  time4 = time.time()

  print(f"Time to read files: {time2 - time1}")
  print(f"Time to encode files: {time3 - time2}")
  print(f"Time to write files: {time4 - time3}")
