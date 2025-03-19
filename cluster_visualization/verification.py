import pandas as pd
import re
from sklearn.metrics import confusion_matrix, classification_report

# Step 1: 解析生成日志获取真实模式
actual_pattern = {}

with open('generator.log', 'r', encoding='utf-8') as log_file:
    for line in log_file:
        match = re.search(r'用户(\d{11})被分配为【(.*?)】模式', line)
        if match:
            phone, pattern = match.groups()
            # 将模式归一化为两类：规律作息（regular）和夜猫子（night_owl）
            if '规律作息' in pattern:
                actual_pattern[phone] = 'regular'
            elif '夜猫子' in pattern:
                actual_pattern[phone] = 'night_owl'

# Step 2: 加载聚类结果CSV文件
cluster_df = pd.read_csv('cluster_result.csv', dtype={'phone': str})

# 将真实模式列添加到聚类结果DataFrame
cluster_df['actual_pattern'] = cluster_df['phone'].map(actual_pattern)

# 检查未匹配的手机号
unmatched = cluster_df['actual_pattern'].isnull().sum()
if unmatched > 0:
    print(f"警告: 有{unmatched}个手机号未能匹配到真实模式")

# Step 3: 打印交叉统计表
contingency_table = pd.crosstab(cluster_df['actual_pattern'], cluster_df['cluster'])
print("交叉统计表:")
print(contingency_table)

# Step 4: 动态确定聚类标签
# 假设每个聚类中，出现次数最多的标签就是该聚类的标签
cluster_label_mapping = {}
for cluster in cluster_df['cluster'].unique():
    dominant_label = cluster_df[cluster_df['cluster'] == cluster]['actual_pattern'].mode()[0]
    cluster_label_mapping[cluster] = dominant_label

print("\n聚类标签映射:")
print(cluster_label_mapping)

# 将聚类结果映射到真实标签
cluster_df['predicted_pattern'] = cluster_df['cluster'].map(cluster_label_mapping)

# Step 5: 聚类效果评估
# 计算评估指标
y_true = cluster_df['actual_pattern']
y_pred = cluster_df['predicted_pattern']

# 计算混淆矩阵和分类报告
conf_matrix = confusion_matrix(y_true, y_pred, labels=['night_owl', 'regular'])
class_report = classification_report(y_true, y_pred, labels=['night_owl', 'regular'])

print("\n混淆矩阵（行为实际类别，列为预测类别）:")
print(conf_matrix)
print("\n分类报告:")
print(class_report)
