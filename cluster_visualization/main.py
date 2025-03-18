import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# 读取CSV文件
file_path = 'cluster_result.csv'
data = pd.read_csv(file_path)

# 查看数据的前几行
print(data.head())

# 使用seaborn绘制散点图
plt.figure(figsize=(10, 6))
sns.scatterplot(data=data, x='pca_x', y='pca_y', hue='cluster', palette='viridis', s=100, alpha=0.6)

# 添加标题和标签
plt.title('KMeans Clustering Visualization')
plt.xlabel('PCA Component 1')
plt.ylabel('PCA Component 2')

# 显示图例
plt.legend(title='Cluster')

# 保存图形到文件
output_file = 'clustering_visualization.png'  # 定义输出文件名
plt.savefig(output_file, dpi=300, bbox_inches='tight')  # 保存图像，设置分辨率为300dpi，并确保内容不被裁剪

# 显示图形
plt.show()
