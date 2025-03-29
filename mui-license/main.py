import hashlib
import base64

bs = "Tz0xMjMsRT0zMzc2NjU2MDAwMDAwLFM9cHJlbWl1bSxMTT1wZXJwZXR1YWwsUFY9ZGssS1Y9Mg=="

raw = base64.b64decode(bs).decode()

base64_encoded_str = base64.b64encode(raw.encode()).decode()

md5_hash = hashlib.md5(base64_encoded_str.encode()).hexdigest()

print(md5_hash + base64_encoded_str)
