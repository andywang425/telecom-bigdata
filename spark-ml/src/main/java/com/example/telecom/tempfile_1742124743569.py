import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# 1. 读取CSV数据
df = pd.read_csv("/path/to/output/pca_cluster_results/part-00000-*.csv")

# 2. 绘制散点图
plt.figure(figsize=(10, 6))
sns.scatterplot(
    x="pca_x",
    y="pca_y",
    hue="cluster",
    palette="viridis",
    data=df,
    alpha=0.7
)
plt.title("User Clustering Visualization (PCA Reduced)")
plt.xlabel("Principal Component 1")
plt.ylabel("Principal Component 2")
plt.legend(title='Cluster')
plt.grid(True)
plt.show()
