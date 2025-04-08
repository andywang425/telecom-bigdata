import hashlib
import base64

# Format: O=${orderNumber},E=${expiryTimestamp},S=${planScope},LM=${licenseModel},PV=${planVersion},KV=2
# O=订单号（随便填）,E=过期时间（毫秒时间戳）,S=范围（填最大的premium）,LM=许可证模式（填最大的perpetual永久）,PV=计划模式（乱填即可）,KV=密钥版本（必须为2）
raw = 'O=123,E=3376656000000,S=premium,LM=perpetual,PV=dk,KV=2'

base64_encoded_str = base64.b64encode(raw.encode()).decode()

md5_hash = hashlib.md5(base64_encoded_str.encode()).hexdigest()

print(md5_hash + base64_encoded_str)
